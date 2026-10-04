import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { contentSchemas, type ContentType } from "@/lib/validation";
import { createContentItem, enabledCategoryExists } from "@/lib/content";

const types: ContentType[] = ["commands", "scripts", "tools", "tutorials"];
const fail = (error: string, status: number, details?: unknown) => NextResponse.json({ success: false, error, ...(details ? { details } : {}) }, { status });
function extractJsonRecords(value: unknown, type: string): unknown[] | undefined {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return undefined;
  const source = value as Record<string, unknown>;
  if (Array.isArray(source.records)) return source.records;
  if (Array.isArray(source.data)) return source.data;
  const data = source.data && typeof source.data === "object" ? source.data as Record<string, unknown> : source;
  return Array.isArray(data[type]) ? data[type] as unknown[] : undefined;
}
const names = {
  commands: ["title", "command", "category", "platform", "description", "syntax", "example", "expectedOutput", "difficulty", "warning", "relatedCommands", "status", "featured", "tags"],
  scripts: ["title", "language", "category", "description", "code", "explanation", "usage", "exampleOutput", "lineExplanations", "difficulty", "warning", "status", "featured", "tags"],
  tools: ["name", "description", "category", "platform", "installationCommand", "basicUsage", "examples", "documentation", "difficulty", "warning", "status", "featured", "tags"],
  tutorials: ["title", "description", "category", "difficulty", "coverImage", "estimatedMinutes", "status", "featured", "tags", "lessons"],
};

function parseCsv(text: string): Record<string, unknown>[] {
  const rows: string[][] = [];
  let row: string[] = [];
  let field = "";
  let quoted = false;
  for (let i = 0; i < text.length; i += 1) {
    const char = text[i];
    if (quoted && char === '"' && text[i + 1] === '"') { field += '"'; i += 1; }
    else if (char === '"') quoted = !quoted;
    else if (char === "," && !quoted) { row.push(field); field = ""; }
    else if ((char === "\n" || char === "\r") && !quoted) {
      if (char === "\r" && text[i + 1] === "\n") i += 1;
      row.push(field); rows.push(row); row = []; field = "";
    } else field += char;
  }
  if (quoted) throw new Error("CSV contains an unterminated quoted field.");
  if (field || row.length) { row.push(field); rows.push(row); }
  if (!rows.length) return [];
  const headers = rows.shift()!.map((header) => header.trim());
  if (!headers.length || headers.some((header) => !header)) throw new Error("CSV requires a header row.");
  return rows.filter((values) => values.some(Boolean)).map((values) =>
    Object.fromEntries(headers.map((header, index) => [header, values[index] ?? ""])),
  );
}

function toCsv(rows: Record<string, unknown>[], fields: string[]) {
  const quote = (value: unknown) => {
    const text = Array.isArray(value) || (value && typeof value === "object") ? JSON.stringify(value) : String(value ?? "");
    return `"${text.replaceAll('"', '""')}"`;
  };
  return [fields.map(quote).join(","), ...rows.map((row) => fields.map((field) => quote(row[field])).join(","))].join("\r\n");
}

async function load(type: ContentType) {
  if (type === "commands") return db.command.findMany({ include: { tags: { include: { tag: true } } }, orderBy: { createdAt: "desc" } });
  if (type === "scripts") return db.script.findMany({ include: { tags: { include: { tag: true } } }, orderBy: { createdAt: "desc" } });
  if (type === "tools") return db.tool.findMany({ include: { tags: { include: { tag: true } } }, orderBy: { createdAt: "desc" } });
  return db.tutorial.findMany({ include: { tags: { include: { tag: true } }, lessons: { orderBy: { sortOrder: "asc" } } }, orderBy: { createdAt: "desc" } });
}

export async function GET(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  const params = new URL(request.url).searchParams;
  const type = params.get("type");
  const format = params.get("format") ?? "json";
  if (type && !types.includes(type as ContentType)) return fail("Unknown content type", 400);
  const selected = type ? [type as ContentType] : types;
  const records: Record<string, unknown> = {};
  for (const contentType of selected) {
    records[contentType] = (await load(contentType)).map((item) => ({
      ...item,
      tags: Array.isArray(item.tags) ? item.tags.map((relation) => relation.tag.name) : [],
    }));
  }
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "EXPORTED", entity: selected.join(",") } });
  if (format === "csv") {
    if (!type) return fail("Choose one content type for CSV export", 400);
    const csv = toCsv(records[type] as Record<string, unknown>[], names[type as ContentType]);
    return new Response(csv, { headers: { "Content-Type": "text/csv; charset=utf-8", "Content-Disposition": `attachment; filename="${type}.csv"` } });
  }
  return NextResponse.json({ success: true, data: records });
}

export async function POST(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const contentType = request.headers.get("content-type") ?? "";
  let body: { type?: string; format?: string; commit?: boolean; records?: unknown[] };
  try {
    if (contentType.includes("multipart/form-data")) {
      const form = await request.formData();
      const file = form.get("file");
      if (!(file instanceof File) || file.size > 5_000_000) return fail("Select a file smaller than 5 MB.", 400);
      const format = String(form.get("format") ?? (file.name.endsWith(".csv") ? "csv" : "json"));
      const type = String(form.get("type") ?? "");
      const text = await file.text();
      if (format === "csv") body = { type, format, commit: form.get("commit") === "true", records: parseCsv(text) };
      else {
        const records = extractJsonRecords(JSON.parse(text) as unknown, type);
        body = { type, format, commit: form.get("commit") === "true", records };
      }
    } else {
      const json = await request.json() as unknown;
      const type = new URL(request.url).searchParams.get("type");
      if (Array.isArray(json)) body = { type: type ?? undefined, format: "json", records: json };
      else {
        const object = json && typeof json === "object" && !Array.isArray(json) ? json as { type?: string; commit?: boolean } : {};
        const embeddedData = json && typeof json === "object" && "data" in json ? (json as { data?: unknown }).data : json;
        const embeddedType = embeddedData && typeof embeddedData === "object" && !Array.isArray(embeddedData) ? Object.keys(embeddedData).find((key) => types.includes(key as ContentType)) : undefined;
        const selectedType = object.type ?? type ?? embeddedType;
        body = { type: selectedType, format: "json", commit: object.commit, records: selectedType ? extractJsonRecords(json, selectedType) : undefined };
      }
    }
  } catch (cause) {
    return fail(cause instanceof Error ? cause.message : "Unable to parse import file.", 400);
  }
  if (!body.type || !types.includes(body.type as ContentType)) return fail("Select a valid content type.", 400);
  const type = body.type as ContentType;
  if (!Array.isArray(body.records) || body.records.length > 1000) return fail("Import must contain up to 1,000 records.", 400);
  const valid: Record<string, unknown>[] = [];
  const errors: Array<{ row: number; errors: unknown }> = [];
  for (const [index, record] of body.records.entries()) {
    let normalized = record;
    if (body.format === "csv" && record && typeof record === "object") {
      const row = { ...(record as Record<string, unknown>) };
      if (typeof row.tags === "string") row.tags = row.tags.split(",").map((tag) => tag.trim()).filter(Boolean);
      for (const key of ["featured", "enabled"]) if (typeof row[key] === "string") row[key] = row[key] === "true";
      if (typeof row.estimatedMinutes === "string") row.estimatedMinutes = Number(row.estimatedMinutes);
      for (const key of ["lineExplanations", "lessons", "codeExamples"]) if (typeof row[key] === "string" && row[key]) {
        try { row[key] = JSON.parse(row[key]); } catch { /* Schema validation reports malformed JSON. */ }
      }
      normalized = row;
    }
    const parsed = contentSchemas[type].safeParse(normalized);
    if (!parsed.success) {
      errors.push({ row: index + 1, errors: parsed.error.flatten() });
      continue;
    }
    const value = parsed.data as Record<string, unknown>;
    if (!await enabledCategoryExists(value)) {
      errors.push({ row: index + 1, errors: { category: ["Choose an enabled category before importing this record."] } });
      continue;
    }
    valid.push(value);
  }
  if (body.commit) {
    let imported = 0;
    for (const record of valid) {
      await createContentItem(type, record);
      imported += 1;
    }
    await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "IMPORTED", entity: type, details: { imported, invalid: errors.length } } });
    return NextResponse.json({ success: true, data: { valid: valid.length, invalid: errors.length, imported, errors } });
  }
  return NextResponse.json({ success: true, data: { valid: valid.length, invalid: errors.length, errors } });
}

import { Prisma } from "@prisma/client";
import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { contentSchemas, type ContentType } from "@/lib/validation";
import { createContentItem, enabledCategoryExists } from "@/lib/content";

const validTypes: ContentType[] = ["commands", "scripts", "tools", "tutorials"];
const response = (data: unknown, status = 200) => NextResponse.json({ success: true, data }, { status });
const error = (message: string, status: number, details?: unknown) =>
  NextResponse.json({ success: false, error: message, ...(details ? { details } : {}) }, { status });

function parseType(value: string): ContentType | null {
  return validTypes.includes(value as ContentType) ? value as ContentType : null;
}

function queryWhere(type: ContentType, q: string, status: string, category: string, platform: string, difficulty: string) {
  const common = {
    ...(status ? { status: status as Prisma.EnumContentStatusFilter["equals"] } : {}),
    ...(category ? { category } : {}),
    ...(difficulty ? { difficulty: difficulty as Prisma.EnumDifficultyFilter["equals"] } : {}),
  };
  const search = q ? { contains: q, mode: "insensitive" as const } : undefined;
  if (type === "commands") return {
    ...common, ...(platform ? { platform: platform as Prisma.EnumContentPlatformFilter["equals"] } : {}),
    ...(search ? { OR: [{ title: search }, { command: search }, { description: search }, { category: search }] } : {}),
  } satisfies Prisma.CommandWhereInput;
  if (type === "scripts") return {
    ...common, ...(search ? { OR: [{ title: search }, { description: search }, { code: search }] } : {}),
  } satisfies Prisma.ScriptWhereInput;
  if (type === "tools") return {
    ...common, ...(platform ? { platform: platform as Prisma.EnumContentPlatformFilter["equals"] } : {}),
    ...(search ? { OR: [{ name: search }, { description: search }, { category: search }] } : {}),
  } satisfies Prisma.ToolWhereInput;
  return {
    ...common, ...(search ? { OR: [{ title: search }, { description: search }, { category: search }] } : {}),
  } satisfies Prisma.TutorialWhereInput;
}

async function findMany(type: ContentType, where: object, skip: number, take: number, orderBy: "createdAt" | "updatedAt" | "title", direction: "asc" | "desc") {
  const sort = { [orderBy]: direction };
  if (type === "commands") return db.command.findMany({ where: where as Prisma.CommandWhereInput, skip, take, orderBy: sort, include: { tags: { include: { tag: true } } } });
  if (type === "scripts") return db.script.findMany({ where: where as Prisma.ScriptWhereInput, skip, take, orderBy: sort, include: { tags: { include: { tag: true } } } });
  if (type === "tools") return db.tool.findMany({ where: where as Prisma.ToolWhereInput, skip, take, orderBy: sort, include: { tags: { include: { tag: true } } } });
  return db.tutorial.findMany({ where: where as Prisma.TutorialWhereInput, skip, take, orderBy: sort, include: { tags: { include: { tag: true } }, lessons: { orderBy: { sortOrder: "asc" } } } });
}

async function count(type: ContentType, where: object) {
  if (type === "commands") return db.command.count({ where: where as Prisma.CommandWhereInput });
  if (type === "scripts") return db.script.count({ where: where as Prisma.ScriptWhereInput });
  if (type === "tools") return db.tool.count({ where: where as Prisma.ToolWhereInput });
  return db.tutorial.count({ where: where as Prisma.TutorialWhereInput });
}

async function updateOne(type: ContentType, id: string, value: Record<string, unknown>) {
  const { tags = [], lessons = [], ...fields } = value;
  const relations = {
    deleteMany: {},
    create: (tags as string[]).map((name) => ({ tag: { connectOrCreate: { where: { name }, create: { name } } } })),
  };
  if (type === "commands") return db.command.update({ where: { id }, data: { ...(fields as Prisma.CommandUpdateInput), tags: relations } });
  if (type === "scripts") return db.script.update({ where: { id }, data: { ...(fields as Prisma.ScriptUpdateInput), tags: relations } });
  if (type === "tools") return db.tool.update({ where: { id }, data: { ...(fields as Prisma.ToolUpdateInput), tags: relations } });
  return db.tutorial.update({
    where: { id },
    data: {
      ...(fields as Prisma.TutorialUpdateInput),
      tags: relations,
      lessons: {
        deleteMany: {},
        create: (lessons as Array<{ title: string; content: string; codeExamples: string[] }>).map((lesson, index) => ({ title: lesson.title, content: lesson.content, codeExamples: lesson.codeExamples, sortOrder: index })),
      },
    },
    include: { lessons: { orderBy: { sortOrder: "asc" } } },
  });
}

async function deleteOne(type: ContentType, id: string) {
  if (type === "commands") return db.command.delete({ where: { id } });
  if (type === "scripts") return db.script.delete({ where: { id } });
  if (type === "tools") return db.tool.delete({ where: { id } });
  return db.tutorial.delete({ where: { id } });
}

async function audit(admin: { id: string; name: string }, action: string, type: string, entityId?: string) {
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action, entity: type, entityId } });
}

export async function GET(request: Request, { params }: { params: Promise<{ type: string }> }) {
  const admin = await requireAdmin();
  if (!admin) return error("Unauthorized", 401);
  const { type: rawType } = await params;
  const type = parseType(rawType);
  if (!type) return error("Unknown content type", 404);
  const url = new URL(request.url);
  const page = Math.max(1, Number(url.searchParams.get("page")) || 1);
  const pageSize = Math.min(100, Math.max(1, Number(url.searchParams.get("pageSize")) || 20));
  const sort = url.searchParams.get("sort");
  const direction = url.searchParams.get("direction") === "asc" ? "asc" : "desc";
  const orderBy = sort === "title" ? "title" : sort === "createdAt" ? "createdAt" : "updatedAt";
  const where = queryWhere(type, url.searchParams.get("q")?.slice(0, 160) ?? "", url.searchParams.get("status") ?? "", url.searchParams.get("category") ?? "", url.searchParams.get("platform") ?? "", url.searchParams.get("difficulty") ?? "");
  const [data, total] = await Promise.all([findMany(type, where, (page - 1) * pageSize, pageSize, orderBy, direction), count(type, where)]);
  return NextResponse.json({ success: true, data, pagination: { page, pageSize, total, pages: Math.ceil(total / pageSize) } });
}

export async function POST(request: Request, { params }: { params: Promise<{ type: string }> }) {
  const admin = await requireAdmin();
  if (!admin) return error("Unauthorized", 401);
  if (!isSameOrigin(request)) return error("Invalid request origin", 403);
  const type = parseType((await params).type);
  if (!type) return error("Unknown content type", 404);
  const payload = await request.json().catch(() => null);
  const parsed = contentSchemas[type].safeParse(payload);
  if (!parsed.success) return error("Validation failed", 400, parsed.error.flatten());
  if (!await enabledCategoryExists(parsed.data as Record<string, unknown>)) return error("Choose an enabled category before saving content.", 400);
  const created = await createContentItem(type, parsed.data as Record<string, unknown>);
  await audit(admin, parsed.data.status === "PUBLISHED" ? "PUBLISHED" : "CREATED", type, "id" in created ? created.id : undefined);
  return response(created, 201);
}

export async function PATCH(request: Request, { params }: { params: Promise<{ type: string }> }) {
  const admin = await requireAdmin();
  if (!admin) return error("Unauthorized", 401);
  if (!isSameOrigin(request)) return error("Invalid request origin", 403);
  const type = parseType((await params).type);
  if (!type) return error("Unknown content type", 404);
  const payload = await request.json().catch(() => null) as { id?: string; action?: string; ids?: string[]; data?: unknown } | null;
  if (!payload) return error("Invalid JSON body", 400);
  const ids = payload.ids ?? (payload.id ? [payload.id] : []);
  if (!ids.length || ids.length > 100) return error("Provide between 1 and 100 item IDs", 400);
  if (payload.action === "delete") {
    await Promise.all(ids.map((id) => deleteOne(type, id)));
    await audit(admin, "DELETED", type, ids.join(","));
    return response({ affected: ids.length });
  }
  if (payload.action === "duplicate") {
    if (ids.length !== 1) return error("Duplicate one item at a time", 400);
    const existing = await findMany(type, { id: ids[0] }, 0, 1, "updatedAt", "desc");
    if (!existing[0]) return error("Content not found", 404);
    const source = existing[0] as Record<string, unknown>;
    const clone: Record<string, unknown> = {
      ...source,
      tags: Array.isArray(source.tags) ? source.tags.map((entry) => (entry as { tag: { name: string } }).tag.name) : [],
      lessons: Array.isArray(source.lessons) ? source.lessons : [],
      status: "DRAFT",
      featured: false,
    };
    delete clone.id;
    delete clone.createdAt;
    delete clone.updatedAt;
    delete clone.viewCount;
    const cloned = await createContentItem(type, clone);
    await audit(admin, "DUPLICATED", type, ids[0]);
    return response(cloned, 201);
  }
  if (payload.action === "publish" || payload.action === "unpublish" || payload.action === "archive") {
    const status = payload.action === "publish" ? "PUBLISHED" : payload.action === "archive" ? "ARCHIVED" : "DRAFT";
    for (const id of ids) {
      if (type === "commands") await db.command.update({ where: { id }, data: { status } });
      else if (type === "scripts") await db.script.update({ where: { id }, data: { status } });
      else if (type === "tools") await db.tool.update({ where: { id }, data: { status } });
      else await db.tutorial.update({ where: { id }, data: { status } });
    }
    await audit(admin, status === "PUBLISHED" ? "PUBLISHED" : "UPDATED", type, ids.join(","));
    return response({ affected: ids.length, status });
  }
  if (!payload.id) return error("An item ID is required", 400);
  const parsed = contentSchemas[type].safeParse(payload.data);
  if (!parsed.success) return error("Validation failed", 400, parsed.error.flatten());
  if (!await enabledCategoryExists(parsed.data as Record<string, unknown>)) return error("Choose an enabled category before saving content.", 400);
  const updated = await updateOne(type, payload.id, parsed.data as Record<string, unknown>);
  await audit(admin, parsed.data.status === "PUBLISHED" ? "PUBLISHED" : "UPDATED", type, payload.id);
  return response(updated);
}

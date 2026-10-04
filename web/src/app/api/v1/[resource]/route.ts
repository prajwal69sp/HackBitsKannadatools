import { NextResponse } from "next/server";
import { hashIp } from "@/lib/auth";
import { db } from "@/lib/db";

const result = (data: unknown, pagination?: object) =>
  NextResponse.json({ success: true, data, ...(pagination ? { pagination } : {}) });

const recentRequests = new Map<string, { count: number; resetAt: number }>();

export async function POST(request: Request, { params }: { params: Promise<{ resource: string }> }) {
  const { resource } = await params;
  if (resource !== "events") return NextResponse.json({ success: false, error: "Unknown resource" }, { status: 404 });
  const ip = request.headers.get("x-forwarded-for")?.split(",")[0]?.trim() || "unknown";
  const key = hashIp(ip);
  const now = Date.now();
  const bucket = recentRequests.get(key);
  if (bucket && bucket.resetAt > now && bucket.count >= 120) {
    return NextResponse.json({ success: false, error: "Too many analytics events. Retry later." }, { status: 429 });
  }
  if (!bucket || bucket.resetAt <= now) recentRequests.set(key, { count: 1, resetAt: now + 60_000 });
  else bucket.count += 1;
  if (recentRequests.size > 5000) {
    for (const [entry, limit] of recentRequests) if (limit.resetAt <= now) recentRequests.delete(entry);
  }
  const enabledCategories = (await db.category.findMany({ where: { enabled: true }, select: { name: true } })).map(({ name }) => name);
  const body = await request.json().catch(() => null) as { events?: unknown } | null;
  if (!body || !Array.isArray(body.events) || body.events.length < 1 || body.events.length > 20) {
    return NextResponse.json({ success: false, error: "Provide between 1 and 20 content events." }, { status: 400 });
  }
  const records: Array<{ eventType: string; entity: string; entityId: string }> = [];
  for (const item of body.events) {
    if (!item || typeof item !== "object") return NextResponse.json({ success: false, error: "Invalid event shape." }, { status: 400 });
    const event = item as { type?: unknown; id?: unknown };
    if (typeof event.type !== "string" || !["commands", "scripts", "tools", "tutorials"].includes(event.type) || typeof event.id !== "string" || event.id.length > 100) {
      return NextResponse.json({ success: false, error: "Invalid event type or content ID." }, { status: 400 });
    }
    const where = { id: event.id, status: "PUBLISHED" as const, category: { in: enabledCategories } };
    const isPublished = event.type === "commands" ? await db.command.count({ where })
      : event.type === "scripts" ? await db.script.count({ where })
        : event.type === "tools" ? await db.tool.count({ where })
          : await db.tutorial.count({ where });
    if (!isPublished) continue;
    records.push({ eventType: "VIEW", entity: event.type, entityId: event.id });
  }
  if (records.length) await db.analyticsEvent.createMany({ data: records });
  return result({ accepted: records.length });
}

export async function GET(request: Request, { params }: { params: Promise<{ resource: string }> }) {
  const { resource } = await params;
  const url = new URL(request.url);
  const page = Math.max(1, Number(url.searchParams.get("page")) || 1);
  const pageSize = Math.min(100, Math.max(1, Number(url.searchParams.get("pageSize")) || 100));
  const skip = (page - 1) * pageSize;
  const since = url.searchParams.get("since");
  const updatedAt = since && !Number.isNaN(Date.parse(since)) ? { updatedAt: { gt: new Date(since) } } : {};
  if (resource === "categories") return result(await db.category.findMany({ where: { enabled: true }, orderBy: [{ sortOrder: "asc" }, { name: "asc" }] }));
  if (resource === "config") return result(await db.appConfig.findUnique({ where: { id: "main" } }));
  const enabledCategories = (await db.category.findMany({ where: { enabled: true }, select: { name: true } })).map(({ name }) => name);
  if (resource === "featured") {
    const [commands, scripts, tools, tutorials] = await Promise.all([
      db.command.findMany({ where: { status: "PUBLISHED", featured: true, category: { in: enabledCategories } }, take: 10 }),
      db.script.findMany({ where: { status: "PUBLISHED", featured: true, category: { in: enabledCategories } }, take: 10 }),
      db.tool.findMany({ where: { status: "PUBLISHED", featured: true, category: { in: enabledCategories } }, take: 10 }),
      db.tutorial.findMany({ where: { status: "PUBLISHED", featured: true, category: { in: enabledCategories } }, take: 10, include: { lessons: { orderBy: { sortOrder: "asc" } } } }),
    ]);
    return result({ commands, scripts, tools, tutorials });
  }
  const models = {
    commands: () => db.command.findMany({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt }, orderBy: { updatedAt: "desc" }, skip, take: pageSize, include: { tags: { include: { tag: true } } } }),
    scripts: () => db.script.findMany({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt }, orderBy: { updatedAt: "desc" }, skip, take: pageSize, include: { tags: { include: { tag: true } } } }),
    tools: () => db.tool.findMany({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt }, orderBy: { updatedAt: "desc" }, skip, take: pageSize, include: { tags: { include: { tag: true } } } }),
    tutorials: () => db.tutorial.findMany({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt }, orderBy: { updatedAt: "desc" }, skip, take: pageSize, include: { lessons: { orderBy: { sortOrder: "asc" } }, tags: { include: { tag: true } } } }),
  };
  if (!(resource in models)) return NextResponse.json({ success: false, error: "Unknown resource" }, { status: 404 });
  const fetchItems = models[resource as keyof typeof models];
  const [data, total] = await Promise.all([
    fetchItems(),
    resource === "commands" ? db.command.count({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt } })
      : resource === "scripts" ? db.script.count({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt } })
        : resource === "tools" ? db.tool.count({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt } })
          : db.tutorial.count({ where: { status: "PUBLISHED", category: { in: enabledCategories }, ...updatedAt } }),
  ]);
  return result(data, { page, pageSize, total, pages: Math.ceil(total / pageSize) });
}

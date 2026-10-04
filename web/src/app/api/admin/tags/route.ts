import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { tagSchema } from "@/lib/validation";

const fail = (error: string, status: number, details?: unknown) => NextResponse.json({ success: false, error, ...(details ? { details } : {}) }, { status });

export async function GET(request: Request) {
  if (!await requireAdmin()) return fail("Unauthorized", 401);
  const q = new URL(request.url).searchParams.get("q")?.slice(0, 80);
  const rows = await db.tag.findMany({
    where: q ? { name: { contains: q, mode: "insensitive" } } : undefined,
    orderBy: { name: "asc" },
    include: { _count: { select: { commands: true, scripts: true, tools: true, tutorials: true } } },
  });
  return NextResponse.json({ success: true, data: rows.map(({ _count, ...tag }) => ({ ...tag, usageCount: Object.values(_count).reduce((sum, count) => sum + count, 0) })) });
}

export async function POST(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null);
  const parsed = tagSchema.safeParse(body);
  if (!parsed.success) return fail("Validation failed", 400, parsed.error.flatten());
  const tag = await db.tag.create({ data: parsed.data });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "CREATED", entity: "tag", entityId: tag.id } });
  return NextResponse.json({ success: true, data: tag }, { status: 201 });
}

export async function PATCH(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null) as { id?: string; name?: string } | null;
  const parsed = tagSchema.safeParse(body);
  if (!body?.id || !parsed.success) return fail("Provide a tag ID and valid name", 400, parsed.success ? undefined : parsed.error.flatten());
  const tag = await db.tag.update({ where: { id: body.id }, data: parsed.data });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "UPDATED", entity: "tag", entityId: tag.id } });
  return NextResponse.json({ success: true, data: tag });
}

export async function DELETE(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const { id } = await request.json().catch(() => ({})) as { id?: string };
  if (!id) return fail("Tag ID is required", 400);
  await db.tag.delete({ where: { id } });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "DELETED", entity: "tag", entityId: id } });
  return NextResponse.json({ success: true, data: null });
}

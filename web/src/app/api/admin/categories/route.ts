import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { categorySchema } from "@/lib/validation";

const fail = (error: string, status: number, details?: unknown) => NextResponse.json({ success: false, error, ...(details ? { details } : {}) }, { status });

export async function GET() {
  if (!await requireAdmin()) return fail("Unauthorized", 401);
  return NextResponse.json({ success: true, data: await db.category.findMany({ orderBy: [{ sortOrder: "asc" }, { name: "asc" }] }) });
}

export async function POST(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null) as { id?: string } | null;
  if (!body) return fail("Invalid JSON body", 400);
  const parsed = categorySchema.safeParse(body);
  if (!parsed.success) return fail("Validation failed", 400, parsed.error.flatten());
  const category = await db.category.create({ data: parsed.data });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "CREATED", entity: "category", entityId: category.id } });
  return NextResponse.json({ success: true, data: category }, { status: 201 });
}

export async function PATCH(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null) as { id?: string } & Record<string, unknown> | null;
  if (!body?.id) return fail("Category ID is required", 400);
  const parsed = categorySchema.safeParse(body);
  if (!parsed.success) return fail("Validation failed", 400, parsed.error.flatten());
  const category = await db.category.update({ where: { id: body.id }, data: parsed.data });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "UPDATED", entity: "category", entityId: category.id } });
  return NextResponse.json({ success: true, data: category });
}

export async function DELETE(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return fail("Unauthorized", 401);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const { id } = await request.json().catch(() => ({})) as { id?: string };
  if (!id) return fail("Category ID is required", 400);
  const [commands, scripts, tools, tutorials] = await Promise.all([
    db.command.count({ where: { category: id } }), db.script.count({ where: { category: id } }),
    db.tool.count({ where: { category: id } }), db.tutorial.count({ where: { category: id } }),
  ]);
  if (commands + scripts + tools + tutorials) return fail("Move content to another category before deleting it.", 409);
  await db.category.delete({ where: { id } });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "DELETED", entity: "category", entityId: id } });
  return NextResponse.json({ success: true, data: null });
}

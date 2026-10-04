import { AdminRole, AdminStatus } from "@prisma/client";
import { hash } from "bcryptjs";
import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { z } from "zod";

const userSchema = z.object({
  name: z.string().trim().min(1).max(100),
  email: z.string().trim().email().max(254),
  role: z.enum(["SUPER_ADMIN", "EDITOR"]),
  status: z.enum(["ACTIVE", "DISABLED"]),
  password: z.string().min(12).max(200).optional(),
});
const fail = (error: string, status: number, details?: unknown) => NextResponse.json({ success: false, error, ...(details ? { details } : {}) }, { status });
const select = { id: true, name: true, email: true, role: true, status: true, createdAt: true, lastLoginAt: true } as const;

export async function GET() {
  if (!await requireAdmin(AdminRole.SUPER_ADMIN)) return fail("Super administrator access required", 403);
  return NextResponse.json({ success: true, data: await db.adminUser.findMany({ select, orderBy: { createdAt: "desc" } }) });
}

export async function POST(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return fail("Super administrator access required", 403);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null);
  const parsed = userSchema.safeParse(body);
  if (!parsed.success || !parsed.data.password) return fail("Valid user details and a password of at least 12 characters are required", 400, parsed.success ? undefined : parsed.error.flatten());
  const { password, ...fields } = parsed.data;
  const user = await db.adminUser.create({ data: { ...fields, email: fields.email.toLowerCase(), passwordHash: await hash(password, 12) }, select });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "ADMIN_CREATED", entity: "admin_user", entityId: user.id } });
  return NextResponse.json({ success: true, data: user }, { status: 201 });
}

export async function PATCH(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return fail("Super administrator access required", 403);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null) as ({ id?: string } & Record<string, unknown>) | null;
  const parsed = userSchema.safeParse(body);
  if (!body?.id || !parsed.success) return fail("Validation failed", 400, parsed.success ? undefined : parsed.error.flatten());
  const existing = await db.adminUser.findUnique({ where: { id: body.id } });
  if (!existing) return fail("Admin user not found", 404);
  if (existing.id === admin.id && (parsed.data.role !== AdminRole.SUPER_ADMIN || parsed.data.status !== AdminStatus.ACTIVE)) {
    return fail("You cannot disable or demote your own account.", 409);
  }
  if (existing.role === AdminRole.SUPER_ADMIN && existing.status === AdminStatus.ACTIVE &&
    (parsed.data.role !== AdminRole.SUPER_ADMIN || parsed.data.status !== AdminStatus.ACTIVE) &&
    await db.adminUser.count({ where: { role: AdminRole.SUPER_ADMIN, status: AdminStatus.ACTIVE } }) <= 1) {
    return fail("At least one active super administrator must remain.", 409);
  }
  const { password, ...fields } = parsed.data;
  const user = await db.adminUser.update({
    where: { id: existing.id },
    data: { ...fields, email: fields.email.toLowerCase(), ...(password ? { passwordHash: await hash(password, 12) } : {}) },
    select,
  });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "ADMIN_UPDATED", entity: "admin_user", entityId: user.id } });
  return NextResponse.json({ success: true, data: user });
}

export async function DELETE(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return fail("Super administrator access required", 403);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const { id } = await request.json().catch(() => ({})) as { id?: string };
  if (!id || id === admin.id) return fail("You cannot delete your own account.", 400);
  const user = await db.adminUser.findUnique({ where: { id } });
  if (!user) return fail("Admin user not found", 404);
  if (user.role === AdminRole.SUPER_ADMIN && user.status === AdminStatus.ACTIVE &&
    await db.adminUser.count({ where: { role: AdminRole.SUPER_ADMIN, status: AdminStatus.ACTIVE } }) <= 1) {
    return fail("At least one active super administrator must remain.", 409);
  }
  await db.adminUser.delete({ where: { id } });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "ADMIN_DELETED", entity: "admin_user", entityId: id } });
  return NextResponse.json({ success: true, data: null });
}

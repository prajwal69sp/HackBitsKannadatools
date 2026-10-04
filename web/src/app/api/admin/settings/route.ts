import { AdminRole } from "@prisma/client";
import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { configSchema } from "@/lib/validation";

const fail = (error: string, status: number, details?: unknown) => NextResponse.json({ success: false, error, ...(details ? { details } : {}) }, { status });

export async function GET() {
  if (!await requireAdmin(AdminRole.SUPER_ADMIN)) return fail("Super administrator access required", 403);
  const config = await db.appConfig.findUnique({ where: { id: "main" } });
  return NextResponse.json({ success: true, data: config });
}

export async function PUT(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return fail("Super administrator access required", 403);
  if (!isSameOrigin(request)) return fail("Invalid request origin", 403);
  const body = await request.json().catch(() => null);
  const parsed = configSchema.safeParse(body);
  if (!parsed.success) return fail("Validation failed", 400, parsed.error.flatten());
  const config = await db.appConfig.upsert({ where: { id: "main" }, create: { id: "main", ...parsed.data }, update: parsed.data });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "SETTINGS_CHANGED", entity: "app_config", entityId: "main" } });
  return NextResponse.json({ success: true, data: config });
}

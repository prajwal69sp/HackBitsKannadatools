import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";
import { z } from "zod";

const schema = z.object({ about: z.string().max(20000), aboutSections: z.record(z.string(), z.string().max(10000)) });
export async function GET() {
  if (!await requireAdmin()) return NextResponse.json({ success: false, error: "Unauthorized" }, { status: 401 });
  const config = await db.appConfig.findUnique({ where: { id: "main" }, select: { about: true, aboutSections: true } });
  return NextResponse.json({ success: true, data: config ?? { about: "", aboutSections: {} } });
}
export async function PUT(request: Request) {
  const admin = await requireAdmin();
  if (!admin) return NextResponse.json({ success: false, error: "Unauthorized" }, { status: 401 });
  if (!isSameOrigin(request)) return NextResponse.json({ success: false, error: "Invalid request origin" }, { status: 403 });
  const parsed = schema.safeParse(await request.json().catch(() => null));
  if (!parsed.success) return NextResponse.json({ success: false, error: "Validation failed", details: parsed.error.flatten() }, { status: 400 });
  const config = await db.appConfig.upsert({ where: { id: "main" }, create: { id: "main", ...parsed.data }, update: parsed.data, select: { about: true, aboutSections: true } });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "ABOUT_UPDATED", entity: "app_config", entityId: "main" } });
  return NextResponse.json({ success: true, data: config });
}

import { NextResponse } from "next/server";
import { AdminRole } from "@prisma/client";
import { db } from "@/lib/db";
import { requireAdmin } from "@/lib/auth";

export async function GET(request: Request) {
  if (!await requireAdmin(AdminRole.SUPER_ADMIN)) return NextResponse.json({ success: false, error: "Super administrator access required" }, { status: 403 });
  const params = new URL(request.url).searchParams;
  const page = Math.max(1, Number(params.get("page")) || 1);
  const pageSize = Math.min(100, Math.max(1, Number(params.get("pageSize")) || 30));
  const [data, total] = await Promise.all([
    db.auditLog.findMany({ orderBy: { createdAt: "desc" }, skip: (page - 1) * pageSize, take: pageSize }),
    db.auditLog.count(),
  ]);
  return NextResponse.json({ success: true, data, pagination: { page, pageSize, total, pages: Math.ceil(total / pageSize) } });
}

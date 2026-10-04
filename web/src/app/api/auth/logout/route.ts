import { cookies } from "next/headers";
import { NextResponse } from "next/server";
import { hashSecret, isSameOrigin, SESSION_COOKIE } from "@/lib/auth";
import { db } from "@/lib/db";

export async function POST(request: Request) {
  if (!isSameOrigin(request)) return NextResponse.json({ success: false, error: "Invalid request origin" }, { status: 403 });
  const jar = await cookies();
  const token = jar.get(SESSION_COOKIE)?.value;
  if (token) {
    const session = await db.session.findUnique({ where: { tokenHash: hashSecret(token) }, include: { admin: true } });
    if (session) {
      await db.auditLog.create({ data: { adminId: session.adminId, adminName: session.admin.name, action: "LOGOUT", entity: "session" } });
      await db.session.delete({ where: { id: session.id } });
    }
  }
  jar.delete(SESSION_COOKIE);
  return NextResponse.json({ success: true, data: null });
}

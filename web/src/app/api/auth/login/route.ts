import { compare } from "bcryptjs";
import { AdminStatus } from "@prisma/client";
import { NextResponse } from "next/server";
import { createSession, hashIp, isSameOrigin } from "@/lib/auth";
import { db } from "@/lib/db";

const failure = (message: string, status = 401) => NextResponse.json({ success: false, error: message }, { status });

export async function POST(request: Request) {
  if (!isSameOrigin(request)) return failure("Invalid request origin", 403);
  const payload = await request.json().catch(() => null) as { email?: unknown; password?: unknown } | null;
  if (!payload || typeof payload.email !== "string" || payload.email.length > 254 || typeof payload.password !== "string" || payload.password.length > 200) {
    return failure("Enter a valid email and password", 400);
  }
  const email = payload.email.trim().toLowerCase();
  const ip = request.headers.get("x-forwarded-for")?.split(",")[0]?.trim() || "unknown";
  const ipHash = hashIp(ip);
  const now = new Date();
  const attempt = await db.loginAttempt.findUnique({ where: { ipHash } });
  if (attempt?.lockedUntil && attempt.lockedUntil > now) {
    return failure("Too many attempts. Try again later.", 429);
  }

  const user = await db.adminUser.findUnique({ where: { email } });
  const valid = user && user.status === AdminStatus.ACTIVE
    ? await compare(payload.password, user.passwordHash)
    : await compare(payload.password, "$2a$12$1MX5t5QxO9z9h5hif6pnW.d2bwwQw7.7yxo4R7uB2cTn7T/c7Rz5e");
  if (!user || user.status !== AdminStatus.ACTIVE || !valid) {
    const failures = (attempt?.failures ?? 0) + 1;
    await db.loginAttempt.upsert({
      where: { ipHash },
      create: { ipHash, failures, lockedUntil: failures >= 5 ? new Date(Date.now() + 15 * 60 * 1000) : null },
      update: { failures, lockedUntil: failures >= 5 ? new Date(Date.now() + 15 * 60 * 1000) : null },
    });
    return failure(failures >= 5 ? "Too many attempts. Try again later." : "Invalid email or password.", failures >= 5 ? 429 : 401);
  }

  await db.loginAttempt.deleteMany({ where: { ipHash } });
  await createSession(user.id);
  await db.adminUser.update({ where: { id: user.id }, data: { lastLoginAt: now } });
  await db.auditLog.create({ data: { adminId: user.id, adminName: user.name, action: "LOGIN", entity: "session" } });
  return NextResponse.json({ success: true, data: { name: user.name, role: user.role } });
}

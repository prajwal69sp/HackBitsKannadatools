import { createHash, randomBytes } from "node:crypto";
import { cookies } from "next/headers";
import { AdminRole, AdminStatus } from "@prisma/client";
import { db } from "@/lib/db";

export const SESSION_COOKIE = "hbk_admin_session";
const SESSION_TTL_MS = 1000 * 60 * 60 * 24 * 7;

export function sessionSecret(): string {
  const secret = process.env.ADMIN_SESSION_SECRET;
  if (!secret || secret.length < 32) {
    throw new Error("ADMIN_SESSION_SECRET must contain at least 32 characters.");
  }
  return secret;
}

export function hashSecret(value: string): string {
  return createHash("sha256").update(value).digest("hex");
}

export function hashIp(ip: string): string {
  return createHash("sha256").update(`${sessionSecret()}:${ip}`).digest("hex");
}

export async function createSession(adminId: string): Promise<void> {
  const token = randomBytes(32).toString("base64url");
  const jar = await cookies();
  const currentToken = jar.get(SESSION_COOKIE)?.value;
  if (currentToken) await db.session.deleteMany({ where: { tokenHash: hashSecret(currentToken) } });
  await db.session.create({
    data: { adminId, tokenHash: hashSecret(token), expiresAt: new Date(Date.now() + SESSION_TTL_MS) },
  });
  jar.set(SESSION_COOKIE, token, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "strict",
    path: "/",
    expires: new Date(Date.now() + SESSION_TTL_MS),
  });
}

export async function currentAdmin() {
  const token = (await cookies()).get(SESSION_COOKIE)?.value;
  if (!token) return null;
  const session = await db.session.findUnique({
    where: { tokenHash: hashSecret(token) },
    include: { admin: { select: { id: true, name: true, email: true, role: true, status: true } } },
  });
  if (!session || session.expiresAt <= new Date() || session.admin.status !== AdminStatus.ACTIVE) return null;
  return { ...session.admin, sessionId: session.id };
}

export async function requireAdmin(role?: AdminRole) {
  const admin = await currentAdmin();
  if (!admin || (role && admin.role !== role)) return null;
  return admin;
}

export function isSameOrigin(request: Request): boolean {
  const origin = request.headers.get("origin");
  if (!origin) return false;
  try {
    const forwardedHost = request.headers.get("x-forwarded-host")?.split(",")[0]?.trim();
    const requestHost = forwardedHost || request.headers.get("host") || new URL(request.url).host;
    const forwardedProtocol = request.headers.get("x-forwarded-proto")?.split(",")[0]?.trim();
    const requestProtocol = forwardedProtocol ? `${forwardedProtocol.replace(/:$/, "")}:` : new URL(request.url).protocol;
    const originUrl = new URL(origin);
    return originUrl.host === requestHost && originUrl.protocol === requestProtocol;
  } catch {
    return false;
  }
}

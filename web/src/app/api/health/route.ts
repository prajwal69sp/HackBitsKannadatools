import { NextResponse } from "next/server";
import { db } from "@/lib/db";

export const dynamic = "force-dynamic";

export async function GET() {
  try {
    await db.$queryRaw`SELECT 1`;
    return NextResponse.json({ status: "ok" }, { headers: { "Cache-Control": "no-store" } });
  } catch (error) {
    console.error("Health check failed: PostgreSQL is unavailable.", error);
    return NextResponse.json(
      { status: "error", error: "Database unavailable" },
      { status: 503, headers: { "Cache-Control": "no-store" } },
    );
  }
}

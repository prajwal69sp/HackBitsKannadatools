import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { requireAdmin } from "@/lib/auth";

export async function GET(request: Request) {
  if (!await requireAdmin()) return NextResponse.json({ success: false, error: "Unauthorized" }, { status: 401 });
  const q = new URL(request.url).searchParams.get("q")?.trim().slice(0, 100) ?? "";
  if (q.length < 2) return NextResponse.json({ success: true, data: [] });
  const contains = { contains: q, mode: "insensitive" as const };
  const [commands, scripts, tools, tutorials, categories, tags] = await Promise.all([
    db.command.findMany({ where: { OR: [{ title: contains }, { command: contains }, { description: contains }] }, take: 5, select: { id: true, title: true } }),
    db.script.findMany({ where: { OR: [{ title: contains }, { description: contains }] }, take: 5, select: { id: true, title: true } }),
    db.tool.findMany({ where: { OR: [{ name: contains }, { description: contains }] }, take: 5, select: { id: true, name: true } }),
    db.tutorial.findMany({ where: { OR: [{ title: contains }, { description: contains }] }, take: 5, select: { id: true, title: true } }),
    db.category.findMany({ where: { OR: [{ name: contains }, { description: contains }] }, take: 5, select: { id: true, name: true } }),
    db.tag.findMany({ where: { name: contains }, take: 5, select: { id: true, name: true } }),
  ]);
  const data = [
    ...commands.map((row) => ({ ...row, label: row.title, type: "Command", href: "/admin/commands" })),
    ...scripts.map((row) => ({ ...row, label: row.title, type: "Script", href: "/admin/scripts" })),
    ...tools.map((row) => ({ ...row, label: row.name, type: "Tool", href: "/admin/tools" })),
    ...tutorials.map((row) => ({ ...row, label: row.title, type: "Tutorial", href: "/admin/tutorials" })),
    ...categories.map((row) => ({ ...row, label: row.name, type: "Category", href: "/admin/categories" })),
    ...tags.map((row) => ({ ...row, label: row.name, type: "Tag", href: "/admin/tags" })),
  ];
  return NextResponse.json({ success: true, data });
}

import { NextResponse } from "next/server";
import { AdminRole } from "@prisma/client";
import { db } from "@/lib/db";
import { requireAdmin } from "@/lib/auth";

export async function GET() {
  if (!await requireAdmin(AdminRole.SUPER_ADMIN)) return NextResponse.json({ success: false, error: "Super administrator access required" }, { status: 403 });
  const [commands, scripts, tools, tutorials, views, recent] = await Promise.all([
    db.command.count(), db.script.count(), db.tool.count(), db.tutorial.count(),
    db.analyticsEvent.groupBy({ by: ["entity", "entityId"], _count: { _all: true }, orderBy: { _count: { entityId: "desc" } }, take: 100 }),
    db.auditLog.findMany({ orderBy: { createdAt: "desc" }, take: 8 }),
  ]);
  const ids = [...new Set(views.map((view) => view.entityId))];
  const [commandRows, scriptRows, toolRows, tutorialRows] = await Promise.all([
    db.command.findMany({ where: { id: { in: ids } }, select: { id: true, title: true } }),
    db.script.findMany({ where: { id: { in: ids } }, select: { id: true, title: true } }),
    db.tool.findMany({ where: { id: { in: ids } }, select: { id: true, name: true } }),
    db.tutorial.findMany({ where: { id: { in: ids } }, select: { id: true, title: true } }),
  ]);
  const labels = new Map<string, string>();
  for (const item of commandRows) labels.set(item.id, item.title);
  for (const item of scriptRows) labels.set(item.id, item.title);
  for (const item of toolRows) labels.set(item.id, item.name);
  for (const item of tutorialRows) labels.set(item.id, item.title);
  const popular = views.map((view) => ({ type: view.entity, id: view.entityId, title: labels.get(view.entityId) ?? "Deleted item", views: view._count._all })).filter((item) => labels.has(item.id)).slice(0, 10);
  return NextResponse.json({ success: true, data: { counts: { commands, scripts, tools, tutorials }, popular, recent } });
}

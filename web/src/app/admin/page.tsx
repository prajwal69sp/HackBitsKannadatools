import Link from "next/link";
import { Activity, ArrowUpRight, BookOpen, Braces, Command, Package, Plus, TrendingUp } from "lucide-react";
import { db } from "@/lib/db";

export const dynamic = "force-dynamic";

export default async function DashboardPage() {
  const [
    commands, termux, linux, scripts, tools, tutorials, published, drafts,
    recentCommands, recentScripts, recentTools, recentTutorials, activity,
  ] = await Promise.all([
    db.command.count(), db.command.count({ where: { platform: "TERMUX" } }), db.command.count({ where: { platform: "LINUX" } }),
    db.script.count(), db.tool.count(), db.tutorial.count(), db.command.count({ where: { status: "PUBLISHED" } })
      .then(async (count) => count + await db.script.count({ where: { status: "PUBLISHED" } }) + await db.tool.count({ where: { status: "PUBLISHED" } }) + await db.tutorial.count({ where: { status: "PUBLISHED" } })),
    db.command.count({ where: { status: "DRAFT" } }).then(async (count) => count + await db.script.count({ where: { status: "DRAFT" } }) + await db.tool.count({ where: { status: "DRAFT" } }) + await db.tutorial.count({ where: { status: "DRAFT" } })),
    db.command.findMany({ orderBy: { updatedAt: "desc" }, take: 3, select: { id: true, title: true, category: true, updatedAt: true } }),
    db.script.findMany({ orderBy: { updatedAt: "desc" }, take: 3, select: { id: true, title: true, category: true, updatedAt: true } }),
    db.tool.findMany({ orderBy: { updatedAt: "desc" }, take: 3, select: { id: true, name: true, category: true, updatedAt: true } }),
    db.tutorial.findMany({ orderBy: { updatedAt: "desc" }, take: 3, select: { id: true, title: true, category: true, updatedAt: true } }),
    db.auditLog.findMany({ orderBy: { createdAt: "desc" }, take: 5 }),
  ]);
  const recent = [
    ...recentCommands.map((item) => ({ ...item, name: item.title, type: "Command" })),
    ...recentScripts.map((item) => ({ ...item, name: item.title, type: "Script" })),
    ...recentTools.map((item) => ({ ...item, name: item.name, type: "Tool" })),
    ...recentTutorials.map((item) => ({ ...item, name: item.title, type: "Tutorial" })),
  ].sort((a, b) => b.updatedAt.getTime() - a.updatedAt.getTime()).slice(0, 6);
  const cards = [
    { label: "Total commands", value: commands, note: "Across all platforms", icon: Command },
    { label: "Linux commands", value: linux, note: "Linux learning library", icon: Activity },
    { label: "Termux commands", value: termux, note: "Android terminal guides", icon: Braces },
    { label: "Scripts", value: scripts, note: "Bash, Python & more", icon: Braces },
    { label: "Tools", value: tools, note: "Educational tool guides", icon: Package },
    { label: "Tutorials", value: tutorials, note: "Lessons & learning paths", icon: BookOpen },
    { label: "Published content", value: published, note: "Visible to app users", icon: TrendingUp },
    { label: "Draft content", value: drafts, note: "Not yet synced to the app", icon: Command },
  ];
  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">Content & App Management Dashboard</div><h1>Dashboard</h1><p className="page-intro">Manage learning content and prepare updates for the HackBitsKannada app.</p></div><Link href="/admin/commands?new=1" className="btn btn-primary"><Plus size={15} /> Add command</Link></div>
    <div className="stats-grid">{cards.map(({ label, value, note, icon: Icon }) => <div className="card stat-card" key={label}><Icon size={16} className="stat-mark" /><div className="stat-label">{label}</div><div className="stat-value">{value}</div><div className="stat-foot">{note}</div></div>)}</div>
    <div className="dashboard-columns">
      <section><div className="section-title">Recent content <Link href="/admin/commands" className="btn btn-sm">View library <ArrowUpRight size={12} /></Link></div>
        <div className="card">{recent.length ? recent.map((item) => <div className="activity" style={{ padding: "14px 17px" }} key={`${item.type}-${item.id}`}><span className="activity-dot" /><div style={{ flex: 1 }}><div className="table-title">{item.name}</div><div className="table-muted">{item.type} · {item.category}</div></div><div className="activity-time">{item.updatedAt.toLocaleDateString()}</div></div>) : <div className="empty">No content yet. Add a command, script, tool or tutorial to get started.</div>}</div>
      </section>
      <section><div className="section-title">Quick actions <small>Build your learning library</small></div>
        <div className="card pad"><div className="quick-grid">
          {[["Command", "/admin/commands?new=1", "$_"], ["Script", "/admin/scripts?new=1", "{ }"], ["Tool", "/admin/tools?new=1", "⌘"], ["Tutorial", "/admin/tutorials?new=1", "▤"]].map(([label, href, mark]) => <Link className="quick-link" href={href} key={label}><span>{mark} &nbsp; CREATE NEW</span>+ Add {label}</Link>)}
        </div></div>
        <div className="section-title">Recent activity <small>Administrative changes</small></div>
        <div className="card pad">{activity.length ? activity.map((entry) => <div className="activity" key={entry.id}><span className="activity-dot" /><div><div><b>{entry.adminName}</b> {entry.action.toLowerCase().replaceAll("_", " ")} <span style={{ color: "var(--green)" }}>{entry.entity}</span></div><div className="activity-time">{entry.createdAt.toLocaleString()}</div></div></div>) : <div className="empty" style={{ padding: 22 }}>No activity recorded yet.</div>}</div>
      </section>
    </div>
    <p style={{ color: "var(--muted)", fontSize: 11, marginTop: 22 }}>Only published content is available through the public synchronization API. App users keep previously synced content for offline learning.</p>
  </div>;
}

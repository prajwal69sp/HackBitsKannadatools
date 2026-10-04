"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import {
  Activity, Archive, ArrowUpDown, BookOpen, Boxes, Braces, ChevronRight, Command, FileText,
  FolderTree, LayoutDashboard, LogOut, Menu, Search, Settings, Shield, Tags, Users,
} from "lucide-react";

const nav = [
  { label: "OVERVIEW", items: [{ name: "Dashboard", href: "/admin", icon: LayoutDashboard }] },
  { label: "CONTENT", items: [
    { name: "Commands", href: "/admin/commands", icon: Command },
    { name: "Termux commands", href: "/admin/termux", icon: Command },
    { name: "Linux commands", href: "/admin/linux", icon: Command },
    { name: "Scripts", href: "/admin/scripts", icon: Braces },
    { name: "Tools", href: "/admin/tools", icon: Boxes },
    { name: "Tutorials", href: "/admin/tutorials", icon: BookOpen },
    { name: "Categories", href: "/admin/categories", icon: FolderTree },
    { name: "Tags", href: "/admin/tags", icon: Tags },
  ] },
  { label: "WORKSPACE", items: [
    { name: "Media", href: "/admin/media", icon: Archive },
    { name: "Import / Export", href: "/admin/import-export", icon: ArrowUpDown },
    { name: "Analytics", href: "/admin/analytics", icon: Activity },
    { name: "Audit logs", href: "/admin/audit", icon: FileText },
    { name: "Admin users", href: "/admin/users", icon: Users, superOnly: true },
    { name: "Settings", href: "/admin/settings", icon: Settings, superOnly: true },
    { name: "About", href: "/admin/about", icon: Shield },
  ] },
];

type Admin = { id: string; name: string; email: string; role: "SUPER_ADMIN" | "EDITOR" };
type SearchResult = { id: string; label: string; type: string; href: string };

export function AdminShell({ admin, children }: { admin: Admin; children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<SearchResult[]>([]);
  const [searchOpen, setSearchOpen] = useState(false);
  const activeTitle = nav.flatMap((group) => group.items).find((item) => item.href === pathname)?.name ?? "Dashboard";

  useEffect(() => {
    if (query.trim().length < 2) return;
    const timer = setTimeout(() => {
      fetch(`/api/admin/search?q=${encodeURIComponent(query)}`).then((response) => response.json())
        .then((body) => setResults(body.success ? body.data : []))
        .catch(() => setResults([]));
    }, 250);
    return () => clearTimeout(timer);
  }, [query]);

  async function logout() {
    await fetch("/api/auth/logout", { method: "POST" });
    router.replace("/login");
    router.refresh();
  }

  return <div className="shell">
    {open && <button aria-label="Close navigation" onClick={() => setOpen(false)} style={{ position: "fixed", inset: 0, zIndex: 19, background: "#0009", border: 0 }} />}
    <aside className={`sidebar ${open ? "open" : ""}`}>
      <Link href="/admin" className="brand" onClick={() => setOpen(false)}><div className="brand-icon">&gt;_</div><div><div className="brand-name">HackBitsKannada</div><div className="brand-sub">Admin workspace</div></div></Link>
      {nav.map((group) => <div key={group.label}><div className="nav-label">{group.label}</div><nav className="nav-list">
        {group.items.filter((item) => !("superOnly" in item) || admin.role === "SUPER_ADMIN").map(({ name, href, icon: Icon }) => {
          const active = href === "/admin" ? pathname === href : pathname.startsWith(href);
          return <Link key={href} href={href} onClick={() => setOpen(false)} className={`nav-item ${active ? "active" : ""}`}><Icon size={16} />{name}{active && <ChevronRight size={13} style={{ marginLeft: "auto" }} />}</Link>;
        })}
      </nav></div>)}
      <div className="sidebar-bottom"><div style={{ color: "#aab9af", marginBottom: 5 }}>Content & App Management</div>HackBitsKannada · build 1.0</div>
    </aside>
    <main className="main">
      <header className="topbar">
        <div className="top-left"><button className="btn btn-quiet menu-toggle" aria-label="Open navigation" onClick={() => setOpen(!open)}><Menu size={18} /></button><span className="crumb">Workspace</span><ChevronRight size={13} color="#526158" /><span className="crumb" style={{ color: "#edf4ef" }}>{activeTitle}</span></div>
        <div className="top-actions">
          <div style={{ position: "relative" }} className="hide-mobile">
            <div style={{ position: "relative" }}><Search size={14} color="#7f9086" style={{ position: "absolute", left: 10, top: 10 }} /><input className="input" aria-label="Global search" placeholder="Search all content…" value={query} onChange={(event) => { setQuery(event.target.value); setSearchOpen(true); }} onFocus={() => setSearchOpen(true)} style={{ width: 230, padding: "8px 10px 8px 31px" }} /></div>
            {searchOpen && query.length > 1 && <div className="card" style={{ position: "absolute", top: 42, right: 0, width: 330, maxHeight: 360, overflow: "auto", zIndex: 30, padding: 6 }}>
              {results.length ? results.map((result) => <Link key={`${result.type}:${result.id}`} href={result.href} onClick={() => { setSearchOpen(false); setQuery(""); }} style={{ display: "flex", justifyContent: "space-between", gap: 10, padding: 10, fontSize: 11, borderRadius: 6 }} className="nav-item"><span>{result.label}</span><span className="pill">{result.type}</span></Link>) : <div className="empty" style={{ padding: 18 }}>No matching content</div>}
            </div>}
          </div>
          <div className="admin-chip"><div className="avatar">{admin.name.slice(0, 1).toUpperCase()}</div><div className="hide-mobile"><div>{admin.name}</div><div style={{ color: "var(--muted)", fontSize: 10 }}>{admin.role.replace("_", " ")}</div></div></div>
          <button className="btn btn-quiet" onClick={logout} title="Sign out" aria-label="Sign out"><LogOut size={16} /></button>
        </div>
      </header>
      {children}
    </main>
  </div>;
}

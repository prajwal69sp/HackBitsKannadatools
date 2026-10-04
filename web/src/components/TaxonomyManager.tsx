"use client";

import { useCallback, useEffect, useState } from "react";
import { Plus, Search, Trash2 } from "lucide-react";

type Category = { id: string; name: string; description: string; section: string; sortOrder: number; enabled: boolean };
type Tag = { id: string; name: string; usageCount: number };

export function CategoryManager() {
  const [items, setItems] = useState<Category[]>([]);
  const [editing, setEditing] = useState<Category | null>(null);
  const [form, setForm] = useState({ name: "", description: "", section: "General", sortOrder: 0, enabled: true });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const load = useCallback(async () => {
    const response = await fetch("/api/admin/categories");
    const body = await response.json();
    if (!response.ok) { setError(body.error || "Unable to load categories."); return; }
    setItems(body.data);
  }, []);
  useEffect(() => { const timer = setTimeout(() => void load(), 0); return () => clearTimeout(timer); }, [load]);
  async function save(event: React.FormEvent) {
    event.preventDefault(); setBusy(true); setError("");
    try {
      const response = await fetch("/api/admin/categories", {
        method: editing ? "PATCH" : "POST", headers: { "Content-Type": "application/json" },
        body: JSON.stringify(editing ? { id: editing.id, ...form } : form),
      });
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Unable to save category.");
      setForm({ name: "", description: "", section: "General", sortOrder: 0, enabled: true }); setEditing(null); await load();
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Unable to save."); }
    finally { setBusy(false); }
  }
  async function remove(id: string) {
    if (!window.confirm("Delete this category? Categories used by content cannot be removed.")) return;
    const response = await fetch("/api/admin/categories", { method: "DELETE", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ id }) });
    const body = await response.json();
    if (!response.ok) setError(body.error || "Unable to delete."); else await load();
  }
  function edit(item: Category) { setEditing(item); setForm({ name: item.name, description: item.description, section: item.section, sortOrder: item.sortOrder, enabled: item.enabled }); }
  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">Content structure</div><h1>Categories</h1><p className="page-intro">Organize commands, scripts, tools and tutorials by learning topic.</p></div></div>
    <div className="grid-2">
      <section className="card pad"><div className="section-title" style={{ marginTop: 0 }}>{editing ? "Edit category" : "Add category"}</div>
        <form onSubmit={save}><div className="field"><label>Category name</label><input className="input" required maxLength={100} value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="e.g. Package Management" /></div>
          <div className="field"><label>Section / platform group</label><input className="input" value={form.section} onChange={(event) => setForm({ ...form, section: event.target.value })} placeholder="Termux, Linux, Development…" /></div>
          <div className="field"><label>Description</label><textarea className="textarea" value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} /></div>
          <div className="form-grid"><div className="field"><label>Display order</label><input className="input" type="number" min="0" value={form.sortOrder} onChange={(event) => setForm({ ...form, sortOrder: Number(event.target.value) })} /></div><label className="check-row"><input type="checkbox" checked={form.enabled} onChange={(event) => setForm({ ...form, enabled: event.target.checked })} />Enabled</label></div>
          {error && <div className="error">{error}</div>}<div className="form-actions">{editing && <button className="btn" type="button" onClick={() => { setEditing(null); setForm({ name: "", description: "", section: "General", sortOrder: 0, enabled: true }); }}>Cancel</button>}<button className="btn btn-primary" disabled={busy}><Plus size={14} />{busy ? "Saving…" : editing ? "Save category" : "Create category"}</button></div>
        </form>
      </section>
      <section className="card"><div className="toolbar"><span style={{ fontSize: 12, fontWeight: 700 }}>{items.length} categories</span></div><div className="table-wrap"><table className="table" style={{ minWidth: 420 }}><thead><tr><th>Name</th><th>Section</th><th>Order</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><div className="table-title">{item.name}</div><div className="table-muted">{item.enabled ? item.description : "Disabled"}</div></td><td>{item.section}</td><td>{item.sortOrder}</td><td><div className="row-actions"><button className="btn btn-sm" onClick={() => edit(item)}>Edit</button><button className="btn btn-sm btn-danger" aria-label={`Delete ${item.name}`} onClick={() => void remove(item.id)}><Trash2 size={12} /></button></div></td></tr>)}</tbody></table>{items.length === 0 && <div className="empty">No categories yet.</div>}</div></section>
    </div>
  </div>;
}

export function TagManager() {
  const [items, setItems] = useState<Tag[]>([]);
  const [q, setQ] = useState("");
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const load = useCallback(async () => {
    const response = await fetch(`/api/admin/tags?q=${encodeURIComponent(q)}`);
    const body = await response.json();
    if (response.ok) setItems(body.data); else setError(body.error || "Unable to load tags.");
  }, [q]);
  useEffect(() => { const timer = setTimeout(() => void load(), 150); return () => clearTimeout(timer); }, [load]);
  async function add(event: React.FormEvent) {
    event.preventDefault(); setError("");
    const response = await fetch("/api/admin/tags", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ name }) });
    const body = await response.json();
    if (!response.ok) { setError(body.error || "Unable to add tag."); return; }
    setName(""); await load();
  }
  async function edit(item: Tag) {
    const next = window.prompt("Edit tag", item.name);
    if (!next || next === item.name) return;
    const response = await fetch("/api/admin/tags", { method: "PATCH", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ id: item.id, name: next }) });
    const body = await response.json();
    if (!response.ok) setError(body.error || "Unable to update tag."); else await load();
  }
  async function remove(item: Tag) {
    if (!window.confirm(`Delete "${item.name}"? It will be detached from ${item.usageCount} content item(s).`)) return;
    const response = await fetch("/api/admin/tags", { method: "DELETE", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ id: item.id }) });
    const body = await response.json();
    if (!response.ok) setError(body.error || "Unable to delete tag."); else await load();
  }
  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">Content structure</div><h1>Tags</h1><p className="page-intro">Manage reusable labels and see which topics are used most.</p></div></div>
    {error && <div className="error">{error}</div>}
    <section className="card">
      <div className="toolbar"><form onSubmit={add} style={{ display: "flex", gap: 8, flex: 1 }}><input className="input" value={name} onChange={(event) => setName(event.target.value)} placeholder="New tag name…" required maxLength={40} style={{ maxWidth: 310 }} /><button className="btn btn-primary"><Plus size={14} /> Add tag</button></form><div style={{ position: "relative" }}><Search size={13} style={{ position: "absolute", left: 9, top: 10 }} color="#718177" /><input className="input" value={q} onChange={(event) => setQ(event.target.value)} placeholder="Search tags…" style={{ width: 200, paddingLeft: 29 }} /></div></div>
      <div className="table-wrap"><table className="table" style={{ minWidth: 420 }}><thead><tr><th>Tag</th><th>Content items</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><span className="pill"># {item.name}</span></td><td>{item.usageCount}</td><td><div className="row-actions"><button className="btn btn-sm" onClick={() => void edit(item)}>Edit</button><button className="btn btn-sm btn-danger" onClick={() => void remove(item)} aria-label={`Delete tag ${item.name}`}><Trash2 size={12} /></button></div></td></tr>)}</tbody></table>{!items.length && <div className="empty">No tags found.</div>}</div>
    </section>
  </div>;
}

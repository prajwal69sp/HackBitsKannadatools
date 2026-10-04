"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { Plus, RefreshCw, Search, Trash2 } from "lucide-react";
import { CodeEditor } from "@/components/CodeEditor";

type ContentType = "commands" | "scripts" | "tools" | "tutorials";
type Field = { key: string; label: string; kind?: "text" | "textarea" | "code" | "select" | "check" | "json" | "category" | "number"; options?: string[]; wide?: boolean; hint?: string };
type Item = Record<string, unknown> & { id: string; createdAt?: string; updatedAt?: string };

const choices = {
  statuses: ["DRAFT", "PUBLISHED", "ARCHIVED"],
  difficulty: ["BEGINNER", "INTERMEDIATE", "ADVANCED"],
  platform: ["TERMUX", "LINUX", "ANDROID_LINUX", "GENERAL"],
};
const config: Record<ContentType, { title: string; singular: string; nameKey: string; fields: Field[] }> = {
  commands: { title: "Commands", singular: "command", nameKey: "title", fields: [
    { key: "title", label: "Title" }, { key: "command", label: "Command", kind: "code", wide: true },
    { key: "category", label: "Category", kind: "category" }, { key: "platform", label: "Platform", kind: "select", options: choices.platform },
    { key: "description", label: "Description", kind: "textarea", wide: true },
    { key: "syntax", label: "Syntax", kind: "code" }, { key: "difficulty", label: "Difficulty", kind: "select", options: choices.difficulty },
    { key: "example", label: "Example", kind: "code", wide: true }, { key: "expectedOutput", label: "Expected output", kind: "code", wide: true },
    { key: "warning", label: "Safety warning", kind: "textarea", wide: true }, { key: "relatedCommands", label: "Related command IDs", hint: "Comma-separated IDs" },
    { key: "tags", label: "Tags", hint: "Comma-separated labels" }, { key: "status", label: "Status", kind: "select", options: choices.statuses },
    { key: "featured", label: "Featured", kind: "check" },
  ] },
  scripts: { title: "Scripts", singular: "script", nameKey: "title", fields: [
    { key: "title", label: "Title" }, { key: "language", label: "Language", kind: "select", options: ["Bash", "Shell", "Python", "JavaScript", "Other"] },
    { key: "category", label: "Category", kind: "category" }, { key: "difficulty", label: "Difficulty", kind: "select", options: choices.difficulty },
    { key: "description", label: "Description", kind: "textarea", wide: true }, { key: "code", label: "Source code", kind: "code", wide: true },
    { key: "explanation", label: "Explanation", kind: "textarea", wide: true }, { key: "usage", label: "Usage", kind: "code", wide: true },
    { key: "exampleOutput", label: "Example output", kind: "code", wide: true },
    { key: "lineExplanations", label: "Line-by-line notes (JSON array)", kind: "json", wide: true },
    { key: "warning", label: "Safety warning", kind: "textarea", wide: true }, { key: "tags", label: "Tags", hint: "Comma-separated labels" },
    { key: "status", label: "Status", kind: "select", options: choices.statuses }, { key: "featured", label: "Featured", kind: "check" },
  ] },
  tools: { title: "Tools", singular: "tool", nameKey: "name", fields: [
    { key: "name", label: "Tool name" }, { key: "category", label: "Category", kind: "category" },
    { key: "platform", label: "Platform", kind: "select", options: choices.platform }, { key: "difficulty", label: "Difficulty", kind: "select", options: choices.difficulty },
    { key: "description", label: "Description", kind: "textarea", wide: true }, { key: "installationCommand", label: "Installation command", kind: "code", wide: true },
    { key: "basicUsage", label: "Basic usage", kind: "code", wide: true }, { key: "examples", label: "Examples", kind: "code", wide: true },
    { key: "documentation", label: "Documentation URL" }, { key: "warning", label: "Authorization / safety warning", kind: "textarea", wide: true },
    { key: "tags", label: "Tags", hint: "Comma-separated labels" }, { key: "status", label: "Status", kind: "select", options: choices.statuses },
    { key: "featured", label: "Featured", kind: "check" },
  ] },
  tutorials: { title: "Tutorials", singular: "tutorial", nameKey: "title", fields: [
    { key: "title", label: "Title" }, { key: "category", label: "Category", kind: "category" },
    { key: "difficulty", label: "Difficulty", kind: "select", options: choices.difficulty }, { key: "estimatedMinutes", label: "Learning time (minutes)", kind: "number" },
    { key: "description", label: "Description", kind: "textarea", wide: true }, { key: "coverImage", label: "Cover image URL", wide: true },
    { key: "lessons", label: "Lessons (JSON array)", kind: "json", wide: true, hint: 'Each lesson: {"title":"...","content":"...","codeExamples":[]} (array order is lesson order)' },
    { key: "tags", label: "Tags", hint: "Comma-separated labels" }, { key: "status", label: "Status", kind: "select", options: choices.statuses },
    { key: "featured", label: "Featured", kind: "check" },
  ] },
};

function readText(value: unknown) { return typeof value === "string" ? value : ""; }
function flattenRecord(item: Item, type: ContentType): Record<string, unknown> {
  const data = { ...item };
  const relations = data.tags;
  data.tags = Array.isArray(relations) ? relations.map((row) => {
    if (typeof row === "string") return row;
    if (row && typeof row === "object" && "tag" in row) return (row.tag as { name?: string }).name ?? "";
    return "";
  }).join(", ") : "";
  if (type === "tutorials") data.lessons = JSON.stringify(data.lessons ?? [], null, 2);
  if (type === "scripts" && Array.isArray(data.lineExplanations)) data.lineExplanations = JSON.stringify(data.lineExplanations, null, 2);
  if (type === "tutorials" && typeof data.lessons === "string") {
    try { data.lessons = JSON.stringify(JSON.parse(data.lessons), null, 2); } catch { /* Leave editable text intact. */ }
  }
  return data;
}

export function ContentManager({ type, initialPlatform, autoOpen = false }: { type: ContentType; initialPlatform?: string; autoOpen?: boolean }) {
  const meta = config[type];
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [pages, setPages] = useState(1);
  const [total, setTotal] = useState(0);
  const [q, setQ] = useState("");
  const [status, setStatus] = useState("");
  const [category, setCategory] = useState("");
  const [difficulty, setDifficulty] = useState("");
  const [platform, setPlatform] = useState(initialPlatform ?? "");
  const [categories, setCategories] = useState<Array<{ name: string; enabled: boolean }>>([]);
  const [selected, setSelected] = useState<string[]>([]);
  const [editing, setEditing] = useState<Item | null>(null);
  const [draft, setDraft] = useState<Record<string, unknown>>({});
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [sort, setSort] = useState("updatedAt");
  const [direction, setDirection] = useState<"asc" | "desc">("desc");

  const load = useCallback(async () => {
    setLoading(true);
    const params = new URLSearchParams({ page: String(page), pageSize: "20", sort, direction });
    if (q) params.set("q", q);
    if (status) params.set("status", status);
    if (category) params.set("category", category);
    if (difficulty) params.set("difficulty", difficulty);
    if (platform) params.set("platform", platform);
    try {
      const response = await fetch(`/api/admin/content/${type}?${params}`);
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Unable to load content.");
      setItems(body.data);
      setPages(body.pagination.pages || 1);
      setTotal(body.pagination.total);
      setSelected([]);
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Unable to load content."); }
    finally { setLoading(false); }
  }, [page, q, status, category, difficulty, platform, sort, direction, type]);

  useEffect(() => { const timer = setTimeout(() => void load(), 220); return () => clearTimeout(timer); }, [load]);
  useEffect(() => {
    fetch("/api/admin/categories").then(async (response) => {
      const body = await response.json();
      if (response.ok) setCategories(body.data);
    }).catch(() => setError("Unable to load categories."));
  }, []);
  const defaultDraft = useMemo(() => Object.fromEntries(meta.fields.map(({ key, kind, options }) => [
    key,
    kind === "check" ? false : kind === "json" ? "[]" : kind === "category" ? categories[0]?.name ?? "" : key === "status" ? "DRAFT" : options?.[0] ?? "",
  ])), [meta.fields, categories]);
  useEffect(() => {
    if (!autoOpen) return;
    const timer = setTimeout(() => { setEditing(null); setDraft(defaultDraft); }, 0);
    return () => clearTimeout(timer);
  }, [autoOpen, defaultDraft]);
  function openNew() { setEditing(null); setDraft(defaultDraft); setError(""); }
  function openEdit(item: Item) { setEditing(item); setDraft(flattenRecord(item, type)); setError(""); }

  function getValue(key: string, value: unknown) {
    if (key === "tags") return typeof value === "string" ? value.split(",").map((tag) => tag.trim()).filter(Boolean) : [];
    if (key === "relatedCommands") return typeof value === "string" ? value : "";
    if (key === "estimatedMinutes") return Number(value) || 0;
    if (key === "lineExplanations" || key === "lessons") {
      try { return JSON.parse(String(value || "[]")); } catch { throw new Error(`${key} must contain valid JSON.`); }
    }
    if (["featured"].includes(key)) return Boolean(value);
    return value ?? "";
  }

  async function save(event: React.FormEvent) {
    event.preventDefault(); setBusy(true); setError("");
    try {
      const data = Object.fromEntries(Object.entries(draft).map(([key, value]) => [key, getValue(key, value)]));
      const response = await fetch(`/api/admin/content/${type}`, {
        method: editing ? "PATCH" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(editing ? { id: editing.id, data } : data),
      });
      const body = await response.json();
      if (!response.ok) throw new Error(body.details ? `${body.error}: ${JSON.stringify(body.details)}` : body.error || "Unable to save.");
      setDraft({}); await load();
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Unable to save."); }
    finally { setBusy(false); }
  }

  async function operate(action: string, ids: string[]) {
    if (action === "delete" && !window.confirm(`Permanently delete ${ids.length} ${meta.singular}${ids.length > 1 ? "s" : ""}? This cannot be undone.`)) return;
    setError("");
    const response = await fetch(`/api/admin/content/${type}`, { method: "PATCH", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ action, ids }) });
    const body = await response.json();
    if (!response.ok) { setError(body.error || "Action failed."); return; }
    await load();
  }

  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">Content library / {meta.title}</div><h1>{meta.title}</h1><p className="page-intro">Create, edit, publish and organize your app content.</p></div><button className="btn btn-primary" onClick={openNew}><Plus size={15} /> Add {meta.singular}</button></div>
    <section className="card">
      <div className="toolbar">
        <div className="searchbox" style={{ position: "relative" }}><Search size={14} color="#718177" style={{ position: "absolute", top: 10, left: 10 }} /><input className="input" placeholder={`Search ${meta.title.toLowerCase()}…`} value={q} onChange={(event) => { setQ(event.target.value); setPage(1); }} style={{ paddingLeft: 32 }} /></div>
        <select className="select" aria-label="Filter by status" value={status} onChange={(event) => { setStatus(event.target.value); setPage(1); }}><option value="">All statuses</option>{choices.statuses.map((value) => <option key={value}>{value}</option>)}</select>
        <select className="select" aria-label="Filter by difficulty" value={difficulty} onChange={(event) => { setDifficulty(event.target.value); setPage(1); }}><option value="">All difficulty</option>{choices.difficulty.map((value) => <option key={value}>{value}</option>)}</select>
        <select className="select" aria-label="Filter by category" value={category} onChange={(event) => { setCategory(event.target.value); setPage(1); }}><option value="">All categories</option>{categories.map((item) => <option key={item.name}>{item.name}</option>)}</select>
        {type === "commands" || type === "tools" ? <select className="select" aria-label="Filter by platform" value={platform} onChange={(event) => { setPlatform(event.target.value); setPage(1); }}><option value="">All platforms</option>{choices.platform.map((value) => <option key={value}>{value}</option>)}</select> : null}
        <select className="select" aria-label="Sort content" value={sort} onChange={(event) => setSort(event.target.value)}><option value="updatedAt">Recently updated</option><option value="createdAt">Recently created</option><option value="title">Title</option></select>
        <select className="select" aria-label="Sort direction" value={direction} onChange={(event) => setDirection(event.target.value as "asc" | "desc")}><option value="desc">Descending</option><option value="asc">Ascending</option></select>
        <button className="btn btn-sm" onClick={() => void load()}><RefreshCw size={13} /> Refresh</button>
      </div>
      {selected.length > 0 && <div className="toolbar" style={{ background: "#101b14" }}><span style={{ fontSize: 11, color: "var(--green)" }}>{selected.length} selected</span><button className="btn btn-sm" onClick={() => void operate("publish", selected)}>Publish</button><button className="btn btn-sm" onClick={() => void operate("unpublish", selected)}>Unpublish</button><button className="btn btn-sm" onClick={() => void operate("archive", selected)}>Archive</button><button className="btn btn-sm btn-danger" onClick={() => void operate("delete", selected)}><Trash2 size={12} /> Delete</button></div>}
      {error && !draft && <div className="error" style={{ margin: 12 }}>{error}</div>}
      <div className="table-wrap"><table className="table"><thead><tr><th><input type="checkbox" aria-label="Select all records on page" checked={items.length > 0 && selected.length === items.length} onChange={(event) => setSelected(event.target.checked ? items.map((item) => item.id) : [])} /></th><th>Content</th><th>Category</th><th>Status</th><th>Updated</th><th>Actions</th></tr></thead>
        <tbody>{loading ? <tr><td colSpan={6} className="empty">Loading content…</td></tr> : items.length === 0 ? <tr><td colSpan={6} className="empty">No {meta.title.toLowerCase()} found. Create your first one to get started.</td></tr> : items.map((item) => {
          const name = readText(item[meta.nameKey]) || "Untitled";
          const state = readText(item.status);
          return <tr key={item.id}><td><input type="checkbox" aria-label={`Select ${name}`} checked={selected.includes(item.id)} onChange={(event) => setSelected(event.target.checked ? [...selected, item.id] : selected.filter((id) => id !== item.id))} /></td>
            <td><div className="table-title">{name}{item.featured ? <span className="pill pill-green" style={{ marginLeft: 7 }}>Featured</span> : null}</div><div className="table-muted">{readText(item.command) || readText(item.language) || readText(item.platform) || item.id}</div></td>
            <td>{readText(item.category) || "—"}</td><td><span className={`pill ${state === "PUBLISHED" ? "pill-green" : state === "ARCHIVED" ? "pill-red" : "pill-yellow"}`}>{state}</span></td>
            <td>{item.updatedAt ? new Date(String(item.updatedAt)).toLocaleDateString() : "—"}</td>
            <td><div className="row-actions"><button className="btn btn-sm" onClick={() => openEdit(item)}>Edit</button><button className="btn btn-sm" onClick={() => void operate("duplicate", [item.id])}>Duplicate</button><button className="btn btn-sm btn-danger" aria-label={`Delete ${name}`} onClick={() => void operate("delete", [item.id])}><Trash2 size={12} /></button></div></td></tr>;
        })}</tbody></table></div>
      <div className="pagination"><span>{total} records · Page {page} of {pages}</span><div style={{ display: "flex", gap: 7 }}><button className="btn btn-sm" disabled={page <= 1} onClick={() => setPage(page - 1)}>Previous</button><button className="btn btn-sm" disabled={page >= pages} onClick={() => setPage(page + 1)}>Next</button></div></div>
    </section>
    {Object.keys(draft).length > 0 && <div className="dialog-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setDraft({}); }}>
      <section className="dialog card" role="dialog" aria-modal="true" aria-labelledby="content-form-heading">
        <div className="dialog-head"><div><div className="eyebrow">{editing ? "Edit content" : "New content"}</div><h2 id="content-form-heading">{editing ? `Edit ${meta.singular}` : `Add ${meta.singular}`}</h2></div><button className="btn btn-sm" onClick={() => setDraft({})}>Close</button></div>
        <form onSubmit={save}><div className="form-grid">
          {meta.fields.map((field) => {
            const value = draft[field.key];
            if (field.kind === "check") return <label className={`check-row ${field.wide ? "span-2" : ""}`} key={field.key}><input type="checkbox" checked={Boolean(value)} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.checked })} />{field.label}</label>;
            return <div className={`field ${field.wide ? "span-2" : ""}`} key={field.key}><label htmlFor={`field-${field.key}`}>{field.label}</label>
              {field.kind === "code" ? <CodeEditor id={`field-${field.key}`} required={["command", "code"].includes(field.key)} value={readText(value)} language={field.key === "code" ? readText(draft.language) : "bash"} onChange={(next) => setDraft({ ...draft, [field.key]: next })} />
                : field.kind === "category" ? <select className="select" id={`field-${field.key}`} value={readText(value)} required onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value })}><option value="" disabled>Select a category</option>{categories.map((category) => <option key={category.name} value={category.name}>{category.name}</option>)}</select>
                : field.kind === "select" ? <select className="select" id={`field-${field.key}`} value={readText(value)} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value })}>{field.options?.map((option) => <option key={option} value={option}>{option.replaceAll("_", " ")}</option>)}</select>
                : field.kind === "textarea" || field.kind === "json" ? <textarea id={`field-${field.key}`} className={`textarea ${field.kind === "json" ? "code-input" : ""}`} rows={field.kind === "json" ? 6 : 3} value={readText(value)} required={["title", "name", "command", "description", "code", "category"].includes(field.key)} onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value })} />
                  : <input id={`field-${field.key}`} className="input" type={field.kind === "number" ? "number" : "text"} min={field.kind === "number" ? 0 : undefined} value={readText(value)} required={["title", "name", "command", "category"].includes(field.key)} onChange={(event) => setDraft({ ...draft, [field.key]: field.kind === "number" ? Number(event.target.value) : event.target.value })} />}
              {field.hint && <small className="field-hint">{field.hint}</small>}</div>;
          })}
        </div>
          {categories.length === 0 && <div className="error">Create an enabled category before adding content.</div>}
          {error && <div className="error" role="alert">{error}</div>}
          <div className="form-actions"><button type="button" className="btn" onClick={() => setDraft({})}>Cancel</button><button className="btn btn-primary" disabled={busy || categories.length === 0}>{busy ? "Saving…" : editing ? "Save changes" : `Create ${meta.singular}`}</button></div>
        </form>
      </section>
    </div>}
  </div>;
}

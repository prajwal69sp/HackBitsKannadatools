"use client";

import { useCallback, useEffect, useState } from "react";
import { Download, FileUp, ImagePlus, Plus, Save, Trash2, Users } from "lucide-react";

type Config = {
  appName: string; subtitle: string; about: string; description: string; version: string; maintenance: boolean;
  contactEmail: string; socialLinks: Record<string, string>; privacyPolicy: string; disclaimer: string;
  aboutSections: Record<string, string>;
};
const defaultAbout = "HackBitsKannada is a technology-focused learning platform created to make Termux, Linux, programming, AI tools, Android customization, web development, app development and ethical hacking easier to understand.";
const aboutTopics = ["About", "What We Teach", "Termux", "Linux", "Programming", "AI Tools", "Android", "Web Development", "Cybersecurity", "Ethical Hacking"];

export function SettingsPanel({ aboutOnly = false }: { aboutOnly?: boolean }) {
  const [config, setConfig] = useState<Config>({
    appName: "HackBitsKannada", subtitle: "Termux & Linux Lab", about: defaultAbout, description: "", version: "1.0.0",
    maintenance: false, contactEmail: "", socialLinks: {}, privacyPolicy: "",
    disclaimer: "Use security tools only on systems you own or have explicit permission to test. HackBitsKannada provides educational material for responsible security learning, authorized testing, personal laboratories and CTF environments.",
    aboutSections: Object.fromEntries(aboutTopics.map((topic) => [topic, ""])),
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  useEffect(() => {
    fetch(aboutOnly ? "/api/admin/about" : "/api/admin/settings").then(async (response) => {
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Unable to load configuration.");
      if (body.data) setConfig((current) => ({ ...current, ...body.data, aboutSections: { ...current.aboutSections, ...(body.data.aboutSections as Record<string, string>) } }));
    }).catch((cause) => setError(cause instanceof Error ? cause.message : "Unable to load configuration."));
  }, [aboutOnly]);
  async function save(event: React.FormEvent) {
    event.preventDefault(); setBusy(true); setError(""); setSuccess("");
    try {
      const response = await fetch(aboutOnly ? "/api/admin/about" : "/api/admin/settings", {
        method: aboutOnly ? "PUT" : "PUT", headers: { "Content-Type": "application/json" },
        body: JSON.stringify(aboutOnly ? { about: config.about, aboutSections: config.aboutSections } : config),
      });
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Unable to save configuration.");
      setSuccess("Changes saved successfully.");
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Unable to save."); }
    finally { setBusy(false); }
  }
  function textField(key: keyof Config, label: string, multiline = false) {
    const value = config[key];
    if (typeof value !== "string") return null;
    return <div className="field"><label>{label}</label>{multiline ? <textarea className="textarea" rows={4} value={value} onChange={(event) => setConfig({ ...config, [key]: event.target.value })} /> : <input className="input" value={value} onChange={(event) => setConfig({ ...config, [key]: event.target.value })} />}</div>;
  }
  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">{aboutOnly ? "App content" : "System configuration"}</div><h1>{aboutOnly ? "About HackBitsKannada" : "App settings"}</h1><p className="page-intro">{aboutOnly ? "Edit the app’s About screen and learning topics." : "Configure app information and public content API values."}</p></div></div>
    <form onSubmit={save} className="card pad" style={{ maxWidth: 1000 }}>
      {aboutOnly ? <>
        {textField("about", "About HackBitsKannada", true)}
        <div className="section-title">Learning topics <small>Descriptions shown in the Android app</small></div>
        <div className="form-grid">{aboutTopics.slice(1).map((topic) => <div className="field" key={topic}><label>{topic}</label><textarea className="textarea" rows={3} value={config.aboutSections[topic] ?? ""} onChange={(event) => setConfig({ ...config, aboutSections: { ...config.aboutSections, [topic]: event.target.value } })} placeholder={`About ${topic.toLowerCase()}…`} /></div>)}</div>
      </> : <>
        <div className="section-title" style={{ marginTop: 0 }}>Application</div><div className="form-grid">
          {textField("appName", "App name")}{textField("subtitle", "Subtitle")}{textField("version", "Version")}
          <div className="field"><label>Contact email</label><input className="input" type="email" value={config.contactEmail} onChange={(event) => setConfig({ ...config, contactEmail: event.target.value })} placeholder="Optional" /></div>
          {textField("description", "App description", true)}
        </div>
        <label className="check-row"><input type="checkbox" checked={config.maintenance} onChange={(event) => setConfig({ ...config, maintenance: event.target.checked })} />Maintenance mode <span className="field-hint">Inform app clients that the service is temporarily unavailable</span></label>
        <div className="section-title">Social links <small>Only add links you own; values are not prefilled</small></div>
        {["Website", "GitHub", "YouTube", "Instagram", "Other"].map((key) => <div className="field" key={key}><label>{key}</label><input className="input" type="url" value={config.socialLinks[key] ?? ""} onChange={(event) => setConfig({ ...config, socialLinks: { ...config.socialLinks, [key]: event.target.value } })} placeholder="https://" /></div>)}
        <div className="section-title">Legal and privacy</div>{textField("privacyPolicy", "Privacy policy", true)}{textField("disclaimer", "Responsible-use disclaimer", true)}
        <div className="section-title">Featured content <small>Mark items as featured in the content editor</small></div><p className="page-intro">The app’s featured feed is managed by the Featured toggle on each command, script, tool and tutorial.</p>
      </>}
      {error && <div className="error">{error}</div>}{success && <div className="success">{success}</div>}
      <div className="form-actions"><button className="btn btn-primary" disabled={busy}><Save size={14} />{busy ? "Saving…" : "Save changes"}</button></div>
    </form>
  </div>;
}

type AdminUser = { id: string; name: string; email: string; role: "SUPER_ADMIN" | "EDITOR"; status: "ACTIVE" | "DISABLED"; createdAt: string; lastLoginAt: string | null };
const emptyUser = { name: "", email: "", role: "EDITOR", status: "ACTIVE", password: "" };
export function UsersPanel({ selfId }: { selfId: string }) {
  const [items, setItems] = useState<AdminUser[]>([]);
  const [form, setForm] = useState({ ...emptyUser });
  const [editing, setEditing] = useState<AdminUser | null>(null);
  const [show, setShow] = useState(false);
  const [error, setError] = useState("");
  const load = useCallback(async () => {
    const response = await fetch("/api/admin/users");
    const body = await response.json();
    if (response.ok) setItems(body.data); else setError(body.error || "Unable to load administrator accounts.");
  }, []);
  useEffect(() => { const timer = setTimeout(() => void load(), 0); return () => clearTimeout(timer); }, [load]);
  async function save(event: React.FormEvent) {
    event.preventDefault(); setError("");
    const payload = { ...form, ...(editing && !form.password ? { password: undefined } : {}) };
    const response = await fetch("/api/admin/users", { method: editing ? "PATCH" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(editing ? { id: editing.id, ...payload } : payload) });
    const body = await response.json();
    if (!response.ok) { setError(body.error || "Unable to save account."); return; }
    setShow(false); setEditing(null); setForm({ ...emptyUser }); await load();
  }
  function openEdit(user: AdminUser) { setEditing(user); setForm({ name: user.name, email: user.email, role: user.role, status: user.status, password: "" }); setShow(true); }
  async function remove(user: AdminUser) {
    if (!window.confirm(`Permanently remove admin ${user.email}?`)) return;
    const response = await fetch("/api/admin/users", { method: "DELETE", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ id: user.id }) });
    const body = await response.json();
    if (!response.ok) setError(body.error || "Unable to remove account."); else await load();
  }
  return <div className="page">
    <div className="page-heading"><div><div className="eyebrow">Access control</div><h1>Admin users</h1><p className="page-intro">Manage staff access. Editors can manage learning content; only super admins can manage accounts and settings.</p></div><button className="btn btn-primary" onClick={() => { setEditing(null); setForm({ ...emptyUser }); setShow(true); }}><Plus size={14} /> Add administrator</button></div>
    {error && <div className="error">{error}</div>}
    <section className="card"><div className="table-wrap"><table className="table"><thead><tr><th>Administrator</th><th>Role</th><th>Status</th><th>Last login</th><th>Created</th><th>Actions</th></tr></thead><tbody>{items.map((user) => <tr key={user.id}><td><div className="table-title">{user.name}{user.id === selfId && <span className="pill pill-green" style={{ marginLeft: 7 }}>You</span>}</div><div className="table-muted">{user.email}</div></td><td>{user.role.replace("_", " ")}</td><td><span className={`pill ${user.status === "ACTIVE" ? "pill-green" : "pill-red"}`}>{user.status}</span></td><td>{user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : "Never"}</td><td>{new Date(user.createdAt).toLocaleDateString()}</td><td><div className="row-actions"><button className="btn btn-sm" onClick={() => openEdit(user)}>Edit</button>{user.id !== selfId && <button className="btn btn-sm btn-danger" onClick={() => void remove(user)} aria-label={`Delete ${user.email}`}><Trash2 size={12} /></button>}</div></td></tr>)}</tbody></table></div></section>
    {show && <div className="dialog-backdrop"><section className="dialog card" style={{ maxWidth: 520 }}><div className="dialog-head"><h2>{editing ? "Edit administrator" : "Add administrator"}</h2><button className="btn btn-sm" onClick={() => setShow(false)}>Close</button></div><form onSubmit={save}>
      <div className="field"><label>Name</label><input className="input" required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} /></div><div className="field"><label>Email</label><input className="input" type="email" required value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} /></div>
      <div className="field"><label>{editing ? "New password (optional)" : "Password (12+ characters)"}</label><input className="input" type="password" minLength={12} autoComplete="new-password" required={!editing} value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} /></div>
      <div className="form-grid"><div className="field"><label>Role</label><select className="select" value={form.role} onChange={(event) => setForm({ ...form, role: event.target.value })}><option value="EDITOR">Editor</option><option value="SUPER_ADMIN">Super admin</option></select></div><div className="field"><label>Status</label><select className="select" value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value })}><option value="ACTIVE">Active</option><option value="DISABLED">Disabled</option></select></div></div>
      <div className="form-actions"><button type="button" className="btn" onClick={() => setShow(false)}>Cancel</button><button className="btn btn-primary"><Users size={14} /> Save administrator</button></div></form></section></div>}
  </div>;
}

export function AuditPanel() {
  const [data, setData] = useState<Array<Record<string, unknown>>>([]);
  const [page, setPage] = useState(1);
  const [pages, setPages] = useState(1);
  const [error, setError] = useState("");
  useEffect(() => { fetch(`/api/admin/audit?page=${page}`).then(async (response) => { const body = await response.json(); if (!response.ok) throw new Error(body.error); setData(body.data); setPages(body.pagination.pages || 1); }).catch((cause) => setError(cause instanceof Error ? cause.message : "Unable to load audit log.")); }, [page]);
  return <div className="page"><div className="page-heading"><div><div className="eyebrow">Security & traceability</div><h1>Audit logs</h1><p className="page-intro">Administrative actions. Authentication secrets and passwords are never recorded.</p></div></div>{error && <div className="error">{error}</div>}<section className="card"><div className="table-wrap"><table className="table"><thead><tr><th>Administrator</th><th>Action</th><th>Entity</th><th>Entity ID</th><th>Timestamp</th></tr></thead><tbody>{data.map((row) => <tr key={String(row.id)}><td>{String(row.adminName)}</td><td><span className="pill">{String(row.action)}</span></td><td>{String(row.entity)}</td><td className="table-muted">{String(row.entityId ?? "—")}</td><td>{new Date(String(row.createdAt)).toLocaleString()}</td></tr>)}</tbody></table>{!data.length && <div className="empty">No activity recorded.</div>}</div><div className="pagination"><span>Page {page} of {pages}</span><div style={{ display: "flex", gap: 8 }}><button className="btn btn-sm" disabled={page <= 1} onClick={() => setPage(page - 1)}>Previous</button><button className="btn btn-sm" disabled={page >= pages} onClick={() => setPage(page + 1)}>Next</button></div></div></section></div>;
}

export function AnalyticsPanel() {
  const [data, setData] = useState<{ counts: Record<string, number>; popular: Array<{ id: string; title: string; type: string; views: number }> } | null>(null);
  const [error, setError] = useState("");
  useEffect(() => { fetch("/api/admin/analytics").then(async (response) => { const body = await response.json(); if (!response.ok) throw new Error(body.error); setData(body.data); }).catch((cause) => setError(cause instanceof Error ? cause.message : "Unable to load analytics.")); }, []);
  return <div className="page"><div className="page-heading"><div><div className="eyebrow">Usage insights</div><h1>Analytics</h1><p className="page-intro">Content counts and privacy-conscious events recorded by the public synchronization API.</p></div></div>{error && <div className="error">{error}</div>}
    <div className="stats-grid">{Object.entries(data?.counts ?? {}).map(([name, count]) => <div className="card stat-card" key={name}><div className="stat-label">{name}</div><div className="stat-value">{count}</div><div className="stat-foot">content items</div></div>)}</div>
    <div className="section-title">Most viewed content <small>Only available when the app reports view events</small></div><section className="card"><div className="table-wrap"><table className="table" style={{ minWidth: 440 }}><thead><tr><th>Content</th><th>Type</th><th>Views</th></tr></thead><tbody>{data?.popular.map((item) => <tr key={item.id}><td>{item.title}</td><td>{item.type}</td><td>{item.views}</td></tr>)}</tbody></table>{!data?.popular.length && <div className="empty">No view analytics yet. Events are not attributed to individual users.</div>}</div></section>
  </div>;
}

export function MediaPanel() {
  const [media, setMedia] = useState<Array<{ id: string; filename: string; url: string; altText: string; sizeBytes: number }>>([]);
  const [altText, setAltText] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const load = useCallback(async () => { const response = await fetch("/api/admin/media"); const body = await response.json(); if (response.ok) setMedia(body.data); else setError(body.error || "Unable to load media."); }, []);
  useEffect(() => { const timer = setTimeout(() => void load(), 0); return () => clearTimeout(timer); }, [load]);
  async function upload(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setBusy(true); setError(""); setSuccess("");
    const form = new FormData(event.currentTarget);
    try {
      const response = await fetch("/api/admin/media", { method: "POST", body: form });
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Upload failed.");
      setSuccess("Image uploaded."); setAltText(""); event.currentTarget.reset(); await load();
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Upload failed."); }
    finally { setBusy(false); }
  }
  async function remove(id: string) {
    if (!window.confirm("Delete this image permanently?")) return;
    const response = await fetch("/api/admin/media", { method: "DELETE", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ id }) });
    const body = await response.json(); if (!response.ok) setError(body.error || "Delete failed."); else await load();
  }
  return <div className="page"><div className="page-heading"><div><div className="eyebrow">Asset library</div><h1>Media</h1><p className="page-intro">Upload tutorial covers, tool logos, thumbnails and banners. PNG, JPEG, WebP and AVIF only.</p></div></div>
    <section className="card pad" style={{ maxWidth: 700 }}><form onSubmit={upload}><div className="field"><label>Image file</label><input className="input" name="file" type="file" accept="image/jpeg,image/png,image/webp,image/avif" required /></div><div className="field"><label>Alt text</label><input className="input" name="altText" maxLength={250} value={altText} onChange={(event) => setAltText(event.target.value)} placeholder="Describe this image" /></div>{error && <div className="error">{error}</div>}{success && <div className="success">{success}</div>}<button className="btn btn-primary" disabled={busy}><ImagePlus size={14} />{busy ? "Uploading…" : "Upload image"}</button></form><p className="field-hint" style={{ marginTop: 12 }}>Files are size-limited. Configure S3-compatible storage for production or use local storage for development.</p></section>
    <div className="section-title">Uploaded media <small>{media.length} files</small></div><div className="grid-2">{media.map((item) => <section key={item.id} className="card pad" style={{ display: "flex", gap: 13, alignItems: "center" }}><img src={item.url} alt={item.altText || item.filename} width={82} height={68} style={{ objectFit: "cover", borderRadius: 7, background: "#18221c" }} /><div style={{ flex: 1, minWidth: 0 }}><div className="table-title" style={{ overflowWrap: "anywhere" }}>{item.filename}</div><div className="table-muted">{Math.ceil(item.sizeBytes / 1024)} KB · {item.altText || "No alt text"}</div><input className="input" readOnly value={item.url} style={{ marginTop: 8, fontSize: 10 }} /></div><button className="btn btn-sm btn-danger" aria-label={`Delete ${item.filename}`} onClick={() => void remove(item.id)}><Trash2 size={13} /></button></section>)}</div>{!media.length && <section className="card empty">No media uploaded yet.</section>}
  </div>;
}

type ImportResult = { valid: number; invalid: number; imported?: number; errors: Array<{ row: number; errors: unknown }> };
export function ImportExportPanel() {
  const [type, setType] = useState("commands");
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<ImportResult | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  async function preview(commit: boolean) {
    if (!file) { setError("Choose a JSON or CSV file."); return; }
    setBusy(true); setError("");
    const form = new FormData(); form.set("file", file); form.set("type", type); form.set("format", file.name.toLowerCase().endsWith(".csv") ? "csv" : "json"); form.set("commit", String(commit));
    try {
      const response = await fetch("/api/admin/import-export", { method: "POST", body: form });
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Import failed.");
      setResult(body.data);
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Import failed."); }
    finally { setBusy(false); }
  }
  const exportUrl = (format: string) => `/api/admin/import-export?type=${type}&format=${format}`;
  return <div className="page"><div className="page-heading"><div><div className="eyebrow">Data portability</div><h1>Import / Export</h1><p className="page-intro">Export a backup or validate content records before importing. Imports add new items and never overwrite existing content.</p></div></div>
    <div className="grid-2">
      <section className="card pad"><div className="section-title" style={{ marginTop: 0 }}>Export content</div><p className="page-intro" style={{ marginBottom: 17 }}>Download JSON for structured backups or CSV for spreadsheets.</p><div className="field"><label>Content type</label><select className="select" value={type} onChange={(event) => setType(event.target.value)}><option value="commands">Commands</option><option value="scripts">Scripts</option><option value="tools">Tools</option><option value="tutorials">Tutorials</option></select></div><div style={{ display: "flex", gap: 9 }}><a className="btn btn-primary" href={exportUrl("json")}><Download size={14} /> Export JSON</a><a className="btn" href={exportUrl("csv")}><Download size={14} /> Export CSV</a></div></section>
      <section className="card pad"><div className="section-title" style={{ marginTop: 0 }}>Import content</div><p className="page-intro" style={{ marginBottom: 17 }}>Validate each record before committing. Invalid rows are skipped and displayed.</p><div className="field"><label>Content type</label><select className="select" value={type} onChange={(event) => { setType(event.target.value); setResult(null); }}><option value="commands">Commands</option><option value="scripts">Scripts</option><option value="tools">Tools</option><option value="tutorials">Tutorials</option></select></div><div className="field"><label>JSON or CSV file</label><input className="input" type="file" accept=".json,.csv,application/json,text/csv" onChange={(event) => { setFile(event.target.files?.[0] ?? null); setResult(null); }} /></div>{error && <div className="error">{error}</div>}<button className="btn btn-primary" disabled={busy || !file} onClick={() => void preview(false)}><FileUp size={14} />{busy ? "Validating…" : "Validate import"}</button>
        {result && <div className="card pad" style={{ marginTop: 15 }}><div style={{ display: "flex", gap: 15 }}><span className="pill pill-green">{result.valid} valid</span><span className={`pill ${result.invalid ? "pill-red" : "pill-green"}`}>{result.invalid} invalid</span>{result.imported !== undefined && <span className="pill pill-green">{result.imported} imported</span>}</div>
          {result.errors.length > 0 && <div style={{ maxHeight: 150, overflow: "auto", marginTop: 10 }}>{result.errors.slice(0, 20).map((item) => <div className="table-muted" key={item.row}>Row {item.row}: {JSON.stringify(item.errors)}</div>)}</div>}
          {result.imported === undefined && result.valid > 0 && <button className="btn btn-primary" style={{ marginTop: 14 }} disabled={busy} onClick={() => { if (window.confirm(`Add ${result.valid} records? Existing items will not be overwritten.`)) void preview(true); }}>Confirm import of {result.valid}</button>}
        </div>}
      </section>
    </div>
    <div className="section-title">Supported data fields <small>Record names must match the selected content type</small></div><div className="card pad"><p className="page-intro">JSON imports accept either a top-level array of records or an object with a <code>data</code> array. CSV first rows contain field names. Each record is validated against the same schema used by the content editor. The import is additive only; records are assigned new database IDs.</p></div>
  </div>;
}

"use client";

import { useState } from "react";
import { Eye, EyeOff, ShieldCheck } from "lucide-react";
import { useRouter } from "next/navigation";

export function LoginForm() {
  const router = useRouter();
  const [show, setShow] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setBusy(true);
    const form = new FormData(event.currentTarget);
    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: form.get("email"), password: form.get("password") }),
      });
      const body = await response.json();
      if (!response.ok) throw new Error(body.error || "Unable to sign in.");
      router.replace("/admin");
      router.refresh();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to sign in.");
    } finally {
      setBusy(false);
    }
  }
  return <section className="login-card card">
    <div className="login-brand"><div className="brand-icon" style={{ width: 46, height: 46, fontSize: 18 }}>&gt;_</div>
      <div><div className="brand-name" style={{ fontSize: 18 }}>HackBitsKannada</div><div className="brand-sub">Content & App Management Dashboard</div></div></div>
    <div className="terminal-line"><b>admin@hackbits</b>:~$ authenticate --secure</div>
    <form onSubmit={submit}>
      <div className="field"><label htmlFor="email">Email address</label><input className="input" id="email" name="email" type="email" autoComplete="username" placeholder="admin@example.com" required /></div>
      <div className="field"><label htmlFor="password">Password</label><div style={{ position: "relative" }}><input className="input" id="password" name="password" type={show ? "text" : "password"} autoComplete="current-password" placeholder="Enter your password" required style={{ paddingRight: 42 }} /><button className="btn btn-quiet" type="button" aria-label={show ? "Hide password" : "Show password"} onClick={() => setShow(!show)} style={{ position: "absolute", right: 3, top: 3, padding: 7 }}>{show ? <EyeOff size={16} /> : <Eye size={16} />}</button></div></div>
      {error && <div className="error" role="alert">{error}</div>}
      <button className="btn btn-primary" disabled={busy} style={{ width: "100%", padding: 12, marginTop: 5 }}>{busy ? "Authenticating…" : <><ShieldCheck size={15} /> Sign in securely</>}</button>
    </form>
    <div style={{ color: "var(--muted)", fontSize: 10, textAlign: "center", marginTop: 19 }}>Protected admin access · HTTP-only session</div>
  </section>;
}

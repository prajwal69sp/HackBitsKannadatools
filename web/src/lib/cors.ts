const localHosts = new Set(["localhost", "127.0.0.1", "[::1]"]);

export function allowedApiOrigins(environment: NodeJS.ProcessEnv = process.env): Set<string> {
  const origins = new Set<string>();
  const production = environment.NODE_ENV === "production";
  for (const entry of (environment.CORS_ALLOWED_ORIGINS ?? "").split(",")) {
    const value = entry.trim();
    if (!value || value === "*") continue;
    try {
      const parsed = new URL(value);
      const localHttp = !production && parsed.protocol === "http:" && localHosts.has(parsed.hostname);
      if ((parsed.protocol !== "https:" && !localHttp) || parsed.username || parsed.password ||
        parsed.pathname !== "/" || parsed.search || parsed.hash) continue;
      origins.add(parsed.origin);
    } catch {
      continue;
    }
  }
  return origins;
}

export function isAllowedApiOrigin(origin: string, allowedOrigins = allowedApiOrigins()): boolean {
  try {
    return allowedOrigins.has(new URL(origin).origin) && origin === new URL(origin).origin;
  } catch {
    return false;
  }
}

import assert from "node:assert/strict";
import test from "node:test";

const base = process.env.API_BASE_URL;
const adminEmail = process.env.ADMIN_EMAIL;
const adminPassword = process.env.ADMIN_PASSWORD;

test("authenticated API supports CRUD, sync, import/export, audit, and role checks", { skip: !base || !adminEmail || !adminPassword }, async () => {
  const origin = base!;
  let superCookie = "";
  let editorCookie = "";
  const commandIds: string[] = [];
  const contentIds: Record<string, string[]> = { scripts: [], tools: [], tutorials: [] };
  let editorId = "";
  const request = (path: string, init: RequestInit = {}, cookie = superCookie) =>
    fetch(`${origin}${path}`, {
      ...init,
        headers: { ...(init.body && !(init.body instanceof FormData) ? { "Content-Type": "application/json" } : {}), ...(cookie ? { Cookie: cookie } : {}), ...(init.headers ?? {}) },
    });
  const json = (method: string, path: string, data?: unknown, cookie = superCookie) =>
    request(path, { method, headers: { Origin: origin, ...(data instanceof FormData ? {} : { "Content-Type": "application/json" }) }, body: data instanceof FormData ? data : data === undefined ? undefined : JSON.stringify(data) }, cookie);
  const unique = `api-test-${Date.now()}`;
  const command = (title: string, status = "DRAFT") => ({
    title, command: "pwd", category: "Termux Basics", platform: "TERMUX", description: "Integration test educational command.",
    syntax: "pwd", example: "pwd", expectedOutput: "/", difficulty: "BEGINNER", warning: "", relatedCommands: "",
    status, featured: false, tags: ["api-test"],
  });

  const health = await request("/api/health", {}, "");
  assert.equal(health.status, 200);
  assert.deepEqual(await health.json(), { status: "ok" });
  const allowedOrigin = "https://android.example.test";
  const allowedCors = await fetch(`${origin}/api/v1/categories`, { headers: { Origin: allowedOrigin } });
  assert.equal(allowedCors.status, 200);
  assert.equal(allowedCors.headers.get("access-control-allow-origin"), allowedOrigin);
  const deniedCors = await fetch(`${origin}/api/v1/categories`, { headers: { Origin: "https://attacker.example.test" } });
  assert.equal(deniedCors.status, 403);
  const preflight = await fetch(`${origin}/api/v1/events`, {
    method: "OPTIONS",
    headers: {
      Origin: allowedOrigin,
      "Access-Control-Request-Method": "POST",
      "Access-Control-Request-Headers": "content-type",
    },
  });
  assert.equal(preflight.status, 204);
  assert.equal(preflight.headers.get("access-control-allow-origin"), allowedOrigin);

  const invalid = await json("POST", "/api/auth/login", { email: adminEmail, password: "wrong-password" }, "");
  assert.equal(invalid.status, 401);
  const login = await json("POST", "/api/auth/login", { email: adminEmail, password: adminPassword }, "");
  assert.equal(login.status, 200);
  superCookie = login.headers.get("set-cookie")?.split(";")[0] ?? "";
  assert.ok(superCookie.includes("hbk_admin_session="));

  for (const route of ["/admin", "/admin/commands", "/admin/termux", "/admin/linux", "/admin/scripts", "/admin/tools", "/admin/tutorials", "/admin/categories", "/admin/tags", "/admin/media", "/admin/import-export", "/admin/analytics", "/admin/audit", "/admin/users", "/admin/settings", "/admin/about"]) {
    const page = await request(route);
    assert.equal(page.status, 200, `${route} should load with a valid session`);
  }
  assert.equal((await request("/api/admin/content/commands")).status, 200);
  const unsafeUpload = new FormData();
  unsafeUpload.set("file", new File(["<svg/onload=alert(1)>"], "image.svg", { type: "image/svg+xml" }));
  assert.equal((await json("POST", "/api/admin/media", unsafeUpload)).status, 400);

  const created = await json("POST", "/api/admin/content/commands", command(unique));
  assert.equal(created.status, 201);
  const record = (await created.json() as { data: { id: string } }).data;
  commandIds.push(record.id);
  const search = await request(`/api/admin/content/commands?q=${encodeURIComponent(unique)}&status=DRAFT`);
  assert.equal((await search.json() as { pagination: { total: number } }).pagination.total, 1);

  const updated = await json("PATCH", "/api/admin/content/commands", { id: record.id, data: command(`${unique}-edited`, "PUBLISHED") });
  assert.equal(updated.status, 200);
  const published = await request("/api/v1/commands?q=does-not-exist");
  assert.equal(published.status, 200);
  const publishedRows = await request("/api/v1/commands?page=1&pageSize=100");
  const rows = (await publishedRows.json() as { data: Array<{ id: string }> }).data;
  assert.ok(rows.some((item) => item.id === record.id));
  const event = await fetch(`${origin}/api/v1/events`, {
    method: "POST",
    headers: { Origin: allowedOrigin, "Content-Type": "application/json" },
    body: JSON.stringify({ events: [{ type: "commands", id: record.id }] }),
  });
  assert.equal(event.status, 200);
  for (const resource of ["scripts", "tools", "tutorials", "categories", "featured", "config"]) {
    assert.equal((await request(`/api/v1/${resource}`)).status, 200);
  }

  const scriptRecord = {
    title: `${unique}-script`, language: "Python", category: "Python", description: "Read-only API test script.",
    code: "print('safe')", explanation: "Prints a string.", usage: "python demo.py", exampleOutput: "safe",
    difficulty: "BEGINNER", warning: "", status: "DRAFT", featured: false, tags: ["api-test"],
  };
  const scriptCreate = await json("POST", "/api/admin/content/scripts", scriptRecord);
  assert.equal(scriptCreate.status, 201);
  const createdScript = (await scriptCreate.json() as { data: { id: string } }).data;
  contentIds.scripts.push(createdScript.id);
  assert.equal((await json("PATCH", "/api/admin/content/scripts", { id: createdScript.id, data: { ...scriptRecord, status: "PUBLISHED" } })).status, 200);

  const toolRecord = {
    name: `${unique}-tool`, description: "Authorized learning tool reference.", category: "Development", platform: "GENERAL",
    installationCommand: "", basicUsage: "help", examples: "", documentation: "", difficulty: "BEGINNER",
    warning: "", status: "PUBLISHED", featured: true, tags: [],
  };
  const toolCreate = await json("POST", "/api/admin/content/tools", toolRecord);
  assert.equal(toolCreate.status, 201);
  contentIds.tools.push((await toolCreate.json() as { data: { id: string } }).data.id);

  const tutorialRecord = {
    title: `${unique}-tutorial`, description: "A multi-lesson tutorial.", category: "Termux Basics",
    difficulty: "BEGINNER", coverImage: "", estimatedMinutes: 15, status: "DRAFT", featured: false, tags: [],
    lessons: [{ title: "First lesson", content: "Start safely.", codeExamples: ["pwd"] }],
  };
  const tutorialCreate = await json("POST", "/api/admin/content/tutorials", tutorialRecord);
  assert.equal(tutorialCreate.status, 201);
  const createdTutorial = (await tutorialCreate.json() as { data: { id: string } }).data;
  contentIds.tutorials.push(createdTutorial.id);
  const tutorialEdit = await json("PATCH", "/api/admin/content/tutorials", {
    id: createdTutorial.id, data: { ...tutorialRecord, status: "PUBLISHED", lessons: [
      { title: "First lesson", content: "Start safely.", codeExamples: ["pwd"] },
      { title: "Second lesson", content: "Keep learning.", codeExamples: ["ls"] },
    ] },
  });
  assert.equal(tutorialEdit.status, 200);
  const publicTutorials = await request("/api/v1/tutorials?page=1&pageSize=100");
  const tutorialRows = (await publicTutorials.json() as { data: Array<{ id: string; lessons: unknown[] }> }).data;
  assert.equal(tutorialRows.find((item) => item.id === createdTutorial.id)?.lessons.length, 2);

  const duplicate = await json("PATCH", "/api/admin/content/commands", { action: "duplicate", ids: [record.id] });
  assert.equal(duplicate.status, 201);
  commandIds.push((await duplicate.json() as { data: { id: string } }).data.id);
  const exported = await request("/api/admin/import-export?type=commands&format=json");
  assert.equal(exported.status, 200);
  assert.ok((await exported.text()).includes(`${unique}-edited`));

  const jsonFile = new File([JSON.stringify({ success: true, data: { commands: [command(`${unique}-json`)] } })], "commands.json", { type: "application/json" });
  const jsonForm = new FormData(); jsonForm.set("file", jsonFile); jsonForm.set("type", "commands"); jsonForm.set("format", "json");
  assert.equal((await json("POST", "/api/admin/import-export", jsonForm)).status, 200);
  jsonForm.set("commit", "true");
  assert.equal((await (await json("POST", "/api/admin/import-export", jsonForm)).json() as { data: { imported: number } }).data.imported, 1);
  const jsonItem = await request(`/api/admin/content/commands?q=${encodeURIComponent(`${unique}-json`)}`);
  commandIds.push(...(await jsonItem.json() as { data: Array<{ id: string }> }).data.map((item) => item.id));

  const csv = new File([`title,command,category,platform,description,difficulty,status,featured,tags\n${unique}-csv,pwd,Termux Basics,TERMUX,CSV import test,BEGINNER,DRAFT,false,api-test`], "commands.csv", { type: "text/csv" });
  const form = new FormData(); form.set("file", csv); form.set("type", "commands"); form.set("format", "csv");
  const preview = await json("POST", "/api/admin/import-export", form);
  assert.equal(preview.status, 200);
  assert.equal((await preview.json() as { data: { valid: number; invalid: number } }).data.valid, 1);
  form.set("commit", "true");
  const imported = await json("POST", "/api/admin/import-export", form);
  const importResult = (await imported.json() as { data: { imported: number } }).data;
  assert.equal(importResult.imported, 1);
  const csvItem = await request(`/api/admin/content/commands?q=${encodeURIComponent(`${unique}-csv`)}`);
  commandIds.push(...(await csvItem.json() as { data: Array<{ id: string }> }).data.map((item) => item.id));

  const editorEmail = `${unique}@example.test`;
  const editor = await json("POST", "/api/admin/users", {
    name: "Integration Editor", email: editorEmail, role: "EDITOR", status: "ACTIVE", password: "Integration-Editor-Password-2026",
  });
  assert.equal(editor.status, 201);
  editorId = (await editor.json() as { data: { id: string } }).data.id;
  const editorLogin = await json("POST", "/api/auth/login", { email: editorEmail, password: "Integration-Editor-Password-2026" }, "");
  assert.equal(editorLogin.status, 200);
  editorCookie = editorLogin.headers.get("set-cookie")?.split(";")[0] ?? "";
  assert.ok(editorCookie);
  assert.equal((await request("/api/admin/users", {}, editorCookie)).status, 403);
  assert.equal((await request("/api/admin/settings", {}, editorCookie)).status, 403);
  assert.equal((await request("/api/admin/content/commands", {}, editorCookie)).status, 200);
  const deleteAsEditor = await json("PATCH", "/api/admin/content/commands", { action: "delete", ids: [record.id] }, editorCookie);
  assert.equal(deleteAsEditor.status, 200);
  commandIds.splice(commandIds.indexOf(record.id), 1);
  await json("POST", "/api/auth/logout", undefined, editorCookie);

  const cleanup = await json("PATCH", "/api/admin/content/commands", { action: "delete", ids: commandIds }, superCookie);
  assert.equal(cleanup.status, 200);
  for (const [type, ids] of Object.entries(contentIds)) {
    assert.equal((await json("PATCH", `/api/admin/content/${type}`, { action: "delete", ids })).status, 200);
  }
  const deleteEditor = await json("DELETE", "/api/admin/users", { id: editorId });
  assert.equal(deleteEditor.status, 200);
  const audit = await request("/api/admin/audit");
  assert.equal(audit.status, 200);
  const logout = await json("POST", "/api/auth/logout");
  assert.equal(logout.status, 200);
  superCookie = "";
  assert.equal((await request("/api/admin/content/commands", {}, "")).status, 401);
});

import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";

const port = Number(process.env.PORT || 3000);
if (!Number.isInteger(port) || port < 1 || port > 65535) {
  console.error("PORT must be an integer between 1 and 65535.");
  process.exit(1);
}

const nextBin = fileURLToPath(new URL("../node_modules/next/dist/bin/next", import.meta.url));
const webDirectory = fileURLToPath(new URL("../web/", import.meta.url));
const server = spawn(process.execPath, [nextBin, "start", "--hostname", "0.0.0.0", "--port", String(port)], {
  cwd: webDirectory,
  env: process.env,
  stdio: "inherit",
});

for (const signal of ["SIGINT", "SIGTERM"]) {
  process.on(signal, () => server.kill(signal));
}

server.on("error", (error) => {
  console.error("Failed to start the Next.js production server:", error);
  process.exitCode = 1;
});
server.on("exit", (code, signal) => {
  if (signal) process.kill(process.pid, signal);
  else process.exitCode = code ?? 1;
});

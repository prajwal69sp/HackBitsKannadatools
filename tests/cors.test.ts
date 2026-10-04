import assert from "node:assert/strict";
import test from "node:test";
import { allowedApiOrigins, isAllowedApiOrigin } from "../web/src/lib/cors";

test("production CORS accepts only explicitly configured HTTPS origins", () => {
  const allowed = allowedApiOrigins({
    NODE_ENV: "production",
    CORS_ALLOWED_ORIGINS: "https://app.example.test, https://admin.example.test",
  });
  assert.deepEqual([...allowed].sort(), ["https://admin.example.test", "https://app.example.test"]);
  assert.equal(isAllowedApiOrigin("https://app.example.test", allowed), true);
  assert.equal(isAllowedApiOrigin("https://attacker.example.test", allowed), false);
  assert.equal(isAllowedApiOrigin("https://app.example.test.evil.test", allowed), false);
  assert.equal(isAllowedApiOrigin("http://app.example.test", allowed), false);
});

test("CORS rejects wildcard, URL paths, credentials and production localhost origins", () => {
  const allowed = allowedApiOrigins({
    NODE_ENV: "production",
    CORS_ALLOWED_ORIGINS: "*, https://app.example.test/path, https://user:pass@app.example.test, http://localhost:3000",
  });
  assert.deepEqual([...allowed], []);
});

test("local development can allow localhost origins without enabling production HTTP", () => {
  const allowed = allowedApiOrigins({
    NODE_ENV: "development",
    CORS_ALLOWED_ORIGINS: "http://localhost:5173, https://app.example.test",
  });
  assert.equal(isAllowedApiOrigin("http://localhost:5173", allowed), true);
  assert.equal(allowedApiOrigins({
    NODE_ENV: "production",
    CORS_ALLOWED_ORIGINS: "http://localhost:5173",
  }).size, 0);
});

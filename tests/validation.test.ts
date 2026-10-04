import assert from "node:assert/strict";
import test from "node:test";
import { commandSchema, scriptSchema, toolSchema, tutorialSchema } from "../web/src/lib/validation";

test("command content requires safe typed fields and supplies optional defaults", () => {
  const result = commandSchema.safeParse({
    title: "List files", command: "ls", category: "Linux", platform: "LINUX", description: "Show files.",
    difficulty: "BEGINNER", status: "DRAFT",
  });
  assert.equal(result.success, true);
  if (result.success) {
    assert.deepEqual(result.data.tags, []);
    assert.equal(result.data.featured, false);
    assert.equal(result.data.warning, "");
  }
  assert.equal(commandSchema.safeParse({ command: "rm -rf /" }).success, false);
});

test("scripts validate their language, code, and line-by-line notes", () => {
  const valid = scriptSchema.safeParse({
    title: "Safe example", language: "Python", category: "Python", description: "Read-only demo", code: "print('hello')",
    difficulty: "BEGINNER", status: "DRAFT", lineExplanations: ["Print a greeting."],
  });
  assert.equal(valid.success, true);
  assert.equal(scriptSchema.safeParse({
    title: "Bad", language: "Unknown", category: "Python", description: "x", code: "x",
    difficulty: "BEGINNER", status: "DRAFT",
  }).success, false);
});

test("tools require valid documentation URLs and tutorials bound lesson content", () => {
  const tool = toolSchema.safeParse({
    name: "curl", description: "Transfer URLs", category: "Networking", platform: "GENERAL",
    difficulty: "BEGINNER", status: "PUBLISHED", documentation: "https://curl.se/docs/",
  });
  assert.equal(tool.success, true);
  assert.equal(toolSchema.safeParse({
    name: "curl", description: "Transfer URLs", category: "Networking", platform: "GENERAL",
    difficulty: "BEGINNER", status: "DRAFT", documentation: "",
  }).success, true);
  assert.equal(toolSchema.safeParse({
    name: "curl", description: "Transfer URLs", category: "Networking", platform: "GENERAL",
    difficulty: "BEGINNER", status: "PUBLISHED", documentation: "javascript:alert(1)",
  }).success, false);
  assert.equal(tutorialSchema.safeParse({
    title: "Intro", description: "Learn safely", category: "Termux", difficulty: "BEGINNER",
    estimatedMinutes: 30, status: "DRAFT",
    lessons: [{ title: "Lesson 1", content: "Read-only basics", codeExamples: ["pwd"] }],
  }).success, true);
});

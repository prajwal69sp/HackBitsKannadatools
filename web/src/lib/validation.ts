import { z } from "zod";

const text = (max = 10000) => z.string().trim().max(max);
const tags = z.array(z.string().trim().min(1).max(40)).max(30).default([]);

export const commandSchema = z.object({
  title: text(160).min(1),
  command: text(4000).min(1),
  category: text(100).min(1),
  platform: z.enum(["TERMUX", "LINUX", "ANDROID_LINUX", "GENERAL"]),
  description: text().min(1),
  syntax: text(1000).default(""),
  example: text(4000).default(""),
  expectedOutput: text(4000).default(""),
  difficulty: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED"]),
  warning: text(4000).default(""),
  relatedCommands: text(1000).default(""),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  featured: z.boolean().default(false),
  tags,
});

export const scriptSchema = z.object({
  title: text(160).min(1),
  language: z.enum(["Bash", "Shell", "Python", "JavaScript", "Other"]),
  category: text(100).min(1),
  description: text().min(1),
  code: text(20000).min(1),
  explanation: text().default(""),
  usage: text(4000).default(""),
  exampleOutput: text(4000).default(""),
  lineExplanations: z.array(z.string().max(1000)).max(500).default([]),
  difficulty: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED"]),
  warning: text(4000).default(""),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  featured: z.boolean().default(false),
  tags,
});

export const toolSchema = z.object({
  name: text(160).min(1),
  description: text().min(1),
  category: text(100).min(1),
  platform: z.enum(["TERMUX", "LINUX", "ANDROID_LINUX", "GENERAL"]),
  installationCommand: text(4000).default(""),
  basicUsage: text(4000).default(""),
  examples: text(4000).default(""),
  documentation: z.string().url().refine((value) => /^https:\/\//i.test(value)).or(z.literal("")).default(""),
  difficulty: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED"]),
  warning: text(4000).default(""),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  featured: z.boolean().default(false),
  tags,
});

export const tutorialSchema = z.object({
  title: text(160).min(1),
  description: text().min(1),
  category: text(100).min(1),
  difficulty: z.enum(["BEGINNER", "INTERMEDIATE", "ADVANCED"]),
  coverImage: z.string().max(500).default(""),
  estimatedMinutes: z.number().int().min(0).max(10000),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  featured: z.boolean().default(false),
  tags,
  lessons: z.array(z.object({
    id: z.string().optional(),
    title: text(160).min(1),
    content: text().min(1),
    codeExamples: z.array(z.string().max(10000)).max(30).default([]),
  })).max(100).default([]),
});

export const categorySchema = z.object({
  name: text(100).min(1),
  description: text(2000).default(""),
  section: text(100).default("General"),
  sortOrder: z.number().int().min(0).max(100000).default(0),
  enabled: z.boolean().default(true),
});

export const tagSchema = z.object({ name: text(40).min(1) });

export const configSchema = z.object({
  appName: text(120).min(1),
  subtitle: text(160),
  about: text(),
  description: text(),
  version: text(40),
  maintenance: z.boolean(),
  contactEmail: z.union([z.string().email(), z.literal("")]),
  socialLinks: z.record(z.string(), z.string().url().or(z.literal(""))),
  privacyPolicy: text(),
  disclaimer: text(),
  aboutSections: z.record(z.string(), z.string()),
});

export type ContentType = "commands" | "scripts" | "tools" | "tutorials";

export const contentSchemas = {
  commands: commandSchema,
  scripts: scriptSchema,
  tools: toolSchema,
  tutorials: tutorialSchema,
} satisfies Record<ContentType, z.ZodType>;

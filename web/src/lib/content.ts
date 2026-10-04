import { Prisma, type PrismaClient } from "@prisma/client";
import { db } from "@/lib/db";
import type { ContentType } from "@/lib/validation";

function nestedTagCreate(tags: string[]) {
  return { create: tags.map((name) => ({ tag: { connectOrCreate: { where: { name }, create: { name } } } })) };
}

export async function createContentItem(type: ContentType, value: Record<string, unknown>) {
  const { tags = [], lessons = [], ...fields } = value;
  const relations = { tags: nestedTagCreate(tags as string[]) };
  if (type === "commands") return db.command.create({ data: { ...(fields as Prisma.CommandCreateInput), ...relations } });
  if (type === "scripts") return db.script.create({ data: { ...(fields as Prisma.ScriptCreateInput), ...relations } });
  if (type === "tools") return db.tool.create({ data: { ...(fields as Prisma.ToolCreateInput), ...relations } });
  return db.tutorial.create({
    data: {
      ...(fields as Prisma.TutorialCreateInput),
      ...relations,
      lessons: { create: (lessons as Array<{ title: string; content: string; codeExamples: string[] }>).map((lesson, index) => ({ title: lesson.title, content: lesson.content, codeExamples: lesson.codeExamples, sortOrder: index })) },
    },
    include: { lessons: true },
  });
}

export async function enabledCategoryExists(value: Record<string, unknown>, client: PrismaClient = db) {
  return typeof value.category === "string" && Boolean(await client.category.findFirst({ where: { name: value.category, enabled: true }, select: { id: true } }));
}

import { randomUUID } from "node:crypto";
import { mkdir, unlink, writeFile } from "node:fs/promises";
import path from "node:path";
import { DeleteObjectCommand, PutObjectCommand, S3Client } from "@aws-sdk/client-s3";
import { AdminRole } from "@prisma/client";
import { NextResponse } from "next/server";
import { db } from "@/lib/db";
import { isSameOrigin, requireAdmin } from "@/lib/auth";

export const runtime = "nodejs";
const imageTypes: Record<string, { extension: string; matches: (bytes: Uint8Array) => boolean }> = {
  "image/jpeg": { extension: "jpg", matches: (b) => b[0] === 0xff && b[1] === 0xd8 && b[2] === 0xff },
  "image/png": { extension: "png", matches: (b) => b[0] === 0x89 && b[1] === 0x50 && b[2] === 0x4e && b[3] === 0x47 },
  "image/webp": { extension: "webp", matches: (b) => String.fromCharCode(...b.slice(0, 4)) === "RIFF" && String.fromCharCode(...b.slice(8, 12)) === "WEBP" },
  "image/avif": { extension: "avif", matches: (b) => String.fromCharCode(...b.slice(4, 12)).includes("ftypavif") },
};

function s3Client() {
  const region = process.env.S3_REGION;
  const endpoint = process.env.S3_ENDPOINT;
  const accessKeyId = process.env.S3_ACCESS_KEY_ID;
  const secretAccessKey = process.env.S3_SECRET_ACCESS_KEY;
  if (!region || !accessKeyId || !secretAccessKey) throw new Error("S3_REGION, S3_ACCESS_KEY_ID, and S3_SECRET_ACCESS_KEY are required for S3 media storage.");
  return new S3Client({ region, ...(endpoint ? { endpoint, forcePathStyle: true } : {}), credentials: { accessKeyId, secretAccessKey } });
}

export async function GET() {
  if (!await requireAdmin(AdminRole.SUPER_ADMIN)) return NextResponse.json({ success: false, error: "Super administrator access required" }, { status: 403 });
  const base = process.env.MEDIA_PUBLIC_URL?.replace(/\/$/, "");
  const rows = await db.media.findMany({ orderBy: { createdAt: "desc" } });
  return NextResponse.json({ success: true, data: rows.map((media) => ({ ...media, url: base ? `${base}/${media.filename}` : media.storagePath })) });
}

export async function POST(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return NextResponse.json({ success: false, error: "Super administrator access required" }, { status: 403 });
  if (!isSameOrigin(request)) return NextResponse.json({ success: false, error: "Invalid request origin" }, { status: 403 });
  const form = await request.formData();
  const file = form.get("file");
  if (!(file instanceof File)) return NextResponse.json({ success: false, error: "Select an image file." }, { status: 400 });
  const maxBytes = Math.min(20, Math.max(1, Number(process.env.MAX_MEDIA_MB) || 5)) * 1024 * 1024;
  if (file.size < 1 || file.size > maxBytes) return NextResponse.json({ success: false, error: `Image must be smaller than ${Math.floor(maxBytes / 1024 / 1024)} MB.` }, { status: 400 });
  const type = imageTypes[file.type];
  if (!type) return NextResponse.json({ success: false, error: "Supported images: JPEG, PNG, WebP, AVIF." }, { status: 400 });
  const bytes = new Uint8Array(await file.arrayBuffer());
  if (!type.matches(bytes)) return NextResponse.json({ success: false, error: "File content does not match its image type." }, { status: 400 });
  const key = `${randomUUID()}.${type.extension}`;
  const bucket = process.env.S3_BUCKET;
  let storagePath: string;
  if (bucket) {
    await s3Client().send(new PutObjectCommand({ Bucket: bucket, Key: key, Body: bytes, ContentType: file.type }));
    storagePath = key;
  } else {
    if (process.env.NODE_ENV === "production") {
      return NextResponse.json({ success: false, error: "Configure S3-compatible media storage before uploading in production." }, { status: 503 });
    }
    const directory = path.join(process.cwd(), "public", "uploads");
    await mkdir(directory, { recursive: true });
    await writeFile(path.join(directory, key), bytes, { flag: "wx" });
    storagePath = `/uploads/${key}`;
  }
  const media = await db.media.create({
    data: { filename: key, storagePath, mimeType: file.type, sizeBytes: bytes.byteLength, altText: String(form.get("altText") ?? "").slice(0, 250) },
  });
  const base = process.env.MEDIA_PUBLIC_URL?.replace(/\/$/, "");
  const url = base ? `${base}/${key}` : storagePath;
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "CREATED", entity: "media", entityId: media.id } });
  return NextResponse.json({ success: true, data: { ...media, url } }, { status: 201 });
}

export async function DELETE(request: Request) {
  const admin = await requireAdmin(AdminRole.SUPER_ADMIN);
  if (!admin) return NextResponse.json({ success: false, error: "Super administrator access required" }, { status: 403 });
  if (!isSameOrigin(request)) return NextResponse.json({ success: false, error: "Invalid request origin" }, { status: 403 });
  const { id } = await request.json().catch(() => ({})) as { id?: string };
  const media = id ? await db.media.findUnique({ where: { id } }) : null;
  if (!media) return NextResponse.json({ success: false, error: "Media item not found" }, { status: 404 });
  if (process.env.S3_BUCKET) {
    await s3Client().send(new DeleteObjectCommand({ Bucket: process.env.S3_BUCKET, Key: media.filename }));
  } else if (media.storagePath.startsWith("/uploads/")) {
    const directory = path.join(process.cwd(), "public", "uploads");
    const target = path.resolve(directory, path.basename(media.storagePath));
    if (!target.startsWith(`${directory}${path.sep}`)) return NextResponse.json({ success: false, error: "Invalid media path" }, { status: 400 });
    await unlink(target);
  }
  await db.media.delete({ where: { id } });
  await db.auditLog.create({ data: { adminId: admin.id, adminName: admin.name, action: "DELETED", entity: "media", entityId: id } });
  return NextResponse.json({ success: true, data: null });
}

import { redirect } from "next/navigation";
import { currentAdmin } from "@/lib/auth";
import { ContentManager } from "@/components/ContentManager";
import { CategoryManager, TagManager } from "@/components/TaxonomyManager";
import { AnalyticsPanel, AuditPanel, ImportExportPanel, MediaPanel, SettingsPanel, UsersPanel } from "@/components/WorkspacePanels";

export default async function AdminSectionPage({ params, searchParams }: { params: Promise<{ section: string }>; searchParams: Promise<{ new?: string }> }) {
  const [{ section }, search, admin] = await Promise.all([params, searchParams, currentAdmin()]);
  if (!admin) redirect("/login");
  const superOnly = ["users", "settings", "media", "audit", "analytics"].includes(section);
  if (superOnly && admin.role !== "SUPER_ADMIN") redirect("/admin");
  const autoOpen = search.new === "1";
  if (section === "commands") return <ContentManager type="commands" autoOpen={autoOpen} />;
  if (section === "termux") return <ContentManager type="commands" initialPlatform="TERMUX" autoOpen={autoOpen} />;
  if (section === "linux") return <ContentManager type="commands" initialPlatform="LINUX" autoOpen={autoOpen} />;
  if (section === "scripts") return <ContentManager type="scripts" autoOpen={autoOpen} />;
  if (section === "tools") return <ContentManager type="tools" autoOpen={autoOpen} />;
  if (section === "tutorials") return <ContentManager type="tutorials" autoOpen={autoOpen} />;
  if (section === "categories") return <CategoryManager />;
  if (section === "tags") return <TagManager />;
  if (section === "about") return <SettingsPanel aboutOnly />;
  if (section === "settings") return <SettingsPanel />;
  if (section === "users") return <UsersPanel selfId={admin.id} />;
  if (section === "audit") return <AuditPanel />;
  if (section === "analytics") return <AnalyticsPanel />;
  if (section === "media") return <MediaPanel />;
  if (section === "import-export") return <ImportExportPanel />;
  redirect("/admin");
}

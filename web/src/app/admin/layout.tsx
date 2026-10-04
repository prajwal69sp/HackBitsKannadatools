import { redirect } from "next/navigation";
import { currentAdmin } from "@/lib/auth";
import { AdminShell } from "@/components/AdminShell";

export const dynamic = "force-dynamic";

export default async function AdminLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  const admin = await currentAdmin();
  if (!admin) redirect("/login");
  return <AdminShell admin={admin}>{children}</AdminShell>;
}

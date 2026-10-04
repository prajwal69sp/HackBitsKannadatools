import { hash } from "bcryptjs";
import { PrismaClient } from "@prisma/client";

const db = new PrismaClient();

const categories = [
  ["Termux Basics", "Start here with the Termux shell.", "Termux"],
  ["Package Management", "Install and maintain packages.", "Termux"],
  ["Files", "Create, copy and manage files.", "Termux"],
  ["Directories", "Navigate and organize directories.", "Termux"],
  ["Permissions", "Understand file access modes.", "Linux"],
  ["Networking", "Learn common network tools.", "Networking"],
  ["Processes", "Inspect and manage local processes.", "Linux"],
  ["Storage", "Inspect storage and disk usage.", "Linux"],
  ["Git", "Track changes and manage source code.", "Development"],
  ["Python", "Python environment and programming.", "Development"],
  ["Node.js", "JavaScript development in the terminal.", "Development"],
  ["SSH", "Remote shell access to systems you administer.", "Networking"],
  ["Automation", "Small, safe local automation examples.", "Development"],
  ["Development", "Programming and development tools.", "Development"],
  ["File Management", "Organize and inspect local files.", "Linux"],
  ["Users", "Account and group information.", "Linux"],
  ["Services", "Local service management.", "Linux"],
  ["Shell", "Shell fundamentals and utilities.", "Linux"],
  ["Environment", "Shell environment and variables.", "Linux"],
  ["System Administration", "Local system administration.", "Linux"],
  ["Cybersecurity", "Authorized security learning.", "Security"],
  ["Networking Lab", "Safe networking tools for owned labs.", "Security"],
] as const;
const disclaimer = "Use security tools only on systems you own or have explicit permission to test. HackBitsKannada provides educational material for responsible security learning, authorized testing, personal laboratories and CTF environments.";

async function main() {
  const email = process.env.ADMIN_EMAIL?.trim().toLowerCase();
  const password = process.env.ADMIN_PASSWORD;
  if (!email || !password || password.length < 12) {
    throw new Error("Set ADMIN_EMAIL and an ADMIN_PASSWORD of at least 12 characters before seeding.");
  }
  const admin = await db.adminUser.findUnique({ where: { email } });
  if (!admin) await db.adminUser.create({ data: { name: "HackBitsKannada Admin", email, passwordHash: await hash(password, 12), role: "SUPER_ADMIN" } });

  for (let index = 0; index < categories.length; index += 1) {
    const [name, description, section] = categories[index];
    await db.category.upsert({
      where: { name },
      create: { name, description, section, sortOrder: index },
      update: { description, section, sortOrder: index },
    });
  }
  const commands = [
    ["pkg-update", "Update package lists", "pkg update", "Package Management", "Refresh package metadata before installing or upgrading packages.", "TERMUX", "pkg update", "TERMUX"],
    ["pkg-upgrade", "Upgrade Termux packages", "pkg upgrade", "Package Management", "Upgrade installed packages after reviewing the changes.", "TERMUX", "pkg upgrade", "TERMUX"],
    ["pkg-install", "Install a Termux package", "pkg install", "Package Management", "Install a package by its repository name.", "TERMUX", "pkg install python", "TERMUX"],
    ["termux-storage", "Set up shared storage", "termux-setup-storage", "Storage", "Request access to shared Android storage for Termux.", "TERMUX", "termux-setup-storage", "TERMUX"],
    ["pwd", "Print working directory", "pwd", "File Management", "Show the current working directory.", "GENERAL", "pwd", "LINUX"],
    ["ls", "List directory contents", "ls", "File Management", "List files in the current directory.", "GENERAL", "ls -la", "LINUX"],
    ["cd", "Change directory", "cd", "File Management", "Change the current shell directory.", "GENERAL", "cd ~/projects", "LINUX"],
    ["mkdir", "Create a directory", "mkdir", "File Management", "Create a new directory.", "GENERAL", "mkdir -p projects/demo", "LINUX"],
    ["cp", "Copy a file", "cp", "File Management", "Copy a file to a destination.", "GENERAL", "cp notes.txt notes-copy.txt", "LINUX"],
    ["mv", "Move or rename a file", "mv", "File Management", "Move or rename a path. Verify destination paths.", "GENERAL", "mv draft.txt final.txt", "LINUX"],
    ["grep", "Search text with grep", "grep", "Shell", "Find matching lines in files or piped input.", "GENERAL", "grep -n 'TODO' README.md", "LINUX"],
    ["find", "Find files by name", "find", "File Management", "Search a directory tree by path or name.", "GENERAL", "find . -name '*.py'", "LINUX"],
    ["chmod", "Change file permissions", "chmod", "Permissions", "Set file access permissions. Review the target before changing modes.", "GENERAL", "chmod 600 private.txt", "LINUX"],
    ["git-status", "Inspect Git changes", "git status", "Git", "Review the current branch and working tree changes.", "GENERAL", "git status", "GENERAL"],
    ["git-clone", "Clone a Git repository", "git clone", "Git", "Copy a repository you are permitted to access.", "GENERAL", "git clone https://example.com/owner/project.git", "GENERAL"],
    ["git-add", "Stage Git changes", "git add", "Git", "Stage selected changes for the next commit.", "GENERAL", "git add README.md", "GENERAL"],
    ["git-commit", "Commit staged changes", "git commit", "Git", "Create a commit from staged changes.", "GENERAL", "git commit -m 'Update docs'", "GENERAL"],
    ["git-pull", "Fetch and integrate changes", "git pull", "Git", "Fetch and integrate updates from the configured remote.", "GENERAL", "git pull", "GENERAL"],
    ["ping", "Check network reachability", "ping", "Networking", "Send ICMP echo requests to a host you are authorized to contact.", "GENERAL", "ping -c 4 example.com", "GENERAL"],
    ["curl", "Fetch a web resource", "curl", "Networking", "Transfer data to or from a URL. Review remote content before running it.", "GENERAL", "curl -I https://example.com", "GENERAL"],
    ["wget", "Download a file", "wget", "Networking", "Download a resource from a URL.", "GENERAL", "wget https://example.com/file.txt", "GENERAL"],
    ["ssh", "Connect to an authorized SSH server", "ssh", "SSH", "Open a secure shell connection to a system you administer.", "GENERAL", "ssh user@your-server", "GENERAL"],
  ] as const;
  for (const [id, title, command, category, description, platform, example, source] of commands) {
    await db.command.upsert({
      where: { id },
      create: { id, title, command, category, description, platform, example, syntax: command, difficulty: "BEGINNER", status: "PUBLISHED" },
      update: { title, command, category, description, platform, example, syntax: command, status: "PUBLISHED" },
    });
    if (source === "TERMUX") await db.command.update({ where: { id }, data: { platform: "TERMUX" } });
  }
  const scripts = [
    { id: "system-info", title: "Local system information", language: "Bash", category: "Shell", description: "Print basic information about the current local environment.", code: "#!/usr/bin/env bash\nset -eu\nprintf 'Kernel: '; uname -srm\nprintf 'User: %s\\n' \"$(id -un)\"\nprintf 'Directory: %s\\n' \"$PWD\"", explanation: "Uses read-only system information commands.", usage: "bash system-info.sh", exampleOutput: "Kernel: Linux …", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "" },
    { id: "file-backup", title: "Create a dated file backup", language: "Bash", category: "Files", description: "Copy a chosen file into a local backup directory.", code: "#!/usr/bin/env bash\nset -eu\nsource_file=${1:?Usage: bash backup.sh FILE}\n[ -f \"$source_file\" ] || { echo 'File not found' >&2; exit 1; }\nbackup_dir=\"${HOME}/backups\"\nmkdir -p \"$backup_dir\"\ncp -- \"$source_file\" \"$backup_dir/$(basename \"$source_file\").$(date +%Y%m%d%H%M%S).bak\"\necho \"Backup saved in $backup_dir\"", explanation: "Uses quoted paths and copies only the file explicitly supplied.", usage: "bash backup.sh notes.txt", exampleOutput: "Backup saved in /home/user/backups", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "Review paths and available storage before use." },
    { id: "python-system-info", title: "Python system information", language: "Python", category: "Python", description: "Display basic system information using the Python standard library.", code: "import platform\nimport os\nprint(f\"System: {platform.system()} {platform.release()}\")\nprint(f\"Python: {platform.python_version()}\")\nprint(f\"Working directory: {os.getcwd()}\")", explanation: "Uses standard-library queries and does not modify system state.", usage: "python system_info.py", exampleOutput: "System: Linux …", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "" },
    { id: "simple-file-organizer", title: "Preview a file organizer", language: "Python", category: "Files", description: "Print proposed file groups without moving or changing files.", code: "from pathlib import Path\nfor path in sorted(Path('.').iterdir()):\n    if path.is_file():\n        print(f\"{path.name}: {path.suffix.lower() or '[no extension]'}\")", explanation: "This safe example only previews filenames and does not modify files.", usage: "python organizer_preview.py", exampleOutput: "notes.txt: .txt", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "This preview script never moves or deletes files." },
  ];
  for (const script of scripts) await db.script.upsert({ where: { id: script.id }, create: script, update: script });
  const tools = [
    { id: "curl-tool", name: "curl", category: "Networking", platform: "GENERAL" as const, description: "Transfer URLs and inspect web responses for learning and development.", installationCommand: "pkg install curl (Termux)", basicUsage: "curl -I https://example.com", examples: "Inspect response headers for a public test website.", documentation: "https://curl.se/docs/", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "" },
    { id: "git-tool", name: "Git", category: "Development", platform: "GENERAL" as const, description: "Distributed version control for source code and documentation.", installationCommand: "pkg install git (Termux)", basicUsage: "git status", examples: "git clone <authorized repository URL>", documentation: "https://git-scm.com/docs", difficulty: "BEGINNER" as const, status: "PUBLISHED" as const, warning: "" },
    { id: "nmap-lab", name: "Nmap (authorized labs only)", category: "Cybersecurity", platform: "LINUX" as const, description: "A network discovery utility to study systems in an owned lab or explicitly authorized CTF.", installationCommand: "Install from your distribution's trusted package repository.", basicUsage: "nmap -sV <your-lab-host>", examples: "Scan only an isolated system you own or have written authorization to test.", documentation: "https://nmap.org/book/man.html", difficulty: "INTERMEDIATE" as const, status: "PUBLISHED" as const, warning: disclaimer },
  ];
  for (const tool of tools) await db.tool.upsert({ where: { id: tool.id }, create: tool, update: tool });
  const tutorial = await db.tutorial.upsert({
    where: { id: "termux-from-zero" },
    create: { id: "termux-from-zero", title: "Learn Termux From Zero", description: "A beginner-friendly introduction to terminal basics, packages, files, Python, Git and responsible networking.", category: "Termux Basics", difficulty: "BEGINNER", estimatedMinutes: 90, status: "PUBLISHED" },
    update: { title: "Learn Termux From Zero", status: "PUBLISHED" },
  });
  const lessons = [
    ["What is Termux?", "Termux provides a Linux-like terminal environment on Android. It does not root your device."],
    ["First commands", "Use pwd to inspect the current directory and ls to list files."],
    ["Package management", "Refresh package metadata with pkg update, then review package upgrades before confirming."],
    ["Files and directories", "Create a practice folder with mkdir and navigate into it using cd."],
    ["Bash basics", "Practice variables, quoting, conditions and scripts with files in a temporary lab directory."],
    ["Python", "Install Python from trusted Termux repositories and use it for local learning examples."],
    ["Git", "Use Git to track your own source code and clone repositories you are allowed to access."],
    ["Networking", "Practice connectivity checks only against your own devices or public services that permit them."],
  ];
  for (let index = 0; index < lessons.length; index += 1) {
    await db.lesson.upsert({
      where: { id: `termux-zero-${index + 1}` },
      create: { id: `termux-zero-${index + 1}`, tutorialId: tutorial.id, title: lessons[index][0], content: lessons[index][1], sortOrder: index },
      update: { title: lessons[index][0], content: lessons[index][1], sortOrder: index },
    });
  }
  await db.appConfig.upsert({
    where: { id: "main" },
    create: {
      id: "main",
      disclaimer,
      aboutSections: {
        "What We Teach": "Termux, Linux, programming, AI tools, Android customization, web and app development, and responsible cybersecurity.",
        Termux: "Learn terminal basics, package management, files and development workflows on Android.",
        Linux: "Build practical knowledge of Linux commands, permissions, processes and system tools.",
        Programming: "Explore beginner-friendly programming examples with Bash, Python and JavaScript.",
        "AI Tools": "Learn about AI tools and responsible ways to use them in technology workflows.",
        Android: "Explore Android customization and developer workflows using safe, supported approaches.",
        "Web Development": "Learn the building blocks of web pages and development workflows.",
        Cybersecurity: "Study defensive fundamentals in personal labs and explicitly authorized environments.",
        "Ethical Hacking": disclaimer,
      },
    },
    update: { disclaimer },
  });
  console.info(`Database seeded. Super-admin account: ${email}.`);
}

main().catch((error: unknown) => {
  console.error("Database seed failed:", error);
  process.exitCode = 1;
}).finally(async () => db.$disconnect());

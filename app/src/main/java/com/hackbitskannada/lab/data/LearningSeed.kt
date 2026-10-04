package com.hackbitskannada.lab.data

object LearningSeed {
    val scripts = listOf(
        ScriptEntity("hello-shell", "Hello from the shell", "Beginner", "Beginner", "Print a greeting and basic operating-system information.", """#!/usr/bin/env bash
echo "Welcome to HackBitsKannada"
echo "System Information"
uname -a""", "The shebang selects Bash. Each echo prints a line. uname -a prints available kernel and machine details.", "Save as hello.sh and run `bash hello.sh`.", "A greeting, a heading, and one line of kernel information.", "Read-only. Review output for device or kernel details before sharing."),
        ScriptEntity("safe-backup", "Back up one text file", "Backup", "Beginner", "Copy a chosen text file to a timestamped backup beside it.", """#!/usr/bin/env bash
set -eu
source_file="${'$'}{1:?Usage: bash backup.sh FILE}"
if [[ ! -f "${'$'}source_file" ]]; then
        printf 'Not a regular file: %s\\n' "${'$'}source_file" >&2
  exit 1
fi
backup_file="${'$'}{source_file}.$(date +%Y%m%d-%H%M%S).bak"
cp -- "${'$'}source_file" "${'$'}backup_file"
printf 'Backup created: %s\\n' "${'$'}backup_file""", "set -eu stops on errors and unset variables. The argument expansion prints usage if missing. -f checks for a regular file. date makes a unique suffix; cp copies the file.", "Save as backup.sh, then run `bash backup.sh notes.txt`.", "A confirmation naming the new .bak file.", "Creates one new backup and never removes the original. Use on files you own and check available storage."),
        ScriptEntity("disk-report", "Quick disk-space report", "System information", "Beginner", "Print readable filesystem capacity.", """#!/usr/bin/env bash
set -eu
df -h""", "set -eu stops on errors. df reports filesystem space and -h uses readable units.", "Save as disk-report.sh and run `bash disk-report.sh`.", "A table of filesystem size, used space, available space, and mount point.", "Read-only. Termux may show app-specific storage views."),
        ScriptEntity("file-count", "Count files in a folder", "File automation", "Beginner", "Count regular files directly inside a directory without changing them.", """#!/usr/bin/env bash
set -eu
target="${'$'}{1:-.}"
find "${'$'}target" -maxdepth 1 -type f -print | wc -l""", "The first argument selects a directory; . is the default. find lists direct regular files and wc -l counts the paths.", "Save as file-count.sh; run `bash file-count.sh [directory]`.", "One integer: the count of regular files at the top level.", "Read-only. Nested files are not counted; inspect the target path first."),
        ScriptEntity("local-web-check", "Check a local web page", "Networking utilities", "Beginner", "Request one page from a local development server and print its HTTP status.", """#!/usr/bin/env bash
set -eu
url="${'$'}{1:-http://127.0.0.1:8000/}"
curl --max-time 5 --silent --output /dev/null --write-out 'HTTP %{http_code}\\n' "${'$'}url""", "The optional URL defaults to loopback. Curl waits at most five seconds, discards the body, and prints the status code.", "Start your own local server, then run `bash local-web-check.sh` or pass its 127.0.0.1 URL.", "For a running local server, a line such as HTTP 200.", "Use only with your own local development service. One request; no host scanning."),
        ScriptEntity("pretty-json", "Format a JSON file", "JSON", "Beginner", "Load JSON from a file and print it with indentation.", """#!/usr/bin/env python3
import json
import sys

path = sys.argv[1] if len(sys.argv) > 1 else "sample.json"
with open(path, encoding="utf-8") as source:
    data = json.load(source)
print(json.dumps(data, indent=2, ensure_ascii=False))""", "sys.argv reads an optional path. json.load parses the document; json.dumps formats it with indentation.", "Save as pretty_json.py; run `python pretty_json.py sample.json`.", "The JSON value printed with two-space indentation.", "The file is read-only. Treat JSON from unknown sources as data, not executable code."),
        ScriptEntity("text-stats", "Summarize a text file", "Text processing", "Beginner", "Report line, word, and character counts for a UTF-8 file.", """#!/usr/bin/env python3
from pathlib import Path
import sys

path = Path(sys.argv[1] if len(sys.argv) > 1 else "notes.txt")
text = path.read_text(encoding="utf-8")
print(f"Lines: {len(text.splitlines())}")
print(f"Words: {len(text.split())}")
print(f"Characters: {len(text)}")""", "Path handles the filename. read_text reads UTF-8, splitlines counts lines, and split counts whitespace-separated words.", "Save as text_stats.py; run `python text_stats.py notes.txt`.", "Three labeled counts. Character count includes spaces and newline characters.", "Read-only. Use on files you own or intend to inspect."),
        ScriptEntity("env-summary", "Show a small system summary", "Developer utilities", "Beginner", "Print the current shell user, directory, and kernel name.", """#!/usr/bin/env bash
set -eu
printf 'User: %s\\n' "$(whoami)"
printf 'Directory: %s\\n' "${'$'}PWD"
printf 'Kernel: %s\\n' "$(uname -s)""", "whoami prints the account. PWD is the shell's current path. uname -s prints the kernel name.", "Save as env-summary.sh; run `bash env-summary.sh`.", "Three labeled lines identifying user, directory, and kernel.", "Read-only. Review output for usernames or paths before sharing.")
    )

    val tools = listOf(
        ToolEntity("git", "Git", "Developer tools", "A distributed version-control system for tracking project changes.", "Review history, isolate work on branches, and collaborate through a remote host.", "pkg install git", "git status", "git log --oneline -5", "Authentication fails or no remote is configured.", "Check `git remote -v`, network access, and your host's authentication setup. Never paste tokens into public logs.", "Review changes before pushing; repositories may contain secrets."),
        ToolEntity("curl", "curl", "Networking", "A command-line client for making requests to URLs and APIs.", "Learn HTTP methods and responses against services you control.", "pkg install curl", "curl --help", "curl --max-time 5 http://127.0.0.1:8000/", "DNS errors, certificate errors, or timeouts.", "Confirm the URL, server, clock, and certificate trust. Do not disable TLS checks to hide errors.", "Use only services you own or are authorized to contact."),
        ToolEntity("python", "Python", "Programming", "A readable language with a standard library suited to learning and automation.", "Write small scripts for text, JSON, and local development tasks.", "pkg install python", "python --version", "python -c 'print(\"Hello\")'", "The python command is missing or a module import fails.", "Confirm installation with `pkg show python`; on desktop Linux use a virtual environment for project dependencies.", "Inspect scripts before running them, especially code from untrusted sources."),
        ToolEntity("nmap", "Nmap", "Security learning", "A network inventory and diagnostic utility for administrators and security learners.", "Learn host and service discovery inside an isolated lab or on systems you administer.", "pkg install nmap", "nmap --version", "nmap -sV 127.0.0.1", "Permission errors, filtered ports, or no route to the host.", "Start with loopback or an intentionally vulnerable local lab. A filtered result does not prove a service is absent.", "Scan only your own devices, CTF targets, or systems with explicit written permission."),
        ToolEntity("openssh", "OpenSSH client", "Networking", "Encrypted remote login and file-transfer tools.", "Connect to a server you administer or use public-key authentication in your lab.", "pkg install openssh", "ssh -V", "ssh learner@lab.example", "Host key changed, connection refused, or key permission warnings.", "Verify changed host keys through a trusted channel; confirm server address and account.", "Use only authorized accounts and systems. Keep private keys secret."),
        ToolEntity("wireshark", "Wireshark concepts", "Networking", "A packet-analysis tool for inspecting protocols and troubleshooting.", "Learn DNS, TCP, and HTTP metadata with a capture created in your own lab.", "Install Wireshark from your Linux distribution; desktop capture support varies.", "Open your own capture and filter with `dns`.", "Capture traffic from your own test device in an isolated lab.", "No capture interface, permission denied, or encrypted payloads.", "Use an approved interface or a sample capture. TLS protects payload contents.", "Capture only traffic you own or have explicit permission to inspect."),
        ToolEntity("metasploit", "Metasploit Framework", "Security learning", "A security-testing framework used in controlled training labs and authorized assessments.", "Understand lab setup and defensive validation in an isolated environment.", "Installation differs by platform; follow official documentation for a dedicated lab VM.", "Read official documentation and begin with a purpose-built local training target.", "Keep the target and tester on an isolated private lab network.", "Large dependencies, unsupported device architecture, or database configuration issues.", "Use a supported desktop Linux virtual machine and official documentation.", "Training only: use an isolated lab, CTF, or explicit written authorization. No public-target testing."),
        ToolEntity("termux", "Termux", "Android", "An Android terminal and Linux-style user space that works without device root for ordinary use.", "Practice shell commands, supported packages, and scripts in app-private storage.", "Install Termux from its official source and keep add-ons from the same source.", "pkg update && pkg install python", "termux-info", "Repository signature mismatch, outdated app, or package mirror failure.", "Update from the official source and use matching add-on sources; do not mix unrelated builds.", "Packages run with Android app permissions. Understand the impact before granting access or root capabilities.")
    )

    val tutorials = listOf(
        lesson("termux-intro", "What is Termux?", "Beginner", "Termux", 1, "Termux provides a terminal and Linux-style user-space tools inside Android. It is not a full Linux boot and normally does not grant root access. Start in the app's private home directory, learn command syntax, and install software from maintained repositories.", "termux,android,basics"),
        lesson("packages", "Installing packages", "Beginner", "Termux", 2, "Refresh package metadata with `pkg update`, search with `pkg search python`, then install with `pkg install python`. `pkg upgrade` updates installed packages. Termux uses its own package environment; do not follow desktop `sudo apt` instructions blindly.", "termux,pkg,packages"),
        lesson("terminal", "Linux terminal basics", "Beginner", "Linux", 3, "A shell reads a command, separates arguments, and launches a program. Try `whoami`, `pwd`, `uname -a`, then `clear`. Quote paths with spaces. A pipe connects one program's output to another; inspect each command before chaining it.", "shell,linux,terminal"),
        lesson("files", "Files and directories", "Beginner", "Linux", 4, "Use `ls -la` to list, `mkdir practice` to create, `cd practice` to enter, and `touch notes.txt` to create a file. Use `pwd` to verify location. Practice `cp` and `mv` on sample files. Shell deletion has no recycle bin; inspect every path before `rm`.", "files,navigation,linux"),
        lesson("permissions", "Permissions", "Beginner", "Linux", 5, "Permissions describe read (r), write (w), and execute (x) for owner, group, and others. `chmod +x script.sh` adds execute permission. Numeric 755 means owner read/write/execute and others read/execute. Grant the least access needed; avoid 777.", "permissions,security,chmod"),
        lesson("bash", "Bash basics", "Beginner", "Bash", 6, "Bash scripts collect commands in a text file. Begin with `#!/usr/bin/env bash`, add a quoted echo, and run with `bash hello.sh`. Quote variable expansions so spaces remain one argument. `set -eu` stops on errors and unset variables.", "bash,shell,scripts"),
        lesson("python", "Python basics", "Beginner", "Python", 7, "Check `python --version`, then start the REPL or run `python hello.py`. Learn values, names, conditionals, loops, and functions. Use UTF-8 when reading text, validate file paths, and install third-party packages only when needed.", "python,programming"),
        lesson("shell-scripting", "Shell scripting", "Intermediate", "Bash", 8, "Build scripts from small, testable steps. Quote paths, validate arguments, and send diagnostics to standard error. Test with sample inputs and ShellCheck when available. Avoid eval and do not pipe unverified downloads directly to a shell.", "bash,automation,safety"),
        lesson("git", "Git workflow", "Intermediate", "Git", 9, "Use `git status` and `git diff` to inspect, `git add` to stage selected files, and `git commit` to create a local checkpoint. A branch isolates work. `git push` publishes commits; inspect staged files for credentials first.", "git,version-control"),
        lesson("ssh", "SSH fundamentals", "Intermediate", "Networking", 10, "SSH creates an encrypted session to a server where you have an account. Create a key with `ssh-keygen -t ed25519`; keep the private key private and share only its .pub file. Verify host keys through a trusted channel.", "ssh,network,security"),
        lesson("networking", "Networking fundamentals", "Intermediate", "Networking", 11, "An IP address identifies an interface, DNS maps names to addresses, and TCP provides ordered connections. Explore a domain you control with `dig`, then make one request with `curl`. Never probe systems without explicit permission.", "network,dns,tcp"),
        lesson("automation", "Automation", "Intermediate", "Bash", 12, "Good automation is repeatable, observable, and safe to retry. Start with read-only reports, write output to a chosen path, check exit codes, and keep backups before edits. Test both expected output and failure behavior.", "automation,bash,safety"),
        lesson("python-scripts", "Python scripting", "Intermediate", "Python", 13, "Use pathlib for paths, context managers for files, and json for JSON data. Handle expected errors narrowly and explain what failed. Avoid shelling out when a standard-library function can do the task directly.", "python,files,json"),
        lesson("linux-admin", "Linux administration", "Advanced", "Linux", 14, "Administration manages accounts, services, storage, and updates with a change plan. Inspect first, understand scope, make a backup, and know how to roll back. Use sudo only for the privileged action needed on a machine you administer.", "linux,admin,services"),
        lesson("security", "Cybersecurity fundamentals", "Advanced", "Security", 15, "Security starts with assets, threats, least privilege, patching, backups, and monitoring. Define the system and permission boundary before testing. A tool's availability does not grant authorization. Practice in your isolated lab, a CTF, or with written permission.", "cybersecurity,defense,authorization"),
        lesson("web-security", "Web security concepts", "Advanced", "Security", 16, "Input validation, output encoding, authentication, authorization, and secure cookies reduce common web risks. Follow a deliberately vulnerable local training app and observe fixes in code review. Do not test production websites without explicit authorization and scope.", "web,security,secure-coding"),
        lesson("ctf", "CTF methodology", "Advanced", "Security", 17, "Read challenge rules first. Keep notes, identify evidence for each hypothesis, and use only the challenge environment. Learn the concept instead of copying commands. CTF access does not authorize testing unrelated systems.", "ctf,security,methodology"),
        lesson("security-tools", "Security tooling", "Advanced", "Security", 18, "Nmap, Wireshark, and Metasploit can affect systems and reveal sensitive traffic. Begin with documentation and offline examples, then use an isolated lab. Record scope and get explicit written approval before any assessment beyond your lab.", "nmap,wireshark,metasploit,lab"),
        lesson("secure-coding", "Secure coding", "Advanced", "Development", 19, "Validate untrusted input, use parameterized database queries, keep dependencies updated, and avoid embedding secrets in source. Test invalid input and permission boundaries. Ensure logs and errors do not disclose credentials or private data.", "secure-coding,development,defense")
    )

    private fun lesson(id: String, title: String, level: String, section: String, order: Int, body: String, tags: String) =
        TutorialEntity(id, title, level, section, order, body, tags)
}

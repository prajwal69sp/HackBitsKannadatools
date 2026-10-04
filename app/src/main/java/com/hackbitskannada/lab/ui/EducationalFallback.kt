package com.hackbitskannada.lab.ui

object EducationalFallback {
    fun explain(prompt: String): String {
        val normalized = prompt.trim().lowercase()
        return when {
            "chmod" in normalized && ("755" in normalized || "permission" in normalized) ->
                "`chmod 755 script.sh` sets owner permissions to read, write, and execute (7 = 4+2+1), and group/other permissions to read and execute (5 = 4+1). It affects only the named file. Use the narrower `chmod +x script.sh` when you only need to add execute permission. Avoid 777 because it grants everyone write access. This is a local reference explanation, not an AI service."
            "chmod" in normalized ->
                "`chmod` changes a file's read, write, and execute permission bits. For example, `chmod +x script.sh` adds execute permission. Check current permissions with `ls -l script.sh` and grant only the access needed. This is a local reference explanation, not an AI service."
            "pkg" in normalized || "apt" in normalized ->
                "`pkg` is Termux's package-manager interface. `pkg update` refreshes package metadata; `pkg install NAME` installs a package. Desktop Debian/Ubuntu typically use `apt`. Termux is not a rooted desktop Linux system. This is a local reference explanation, not an AI service."
            "grep" in normalized ->
                "`grep` searches text for lines matching a pattern. For example, `grep -n 'TODO' README.md` prints matching lines and their line numbers. Quote patterns that contain spaces or shell metacharacters. This is a local reference explanation, not an AI service."
            "ssh" in normalized ->
                "SSH opens an encrypted session to a remote host where you have an account. Verify the host key through a trusted channel, keep private keys private, and connect only to systems you own or are authorized to use. This is a local reference explanation, not an AI service."
            normalized.isBlank() ->
                "Enter a command or concept, for example: Explain chmod 755."
            else ->
                "This offline helper has short explanations for chmod, package management, grep, and SSH. Try one of those, or search the Commands and Tutorials library. No AI service is connected; this response was selected from local educational guidance."
        }
    }
}

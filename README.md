# HackBitsKannada — Termux & Linux Lab

An offline-first Android learning reference for Termux, Linux, Bash, Python, networking, developer tools, and responsible cybersecurity practice.

**Learn. Build. Explore. Secure.**

## Features

- Searchable offline command reference with Termux and Linux categories, examples, difficulty, usage notes, and safety warnings.
- Bash and Python script library with source, explanations, run instructions, sample output, and safety notes.
- Tool installation guides, troubleshooting notes, and an authorization reminder for security tooling.
- 19 structured lessons across beginner, intermediate, and advanced learning paths.
- Room-backed saved items, recent items, lesson completion, and appearance preferences.
- Local partial search across commands, scripts, tools, tutorials, categories, and tags.
- Copyable code blocks with a confirmation message. The app never executes copied commands.
- A local command explanation fallback. No AI API is configured or represented as connected.
- Dark, light, and system themes; adjustable text size; Android 8.0 (API 26) and newer.

## Screenshots

Screenshots can be added here after capturing the app on a device or emulator.

| Home | Command detail | Learning paths |
| --- | --- | --- |
| _Screenshot placeholder_ | _Screenshot placeholder_ | _Screenshot placeholder_ |

## Tech stack

Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, Coroutines, ViewModel, Gradle, and Android Gradle Plugin.

## Project structure

```text
.
├── .devcontainer/       # Codespaces Java and Android SDK setup
├── .github/workflows/   # Android CI and signed GitHub releases
├── app/src/main/java/   # Compose UI, ViewModel, Room data and seed content
├── app/src/test/        # Offline search and assistant unit tests
├── app/build.gradle.kts
├── gradle/              # Gradle wrapper configuration
└── README.md
```

## Local development

Install JDK 17 and Android SDK Platform 36 with Build Tools 36.0.0. Set `ANDROID_HOME` (or `ANDROID_SDK_ROOT`) to the SDK directory, accept Android SDK licenses, and make the wrapper executable:

```bash
chmod +x gradlew
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

The wrapper pins Gradle 8.13, compatible with the configured Android Gradle Plugin. Android Studio can open the repository root directly.

## GitHub Codespaces

Open the repository in a Codespace. The dev container installs JDK 17 and downloads Android command-line tools, platform-tools, API 36, and Build Tools 36.0.0 after creation. Once setup completes:

```bash
./gradlew testDebugUnitTest assembleDebug
```

The workflow at `.github/workflows/android.yml` runs the unit tests and debug build for pushes, pull requests, and manual dispatch. It uploads `app-debug.apk` as the `hackbitskannada-debug-apk` artifact.

## Publish a signed release

Push a semantic-version tag such as `v1.2.3` to build and publish a GitHub release containing a signed APK, an Android App Bundle (AAB), and their SHA-256 checksums:

```bash
git tag v1.2.3
git push origin v1.2.3
```

Before publishing the first release, add these repository Actions secrets:

- `ANDROID_KEYSTORE_BASE64`: the release keystore encoded as a single-line Base64 value.
- `ANDROID_STORE_PASSWORD`: the keystore password.
- `ANDROID_KEY_ALIAS`: the signing key alias.
- `ANDROID_KEY_PASSWORD`: the signing key password.

Keep the keystore and passwords backed up securely; releases should continue using the same signing key. For example, create a keystore with `keytool -genkeypair -v -keystore release.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000`, then encode it for the Base64 secret with `base64 -w 0 release.jks`. Never commit the keystore or signing credentials.

The release workflow runs unit tests before building. It uses the tag (without the `v`) as the Android version name and the GitHub Actions run number as the monotonically increasing version code. Release builds are signed in CI; local `assembleRelease` builds without the signing environment variables are unsigned and are not suitable for distribution. To build local release variants, run `./gradlew assembleRelease bundleRelease`; the outputs are `app/build/outputs/apk/release/app-release-unsigned.apk` and `app/build/outputs/bundle/release/app-release.aab`.

## Build an APK

```bash
chmod +x gradlew
./gradlew assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Ethical-use disclaimer

Cybersecurity material is for legal education, authorized testing, CTFs, and personal laboratories. Use security tools only on systems you own or have explicit permission to test. Do not use this material for unauthorized access, credential theft, malware, persistence, ransomware, or real-world attack automation. Examples are educational and must be reviewed before use. The application copies text only; it does not execute commands.

## Contributing

Contributions should keep examples accurate, safe, and useful offline. Include a clear explanation and focused tests for behavior changes. Do not add destructive scripts, real credentials, invented contact links, or instructions that exceed an authorized lab scope. Run `./gradlew testDebugUnitTest assembleDebug` before opening a pull request.

## License

No license has been selected yet. Until the project owner adds one, all rights are reserved; dependency licenses remain with their respective authors.
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

## Android tech stack

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
├── prisma/              # Admin PostgreSQL schema, migration and safe seed data
├── web/src/app/         # Next.js admin panel, health check and Android sync API
├── web/src/components/  # Responsive admin dashboard and content management
├── admin/               # Android WebView admin companion APK
├── tests/               # Web validation and authenticated API integration tests
├── package.json         # Web panel scripts and dependencies
└── README.md
```

## HackBitsKannada Admin

The repository also contains a functional, separate web content-management dashboard for the Android learning library. It uses Next.js 16, React, TypeScript, Tailwind CSS, PostgreSQL, Prisma and server-side administrator authentication.

### Admin features

- Dark, responsive terminal-inspired dashboard with content metrics, recent updates and quick actions.
- Email/password sign-in, bcrypt password hashes, opaque random sessions stored by hash, HTTP-only/SameSite cookies, server-side role checks, origin validation and persistent login throttling.
- Create, edit, search, filter, sort, publish, archive, duplicate and bulk-delete commands, scripts, tools and lessons/tutorials; syntax-highlighted Bash, Shell, Python and JavaScript code editors preserve source as plain text.
- Dynamic categories and tags, status and featured content, JSON/CSV validation and additive imports, exports, audit history and privacy-conscious view analytics.
- Super-admin account and app configuration management; content editors can manage content, categories, tags and the About copy.
- Image uploads accept JPEG, PNG, WebP and AVIF after checking file signatures and size limits. Development defaults to local files; configure an S3-compatible bucket for production.
- Public, read-only `/api/v1` content endpoints expose only published app content. Android can pull updates and cache them in Room for offline access.

### Web architecture and API

Next.js App Router server pages and route handlers share a modular Prisma data layer. Administrators use `/api/admin/*`; these endpoints require a live database session and role checks. App clients use the separate public `/api/v1/*` API and do not receive admin endpoints or draft content.

Public sync endpoints:

| Endpoint | Purpose |
| --- | --- |
| `GET /api/v1/commands` | Published commands |
| `GET /api/v1/scripts` | Published scripts |
| `GET /api/v1/tools` | Published tools |
| `GET /api/v1/tutorials` | Published tutorials and ordered lessons |
| `GET /api/v1/categories` | Enabled categories |
| `GET /api/v1/featured` | Featured published content, grouped by type |
| `GET /api/v1/config` | Public app configuration |
| `POST /api/v1/events` | Optional aggregate content-view events; no user IDs are stored |

List endpoints accept `page`, `pageSize` (up to 100) and an optional ISO-8601 `since` update cursor. Responses use `{ "success": true, "data": ..., "pagination": ... }`. When an HTTPS `CONTENT_API_BASE_URL` is supplied during the Android build, the app fetches published pages at startup and upserts them into Room. Existing locally seeded content remains available if no API URL is configured or sync fails; offline reading remains supported.

### Database models

Prisma models cover `AdminUser`, `Session`, `LoginAttempt`, `Command`, `Script`, `Tool`, `Tutorial`, ordered `Lesson` records, `Category`, `Tag` and their tag relationships, `Media`, `AppConfig`, `AuditLog` and `AnalyticsEvent`. PostgreSQL is the production database. Searchable content fields and frequent status, platform and timestamp queries are indexed.

### Admin roles

- **SUPER_ADMIN**: full access, including app configuration, media, analytics, audit logs and administrator accounts.
- **EDITOR**: command, script, tool, tutorial, category, tag and About-content management. Editors cannot access admin-user management, media, analytics, audit logs or app/security settings.

### Local web development

Install Node.js 22 or newer and use a PostgreSQL database. The Android Gradle project remains at the repository root alongside the web application.

1. Install project dependencies:

   ```bash
   npm install
   ```

2. Copy `.env.example` to `web/.env` and set `DATABASE_URL`, a unique `ADMIN_SESSION_SECRET` (at least 32 characters), `ADMIN_EMAIL`, and a strong `ADMIN_PASSWORD` (at least 12 characters). Do not commit `.env`.
3. Create the PostgreSQL schema and generate Prisma Client:

   ```bash
   npm run db:generate
   npm run db:migrate -- --name init
   ```

   To apply already-created migrations in a deployed environment, use `npm run db:deploy`; use `npm run db:status` to inspect migration state.

4. Seed the first super administrator plus safe, published educational examples:

   ```bash
   npm run seed
   ```

   The seed command requires `ADMIN_EMAIL` and `ADMIN_PASSWORD`; it hashes the password and does not reset an existing administrator's password on subsequent runs.
5. Start the dashboard:

   ```bash
   npm run dev
   ```

Open `http://localhost:3000/login`. The seeded credentials are the values configured in your local environment.

### Codespaces

The existing dev container installs Node.js 22 and Java 17 while retaining the Android SDK setup. Open the repository in a Codespace, then configure a PostgreSQL `DATABASE_URL`, `ADMIN_SESSION_SECRET`, `ADMIN_EMAIL` and `ADMIN_PASSWORD` in the Codespace environment or `web/.env` (copied from `.env.example`). Run the migration and seed commands above, then `npm run dev`. The web app and Android Gradle project can be worked on in the same Codespace.

### Development commands and tests

```bash
npm run lint
npm run typecheck
npm test
npm run build
```

`npm test` runs schema-validation unit tests and skips the HTTP integration test unless `API_BASE_URL`, `ADMIN_EMAIL` and `ADMIN_PASSWORD` are configured. For authenticated database/API CRUD, import/export, role and route tests, start a built server and set those values:

```bash
npm run build
API_BASE_URL=http://127.0.0.1:3000 npm run test:integration
```

The `Admin panel CI` workflow provisions PostgreSQL, installs from the lockfile, migrates and seeds a temporary CI administrator, then runs lint, type checking, unit/API integration tests and a production build. CI-only credentials are not source-code credentials and must never be reused in deployments.

### Deployment

#### Render production deployment

The backend/API is the Next.js 16 App Router server. The Render Blueprint in [`render.yaml`](./render.yaml) provisions a Node web service and a persistent, paid PostgreSQL database; review the selected plans and expected costs before applying it. Render's free databases are temporary and are not suitable for permanent production data.

1. Push the repository to GitHub and create a Render Blueprint from that repository. The web service installs the lockfile, generates Prisma Client, builds Next.js, binds to `0.0.0.0:$PORT`, and exposes `/api/health` as its health check.
2. Set `ADMIN_EMAIL` to the initial administrator email and set/generate a unique `ADMIN_SESSION_SECRET`. Render generates `ADMIN_PASSWORD`; retrieve it securely from the Render dashboard. After the first deployment has migrated the database, open a Render shell and run `npm run seed` once to create the initial super-admin and safe sample content. The seed hashes the password and does not reset an existing user's password on subsequent runs. Change the initial password after signing in. Later deploys run `npm run db:deploy` only and will not reapply seed data over admin edits.
3. Add the exact HTTPS origin(s) of any separately hosted browser app to `CORS_ALLOWED_ORIGINS`, as a comma-separated list. Do not use `*`, paths, local URLs, or GitHub Codespaces/`*.github.dev` URLs. The admin UI and its API share one Render origin by default and do not need cross-origin access. Android native HTTP clients do not need browser CORS; browser/WebView clients are restricted to the configured origins.
4. Configure production tutorial images using a durable S3-compatible object store: `S3_BUCKET`, `S3_REGION`, `S3_ENDPOINT` if needed, server-side `S3_ACCESS_KEY_ID`, `S3_SECRET_ACCESS_KEY`, and `MEDIA_PUBLIC_URL`. Render's local filesystem is ephemeral; uploads return `503` in production if object storage has not been configured. Never make these credentials `NEXT_PUBLIC_*` values.
5. After deployment, use the Render HTTPS service URL, for example `https://hackbitskannada-admin.onrender.com/login`. The Android admin companion accepts that HTTPS dashboard URL on first launch. Build the learning app against the production content API with `CONTENT_API_BASE_URL=https://hackbitskannada-admin.onrender.com` (or `-PcontentApiBaseUrl=...`) and `./gradlew :app:assembleRelease`; HTTP content API URLs are rejected in release builds.
6. Confirm `GET https://<service>/api/health` returns `{"status":"ok"}`, sign in at `/login`, and check published content through `/api/v1/commands`. Back up the production PostgreSQL database regularly.

For a local production-start smoke test, `npm start` reads `process.env.PORT` (default `3000`) and explicitly binds to `0.0.0.0`. Set `DATABASE_URL`, `ADMIN_SESSION_SECRET`, and other credentials in the deployment platform's secret environment configuration. Never embed database passwords, admin passwords, API keys, or session secrets in source code or Android APKs.

### Security and responsible use

Authentication secrets and database credentials are environment-only. Passwords use bcrypt; browser session cookies are HTTP-only; only a hash of each random session token is stored in PostgreSQL. Administrative writes require same-origin requests and server-side authorization. Prisma parameterizes database operations, content is rendered as escaped text, uploads are restricted to supported image signatures and size limits, and administrative changes are audited without credentials. Editors cannot elevate their own role. Security-tool content is for legal, authorized labs and CTFs only.

Use security tools only on systems you own or have explicit permission to test. HackBitsKannada provides educational material for responsible security learning, authorized testing, personal laboratories and CTF environments.

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

Build both Android applications (the offline learning app and the WebView-based admin companion) and run their unit tests:

```bash
chmod +x gradlew
./gradlew testDebugUnitTest :app:assembleDebug :admin:assembleDebug
```

Outputs:

- Learning app: `app/build/outputs/apk/debug/app-debug.apk`
- Admin companion: `admin/build/outputs/apk/debug/admin-debug.apk`

The admin APK opens the deployed web dashboard in a secured WebView. On first launch, enter the HTTPS URL of your deployed admin panel; the URL is saved on the device. For a preconfigured admin APK, build with `ADMIN_DASHBOARD_URL=https://your-hosted-dashboard` in the environment or `-PadminDashboardUrl=https://your-hosted-dashboard` as a Gradle property. HTTPS is required outside local development. No admin server or database is embedded in the APK; deploy the web dashboard and configure its PostgreSQL connection before connecting the companion.

## Ethical-use disclaimer

Cybersecurity material is for legal education, authorized testing, CTFs, and personal laboratories. Use security tools only on systems you own or have explicit permission to test. Do not use this material for unauthorized access, credential theft, malware, persistence, ransomware, or real-world attack automation. Examples are educational and must be reviewed before use. The application copies text only; it does not execute commands.

## Contributing

Contributions should keep examples accurate, safe, and useful offline. Include a clear explanation and focused tests for behavior changes. Do not add destructive scripts, real credentials, invented contact links, or instructions that exceed an authorized lab scope. Run `./gradlew testDebugUnitTest assembleDebug` before opening a pull request.

## License

No license has been selected yet. Until the project owner adds one, all rights are reserved; dependency licenses remain with their respective authors.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val configuredDashboardUrl = providers.gradleProperty("adminDashboardUrl")
    .orElse(providers.environmentVariable("ADMIN_DASHBOARD_URL"))
    .orElse("")
    .get()
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

android {
    namespace = "com.hackbitskannada.admin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hackbitskannada.admin"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("String", "DEFAULT_DASHBOARD_URL", "\"$configuredDashboardUrl\"")
    }

    dependencies {
        testImplementation("junit:junit:4.13.2")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "uz.itkoders.alfaplay"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        val supabaseUrl = (project.findProperty("SUPABASE_URL") as String?) ?: ""
        val supabaseAnonKey = (project.findProperty("SUPABASE_ANON_KEY") as String?) ?: ""
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    signingConfigs {\n        create("release") {\n            val keyPath = System.getenv("ALFAPLAY_KEYSTORE_FILE")\n            if (!keyPath.isNullOrBlank()) {\n                storeFile = file(keyPath)\n                storePassword = System.getenv("ALFAPLAY_STORE_PASSWORD")\n                keyAlias = System.getenv("ALFAPLAY_KEY_ALIAS")\n                keyPassword = System.getenv("ALFAPLAY_KEY_PASSWORD")\n            }\n        }\n    }\n\n    buildTypes {\n        getByName("release") {\n            signingConfig = signingConfigs.getByName("release")\n            isMinifyEnabled = false\n        }\n    }\n\n    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

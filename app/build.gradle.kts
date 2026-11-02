plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.example"
    compileSdk = 35
    defaultConfig {
        applicationId = "uz.itkoders.alfaplay"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {\n        compose = true\n        buildConfig = true\n    }\n    val supabaseUrl = (project.findProperty("SUPABASE_URL") as String?) ?: ""\n    val supabaseAnonKey = (project.findProperty("SUPABASE_ANON_KEY") as String?) ?: ""\n    defaultConfig {\n        buildConfigField("String", "SUPABASE_URL", "\\\"$supabaseUrl\\\"")\n        buildConfigField("String", "SUPABASE_ANON_KEY", "\\\"$supabaseAnonKey\\\"")\n    }
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
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")\n    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

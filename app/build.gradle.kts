plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "ir.technologist.mokhtasat"
    compileSdk = 35
    defaultConfig {
        applicationId = "ir.technologist.mokhtasat"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "0.5.0"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-viewbinding")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("neshan-android-sdk:mobile-sdk:1.0.1")
    implementation("neshan-android-sdk:services-sdk:1.0.0")
    implementation("neshan-android-sdk:common-sdk:0.0.2")
    implementation("com.google.android.gms:play-services-location:17.0.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

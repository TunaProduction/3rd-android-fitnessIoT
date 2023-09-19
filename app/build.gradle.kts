plugins {
    id("com.android.application")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    /*id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")*/
}

android {
    namespace = "com.eTime.a3rd_android_fitnesslot"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.eTime.a3rd_android_fitnesslot"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
}

dependencies {

    implementation(Hilt.hiltAndroid)
    kapt(Hilt.hiltCompiler)

    implementation(Compose.navigation)
    implementation(Hilt.hiltNavigation)

    implementation (KotlinDependencies.core)
    implementation(KotlinDependencies.lifecycle)
    implementation(Compose.composeActivity)
    implementation(platform(Compose.composeBom))
    implementation(Compose.compose)
    implementation(Compose.composeUiGraphics)
    implementation(Compose.preview)
    implementation(Material.material)
    testImplementation(Testing.jUnit)
    androidTestImplementation(Testing.extJUnit)
    androidTestImplementation(Testing.espresso)
    androidTestImplementation(platform(Compose.composeBom))
    androidTestImplementation(Testing.composeJUnit)
    debugImplementation(Debug.composeToolingUI)
    debugImplementation(Debug.composeUIManifestTest)

    //POLAR TOOLS
    implementation(Polar.sdk)
    implementation(RxJava.java)
    implementation(RxJava.android)
    //implementation("androidx.core:core-ktx:1.9.0")
    //implementation("androidx.activity:activity-compose:1.7.0")
    //implementation("androidx.compose.ui:ui")
    //implementation("androidx.compose.ui:ui-graphics")
    //implementation("androidx.compose.ui:ui-tooling-preview")
    //implementation("androidx.compose.material3:material3")
    //testImplementation("junit:junit:4.13.2")
    //androidTestImplementation("androidx.test.ext:junit:1.1.5")
    //androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    //androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    //debugImplementation("androidx.compose.ui:ui-tooling")

    //MY MODULES
    implementation(project(Modules.core))
    implementation(project(Modules.coreUi))
    implementation(project(Modules.featureLoginPresentation))
    implementation(project(Modules.featureTrainingPresentation))

}
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

apply(from = "$rootDir/compose-module.gradle")

android {
    namespace = "com.etime.training_presentation"
}

dependencies {

    implementation(project(Modules.coreUi))
    implementation(project(Modules.core))
    //POLAR TOOLS
    implementation(Polar.sdk)
    implementation(RxJava.java)
    implementation(RxJava.android)
}
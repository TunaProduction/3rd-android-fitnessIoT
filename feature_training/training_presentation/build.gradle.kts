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

    implementation(Coroutines.coroutines)
    implementation(Coroutines.android)
    implementation(Coroutines.reactive)

    implementation(Gson.gson)

    //POLAR TOOLS
    implementation(Polar.sdk)
    implementation(Charts.material3)
    implementation(Charts.views)
    implementation(RxJava.java)
    implementation(RxJava.android)

    implementation(Retrofit.okHttp)
    implementation(Retrofit.retrofit)
    implementation(Retrofit.okHttpLoggingInterceptor)
    implementation(Retrofit.moshiConverter)


}
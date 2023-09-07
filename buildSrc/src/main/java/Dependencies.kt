import Versions.androidBuildToolsVersion
import Versions.androidxCompose
import Versions.androidxCoreVersion
import Versions.androidxLifecycle
import Versions.appCompatVersion
import Versions.composeBomVersion
import Versions.composeCompilerVersion
import Versions.coroutinesVersion
import Versions.espressoVersion
import Versions.extJUnitVersion
import Versions.hiltAndroidGradlePluginVersion
import Versions.hiltLifeCycleViewModelVersion
import Versions.hiltNavigationComposeVersion
import Versions.hiltVersion
import Versions.jUnitVersion
import Versions.kotlinVersion
import Versions.materialVersion
import Versions.navigationVersion

object Versions{
    const val androidxCompose = "1.1.0-rc01"
    const val composeCompilerVersion = "1.4.6"
    //const val androidxCompose = "1.1.0-beta01"
    const val androidxLifecycle = "2.6.1"
    const val composeActivity = "1.7.0"
    const val jUnitVersion = "4.13.2"
    const val extJUnitVersion = "1.1.5"
    const val espressoVersion = "3.5.1"
    const val androidxCoreVersion = "1.9.0"
    const val hiltVersion = "2.45"
    const val hiltNavigationComposeVersion = "1.0.0-beta01"
    const val hiltLifeCycleViewModelVersion = "1.0.0-alpha03"
    const val composeCoilVersion = "2.1.0"
    const val materialVersion = "1.0.0-alpha14"
    //const val kotlinVersion = "1.5.31"
    const val kotlinVersion = "1.8.10"
    const val androidBuildToolsVersion = "7.0.4"
    const val coroutinesVersion = "1.6.0"
    const val appCompatVersion = "1.6.1"
    const val composeBomVersion = "2023.03.00"
    const val navigationVersion = "2.5.3"
    const val hiltAndroidGradlePluginVersion = "2.38.1"
    const val polarSdkVersion = "5.1.0"
    const val rxJavaVersion = "3.1.6"
    const val rxJavaAndroid = "3.0.2"
}

object Compose{
    const val compose = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"
    const val preview = "androidx.compose.ui:ui-tooling-preview"
    const val runtime = "androidx.compose.runtime:runtime:$androidxCompose"
    const val compiler = "androidx.compose.compiler:compiler:$composeCompilerVersion"
    const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
    const val composeBom = "androidx.compose:compose-bom:$composeBomVersion"

}

object Hilt{
    const val hiltAndroid = "com.google.dagger:hilt-android:$hiltVersion"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$hiltVersion"
    const val hiltNavigation = "androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion"
}

object KotlinDependencies{
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:$androidxLifecycle"
    const val core = "androidx.core:core-ktx:$androidxCoreVersion"
    const val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"

}

object Testing{
    const val jUnit = "junit:junit:$jUnitVersion"
    const val extJUnit = "androidx.test.ext:junit:$extJUnitVersion"
    const val espresso = "androidx.test.espresso:espresso-core:$espressoVersion"
    const val composeJUnit = "androidx.compose.ui:ui-test-junit4"

}

object Debug{
    const val composeToolingUI = "androidx.compose.ui:ui-tooling"
    const val composeUIManifestTest = "androidx.compose.ui:ui-test-manifest"
}

object Coil{
    const val coil = "io.coil-kt:coil-compose:${Versions.composeCoilVersion}"
}

object Material{
    const val material = "androidx.compose.material3:material3"
}

object Coroutines {
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
}

object Polar {
    const val sdk = "com.github.polarofficial:polar-ble-sdk:${Versions.polarSdkVersion}"
}

object RxJava{
    const val java = "io.reactivex.rxjava3:rxjava:${Versions.rxJavaVersion}"
    const val android = "io.reactivex.rxjava3:rxandroid:${Versions.rxJavaAndroid}"
}

object Build {
    const val androidBuildTools = "com.android.tools.build:gradle:$androidBuildToolsVersion"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val hiltAndroidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltAndroidGradlePluginVersion"
}

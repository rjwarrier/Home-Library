plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
}

val homeLibraryBuildRoot = providers.gradleProperty("homeLibraryBuildRoot").orElse(".out-redesign").get()
layout.buildDirectory.set(file("$homeLibraryBuildRoot/root-build"))

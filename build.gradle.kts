plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
}

// Must stay on the same drive as the project: KSP/Room relativize generated-file paths
// against the project root and throw ("different roots") if build output lands on a
// different drive letter. If the project sits inside a synced folder (Dropbox, OneDrive,
// etc.) and its sync client is holding file handles on build outputs, pass
// -PhomeLibraryBuildRoot=<same-drive path outside the synced tree> to work around it —
// don't default there, since a different-drive default breaks Windows builds outright.
val homeLibraryBuildRoot = providers.gradleProperty("homeLibraryBuildRoot").orElse(".out-redesign").get()
layout.buildDirectory.set(file("$homeLibraryBuildRoot/root-build"))

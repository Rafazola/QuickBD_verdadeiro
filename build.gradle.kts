// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // KSP 2.3.x: primeira linha com suporte ao AGP 9 (a 2.0.21-1.0.27 NÃO funciona com AGP 9)
    id("com.google.devtools.ksp") version "2.3.4" apply false
}

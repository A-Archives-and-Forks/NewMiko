buildscript {
    dependencies {}
}
plugins {
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildFile)
}
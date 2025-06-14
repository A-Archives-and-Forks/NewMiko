plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlinpoet.ksp)
}





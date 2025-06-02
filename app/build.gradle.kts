plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "im.mingxi.miko"
    compileSdk = 35
    buildToolsVersion = "35"

    defaultConfig {
        applicationId = "im.mingxi.miko"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true

    }
    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x78"
        )
    }

}

dependencies {
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(project(":core"))
    api(libs.appcompat)
}

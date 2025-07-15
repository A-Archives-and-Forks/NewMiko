import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "im.mingxi.miko"
    compileSdk = 35
    buildToolsVersion = "35"

    defaultConfig {
        applicationId = "im.mingxi.miko"
        minSdk = 26
        targetSdk = 35
        versionCode = 17
        versionName = "1.0.1"

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

    signingConfigs {
        getByName("debug") {
            keyAlias = "NewMiko2025"
            keyPassword = "NewMiko2025"
            storePassword = "NewMiko2025"
            storeFile = file("NewMiko2025.jks")
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = false
        }
        create("release") {
            keyAlias = "NewMiko2025"
            keyPassword = "NewMiko2025"
            storePassword = "NewMiko2025"
            storeFile = file("NewMiko2025.jks")
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = false
        }
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
        buildConfig = true
    }
    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x78"
        )
    }

    android.applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl)
                outputFileName = "NewMiko(1.0.1)-17-output.APK"
        }
    }


}

dependencies {
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(project(":core"))
    implementation(project(":native"))
    implementation(libs.appcompat)
    implementation(project(":service"))
    implementation(libs.preference.ktx)
}

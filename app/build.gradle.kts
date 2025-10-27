import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin)
//    alias(libs.plugins.java.zygisk)
}

android {
    namespace = "im.mingxi.miko"
    compileSdk = 35
    buildToolsVersion = "35"

    defaultConfig {
        applicationId = "im.mingxi.miko"
        minSdk = 26
        targetSdk = 35
        versionCode = 44
        versionName = "1.0.4"

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
           
        }
    }

//    flavorDimensions += "target"
//
//    productFlavors {
//        create("applications") {
//            dimension = "target"
//
//           zygisk {
//               packages(ZygoteLoader.ALL_PACKAGES)
//           }
//
//            buildConfigField("Boolean", "RUN_FOR_APPLICATIONS", "true")
//            buildConfigField("Boolean", "RUN_FOR_SYSTEM_SERVER", "false")
//        }
//        create("system_server") {
//            dimension = "target"
//
//            zygisk {
//                packages(ZygoteLoader.PACKAGE_SYSTEM_SERVER)
//            }
//
//            buildConfigField("Boolean", "RUN_FOR_APPLICATIONS", "false")
//            buildConfigField("Boolean", "RUN_FOR_SYSTEM_SERVER", "true")
//        }
//    }

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
                outputFileName = "NewMiko(1.0.4)-44-output.APK"
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


//zygisk {
//    id = "io_curl_zygisk_miko"
//    name = "MikoZygisk"
//    archiveName = "MikoZygisk"
//    author = "curl"
//    description = "深行红尘里，所思在远道。"
//    entrypoint = "im.mingxi.loader.zygisk.HookEntry"
//}

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "im.mingxi.core"
    compileSdk = 35
  

    defaultConfig {

        minSdk = 26
        // targetSdk = 35

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.add("arm64-v8a")
        }
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x78"
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }



    buildFeatures {
        viewBinding = true
        buildConfig = true
    }


    externalNativeBuild {
        cmake {
            //  path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.mmkv)
    implementation(libs.okhttp)
    //implementation("com.google.code.gson:gson:2.11.0")
    implementation(libs.kotlin.stdlib)
    implementation(libs.dexkit)
    //implementation("net.bytebuddy:byte-buddy-android:1.14.10")
    implementation(libs.glide)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    annotationProcessor(libs.glidecompiler)
    implementation(project(":loader"))
    //ksp(project(":annotation"))
    ksp(project(":annotation"))
    implementation(project(":annotation"))
}

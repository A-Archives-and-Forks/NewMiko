plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "im.mingxi.core"
    compileSdk = 35
    ndkVersion = "24.0.8215888"

    defaultConfig {
        
        minSdk = 26
        targetSdk = 35

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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.tencent:mmkv:2.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")
    implementation("org.luckypray:dexkit:2.0.2")
    //implementation("net.bytebuddy:byte-buddy-android:1.14.10")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.1")
    implementation(project(":loader"))
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.axix.il2cpp_bridge"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.axix.il2cpp_bridge"
        minSdk = 26
        targetSdk = 34
        versionCode = 104
        versionName = "1.0.4"

        ndk {
        }

        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }


    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures{
        prefab = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    ndkVersion = "25.2.9519653"
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    compileOnly("de.robv.android.xposed:api:82")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    implementation("io.github.hexhacking:xdl:2.1.1")
}
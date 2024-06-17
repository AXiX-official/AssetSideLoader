plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "top.axix.assetsideloader"
    compileSdk = 34

    defaultConfig {
        applicationId = "top.axix.assetsideloader"
        minSdk = 24
        targetSdk = 34
        versionCode = 111
        versionName = "1.1.1"

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
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
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
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.code.gson:gson:2.8.9")
}
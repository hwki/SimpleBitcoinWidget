plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.brentpanther.bitcoinwidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.brentpanther.bitcoinwidget"
        minSdk = 23
        targetSdk = 35
        versionCode = 334
        versionName = "8.5.9"
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("test") {
            resources.srcDir("src/main/res")
        }
    }
    flavorDimensions += listOf("distribution")
    productFlavors {
        create("playstore") {
            dimension = "distribution"
        }
        create("fdroid") {
            dimension = "distribution"
        }
    }
    ksp {
        arg("room.generateKotlin", "true")
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.preferences)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.work)
    implementation(libs.coil)
    implementation(libs.okhttp)
    implementation(libs.serialization)

    testImplementation(libs.json.path)
    testImplementation(libs.junit)
}

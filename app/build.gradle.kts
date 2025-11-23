plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Plugin para Room
}

android {
    namespace = "com.gameverse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gameverse"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments += mapOf(
            "clearPackageData" to "true",
            "useTestStorageService" to "true"
        )

        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Exclusiones necesarias para evitar conflictos en tests con MockK
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle & Navigation
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Accompanist & Location
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ============ TESTING ============

    // Unit Tests (Local)
    testImplementation(libs.junit)
    // --- NUEVAS DEPENDENCIAS AÑADIDAS ---
    testImplementation(libs.mockk)              // Para crear Mocks
    testImplementation(libs.kotest.assertions)  // Para aserciones legibles (shouldBe)
    testImplementation(libs.kotlinx.coroutines.test) // Para probar corutinas (runTest)

    // Android Instrumented Tests - TODAS LAS VERSIONES 1.6.x
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // UI Automator
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Test Orchestrator
    androidTestUtil("androidx.test:orchestrator:1.5.0")

    // Retrofit y Gson para consumir API
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Compose Testing
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
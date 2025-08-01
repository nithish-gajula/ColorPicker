plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {

    namespace = "com.example.colorpicker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.colorpicker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
//    implementation("me.jfenn.ColorPickerDialog:base:0.2.2")
//    implementation("me.jfenn.ColorPickerDialog:imagepicker:0.2.2")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.4.2")

    implementation(project(":base")) // ColorPickerDialog library
    implementation(project(":chooser"))
    implementation(libs.androidx.camera.view) //ohmae library
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
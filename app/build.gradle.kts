plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appnotes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appnotes"
        minSdk = 22
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore-ktx:24.4.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.compose.material3:material3:1.0.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.0.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx:20.1.0")
    implementation("com.google.firebase:firebase-appcheck-debug:16.1.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.1.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.room:room-runtime:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.5.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.arch.core:core-runtime:2.1.0")
    implementation("com.airbnb.android:lottie:4.1.0")

}
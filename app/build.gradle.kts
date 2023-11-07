plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.medease"
    compileSdk = 33
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }

    defaultConfig {
        applicationId = "com.example.medease"
        minSdk = 24
        targetSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation ("io.agora.rtc:full-sdk:4.0.1") //agora
        // Add the dependency for the Firebase Authentication library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-auth-ktx")

        // Also add the dependency for the Google Play services library and specify its version
        implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // Kotlin Coroutines Core Library
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
        implementation("androidx.core:core-ktx:1.9.0")
    implementation ("com.mikhaellopez:circularimageview:4.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
//used piccasso to add image from a url
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.firebase:firebase-firestore-ktx:24.8.1")
        implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("androidx.media3:media3-common:1.1.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}
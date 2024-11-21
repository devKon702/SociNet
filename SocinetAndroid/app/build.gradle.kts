plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.socinetandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.socinetandroid"
        minSdk = 27
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

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.lifecycle.livedata)
    implementation (libs.lifecycle.viewmodel)

    implementation(libs.annotation)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.security.crypto)
    implementation(libs.picasso)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    implementation(libs.tedpermission.normal)
//    implementation("io.github.ParkSangGwon:tedbottompicker:2.0.1")
//    implementation("gun0912.ted:tedbottompicker:1.2.6")
    implementation (libs.socket.io.client){
        exclude(group = "org.json", module = "json")
    }
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.13.0")

}
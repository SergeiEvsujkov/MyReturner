

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}



android {
    namespace = "com.example.myreturner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myreturner"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }

    buildFeatures {
        viewBinding = true
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
    implementation(libs.play.services.location)
    implementation(libs.mapkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

        //hua
        // implementation ("com.huawei.hms:push:6.11.0.300")
   // implementation ("com.huawei.hms:maps:6.4.1.300")
    //implementation ("com.huawei.hms:location:6.4.0.300")
    //implementation ("com.huawei.hms:hianalytics:6.4.1.302")
   // implementation ("com.huawei.agconnect:agconnect-crash:1.9.1.304")
   // implementation ("com.huawei.agconnect:agconnect-remoteconfig:1.6.2.300")
   // implementation("com.huawei.agconnect:agconnect-core:1.9.1.304")
}

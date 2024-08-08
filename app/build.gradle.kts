plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.ember"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.ember"
        minSdk = 26
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
        buildConfig =true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation (libs.firebase.ui.auth)


    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.database)
        implementation (libs.play.services.maps.v1802)
        implementation (libs.play.services.location)

    implementation (libs.play.services.maps)


        implementation (libs.material)
        implementation (libs.recyclerview)
        implementation (libs.cardview)

        implementation (libs.picasso)
    implementation (libs.material.v121)
        implementation (libs.material.v130)
    implementation (libs.firebase.storage)













}
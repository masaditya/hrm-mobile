plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.hrmpandjiadhi"
    compileSdk = 34

    flavorDimensions += "version"

    productFlavors{
        create("dev"){
            dimension = "version"
            applicationId = "com.hrmpandjiadhi"
            minSdk = 24
            targetSdk = 34
            versionCode = 4
            versionName = "1.0.14"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            buildConfigField("String", "BASE_URL", "\"https://api-appam.mahawangsa.com\"")
            buildConfigField("String", "img_url", "\"https://appam.mahawangsa.com/public/user-uploads/avatar/\"")

        }
        create("prod"){
            dimension = "version"
            applicationId = "com.hrmapps"
            minSdk = 24
            targetSdk = 34
            versionCode = 3
            versionName = "1.0.13"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            buildConfigField("String", "BASE_URL", "\"https://api-app.mahawangsa.com\"")
            buildConfigField("String", "img_url", "\"https://app.mahawangsa.com/public/user-uploads/avatar/\"")

        }
    }


    buildTypes {
        release {
            isDebuggable  = false
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", "\"https://api-app.mahawangsa.com\"")
            buildConfigField("String", "img_url", "\"https://app.mahawangsa.com/public/user-uploads/avatar/\"")
            resValue("string", "app_name", "Emahawangsa")
            setProperty("archivesBaseName", "Emahawangsa")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
        debug {
            isDebuggable  = true
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "BASE_URL", "\"https://api-appam.mahawangsa.com\"")
            buildConfigField("String", "img_url", "\"https://appam.mahawangsa.com/public/user-uploads/avatar/\"")

            resValue("string", "app_name", "Emahawangsa - PAM")

            setProperty("archivesBaseName", "Emahawangsa - PAM")
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //Bottom Navigation Bar Curved
    implementation(libs.curvedbottomnavigation)
    //Circle Image
    implementation(libs.circleimageview)
    //Spinner
    //noinspection UseTomlInstead
    implementation(libs.powerspinner)

    // Paging 3
    implementation(libs.androidx.paging.runtime.ktx)

    //Camera
    implementation(libs.face.detection)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    //Glide
    implementation(libs.glide)

    //lottie
    implementation(libs.lottie)
    //shimmer
    implementation(libs.shimmer)

    //Chucker
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)


}
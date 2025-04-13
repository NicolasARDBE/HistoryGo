plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.historygo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.historygo"
        minSdk = 31
        targetSdk = 35
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.core.ktx)
    implementation(libs.projectlombok.lombok)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.filament.android)
    implementation(libs.material)
    implementation(libs.constraintlayout.core)
    implementation(libs.constraintlayout.solver)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.preference.ktx)
    implementation(libs.okhttp)

    //AWS
    implementation(libs.aws.android.sdk.core)
    implementation(libs.aws.android.sdk.cognitoidentityprovider)
    implementation(libs.aws.android.sdk.ddb)
    implementation(libs.aws.android.sdk.ddb.document)
    implementation (libs.aws.android.sdk.apigateway.core)

    //lombok
    compileOnly(libs.lombok)

    //glide (image loading)
    implementation (libs.glide)

    //OSM
    implementation (libs.osmdroid.android)
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.gson)
    implementation(libs.osmbonuspack)

    //API Gateway
    implementation(files("libs/historygo-sdk-1.0.0.jar"))

    //JWT
    implementation(libs.jwtdecode)

    //360 Degree Display
    implementation(libs.panoramagl)

    //GeoFencing
    implementation(libs.play.services.location)
}
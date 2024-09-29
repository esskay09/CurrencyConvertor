plugins {
    alias(libs.plugins.currencyconvertor.library)
    alias(libs.plugins.currencyconvertor.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.example.currencyconvertor.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    implementation(projects.core.common)

    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

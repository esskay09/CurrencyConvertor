
plugins {
    alias(libs.plugins.currencyconvertor.library)
    alias(libs.plugins.currencyconvertor.room)
    alias(libs.plugins.currencyconvertor.hilt)
}

android {
    namespace = "com.example.currencyconvertor.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

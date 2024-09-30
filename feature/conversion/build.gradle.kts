plugins {
    alias(libs.plugins.currencyconvertor.feature)
    alias(libs.plugins.currencyconvertor.library.compose)
}

android {
    namespace = "com.example.currencyconvertor.feature.topic"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

    testImplementation(projects.core.testing)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
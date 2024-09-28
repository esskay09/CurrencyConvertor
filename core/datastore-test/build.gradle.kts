
plugins {
    alias(libs.plugins.currencyconvertor.library)
    alias(libs.plugins.currencyconvertor.hilt)
}

android {
    namespace = "com.example.currencyconvertor.core.datastore.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}

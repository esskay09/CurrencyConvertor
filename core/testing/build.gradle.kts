
plugins {
    alias(libs.plugins.currencyconvertor.library)
    alias(libs.plugins.currencyconvertor.hilt)
}

android {
    namespace = "com.example.currencyconvertor.core.testing"
}

dependencies {
    api(libs.kotlinx.coroutines.test)
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.datetime)
}

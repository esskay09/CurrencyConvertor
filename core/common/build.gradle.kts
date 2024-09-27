
plugins {
    alias(libs.plugins.currencyconvertor.jvm.library)
    alias(libs.plugins.currencyconvertor.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    testImplementation(libs.kotlin.test)
}
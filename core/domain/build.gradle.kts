plugins {
    alias(libs.plugins.currencyconvertor.library)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.currencyconvertor.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)

    testImplementation(projects.core.testing)
}
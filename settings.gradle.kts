pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CurrencyConvertor"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":app")
include(":core:model")
include(":core:common")
include(":core:designsystem")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:datastore-test")
include(":core:database")
include(":core:network")
include(":core:data")
include(":core:domain")
include(":core:testing")
include(":feature:conversion")
include(":sync:work")

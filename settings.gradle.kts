pluginManagement {
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
        google() // Per AndroidX, Material e altre librerie Google
        mavenCentral() // Per librerie come Retrofit, Picasso, MPAndroidChart, ecc.
        maven { url = uri("https://jitpack.io") } // Per eventuali librerie da GitHub/JitPack
    }
}

rootProject.name = "GitHubRepo"
include(":app")

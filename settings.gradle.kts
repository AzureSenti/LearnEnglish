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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ĐÃ THÊM KHO LƯU TRỮ JITPACK Ở ĐÂY ĐỂ TẢI THƯ VIỆN TOASTY
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "LearnEnglish"
include(":app")
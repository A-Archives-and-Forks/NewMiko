pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Miko"

include(":app", ":loader", ":core", ":annotation")
project(":loader").projectDir = file("loader/")
project(":core").projectDir = file("core/")
project(":annotation").projectDir = file("annotation/")
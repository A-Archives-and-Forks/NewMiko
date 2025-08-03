pluginManagement {
  repositories {
    gradlePluginPortal()
      maven { url = uri("https://jitpack.io") }
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
      maven { url = uri("https://jitpack.io") }
      maven {
          url =
              uri("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
      }
  }

}

rootProject.name = "Miko"

include(":app", ":loader", ":core", ":annotation", ":native", ":service")


pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
        maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
    }
}

rootProject.name = "cirrus"
include("cirrus-api")
include("cirrus-common")
include("cirrus-spigot")
include("cirrus-bungeecord")
include("cirrus-velocity")
include("cirrus-fabric")

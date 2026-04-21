pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.neoforged.net/releases")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("net.neoforged.moddev.repositories") version "2.0.+"
}

// Extra repos for cirrus-neoforge (which can't declare project-level repos due to ModDevGradle)
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
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "gg.modl.minecraft.cirrus"
version = "4.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(project(":cirrus-api"))
    implementation(project(":cirrus-common"))

    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
//    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

    compileOnly("com.github.retrooper:packetevents-velocity:2.11.2")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("Cirrus-Velocity")
        archiveClassifier.set("")
    }
}

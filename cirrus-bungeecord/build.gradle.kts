plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "gg.modl.minecraft.cirrus"
version = "4.1.8-SNAPSHOT"

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("Cirrus-BungeeCord")
        archiveClassifier.set("")
    }
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.21-R0.4")
    implementation(project(":cirrus-api"))
    implementation(project(":cirrus-common"))

    compileOnly("com.github.retrooper:packetevents-bungeecord:2.11.2")
}
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.4"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    implementation(project(":cirrus-api"))
    implementation(project(":cirrus-common"))

    compileOnly("gg.modl.minecraft.packetevents:packetevents-spigot:${rootProject.extra["packetEventsVersion"]}")

    // Adventure API (provided by Paper/Spigot)
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.14.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("Cirrus-Spigot")
        archiveClassifier.set("")
        relocate("com.github.retrooper.packetevents", "gg.modl.libs.packetevents.api")
        relocate("io.github.retrooper.packetevents", "gg.modl.libs.packetevents.impl")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

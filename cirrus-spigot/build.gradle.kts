plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "gg.modl.minecraft.cirrus"
version = "3.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    implementation(project(":cirrus-api"))

    // NBT library for item data conversion
    compileOnly("com.github.Querz:NBT:6.1")

    // Adventure API (provided by Paper/Spigot)
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.14.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("Cirrus-Spigot")
        archiveClassifier.set("")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

plugins {
    id("java")
    `maven-publish`
    id("com.gradleup.shadow") version "9.3.1"
    id("fabric-loom") version "1.9-SNAPSHOT"
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.2"

repositories {
    mavenCentral()
    maven(url = "https://maven.fabricmc.net/")
    maven(url = "https://repo.codemc.io/repository/maven-releases/")
    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.10")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.114.0+1.21.4")

    implementation(project(":cirrus-api"))
    implementation(project(":cirrus-common"))

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("com.github.retrooper:packetevents-api:2.11.2")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
}

tasks.shadowJar {
    isEnabled = false
}

tasks.remapJar {
    archiveClassifier.set("remapped")
}

val relocatedJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    configurations = emptyList()
    dependsOn(tasks.remapJar)
    from(zipTree(tasks.remapJar.flatMap { it.archiveFile }))

    // Bundle cirrus-api and cirrus-common into the JAR
    from(project(":cirrus-api").sourceSets.main.get().output)
    from(project(":cirrus-common").sourceSets.main.get().output)

    relocate("com.github.retrooper.packetevents", "gg.modl.libs.packetevents.api")
    relocate("io.github.retrooper.packetevents", "gg.modl.libs.packetevents.impl")

    archiveClassifier.set("")
    archiveBaseName.set("cirrus-fabric")
}

tasks.assemble {
    dependsOn(relocatedJar)
}

// Publish the final JAR (Loom-remapped + PE-relocated + bundled deps)
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "gg.modl.minecraft.cirrus"
            artifactId = "cirrus-fabric"
            version = project.version.toString()
            afterEvaluate {
                artifact(relocatedJar)
            }
        }
    }
    repositories {
        maven {
            name = "ModlNexus"
            url = uri("https://nexus.modl.gg/repository/maven-releases/")
            val nexusUser = System.getenv("NEXUS_USER") ?: project.findProperty("nexus.user") as String?
            val nexusPass = System.getenv("NEXUS_PASS") ?: project.findProperty("nexus.pass") as String?
            if (nexusUser != null && nexusPass != null) {
                credentials {
                    username = nexusUser
                    password = nexusPass
                }
            }
        }
    }
}

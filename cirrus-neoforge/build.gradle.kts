plugins {
    id("java")
    `maven-publish`
    id("net.neoforged.moddev")
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.2"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = "21.4.157"
}

dependencies {
    implementation(project(":cirrus-api"))
    implementation(project(":cirrus-common"))

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("com.github.retrooper:packetevents-api:2.11.2")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "gg.modl.minecraft.cirrus"
            artifactId = "cirrus-neoforge"
            version = project.version.toString()
            from(components["java"])
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

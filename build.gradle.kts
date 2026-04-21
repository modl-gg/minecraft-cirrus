plugins {
    id("java")
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.4"
extra["packetEventsVersion"] = "2.12.3"

val modPlatformModules = setOf("cirrus-fabric", "cirrus-neoforge")

allprojects {
    if (name in modPlatformModules) return@allprojects

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        disableAutoTargetJvm()
        withSourcesJar()
        withJavadocJar()
    }

    afterEvaluate {
        configurations.all {
            if (name.contains("plugin-yml")) {
                extendsFrom.clear()
            }
        }
    }

    repositories {
        mavenCentral()
        maven(url = "https://nexus.modl.gg/repository/maven-releases/")
        maven(url = "https://nexus.modl.gg/repository/maven-snapshots/")
        maven(url = "https://libraries.minecraft.net")
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
        maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        lib("org.projectlombok:lombok:1.18.36")
        lib("com.google.code.gson:gson:2.10")
        lib("org.slf4j:slf4j-api:1.8.0-beta4")

        lib("com.mojang:authlib:1.5.21")

        annotationProcessor("org.projectlombok:lombok:1.18.36")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
        testCompileOnly("org.projectlombok:lombok:1.18.36")

        testImplementation("org.slf4j:slf4j-api:1.8.0-beta4")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks {

        test {
            useJUnitPlatform()
        }

        javadoc {
            options {
                (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }
}

subprojects {
    if (name in modPlatformModules) return@subprojects

    plugins.withId("com.gradleup.shadow") {
        tasks.named<Jar>("jar") {
            archiveClassifier.set("slim")
        }
    }

    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("gpr") {
                    groupId = "gg.modl.minecraft.cirrus"
                    artifactId = project.name
                    version = project.version.toString()
                    from(components["java"])
                }
            }
            repositories {
                maven {
                    name = "ModlNexus"
                    url = uri("https://nexus.modl.gg/repository/maven-releases/")
                    credentials {
                        username = System.getenv("NEXUS_USER") ?: project.findProperty("nexus.user") as String?
                        password = System.getenv("NEXUS_PASS") ?: project.findProperty("nexus.pass") as String?
                    }
                }
            }
        }
    }
}

fun DependencyHandlerScope.lib(value: String) {
    compileOnly(value)
    testImplementation(value)
}

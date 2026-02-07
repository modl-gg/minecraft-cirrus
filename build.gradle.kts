plugins {
    id("java")
}

group = "gg.modl.minecraft.cirrus"
version = "4.0.2-SNAPSHOT"

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
        maven(url = "https://libraries.minecraft.net")
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
        maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        lib("org.projectlombok:lombok:1.18.24")
        lib("com.google.code.gson:gson:2.10")
        lib("org.slf4j:slf4j-api:1.8.0-beta4")

        lib("com.mojang:authlib:1.5.21")

        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
        testCompileOnly("org.projectlombok:lombok:1.18.24")

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
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/modl-gg/minecraft-cirrus")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                        password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.token") as String?
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
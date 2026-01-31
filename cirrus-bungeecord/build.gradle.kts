plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-waterfall") version "2.0.0"
    // Authenticated Maven publishing
    id("org.hibernate.build.maven-repo-auth") version "3.0.3"
}

group = "dev.simplix.cirrus"
version = "3.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// publish
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

tasks {

    assemble {
        dependsOn(shadowJar)
    }
    runWaterfall {
        waterfallVersion("1.19")
    }

    shadowJar {
    }
}



dependencies {

    compileOnly("net.md-5:bungeecord-api:1.20-R0.2")
    implementation(project(":cirrus-api"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
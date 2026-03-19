plugins {
    id("java")
}

group = "gg.modl.minecraft.cirrus"
version = "4.1.4-SNAPSHOT"

dependencies {
    implementation(project(":cirrus-api"))
    compileOnly("com.github.retrooper:packetevents-api:2.11.2")
    compileOnly("net.kyori:adventure-api:4.14.0")
}

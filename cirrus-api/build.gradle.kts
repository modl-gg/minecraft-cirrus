plugins {
    id("java")
    application
}

group = "gg.modl.minecraft.cirrus"
version = "4.1.8-SNAPSHOT"

dependencies {
    compileOnly("com.github.retrooper:packetevents-api:2.11.2")

    // Adventure for text components
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
    testImplementation("net.kyori:adventure-api:4.14.0")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
}
plugins {
    id("java")
    application
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.4"

dependencies {
    compileOnly("gg.modl.minecraft.packetevents:packetevents-api:${rootProject.extra["packetEventsVersion"]}")

    // Adventure for text components
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
    testImplementation("net.kyori:adventure-api:4.14.0")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
}

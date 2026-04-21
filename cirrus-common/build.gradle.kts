plugins {
    id("java")
}

group = "gg.modl.minecraft.cirrus"
version = "4.2.4"

dependencies {
    implementation(project(":cirrus-api"))
    compileOnly("gg.modl.minecraft.packetevents:packetevents-api:${rootProject.extra["packetEventsVersion"]}")
    compileOnly("net.kyori:adventure-api:4.14.0")

    testImplementation("gg.modl.minecraft.packetevents:packetevents-api:${rootProject.extra["packetEventsVersion"]}")
    testImplementation("net.kyori:adventure-api:4.14.0")
    testImplementation("net.kyori:adventure-nbt:4.14.0")
    testImplementation("org.jspecify:jspecify:1.0.0")
}

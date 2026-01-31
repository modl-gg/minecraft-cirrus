plugins {
    id("java")
    application
}

group = "gg.modl.minecraft.cirrus"
version = "3.0.0-SNAPSHOT"

dependencies {
    // NBT library for item data serialization
    compileOnly("com.github.Querz:NBT:6.1")
    testImplementation("com.github.Querz:NBT:6.1")

    // Adventure for text components
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
    testImplementation("net.kyori:adventure-api:4.14.0")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
}
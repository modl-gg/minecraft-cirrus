# Cirrus fork

We decided to fork Cirrus so we can maintain it on our own, and so we can use packetevents instead of protocolize for our
Velocity/BungeeCord menus. We plan to keep this working for all client versions 1.8.8 and above.

[Original Repo](https://github.com/Simplix-Softworks/Cirrus)

## Maven/Gradle Dependency

The `[PLATFORM]` placeholder can be replaced with `spigot`, `bungeecord`, or `velocity`. Forks of these software are covered under the parent, so Paper is covered under Spigot, and Waterfall is covered under BungeeCord.

**YOU MUST INCLUDE [PACKETEVENTS](https://github.com/retrooper/packetevents) AS A DEPENDENCY IN YOUR PLUGIN!**
You can shade it or require it to be installed on the server. For our library, Cirrus, you should just shade it.

> Maven
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  	<dependency>
	    <groupId>com.github.modl-gg.minecraft-cirrus</groupId>
	    <artifactId>cirrus-api</artifactId>
	    <version>4.1.4-SNAPSHOT</version>
	</dependency>
  	<dependency>
	    <groupId>com.github.modl-gg.minecraft-cirrus</groupId>
	    <artifactId>cirrus-[PLATFORM]</artifactId>
	    <version>4.1.4-SNAPSHOT</version>
	</dependency>
</dependencies>
```

> Gradle
```groovy
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}

dependencies {
        implementation 'com.github.modl-gg.minecraft-cirrus:cirrus-api:4.1.4-SNAPSHOT'
        implementation 'com.github.modl-gg.minecraft-cirrus:cirrus-[PLATFORM]:4.1.4-SNAPSHOT'
}
```

## Example Usage

View how we used this library in our plugin for modl.gg [here](https://github.com/modl-gg/minecraft/tree/main/core/src/main/java/gg/modl/minecraft/core/impl/menus).

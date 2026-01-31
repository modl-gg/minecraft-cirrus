package dev.simplix.cirrus.spigot.util;

import dev.simplix.protocolize.api.util.ProtocolVersions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public final class ProtocolVersionUtil {

  private int protocolVersion;
  private String versionString;

  /**
   * Minimum fallback protocol version for unknown newer versions.
   * Uses MINECRAFT_LATEST which represents the highest known protocol version.
   */
  private static final int FALLBACK_PROTOCOL_VERSION = ProtocolVersions.MINECRAFT_LATEST;

  /**
   * Pattern to extract version from Bukkit version string (e.g., "1.21.3-R0.1-SNAPSHOT")
   */
  private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

  /**
   * Returns the protocol-version int of the server. E.G: 755 for 1.17.1
   *
   * @return the protocol-version int.
   */
  public int serverProtocolVersion() {
    if (protocolVersion == 0) {
      protocolVersion = detectVersion();
    }
    return protocolVersion;

  }

  /**
   * Returns the major server version E.G: "1.21" or "1.21.3"
   *
   * @return the major server version.
   */
  public String versionString() {
    if (versionString != null) {
      return versionString;
    }

    String serverVersion = ReflectionUtil.serverVersion();
    if (serverVersion != null && !serverVersion.isEmpty() && serverVersion.startsWith("v")) {
      // Legacy format: v1_21_R1 -> 1.21
      int secondUnderscore = serverVersion.indexOf('_', 3);
      if (secondUnderscore > 0) {
        versionString = serverVersion.substring(1, secondUnderscore).replace('_', '.');
        return versionString;
      }
    }

    // Modern Paper: extract from Bukkit version
    versionString = extractVersionFromBukkit();
    return versionString;
  }

  private int detectVersion() {
    String serverVersion = ReflectionUtil.serverVersion();

    // Try legacy format first (v1_21_R1)
    if (serverVersion != null && !serverVersion.isEmpty() && serverVersion.startsWith("v")) {
      int secondUnderscore = serverVersion.indexOf('_', 3);
      if (secondUnderscore > 0) {
        String majorVersion = serverVersion.substring(1, secondUnderscore);
        Integer result = tryGetProtocolVersion(majorVersion);
        if (result != null) {
          return result;
        }
      }
    }

    // Modern Paper servers: extract version from Bukkit.getBukkitVersion()
    String bukkitVersion = extractVersionFromBukkit();
    if (bukkitVersion != null) {
      String majorVersion = bukkitVersion.replace('.', '_');
      Integer result = tryGetProtocolVersion(majorVersion);
      if (result != null) {
        return result;
      }
      // Try without patch version (1_21_3 -> 1_21)
      int lastUnderscore = majorVersion.lastIndexOf('_');
      if (lastUnderscore > 2) {
        String withoutPatch = majorVersion.substring(0, lastUnderscore);
        result = tryGetProtocolVersion(withoutPatch);
        if (result != null) {
          return result;
        }
      }
      log.info("[Cirrus] Server version {} not explicitly mapped, using latest compatibility mode", bukkitVersion);
    }

    return FALLBACK_PROTOCOL_VERSION;
  }

  private Integer tryGetProtocolVersion(String majorVersion) {
    try {
      Field field = ProtocolVersions.class.getField("MINECRAFT_" + majorVersion);
      return field.getInt(null);
    } catch (IllegalAccessException exception) {
      log.error("Could not access field MINECRAFT_" + majorVersion, exception);
    } catch (NoSuchFieldException ignored) {
      // Version not found, will try fallback
    }
    return null;
  }

  private String extractVersionFromBukkit() {
    try {
      String bukkitVersion = Bukkit.getBukkitVersion();
      Matcher matcher = VERSION_PATTERN.matcher(bukkitVersion);
      if (matcher.find()) {
        String major = matcher.group(1);
        String minor = matcher.group(2);
        String patch = matcher.group(3);
        if (patch != null) {
          return major + "." + minor + "." + patch;
        }
        return major + "." + minor;
      }
    } catch (Exception e) {
      log.warn("[Cirrus] Could not extract version from Bukkit", e);
    }
    return "1.21";
  }

}

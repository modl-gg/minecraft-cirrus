package dev.simplix.cirrus.spigot.util;

import dev.simplix.cirrus.protocol.CirrusProtocolVersions;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

@Slf4j
@UtilityClass
public final class ProtocolVersionUtil {

    private static final int FALLBACK_PROTOCOL_VERSION = CirrusProtocolVersions.MINECRAFT_LATEST;
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private int protocolVersion;
    private String versionString;

    public int serverProtocolVersion() {
        if (protocolVersion == 0) {
            protocolVersion = detectVersion();
        }
        return protocolVersion;

    }

    private int detectVersion() {
        String serverVersion = ReflectionUtil.serverVersion();

        if (serverVersion != null && serverVersion.startsWith("v")) {
            int secondUnderscore = serverVersion.indexOf('_', 3);
            if (secondUnderscore > 0) {
                String majorVersion = serverVersion.substring(1, secondUnderscore);
                Integer result = tryGetProtocolVersion(majorVersion);
                if (result != null) {
                    return result;
                }
            }
        }

        String bukkitVersion = extractVersionFromBukkit();
        String majorVersion = bukkitVersion.replace('.', '_');
        Integer result = tryGetProtocolVersion(majorVersion);
        if (result != null) {
            return result;
        }
        int lastUnderscore = majorVersion.lastIndexOf('_');
        if (lastUnderscore > 2) {
            String withoutPatch = majorVersion.substring(0, lastUnderscore);
            result = tryGetProtocolVersion(withoutPatch);
            if (result != null) {
                return result;
            }
        }
        log.info("[Cirrus] Server version {} not explicitly mapped, using latest compatibility mode", bukkitVersion);

        return FALLBACK_PROTOCOL_VERSION;
    }

    private Integer tryGetProtocolVersion(String majorVersion) {
        try {
            Field field = CirrusProtocolVersions.class.getField("MINECRAFT_" + majorVersion);
            return field.getInt(null);
        } catch (IllegalAccessException exception) {
            log.error("Could not access field MINECRAFT_" + majorVersion, exception);
        } catch (NoSuchFieldException ignored) {
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

    public String versionString() {
        if (versionString != null) {
            return versionString;
        }

        String serverVersion = ReflectionUtil.serverVersion();
        if (serverVersion != null && serverVersion.startsWith("v")) {
            int secondUnderscore = serverVersion.indexOf('_', 3);
            if (secondUnderscore > 0) {
                versionString = serverVersion.substring(1, secondUnderscore).replace('_', '.');
                return versionString;
            }
        }

        versionString = extractVersionFromBukkit();
        return versionString;
    }

}

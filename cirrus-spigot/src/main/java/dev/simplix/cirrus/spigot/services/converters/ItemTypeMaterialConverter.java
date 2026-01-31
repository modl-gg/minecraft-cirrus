package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.spigot.util.ProtocolVersionUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.ItemType;

import java.util.function.Function;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;

@Slf4j
public class ItemTypeMaterialConverter implements Function<ItemType, Material> {

  @Override
  public Material apply(@NonNull ItemType src) {
    if (ProtocolVersionUtil.serverProtocolVersion() >= ProtocolVersions.MINECRAFT_1_13) {
      try {
        return Material.valueOf(src.name());
      } catch (IllegalArgumentException e) {
        // Material may not exist on this server version, fall back to STONE
        log.debug("[Cirrus] Material {} not found on this server version, using STONE as fallback", src.name());
        return Material.STONE;
      }
    }

    throw new IllegalArgumentException("Unsupported type "
                                       + src.name()
                                       + " on protocol version: "
                                       + ProtocolVersionUtil.serverProtocolVersion());
  }

}

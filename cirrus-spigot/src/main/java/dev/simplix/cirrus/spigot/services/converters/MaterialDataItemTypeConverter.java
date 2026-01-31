package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.spigot.util.ProtocolVersionUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.ItemType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.material.MaterialData;

import java.util.function.Function;

@Slf4j
public class MaterialDataItemTypeConverter implements Function<MaterialData, ItemType> {

  @Override
  public ItemType apply(@NonNull MaterialData src) {
    // Modern versioning
    if (ProtocolVersionUtil.serverProtocolVersion() >= ProtocolVersions.MINECRAFT_1_14) {
      try {
        return ItemType.valueOf(src.getItemType().name());
      } catch (IllegalArgumentException e) {
        // ItemType may not exist in Protocolize for newer server materials
        log.debug("[Cirrus] ItemType {} not found in Protocolize, using STONE as fallback", src.getItemType().name());
        return ItemType.STONE;
      }
    }

    throw new IllegalStateException("Version not supported");
  }

}

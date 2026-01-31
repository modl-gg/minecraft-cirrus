package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.item.CirrusItemType;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.material.MaterialData;

@Slf4j
public class MaterialDataItemTypeConverter implements Function<MaterialData, CirrusItemType> {

    @Override
    public CirrusItemType apply(@NonNull MaterialData src) {
        try {
            String materialName = src.getItemType().name().toLowerCase();
            return CirrusItemType.of("minecraft:" + materialName);
        } catch (Exception e) {
            log.error("Cannot handle MaterialData on this server version!", e);
            return CirrusItemType.STONE;
        }
    }

}

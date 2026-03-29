package dev.simplix.cirrus.neoforge.services;

import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.service.ItemService;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class NeoForgeItemService extends ItemService {

    @Override
    public boolean isItemAvailable(CirrusItemType itemType, int protocolVersion) {
        if (itemType == null) return false;
        ResourceLocation id = ResourceLocation.parse(itemType.identifier());
        var item = BuiltInRegistries.ITEM.getOptional(id);
        return item.isPresent() && item.get() != Items.AIR;
    }
}

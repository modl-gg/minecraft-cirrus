package dev.simplix.cirrus.fabric.services;

import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.service.ItemService;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FabricItemService extends ItemService {

    @Override
    public boolean isItemAvailable(CirrusItemType itemType, int protocolVersion) {
        if (itemType == null) return false;
        Identifier id = Identifier.of(itemType.identifier());
        Item item = Registries.ITEM.get(id);
        return item != Items.AIR;
    }
}

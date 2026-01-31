package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import dev.simplix.cirrus.common.util.ComponentHelper;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
@UtilityClass
public class PacketItemStackConverter {

    public ItemStack toPacketEventsItemStack(CirrusBaseItemStack cirrusItem, int protocolVersion) {
        CirrusItemType cirrusType = cirrusItem.itemType();

        ItemType itemType = ItemTypes.getByName(cirrusType.identifier());
        if (itemType == null) {
            log.warn("Unknown item type: {}", cirrusType.identifier());
            return ItemStack.EMPTY;
        }

        ItemStack.Builder builder = ItemStack.builder()
            .type(itemType)
            .amount(cirrusItem.amount());

        CirrusChatElement displayName = cirrusItem.displayName();
        if (displayName != null && !displayName.isEmpty()) {
            Component nameComponent = ComponentHelper.removeItalic(displayName.asComponent());
            builder.component(ComponentTypes.CUSTOM_NAME, nameComponent);
        }

        List<CirrusChatElement> lore = cirrusItem.lore();
        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = lore.stream()
                .map(element -> ComponentHelper.removeItalic(element.asComponent()))
                .toList();
            builder.component(ComponentTypes.LORE, new ItemLore(loreComponents));
        }

        NBTCompound nbtData = cirrusItem.nbtData();
        if (nbtData != null && !nbtData.getTags().isEmpty()) {
            builder.component(ComponentTypes.CUSTOM_DATA, nbtData);
        }

        return builder.build();
    }
}

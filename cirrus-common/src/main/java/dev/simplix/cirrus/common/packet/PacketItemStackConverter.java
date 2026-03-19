package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import dev.simplix.cirrus.common.util.ComponentHelper;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.ArrayList;
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

        int legacyData = 0;
        if (ClientVersion.getById(protocolVersion).isOlderThan(ClientVersion.V_1_13)) {
            legacyData = LegacyItemMapping.getDataValue(cirrusType.identifier());
        }

        ItemStack.Builder builder = ItemStack.builder()
            .type(itemType)
            .amount(cirrusItem.amount())
            .legacyData(legacyData);

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
            // Extract SkullOwner for player heads and set as PROFILE component
            // Do NOT mutate nbtData - it's reused across renders
            NBT skullOwnerRaw = nbtData.getTags().get("SkullOwner");
            if (skullOwnerRaw instanceof NBTCompound skullOwner) {
                ItemProfile profile = extractProfile(skullOwner);
                if (profile != null) {
                    builder.component(ComponentTypes.PROFILE, profile);
                }
            }

            builder.component(ComponentTypes.CUSTOM_DATA, nbtData);
        }

        return builder.build();
    }

    /**
     * Extract an ItemProfile from a SkullOwner NBT compound.
     * Converts the legacy SkullOwner NBT format to PacketEvents' ItemProfile for 1.20.5+ compatibility.
     */
    private ItemProfile extractProfile(NBTCompound skullOwner) {
        try {
            List<ItemProfile.Property> properties = new ArrayList<>();

            NBT propertiesRaw = skullOwner.getTags().get("Properties");
            if (propertiesRaw instanceof NBTCompound propertiesCompound) {
                NBT texturesRaw = propertiesCompound.getTags().get("textures");
                if (texturesRaw instanceof NBTList<?> texturesList) {
                    for (Object entry : texturesList.getTags()) {
                        if (entry instanceof NBTCompound textureEntry) {
                            NBT valueRaw = textureEntry.getTags().get("Value");
                            String value = valueRaw instanceof NBTString ? ((NBTString) valueRaw).getValue() : null;
                            NBT sigRaw = textureEntry.getTags().get("Signature");
                            String signature = sigRaw instanceof NBTString ? ((NBTString) sigRaw).getValue() : null;
                            if (value != null) {
                                properties.add(new ItemProfile.Property("textures", value, signature));
                            }
                        }
                    }
                }
            }

            if (properties.isEmpty()) {
                return null;
            }

            return new ItemProfile(null, null, properties);
        } catch (Exception e) {
            log.warn("Failed to extract profile from SkullOwner NBT", e);
            return null;
        }
    }
}

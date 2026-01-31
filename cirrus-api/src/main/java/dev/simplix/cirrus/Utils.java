package dev.simplix.cirrus;

import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTShort;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    private static final String STEVE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTU5MTU3NDcyMzc4MywKICAicHJvZmlsZUlkIiA6ICI4NjY3YmE3MWI4NWE0MDA0YWY1NDQ1N2E5NzM0ZWVkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGV2ZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDNiMDZjMzg1MDRmZmMwMjI5Yjk0OTIxNDdjNjlmY2Y1OWZkMmVkNzg4NWY3ODUwMjE1MmY3N2I0ZDUwZGUxIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NTNjYWM4Yjc3OWZlNDEzODNlNjc1ZWUyYjg2MDcxYTcxNjU4ZjIxODBmNTZmYmNlOGFhMzE1ZWE3MGUyZWQ2IgogICAgfQogIH0KfQ==";

    public Optional<UUID> fromString(final String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return Optional.empty();
        }

        final String[] components = uuidString.split("-");
        if (components.length != 5) {
            return Optional.empty();
        }

        return Optional.of(UUID.fromString(uuidString));
    }

    public CirrusInventoryType calculateTypeForContent(final int size) {
        return CirrusInventoryType.calculateForContent(size);
    }

    public int calculateSizeForContent(final int size) {
        if (size <= 9) return 9;
        if (size <= 18) return 18;
        if (size <= 27) return 27;
        if (size <= 36) return 36;
        if (size <= 45) return 45;
        return 54;
    }

    public CirrusInventoryType typeOfSize(final int size) {
        return CirrusInventoryType.fromSize(size);
    }

    public int sizeOfType(@NonNull final CirrusInventoryType type) {
        return type.size();
    }

    public Optional<Long> toLong(final String string) {
        try {
            return Optional.of(Long.parseLong(string));
        } catch (final NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public Optional<Integer> toIntOrNull(final String string) {
        try {
            return Optional.of(Integer.parseInt(string));
        } catch (final NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static List<String> colorize(final List<String> toColorize) {
        final List<String> out = new ArrayList<>();

        for (final String lore : toColorize) {
            out.add(lore.replace("&", "§"));
        }
        return out;
    }

    public static String colorize(final String name) {
        return name.replace("&", "§");
    }

    public static void glow(NBTCompound tag) {
        hideNbtFlags(tag);

        NBTList<NBTCompound> enchantments = new NBTList<>(NBTType.COMPOUND);
        NBTList<NBTCompound> enchs = new NBTList<>(NBTType.COMPOUND);

        NBTCompound exampleEnchantment = new NBTCompound();
        exampleEnchantment.setTag("id", new NBTString("minecraft:efficiency"));
        exampleEnchantment.setTag("lvl", new NBTShort((short) 1));

        NBTCompound exampleEnch = new NBTCompound();
        exampleEnch.setTag("id", new NBTShort((short) 1));
        exampleEnch.setTag("lvl", new NBTShort((short) 1));

        enchantments.addTag(exampleEnchantment);
        enchs.addTag(exampleEnch);

        tag.setTag("ench", enchs);
        tag.setTag("Enchantments", enchantments);
    }

    public static void hideNbtFlags(NBTCompound tag) {
        tag.setTag("HideFlags", new NBTInt(127));
    }

    public static void texture(NBTCompound tag, String textureHash) {
        NBT skullOwnerRaw = tag.getTags().get("SkullOwner");
        NBTCompound skullOwner;
        if (!(skullOwnerRaw instanceof NBTCompound)) {
            skullOwner = new NBTCompound();
        } else {
            skullOwner = (NBTCompound) skullOwnerRaw;
        }

        skullOwner.setTag("Name", new NBTString(textureHash));

        NBT propertiesRaw = skullOwner.getTags().get("Properties");
        NBTCompound properties;
        if (!(propertiesRaw instanceof NBTCompound)) {
            properties = new NBTCompound();
        } else {
            properties = (NBTCompound) propertiesRaw;
        }

        NBTCompound texture = new NBTCompound();
        texture.setTag("Value", new NBTString(textureHash.isEmpty() ? STEVE_TEXTURE : textureHash));

        NBTList<NBTCompound> textures = new NBTList<>(NBTType.COMPOUND);
        textures.addTag(texture);

        properties.setTag("textures", textures);
        skullOwner.setTag("Properties", properties);
        tag.setTag("SkullOwner", skullOwner);
    }
}

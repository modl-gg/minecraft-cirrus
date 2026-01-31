package dev.simplix.cirrus;

import dev.simplix.cirrus.menu.CirrusInventoryType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.querz.nbt.tag.*;

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

    public static void glow(CompoundTag tag) {
        hideNbtFlags(tag);

        final ListTag<CompoundTag> enchantments = new ListTag<>(CompoundTag.class);
        final ListTag<CompoundTag> enchs = new ListTag<>(CompoundTag.class);

        final CompoundTag exampleEnchantment = new CompoundTag();

        exampleEnchantment.put("id", new StringTag("minecraft:efficiency"));
        exampleEnchantment.put("lvl", new ShortTag((short) 1));

        final CompoundTag exampleEnch = new CompoundTag();
        exampleEnch.put("id", new ShortTag((short) 1));
        exampleEnch.put("lvl", new ShortTag((short) 1));

        enchantments.add(exampleEnchantment);
        enchs.add(exampleEnch);

        tag.put("ench", enchs);
        tag.put("Enchantments", enchantments);
    }

    public static void hideNbtFlags(CompoundTag tag) {
        tag.put("HideFlags", new IntTag(127));
    }

    public static void texture(CompoundTag tag, String textureHash) {
        if (!(tag.get("SkullOwner") instanceof CompoundTag)) {
            tag.put("SkullOwner", new CompoundTag());
        }

        CompoundTag skullOwner = tag.getCompoundTag("SkullOwner");

        if (skullOwner == null) {
            skullOwner = new CompoundTag();
        }

        skullOwner.put("Name", new StringTag(textureHash));
        CompoundTag properties = skullOwner.getCompoundTag("Properties");
        if (properties == null) {
            properties = new CompoundTag();
        }

        CompoundTag texture = new CompoundTag();
        texture.put(
            "Value",
            new StringTag(textureHash.isEmpty() ? STEVE_TEXTURE : textureHash));
        ListTag<CompoundTag> textures = new ListTag<>(CompoundTag.class);
        textures.add(texture);
        properties.put("textures", textures);
        skullOwner.put("Properties", properties);
        tag.put("SkullOwner", skullOwner);

    }
}

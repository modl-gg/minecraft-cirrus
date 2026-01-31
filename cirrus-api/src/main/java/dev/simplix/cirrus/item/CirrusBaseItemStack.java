package dev.simplix.cirrus.item;

import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.querz.nbt.tag.CompoundTag;

public interface CirrusBaseItemStack {

    @NonNull
    CirrusItemType itemType();

    byte amount();

    short durability();

    @Nullable
    CirrusChatElement displayName();

    @Nullable
    List<CirrusChatElement> lore();

    @Nullable
    CompoundTag nbtData();

    int hideFlags();

    @Nullable
    Set<ItemFlag> itemFlags();

    enum ItemFlag {
        HIDE_ENCHANTS,
        HIDE_ATTRIBUTES,
        HIDE_UNBREAKABLE,
        HIDE_DESTROYS,
        HIDE_PLACED_ON,
        HIDE_POTION_EFFECTS,
        HIDE_DYE,
        HIDE_ARMOR_TRIM
    }
}

package dev.simplix.cirrus.item;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.NonNull;

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
    NBTCompound nbtData();

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

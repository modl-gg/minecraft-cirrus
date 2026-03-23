package dev.simplix.cirrus.item;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import dev.simplix.cirrus.Utils;
import dev.simplix.cirrus.effect.AbstractMenuEffect;
import dev.simplix.cirrus.text.CirrusChatElement;
import dev.simplix.cirrus.util.ToStringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class CirrusItem implements CirrusBaseItemStack {

    protected CirrusItemType itemType;
    protected byte amount;
    protected short durability;
    protected CirrusChatElement displayName;
    protected List<CirrusChatElement> lore;
    protected NBTCompound nbtData;
    protected int hideFlags;
    protected Set<ItemFlag> itemFlags;

    private String actionHandler = "noAction";
    private transient int slot = -1;
    private List<String> actionArguments = Collections.emptyList();
    @Nullable
    private AbstractMenuEffect<String> displayNameEffect = null;

    public static CirrusItem EMPTY = new CirrusItem();

    public CirrusItem() {
        this(CirrusItemType.AIR);
    }

    public CirrusItem(@NonNull CirrusItemType itemType) {
        this(itemType, (byte) 1);
    }

    public CirrusItem(@NonNull CirrusItemType itemType, int amount) {
        this(itemType, (byte) amount, (short) -1);
    }

    public CirrusItem(@NonNull CirrusItemType itemType, byte amount, short durability) {
        this.itemType = itemType;
        this.amount = amount;
        this.durability = durability;
        this.displayName = CirrusChatElement.empty();
        this.lore = new ArrayList<>();
        this.nbtData = new NBTCompound();
        this.hideFlags = 0;
        this.itemFlags = new HashSet<>();
    }

    public CirrusItem(@NonNull CirrusBaseItemStack base) {
        this.itemType = base.itemType();
        this.amount = base.amount();
        this.durability = base.durability();
        this.displayName = base.displayName();
        this.lore = new ArrayList<>(base.lore());
        this.nbtData = base.nbtData() != null ? cloneNbtCompound(base.nbtData()) : new NBTCompound();
        this.hideFlags = base.hideFlags();
        this.itemFlags = new HashSet<>(base.itemFlags());
    }

    public static CirrusItem of(@NonNull CirrusItemType itemType) {
        return new CirrusItem(itemType);
    }

    public static CirrusItem of(@NonNull String itemTypeIdentifier) {
        return new CirrusItem(CirrusItemType.of(itemTypeIdentifier));
    }

    public static CirrusItem ofSkullHash(@NonNull String skullHash) {
        return new CirrusItem(CirrusItemType.PLAYER_HEAD).texture(skullHash);
    }

    public static CirrusItem of(
        @NonNull CirrusItemType itemType,
        @NonNull CirrusChatElement displayName,
        @NonNull List<CirrusChatElement> lore) {
        return new CirrusItem(itemType).displayName(displayName).loreElements(lore);
    }

    public static CirrusItem of(
        @NonNull CirrusItemType itemType,
        @NonNull CirrusChatElement displayName,
        @NonNull CirrusChatElement... lore) {
        return new CirrusItem(itemType).displayName(displayName).lore(lore);
    }

    public static CirrusItem of(
        @NonNull CirrusItemType itemType,
        @NonNull AbstractMenuEffect<String> displayNameEffect,
        @NonNull CirrusChatElement... lore) {
        return new CirrusItem(itemType).displayNameEffect(displayNameEffect).lore(lore);
    }

    public CirrusItem texture(@NonNull String texture) {
        Utils.texture(this.nbtData, texture);
        return this;
    }

    public CirrusItem itemType(@NonNull CirrusItemType itemType) {
        this.itemType = itemType;
        return this;
    }

    public CirrusItem amount(byte amount) {
        this.amount = amount;
        return this;
    }

    public CirrusItem amount(int amount) {
        this.amount = (byte) amount;
        return this;
    }

    public CirrusItem durability(short durability) {
        this.durability = durability;
        return this;
    }

    public CirrusItem displayName(@Nullable CirrusChatElement displayName) {
        if (displayName == null) {
            return this;
        }
        this.displayName = displayName;
        return this;
    }

    public CirrusItem displayName(@Nullable String legacyText) {
        if (legacyText == null) {
            return this;
        }
        this.displayName = CirrusChatElement.ofLegacyText(legacyText);
        return this;
    }

    @Override
    public List<CirrusChatElement> lore() {
        return this.lore;
    }

    public CirrusItem lore(@NonNull CirrusChatElement... loreElements) {
        this.lore = new ArrayList<>(Arrays.asList(loreElements));
        return this;
    }

    public CirrusItem lore(@NonNull List<CirrusChatElement> loreElements) {
        this.lore = new ArrayList<>(loreElements);
        return this;
    }

    public CirrusItem loreElements(@Nullable List<CirrusChatElement> loreElements) {
        if (loreElements == null) {
            return this;
        }
        this.lore = new ArrayList<>(loreElements);
        return this;
    }

    public CirrusItem addLore(@NonNull CirrusChatElement line) {
        this.lore.add(line);
        return this;
    }

    public CirrusItem addLore(@NonNull String legacyText) {
        this.lore.add(CirrusChatElement.ofLegacyText(legacyText));
        return this;
    }

    public CirrusItem nbtData(@NonNull NBTCompound nbtData) {
        this.nbtData = nbtData;
        return this;
    }

    public CirrusItem hideFlags(int hideFlags) {
        this.hideFlags = hideFlags;
        return this;
    }

    public CirrusItem slot(int slot) {
        this.slot = slot;
        return this;
    }

    public CirrusItem actionHandler(@NonNull String actionHandler) {
        this.actionHandler = actionHandler;
        return this;
    }

    public CirrusItem actionArguments(@NonNull List<String> actionArguments) {
        this.actionArguments = actionArguments;
        return this;
    }

    public CirrusItem hideNbtFlags() {
        Utils.hideNbtFlags(this.nbtData);
        return this;
    }

    public CirrusItem glow() {
        Utils.glow(this.nbtData);
        return this;
    }

    public CirrusItem deepClone() {
        CirrusItem clone = new CirrusItem(this.itemType, this.amount, this.durability);
        clone.displayName = this.displayName;
        clone.lore = new ArrayList<>(this.lore);
        clone.nbtData = cloneNbtCompound(this.nbtData);
        clone.hideFlags = this.hideFlags;
        clone.itemFlags = new HashSet<>(this.itemFlags);
        clone.actionHandler = this.actionHandler;
        clone.slot = this.slot;
        clone.actionArguments = new ArrayList<>(this.actionArguments);
        clone.displayNameEffect = this.displayNameEffect;
        return clone;
    }

    private static NBTCompound cloneNbtCompound(NBTCompound source) {
        if (source == null) {
            return new NBTCompound();
        }
        return (NBTCompound) source.copy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.itemType,
            this.amount,
            this.durability,
            this.nbtData,
            this.actionHandler,
            this.lore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof CirrusItem)) {
            if (o instanceof CirrusBaseItemStack) {
                CirrusBaseItemStack base = (CirrusBaseItemStack) o;
                return Objects.equals(itemType, base.itemType()) &&
                       amount == base.amount() &&
                       durability == base.durability();
            }
            return false;
        }

        CirrusItem item = (CirrusItem) o;
        if (!Objects.equals(lore, item.lore)) return false;
        if (!Objects.equals(itemType, item.itemType)) return false;
        if (!Objects.equals(actionHandler, item.actionHandler)) return false;
        if (!Objects.equals(actionArguments, item.actionArguments)) return false;
        return Objects.equals(displayNameEffect, item.displayNameEffect);
    }

    @Override
    public String toString() {
        return ToStringUtil.of("CirrusItem")
            .add("displayName", this.displayName)
            .add("displayNameEffect", this.displayNameEffect == null ? "null" : this.displayNameEffect.toString())
            .add("itemType", this.itemType)
            .add("lore", this.lore)
            .add("amount", this.amount)
            .add("hideFlags", this.hideFlags)
            .add("actionArguments", this.actionArguments)
            .toString();
    }
}

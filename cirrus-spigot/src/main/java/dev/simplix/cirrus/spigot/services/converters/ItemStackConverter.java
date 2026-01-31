package dev.simplix.cirrus.spigot.services.converters;

import static dev.simplix.cirrus.protocol.CirrusProtocolVersions.MINECRAFT_1_13;
import static dev.simplix.cirrus.protocol.CirrusProtocolVersions.MINECRAFT_1_14;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.spigot.util.ComponentHelper;
import dev.simplix.cirrus.spigot.util.ProtocolVersionUtil;
import dev.simplix.cirrus.spigot.util.ReflectionClasses;
import dev.simplix.cirrus.spigot.util.ReflectionUtil;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@Slf4j
public class ItemStackConverter implements Function<CirrusBaseItemStack, ItemStack> {

    private static Class<?> craftItemStackClass;
    private static Class<?> nbtTagCompoundClass;
    private static Class<?> itemStackNMSClass;
    private static Method nmsCopyMethod;
    private static Method bukkitCopyMethod;
    private static Method setTagMethod;
    private static boolean nbtReflectionAvailable = true;

    static {
        try {
            craftItemStackClass = ReflectionUtil.getClass("{obc}.inventory.CraftItemStack");
            itemStackNMSClass = ReflectionClasses.itemStackClass();
            nmsCopyMethod = craftItemStackClass.getMethod(
                "asNMSCopy",
                ItemStack.class);
            bukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", itemStackNMSClass);
        } catch (Exception exception) {
            log.warn("[Cirrus] Could not initialize CraftItemStack reflection, using fallback mode", exception);
            nbtReflectionAvailable = false;
        }
        try {
            nbtTagCompoundClass = ReflectionClasses.nbtTagCompound();
        } catch (Exception exception) {
            log.info("[Cirrus] NBTTagCompound not available (1.20.5+ uses Data Components), using Bukkit API");
            nbtReflectionAvailable = false;
        }
    }

    @Override
    public ItemStack apply(CirrusBaseItemStack cirrusItemStack) {
        if (cirrusItemStack.itemType() == null) {
            return new ItemStack(Material.AIR);
        }

        Material material = Cirrus
            .service(ItemTypeMaterialConverter.class).apply(cirrusItemStack.itemType());
        ItemStack itemStack;

        if (ProtocolVersionUtil.serverProtocolVersion() < MINECRAFT_1_13) {
            itemStack = new ItemStack(
                material,
                cirrusItemStack.amount(),
                cirrusItemStack.durability(),
                (byte) 0);
        } else {
            itemStack = new ItemStack(
                Cirrus.service(ItemTypeMaterialConverter.class).apply(cirrusItemStack.itemType()),
                cirrusItemStack.amount(),
                cirrusItemStack.durability());
        }

        CompoundTag nbtData = cirrusItemStack.nbtData();
        if (nbtData == null) {
            nbtData = new CompoundTag();
        }

        String textureHashToInsert = null;

        if (nbtData != null && !nbtData.keySet().isEmpty()) {
            if (nbtData.containsKey("SkullOwner") && nbtData.get("SkullOwner") instanceof CompoundTag) {
                final CompoundTag skullOwnerTag = nbtData.getCompoundTag("SkullOwner");
                final Tag<?> propertiesRaw = skullOwnerTag.get("Properties");

                if (propertiesRaw instanceof CompoundTag propertiesTag) {
                    @SuppressWarnings("unchecked")
                    final ListTag<CompoundTag> textures = (ListTag<CompoundTag>) propertiesTag.getListTag("textures");
                    if (textures != null && textures.size() > 0) {
                        textureHashToInsert = textures.get(0).getString("Value");
                    }
                }
            }
        }

        writeLoreAndDisplayNameToNbt(cirrusItemStack, nbtData);

        try {
            ItemStack result;

            if (nbtReflectionAvailable && nmsCopyMethod != null) {
                Object nmsItemStack = nmsCopyMethod.invoke(null, itemStack);
                if (nbtData != null && !nbtData.keySet().isEmpty()) {
                    try {
                        Method setTag = setTagMethod();
                        if (setTag != null) {
                            final CompoundTag nbtTag = nbtData.clone();
                            if (textureHashToInsert != null) {
                                nbtTag.remove("SkullOwner");
                            }
                            setTag.invoke(nmsItemStack, Cirrus.service(QuerzNbtNmsNbtConverter.class).apply(nbtTag));
                        }
                    } catch (Throwable throwable) {
                        log.debug("Could not set NBT tag via reflection, falling back to Bukkit API", throwable);
                    }
                }
                result = (ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack);
            } else {
                result = itemStack;
            }

            final ItemMeta itemMeta = result.getItemMeta();

            mutateMetaDataToHideAttributes(itemMeta);

            result.setType(material);
            if (cirrusItemStack.displayName() != null && !cirrusItemStack.displayName().isEmpty()) {
                String displayName = convertToLegacyText(cirrusItemStack.displayName().asComponent());
                if (displayName != null) {
                    itemMeta.setDisplayName(displayName);
                }
            }

            if (cirrusItemStack.lore() != null && !cirrusItemStack.lore().isEmpty()) {
                List<String> loreLines = new ArrayList<>();
                for (var loreLine : cirrusItemStack.lore()) {
                    if (loreLine != null && !loreLine.isEmpty()) {
                        String loreText = convertToLegacyText(loreLine.asComponent());
                        if (loreText != null) {
                            loreLines.add(loreText);
                        }
                    }
                }
                if (!loreLines.isEmpty()) {
                    itemMeta.setLore(loreLines);
                }
            }

            if (textureHashToInsert != null && itemMeta instanceof SkullMeta skullMeta) {
                mutateItemMetaForTextureHash(skullMeta, textureHashToInsert);
            }

            result.setItemMeta(itemMeta);

            return result;
        } catch (final Exception exception) {
            throw new IllegalStateException("Could not fully execute copying operations ", exception);
        }
    }

    private Method setTagMethod() {
        if (setTagMethod != null) {
            return setTagMethod;
        }
        if (!nbtReflectionAvailable || itemStackNMSClass == null || nbtTagCompoundClass == null) {
            return null;
        }
        try {
            setTagMethod = itemStackNMSClass.getMethod("setTag", nbtTagCompoundClass);
            return setTagMethod;
        } catch (NoSuchMethodException e) {
            try {
                setTagMethod = itemStackNMSClass.getDeclaredMethod("setTagClone", nbtTagCompoundClass);
                setTagMethod.setAccessible(true);
                return setTagMethod;
            } catch (NoSuchMethodException ex) {
                log.debug("[Cirrus] NBT setTag methods not available, server likely uses Data Components (1.20.5+)");
                nbtReflectionAvailable = false;
                return null;
            }
        }
    }

    private void mutateMetaDataToHideAttributes(final ItemMeta itemMeta) {
        tryAddItemFlag(itemMeta, "HIDE_ENCHANTS");
        tryAddItemFlag(itemMeta, "HIDE_ATTRIBUTES");
        tryAddItemFlag(itemMeta, "HIDE_UNBREAKABLE");
        tryAddItemFlag(itemMeta, "HIDE_DESTROYS");
        tryAddItemFlag(itemMeta, "HIDE_PLACED_ON");
        tryAddItemFlag(itemMeta, "HIDE_POTION_EFFECTS");
        tryAddItemFlag(itemMeta, "HIDE_ADDITIONAL_TOOLTIP");
        tryAddItemFlag(itemMeta, "HIDE_DYE");
        tryAddItemFlag(itemMeta, "HIDE_ARMOR_TRIM");
    }

    private void tryAddItemFlag(ItemMeta itemMeta, String flagName) {
        try {
            ItemFlag flag = ItemFlag.valueOf(flagName);
            itemMeta.addItemFlags(flag);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private String convertToLegacyText(Object componentObj) {
        try {
            if (componentObj instanceof BaseComponent[] baseComponents) {
                return TextComponent.toLegacyText(baseComponents);
            } else if (componentObj instanceof BaseComponent baseComponent) {
                return baseComponent.toLegacyText();
            } else if (componentObj instanceof Component adventureComponent) {
                return GsonComponentSerializer.gson().serialize(adventureComponent);
            } else {
                log.debug("[Cirrus] Unknown component type: {}", componentObj.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            log.debug("[Cirrus] Could not convert component to legacy text", e);
            return null;
        }
    }

    private void mutateItemMetaForTextureHash(SkullMeta meta, String textureHash) {
        try {
            Method metaSetProfileMethod = meta
                .getClass()
                .getDeclaredMethod("setProfile", GameProfile.class);
            metaSetProfileMethod.setAccessible(true);
            metaSetProfileMethod.invoke(meta, makeProfile(textureHash));
        } catch (NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException reflectiveOperationException) {
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, makeProfile(textureHash));

            } catch (NoSuchFieldException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

    private GameProfile makeProfile(@NonNull String textureHash) {
        UUID id = new UUID(
            textureHash.substring(textureHash.length() - 20).hashCode(),
            textureHash.substring(textureHash.length() - 10).hashCode()
        );
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", textureHash));
        return profile;
    }

    private void writeLoreAndDisplayNameToNbt(@NonNull CirrusBaseItemStack stack, @NonNull CompoundTag nbtData) {
        CirrusChatElement displayName = stack.displayName();
        if (displayName != null && !displayName.isEmpty()) {
            if (ProtocolVersionUtil.serverProtocolVersion() >= MINECRAFT_1_13) {
                nbtData.put("Damage", new IntTag(stack.durability()));
                Object component = displayName.asComponent();
                if (component instanceof BaseComponent[] baseComponents) {
                    ComponentHelper.removeItalic(baseComponents);
                    setDisplayNameTag(nbtData, ComponentSerializer.toString(baseComponents));
                } else if (component instanceof Component adventureComponent) {
                    setDisplayNameTag(nbtData, GsonComponentSerializer.gson().serialize(adventureComponent));
                }
            } else {
                Object component = displayName.asComponent();
                if (component instanceof BaseComponent[] baseComponents) {
                    setDisplayNameTag(nbtData, TextComponent.toLegacyText(baseComponents));
                }
            }
        }

        List<CirrusChatElement> lore = stack.lore();
        if (lore != null && !lore.isEmpty()) {
            setLoreTag(nbtData, lore, ProtocolVersionUtil.serverProtocolVersion());
        }
    }

    private void setDisplayNameTag(@NonNull CompoundTag nbtData, @NonNull String name) {
        if (name == null) {
            return;
        }
        CompoundTag display = (CompoundTag) nbtData.get("display");
        if (display == null) {
            display = new CompoundTag();
        }
        final StringTag tag = new StringTag(name);
        display.put("Name", tag);
        nbtData.put("display", display);
    }

    private void setLoreTag(
        @NonNull CompoundTag nbtData,
        @NonNull List<CirrusChatElement> lore,
        int protocolVersion) {
        CompoundTag display = (CompoundTag) nbtData.get("display");
        if (display == null) {
            display = new CompoundTag();
        }
        final ListTag<StringTag> tag = new ListTag<>(StringTag.class);
        if (protocolVersion < MINECRAFT_1_14) {
            tag.addAll(lore.stream()
                .filter(element -> element != null && !element.isEmpty())
                .map(element -> {
                    Object component = element.asComponent();
                    if (component instanceof BaseComponent[] baseComponents) {
                        return new StringTag(TextComponent.toLegacyText(baseComponents));
                    }
                    return new StringTag("");
                })
                .collect(Collectors.toList()));
        } else {
            tag.addAll(lore.stream()
                .filter(element -> element != null && !element.isEmpty())
                .map(element -> {
                    Object component = element.asComponent();
                    if (component instanceof BaseComponent[] baseComponents) {
                        for (BaseComponent baseComponent : baseComponents) {
                            if (!baseComponent.isItalic()) {
                                baseComponent.setItalic(false);
                            }
                        }
                        return new StringTag(ComponentSerializer.toString(baseComponents));
                    } else if (component instanceof Component adventureComponent) {
                        return new StringTag(GsonComponentSerializer.gson().serialize(adventureComponent));
                    }
                    return new StringTag("");
                })
                .collect(Collectors.toList()));
        }
        display.put("Lore", tag);
        nbtData.put("display", display);
    }
}
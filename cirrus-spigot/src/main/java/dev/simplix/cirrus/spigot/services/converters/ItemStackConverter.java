package dev.simplix.cirrus.spigot.services.converters;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_13;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_14;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.spigot.util.*;
import dev.simplix.protocolize.api.item.BaseItemStack;

import java.lang.reflect.*;
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
import net.querz.nbt.tag.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@Slf4j
public class ItemStackConverter implements Function<BaseItemStack, org.bukkit.inventory.ItemStack> {

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
          org.bukkit.inventory.ItemStack.class);
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
  public org.bukkit.inventory.ItemStack apply(BaseItemStack protocolizeItemStack) {
    if (protocolizeItemStack.itemType() == null) {
      return new org.bukkit.inventory.ItemStack(Material.AIR);
    }

    Material material = Cirrus
        .service(ItemTypeMaterialConverter.class).apply(protocolizeItemStack.itemType());
    org.bukkit.inventory.ItemStack itemStack;

    if (ProtocolVersionUtil.serverProtocolVersion() < MINECRAFT_1_13) {
      itemStack = new org.bukkit.inventory.ItemStack(
          material,
          protocolizeItemStack.amount(),
          protocolizeItemStack.durability(),
          (byte) 0);
    } else {
      itemStack = new org.bukkit.inventory.ItemStack(
          Cirrus.service(ItemTypeMaterialConverter.class).apply(protocolizeItemStack.itemType()),
          protocolizeItemStack.amount(),
          protocolizeItemStack.durability());
    }

    if (protocolizeItemStack.nbtData() == null) {
      protocolizeItemStack.nbtData(new CompoundTag());
    }

    String textureHashToInsert = null;

    if (protocolizeItemStack.nbtData() != null && !protocolizeItemStack
        .nbtData()
        .keySet()
        .isEmpty()) {
      CompoundTag tag = protocolizeItemStack.nbtData();
      if (tag.containsKey("SkullOwner") && tag.get("SkullOwner") instanceof CompoundTag) {

        final CompoundTag skullOwnerTag = tag.getCompoundTag("SkullOwner");
        final Tag<?> propertiesRaw = skullOwnerTag.get("Properties");

        if (propertiesRaw instanceof CompoundTag) {
          final ListTag<CompoundTag> textures = (ListTag<CompoundTag>) ((CompoundTag) propertiesRaw)
              .getListTag("textures");
          textureHashToInsert = textures.get(0).getString("Value");
        }
      }
    }

    writeLoreAndDisplayNameToStack(protocolizeItemStack);

    // Finalizing the itemstack by inserting nbt material & hiding attributes
    try {
      ItemStack result;

      if (nbtReflectionAvailable && nmsCopyMethod != null) {
        // Traditional NMS-based NBT handling (pre-1.20.5)
        Object nmsItemStack = nmsCopyMethod.invoke(null, itemStack);
        if (protocolizeItemStack.nbtData() != null && !protocolizeItemStack
            .nbtData()
            .keySet()
            .isEmpty()) {
          try {
            Method setTag = setTagMethod();
            if (setTag != null) {
              final CompoundTag nbtTag = protocolizeItemStack.nbtData().clone();
              if (textureHashToInsert != null) {
                nbtTag.remove("SkullOwner");
              }
              setTag.invoke(nmsItemStack, Cirrus.service(QuerzNbtNmsNbtConverter.class).apply(nbtTag));
            }
          } catch (Throwable throwable) {
            log.debug("Could not set NBT tag via reflection, falling back to Bukkit API", throwable);
          }
        }
        result = (org.bukkit.inventory.ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack);
      } else {
        // Modern Bukkit API fallback (1.20.5+)
        result = itemStack;
      }

      final ItemMeta itemMeta = result.getItemMeta();

      mutateMetaDataToHideAttributes(itemMeta);

      // Apply special 'precautions' against NMS.
      result.setType(material);
      if (protocolizeItemStack.displayName() != null && protocolizeItemStack.displayName().asComponent() != null) {
        Component adventureComponent = convertToAdventureComponent(protocolizeItemStack.displayName().asComponent());
        if (adventureComponent != null) {
          itemMeta.displayName(adventureComponent);
        }
      }

      // Set lore via Bukkit API (especially important for 1.20.5+ where NBT doesn't work)
      if (protocolizeItemStack.lore() != null && !protocolizeItemStack.lore().isEmpty()) {
        List<Component> adventureLore = new ArrayList<>();
        for (var loreLine : protocolizeItemStack.lore()) {
          if (loreLine != null && loreLine.asComponent() != null) {
            Component loreComponent = convertToAdventureComponent(loreLine.asComponent());
            if (loreComponent != null) {
              adventureLore.add(loreComponent);
            }
          }
        }
        if (!adventureLore.isEmpty()) {
          itemMeta.lore(adventureLore);
        }
      }

      // No texture-hash to insert
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
        // 1.20.5+ uses Data Components, setTag methods don't exist
        log.debug("[Cirrus] NBT setTag methods not available, server likely uses Data Components (1.20.5+)");
        nbtReflectionAvailable = false;
        return null;
      }
    }
  }

  private void mutateMetaDataToHideAttributes(final ItemMeta itemMeta) {
    // Add each flag individually with try-catch to handle version differences
    // (e.g., HIDE_POTION_EFFECTS was renamed to HIDE_ADDITIONAL_TOOLTIP in 1.20.5+)
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
      // Flag doesn't exist in this version
    }
  }

  /**
   * Converts a BungeeCord BaseComponent (or array) to Adventure Component.
   * Protocolize uses BungeeCord chat API, but Paper 1.16.5+ uses Adventure.
   */
  private Component convertToAdventureComponent(Object componentObj) {
    try {
      String json;
      if (componentObj instanceof BaseComponent[] baseComponents) {
        json = ComponentSerializer.toString(baseComponents);
      } else if (componentObj instanceof BaseComponent baseComponent) {
        json = ComponentSerializer.toString(baseComponent);
      } else if (componentObj instanceof Component) {
        // Already an Adventure component
        return (Component) componentObj;
      } else {
        log.debug("[Cirrus] Unknown component type: {}", componentObj.getClass().getName());
        return null;
      }
      return GsonComponentSerializer.gson().deserialize(json);
    } catch (Exception e) {
      log.debug("[Cirrus] Could not convert component to Adventure format", e);
      return null;
    }
  }

  private GameProfile makeProfile(@NonNull String textureHash) {
    // random uuid based on the textureHash string
    UUID id = new UUID(
        textureHash.substring(textureHash.length() - 20).hashCode(),
        textureHash.substring(textureHash.length() - 10).hashCode()
    );
    GameProfile profile = new GameProfile(id, "Player");
    profile.getProperties().put("textures", new Property("textures", textureHash));
    return profile;
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
      // if in an older API where there is no setProfile method,
      // we set the profile field directly.
      try {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);
        profileField.set(meta, makeProfile(textureHash));

      } catch (NoSuchFieldException | IllegalAccessException exception) {
        exception.printStackTrace();
      }
    }
  }

  // TODO: THIS IS UNTESTED -> ONLY HERE TO COMPILE
  private void writeLoreAndDisplayNameToStack(@NonNull BaseItemStack stack) {
    if (stack.displayName() != null) {
      if (ProtocolVersionUtil.serverProtocolVersion() >= MINECRAFT_1_13) {
        stack.nbtData().put("Damage", new IntTag(stack.durability()));
        final BaseComponent[] baseComponents = (BaseComponent[]) stack.displayName().asComponent(); // This is were crappy adventure components come into play
        ComponentHelper.removeItalic(baseComponents);
        setDisplayNameTag(stack.nbtData(), ComponentSerializer.toString(baseComponents));
      } else {
        setDisplayNameTag(
            stack.nbtData(),
            TextComponent.toLegacyText((BaseComponent) stack.displayName().asComponent()));
      }
    }

    if (stack.lore() != null) {
      setLoreTag(stack.nbtData(), new ArrayList<>(stack.lore().stream().map(i -> (BaseComponent[]) i.asComponent()).toList()),
              ProtocolVersionUtil.serverProtocolVersion());
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
      @NonNull List<BaseComponent[]> lore,
      int protocolVersion) {
    CompoundTag display = (CompoundTag) nbtData.get("display");
    if (display == null) {
      display = new CompoundTag();
    }
    final ListTag<StringTag> tag = new ListTag<>(StringTag.class);
    if (protocolVersion < MINECRAFT_1_14) {
      tag.addAll(lore.stream().map(i -> new StringTag(TextComponent.toLegacyText(i))).collect(
          Collectors.toList()));
    } else {
      tag.addAll(lore.stream().map(components -> {
        for (BaseComponent component : components) {
          if (!component.isItalic()) {
            component.setItalic(false);
          }
        }
        return new StringTag(ComponentSerializer.toString(components));
      }).collect(Collectors.toList()));
    }
    display.put("Lore", tag);
    nbtData.put("display", display);
  }
}
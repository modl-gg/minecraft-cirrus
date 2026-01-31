package dev.simplix.cirrus.spigot.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectionClasses {

  public static Class<?> itemStackClass() throws ClassNotFoundException {
    if (ReflectionUtil.hasNewPackageStructure()) {
      return ReflectionUtil
          .getClass("{nm}.world.item.ItemStack");

    } else {
      return ReflectionUtil
          .getClass("{nms}.ItemStack");
    }
  }

  public static Class<?> nbtCompressedStreamTools() throws ClassNotFoundException {
    if (ReflectionUtil.hasNewPackageStructure()) {
      // Try the standard location first
      try {
        return ReflectionUtil.getClass("{nm}.nbt.NBTCompressedStreamTools");
      } catch (ClassNotFoundException e) {
        // Try alternate location/name for newer versions
        try {
          return ReflectionUtil.getClass("{nm}.nbt.NbtIo");
        } catch (ClassNotFoundException e2) {
          log.debug("[Cirrus] NBT stream tools not found at expected locations");
          throw e;
        }
      }
    } else {
      return ReflectionUtil
          .getClass("{nms}.NBTCompressedStreamTools");
    }
  }

  public static Class<?> nbtTagCompound() throws ClassNotFoundException {
    if (ReflectionUtil.hasNewPackageStructure()) {
      return ReflectionUtil
          .getClass("{nm}.nbt.NBTTagCompound");

    } else {
      return ReflectionUtil
          .getClass("{nms}.NBTTagCompound");
    }
  }

}

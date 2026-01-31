package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.spigot.util.ReflectionClasses;
import dev.simplix.cirrus.spigot.util.ReflectionUtil;
import java.lang.reflect.Method;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.inventory.ItemStack;

@Slf4j
public class BukkitItemStackConverter implements Function<ItemStack, CirrusItem> {

    private static Class<?> craftItemStackClass;
    private static Class<?> itemStackNMSClass;
    private static Method getTagMethod;
    private static boolean nbtReflectionAvailable = true;

    static {
        try {
            craftItemStackClass = ReflectionUtil.getClass("{obc}.inventory.CraftItemStack");
            itemStackNMSClass = ReflectionClasses.itemStackClass();
            try {
                getTagMethod = itemStackNMSClass.getMethod("getTag");
            } catch (NoSuchMethodException e) {
                log.info("[Cirrus] NBT getTag method not available (1.20.5+ uses Data Components)");
                nbtReflectionAvailable = false;
            }
        } catch (Exception exception) {
            log.warn("[Cirrus] Could not initialize BukkitItemStackConverter reflection, using fallback mode", exception);
            nbtReflectionAvailable = false;
        }
    }

    @Override
    public CirrusItem apply(@NonNull ItemStack src) {
        try {
            CirrusItem out = new CirrusItem(
                Cirrus.service(MaterialDataItemTypeConverter.class).apply(src.getData()),
                (byte) src.getAmount(),
                src.getDurability());

            if (nbtReflectionAvailable && craftItemStackClass != null && getTagMethod != null) {
                try {
                    Object handle = ReflectionUtil.fieldValue(craftItemStackClass, src, "handle");
                    if (handle != null) {
                        Object nbtTag = getTagMethod.invoke(handle);
                        if (nbtTag != null) {
                            out.nbtData(Cirrus.service(NmsNbtQuerzNbtConverter.class).apply(nbtTag));
                        }
                    }
                } catch (Exception e) {
                    log.debug("[Cirrus] Could not extract NBT data from item stack", e);
                }
            }

            return out;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Could not convert item stack", exception);
        }
    }

}

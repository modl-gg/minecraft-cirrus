package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.spigot.util.ReflectionClasses;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.CompoundTag;

@Slf4j
public class NmsNbtQuerzNbtConverter implements Function<Object, CompoundTag> {

    private static Method nbtCompressedStreamToolWriteMethod;
    private static Class<?> nbtTagCompoundClass;
    private static boolean available = true;

    static {
        try {
            Class<?> nbtCompressedStreamToolsClass = ReflectionClasses.nbtCompressedStreamTools();
            nbtTagCompoundClass = ReflectionClasses.nbtTagCompound();
            nbtCompressedStreamToolWriteMethod = findWriteMethod(nbtCompressedStreamToolsClass, nbtTagCompoundClass);
            if (nbtCompressedStreamToolWriteMethod == null) {
                log.info("[Cirrus] NBT write method not found, NmsNbtQuerzNbtConverter will be unavailable");
                available = false;
            }
        } catch (final Exception exception) {
            log.info("[Cirrus] Could not initialize NmsNbtQuerzNbtConverter (1.20.5+ uses Data Components)", exception);
            available = false;
        }
    }

    private static Method findWriteMethod(Class<?> clazz, Class<?> nbtClass) {
        // Try method name 'a' (obfuscated, used in most versions)
        try {
            return clazz.getMethod("a", nbtClass, OutputStream.class);
        } catch (NoSuchMethodException ignored) {
        }

        // Try method name 'writeCompressed'
        try {
            return clazz.getMethod("writeCompressed", nbtClass, OutputStream.class);
        } catch (NoSuchMethodException ignored) {
        }

        // Try all public methods with matching signature
        for (Method method : clazz.getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 2
                && nbtClass.isAssignableFrom(params[0])
                && OutputStream.class.isAssignableFrom(params[1])) {
                return method;
            }
        }

        return null;
    }

    /**
     * Returns whether this converter is available on the current server version.
     */
    public static boolean isAvailable() {
        return available;
    }

    @Override
    public CompoundTag apply(@NonNull Object src) {
        if (!available || nbtCompressedStreamToolWriteMethod == null) {
            return null;
        }

        byte[] data;
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            nbtCompressedStreamToolWriteMethod.invoke(null, src, byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();
        } catch (final Exception exception) {
            log.debug("[Cirrus] Could not write NBT data", exception);
            return null;
        }

        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            return (CompoundTag) new NBTInputStream(byteArrayInputStream).readTag(99).getTag();
        } catch (final IOException ioException) {
            log.debug("[Cirrus] Could not read NBT data", ioException);
        }
        return null;
    }

}

package dev.simplix.cirrus.spigot.services.converters;

import dev.simplix.cirrus.spigot.util.ReflectionClasses;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;
import lombok.NonNull;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.CompoundTag;

public class QuerzNbtNmsNbtConverter implements Function<CompoundTag, Object> {

    private static Method nbtCompressedStreamToolAMethod;
    private static boolean available = true;

    static {
        try {
            Class<?> nbtCompressedStreamToolsClass = ReflectionClasses.nbtCompressedStreamTools();
            // Try different method names used across versions
            nbtCompressedStreamToolAMethod = findReadMethod(nbtCompressedStreamToolsClass);
        } catch (final Exception e) {
            available = false;
        }
    }

    private static Method findReadMethod(Class<?> clazz) {
        // Try method name 'a' (obfuscated, used in most versions)
        try {
            return clazz.getMethod("a", InputStream.class);
        } catch (NoSuchMethodException ignored) {
        }

        // Try method name 'readCompressed' (deobfuscated name in some versions)
        try {
            return clazz.getMethod("readCompressed", InputStream.class);
        } catch (NoSuchMethodException ignored) {
        }

        // Try all public methods with matching signature
        for (Method method : clazz.getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1 && InputStream.class.isAssignableFrom(params[0])) {
                return method;
            }
        }

        return null;
    }

    @Override
    public Object apply(@NonNull CompoundTag src) {
        if (!isAvailable()) {
            return null;
        }
        byte[] data = null;
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            new NBTOutputStream(gzipOutputStream).writeTag(src, 99);
            gzipOutputStream.close();
            data = byteArrayOutputStream.toByteArray();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if (data == null) {
            return null;
        }
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            return nbtCompressedStreamToolAMethod.invoke(null, byteArrayInputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Returns whether this converter is available on the current server version.
     */
    public static boolean isAvailable() {
        return available && nbtCompressedStreamToolAMethod != null;
    }

}

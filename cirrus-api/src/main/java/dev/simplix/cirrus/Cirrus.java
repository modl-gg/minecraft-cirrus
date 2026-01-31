package dev.simplix.cirrus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.simplix.cirrus.effect.AbstractMenuEffect;
import dev.simplix.cirrus.effects.SimpleChangingItemEffect;
import dev.simplix.cirrus.effects.SpectrumEffect;
import dev.simplix.cirrus.effects.WaveEffect;
import dev.simplix.cirrus.gson.CirrusItemDeserializer;
import dev.simplix.cirrus.gson.CirrusItemSerializer;
import dev.simplix.cirrus.gson.ColorDeserializer;
import dev.simplix.cirrus.gson.ColorSerializer;
import dev.simplix.cirrus.gson.ItemStackDeserializer;
import dev.simplix.cirrus.gson.ItemStackSerializer;
import dev.simplix.cirrus.gson.RuntimeTypeAdapterFactory;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.mojang.TextureService;
import dev.simplix.cirrus.mojang.UUIDNameService;
import dev.simplix.cirrus.service.CirrusServiceRegistry;
import dev.simplix.cirrus.service.ColorConvertService;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.cirrus.service.MenuBuildService;
import java.awt.Color;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cirrus {

    private static final Executor executor = Executors.newCachedThreadPool();
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(CirrusBaseItemStack.class, new ItemStackDeserializer())
        .registerTypeAdapter(CirrusBaseItemStack.class, new ItemStackSerializer())
        .registerTypeHierarchyAdapter(CirrusItem.class, new CirrusItemSerializer())
        .registerTypeHierarchyAdapter(CirrusItem.class, new CirrusItemDeserializer())
        .registerTypeAdapter(Color.class, new ColorSerializer())
        .registerTypeAdapter(Color.class, new ColorDeserializer())
        .registerTypeAdapterFactory(animationTypeAdapterFactory())
        .registerTypeAdapterFactory(itemStackTypeAdapterFactory())
        .setPrettyPrinting()
        .serializeNulls()
        .create();
    // On normal platforms displaying menu async is not a problem.
    // Only Spigot based platforms seem to have issues with it.
    // We work around this by disabling async for Spigot.
    private static boolean canDisplayAsync = true;
    private static boolean canUpdateAsync = true;
    private static boolean isSpigot = false;
    private static MenuBuildService menuBuildService;

    public static void init() {
        Cirrus.registerService(TextureService.class, new TextureService());
        Cirrus.registerService(UUIDNameService.class, new UUIDNameService());
        Cirrus.registerService(ItemService.class, new ItemService());
        Cirrus.registerService(ColorConvertService.class, new ColorConvertService() {
            @Override
            public String colorToString(Color color) {
                String input = "#" + String.format("%08x", color.getRGB()).substring(2);
                StringBuilder output = new StringBuilder("§x");

                for (char c : input.substring(1).toCharArray()) {
                    output.append("§").append(c);
                }
                return output.toString();

            }

            @Override
            public Color stringToColor(String hexColor) {
                String replace = hexColor.replace("&", "§").replace("§x", "").replace("§", "");
                int rgb = Integer.parseInt(replace, 16);

                return new Color(rgb);
            }
        });
    }

    public static <T> void registerService(Class<T> clazz, T implementation) {
        CirrusServiceRegistry.register(clazz, implementation);
    }

    public static <T> T service(Class<T> clazz) {
        return CirrusServiceRegistry.require(clazz);
    }

    public static Gson gson() {
        return Objects.requireNonNull(GSON, "GSON must not be null");
    }

    public static Locale defaultLocale() {
        return Objects.requireNonNull(DEFAULT_LOCALE, "DEFAULT_LOCALE must not be null");
    }

    public static boolean canUpdateAsync() {
        return canUpdateAsync;
    }

    public static boolean canDisplayAsync() {
        return canDisplayAsync;
    }

    public static void canUpdateAsync(boolean canDisplayAsync) {
        Cirrus.canUpdateAsync = canDisplayAsync;
    }

    public static void canDisplayAsync(boolean canDisplayAsync) {
        Cirrus.canDisplayAsync = canDisplayAsync;
    }

    public static Executor executor() {
        return Objects.requireNonNull(executor, "executer must not be null");
    }

    public static RuntimeTypeAdapterFactory<?> animationTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
            .of(AbstractMenuEffect.class)
            .registerSubtype(
                SpectrumEffect.class,
                SpectrumEffect.class.getSimpleName().toLowerCase())
            .registerSubtype(
                WaveEffect.class,
                WaveEffect.class.getSimpleName().toLowerCase())
            .registerSubtype(
                SimpleChangingItemEffect.class,
                SimpleChangingItemEffect.class.getSimpleName().toLowerCase());
    }

    public static RuntimeTypeAdapterFactory<?> itemStackTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
            .of(CirrusBaseItemStack.class)
            .registerSubtype(
                CirrusItem.class,
                CirrusItem.class.getSimpleName().toLowerCase());
    }

    /**
     * Spigot behaves very differently from Velocity, BungeeCord. So we need to know if we are
     * running on Spigot or not.
     *
     * @return True if Spigot, false otherwise
     */
    public static boolean isSpigot() {
        return isSpigot;
    }

    /**
     * Set whether we are running on Spigot or not.
     * <p>
     * Spigot behaves very differently from Velocity, BungeeCord. * So we need to know if we are
     * running on Spigot or not.
     *
     * @param isSpigot True if Spigot, false otherwise
     */
    public static void isSpigot(boolean isSpigot) {
        Cirrus.isSpigot = isSpigot;
    }

    public MenuBuildService menuBuildService() {

        if (menuBuildService == null) {
            throw new IllegalArgumentException("MenuBuildService not initialized yet");
        }

        return menuBuildService;
    }
}

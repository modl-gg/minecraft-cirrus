package dev.simplix.cirrus.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import lombok.NonNull;

public final class CirrusServiceRegistry {

    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    private CirrusServiceRegistry() {
    }

    public static <T> void register(@NonNull Class<T> serviceClass, @NonNull T implementation) {
        SERVICES.put(serviceClass, implementation);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T get(@NonNull Class<T> serviceClass) {
        return (T) SERVICES.get(serviceClass);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> T require(@NonNull Class<T> serviceClass) {
        T service = (T) SERVICES.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("No service registered for " + serviceClass.getName());
        }
        return service;
    }

    public static boolean isRegistered(@NonNull Class<?> serviceClass) {
        return SERVICES.containsKey(serviceClass);
    }

    public static void unregister(@NonNull Class<?> serviceClass) {
        SERVICES.remove(serviceClass);
    }

    public static void clear() {
        SERVICES.clear();
    }
}

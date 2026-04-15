package dev.simplix.cirrus.fabric.listener;

import dev.simplix.cirrus.common.packet.AbstractInventoryPacketListener;
import dev.simplix.cirrus.fabric.menubuilder.FabricMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricInventoryPacketListener extends AbstractInventoryPacketListener {

    public FabricInventoryPacketListener(InventoryTracker inventoryTracker, FabricMenuBuildService menuBuildService) {
        super(inventoryTracker, menuBuildService);
    }

    @Override
    protected UUID getPlayerUuid(Object playerHandle) {
        Object normalizedHandle = normalizePlayerHandle(playerHandle);
        if (!(normalizedHandle instanceof ServerPlayerEntity)) {
            return null;
        }
        return ((ServerPlayerEntity) normalizedHandle).getUuid();
    }

    @Override
    protected Object normalizePlayerHandle(Object playerHandle) {
        if (playerHandle == null || playerHandle instanceof ServerPlayerEntity) {
            return playerHandle;
        }

        ServerPlayerEntity player = invokePlayerGetter(playerHandle, "getPlayer");
        if (player != null) {
            return player;
        }

        player = invokePlayerGetter(playerHandle, "player");
        if (player != null) {
            return player;
        }

        player = readPlayerField(playerHandle, "player");
        if (player != null) {
            return player;
        }

        return playerHandle;
    }

    private ServerPlayerEntity invokePlayerGetter(Object playerHandle, String methodName) {
        Method method = findMethod(playerHandle.getClass(), methodName);
        if (method == null || method.getParameterCount() != 0) {
            return null;
        }

        try {
            method.setAccessible(true);
            Object value = method.invoke(playerHandle);
            if (value instanceof ServerPlayerEntity) {
                return (ServerPlayerEntity) value;
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return null;
    }

    private ServerPlayerEntity readPlayerField(Object playerHandle, String fieldName) {
        Field field = findField(playerHandle.getClass(), fieldName);
        if (field == null) {
            return null;
        }

        try {
            field.setAccessible(true);
            Object value = field.get(playerHandle);
            if (value instanceof ServerPlayerEntity) {
                return (ServerPlayerEntity) value;
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return null;
    }

    private Method findMethod(Class<?> type, String methodName) {
        Class<?> current = type;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}

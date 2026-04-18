package dev.simplix.cirrus.fabric.listener;

import dev.simplix.cirrus.fabric.menubuilder.FabricMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class FabricInventoryPacketListenerTest {

    @Test
    void normalizePlayerHandleUsesGetPlayerMethod() {
        FakePlayer player = new FakePlayer(UUID.randomUUID());
        TestFabricInventoryPacketListener listener = new TestFabricInventoryPacketListener(
            new InventoryTracker(),
            new FabricMenuBuildService()
        );

        assertSame(player, listener.normalizePlayerHandle(new MethodPlayerHandle(player)));
    }

    @Test
    void normalizePlayerHandleUsesPlayerField() {
        FakePlayer player = new FakePlayer(UUID.randomUUID());
        TestFabricInventoryPacketListener listener = new TestFabricInventoryPacketListener(
            new InventoryTracker(),
            new FabricMenuBuildService()
        );

        assertSame(player, listener.normalizePlayerHandle(new FieldPlayerHandle(player)));
    }

    @Test
    void normalizePlayerHandleUsesServerPlayerField() {
        FakePlayer player = new FakePlayer(UUID.randomUUID());
        TestFabricInventoryPacketListener listener = new TestFabricInventoryPacketListener(
            new InventoryTracker(),
            new FabricMenuBuildService()
        );

        assertSame(player, listener.normalizePlayerHandle(new ServerPlayerFieldHandle(player)));
    }

    @Test
    void normalizePlayerHandleUsesGetHandleMethod() {
        FakePlayer player = new FakePlayer(UUID.randomUUID());
        TestFabricInventoryPacketListener listener = new TestFabricInventoryPacketListener(
            new InventoryTracker(),
            new FabricMenuBuildService()
        );

        assertSame(player, listener.normalizePlayerHandle(new HandleMethodPlayerHandle(player)));
    }

    private static final class MethodPlayerHandle {
        private final FakePlayer player;

        private MethodPlayerHandle(FakePlayer player) {
            this.player = player;
        }

        public FakePlayer getPlayer() {
            return player;
        }
    }

    private static final class FieldPlayerHandle {
        private final FakePlayer player;

        private FieldPlayerHandle(FakePlayer player) {
            this.player = player;
        }
    }

    private static final class ServerPlayerFieldHandle {
        private final FakePlayer serverPlayer;

        private ServerPlayerFieldHandle(FakePlayer serverPlayer) {
            this.serverPlayer = serverPlayer;
        }
    }

    private static final class HandleMethodPlayerHandle {
        private final FakePlayer player;

        private HandleMethodPlayerHandle(FakePlayer player) {
            this.player = player;
        }

        public FakePlayer getHandle() {
            return player;
        }
    }

    private static final class FakePlayer {
        private final UUID uuid;

        private FakePlayer(UUID uuid) {
            this.uuid = uuid;
        }
    }

    private static final class TestFabricInventoryPacketListener extends FabricInventoryPacketListener {

        private TestFabricInventoryPacketListener(InventoryTracker inventoryTracker, FabricMenuBuildService menuBuildService) {
            super(inventoryTracker, menuBuildService);
        }

        @Override
        protected boolean isPlayerHandle(Object value) {
            return value instanceof FakePlayer;
        }

        @Override
        protected UUID extractPlayerUuid(Object normalizedHandle) {
            return ((FakePlayer) normalizedHandle).uuid;
        }
    }
}

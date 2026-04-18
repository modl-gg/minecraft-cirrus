package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.exception.PacketProcessException;
import com.github.retrooper.packetevents.injector.ChannelInjector;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.netty.NettyManager;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import java.lang.reflect.Field;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import sun.misc.Unsafe;

class AbstractInventoryPacketListenerTest {

    private static final UUID USER_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID HANDLE_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUpPacketEventsApi() {
        PacketEvents.setAPI(new TestPacketEventsApi());
    }

    @AfterEach
    void clearPacketEventsApi() {
        PacketEvents.setAPI(null);
    }

    @Test
    void resolvePlayerUuidFallsBackToPlayerHandleWhenUserUuidMissing() throws Exception {
        ServerPlayerHandle playerHandle = new ServerPlayerHandle(HANDLE_UUID);
        TestPacketReceiveEvent event = new TestPacketReceiveEvent(newUser(null), playerHandle);

        UUID resolved = new TestListener(playerHandle).resolve(event);

        assertEquals(HANDLE_UUID, resolved);
    }

    @Test
    void resolvePlayerUuidPrefersPlayerHandleWhenUserUuidDiffers() throws Exception {
        ServerPlayerHandle playerHandle = new ServerPlayerHandle(HANDLE_UUID);
        TestPacketReceiveEvent event = new TestPacketReceiveEvent(newUser(USER_UUID), playerHandle);

        UUID resolved = new TestListener(playerHandle).resolve(event);

        assertEquals(HANDLE_UUID, resolved);
    }

    @Test
    void resolvePlayerUuidFallsBackToUserUuidWhenPlayerHandleMissing() throws Exception {
        TestPacketReceiveEvent event = new TestPacketReceiveEvent(newUser(USER_UUID), null);

        UUID resolved = new TestListener(null).resolve(event);

        assertEquals(USER_UUID, resolved);
    }

    private static User newUser(UUID uuid) {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            User user = (User) unsafe.allocateInstance(User.class);

            Field profileField = User.class.getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(user, new UserProfile(uuid, "player"));
            return user;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static final class TestListener extends AbstractInventoryPacketListener {

        private final ServerPlayerHandle expectedHandle;

        private TestListener(ServerPlayerHandle expectedHandle) {
            super(new InventoryTracker(), new NoopMenuBuildService());
            this.expectedHandle = expectedHandle;
        }

        private UUID resolve(PacketReceiveEvent event) throws Exception {
            java.lang.reflect.Method method = AbstractInventoryPacketListener.class.getDeclaredMethod("resolvePlayerUuid", PacketReceiveEvent.class);
            method.setAccessible(true);
            return (UUID) method.invoke(this, event);
        }

        @Override
        protected UUID getPlayerUuid(Object playerHandle) {
            if (expectedHandle == null || playerHandle != expectedHandle) {
                return null;
            }
            return expectedHandle.uuid;
        }
    }

    private static final class NoopMenuBuildService extends AbstractPacketMenuBuildService {

        @Override
        protected com.github.retrooper.packetevents.protocol.player.User getUser(CirrusPlayerWrapper playerWrapper) {
            return null;
        }

        @Override
        protected UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper) {
            return null;
        }
    }

    private static final class TestPacketReceiveEvent extends PacketReceiveEvent {

        private TestPacketReceiveEvent(User user, Object playerHandle) throws PacketProcessException {
            super(0, (PacketTypeCommon) null, ServerVersion.V_1_21_4, null, user, playerHandle, new Object());
        }
    }

    private static final class TestPacketEventsApi extends PacketEventsAPI<Object> {

        @Override
        public void load() {
        }

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void init() {
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public void terminate() {
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public Object getPlugin() {
            return null;
        }

        @Override
        public ServerManager getServerManager() {
            return null;
        }

        @Override
        public ProtocolManager getProtocolManager() {
            return null;
        }

        @Override
        public com.github.retrooper.packetevents.manager.player.PlayerManager getPlayerManager() {
            return null;
        }

        @Override
        public NettyManager getNettyManager() {
            return null;
        }

        @Override
        public ChannelInjector getInjector() {
            return null;
        }
    }

    private static final class ServerPlayerHandle {
        private final UUID uuid;

        private ServerPlayerHandle(UUID uuid) {
            this.uuid = uuid;
        }
    }
}

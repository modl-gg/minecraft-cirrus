package dev.simplix.cirrus.fabric.wrapper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.netty.NettyManager;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FabricPlayerWrapperTest {

    @BeforeEach
    void setUpPacketEventsApi() {
        PacketEvents.setAPI(new TestPacketEventsApi());
    }

    @AfterEach
    void clearPacketEventsApi() {
        PacketEvents.setAPI(null);
    }

    @Test
    void protocolVersionFallsBackToServerVersionWhenUserLookupMissing() {
        assertEquals(
            ServerVersion.V_1_21_4.getProtocolVersion(),
            newWrapper().protocolVersion()
        );
    }

    private static FabricPlayerWrapper newWrapper() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            return (FabricPlayerWrapper) unsafe.allocateInstance(FabricPlayerWrapper.class);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static final class TestPacketEventsApi extends PacketEventsAPI<Object> {

        private final ServerManager serverManager = () -> ServerVersion.V_1_21_4;
        private final com.github.retrooper.packetevents.manager.player.PlayerManager playerManager = new com.github.retrooper.packetevents.manager.player.PlayerManager() {
            @Override
            public int getPing(Object player) {
                return 0;
            }

            @Override
            public com.github.retrooper.packetevents.protocol.player.ClientVersion getClientVersion(Object player) {
                return null;
            }

            @Override
            public Object getChannel(Object player) {
                return null;
            }

            @Override
            public User getUser(Object player) {
                return null;
            }
        };

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
            return serverManager;
        }

        @Override
        public ProtocolManager getProtocolManager() {
            return null;
        }

        @Override
        public com.github.retrooper.packetevents.manager.player.PlayerManager getPlayerManager() {
            return playerManager;
        }

        @Override
        public NettyManager getNettyManager() {
            return null;
        }

        @Override
        public com.github.retrooper.packetevents.injector.ChannelInjector getInjector() {
            return null;
        }
    }
}

package dev.simplix.cirrus.fabric.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class FabricMenuBuildService extends AbstractPacketMenuBuildService {

    @Override
    protected User getUser(CirrusPlayerWrapper playerWrapper) {
        ServerPlayerEntity player = playerWrapper.handle();
        return PacketEvents.getAPI().getPlayerManager().getUser(player);
    }

    @Override
    protected UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper) {
        ServerPlayerEntity player = playerWrapper.handle();
        return player.getUuid();
    }
}

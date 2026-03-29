package dev.simplix.cirrus.neoforge.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class NeoForgeMenuBuildService extends AbstractPacketMenuBuildService {

    @Override
    protected User getUser(CirrusPlayerWrapper playerWrapper) {
        ServerPlayer player = playerWrapper.handle();
        return PacketEvents.getAPI().getPlayerManager().getUser(player);
    }

    @Override
    protected UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper) {
        ServerPlayer player = playerWrapper.handle();
        return player.getUUID();
    }
}

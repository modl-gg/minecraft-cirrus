package dev.simplix.cirrus.velocity.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VelocityMenuBuildService extends AbstractPacketMenuBuildService {

    @Override
    protected User getUser(CirrusPlayerWrapper playerWrapper) {
        Player player = playerWrapper.handle();
        return PacketEvents.getAPI().getPlayerManager().getUser(player);
    }

    @Override
    protected UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper) {
        Player player = playerWrapper.handle();
        return player.getUniqueId();
    }
}

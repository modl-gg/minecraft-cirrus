package dev.simplix.cirrus.bungee.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Slf4j
public class BungeeMenuBuildService extends AbstractPacketMenuBuildService {

    @Override
    protected User getUser(CirrusPlayerWrapper playerWrapper) {
        ProxiedPlayer player = playerWrapper.handle();
        return PacketEvents.getAPI().getPlayerManager().getUser(player);
    }

    @Override
    protected UUID getPlayerUuid(CirrusPlayerWrapper playerWrapper) {
        ProxiedPlayer player = playerWrapper.handle();
        return player.getUniqueId();
    }
}

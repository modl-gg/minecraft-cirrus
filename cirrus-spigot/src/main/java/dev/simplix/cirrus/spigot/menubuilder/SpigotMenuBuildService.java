package dev.simplix.cirrus.spigot.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.simplix.cirrus.common.service.AbstractPacketMenuBuildService;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;

@Slf4j
public class SpigotMenuBuildService extends AbstractPacketMenuBuildService {

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

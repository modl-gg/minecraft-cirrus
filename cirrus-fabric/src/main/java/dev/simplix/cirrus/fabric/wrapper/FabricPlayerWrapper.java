package dev.simplix.cirrus.fabric.wrapper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.simplix.cirrus.Utils;
import dev.simplix.cirrus.model.CirrusSoundCategory;
import dev.simplix.cirrus.model.SimpleSound;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.text.CirrusChatElement;
import lombok.RequiredArgsConstructor;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.UUID;

@RequiredArgsConstructor
public class FabricPlayerWrapper implements CirrusPlayerWrapper {

    private final ServerPlayerEntity player;

    @Override
    public UUID uuid() {
        return player.getUuid();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T handle() {
        return (T) player;
    }

    @Override
    public int protocolVersion() {
        try {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if (user != null) return user.getClientVersion().getProtocolVersion();
        } catch (Exception ignored) {
        }
        return ClientVersion.getLatest().getProtocolVersion();
    }

    @Override
    public void play(SimpleSound sound) {
        try {
            if (PacketEvents.getAPI() == null) return;

            var peSound = Sounds.getByName(sound.sound().identifier());
            if (peSound == null) return;

            SoundCategory category = mapSoundCategory(sound.soundCategory());
            WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(
                peSound, category, new Vector3i(0, 64, 0),
                sound.volume(), sound.pitch()
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        } catch (Exception ignored) {
        }
    }

    private SoundCategory mapSoundCategory(CirrusSoundCategory category) {
        if (category == null) return SoundCategory.MASTER;
        return switch (category) {
            case MASTER -> SoundCategory.MASTER;
            case MUSIC -> SoundCategory.MUSIC;
            case RECORDS -> SoundCategory.RECORD;
            case WEATHER -> SoundCategory.WEATHER;
            case BLOCKS -> SoundCategory.BLOCK;
            case HOSTILE -> SoundCategory.HOSTILE;
            case NEUTRAL -> SoundCategory.NEUTRAL;
            case PLAYERS -> SoundCategory.PLAYER;
            case AMBIENT -> SoundCategory.AMBIENT;
            case VOICE -> SoundCategory.VOICE;
        };
    }

    @Override
    public void sendMessage(String message) {
        String colorized = Utils.colorize(message);
        try {
            String json = AdventureSerializer.toJson(CirrusChatElement.ofLegacyText(colorized).asComponent());
            Text text = TextCodecs.CODEC.parse(
                RegistryOps.of(JsonOps.INSTANCE, player.getRegistryManager()),
                JsonParser.parseString(json)
            ).result().orElse(null);
            if (text != null) {
                player.sendMessage(text, false);
                return;
            }
        } catch (Exception ignored) {
        }
        player.sendMessage(Text.literal(colorized.replaceAll("\u00a7[0-9a-fk-or]", "")), false);
    }
}

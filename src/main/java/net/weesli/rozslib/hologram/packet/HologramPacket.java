package net.weesli.rozslib.hologram.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import net.kyori.adventure.text.Component;
import net.weesli.rozslib.hologram.Hologram;
import net.weesli.rozslib.hologram.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class HologramPacket {

    public static final double LINE_SPACING = 0.30;

    private HologramPacket() {
    }

    public static void spawn(Player player, Hologram hologram) {
        if (hologram.getPlayerEntityIds(player.getUniqueId()) != null) {
            return;
        }

        Location base = hologram.getBaseLocation();
        Map<Integer, Integer> entityIds = new HashMap<>();

        int i = 0;
        for (Map.Entry<Integer, HologramLine> entry : hologram.getLines().entrySet()) {
            int lineIndex = entry.getKey();
            HologramLine line = entry.getValue();
            int entityId = Hologram.nextEntityId();
            entityIds.put(lineIndex, entityId);

            double y = base.getY() - (i * LINE_SPACING);
            spawnLine(player, entityId, line, base.getX(), y, base.getZ());
            i++;
        }

        hologram.setPlayerEntityIds(player.getUniqueId(), entityIds);
    }

    public static void update(Player player, Hologram hologram) {
        Map<Integer, Integer> entityIds = hologram.getPlayerEntityIds(player.getUniqueId());
        if (entityIds == null)
            return;

        for (Map.Entry<Integer, HologramLine> entry : hologram.getLines().entrySet()) {
            Integer entityId = entityIds.get(entry.getKey());
            if (entityId == null)
                continue;
            sendMetadata(player, entityId, entry.getValue());
        }
    }

    public static void updateLine(Player player, Hologram hologram, int lineIndex) {
        Map<Integer, Integer> entityIds = hologram.getPlayerEntityIds(player.getUniqueId());
        if (entityIds == null)
            return;

        Integer entityId = entityIds.get(lineIndex);
        HologramLine line = hologram.getLines().get(lineIndex);
        if (entityId == null || line == null)
            return;

        sendMetadata(player, entityId, line);
    }

    public static void spawnSingleLine(Player player, Hologram hologram, int lineIndex) {
        Location base = hologram.getBaseLocation();
        HologramLine line = hologram.getLines().get(lineIndex);
        if (line == null)
            return;

        int entityId = Hologram.nextEntityId();
        hologram.addPlayerEntityId(player.getUniqueId(), lineIndex, entityId);

        double y = base.getY() - (lineIndex * LINE_SPACING);
        spawnLine(player, entityId, line, base.getX(), y, base.getZ());
    }

    public static void destroy(Player player, Hologram hologram) {
        Map<Integer, Integer> entityIds = hologram.removePlayerEntityIds(player.getUniqueId());
        if (entityIds == null || entityIds.isEmpty())
            return;

        int[] ids = entityIds.values().stream().mapToInt(Integer::intValue).toArray();
        send(player, new WrapperPlayServerDestroyEntities(ids));
    }

    public static void destroyLine(Player player, Hologram hologram, int lineIndex) {
        Integer entityId = hologram.removePlayerEntityId(player.getUniqueId(), lineIndex);
        if (entityId == null)
            return;
        send(player, new WrapperPlayServerDestroyEntities(entityId));
    }

    private static void spawnLine(Player player, int entityId, HologramLine line,
            double x, double y, double z) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(UUID.randomUUID()),
                EntityTypes.TEXT_DISPLAY,
                new Vector3d(x, y, z),
                0f, 0f, 0f,
                0,
                Optional.empty());
        send(player, spawnPacket);
        sendMetadata(player, entityId, line);
    }

    private static void sendMetadata(Player player, int entityId, HologramLine line) {
        Component text = line.getText(player);

        List<EntityData<?>> metadata = new ArrayList<>();
        metadata.add(new EntityData<>(15, EntityDataTypes.BYTE, line.getBillboard().getValue()));
        metadata.add(new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, text));
        metadata.add(new EntityData<>(24, EntityDataTypes.INT, 9999999));
        metadata.add(new EntityData<>(25, EntityDataTypes.INT, line.getBackgroundColor()));
        metadata.add(new EntityData<>(26, EntityDataTypes.BYTE, line.getTextOpacity()));
        metadata.add(new EntityData<>(27, EntityDataTypes.BYTE, line.getStyleFlags()));

        send(player, new WrapperPlayServerEntityMetadata(entityId, metadata));
    }

    private static void send(Player player, PacketWrapper<?> packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
package net.weesli.rozslib.hologram;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.hologram.packet.HologramPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Getter
@Setter
public class Hologram {

    private static final AtomicInteger COUNTER = new AtomicInteger(100_000);

    private final String hologramId;
    private Location baseLocation;

    private final Map<Integer, HologramLine> lines = new LinkedHashMap<>();


    @Nullable
    private List<Player> viewers;

    private final Map<UUID, Map<Integer, Integer>> playerEntityIds = new HashMap<>();


    public Hologram(String hologramId, Location location) {
        this.hologramId = hologramId;
        this.baseLocation = location;
    }


    public HologramLine addLine(String text) {
        return addLine(new HologramLine(text));
    }

    public HologramLine addLine(Function<Player, String> textFunction) {
        return addLine(new HologramLine(textFunction));
    }

    public HologramLine addLine(HologramLine line) {
        int index = lines.size();
        lines.put(index, line);
        forEachViewer(p -> HologramPacket.spawnSingleLine(p, this, index));
        return line;
    }

    public HologramLine setLine(int index, String text) {
        HologramLine line = lines.get(index);
        if (line == null)
            return addLine(text);
        line.text(text);
        forEachViewer(p -> HologramPacket.updateLine(p, this, index));
        return line;
    }

    public HologramLine setLine(int index, Function<Player, String> textFunction) {
        HologramLine line = lines.get(index);
        if (line == null)
            return addLine(textFunction);
        line.text(textFunction);
        forEachViewer(p -> HologramPacket.updateLine(p, this, index));
        return line;
    }

    public void removeLine(int index) {
        lines.remove(index);
        forEachViewer(p -> HologramPacket.destroyLine(p, this, index));
    }

    public void addViewer(Player player) {
        if (viewers == null)
            viewers = new ArrayList<>();
        if (viewers.contains(player))
            return;
        viewers.add(player);
        HologramPacket.spawn(player, this);
    }

    public void removeViewer(Player player) {
        if (viewers != null)
            viewers.remove(player);
        HologramPacket.destroy(player, this);
    }


    public void showToAll() {
        viewers = null;
        Bukkit.getOnlinePlayers().forEach(p -> HologramPacket.spawn(p, this));
    }

    public void update() {
        forEachViewer(p -> HologramPacket.update(p, this));
    }

    public void delete() {
        forEachViewer(p -> HologramPacket.destroy(p, this));
        if (viewers != null)
            viewers.clear();
        playerEntityIds.clear();
    }


    public @Nullable Map<Integer, Integer> getPlayerEntityIds(UUID uuid) {
        return playerEntityIds.get(uuid);
    }

    public void setPlayerEntityIds(UUID uuid, Map<Integer, Integer> ids) {
        playerEntityIds.put(uuid, ids);
    }

    public @Nullable Map<Integer, Integer> removePlayerEntityIds(UUID uuid) {
        return playerEntityIds.remove(uuid);
    }

    public void addPlayerEntityId(UUID uuid, int lineIndex, int entityId) {
        playerEntityIds.computeIfAbsent(uuid, k -> new HashMap<>()).put(lineIndex, entityId);
    }

    public @Nullable Integer removePlayerEntityId(UUID uuid, int lineIndex) {
        Map<Integer, Integer> map = playerEntityIds.get(uuid);
        return (map != null) ? map.remove(lineIndex) : null;
    }

    public static int nextEntityId() {
        return COUNTER.incrementAndGet();
    }

    private void forEachViewer(java.util.function.Consumer<Player> action) {
        if (viewers == null) {
            Bukkit.getOnlinePlayers().forEach(action);
        } else {
            viewers.forEach(action);
        }
    }
}
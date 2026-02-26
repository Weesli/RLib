package net.weesli.rozslib.hologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class RozsHologramService {

    @Getter
    private static final Map<String, Hologram> hologramRegistry = new HashMap<>();

    private final Plugin plugin;

    public RozsHologramService(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(plugin, this::updateHolograms, 1L, 20L);
        Bukkit.getServer().getPluginManager().registerEvents(new HologramEvent(), plugin);
        plugin.getLogger().log(Level.INFO, "RozsHologram service is enabled!");
    }

    public Hologram createHologram(String id, Location location) {
        Hologram old = hologramRegistry.remove(id);
        if (old != null) {
            old.delete();
        }
        Hologram hologram = new Hologram(id, location);
        hologramRegistry.put(id, hologram);
        return hologram;
    }

    public @Nullable Hologram getHologram(String id) {
        return hologramRegistry.get(id);
    }

    public void deleteHologram(String id) {
        Hologram hologram = hologramRegistry.remove(id);
        if (hologram != null) {
            hologram.delete();
        }
    }

    private void updateHolograms() {
        for (Hologram hologram : hologramRegistry.values()) {
            hologram.update();
        }
    }
}

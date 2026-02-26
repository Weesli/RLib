package net.weesli.rozslib.hologram;

import net.weesli.rozslib.hologram.packet.HologramPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class HologramEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for (Hologram holo : RozsHologramService.getHologramRegistry().values()) {
            if (holo.getViewers() == null) {
                HologramPacket.spawn(e.getPlayer(), holo);
            }
        }
    }

    @EventHandler
    public void onExit(PlayerQuitEvent e) {
        for (Hologram holo : RozsHologramService.getHologramRegistry().values()) {
            holo.removePlayerEntityIds(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDisablePlugin(PluginDisableEvent e) {
        for (Hologram holo : RozsHologramService.getHologramRegistry().values()) {
            holo.delete();
        }
    }
}

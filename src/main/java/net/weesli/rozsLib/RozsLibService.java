package net.weesli.rozsLib;

import lombok.Getter;
import net.weesli.rozsLib.events.LibListener;
import org.bukkit.plugin.Plugin;
import java.util.logging.Level;

@Getter
public class RozsLibService {

    @Getter private static Plugin plugin;

    private RozsLibService(Plugin plugin){
        RozsLibService.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Starting RozsLibService...");
        plugin.getServer().getPluginManager().registerEvents(new LibListener(), plugin);
    }

    public static void start(Plugin plugin){
        new RozsLibService(plugin);
    }

}
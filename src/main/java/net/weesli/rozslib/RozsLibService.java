package net.weesli.rozslib;

import lombok.Getter;
import net.weesli.rozslib.events.LibListener;
import org.bukkit.plugin.Plugin;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class RozsLibService {

    @Getter private static Plugin plugin;
    @Getter Logger logger;

    private RozsLibService(Plugin plugin){
        RozsLibService.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Starting RozsLibService...");
        plugin.getServer().getPluginManager().registerEvents(new LibListener(), plugin);
        logger = plugin.getLogger();
    }

    public static void start(Plugin plugin){
        new RozsLibService(plugin);

    }

}
package net.weesli.rozsLib;

import lombok.Getter;
import net.weesli.rozsLib.events.LibListener;
import org.bukkit.plugin.Plugin;
import java.util.logging.Level;

@Getter
public final class RozsLib {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/Weesli/RLib/releases";

    private final Plugin plugin;

    private RozsLib(Plugin plugin){
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Starting RozsLibService...");
        plugin.getServer().getPluginManager().registerEvents(new LibListener(), plugin);
    }

    public static void start(Plugin plugin){
        new RozsLib(plugin);
    }

}
package net.weesli.rozslib;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
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
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .debugMode()
                .tickTickables()
                .trackPlatformEntities()
                .usePlatformLogger();
        PacketEvents.getAPI().init();
        EntityLib.init(SpigotEn);
    }

    public static void start(Plugin plugin){
        new RozsLibService(plugin);

    }

}
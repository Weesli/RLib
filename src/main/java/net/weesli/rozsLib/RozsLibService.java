package net.weesli.rozsLib;


import lombok.Getter;
import lombok.Setter;
import net.weesli.rozsLib.events.LibListener;
import org.bukkit.plugin.Plugin;
@Getter@Setter
public class RozsLibService {

    @Getter private static Plugin plugin;

    public RozsLibService(Plugin plugin){
        RozsLibService.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new LibListener(), plugin);
    }


}

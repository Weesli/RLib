package net.weesli.rozsLib.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class LibListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntityType().equals(EntityType.PLAYER)){
            Player player = (Player) e.getEntity();
            PlayerDamageEvent playerDamageEvent = new PlayerDamageEvent(player, e.getDamage());
            if (e.isCancelled()){
                playerDamageEvent.setCancelled(true);
            }
            if (playerDamageEvent.isCancelled()){
                e.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(playerDamageEvent);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e){
        if (e.getEntityType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)){
            Player player = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            PlayerDamageByPlayerEvent playerDamageByEntityEvent = new PlayerDamageByPlayerEvent(player, damager, e.getDamage());
            if (e.isCancelled()){
                playerDamageByEntityEvent.setCancelled(true);
            }
            if (playerDamageByEntityEvent.isCancelled()){
                e.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(playerDamageByEntityEvent);
        }
    }

}

package net.weesli.rozsLib.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LibListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntityType().equals(EntityType.PLAYER)){
            Player player = (Player) e.getEntity();
            PlayerDamageEvent playerDamageEvent = new PlayerDamageEvent(player, e.getDamage());
            Bukkit.getPluginManager().callEvent(playerDamageEvent);
            if (playerDamageEvent.isCancelled()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e){
        if (e.getEntityType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)){
            Player player = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            PlayerDamageByPlayerEvent playerDamageByEntityEvent = new PlayerDamageByPlayerEvent(player, damager, e.getDamage());
            Bukkit.getPluginManager().callEvent(playerDamageByEntityEvent);
            if (playerDamageByEntityEvent.isCancelled()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractPlayer(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null){ return;}
        if (e.getAction().isLeftClick()){
            BlockLeftClickEvent event = new BlockLeftClickEvent(player,block);
            Bukkit.getPluginManager().callEvent(event);
            if (e.isCancelled()){
                event.setCancelled(true);
            }
        } else if (e.getAction().isRightClick()) {
            BlockRightClickEvent event = new BlockRightClickEvent(player, block);
            Bukkit.getPluginManager().callEvent(event);
            if (e.isCancelled()){
                event.setCancelled(true);
            }
        }
    }

}

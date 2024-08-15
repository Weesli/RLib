package net.weesli.rozsLib.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageByPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Player damager;
    private double damage;

    public PlayerDamageByPlayerEvent(Player player, Player damager, double damage) {
        this.player = player;
        this.damager = damager;
        this.damage = damage;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }
}

package net.weesli.rozsLib.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BlockRightClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Block clickedBlock;

    public BlockRightClickEvent(Player player, Block clickedBlock) {
        this.player = player;
        this.clickedBlock = clickedBlock;
    }

    private boolean cancelled;


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
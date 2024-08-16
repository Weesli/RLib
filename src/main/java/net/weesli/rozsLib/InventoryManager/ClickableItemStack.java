package net.weesli.rozsLib.InventoryManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


/**
 * Author {@Weesli}
 * Clickable item for InventoryBuilder
 */

public abstract class ClickableItemStack implements Listener {

    private ItemStack itemStack;
    private Inventory inventory;

    private boolean cancelled = false;

    public ClickableItemStack(Plugin plugin,ItemStack itemStack, Inventory inventory) {
        this.itemStack = itemStack;
        this.inventory = inventory;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ClickableItemStack setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }

    protected abstract void addListener(InventoryClickEvent e);

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isCancelled() {
        return cancelled;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == inventory && e.getCurrentItem() != null && e.getCurrentItem().isSimilar(itemStack)){
            addListener(e);
            e.setCancelled(isCancelled());
        }
    }
}

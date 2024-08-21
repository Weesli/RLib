package net.weesli.rozsLib.InventoryManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

public class ClickableItemStack implements Listener {

    private ItemStack itemStack;
    private Inventory inventory;
    private ClickEvent event;

    private boolean cancelled = false;
    private boolean clickSound = true;
    private Sound sound = Sound.UI_BUTTON_CLICK;

    public ClickableItemStack(Plugin plugin,ItemStack itemStack, Inventory inventory) {
        this.itemStack = itemStack;
        this.inventory = inventory;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public ClickableItemStack setEvent(ClickEvent event) {
        this.event = event;
        return this;
    }

    public ClickableItemStack setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }
    public ClickableItemStack setClickSound(boolean clickSound) {
        this.clickSound = clickSound;
        return this;
    }

    public ClickableItemStack setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isClickSound() {
        return clickSound;
    }

    public Sound getSound() {
        return sound;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null){return;}
        if (!e.getClickedInventory().equals(inventory)){return;}
        if (e.getCurrentItem().isSimilar(itemStack)){
            if (isClickSound()){
                e.getWhoClicked().getWorld().playSound(e.getWhoClicked().getLocation(), getSound(), 3,1);
            }
            event.onClick(e);
            e.setCancelled(isCancelled());
        }
    }

}

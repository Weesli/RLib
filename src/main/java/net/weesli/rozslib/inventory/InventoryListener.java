package net.weesli.rozslib.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getClickedInventory() == null ) return;
        if (e.getInventory().getHolder() instanceof AbstractInventory inventory) {
            inventory.onInventoryClick(e);
        }
    }

    @EventHandler
    public void onOpenInventory(final InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof AbstractInventory inventory) {
            inventory.handleOpen(e);
        }
    }

    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof AbstractInventory inventory) {
            inventory.handleClose(e);
        }
    }

    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof AbstractInventory inventory)) return;
        inventory.onInventoryDrag(event);
    }
}

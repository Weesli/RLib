package net.weesli.rozsLib.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryChecker {

    boolean check(InventoryClickEvent event, Object object);
}

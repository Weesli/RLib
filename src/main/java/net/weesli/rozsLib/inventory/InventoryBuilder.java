package net.weesli.rozsLib.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A builder for creating custom inventories.
 *
 */
@Deprecated
public class InventoryBuilder implements Listener {

    @Getter
    private String title;
    @Getter
    private int size;

    private final Inventory inventory;

    @Setter
    @Getter private boolean inventoryClick = true;

    private List<ClickableItemStack> items = new ArrayList<>();


    public InventoryBuilder(Plugin plugin, String title, int size) {
        this.title = title;
        this.size = size;
        inventory = plugin.getServer().createInventory(null, size, title);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setItem(int slot, ClickableItemStack itemStack) {
        inventory.setItem(slot, itemStack.getItemStack());
        items.add(itemStack);
    }

    public void setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    public void addItem(ClickableItemStack itemStack){
        inventory.addItem(itemStack.getItemStack());
        items.add(itemStack);
    }

    public void addItem(ItemStack itemStack){
        inventory.addItem(itemStack);
    }

    /**
     *
     * @return Inventory instance
     */
    public Inventory build(){
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (e.getInventory().equals(inventory)){
            if (isInventoryClick()){
                e.setCancelled(true);
            }
        }
    }


}

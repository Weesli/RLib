package net.weesli.rozsLib.InventoryManager;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


/**
 * A builder for creating custom inventories.
 *
 */
public class InventoryBuilder {

    @Getter
    private String title;
    @Getter
    private int size;

    private final Inventory inventory;


    public InventoryBuilder(Plugin plugin, String title, int size) {
        this.title = title;
        this.size = size;
        inventory = plugin.getServer().createInventory(null, size, title);
    }

    public void setItem(int slot, ClickableItemStack itemStack) {
        inventory.setItem(slot, itemStack.getItemStack());

    }

    public void setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    public void addItem(ClickableItemStack itemStack){
        inventory.addItem(itemStack.getItemStack());
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


}

package net.weesli.rozslib.inventory.builder.hook;

import dev.lone.itemsadder.api.CustomStack;
import net.weesli.rozslib.inventory.builder.MaterialHook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook implements MaterialHook {

    @Override
    public ItemStack getItem(String object) {
        CustomStack customStack = CustomStack.getInstance(object);
        if (customStack == null){
            return new ItemStack(Material.STONE);
        }
        return customStack.getItemStack();
    }

    @Override
    public String getPrefix() {
        return "itemsadder";
    }

}

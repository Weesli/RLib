package net.weesli.rozslib.inventory;

import net.weesli.rozslib.inventory.builder.BaseItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder {

    public static BaseItemBuilder of(Material material) {
        return BaseItemBuilder.of(material);
    }

    public static BaseItemBuilder of(ItemStack stack) {
        return BaseItemBuilder.of(stack);
    }

}

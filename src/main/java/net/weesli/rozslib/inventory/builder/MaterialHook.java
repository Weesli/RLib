package net.weesli.rozslib.inventory.builder;

import org.bukkit.inventory.ItemStack;

public interface MaterialHook {

    ItemStack getItem(final String object);

    String getPrefix();
}

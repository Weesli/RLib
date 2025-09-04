package net.weesli.rozslib.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
@Getter@Setter

public class ClickableItemStack {

    private ItemStack itemStack;
    private int slot;
    private boolean clickable = false;
    private Sound sound = Sound.UI_BUTTON_CLICK;

    public ClickableItemStack(ItemStack itemStack, int slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    @Override
    public int hashCode() {
        return slot;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClickableItemStack) {
            return ((ClickableItemStack) obj).slot == slot;
        }
        return false;
    }
}

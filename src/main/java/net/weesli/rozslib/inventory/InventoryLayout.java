package net.weesli.rozslib.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InventoryLayout {

    private boolean auto = false;
    @Setter
    private ItemStack defaultItem;
    private List<ClickableItemStack> layoutItems = new ArrayList<>();

    public void generateLayoutWithStructure(String layout) {
        List<ClickableItemStack> clickableItems = new ArrayList<>();
        int index = 0;

        for (char c : layout.toCharArray()) {
            if (c == '*') {
                clickableItems.add(new ClickableItemStack(defaultItem,index));
            }
            if (c != '\n') {
                index++;
            }
        }
        this.layoutItems = clickableItems;
    }

    public void generateLayoutWithIndex(int... index){
        List<ClickableItemStack> clickableItems = new ArrayList<>();
        for(int i: index){
            clickableItems.add(new ClickableItemStack(defaultItem, i));
        }
        this.layoutItems = clickableItems;
    }

    public void fill(ItemStack itemStack, boolean auto){
        this.defaultItem = itemStack;
        this.layoutItems.forEach(item -> item.setItemStack(itemStack)); // set to item then filled inventory
        this.auto = auto;
    }

}

package net.weesli.rozslib.inventory.types;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.enums.InventoryType;
import net.weesli.rozslib.inventory.AbstractInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
public class PageableInventory extends AbstractInventory {

    private int currentPage = 0;
    private ClickableItemStack previousItem, nextItem;

    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> baseItems = new HashMap<>();
    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> staticItems = new HashMap<>();

    public PageableInventory(String title, int size, ClickableItemStack previousItem, ClickableItemStack nextItem, int... blockIndex) {
        super(title, size, InventoryType.PAGEABLE, blockIndex);
        this.previousItem = previousItem;
        this.nextItem = nextItem;
    }

    public void addStaticItem(ClickableItemStack itemStack, Consumer<InventoryClickEvent> event) {
        staticItems.put(itemStack, event);
    }

    public void openDefaultInventory(Player player) {
        baseItems = new HashMap<>(getItems());
        clear();
        openPage(player);
    }

    public void previousPage(Player player) {
        currentPage--;
        openPage(player);
    }

    public void nextPage(Player player) {
        currentPage++;
        openPage(player);
    }

    private void openPage(Player player) {
        int totalPages = (int) Math.ceil((double) baseItems.size() / (getSize() - getLayout().getLayoutItems().size()));
        int pageItemSize = getSize() - getLayout().getLayoutItems().size() - getStaticItems().size() - getBlockIndex().length;

        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        List<Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>>> pageItems = baseItems.entrySet().stream()
                .skip((long) currentPage * pageItemSize)
                .limit(pageItemSize)
                .toList();

        clear();


        for (Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>> staticItem : staticItems.entrySet()) { // firstly add static items to menu
            setItem(staticItem.getKey(), staticItem.getValue());
        }

        if (currentPage > 0) setItem(previousItem, event -> previousPage(player)); // secondly add page items to menu
        if (currentPage < totalPages - 1) setItem(nextItem, event -> nextPage(player));

        for (int itemSlot = 0; itemSlot < pageItemSize; itemSlot++) { // finally add non-static items to menu
            try {
                Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>> item = pageItems.get(itemSlot);
                if (item == null) continue;
                addItem(item.getKey().getItemStack(), item.getValue());
            }catch (Exception ex){
                continue;
            }
        }

        build();
        openInventory(player);
    }


}

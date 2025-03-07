package net.weesli.rozsLib.inventory.types;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozsLib.inventory.AbstractInventory;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter@Setter
public class PageableInventory extends AbstractInventory {

    private int currentPage = 0;
    private ClickableItemStack previousItem, nextItem;

    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> baseItems = new HashMap<>();

    public PageableInventory(String title, int size, ClickableItemStack previousItem, ClickableItemStack nextItem) {
        super(title, size, previousItem.getSlot(), nextItem.getSlot());
        this.previousItem = previousItem;
        this.nextItem = nextItem;
    }

    public void openDefaultInventory(Player player){
        baseItems = new HashMap<>(getItems());
        clear();
        openPage(player);
    }

    public void setPage(int page) {
        currentPage = page;
    }

    public void previousPage(Player player){
        currentPage--;
        openPage(player);
    }

    public void nextPage(Player player){
        currentPage++;
        openPage(player);
    }

    private void openPage(Player player) {
        int pageSize = getSize();
        int totalPages = (int) Math.ceil((double) baseItems.size() / pageSize);

        if (currentPage >= totalPages) currentPage = totalPages - 1;

        List<Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>>> pageItems = baseItems.entrySet()
                .stream()
                .skip(Math.max(0, (long) currentPage * pageSize))
                .limit(pageSize)
                .toList();

        clear();

        for (int x = 0; x < pageItems.size(); x++){
            if (x == previousItem.getSlot()){
                if (currentPage > 0){
                    continue;
                }
            }
            if (x == nextItem.getSlot()){
                if (currentPage < totalPages - 1){
                    continue;
                }
            }
            Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>> item = pageItems.get(x);
            item.getKey().setSlot(x);
            setItem(item.getKey(),item.getValue());
        }

        if (currentPage > 0) setItem(previousItem, (event -> previousPage(player)));
        if (currentPage < totalPages - 1) setItem(nextItem, (event -> nextPage(player)));

        build();
        openInventory(player);
    }






}

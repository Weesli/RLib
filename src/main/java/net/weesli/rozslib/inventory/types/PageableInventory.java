package net.weesli.rozslib.inventory.types;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.enums.InventoryType;
import net.weesli.rozslib.inventory.AbstractInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
        int pageItemSize = Math.max(0,
                getSize()
                        - getLayout().getLayoutItems().size()
                        - getStaticItems().size()
                        - getBlockIndex().length
        );

        int perPage = Math.max(1, pageItemSize);
        int totalPages = Math.max(1, (int) Math.ceil((double) baseItems.size() / perPage));

        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        List<Map.Entry<ClickableItemStack, Consumer<InventoryClickEvent>>> pageItems =
                baseItems.entrySet().stream()
                        .skip((long) currentPage * perPage)
                        .limit(perPage)
                        .toList();

        clear();

        for (var staticItem : staticItems.entrySet()) {
            setItem(staticItem.getKey(), staticItem.getValue());
        }

        if (currentPage > 0) {
            setItem(previousItem, e -> previousPage(player));
        } else {
            if (getLayout().getLayoutItems().stream().anyMatch(i -> i.getSlot() == previousItem.getSlot())) {
                setItem(getLayout().getDefaultItem(), previousItem.getSlot());
            }
        }

        if (currentPage < totalPages - 1) {
            setItem(nextItem, e -> nextPage(player));
        } else {
            if (getLayout().getLayoutItems().stream().anyMatch(i -> i.getSlot() == nextItem.getSlot())) {
                setItem(getLayout().getDefaultItem(), nextItem.getSlot());
            }
        }

        for (var entry : pageItems) {
            addItem(entry.getKey().getItemStack(), entry.getValue());
        }

        build();
        openInventory(player);
    }



}

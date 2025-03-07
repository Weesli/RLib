package net.weesli.rozsLib.inventory;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozsLib.RozsLibService;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

@Getter@Setter
public abstract class AbstractInventory implements Listener {

    private int[] blockIndex;

    private String title;
    // default size is 9 (1 column)
    private int size = 9;
    private Inventory inventory;
    private boolean clickable = false; // default is false
    // events
    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> items = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeEvent;
    private Consumer<InventoryOpenEvent> openEvent;

    public AbstractInventory(String title, int size, int ...blockIndex) {
        this.title = title;
        this.size = size;
        this.blockIndex = blockIndex;
        Bukkit.getPluginManager().registerEvents(this, RozsLibService.getPlugin());  // register this class to service plugin
    }

    public void addItem(ItemStack itemStack){
        int availableIndex = 0;

        Set<Integer> usedIndices = new HashSet<>();
        for (ClickableItemStack item : items.keySet()) {
            usedIndices.add(item.getSlot());
        }

        while (usedIndices.contains(availableIndex) || contains(blockIndex, availableIndex)) {
            availableIndex++;
        }

        items.put(new ClickableItemStack(itemStack, availableIndex), event -> {});
    }

    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> event){
        int availableIndex = 0;

        Set<Integer> usedIndices = new HashSet<>();
        for (ClickableItemStack item : items.keySet()) {
            usedIndices.add(item.getSlot());
        }

        while (usedIndices.contains(availableIndex) || contains(blockIndex, availableIndex)) {
            availableIndex++;
        }

        items.put(new ClickableItemStack(itemStack, availableIndex), event);
    }

    private boolean contains(int[] array, int value) {
        for (int i : array) if (i == value) return true;
        return false;
    }


    public void setItem(ClickableItemStack itemStack, Consumer<InventoryClickEvent> event){
        items.put(itemStack, event);
    }
    public void setItem(ItemStack itemStack, int index){
        items.put(new ClickableItemStack(itemStack,index), event -> {});
    }

    public void openInventory(Player player){
        if (inventory == null) build();
        player.openInventory(inventory);
    }

    public void clear(){
        items.clear();
    }

    public void build(){
        inventory = Bukkit.createInventory(null,size, ColorBuilder.convertColors(title));
        for (ClickableItemStack item : items.keySet()){
            inventory.setItem(item.getSlot(), item.getItemStack());
        }
    }
    public void build(Map<ClickableItemStack, Consumer<InventoryClickEvent>> items){
        this.items.clear();
        this.items = items;
        build();
    }

    @EventHandler
    public void handleClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null){return;}
        if (!e.getInventory().equals(getInventory())){return;}

        if (!isClickable()) e.setCancelled(true);
        Optional<ClickableItemStack> event = items.keySet().stream().filter(item -> item.getSlot() == e.getSlot()).findFirst();
        event.ifPresent(clickableItemStack -> {
            if (!clickableItemStack.isClickable()){
                e.setCancelled(true);
            }
            items.get(clickableItemStack).accept(e);
            player.playSound(player, clickableItemStack.getSound(),3,1);
        });
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent e){
        if (getInventory() == null) return;
        if (!e.getInventory().equals(getInventory())){return;}
        if (closeEvent != null) {
            closeEvent.accept(e);
        }
    }
    @EventHandler
    public void handleOpen(InventoryOpenEvent e){
        if (getInventory() == null) return;
        if (!e.getInventory().equals(getInventory())){return;}
        if (openEvent!= null) {
            openEvent.accept(e);
        }
    }

}

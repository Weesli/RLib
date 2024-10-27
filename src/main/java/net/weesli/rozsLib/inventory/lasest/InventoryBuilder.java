package net.weesli.rozsLib.inventory.lasest;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter@Setter
public class InventoryBuilder implements Listener {

    private String title;
    private int size;
    private boolean clickable = false;
    private Inventory inventory;

    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> events = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeEvent;


    public InventoryBuilder() {
        Bukkit.getPluginManager().registerEvents(this, RozsLibService.getPlugin());
    }

    public InventoryBuilder title(String title) {
        this.title = title;
        return this;
    }

    public InventoryBuilder size(int size) {
        this.size = size;
        return this;
    }

    public InventoryBuilder close(Consumer<InventoryCloseEvent> event) {
        this.closeEvent = event;
        return this;
    }

    public void setItem(int i, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        events.put(new ClickableItemStack(itemStack,i), consumer);
    }

    public void setItem(ClickableItemStack itemStack, Consumer<InventoryClickEvent> consumer){
        events.put(itemStack,consumer);
    }
    public void addItem(ClickableItemStack itemStack, Consumer<InventoryClickEvent> consumer){
        int slot = 0;
        Set<Integer> items = events.keySet().stream().map(ClickableItemStack::getSlot).collect(Collectors.toSet());
        for (int i = 0; i < size; i++) {
            if (!items.contains(i)) {
                slot = i;
                break;
            }
        }
        events.put(itemStack, consumer);
    }

    public void setItem(int i, ItemStack itemStack) {
       setItem(i, itemStack, event -> {});
    }


    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        int slot = 0;
        Set<Integer> items = events.keySet().stream().map(ClickableItemStack::getSlot).collect(Collectors.toSet());
        for (int i = 0; i < size; i++) {
            if (!items.contains(i)) {
                slot = i;
                break;
            }
        }
        events.put(new ClickableItemStack(itemStack, slot), consumer);
    }

    public void addItem(ItemStack itemStack) {
        addItem(itemStack, event -> {});
    }

    public void openInventory(Player player) {
        if (inventory == null){
            build();
        }
        player.openInventory(inventory);
    }

    public void build() {
        inventory = Bukkit.createInventory(null,size, ColorBuilder.convertColors(title));
        events.forEach((itemStack, consumer) -> inventory.setItem(itemStack.getSlot(), itemStack.getItemStack()));
    }


    @EventHandler
    void handleClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null){return;}
        if (!e.getInventory().equals(getInventory())){return;}
        if (!clickable){
            e.setCancelled(true);
        }
        Optional<ClickableItemStack> event = events.keySet().stream().filter(item -> item.getItemStack().isSimilar(e.getCurrentItem())).findFirst();
        event.ifPresent(clickableItemStack -> {
            if (!clickableItemStack.isClickable()){
                e.setCancelled(true);
            }
            events.get(clickableItemStack).accept(e);
            player.playSound(player, clickableItemStack.getSound(),3,1);
        });
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent e){
        if (!e.getInventory().equals(getInventory())){return;}
        if (closeEvent != null) {
            closeEvent.accept(e);
        }
    }
}

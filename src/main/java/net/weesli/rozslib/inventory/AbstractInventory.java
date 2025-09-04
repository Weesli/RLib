package net.weesli.rozslib.inventory;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.enums.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter@Setter
public abstract class AbstractInventory implements InventoryHolder {

    private InventoryType type;
    private int[] blockIndex;

    private String title;
    // default size is 9 (1 column)
    private int size = 9;
    @Getter
    private Inventory inventory;
    private boolean clickable = false; // default is false
    // events
    private Map<ClickableItemStack, Consumer<InventoryClickEvent>> items = new HashMap<>();
    private InventoryLayout layout;
    private Consumer<InventoryCloseEvent> closeEvent;
    private Consumer<InventoryOpenEvent> openEvent;

    public AbstractInventory(String title, int size, InventoryType type, int ...blockIndex) {
        this.title = title;
        this.size = size;
        this.blockIndex = blockIndex;
        this.type = type;
        layout = new InventoryLayout();
    }

    public InventoryLayout setLayout(String layout) {
        this.layout.generateLayoutWithStructure(layout);
        return this.layout;
    }

    public InventoryLayout setLayout(int ...index){
        this.layout.generateLayoutWithIndex(index);
        return this.layout;
    }

    public void addItem(ItemStack itemStack){
        items.put(new ClickableItemStack(itemStack, getAvailableIndex()), event -> {});
    }

    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> event){
        items.put(new ClickableItemStack(itemStack, getAvailableIndex()), event);
    }

    private int getAvailableIndex() {
        int availableIndex = 0;
        Set<Integer> usedIndices = new HashSet<>();
        for (ClickableItemStack item : items.keySet()) {
            usedIndices.add(item.getSlot());
        }
        if (!layout.isAuto()){
            for (ClickableItemStack item : layout.getLayoutItems()){
                usedIndices.add(item.getSlot());
            }
        }

        while (usedIndices.contains(availableIndex) || contains(blockIndex, availableIndex)) {
            availableIndex++;
            if (availableIndex >= getSize()) {
                break;
            }
        }
        return availableIndex;
    }
    private boolean contains(int[] array, int value) {
        Set<Integer> set = Arrays.stream(array).boxed().collect(Collectors.toSet());
        return set.contains(value);
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
        inventory = Bukkit.createInventory(this,size, ColorBuilder.convertColors(title));
        buildLayout();
        for (ClickableItemStack item : items.keySet().stream().sorted(Comparator.comparingInt(ClickableItemStack::getSlot)).toList()){
            try {
                inventory.setItem(item.getSlot(), item.getItemStack());
            }catch (Exception ex){
                continue;
            }
        }
    }
    public void build(Map<ClickableItemStack, Consumer<InventoryClickEvent>> items){
        clear();
        this.items = items;
        build();
    }

    private void buildLayout(){
        if (!layout.isAuto()){
            for (ClickableItemStack item : layout.getLayoutItems()){ // add firstly layout items to inventory
                inventory.setItem(item.getSlot(), item.getItemStack());
            }
        }else {
            ItemStack layoutItem = layout.getDefaultItem();
            Set<Integer> usedIndices = items.keySet().stream().map(ClickableItemStack::getSlot).collect(Collectors.toSet());
            for (int index = 0; index < size; index++){
                if (usedIndices.contains(index)) continue;
                inventory.setItem(index,layoutItem);
            }
        }
    }

    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
        e.setCancelled(true);

        if (e.getClickedInventory().getType() == org.bukkit.event.inventory.InventoryType.PLAYER) {
            return;
        }

        if (e.getClick() == ClickType.DOUBLE_CLICK) {
            return;
        }

        if (e.getAction() == InventoryAction.HOTBAR_SWAP
                || e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            return;
        }

        Optional<ClickableItemStack> opt = items.keySet().stream()
                .filter(item -> item.getSlot() == e.getSlot()).findFirst();
        if (opt.isEmpty()) return;

        ClickableItemStack clickable = opt.get();

        try {
            items.get(clickable).accept(e);
            ((Player) e.getWhoClicked()).playSound(
                    e.getWhoClicked(), clickable.getSound(), 3f, 1f
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().equals(getInventory())) {
            e.setCancelled(true);
        }
    }

    public void handleClose(InventoryCloseEvent e){
        if (!e.getInventory().equals(getInventory())){return;}
        if (closeEvent != null) {
            closeEvent.accept(e);
        }
    }

    public void handleOpen(InventoryOpenEvent e){
        if (!e.getInventory().equals(getInventory())){return;}
        if (openEvent!= null) {
            openEvent.accept(e);
        }
    }
}

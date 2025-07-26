package net.weesli.rozslib.inventory;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.RozsLibService;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.enums.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter@Setter
public abstract class AbstractInventory implements Listener {

    private InventoryType type;
    private int[] blockIndex;

    private String title;
    // default size is 9 (1 column)
    private int size = 9;
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
        Bukkit.getPluginManager().registerEvents(this, RozsLibService.getPlugin());  // register this class to service plugin
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
            if (availableIndex > getSize()) {
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
        inventory = Bukkit.createInventory(null,size, ColorBuilder.convertColors(title));
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

    @SuppressWarnings("unchecked")
    public ClickableItemStack getItemStackFromYaml(File file, String basePath){
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        String material = configuration.getString(basePath + ".material", "STONE");
        int amount = configuration.getInt(basePath + ".amount", 1);
        String displayName = configuration.getString(basePath + ".title", "");
        List<String> lore = (List<String>) configuration.getList(basePath + ".lore", new ArrayList<>());
        int slot = configuration.getInt(basePath + ".slot", 0);
        ItemStack itemStack = new ItemStack(Material.getMaterial(material), amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors(displayName));
        meta.setLore(lore.stream().map(ColorBuilder::convertColors).toList());
        itemStack.setItemMeta(meta);
        return new ClickableItemStack(itemStack, slot);
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

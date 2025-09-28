package net.weesli.rozslib.inventory.builder;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.util.StringsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;
@Setter@Getter
public class BaseItemBuilder {

    protected ItemStack itemStack;
    protected ItemMeta itemMeta;

    protected ItemMeta ensureMeta() {
        if (this.itemMeta == null) {
            this.itemMeta = this.itemStack.getItemMeta();
        }
        return this.itemMeta;
    }

    public static BaseItemBuilder of(Material material) {
        BaseItemBuilder b = new BaseItemBuilder();
        b.itemStack = new ItemStack(material);
        b.itemMeta = b.itemStack.getItemMeta();
        return b;
    }

    public static BaseItemBuilder of(ItemStack stack) {
        BaseItemBuilder b = new BaseItemBuilder();
        b.itemStack = stack.clone();
        b.itemMeta = b.itemStack.getItemMeta();
        return b;
    }

    public static BaseItemBuilder of(String material){
        BaseItemBuilder b = new BaseItemBuilder();
        b.itemStack = ItemFormatter.getMaterial(material);
        b.itemMeta = b.itemStack.getItemMeta();
        return b;
    }

    public BaseItemBuilder name(String name, @Nullable Player player, TagResolver... resolvers) {
        ensureMeta().displayName(StringsUtil.apply(name, player, resolvers));
        return this;
    }

    public BaseItemBuilder lore(List<String> lore, @Nullable Player player, TagResolver... resolvers) {
        if (lore == null || lore.isEmpty()) {
            ensureMeta().lore(null);
        } else {
            ensureMeta().lore(lore.stream().map(l -> StringsUtil.apply(l, player, resolvers)).toList());
        }
        return this;
    }

    public BaseItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public BaseItemBuilder hideFlags() {
        ensureMeta().addItemFlags(ItemFlag.values());
        return this;
    }

    public BaseItemBuilder customModelData(int cmd) {
        ensureMeta().setCustomModelData(cmd);
        return this;
    }

    public ItemStack build() {
        if (this.itemMeta != null) this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public ClickableItemStack asClickableItemStack(int slot) {
        return new ClickableItemStack(build(), slot);
    }

}

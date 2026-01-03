package net.weesli.rozslib.config.model;

import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.builder.BaseItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
@NoArgsConstructor
public class ConfigItemSection {
    private String material;
    private String title;
    private List<String> lore;
    private Integer amount;
    @ConfigKey("custom-model-data")
    private Integer customModelData;
    private Integer slot;

    public ItemStack toItemStack(Player player, TagResolver... tagResolvers){
        return createItemStack(player, tagResolvers);
    }

    public ClickableItemStack toClickableItemStack(Player player, TagResolver... tagResolvers){
        ItemStack itemStack = toItemStack(player, tagResolvers);
        return new ClickableItemStack(itemStack,slot);
    }
    private ItemStack createItemStack(Player player, TagResolver... tagResolvers){
        BaseItemBuilder builder = new BaseItemBuilder();
        @Nonnull Material realMaterial = (Material.getMaterial(material) == null ? Material.DIRT : Objects.requireNonNull(Material.getMaterial(material)));
        builder.setItemStack(new ItemStack(realMaterial));
        if (title != null) {
            builder.name(title,player,tagResolvers);
        }
        if (lore != null) {
            builder.lore(lore,player,tagResolvers);
        }
        if (amount != null) {
            builder.amount(amount);
        }
        if (customModelData != null) {
            builder.customModelData(customModelData);
        }
        return builder.build();
    }
}

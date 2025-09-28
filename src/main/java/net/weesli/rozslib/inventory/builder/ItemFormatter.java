package net.weesli.rozslib.inventory.builder;
import lombok.NonNull;
import net.weesli.rozslib.inventory.builder.hook.ItemsAdderHook;
import net.weesli.rozslib.inventory.builder.hook.SkullHook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemFormatter {

    private static final MaterialHook[] hooks = {
        new SkullHook(),
        new ItemsAdderHook()
    };

    public @NonNull
    static ItemStack getMaterial(String material) {
        for (MaterialHook hook : hooks) {
            if (hook.getPrefix().startsWith(material + "-")) {
                return hook.getItem(material.substring(material.indexOf("-") + 1));
            }
        }
        Material m = Material.getMaterial(material);
        if (m == null){
            return new ItemStack(Material.STONE);
        }
        return new ItemStack(m);
    }
}

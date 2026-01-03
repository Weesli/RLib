package net.weesli.rozslib.config.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.weesli.rozslib.inventory.types.PageableInventory;
import net.weesli.rozslib.inventory.types.SimpleInventory;

import java.util.Map;
@Getter@NoArgsConstructor
public class MenuSection {
    private String title;
    private Integer size;
    private Map<String, ConfigItemSection> items;

    public SimpleInventory toSimpleInventory() {
        return new SimpleInventory(title, size);
    }

    public PageableInventory toPageableInventory(ConfigItemSection previous, ConfigItemSection next, int... blocked) {
        return new PageableInventory(
                title,
                size,
                previous.toClickableItemStack(null), // navigation button haven't any placeholder :D
                next.toClickableItemStack(null), // navigation button haven't any placeholder :D
                blocked
        );
    }
}

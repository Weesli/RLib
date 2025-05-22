package net.weesli.rozslib.inventory.types;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.enums.InventoryType;
import net.weesli.rozslib.inventory.AbstractInventory;

@Getter@Setter
public class SimpleInventory extends AbstractInventory {

    public SimpleInventory(String title, int size) {
        super(title, size, InventoryType.SIMPLE);
    }

}

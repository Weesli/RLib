package net.weesli.rozsLib.inventory.types;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rozsLib.inventory.AbstractInventory;

@Getter@Setter
public class SimpleInventory extends AbstractInventory {

    public SimpleInventory(String title, int size) {
        super(title, size);
    }

}

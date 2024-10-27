package net.weesli.rozsLib.database.component;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Column {

    private String name;
    private String type;
    private int length;

    private boolean isPrimary = false;
    private boolean autoIncrement = false;

    public Column(String name, String type, int length) {
        this.name = name;
        this.type = type;
        this.length = length;
    }

    public Column setPrimary(boolean primary) {
        isPrimary = primary;
        return this;
    }

    public Column setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }


}

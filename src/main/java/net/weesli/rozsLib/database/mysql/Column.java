package net.weesli.rozsLib.database.mysql;

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


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }


}

package net.weesli.rozslib.format.model;

public record Placeholder(String name, String value) {

    public Placeholder(String name, Integer value) {
        this(name, String.valueOf(value));
    }

    public Placeholder(String name, Double value) {
        this(name, String.valueOf(value));
    }

    public Placeholder(String name, Float value) {
        this(name, String.valueOf(value));
    }

    public Placeholder(String name, Long value) {
        this(name, String.valueOf(value));
    }

    public Placeholder(String name, Boolean value) {
        this(name, String.valueOf(value));
    }
}

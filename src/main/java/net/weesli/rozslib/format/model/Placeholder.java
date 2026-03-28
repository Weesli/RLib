package net.weesli.rozslib.format.model;

public record Placeholder(String name, String value) {
    @Deprecated
    public Placeholder(String name, Integer value) {
        this(name, String.valueOf(value));
    }
    @Deprecated
    public Placeholder(String name, Double value) {
        this(name, String.valueOf(value));
    }
    @Deprecated
    public Placeholder(String name, Float value) {
        this(name, String.valueOf(value));
    }
    @Deprecated
    public Placeholder(String name, Long value) {
        this(name, String.valueOf(value));
    }
    @Deprecated
    public Placeholder(String name, Boolean value) {
        this(name, String.valueOf(value));
    }

    public static Placeholder of(String name, Integer value){
        return new Placeholder(name, String.valueOf(value));
    }

    public static Placeholder of(String name, Double value){
        return new Placeholder(name,String.valueOf(value));
    }

    public static Placeholder of(String name, Float value){
        return new Placeholder(name,String.valueOf(value));
    }
    public static Placeholder of(String name, Long value){
        return new Placeholder(name,String.valueOf(value));
    }
    public static Placeholder of(String name, Boolean value){
        return new Placeholder(name,String.valueOf(value));
    }
}

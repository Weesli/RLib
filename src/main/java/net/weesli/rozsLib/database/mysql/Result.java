package net.weesli.rozsLib.database.mysql;


import net.weesli.rozsLib.api.ItemSerializer;
import net.weesli.rozsLib.api.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Result {

    private ResultSet resultset;

    public Result(ResultSet resultset) {
        this.resultset = resultset;
    }

    public String getString(String path){
        try {
            return resultset.getString(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String path){
        try {
            return resultset.getInt(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean next(){
        try {
            return resultset.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLong(String path){
        try {
            return resultset.getLong(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double getDouble(String path){
        try {
            return resultset.getDouble(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getBoolean(String path){
        try {
            return resultset.getBoolean(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(String path){
        try {
            return resultset.getObject(path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getUUID(String path){
        try {
            return UUID.fromString(resultset.getString(path));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getStringList(String path) {
        try {
            return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).toList();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getIntList(String path) {
        try {
            return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Double> getDoubleList(String path) {
        try {
            return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).mapToDouble(Double::parseDouble).boxed().collect(Collectors.toList());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Boolean> getBooleanList(String path) {
        try {
            return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).map(Boolean::parseBoolean).collect(Collectors.toList());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getUUIDList(String path) {
        return getStringList(path).stream().filter(key -> {
            try {
                UUID.fromString(key);
                return true;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }).map(UUID::fromString).toList();
    }

    public Map getMap(String path){
        try {
            return resultset.getObject(path, Map.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // serializers

    public Location getLocation(String path){
        return new LocationSerializer().deserialize(path);
    }

    public ItemStack getItemStack(String path){
        return new ItemSerializer().deserialize(path);
    }

    public List<Location> getLocationList(String path) throws SQLException {
        LocationSerializer serializer = new LocationSerializer();
        return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).map(serializer::deserialize).collect(Collectors.toList());
    }

    public List<ItemStack> getItemStackList(String path) throws SQLException {
        ItemSerializer serializer = new ItemSerializer();
        return Arrays.stream(resultset.getString(path).replace("[", "").replace("]", "").split(", ")).map(serializer::deserialize).collect(Collectors.toList());
    }

}

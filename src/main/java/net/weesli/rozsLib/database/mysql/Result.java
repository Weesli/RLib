package net.weesli.rozsLib.database.mysql;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public Map getMap(String path){
        try {
            return resultset.getObject(path, Map.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

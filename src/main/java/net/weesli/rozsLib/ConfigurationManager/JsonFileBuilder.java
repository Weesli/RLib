package net.weesli.rozsLib.ConfigurationManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class JsonFileBuilder {

    private Plugin plugin;
    private String fileName;
    private File path;
    private JSONObject object;

    private boolean isResource;

    public JsonFileBuilder(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        path = plugin.getDataFolder();
    }

    public JsonFileBuilder setResource(boolean isResource){
        this.isResource = isResource;
        return this;
    }

    public JsonFileBuilder setFilePath(File path){
        this.path = path;
        return this;
    }

    public void load() throws IOException {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(new File(path, fileName + ".json"))) {
            Object object = parser.parse(reader);
            if (object instanceof JSONObject) {
                this.object = (JSONObject) object;
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid JSON format in file: " + fileName + ".json");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(){
        File file = new File(path, fileName + ".json");
        if (!file.exists()) {
            try {
                if (isResource) {
                    plugin.saveResource(fileName + ".json", false);
                    Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Resource file loaded: " + fileName + ".json");
                } else {
                    if (!path.exists()) {
                        if (path.mkdirs()) {
                            Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Directory created: " + path.getAbsolutePath());
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: Failed to create directory: " + path.getAbsolutePath());
                            return;
                        }
                    }
                    if (file.createNewFile()) {
                        Bukkit.getServer().getConsoleSender().sendMessage("[RLib] File created successfully: " + fileName + ".json");
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: File already exists or failed to create: " + fileName + ".json");
                    }
                }
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: Failed to create file");
            }
        }
        try {
            load();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error reading JSON file: " + fileName + ".json");
        }
    }

    public JSONObject getObject() {
        if (object == null){
            try {
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    public boolean isResource() {
        return isResource;
    }

    // Optional methods for getters and setters

    public String getString(String key) {
        return getObject().get(key).toString();
    }

    public void setString(String key, String value) {
        getObject().put(key, value);
    }

    public int getInt(String key) {
        return (int) getObject().get(key);
    }

    public void setInt(String key, int value) {
        getObject().put(key, value);
    }

    public long getLong(String key) {
        return (long) getObject().get(key);
    }

    public void setLong(String key, long value) {
        getObject().put(key, value);
    }

    public boolean getBoolean(String key) {
        return (boolean) getObject().get(key);
    }

    public void setBoolean(String key, boolean value) {
        getObject().put(key, value);
    }

    public double getDouble(String key) {
        return (double) getObject().get(key);
    }

    public void setDouble(String key, double value) {
        getObject().put(key, value);
    }

    public JSONObject getJSONObject(String key) {
        return (JSONObject) getObject().get(key);
    }

    public void setJSONObject(String key, JSONObject value) {
        getObject().put(key, value);
    }

    public JSONArray getJSONArray(String key) {
        return (JSONArray) getObject().get(key);
    }

    public void setJSONArray(String key, JSONArray value) {
        getObject().put(key, value);
    }

    /**
     * More methods coming soon
     */

}

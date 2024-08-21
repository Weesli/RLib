package net.weesli.rozsLib.ConfigurationManager;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class YamlFileBuilder {

    private Plugin plugin;
    private String fileName;
    private File path;
    private File file;
    private FileConfiguration configuration;

    private boolean isResource = false;

    public YamlFileBuilder(Plugin plugin, String fileName){
        this.fileName = fileName;
        this.plugin = plugin;
        this.path = plugin.getDataFolder();
        this.file = new File(path, fileName + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlFileBuilder setPath(File path){
        this.path = path;
        this.file = new File(path, fileName + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this;
    }

    public YamlFileBuilder setResource(boolean isResource){
        this.isResource = isResource;
        return this;
    }

    public void save(){
        try {
            if (configuration != null) {
                configuration.save(file);
            }
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: Failed to save configuration");
        }
    }

    public FileConfiguration load(){
        if (!file.exists()) {
            create();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
        return configuration;
    }

    public void create() {
        if (!file.exists()) {
            try {
                if (isResource) {
                    plugin.saveResource(fileName + ".yml", false);
                    Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Resource file loaded: " + fileName + ".yml");
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
                        Bukkit.getServer().getConsoleSender().sendMessage("[RLib] File created successfully: " + fileName + ".yml");
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: File already exists or failed to create: " + fileName + ".yml");
                    }
                }
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Error: Failed to create file");
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        if (isResource) {
            InputStream defConfigStream = plugin.getResource(fileName + ".yml");
            if (defConfigStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
                for (String key : defaultConfig.getKeys(true)) {
                    if (!configuration.contains(key)) {
                        configuration.set(key, defaultConfig.get(key));
                    }
                }
                save();
                Bukkit.getServer().getConsoleSender().sendMessage("[RLib] Config updated with defaults: " + fileName + ".yml");
            }
        }
    }


    public void reload() {
        File newfile = new File(path, fileName + ".yml");
        configuration = YamlConfiguration.loadConfiguration(newfile);
    }

    public String getFileName(){
        return fileName;
    }

    public boolean isResource() {
        return isResource;
    }

    // optional configuration getters and setters

    public ItemStack getItemStack(String path){
        ItemStack itemStack = new ItemStack(Material.getMaterial(configuration.getString(path + ".material")));
        ItemMeta meta = itemStack.getItemMeta();
        if (configuration.get(path + ".title") != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', configuration.getString(path + ".title")));
        List<String> lores = new ArrayList<>();
        if (configuration.get(path + ".lore") != null) for(String line : configuration.getStringList(path + ".lore")){lores.add(ChatColor.translateAlternateColorCodes('&', line));}
        meta.setLore(lores);
        if (configuration.get(path + ".custom-model-data") != null && configuration.getInt(path + ".custom-model-data") != 0){
            meta.setCustomModelData(configuration.getInt(path + ".custom-model-data"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack getItemStackWithPlaceholder(Player player, String path){
        ItemStack itemStack = new ItemStack(Material.getMaterial(configuration.getString(path + ".material")));
        ItemMeta meta = itemStack.getItemMeta();
        if (configuration.get(path + ".title") != null) meta.setDisplayName(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', configuration.getString(path + ".title"))));
        List<String> lores = new ArrayList<>();
        if (configuration.get(path + ".lore") != null) for(String line : configuration.getStringList(path + ".lore")){lores.add(PlaceholderAPI.setPlaceholders(player,ChatColor.translateAlternateColorCodes('&', line)));}
        meta.setLore(lores);
        if (configuration.get(path + ".custom-model-data") != null && configuration.getInt(path + ".custom-model-data") != 0){
            meta.setCustomModelData(configuration.getInt(path + ".custom-model-data"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public Location getLocation(String path){
        World world = Bukkit.getWorld(configuration.getString(path + ".world"));
        double x = configuration.getDouble(path + ".x");
        double y = configuration.getDouble(path + ".y");
        double z = configuration.getDouble(path + ".z");
        float yaw = (float) configuration.getDouble(path + ".yaw");
        float pitch = (float) configuration.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setLocation(String path, Location location){
        configuration.set(path + ".world", location.getWorld().getName());
        configuration.set(path + ".x", location.getX());
        configuration.set(path + ".y", location.getY());
        configuration.set(path + ".z", location.getZ());
        configuration.set(path + ".yaw", location.getYaw());
        configuration.set(path + ".pitch", location.getPitch());
        save();
    }
}

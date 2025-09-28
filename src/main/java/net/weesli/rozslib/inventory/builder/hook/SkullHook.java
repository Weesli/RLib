package net.weesli.rozslib.inventory.builder.hook;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.weesli.rozslib.RozsLibService;
import net.weesli.rozslib.inventory.builder.MaterialHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullHook implements MaterialHook {

    private final Map<String, String> cache = new HashMap<>();

    @Override
    public ItemStack getItem(String object) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        String url;
        if (cache.containsKey(object)) {
            url = cache.get(object);
        }else {
            url = getUrl(object);
            cache.put(object, url);
        }
        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "-rlib-");
            PlayerTextures textures = profile.getTextures();
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            textures.setSkin(new URL(url), PlayerTextures.SkinModel.CLASSIC);
            profile.setTextures(textures);

            Bukkit.getScheduler().runTaskAsynchronously(RozsLibService.getPlugin(), () -> {
                try {
                    profile.update().get();
                    Bukkit.getScheduler().runTask(RozsLibService.getPlugin(), () -> {
                        meta.setPlayerProfile(profile);
                        itemStack.setItemMeta(meta);
                    });
                } catch (Exception ignored) {}
            });

        }catch (Exception e) {
            return itemStack;
        }
        return itemStack;
    }

    private String getUrl(String object) {
        byte[] data = Base64.getDecoder().decode(object);
        JsonObject jsonObject = JsonParser.parseString(new String(data)).getAsJsonObject();
        JsonObject textures = jsonObject.get("textures").getAsJsonObject();
        JsonObject skin = textures.get("SKIN").getAsJsonObject();
        return skin.get("url").getAsString();
    }

    @Override
    public String getPrefix() {
        return "skull";
    }
}

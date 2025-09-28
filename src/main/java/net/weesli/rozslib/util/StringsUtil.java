package net.weesli.rozslib.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class StringsUtil {

    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    public static Component applyColors(String input, TagResolver... tagResolvers) {
        return ColorBuilder.convertColors(input, tagResolvers);
    }

    public static String applyPlaceholderApi(String input, Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return input;
        return PlaceholderAPI.setPlaceholders(player, input);
    }

    public static Component apply(String input, @Nullable Player player, TagResolver... tagResolvers) {
        if (player != null){
            input = applyPlaceholderApi(input, player);
        }
        return applyColors(input, tagResolvers);
    }

    public static String applyAsString(String input, @Nullable Player player) {
        if (player != null){
            input = applyPlaceholderApi(input, player);
        }
        return serializer.serialize(applyColors(input));
    }

    public static LegacyComponentSerializer getSerializer() {
        return serializer;
    }
}

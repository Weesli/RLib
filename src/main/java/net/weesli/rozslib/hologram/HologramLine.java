package net.weesli.rozslib.hologram;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Getter
public class HologramLine {

    public enum Billboard {
        FIXED((byte) 0),
        VERTICAL((byte) 1),
        HORIZONTAL((byte) 2),
        CENTER((byte) 3);

        @Getter
        private final byte value;

        Billboard(byte value) {
            this.value = value;
        }
    }

    private Function<Player, Component> textProvider;

    private Billboard billboard = Billboard.CENTER;
    private int backgroundColor = 0x00000000;
    private byte textOpacity = -1;
    private boolean shadow = false;
    private boolean seeThrough = false;
    private boolean defaultBackground = false;

    public HologramLine(String text) {
        this.textProvider = p -> legacy(text);
    }

    public HologramLine(Function<Player, String> textFunction) {
        this.textProvider = p -> legacy(textFunction.apply(p));
    }

    public HologramLine(Function<Player, Component> componentProvider, boolean isComponent) {
        this.textProvider = componentProvider;
    }

    public HologramLine text(String text) {
        this.textProvider = p -> legacy(text);
        return this;
    }

    public HologramLine text(Function<Player, String> fn) {
        this.textProvider = p -> legacy(fn.apply(p));
        return this;
    }

    public HologramLine component(Function<Player, Component> provider) {
        this.textProvider = provider;
        return this;
    }

    public HologramLine billboard(Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    public HologramLine backgroundColor(int argb) {
        this.backgroundColor = argb;
        return this;
    }

    public HologramLine textOpacity(byte opacity) {
        this.textOpacity = opacity;
        return this;
    }

    public HologramLine shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public HologramLine seeThrough(boolean value) {
        this.seeThrough = value;
        return this;
    }

    public HologramLine defaultBackground(boolean value) {
        this.defaultBackground = value;
        return this;
    }

    public Component getText(Player player) {
        String text = LegacyComponentSerializer.legacyAmpersand().serialize(textProvider.apply(player));
        if (org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text.replace('§', '&'));
    }

    public byte getStyleFlags() {
        byte flags = 0;
        if (shadow)
            flags |= 0x01;
        if (seeThrough)
            flags |= 0x02;
        if (defaultBackground)
            flags |= 0x04;
        return flags;
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
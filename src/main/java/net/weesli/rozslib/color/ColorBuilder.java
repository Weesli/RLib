package net.weesli.rozslib.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorBuilder {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("\\{#([0-9A-Fa-f]{6})>}(.+?)\\{#([0-9A-Fa-f]{6})<}");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(&?#[0-9A-Fa-f]{6})");

    public static String convertColors(String input) {
        Component component = processGradients(input);

        String processed = LegacyComponentSerializer.legacySection().serialize(component);
        processed = processHexColors(processed);

        processed = ChatColor.translateAlternateColorCodes('&', processed);

        return processed.replaceAll("(?<!&[0-9a-fk-or])&(?!&[0-9a-fk-or])", "");
    }

    private static Component processGradients(String input) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                builder.append(Component.text(input.substring(lastIndex, matcher.start())));
            }

            Color startColor = new Color(Integer.parseInt(matcher.group(1), 16));
            String content = matcher.group(2);
            Color endColor = new Color(Integer.parseInt(matcher.group(3), 16));

            builder.append(createGradient(content, startColor, endColor));
            lastIndex = matcher.end();
        }

        if (lastIndex < input.length()) {
            builder.append(Component.text(input.substring(lastIndex)));
        }

        return builder.build();
    }

    private static TextComponent createGradient(String text, Color start, Color end) {
        TextComponent.Builder gradientBuilder = Component.text();
        int length = text.length();

        if (length == 0) {
            return gradientBuilder.build();
        }

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
            int green = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
            int blue = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);

            gradientBuilder.append(Component.text(text.charAt(i),
                    TextColor.color(red, green, blue)));
        }

        return gradientBuilder.build();
    }

    private static String processHexColors(String input) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String cleanHex = hexCode.replaceFirst("&?", "");
            matcher.appendReplacement(buffer, convertHexToMinecraftFormat(cleanHex));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String convertHexToMinecraftFormat(String hex) {
        String cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;
        return "§x" + cleanHex.toLowerCase().replaceAll("(.)", "§$1");
    }
}
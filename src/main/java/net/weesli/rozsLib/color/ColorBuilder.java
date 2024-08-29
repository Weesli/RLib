package net.weesli.rozsLib.color;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ColorBuilder {

    private static final Pattern RGB_PATTERN = Pattern.compile("\\{#([0-9A-Fa-f]{6})\\}");
    private static final Pattern GRADIENT_DETAIL_PATTERN = Pattern.compile("\\{#([0-9A-Fa-f]{6})>\\}(.*?)\\{#([0-9A-Fa-f]{6})<\\}");

    public static String convertColors(String input) {
        input = applyGradient(input);
        input = applyRGB(input);
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private static String applyGradient(String input) {
        Matcher matcher = GRADIENT_DETAIL_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String startColor = matcher.group(1);
            String text = matcher.group(2);
            String endColor = matcher.group(3);
            input = input.replace(matcher.group(), colorizeGradient(text, new Color(Integer.parseInt(startColor, 16)), new Color(Integer.parseInt(endColor, 16))));
        }

        return input;
    }

    private static String applyRGB(String input) {
        Matcher matcher = RGB_PATTERN.matcher(input);
        while (matcher.find()) {
            String colorCode = matcher.group(1);
            input = input.replace(matcher.group(), net.md_5.bungee.api.ChatColor.of(new Color(Integer.parseInt(colorCode, 16))).toString());
        }
        return input;
    }

    private static String colorizeGradient(String text, Color startColor, Color endColor) {
        ChatColor[] gradientColors = createGradient(startColor, endColor, text.length());
        return applyColorsToText(text, gradientColors);
    }

    private static ChatColor[] createGradient(Color startColor, Color endColor, int length) {
        ChatColor[] colors = new ChatColor[length];
        int rStep = (endColor.getRed() - startColor.getRed()) / (length - 1);
        int gStep = (endColor.getGreen() - startColor.getGreen()) / (length - 1);
        int bStep = (endColor.getBlue() - startColor.getBlue()) / (length - 1);

        for (int i = 0; i < length; i++) {
            int r = startColor.getRed() + rStep * i;
            int g = startColor.getGreen() + gStep * i;
            int b = startColor.getBlue() + bStep * i;
            colors[i] = ChatColor.of(new Color(r, g, b));
        }
        return colors;
    }

    private static String applyColorsToText(String text, ChatColor[] colors) {
        StringBuilder coloredText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            coloredText.append(colors[i]).append(text.charAt(i));
        }
        return coloredText.toString();
    }

}

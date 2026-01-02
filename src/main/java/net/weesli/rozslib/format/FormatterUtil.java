package net.weesli.rozslib.format;

import net.weesli.rozslib.format.model.Placeholder;

public class FormatterUtil {

    public static String format(String input, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders) {
            input =input.replaceAll("<"+placeholder.name()+">", placeholder.value());
        }
        return input;
    }

}

package net.weesli.rozslib.color;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorBuilder {

    public static Component convertColors(String input, TagResolver... tagResolvers) {
        MiniMessage mm = MiniMessage.miniMessage();

        Component legacy = LegacyComponentSerializer.builder()
                .hexColors()
                .character('&')
                .build()
                .deserialize(input);

        String minimessage = mm.serialize(legacy).replace("\\", "");
        return mm.deserialize(minimessage, tagResolvers).decoration(TextDecoration.ITALIC, false);
    }

}
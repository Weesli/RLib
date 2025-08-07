package net.weesli.rozslib.color;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorBuilder {

    public static Component convertColors(String input, TagResolver... resolvers) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(input);
        String minimessage = mm.serialize(legacy).replace("\\", "");
        return mm.deserialize(minimessage, resolvers);
    }
}
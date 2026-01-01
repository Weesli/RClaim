package net.weesli.rclaim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
public class ChatUtil {
    public static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .build();

    public static TagResolver createTagResolver(String key, String value) {
        return TagResolver.builder().tag(key, Tag.inserting(Component.text(value))).build();
    }
}

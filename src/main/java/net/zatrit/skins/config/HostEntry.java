package net.zatrit.skins.config;

import lombok.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.zatrit.skins.util.command.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class HostEntry implements TextUtil.ToText {
    private final HostType type;
    private Map<String, ?> properties;

    @Override
    public void toText(@NotNull MutableText text) {
        val map = new HashMap<String, Object>();

        map.put("type", this.getType());
        map.put("properties", this.getProperties());

        text.append(new LiteralText("HostEntry")
                            .styled(style -> style.withFormatting(Formatting.RESET)));

        TextUtil.mapToText(text, map);
    }

    public enum HostType {
        DIRECT,
        FALLBACK,
        FIVEZIG,
        LOCAL,
        MINECRAFT_CAPES,
        MOJANG,
        NAMED_HTTP,
        OPTIFINE,
        VALHALLA,
    }
}

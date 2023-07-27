package net.zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.zatrit.skins.util.command.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.zatrit.skins.util.command.TextUtil.mapToText;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class HostEntry implements TextUtil.ToText {
    private final HostType type;
    private final Map<String, Object> properties;

    @Override
    public void toText(@NotNull MutableText text) {
        val map = new HashMap<String, Object>();

        map.put("type", this.getType());
        map.put("properties", this.getProperties());

        text.append(Text.literal("HostEntry")
                            .styled(style -> style.withFormatting(Formatting.RESET)));

        mapToText(text, map);
    }

    public enum HostType {
        MOJANG,
        NAMED_HTTP,
        OPTIFINE,
        LOCAL
    }
}

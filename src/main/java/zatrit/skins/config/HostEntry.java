package zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zatrit.skins.util.command.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class HostEntry implements TextUtil.ToText {
    private final HostType type;
    private Map<String, Object> properties;

    @Override
    public void toText(@NotNull MutableText text) {
        val map = new HashMap<String, Object>(2, 1);

        map.put("type", this.getType());
        map.put("properties", this.getProperties());

        text.append(Text.literal("HostEntry")
                        .styled(style -> style.withFormatting(Formatting.RESET)));

        TextUtil.mapToText(text, map);
    }

    public enum HostType {
        DIRECT,
        FALLBACK,
        FIVEZIG,
        GEYSER,
        LIQUID_BOUNCE,
        LOCAL,
        METEOR,
        MINECRAFT_CAPES,
        MOJANG,
        NAMED_HTTP,
        OPTIFINE,
        VALHALLA,
        WURST,
    }
}

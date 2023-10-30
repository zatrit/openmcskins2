package net.zatrit.skins.util.command;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Various utility functions for interacting with Minecraft text APIs.
 *
 * @see MutableText
 * @see Text
 * @see ToText
 */
@UtilityClass
public final class TextUtil {
    private static final List<?> SKIP_ELEMENTS = Lists.newArrayList(
            null,
            Collections.emptyMap(),
            Collections.emptyList()
    );

    /**
     * Converts {@link Map} into a nicely formatted {@link Text}.
     */
    @SuppressWarnings("unchecked")
    public static void mapToText(
            @NotNull MutableText text, @NotNull Map<String, ?> map) {
        val specialStyle = (UnaryOperator<net.minecraft.text.Style>) style -> style.withFormatting(
                Formatting.GRAY);

        text.append(new LiteralText("{").styled(specialStyle));

        boolean first = true;
        for (val entry : map.entrySet()) {
            val value = entry.getValue();

            if (SKIP_ELEMENTS.stream().anyMatch(e -> Objects.equals(e, value))) {
                continue;
            }

            if (!first) {
                text.append(new LiteralText(", ").styled(specialStyle));
            }
            first = false;

            text.append(new LiteralText(entry.getKey())
                                .styled(style -> style.withFormatting(Formatting.RESET)));
            text.append(new LiteralText(" = ").styled(specialStyle));

            if (value instanceof Map) {
                mapToText(text, (Map<String, ?>) value);
            } else if (value instanceof ToText) {
                ((ToText) value).toText(text);
            } else {
                val stringValue = value.toString();
                var mutableText = new LiteralText(stringValue)
                        .styled(style -> style.withFormatting(
                                Formatting.GREEN));

                if (isURL(stringValue)) {
                    val clickAction = new ClickEvent(
                            ClickEvent.Action.OPEN_URL,
                            stringValue
                    );
                    mutableText = mutableText.styled(s -> s.withFormatting(
                            Formatting.UNDERLINE).withClickEvent(clickAction));
                }

                text.append(mutableText);
            }
        }

        text.append(new LiteralText("}").styled(specialStyle));
    }

    /**
     * Converts int to text and colors it with {@link Formatting#GREEN}.
     *
     * @see Formatting
     */
    public static MutableText formatNumber(Number n) {
        return new LiteralText(String.valueOf(n)).formatted(Formatting.GREEN);
    }

    /**
     * @return true if the {@code string} is a URL.
     */
    // https://stackoverflow.com/a/41268655/12245612
    private static boolean isURL(String string) {
        try {
            var url = new URL(string);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public interface ToText {
        void toText(@NotNull MutableText text);

        default Text toText() {
            val text = new LiteralText("");
            this.toText(text);

            return text;
        }
    }
}

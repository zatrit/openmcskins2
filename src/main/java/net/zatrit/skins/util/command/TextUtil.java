package net.zatrit.skins.util.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Various utility functions for interacting with Minecraft text APIs.
 *
 * @see MutableText
 * @see Text
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextUtil {
    /**
     * Converts {@link Map} into a nicely formatted {@link Text}.
     */
    public static void mapToText(
            @NotNull MutableText text, @NotNull Map<String, ?> map) {
        val specialStyle = (UnaryOperator<net.minecraft.text.Style>) style -> style.withFormatting(
                Formatting.GRAY);

        text.append(Text.literal("{").styled(specialStyle));

        var first = true;
        for (val entry : map.entrySet()) {
            val value = entry.getValue();

            if (value == null) {
                continue;
            }

            if (!first) {
                text.append(Text.literal(", ").styled(specialStyle));
            }
            first = false;

            text.append(Text.literal(entry.getKey())
                                .styled(style -> style.withFormatting(Formatting.RESET)));
            text.append(Text.literal(" = ").styled(specialStyle));

            if (value instanceof Map) {
                //noinspection unchecked
                mapToText(text, (Map<String, ?>) value);
            } else if (value instanceof ToText) {
                ((ToText) value).toText(text);
            } else {
                val stringValue = value.toString();
                var mutableText = Text.literal(stringValue)
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

        text.append(Text.literal("}").styled(specialStyle));
    }

    /**
     * Converts int to text and colors it with {@link Formatting#GREEN}.
     *
     * @see Formatting
     */
    public static MutableText formatNumber(int n) {
        return Text.literal(String.valueOf(n)).formatted(Formatting.GREEN);
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
            val text = Text.empty();
            this.toText(text);

            return text;
        }
    }
}

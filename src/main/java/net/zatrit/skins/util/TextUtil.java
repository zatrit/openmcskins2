package net.zatrit.skins.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Various utility functions for interacting with Minecraft text APIs.
 *
 * @see MutableText
 * @see Text
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextUtil {
    private static final String SPECIAL = "(){}=,";

    /**
     * Formats as nice chat text the result of the
     * {@link #toString} method for classes annotated with
     * {@link lombok.ToString}.
     */
    public static @NotNull MutableText formatObject(@NotNull Object input) {
        // https://stackoverflow.com/a/59323411/12245612
        final var string = input.toString().replaceAll(
                "(?<=(, |\\())[^\\s(]+?=null(?:, )?",
                ""
        ).replaceFirst(", \\)$", ")");

        var identifier = true;

        final var builder = new StringBuilder();
        final var text = Text.empty();

        for (int i = 0; i < string.length(); i++) {
            var ch = String.valueOf(string.charAt(i));

            if (SPECIAL.contains(ch)) {
                final var buiderString = builder.toString();
                var builderText = Text.literal(buiderString)
                                          .formatted(identifier ?
                                                             Formatting.RESET :
                                                             Formatting.GREEN);

                if (!identifier && isURL(buiderString)) {
                    final var clickAction = new ClickEvent(ClickEvent.Action.OPEN_URL,
                            buiderString
                    );
                    builderText = builderText.styled(s -> s.withFormatting(
                            Formatting.UNDERLINE).withClickEvent(clickAction));
                }

                text.append(builderText);

                identifier = !ch.equals("=");
                if (ch.equals("=")) {
                    ch = " = ";
                }

                text.append(Text.literal(ch)
                                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                builder.setLength(0);
            } else {
                builder.append(ch);
            }
        }

        return text;
    }

    /**
     * Converts int to text and colors it with {@link Formatting#GREEN}.
     *
     * @see Formatting
     */
    public static MutableText formatNumber(int n) {
        return Text.literal(String.valueOf(n)).formatted(Formatting.GREEN);
    }

    // https://stackoverflow.com/a/41268655/12245612
    private static boolean isURL(String urlString) {
        try {
            var url = new URL(urlString);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}

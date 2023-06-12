package net.zatrit.skins.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextUtil {
    public static @NotNull MutableText formatObject(@NotNull Object input) {
        // https://stackoverflow.com/a/59323411/12245612
        final var string = input.toString().replaceAll(
                "(?<=(, |\\())[^\\s(]+?=null(?:, )?",
                ""
        ).replaceFirst(", \\)$", ")");

        final var text = Text.empty();
        final var special = "(){}=,";
        final var builder = new StringBuilder();

        var identifier = true;

        for (int i = 0; i < string.length(); i++) {
            var ch = String.valueOf(string.charAt(i));

            if (special.contains(ch)) {
                final var buiderString = builder.toString();
                var builderText = Text.literal(buiderString)
                                          .formatted(identifier ?
                                                             Formatting.RESET :
                                                             Formatting.GREEN);

                if (!identifier && isURL(buiderString)) {
                    builderText = builderText.formatted(Formatting.UNDERLINE);
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

    public static MutableText formatNumber(int n) {
        return Text.literal(String.valueOf(n)).formatted(Formatting.GREEN);
    }

    private static boolean isURL(String urlString) {
        try {
            var url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

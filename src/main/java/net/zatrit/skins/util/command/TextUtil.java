package net.zatrit.skins.util.command;

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
        /*
         {b=1, a  = null} => {b=1}
         {b=1, a=  {}} => {b=1}
         {b=1, a   =[]} => {b=1}
        */
        final var string = input.toString().replaceAll(
                ",?\\s*\\w*\\s*=\\s*(null|\\{}|\\[])",
                ""
        );

        // Determines whether the currently converted
        // part of the string is an identifier
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

    /**
     * @return true if the {@code string} is a URL.
     */
    private static boolean isURL(String string) {
        try {
            var url = new URL(string);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}

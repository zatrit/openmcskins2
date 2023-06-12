package net.zatrit.skins.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.text.Text;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class FileArgumentType implements ArgumentType<InputStream> {
    private final FileProvider[] providers;
    private final @Getter String extension;
    private final Collection<String> files = new HashSet<>();

    private static final SimpleCommandExceptionType NO_PRESET_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("openmcskins.command.no_preset"));

    @Override
    @SneakyThrows
    public InputStream parse(@NotNull StringReader reader) {
        if (reader.canRead() && reader.peek() != ' ') {
            final var file = reader.readString() + this.extension;
            final var stream = Arrays.stream(this.providers)
                                       .map(p -> p.getFile(file))
                                       .filter(Objects::nonNull).findFirst();

            if (stream.isPresent()) {
                return stream.get();
            }
        }

        throw NO_PRESET_EXCEPTION.create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
            CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        this.files.forEach(builder::suggest);

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return this.files;
    }

    public void refresh() {
        this.files.clear();

        var set = new HashSet<String>();

        Arrays.stream(providers).map(FileProvider::listFiles)
                .forEach(set::addAll);
        set.stream().filter(f -> f.endsWith(this.extension))
                .map(FilenameUtils::removeExtension).forEach(this.files::add);
    }
}

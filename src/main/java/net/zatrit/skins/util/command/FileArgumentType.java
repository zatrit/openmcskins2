package net.zatrit.skins.util.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.text.Text;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * An argument type that allows access to
 * mod resources and files in the mod folder.
 *
 * @see FileProvider
 * @see IndexedResourceProvider
 * @see DirectoryFileProvider
 */
@AllArgsConstructor
public class FileArgumentType implements ArgumentType<InputStream> {
    private static final SimpleCommandExceptionType NO_PRESET_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("openmcskins.command.no_preset"));
    private final FileProvider[] providers;
    private final @Getter String extension;
    private final Collection<String> files = new HashSet<>();

    @Override
    @SneakyThrows
    public InputStream parse(@NotNull StringReader reader) {
        if (reader.canRead() && reader.peek() != ' ') {
            val file = reader.readString() + "." + this.extension;
            val stream = Arrays.stream(this.providers)
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

    /**
     * Searches for all available files
     * and stores them in {@link #files}
     */
    public void refresh() {
        this.files.clear();

        val set = new HashSet<String>();

        Arrays.stream(providers).map(FileProvider::listFiles)
                .forEach(set::addAll);
        set.stream().filter(f -> FilenameUtils.isExtension(f, this.extension))
                .map(FilenameUtils::removeExtension).forEach(this.files::add);
    }
}

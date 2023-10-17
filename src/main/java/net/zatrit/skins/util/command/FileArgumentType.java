package net.zatrit.skins.util.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.minecraft.text.Text;
import net.zatrit.skins.SkinsClient;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
public class FileArgumentType implements ArgumentType<Path> {
    private static final SimpleCommandExceptionType NO_PRESET_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("openmcskins.command.noPreset"));
    private final FileProvider[] providers;
    private final @Getter String extension;
    private final Collection<String> files = new HashSet<>();

    @Override
    public Path parse(@NotNull StringReader reader)
            throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() != ' ') {
            val file = reader.readString() + "." + this.extension;
            val path = Arrays.stream(this.providers).map(p -> p.getFile(file))
                               .filter(Optional::isPresent).map(Optional::get)
                               .findFirst();

            if (path.isPresent()) {
                return path.get();
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

        Arrays.stream(this.providers).map(fileProvider -> {
            try {
                return fileProvider.listFiles();
            } catch (IOException e) {
                SkinsClient.getErrorHandler().accept(e);
                return Collections.<String>emptySet();
            }
        }).forEach(set::addAll);

        set.stream().filter(f -> FilenameUtils.isExtension(f, this.extension))
                .map(FilenameUtils::removeExtension).forEach(this.files::add);
    }
}

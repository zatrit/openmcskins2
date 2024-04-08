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
import net.minecraft.text.TranslatableText;
import net.zatrit.skins.SkinsClient;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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
        new TranslatableText("openmcskins.command.noPreset"));
    private final FileProvider[] providers;
    private final @Getter String extension;
    private final Collection<String> files = new HashSet<>();

    @Override
    public Path parse(@NotNull StringReader reader)
        throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() != ' ') {
            val file = reader.readString() + "." + this.extension;
            var path = Optional.<Path>empty();

            for (FileProvider provider : this.providers) {
                var optionalPath = provider.getFile(file);

                if (optionalPath.isPresent()) {
                    path = optionalPath;
                    break;
                }
            }

            if (path.isPresent()) {
                return path.get();
            }
        }

        throw NO_PRESET_EXCEPTION.create();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
        CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (String file : this.files) {
            builder.suggest(file);
        }

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

        for (val provider : this.providers) {
            try {
                set.addAll(provider.listFiles());
            } catch (IOException e) {
                SkinsClient.getErrorHandler().accept(e);
            }
        }

        for (val name : set) {
            if (FilenameUtils.isExtension(name, this.extension)) {
                this.files.add(FilenameUtils.removeExtension(name));
            }
        }
    }
}

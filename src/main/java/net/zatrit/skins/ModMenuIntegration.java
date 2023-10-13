package net.zatrit.skins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import lombok.val;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.skins.config.UuidMode;
import org.jetbrains.annotations.NotNull;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            val instance = SkinsClient.getConfigHolder();
            val defaults = instance.getDefaults();
            val config = instance.getConfig();

            val builder = ConfigBuilder.create().setParentScreen(parent)
                                  .setTitle(new TranslatableText(
                                          "openmcskins.options.title"))
                                  .setSavingRunnable(instance::save);
            val entryBuilder = builder.entryBuilder();

            builder.getOrCreateCategory(new TranslatableText(
                            "openmcskins.category.general"))
                    .addEntry(entryBuilder.startBooleanToggle(
                                    new TranslatableText(
                                            "openmcskins.option.cacheTextures"),
                                    config.isCacheTextures()
                            ).setDefaultValue(defaults::isCacheTextures)
                                      .setSaveConsumer(config::setCacheTextures)
                                      .build())
                    .addEntry(entryBuilder.startBooleanToggle(
                                    new TranslatableText("openmcskins.option.verboseLogs"),
                                    config.isVerboseLogs()
                            ).setDefaultValue(defaults::isVerboseLogs)
                                      .setSaveConsumer(config::setVerboseLogs)
                                      .build())
                    .addEntry(entryBuilder.startBooleanToggle(
                                    new TranslatableText(
                                            "openmcskins.option.refreshOnConfigSave"),
                                    config.isRefreshOnConfigSave()
                            ).setDefaultValue(defaults::isRefreshOnConfigSave)
                                      .setSaveConsumer(config::setRefreshOnConfigSave)
                                      .build())
                    .addEntry(entryBuilder.startEnumSelector(
                                    new TranslatableText("openmcskins.option.uuidMode"),
                                    UuidMode.class,
                                    config.getUuidMode()
                            ).setSaveConsumer(config::setUuidMode)
                                      .setDefaultValue(config::getUuidMode)
                                      .setEnumNameProvider(mode -> formatMode((UuidMode) mode))
                                      .build());

            return builder.build();
        };
    }

    private @NotNull Text formatMode(@NotNull UuidMode mode) {
        return new TranslatableText(
                "openmcskins.option.uuidMode." + mode.toString().toLowerCase());
    }
}

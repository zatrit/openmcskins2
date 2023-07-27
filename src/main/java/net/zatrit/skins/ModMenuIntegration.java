package net.zatrit.skins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.config.ConfigInstance;
import lombok.val;
import net.zatrit.skins.config.SkinsConfig;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.text.Text.translatable;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            @SuppressWarnings("unchecked")
            val instance = (ConfigInstance<SkinsConfig>) SkinsClient.getConfigHolder();

            return YetAnotherConfigLib.create(instance, this::initConfig)
                           .generateScreen(parent);
        };
    }

    /**
     * Initializes OpenMCSkins config for ConfigInstance.
     */
    private YetAnotherConfigLib.Builder initConfig(
            @NotNull SkinsConfig defaults,
            @NotNull SkinsConfig config,
            YetAnotherConfigLib.@NotNull Builder builder) {
        return builder.title(translatable("openmcskins.options.title"))
                       .category(initializeGeneralCategory(
                               defaults,
                               config,
                               ConfigCategory.createBuilder()
                       ).build());
    }

    /**
     * Adds general category options.
     */
    private ConfigCategory.Builder initializeGeneralCategory(
            @NotNull SkinsConfig defaults,
            @NotNull SkinsConfig config,
            ConfigCategory.@NotNull Builder category) {
        return category.name(translatable("openmcskins.category.general"))
                       .option(Option.<Boolean>createBuilder()
                                       .controller(BooleanControllerBuilder::create)
                                       .binding(
                                               defaults.cacheTextures,
                                               config::isCacheTextures,
                                               config::setCacheTextures
                                       ).name(translatable(
                                       "openmcskins.option.cacheTextures"))
                                       .build())
                       .option(Option.<Boolean>createBuilder()
                                       .controller(BooleanControllerBuilder::create)
                                       .binding(
                                               defaults.verboseLogs,
                                               config::isVerboseLogs,
                                               config::setVerboseLogs
                                       ).name(translatable(
                                       "openmcskins.option.verboseLogs")).build())
                       .option(Option.<Boolean>createBuilder()
                                       .controller(BooleanControllerBuilder::create)
                                       .binding(
                                               defaults.refreshOnConfigSave,
                                               config::isRefreshOnConfigSave,
                                               config::setRefreshOnConfigSave
                                       ).name(translatable(
                                       "openmcskins.option.refreshOnConfigSave"))
                                       .build())
                       .option(Option.<Float>createBuilder()
                                       .controller(option -> FloatSliderControllerBuilder.create(
                                                       option).range(0.5f, 60f)
                                                                     .step(0.5f))
                                       .binding(
                                               defaults.loaderTimeout,
                                               config::getLoaderTimeout,
                                               config::setLoaderTimeout
                                       ).name(translatable(
                                       "openmcskins.option.loaderTimeout"))
                                       .build());
    }
}

package net.zatrit.skins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import lombok.val;
import net.minecraft.text.Text;
import net.zatrit.skins.config.SkinsConfig;
import net.zatrit.skins.config.UuidMode;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.text.Text.translatable;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            val instance = SkinsClient.getConfigHandler();

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
        return builder.title(translatable("openmcskins.options.title")).category(
                initializeGeneralCategory(
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
                .option(Option.<Boolean>createBuilder().controller(
                                BooleanControllerBuilder::create).binding(
                                defaults.isCacheTextures(),
                                config::isCacheTextures,
                                config::setCacheTextures
                        ).name(translatable("openmcskins.option.cacheTextures"))
                                .build())
                .option(Option.<Boolean>createBuilder().controller(
                                BooleanControllerBuilder::create).binding(
                                defaults.isVerboseLogs(),
                                config::isVerboseLogs,
                                config::setVerboseLogs
                        ).name(translatable("openmcskins.option.verboseLogs"))
                                .build())
                .option(Option.<Boolean>createBuilder().controller(
                                BooleanControllerBuilder::create).binding(
                                defaults.isRefreshOnConfigSave(),
                                config::isRefreshOnConfigSave,
                                config::setRefreshOnConfigSave
                        ).name(translatable(
                                "openmcskins.option.refreshOnConfigSave"))
                                .build())
                .option(Option.<Double>createBuilder()
                                .controller(option -> DoubleSliderControllerBuilder.create(
                                        option).range(0.5, 60.).step(0.5))
                                .binding(
                                        defaults.getLoaderTimeout(),
                                        config::getLoaderTimeout,
                                        config::setLoaderTimeout
                                ).name(translatable(
                                "openmcskins.option.loaderTimeout"))
                                .build())
                .option(Option.<UuidMode>createBuilder()
                                .controller(option -> EnumControllerBuilder.create(
                                                option).enumClass(UuidMode.class)
                                        .formatValue(
                                                this::formatMode))
                                .binding(
                                        defaults.getUuidMode(),
                                        config::getUuidMode,
                                        config::setUuidMode
                                ).name(translatable(
                                "openmcskins.option.uuidMode")).build());
    }

    private @NotNull Text formatMode(@NotNull UuidMode mode) {
        return translatable(
                "openmcskins.option.uuidMode." + mode.toString().toLowerCase());
    }
}

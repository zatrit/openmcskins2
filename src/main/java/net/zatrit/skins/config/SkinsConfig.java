package net.zatrit.skins.config;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkinsConfig {
    @SerialEntry private boolean cacheTextures = true;
    @SerialEntry private boolean verboseLogs = false;
    @SerialEntry private boolean refreshOnConfigSave = true;
    @SerialEntry private double loaderTimeout = 2;
    @SerialEntry private UuidMode uuidMode = UuidMode.OFFLINE;
    @SerialEntry private List<HostEntry> hosts = Lists.newArrayList(
        new HostEntry(HostEntry.HostType.MOJANG),
        new HostEntry(HostEntry.HostType.FALLBACK)
    );
}

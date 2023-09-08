package net.zatrit.skins.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkinsConfig {
    public boolean cacheTextures = true;
    public boolean verboseLogs = false;
    public boolean refreshOnConfigSave = true;
    public float loaderTimeout = 5f;
    public UuidMode uuidMode = UuidMode.OFFLINE;
    public List<HostEntry> hosts = Lists.newArrayList(
            new HostEntry(HostEntry.HostType.MOJANG),
            new HostEntry(HostEntry.HostType.FALLBACK)
    );
}

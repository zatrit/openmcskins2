package net.zatrit.skins.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.List;

@Getter
@Setter
@Config(name = "openmcskins")
public class SkinsConfig implements ConfigData {
    private boolean cacheTextures = true;
    private boolean verboseLogs = false;
    private boolean refreshOnConfigSave = true;
    private UuidMode uuidMode = UuidMode.OFFLINE;
    private List<HostEntry> hosts = Lists.newArrayList(new HostEntry(HostEntry.HostType.FALLBACK));
}

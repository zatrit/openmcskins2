package net.zatrit.skins.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "openmcskins")
public class SkinsConfig implements ConfigData {
    public boolean cacheTextures = true;
    public List<HostEntry> hosts = new ArrayList<>();
}

package net.zatrit.skins.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "openmcskins")
public class SkinsConfig implements ConfigData {
    public boolean cacheTextures = true;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int loaderTimeout = 5;
    @ConfigEntry.Gui.Excluded public List<HostEntry> hosts = new ArrayList<>();
}

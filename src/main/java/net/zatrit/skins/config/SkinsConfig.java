package net.zatrit.skins.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SkinsConfig {
    public boolean cacheTextures = true;
    public int loaderTimeout = 5;
    public List<HostEntry> hosts = new ArrayList<>();
    public boolean verboseLogs = false;
}

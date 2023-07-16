package net.zatrit.skins.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SkinsConfig {
    public boolean cacheTextures = true;
    public float loaderTimeout = 5f;
    public List<HostEntry> hosts = new ArrayList<>();
    public boolean verboseLogs = false;
}

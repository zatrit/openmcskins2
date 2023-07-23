package net.zatrit.skins.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class SkinsConfig {
    public boolean cacheTextures = true;
    public float loaderTimeout = 5f;
    public List<HostEntry> hosts = Lists.newArrayList(new HostEntry(
            HostEntry.HostType.MOJANG,
            Collections.emptyMap()
    ));
    public boolean verboseLogs = false;
}

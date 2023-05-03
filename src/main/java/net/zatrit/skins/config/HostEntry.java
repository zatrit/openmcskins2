package net.zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@AllArgsConstructor
public class HostEntry {
    private final @Nullable String tag;
    private final HostType type;
    private final Map<String, Object> properties;

    public enum HostType {
        MOJANG,
        NAMED_HTTP
    }
}

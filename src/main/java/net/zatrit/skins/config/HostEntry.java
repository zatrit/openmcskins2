package net.zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@AllArgsConstructor
public class HostEntry {
    private final @Nullable String tag;
    private final HostType type;
    private final Map<String, Object> properties;

    @AllArgsConstructor
    public enum HostType {
        MOJANG,
        NAMED_HTTP(new String[]{"base_url"});

        @Getter private final String[] params;

        HostType() {
            this(new String[0]);
        }
    }
}

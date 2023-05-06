package net.zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@AllArgsConstructor
public class HostEntry {
    private final @Nullable String tag;
    private final HostType type;
    private final Map<String, Object> properties;

    @NoArgsConstructor
    @AllArgsConstructor
    public enum HostType {
        MOJANG,
        NAMED_HTTP(new String[]{"base_url"});

        @Getter
        private String[] params = new String[0];
    }
}

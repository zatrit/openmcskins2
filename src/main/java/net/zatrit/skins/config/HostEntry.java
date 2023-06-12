package net.zatrit.skins.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class HostEntry {
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

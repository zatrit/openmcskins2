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

    public enum HostType {
        MOJANG,
        NAMED_HTTP,
        OPTIFINE;
    }
}

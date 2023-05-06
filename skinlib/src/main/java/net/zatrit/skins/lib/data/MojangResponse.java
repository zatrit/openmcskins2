package net.zatrit.skins.lib.data;

import lombok.Data;

import java.util.List;

public @Data class MojangResponse {
    private String id;
    private String name;
    private List<MojangProperty> properties;

    public static @Data class MojangProperty {
        private String name;
        private String value;
    }
}

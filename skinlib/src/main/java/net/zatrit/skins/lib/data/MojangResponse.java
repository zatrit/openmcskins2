package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MojangResponse {
    private String id;
    private String name;
    private List<MojangProperty> properties;

    @Getter
    @AllArgsConstructor
    public static class MojangProperty {
        private String name;
        private String value;
    }
}

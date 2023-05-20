package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class MojangResponse {
    private final String id;
    private final String name;
    private final List<MojangProperty> properties;

    @Getter
    @AllArgsConstructor
    @SuppressWarnings("ClassCanBeRecord")
    public static class MojangProperty {
        private final String name;
        private final String value;
    }
}

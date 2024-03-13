package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@ApiStatus.Internal
public class MojangResponse {
    private final String id;
    private final String name;
    private final List<MojangProperty> properties;

    @Getter
    @AllArgsConstructor
    public static class MojangProperty {
        private final String name;
        private final String value;
    }
}

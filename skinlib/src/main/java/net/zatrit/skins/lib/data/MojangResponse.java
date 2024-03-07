package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.JsonData;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@ApiStatus.Internal
@JsonData
public class MojangResponse {
    private final String id;
    private final String name;
    private final List<MojangProperty> properties;

    @Getter
    @AllArgsConstructor
    @JsonData
    public static class MojangProperty {
        private final String name;
        private final String value;
    }
}

package net.zatrit.skins.lib.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zatrit.skins.lib.JsonData;
import org.jetbrains.annotations.Nullable;

/**
 * A class that describes texture parameters
 * such as the model and whether it is animated.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonData
public class Metadata {
    private boolean animated = false;
    private @Nullable String model;
}

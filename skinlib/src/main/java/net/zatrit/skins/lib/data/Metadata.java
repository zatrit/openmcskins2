package net.zatrit.skins.lib.data;

import lombok.*;
import org.jetbrains.annotations.Nullable;

/**
 * A class that describes texture parameters
 * such as the model and whether it is animated.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private boolean animated = false;
    private @Nullable String model;
}

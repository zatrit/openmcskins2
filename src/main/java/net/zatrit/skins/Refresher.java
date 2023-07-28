package net.zatrit.skins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.zatrit.skins.accessor.HasPlayerListEntry;
import net.zatrit.skins.accessor.Refreshable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class Refresher {
    public void refresh(@NotNull Collection<AbstractClientPlayerEntity> entities) {
        entities.stream()
                .map(t -> ((HasPlayerListEntry) t).getPlayerInfo())
                .filter(Objects::nonNull)
                .forEach(e -> ((Refreshable) e).skins$refresh());
    }
}

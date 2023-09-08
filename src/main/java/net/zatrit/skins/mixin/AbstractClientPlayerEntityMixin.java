package net.zatrit.skins.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.skins.accessor.HasPlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements HasPlayerListEntry {
    @Invoker("getPlayerListEntry")
    public abstract PlayerListEntry getPlayerInfo();
}

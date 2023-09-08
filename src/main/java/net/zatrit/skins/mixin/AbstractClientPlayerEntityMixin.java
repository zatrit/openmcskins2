package net.zatrit.skins.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.zatrit.skins.accessor.HasPlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements HasPlayerListEntry {}

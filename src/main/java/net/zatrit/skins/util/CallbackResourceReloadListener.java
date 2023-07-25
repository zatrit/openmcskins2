package net.zatrit.skins.util;

import lombok.AllArgsConstructor;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@AllArgsConstructor
public class CallbackResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private final String name;
    private final Consumer<ResourceManager> callback;

    @Override
    public Identifier getFabricId() {
        return new Identifier("skins", this.name);
    }

    @Override
    public void reload(ResourceManager manager) {
        this.callback.accept(manager);
    }
}

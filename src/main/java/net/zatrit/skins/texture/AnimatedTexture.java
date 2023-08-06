package net.zatrit.skins.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import org.lwjgl.opengl.GL11;

/**
 * An optimized version of OpenMCSkins
 * <a href="https://github.com/zatrit/openmcskins/blob/main/src/main/java/net/zatrit/openmcskins/render/textures/AnimatedTexture.java">AnimatedTexture</a>.
 */
@RequiredArgsConstructor
public class AnimatedTexture extends AbstractTexture {
    private final long firstFrameTime = System.currentTimeMillis();
    private final NativeImage image;
    private final int frameTime;
    private int[] ids;
    private int framesCount;

    /*
     All of these functions are executed in the rendering thread,
     so any thread checks are unnecessary.
    */

    @Override
    public void load(ResourceManager manager) {
        val frameHeight = this.image.getWidth() / 2;

        this.framesCount = this.image.getHeight() / frameHeight;
        this.ids = new int[this.framesCount];

        GL11.glGenTextures(this.ids);

        for (int i = 0; i < this.framesCount; i++) {
            TextureUtil.prepareImage(
                    this.ids[i],
                    this.image.getWidth(),
                    frameHeight
            );

            this.image.upload(
                    0,
                    0,
                    0,
                    0,
                    frameHeight * i,
                    this.image.getWidth(),
                    frameHeight,
                    false,
                    false
            );
        }
    }

    @Override
    public void bindTexture() {
        val time = System.currentTimeMillis();
        val deltaTime = time - this.firstFrameTime;
        val frameIndex = (int) (deltaTime / this.frameTime) % this.framesCount;

        this.glId = this.ids[frameIndex];
        super.bindTexture();
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    public void clearGlId() {
        GlStateManager._deleteTextures(this.ids);

        for (int id : this.ids) {
            TextureUtil.releaseTextureId(id);
        }
    }
}
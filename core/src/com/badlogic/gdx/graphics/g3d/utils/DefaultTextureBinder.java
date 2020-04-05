// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Texture;
import java.nio.IntBuffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.GLTexture;

public final class DefaultTextureBinder implements TextureBinder
{
    public static final int ROUNDROBIN = 0;
    public static final int WEIGHTED = 1;
    public static final int MAX_GLES_UNITS = 32;
    private final int offset;
    private final int count;
    private final int reuseWeight;
    private final GLTexture[] textures;
    private final int[] weights;
    private final int method;
    private boolean reused;
    private int reuseCount;
    private int bindCount;
    private final TextureDescriptor tempDesc;
    private int currentTexture;
    
    public DefaultTextureBinder(final int method) {
        this(method, 0);
    }
    
    public DefaultTextureBinder(final int method, final int offset) {
        this(method, offset, -1);
    }
    
    public DefaultTextureBinder(final int method, final int offset, final int count) {
        this(method, offset, count, 10);
    }
    
    public DefaultTextureBinder(final int method, final int offset, int count, final int reuseWeight) {
        this.reuseCount = 0;
        this.bindCount = 0;
        this.tempDesc = new TextureDescriptor();
        this.currentTexture = 0;
        final int max = Math.min(getMaxTextureUnits(), 32);
        if (count < 0) {
            count = max - offset;
        }
        if (offset < 0 || count < 0 || offset + count > max || reuseWeight < 1) {
            throw new GdxRuntimeException("Illegal arguments");
        }
        this.method = method;
        this.offset = offset;
        this.count = count;
        this.textures = new GLTexture[count];
        this.reuseWeight = reuseWeight;
        this.weights = (int[])((method == 1) ? new int[count] : null);
    }
    
    private static int getMaxTextureUnits() {
        final IntBuffer buffer = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(34930, buffer);
        return buffer.get(0);
    }
    
    @Override
    public void begin() {
        for (int i = 0; i < this.count; ++i) {
            this.textures[i] = null;
            if (this.weights != null) {
                this.weights[i] = 0;
            }
        }
    }
    
    @Override
    public void end() {
        Gdx.gl.glActiveTexture(33984);
    }
    
    @Override
    public final int bind(final TextureDescriptor textureDesc) {
        return this.bindTexture(textureDesc, false);
    }
    
    @Override
    public final int bind(final GLTexture texture) {
        this.tempDesc.set(texture, null, null, null, null);
        return this.bindTexture(this.tempDesc, false);
    }
    
    private final int bindTexture(final TextureDescriptor textureDesc, final boolean rebind) {
        final GLTexture texture = textureDesc.texture;
        this.reused = false;
        int result = 0;
        switch (this.method) {
            case 0: {
                final int idx;
                result = this.offset + (idx = this.bindTextureRoundRobin(texture));
                break;
            }
            case 1: {
                final int idx;
                result = this.offset + (idx = this.bindTextureWeighted(texture));
                break;
            }
            default: {
                return -1;
            }
        }
        if (this.reused) {
            ++this.reuseCount;
            if (rebind) {
                texture.bind(result);
            }
            else {
                Gdx.gl.glActiveTexture(33984 + result);
            }
        }
        else {
            ++this.bindCount;
        }
        texture.unsafeSetWrap(textureDesc.uWrap, textureDesc.vWrap);
        texture.unsafeSetFilter(textureDesc.minFilter, textureDesc.magFilter);
        return result;
    }
    
    private final int bindTextureRoundRobin(final GLTexture texture) {
        for (int i = 0; i < this.count; ++i) {
            final int idx = (this.currentTexture + i) % this.count;
            if (this.textures[idx] == texture) {
                this.reused = true;
                return idx;
            }
        }
        this.currentTexture = (this.currentTexture + 1) % this.count;
        (this.textures[this.currentTexture] = texture).bind(this.offset + this.currentTexture);
        return this.currentTexture;
    }
    
    private final int bindTextureWeighted(final GLTexture texture) {
        int result = -1;
        int weight = this.weights[0];
        int windex = 0;
        for (int i = 0; i < this.count; ++i) {
            if (this.textures[i] == texture) {
                result = i;
                final int[] weights = this.weights;
                final int n = i;
                weights[n] += this.reuseWeight;
            }
            else {
                if (this.weights[i] >= 0) {
                    final int[] weights2 = this.weights;
                    final int n2 = i;
                    if (--weights2[n2] >= weight) {
                        continue;
                    }
                }
                weight = this.weights[i];
                windex = i;
            }
        }
        if (result < 0) {
            this.textures[windex] = texture;
            this.weights[windex] = 100;
            texture.bind(this.offset + (result = windex));
        }
        else {
            this.reused = true;
        }
        return result;
    }
    
    @Override
    public final int getBindCount() {
        return this.bindCount;
    }
    
    @Override
    public final int getReuseCount() {
        return this.reuseCount;
    }
    
    @Override
    public final void resetCounts() {
        final int n = 0;
        this.reuseCount = n;
        this.bindCount = n;
    }
}

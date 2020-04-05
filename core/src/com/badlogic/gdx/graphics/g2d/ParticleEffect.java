// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;
import java.io.File;
import java.io.InputStream;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.Writer;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ParticleEffect implements Disposable
{
    private final Array<ParticleEmitter> emitters;
    private BoundingBox bounds;
    private boolean ownsTexture;
    protected float xSizeScale;
    protected float ySizeScale;
    protected float motionScale;
    
    public ParticleEffect() {
        this.xSizeScale = 1.0f;
        this.ySizeScale = 1.0f;
        this.motionScale = 1.0f;
        this.emitters = new Array<ParticleEmitter>(8);
    }
    
    public ParticleEffect(final ParticleEffect effect) {
        this.xSizeScale = 1.0f;
        this.ySizeScale = 1.0f;
        this.motionScale = 1.0f;
        this.emitters = new Array<ParticleEmitter>(true, effect.emitters.size);
        for (int i = 0, n = effect.emitters.size; i < n; ++i) {
            this.emitters.add(this.newEmitter(effect.emitters.get(i)));
        }
    }
    
    public void start() {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).start();
        }
    }
    
    public void reset() {
        this.reset(true);
    }
    
    public void reset(final boolean resetScaling) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).reset();
        }
        if (resetScaling && (this.xSizeScale != 1.0f || this.ySizeScale != 1.0f || this.motionScale != 1.0f)) {
            this.scaleEffect(1.0f / this.xSizeScale, 1.0f / this.ySizeScale, 1.0f / this.motionScale);
            final float xSizeScale = 1.0f;
            this.motionScale = xSizeScale;
            this.ySizeScale = xSizeScale;
            this.xSizeScale = xSizeScale;
        }
    }
    
    public void update(final float delta) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).update(delta);
        }
    }
    
    public void draw(final Batch spriteBatch) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).draw(spriteBatch);
        }
    }
    
    public void draw(final Batch spriteBatch, final float delta) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).draw(spriteBatch, delta);
        }
    }
    
    public void allowCompletion() {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).allowCompletion();
        }
    }
    
    public boolean isComplete() {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            if (!emitter.isComplete()) {
                return false;
            }
        }
        return true;
    }
    
    public void setDuration(final int duration) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            emitter.setContinuous(false);
            emitter.duration = (float)duration;
            emitter.durationTimer = 0.0f;
        }
    }
    
    public void setPosition(final float x, final float y) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).setPosition(x, y);
        }
    }
    
    public void setFlip(final boolean flipX, final boolean flipY) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).setFlip(flipX, flipY);
        }
    }
    
    public void flipY() {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).flipY();
        }
    }
    
    public Array<ParticleEmitter> getEmitters() {
        return this.emitters;
    }
    
    public ParticleEmitter findEmitter(final String name) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getName().equals(name)) {
                return emitter;
            }
        }
        return null;
    }
    
    public void save(final Writer output) throws IOException {
        int index = 0;
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            if (index++ > 0) {
                output.write("\n");
            }
            emitter.save(output);
        }
    }
    
    public void load(final FileHandle effectFile, final FileHandle imagesDir) {
        this.loadEmitters(effectFile);
        this.loadEmitterImages(imagesDir);
    }
    
    public void load(final FileHandle effectFile, final TextureAtlas atlas) {
        this.load(effectFile, atlas, null);
    }
    
    public void load(final FileHandle effectFile, final TextureAtlas atlas, final String atlasPrefix) {
        this.loadEmitters(effectFile);
        this.loadEmitterImages(atlas, atlasPrefix);
    }
    
    public void loadEmitters(final FileHandle effectFile) {
        final InputStream input = effectFile.read();
        this.emitters.clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input), 512);
            do {
                final ParticleEmitter emitter = this.newEmitter(reader);
                this.emitters.add(emitter);
            } while (reader.readLine() != null);
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
        }
        finally {
            StreamUtils.closeQuietly(reader);
        }
        StreamUtils.closeQuietly(reader);
    }
    
    public void loadEmitterImages(final TextureAtlas atlas) {
        this.loadEmitterImages(atlas, null);
    }
    
    public void loadEmitterImages(final TextureAtlas atlas, final String atlasPrefix) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getImagePaths().size != 0) {
                final Array<Sprite> sprites = new Array<Sprite>();
                for (final String imagePath : emitter.getImagePaths()) {
                    String imageName = new File(imagePath.replace('\\', '/')).getName();
                    final int lastDotIndex = imageName.lastIndexOf(46);
                    if (lastDotIndex != -1) {
                        imageName = imageName.substring(0, lastDotIndex);
                    }
                    if (atlasPrefix != null) {
                        imageName = String.valueOf(atlasPrefix) + imageName;
                    }
                    final Sprite sprite = atlas.createSprite(imageName);
                    if (sprite == null) {
                        throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
                    }
                    sprites.add(sprite);
                }
                emitter.setSprites(sprites);
            }
        }
    }
    
    public void loadEmitterImages(final FileHandle imagesDir) {
        this.ownsTexture = true;
        final ObjectMap<String, Sprite> loadedSprites = new ObjectMap<String, Sprite>(this.emitters.size);
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getImagePaths().size != 0) {
                final Array<Sprite> sprites = new Array<Sprite>();
                for (final String imagePath : emitter.getImagePaths()) {
                    final String imageName = new File(imagePath.replace('\\', '/')).getName();
                    Sprite sprite = loadedSprites.get(imageName);
                    if (sprite == null) {
                        sprite = new Sprite(this.loadTexture(imagesDir.child(imageName)));
                        loadedSprites.put(imageName, sprite);
                    }
                    sprites.add(sprite);
                }
                emitter.setSprites(sprites);
            }
        }
    }
    
    protected ParticleEmitter newEmitter(final BufferedReader reader) throws IOException {
        return new ParticleEmitter(reader);
    }
    
    protected ParticleEmitter newEmitter(final ParticleEmitter emitter) {
        return new ParticleEmitter(emitter);
    }
    
    protected Texture loadTexture(final FileHandle file) {
        return new Texture(file, false);
    }
    
    @Override
    public void dispose() {
        if (!this.ownsTexture) {
            return;
        }
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            final ParticleEmitter emitter = this.emitters.get(i);
            for (final Sprite sprite : emitter.getSprites()) {
                sprite.getTexture().dispose();
            }
        }
    }
    
    public BoundingBox getBoundingBox() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox();
        }
        final BoundingBox bounds = this.bounds;
        bounds.inf();
        for (final ParticleEmitter emitter : this.emitters) {
            bounds.ext(emitter.getBoundingBox());
        }
        return bounds;
    }
    
    public void scaleEffect(final float scaleFactor) {
        this.scaleEffect(scaleFactor, scaleFactor, scaleFactor);
    }
    
    public void scaleEffect(final float scaleFactor, final float motionScaleFactor) {
        this.scaleEffect(scaleFactor, scaleFactor, motionScaleFactor);
    }
    
    public void scaleEffect(final float xSizeScaleFactor, final float ySizeScaleFactor, final float motionScaleFactor) {
        this.xSizeScale *= xSizeScaleFactor;
        this.ySizeScale *= ySizeScaleFactor;
        this.motionScale *= motionScaleFactor;
        for (final ParticleEmitter particleEmitter : this.emitters) {
            particleEmitter.scaleSize(xSizeScaleFactor, ySizeScaleFactor);
            particleEmitter.scaleMotion(motionScaleFactor);
        }
    }
    
    public void setEmittersCleanUpBlendFunction(final boolean cleanUpBlendFunction) {
        for (int i = 0, n = this.emitters.size; i < n; ++i) {
            this.emitters.get(i).setCleansUpBlendFunction(cleanUpBlendFunction);
        }
    }
}

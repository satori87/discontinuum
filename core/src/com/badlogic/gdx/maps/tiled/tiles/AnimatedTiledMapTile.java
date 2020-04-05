// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class AnimatedTiledMapTile implements TiledMapTile
{
    private static long lastTiledMapRenderTime;
    private int id;
    private BlendMode blendMode;
    private MapProperties properties;
    private MapObjects objects;
    private StaticTiledMapTile[] frameTiles;
    private int[] animationIntervals;
    private int frameCount;
    private int loopDuration;
    private static final long initialTimeOffset;
    
    static {
        AnimatedTiledMapTile.lastTiledMapRenderTime = 0L;
        initialTimeOffset = TimeUtils.millis();
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setId(final int id) {
        this.id = id;
    }
    
    @Override
    public BlendMode getBlendMode() {
        return this.blendMode;
    }
    
    @Override
    public void setBlendMode(final BlendMode blendMode) {
        this.blendMode = blendMode;
    }
    
    public int getCurrentFrameIndex() {
        int currentTime = (int)(AnimatedTiledMapTile.lastTiledMapRenderTime % this.loopDuration);
        for (int i = 0; i < this.animationIntervals.length; ++i) {
            final int animationInterval = this.animationIntervals[i];
            if (currentTime <= animationInterval) {
                return i;
            }
            currentTime -= animationInterval;
        }
        throw new GdxRuntimeException("Could not determine current animation frame in AnimatedTiledMapTile.  This should never happen.");
    }
    
    public TiledMapTile getCurrentFrame() {
        return this.frameTiles[this.getCurrentFrameIndex()];
    }
    
    @Override
    public TextureRegion getTextureRegion() {
        return this.getCurrentFrame().getTextureRegion();
    }
    
    @Override
    public void setTextureRegion(final TextureRegion textureRegion) {
        throw new GdxRuntimeException("Cannot set the texture region of AnimatedTiledMapTile.");
    }
    
    @Override
    public float getOffsetX() {
        return this.getCurrentFrame().getOffsetX();
    }
    
    @Override
    public void setOffsetX(final float offsetX) {
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }
    
    @Override
    public float getOffsetY() {
        return this.getCurrentFrame().getOffsetY();
    }
    
    @Override
    public void setOffsetY(final float offsetY) {
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }
    
    public int[] getAnimationIntervals() {
        return this.animationIntervals;
    }
    
    public void setAnimationIntervals(final int[] intervals) {
        if (intervals.length == this.animationIntervals.length) {
            this.animationIntervals = intervals;
            this.loopDuration = 0;
            for (int i = 0; i < intervals.length; ++i) {
                this.loopDuration += intervals[i];
            }
            return;
        }
        throw new GdxRuntimeException("Cannot set " + intervals.length + " frame intervals. The given int[] must have a size of " + this.animationIntervals.length + ".");
    }
    
    @Override
    public MapProperties getProperties() {
        if (this.properties == null) {
            this.properties = new MapProperties();
        }
        return this.properties;
    }
    
    @Override
    public MapObjects getObjects() {
        if (this.objects == null) {
            this.objects = new MapObjects();
        }
        return this.objects;
    }
    
    public static void updateAnimationBaseTime() {
        AnimatedTiledMapTile.lastTiledMapRenderTime = TimeUtils.millis() - AnimatedTiledMapTile.initialTimeOffset;
    }
    
    public AnimatedTiledMapTile(final float interval, final Array<StaticTiledMapTile> frameTiles) {
        this.blendMode = BlendMode.ALPHA;
        this.frameCount = 0;
        this.frameTiles = new StaticTiledMapTile[frameTiles.size];
        this.frameCount = frameTiles.size;
        this.loopDuration = frameTiles.size * (int)(interval * 1000.0f);
        this.animationIntervals = new int[frameTiles.size];
        for (int i = 0; i < frameTiles.size; ++i) {
            this.frameTiles[i] = frameTiles.get(i);
            this.animationIntervals[i] = (int)(interval * 1000.0f);
        }
    }
    
    public AnimatedTiledMapTile(final IntArray intervals, final Array<StaticTiledMapTile> frameTiles) {
        this.blendMode = BlendMode.ALPHA;
        this.frameCount = 0;
        this.frameTiles = new StaticTiledMapTile[frameTiles.size];
        this.frameCount = frameTiles.size;
        this.animationIntervals = intervals.toArray();
        this.loopDuration = 0;
        for (int i = 0; i < intervals.size; ++i) {
            this.frameTiles[i] = frameTiles.get(i);
            this.loopDuration += intervals.get(i);
        }
    }
    
    public StaticTiledMapTile[] getFrameTiles() {
        return this.frameTiles;
    }
}

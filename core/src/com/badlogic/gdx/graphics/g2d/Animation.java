// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.Array;

public class Animation<T>
{
    T[] keyFrames;
    private float frameDuration;
    private float animationDuration;
    private int lastFrameNumber;
    private float lastStateTime;
    private PlayMode playMode;
    
    public Animation(final float frameDuration, final Array<? extends T> keyFrames) {
        this.playMode = PlayMode.NORMAL;
        this.frameDuration = frameDuration;
        final Class arrayType = keyFrames.items.getClass().getComponentType();
        final Object[] frames = (Object[])ArrayReflection.newInstance(arrayType, keyFrames.size);
        for (int i = 0, n = keyFrames.size; i < n; ++i) {
            frames[i] = keyFrames.get(i);
        }
        this.setKeyFrames(frames);
    }
    
    public Animation(final float frameDuration, final Array<? extends T> keyFrames, final PlayMode playMode) {
        this(frameDuration, keyFrames);
        this.setPlayMode(playMode);
    }
    
    public Animation(final float frameDuration, final T... keyFrames) {
        this.playMode = PlayMode.NORMAL;
        this.frameDuration = frameDuration;
        this.setKeyFrames(keyFrames);
    }
    
    public T getKeyFrame(final float stateTime, final boolean looping) {
        final PlayMode oldPlayMode = this.playMode;
        if (looping && (this.playMode == PlayMode.NORMAL || this.playMode == PlayMode.REVERSED)) {
            if (this.playMode == PlayMode.NORMAL) {
                this.playMode = PlayMode.LOOP;
            }
            else {
                this.playMode = PlayMode.LOOP_REVERSED;
            }
        }
        else if (!looping && this.playMode != PlayMode.NORMAL && this.playMode != PlayMode.REVERSED) {
            if (this.playMode == PlayMode.LOOP_REVERSED) {
                this.playMode = PlayMode.REVERSED;
            }
            else {
                this.playMode = PlayMode.LOOP;
            }
        }
        final T frame = this.getKeyFrame(stateTime);
        this.playMode = oldPlayMode;
        return frame;
    }
    
    public T getKeyFrame(final float stateTime) {
        final int frameNumber = this.getKeyFrameIndex(stateTime);
        return this.keyFrames[frameNumber];
    }
    
    public int getKeyFrameIndex(final float stateTime) {
        if (this.keyFrames.length == 1) {
            return 0;
        }
        int frameNumber = (int)(stateTime / this.frameDuration);
        switch (this.playMode) {
            case NORMAL: {
                frameNumber = Math.min(this.keyFrames.length - 1, frameNumber);
                break;
            }
            case LOOP: {
                frameNumber %= this.keyFrames.length;
                break;
            }
            case LOOP_PINGPONG: {
                frameNumber %= this.keyFrames.length * 2 - 2;
                if (frameNumber >= this.keyFrames.length) {
                    frameNumber = this.keyFrames.length - 2 - (frameNumber - this.keyFrames.length);
                    break;
                }
                break;
            }
            case LOOP_RANDOM: {
                final int lastFrameNumber = (int)(this.lastStateTime / this.frameDuration);
                if (lastFrameNumber != frameNumber) {
                    frameNumber = MathUtils.random(this.keyFrames.length - 1);
                    break;
                }
                frameNumber = this.lastFrameNumber;
                break;
            }
            case REVERSED: {
                frameNumber = Math.max(this.keyFrames.length - frameNumber - 1, 0);
                break;
            }
            case LOOP_REVERSED: {
                frameNumber %= this.keyFrames.length;
                frameNumber = this.keyFrames.length - frameNumber - 1;
                break;
            }
        }
        this.lastFrameNumber = frameNumber;
        this.lastStateTime = stateTime;
        return frameNumber;
    }
    
    public T[] getKeyFrames() {
        return this.keyFrames;
    }
    
    protected void setKeyFrames(final T... keyFrames) {
        this.keyFrames = keyFrames;
        this.animationDuration = keyFrames.length * this.frameDuration;
    }
    
    public PlayMode getPlayMode() {
        return this.playMode;
    }
    
    public void setPlayMode(final PlayMode playMode) {
        this.playMode = playMode;
    }
    
    public boolean isAnimationFinished(final float stateTime) {
        final int frameNumber = (int)(stateTime / this.frameDuration);
        return this.keyFrames.length - 1 < frameNumber;
    }
    
    public void setFrameDuration(final float frameDuration) {
        this.frameDuration = frameDuration;
        this.animationDuration = this.keyFrames.length * frameDuration;
    }
    
    public float getFrameDuration() {
        return this.frameDuration;
    }
    
    public float getAnimationDuration() {
        return this.animationDuration;
    }
    
    public enum PlayMode
    {
        NORMAL("NORMAL", 0), 
        REVERSED("REVERSED", 1), 
        LOOP("LOOP", 2), 
        LOOP_REVERSED("LOOP_REVERSED", 3), 
        LOOP_PINGPONG("LOOP_PINGPONG", 4), 
        LOOP_RANDOM("LOOP_RANDOM", 5);
        
        private PlayMode(final String name, final int ordinal) {
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Vector2;

public enum Scaling
{
    fit("fit", 0), 
    fill("fill", 1), 
    fillX("fillX", 2), 
    fillY("fillY", 3), 
    stretch("stretch", 4), 
    stretchX("stretchX", 5), 
    stretchY("stretchY", 6), 
    none("none", 7);
    
    private static final Vector2 temp;
    
    static {
        temp = new Vector2();
    }
    
    private Scaling(final String name, final int ordinal) {
    }
    
    public Vector2 apply(final float sourceWidth, final float sourceHeight, final float targetWidth, final float targetHeight) {
        switch (this) {
            case fit: {
                final float targetRatio = targetHeight / targetWidth;
                final float sourceRatio = sourceHeight / sourceWidth;
                final float scale = (targetRatio > sourceRatio) ? (targetWidth / sourceWidth) : (targetHeight / sourceHeight);
                Scaling.temp.x = sourceWidth * scale;
                Scaling.temp.y = sourceHeight * scale;
                break;
            }
            case fill: {
                final float targetRatio = targetHeight / targetWidth;
                final float sourceRatio = sourceHeight / sourceWidth;
                final float scale = (targetRatio < sourceRatio) ? (targetWidth / sourceWidth) : (targetHeight / sourceHeight);
                Scaling.temp.x = sourceWidth * scale;
                Scaling.temp.y = sourceHeight * scale;
                break;
            }
            case fillX: {
                final float scale2 = targetWidth / sourceWidth;
                Scaling.temp.x = sourceWidth * scale2;
                Scaling.temp.y = sourceHeight * scale2;
                break;
            }
            case fillY: {
                final float scale2 = targetHeight / sourceHeight;
                Scaling.temp.x = sourceWidth * scale2;
                Scaling.temp.y = sourceHeight * scale2;
                break;
            }
            case stretch: {
                Scaling.temp.x = targetWidth;
                Scaling.temp.y = targetHeight;
                break;
            }
            case stretchX: {
                Scaling.temp.x = targetWidth;
                Scaling.temp.y = sourceHeight;
                break;
            }
            case stretchY: {
                Scaling.temp.x = sourceWidth;
                Scaling.temp.y = targetHeight;
                break;
            }
            case none: {
                Scaling.temp.x = sourceWidth;
                Scaling.temp.y = sourceHeight;
                break;
            }
        }
        return Scaling.temp;
    }
}

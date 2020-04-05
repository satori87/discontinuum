// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class Align
{
    public static final int center = 1;
    public static final int top = 2;
    public static final int bottom = 4;
    public static final int left = 8;
    public static final int right = 16;
    public static final int topLeft = 10;
    public static final int topRight = 18;
    public static final int bottomLeft = 12;
    public static final int bottomRight = 20;
    
    public static final boolean isLeft(final int align) {
        return (align & 0x8) != 0x0;
    }
    
    public static final boolean isRight(final int align) {
        return (align & 0x10) != 0x0;
    }
    
    public static final boolean isTop(final int align) {
        return (align & 0x2) != 0x0;
    }
    
    public static final boolean isBottom(final int align) {
        return (align & 0x4) != 0x0;
    }
    
    public static final boolean isCenterVertical(final int align) {
        return (align & 0x2) == 0x0 && (align & 0x4) == 0x0;
    }
    
    public static final boolean isCenterHorizontal(final int align) {
        return (align & 0x8) == 0x0 && (align & 0x10) == 0x0;
    }
    
    public static String toString(final int align) {
        final StringBuilder buffer = new StringBuilder(13);
        if ((align & 0x2) != 0x0) {
            buffer.append("top,");
        }
        else if ((align & 0x4) != 0x0) {
            buffer.append("bottom,");
        }
        else {
            buffer.append("center,");
        }
        if ((align & 0x8) != 0x0) {
            buffer.append("left");
        }
        else if ((align & 0x10) != 0x0) {
            buffer.append("right");
        }
        else {
            buffer.append("center");
        }
        return buffer.toString();
    }
}

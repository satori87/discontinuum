// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.Disposable;

public interface Cursor extends Disposable
{
    public enum SystemCursor
    {
        Arrow("Arrow", 0), 
        Ibeam("Ibeam", 1), 
        Crosshair("Crosshair", 2), 
        Hand("Hand", 3), 
        HorizontalResize("HorizontalResize", 4), 
        VerticalResize("VerticalResize", 5);
        
        private SystemCursor(final String name, final int ordinal) {
        }
    }
}

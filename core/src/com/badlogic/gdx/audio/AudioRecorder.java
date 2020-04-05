// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface AudioRecorder extends Disposable
{
    void read(final short[] p0, final int p1, final int p2);
    
    void dispose();
}

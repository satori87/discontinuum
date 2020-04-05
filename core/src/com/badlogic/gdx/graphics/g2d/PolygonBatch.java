// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public interface PolygonBatch extends Batch
{
    void draw(final PolygonRegion p0, final float p1, final float p2);
    
    void draw(final PolygonRegion p0, final float p1, final float p2, final float p3, final float p4);
    
    void draw(final PolygonRegion p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9);
    
    void draw(final Texture p0, final float[] p1, final int p2, final int p3, final short[] p4, final int p5, final int p6);
}

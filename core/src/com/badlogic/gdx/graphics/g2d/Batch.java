// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;

public interface Batch extends Disposable
{
    public static final int X1 = 0;
    public static final int Y1 = 1;
    public static final int C1 = 2;
    public static final int U1 = 3;
    public static final int V1 = 4;
    public static final int D1 = 5;
    public static final int E1 = 6;
    public static final int P1 = 7;
    public static final int Q1 = 8;
    public static final int X2 = 9;
    public static final int Y2 = 10;
    public static final int C2 = 11;
    public static final int U2 = 12;
    public static final int V2 = 13;
    public static final int D2 = 14;
    public static final int E2 = 15;
    public static final int P2 = 16;
    public static final int Q2 = 17;
    public static final int X3 = 18;
    public static final int Y3 = 19;
    public static final int C3 = 20;
    public static final int U3 = 21;
    public static final int V3 = 22;
    public static final int D3 = 23;
    public static final int E3 = 24;
    public static final int P3 = 25;
    public static final int Q3 = 26;
    public static final int X4 = 27;
    public static final int Y4 = 28;
    public static final int C4 = 29;
    public static final int U4 = 30;
    public static final int V4 = 31;
    public static final int D4 = 32;
    public static final int E4 = 33;
    public static final int P4 = 34;
    public static final int Q4 = 35;
    
    void begin();
    
    void end();
    
    void setColor(final Color p0);
    
    void setColor(final float p0, final float p1, final float p2, final float p3);
    
    Color getColor();
    
    void setPackedColor(final float p0);
    
    float getPackedColor();
    
    void draw(final Texture p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final int p10, final int p11, final int p12, final int p13, final boolean p14, final boolean p15);
    
    void draw(final Texture p0, final float p1, final float p2, final float p3, final float p4, final int p5, final int p6, final int p7, final int p8, final boolean p9, final boolean p10);
    
    void draw(final Texture p0, final float p1, final float p2, final int p3, final int p4, final int p5, final int p6);
    
    void draw(final Texture p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8);
    
    void draw(final Texture p0, final float p1, final float p2);
    
    void draw(final Texture p0, final float p1, final float p2, final float p3, final float p4);
    
    void draw(final Texture p0, final float[] p1, final int p2, final int p3);
    
    void draw(final TextureRegion p0, final float p1, final float p2);
    
    void draw(final TextureRegion p0, final float p1, final float p2, final float p3, final float p4);
    
    void draw(final TextureRegion p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9);
    
    void draw(final TextureRegion p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final boolean p10);
    
    void draw(final TextureRegion p0, final float p1, final float p2, final Affine2 p3);
    
    void flush();
    
    void disableBlending();
    
    void enableBlending();
    
    void setBlendFunction(final int p0, final int p1);
    
    void setBlendFunctionSeparate(final int p0, final int p1, final int p2, final int p3);
    
    int getBlendSrcFunc();
    
    int getBlendDstFunc();
    
    int getBlendSrcFuncAlpha();
    
    int getBlendDstFuncAlpha();
    
    Matrix4 getProjectionMatrix();
    
    Matrix4 getTransformMatrix();
    
    void setProjectionMatrix(final Matrix4 p0);
    
    void setTransformMatrix(final Matrix4 p0);
    
    void setShader(final ShaderProgram p0);
    
    ShaderProgram getShader();
    
    boolean isBlendingEnabled();
    
    boolean isDrawing();
}

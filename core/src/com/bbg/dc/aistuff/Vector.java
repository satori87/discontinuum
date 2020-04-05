// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

public class Vector
{
    public float xChange;
    public float yChange;
    public float direction;
    public float intensity;
    
    public Vector(final float direction, final float intensity) {
        this.direction = direction;
        this.intensity = intensity;
        this.xChange = (float)(Math.cos(direction - Math.toRadians(90.0)) * intensity);
        this.yChange = (float)(Math.sin(direction - Math.toRadians(90.0)) * intensity);
    }
    
    public static float distance(final float X1, final float Y1, final float X2, final float Y2) {
        return (float)Math.hypot(X2 - X1, Y2 - Y1);
    }
    
    public static float fixDir(float dir) {
        while (dir < 0.0f) {
            dir += (float)Math.toRadians(360.0);
        }
        while (dir >= Math.toRadians(360.0)) {
            dir -= (float)Math.toRadians(360.0);
        }
        return dir;
    }
    
    public static Vector byChange(final float xChange, final float yChange) {
        double theta = Math.atan2(yChange, xChange);
        theta += 1.5707963267948966;
        theta = fixDir((float)theta);
        return new Vector((float)theta, distance(0.0f, 0.0f, xChange, yChange));
    }
    
    public static float randomDir() {
        return (float)(Math.random() * Math.toRadians(360.0));
    }
    
    public static Coord rot(final float point2x, final float point2y, final float centerX, final float centerY, final float deg) {
        final double x = Math.toRadians(deg);
        final Coord c = new Coord();
        c.x = (float)(centerX + (point2x - centerX) * Math.cos(x) - (point2y - centerY) * Math.sin(x));
        c.y = (float)(centerY + (point2x - centerX) * Math.sin(x) + (point2y - centerY) * Math.cos(x));
        return c;
    }
    
    public static class Coord
    {
        public float x;
        public float y;
    }
    
    public static class Pos
    {
        public int x;
        public int y;
        
        public Pos(final int x, final int y) {
            this.x = 0;
            this.y = 0;
            this.x = x;
            this.y = y;
        }
    }
}

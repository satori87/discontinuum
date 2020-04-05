// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;

public class Bresenham2
{
    private final Array<GridPoint2> points;
    private final Pool<GridPoint2> pool;
    
    public Bresenham2() {
        this.points = new Array<GridPoint2>();
        this.pool = new Pool<GridPoint2>() {
            @Override
            protected GridPoint2 newObject() {
                return new GridPoint2();
            }
        };
    }
    
    public Array<GridPoint2> line(final GridPoint2 start, final GridPoint2 end) {
        return this.line(start.x, start.y, end.x, end.y);
    }
    
    public Array<GridPoint2> line(final int startX, final int startY, final int endX, final int endY) {
        this.pool.freeAll(this.points);
        this.points.clear();
        return this.line(startX, startY, endX, endY, this.pool, this.points);
    }
    
    public Array<GridPoint2> line(int startX, int startY, final int endX, final int endY, final Pool<GridPoint2> pool, final Array<GridPoint2> output) {
        final int w = endX - startX;
        final int h = endY - startY;
        int dx1 = 0;
        int dy1 = 0;
        int dx2 = 0;
        int dy2 = 0;
        if (w < 0) {
            dx1 = -1;
            dx2 = -1;
        }
        else if (w > 0) {
            dx1 = 1;
            dx2 = 1;
        }
        if (h < 0) {
            dy1 = -1;
        }
        else if (h > 0) {
            dy1 = 1;
        }
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (longest <= shortest) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0) {
                dy2 = -1;
            }
            else if (h > 0) {
                dy2 = 1;
            }
            dx2 = 0;
        }
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; ++i) {
            final GridPoint2 point = pool.obtain();
            point.set(startX, startY);
            output.add(point);
            numerator += shortest;
            if (numerator > longest) {
                numerator -= longest;
                startX += dx1;
                startY += dy1;
            }
            else {
                startX += dx2;
                startY += dy2;
            }
        }
        return output;
    }
}

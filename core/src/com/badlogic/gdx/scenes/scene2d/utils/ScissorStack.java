// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ScissorStack
{
    private static Array<Rectangle> scissors;
    static Vector3 tmp;
    static final Rectangle viewport;
    
    static {
        ScissorStack.scissors = new Array<Rectangle>();
        ScissorStack.tmp = new Vector3();
        viewport = new Rectangle();
    }
    
    public static boolean pushScissors(final Rectangle scissor) {
        fix(scissor);
        if (ScissorStack.scissors.size == 0) {
            if (scissor.width < 1.0f || scissor.height < 1.0f) {
                return false;
            }
            Gdx.gl.glEnable(3089);
        }
        else {
            final Rectangle parent = ScissorStack.scissors.get(ScissorStack.scissors.size - 1);
            final float minX = Math.max(parent.x, scissor.x);
            final float maxX = Math.min(parent.x + parent.width, scissor.x + scissor.width);
            if (maxX - minX < 1.0f) {
                return false;
            }
            final float minY = Math.max(parent.y, scissor.y);
            final float maxY = Math.min(parent.y + parent.height, scissor.y + scissor.height);
            if (maxY - minY < 1.0f) {
                return false;
            }
            scissor.x = minX;
            scissor.y = minY;
            scissor.width = maxX - minX;
            scissor.height = Math.max(1.0f, maxY - minY);
        }
        ScissorStack.scissors.add(scissor);
        HdpiUtils.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
        return true;
    }
    
    public static Rectangle popScissors() {
        final Rectangle old = ScissorStack.scissors.pop();
        if (ScissorStack.scissors.size == 0) {
            Gdx.gl.glDisable(3089);
        }
        else {
            final Rectangle scissor = ScissorStack.scissors.peek();
            HdpiUtils.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
        }
        return old;
    }
    
    public static Rectangle peekScissors() {
        return ScissorStack.scissors.peek();
    }
    
    private static void fix(final Rectangle rect) {
        rect.x = (float)Math.round(rect.x);
        rect.y = (float)Math.round(rect.y);
        rect.width = (float)Math.round(rect.width);
        rect.height = (float)Math.round(rect.height);
        if (rect.width < 0.0f) {
            rect.width = -rect.width;
            rect.x -= rect.width;
        }
        if (rect.height < 0.0f) {
            rect.height = -rect.height;
            rect.y -= rect.height;
        }
    }
    
    public static void calculateScissors(final Camera camera, final Matrix4 batchTransform, final Rectangle area, final Rectangle scissor) {
        calculateScissors(camera, 0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight(), batchTransform, area, scissor);
    }
    
    public static void calculateScissors(final Camera camera, final float viewportX, final float viewportY, final float viewportWidth, final float viewportHeight, final Matrix4 batchTransform, final Rectangle area, final Rectangle scissor) {
        ScissorStack.tmp.set(area.x, area.y, 0.0f);
        ScissorStack.tmp.mul(batchTransform);
        camera.project(ScissorStack.tmp, viewportX, viewportY, viewportWidth, viewportHeight);
        scissor.x = ScissorStack.tmp.x;
        scissor.y = ScissorStack.tmp.y;
        ScissorStack.tmp.set(area.x + area.width, area.y + area.height, 0.0f);
        ScissorStack.tmp.mul(batchTransform);
        camera.project(ScissorStack.tmp, viewportX, viewportY, viewportWidth, viewportHeight);
        scissor.width = ScissorStack.tmp.x - scissor.x;
        scissor.height = ScissorStack.tmp.y - scissor.y;
    }
    
    public static Rectangle getViewport() {
        if (ScissorStack.scissors.size == 0) {
            ScissorStack.viewport.set(0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
            return ScissorStack.viewport;
        }
        final Rectangle scissor = ScissorStack.scissors.peek();
        ScissorStack.viewport.set(scissor);
        return ScissorStack.viewport;
    }
}

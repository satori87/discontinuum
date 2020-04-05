// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class ShapeRenderer implements Disposable
{
    private final ImmediateModeRenderer renderer;
    private boolean matrixDirty;
    private final Matrix4 projectionMatrix;
    private final Matrix4 transformMatrix;
    private final Matrix4 combinedMatrix;
    private final Vector2 tmp;
    private final Color color;
    private ShapeType shapeType;
    private boolean autoShapeType;
    private float defaultRectLineWidth;
    
    public ShapeRenderer() {
        this(5000);
    }
    
    public ShapeRenderer(final int maxVertices) {
        this(maxVertices, null);
    }
    
    public ShapeRenderer(final int maxVertices, final ShaderProgram defaultShader) {
        this.matrixDirty = false;
        this.projectionMatrix = new Matrix4();
        this.transformMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.tmp = new Vector2();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.defaultRectLineWidth = 0.75f;
        if (defaultShader == null) {
            this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0);
        }
        else {
            this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0, defaultShader);
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        this.matrixDirty = true;
    }
    
    public void setColor(final Color color) {
        this.color.set(color);
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void updateMatrices() {
        this.matrixDirty = true;
    }
    
    public void setProjectionMatrix(final Matrix4 matrix) {
        this.projectionMatrix.set(matrix);
        this.matrixDirty = true;
    }
    
    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }
    
    public void setTransformMatrix(final Matrix4 matrix) {
        this.transformMatrix.set(matrix);
        this.matrixDirty = true;
    }
    
    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }
    
    public void identity() {
        this.transformMatrix.idt();
        this.matrixDirty = true;
    }
    
    public void translate(final float x, final float y, final float z) {
        this.transformMatrix.translate(x, y, z);
        this.matrixDirty = true;
    }
    
    public void rotate(final float axisX, final float axisY, final float axisZ, final float degrees) {
        this.transformMatrix.rotate(axisX, axisY, axisZ, degrees);
        this.matrixDirty = true;
    }
    
    public void scale(final float scaleX, final float scaleY, final float scaleZ) {
        this.transformMatrix.scale(scaleX, scaleY, scaleZ);
        this.matrixDirty = true;
    }
    
    public void setAutoShapeType(final boolean autoShapeType) {
        this.autoShapeType = autoShapeType;
    }
    
    public void begin() {
        if (!this.autoShapeType) {
            throw new IllegalStateException("autoShapeType must be true to use this method.");
        }
        this.begin(ShapeType.Line);
    }
    
    public void begin(final ShapeType type) {
        if (this.shapeType != null) {
            throw new IllegalStateException("Call end() before beginning a new shape batch.");
        }
        this.shapeType = type;
        if (this.matrixDirty) {
            this.combinedMatrix.set(this.projectionMatrix);
            Matrix4.mul(this.combinedMatrix.val, this.transformMatrix.val);
            this.matrixDirty = false;
        }
        this.renderer.begin(this.combinedMatrix, this.shapeType.getGlType());
    }
    
    public void set(final ShapeType type) {
        if (this.shapeType == type) {
            return;
        }
        if (this.shapeType == null) {
            throw new IllegalStateException("begin must be called first.");
        }
        if (!this.autoShapeType) {
            throw new IllegalStateException("autoShapeType must be enabled.");
        }
        this.end();
        this.begin(type);
    }
    
    public void point(final float x, final float y, final float z) {
        if (this.shapeType == ShapeType.Line) {
            final float size = this.defaultRectLineWidth * 0.5f;
            this.line(x - size, y - size, z, x + size, y + size, z);
            return;
        }
        if (this.shapeType == ShapeType.Filled) {
            final float size = this.defaultRectLineWidth * 0.5f;
            this.box(x - size, y - size, z - size, this.defaultRectLineWidth, this.defaultRectLineWidth, this.defaultRectLineWidth);
            return;
        }
        this.check(ShapeType.Point, null, 1);
        this.renderer.color(this.color);
        this.renderer.vertex(x, y, z);
    }
    
    public final void line(final float x, final float y, final float z, final float x2, final float y2, final float z2) {
        this.line(x, y, z, x2, y2, z2, this.color, this.color);
    }
    
    public final void line(final Vector3 v0, final Vector3 v1) {
        this.line(v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, this.color, this.color);
    }
    
    public final void line(final float x, final float y, final float x2, final float y2) {
        this.line(x, y, 0.0f, x2, y2, 0.0f, this.color, this.color);
    }
    
    public final void line(final Vector2 v0, final Vector2 v1) {
        this.line(v0.x, v0.y, 0.0f, v1.x, v1.y, 0.0f, this.color, this.color);
    }
    
    public final void line(final float x, final float y, final float x2, final float y2, final Color c1, final Color c2) {
        this.line(x, y, 0.0f, x2, y2, 0.0f, c1, c2);
    }
    
    public void line(final float x, final float y, final float z, final float x2, final float y2, final float z2, final Color c1, final Color c2) {
        if (this.shapeType == ShapeType.Filled) {
            this.rectLine(x, y, x2, y2, this.defaultRectLineWidth, c1, c2);
            return;
        }
        this.check(ShapeType.Line, null, 2);
        this.renderer.color(c1.r, c1.g, c1.b, c1.a);
        this.renderer.vertex(x, y, z);
        this.renderer.color(c2.r, c2.g, c2.b, c2.a);
        this.renderer.vertex(x2, y2, z2);
    }
    
    public void curve(final float x1, final float y1, final float cx1, final float cy1, final float cx2, final float cy2, final float x2, final float y2, int segments) {
        this.check(ShapeType.Line, null, segments * 2 + 2);
        final float colorBits = this.color.toFloatBits();
        final float subdiv_step = 1.0f / segments;
        final float subdiv_step2 = subdiv_step * subdiv_step;
        final float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;
        final float pre1 = 3.0f * subdiv_step;
        final float pre2 = 3.0f * subdiv_step2;
        final float pre3 = 6.0f * subdiv_step2;
        final float pre4 = 6.0f * subdiv_step3;
        final float tmp1x = x1 - cx1 * 2.0f + cx2;
        final float tmp1y = y1 - cy1 * 2.0f + cy2;
        final float tmp2x = (cx1 - cx2) * 3.0f - x1 + x2;
        final float tmp2y = (cy1 - cy2) * 3.0f - y1 + y2;
        float fx = x1;
        float fy = y1;
        float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
        float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;
        float ddfx = tmp1x * pre3 + tmp2x * pre4;
        float ddfy = tmp1y * pre3 + tmp2y * pre4;
        final float dddfx = tmp2x * pre4;
        final float dddfy = tmp2y * pre4;
        while (segments-- > 0) {
            this.renderer.color(colorBits);
            this.renderer.vertex(fx, fy, 0.0f);
            fx += dfx;
            fy += dfy;
            dfx += ddfx;
            dfy += ddfy;
            ddfx += dddfx;
            ddfy += dddfy;
            this.renderer.color(colorBits);
            this.renderer.vertex(fx, fy, 0.0f);
        }
        this.renderer.color(colorBits);
        this.renderer.vertex(fx, fy, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x2, y2, 0.0f);
    }
    
    public void triangle(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        this.check(ShapeType.Line, ShapeType.Filled, 6);
        final float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
        }
        else {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x3, y3, 0.0f);
        }
    }
    
    public void triangle(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final Color col1, final Color col2, final Color col3) {
        this.check(ShapeType.Line, ShapeType.Filled, 6);
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x1, y1, 0.0f);
        }
        else {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x3, y3, 0.0f);
        }
    }
    
    public void rect(final float x, final float y, final float width, final float height) {
        this.check(ShapeType.Line, ShapeType.Filled, 8);
        final float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
        }
        else {
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
        }
    }
    
    public void rect(final float x, final float y, final float width, final float height, final Color col1, final Color col2, final Color col3, final Color col4) {
        this.check(ShapeType.Line, ShapeType.Filled, 8);
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
        }
        else {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
        }
    }
    
    public void rect(final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float degrees) {
        this.rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees, this.color, this.color, this.color, this.color);
    }
    
    public void rect(final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float degrees, final Color col1, final Color col2, final Color col3, final Color col4) {
        this.check(ShapeType.Line, ShapeType.Filled, 8);
        final float cos = MathUtils.cosDeg(degrees);
        final float sin = MathUtils.sinDeg(degrees);
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (scaleX != 1.0f || scaleY != 1.0f) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        final float x2 = cos * fx - sin * fy + worldOriginX;
        final float y2 = sin * fx + cos * fy + worldOriginY;
        final float x3 = cos * fx2 - sin * fy + worldOriginX;
        final float y3 = sin * fx2 + cos * fy + worldOriginY;
        final float x4 = cos * fx2 - sin * fy2 + worldOriginX;
        final float y4 = sin * fx2 + cos * fy2 + worldOriginY;
        final float x5 = x2 + (x4 - x3);
        final float y5 = y4 - (y3 - y2);
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x4, y4, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x4, y4, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x5, y5, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x5, y5, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x2, y2, 0.0f);
        }
        else {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x4, y4, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x4, y4, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x5, y5, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x2, y2, 0.0f);
        }
    }
    
    public void rectLine(final float x1, final float y1, final float x2, final float y2, float width) {
        this.check(ShapeType.Line, ShapeType.Filled, 8);
        final float colorBits = this.color.toFloatBits();
        final Vector2 t = this.tmp.set(y2 - y1, x1 - x2).nor();
        width *= 0.5f;
        final float tx = t.x * width;
        final float ty = t.y * width;
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        }
        else {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        }
    }
    
    public void rectLine(final float x1, final float y1, final float x2, final float y2, float width, final Color c1, final Color c2) {
        this.check(ShapeType.Line, ShapeType.Filled, 8);
        final float col1Bits = c1.toFloatBits();
        final float col2Bits = c2.toFloatBits();
        final Vector2 t = this.tmp.set(y2 - y1, x1 - x2).nor();
        width *= 0.5f;
        final float tx = t.x * width;
        final float ty = t.y * width;
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        }
        else {
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        }
    }
    
    public void rectLine(final Vector2 p1, final Vector2 p2, final float width) {
        this.rectLine(p1.x, p1.y, p2.x, p2.y, width);
    }
    
    public void box(final float x, final float y, final float z, final float width, final float height, float depth) {
        depth = -depth;
        final float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            this.check(ShapeType.Line, ShapeType.Filled, 24);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
        }
        else {
            this.check(ShapeType.Line, ShapeType.Filled, 36);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
        }
    }
    
    public void x(final float x, final float y, final float size) {
        this.line(x - size, y - size, x + size, y + size);
        this.line(x - size, y + size, x + size, y - size);
    }
    
    public void x(final Vector2 p, final float size) {
        this.x(p.x, p.y, size);
    }
    
    public void arc(final float x, final float y, final float radius, final float start, final float degrees) {
        this.arc(x, y, radius, start, degrees, Math.max(1, (int)(6.0f * (float)Math.cbrt(radius) * (degrees / 360.0f))));
    }
    
    public void arc(final float x, final float y, final float radius, final float start, final float degrees, final int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be > 0.");
        }
        final float colorBits = this.color.toFloatBits();
        final float theta = 6.2831855f * (degrees / 360.0f) / segments;
        final float cos = MathUtils.cos(theta);
        final float sin = MathUtils.sin(theta);
        float cx = radius * MathUtils.cos(start * 0.017453292f);
        float cy = radius * MathUtils.sin(start * 0.017453292f);
        if (this.shapeType == ShapeType.Line) {
            this.check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                final float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
        }
        else {
            this.check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x, y, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                final float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
        }
        final float temp2 = cx;
        cx = 0.0f;
        cy = 0.0f;
        this.renderer.color(colorBits);
        this.renderer.vertex(x + cx, y + cy, 0.0f);
    }
    
    public void circle(final float x, final float y, final float radius) {
        this.circle(x, y, radius, Math.max(1, (int)(6.0f * (float)Math.cbrt(radius))));
    }
    
    public void circle(final float x, final float y, final float radius, int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be > 0.");
        }
        final float colorBits = this.color.toFloatBits();
        final float angle = 6.2831855f / segments;
        final float cos = MathUtils.cos(angle);
        final float sin = MathUtils.sin(angle);
        float cx = radius;
        float cy = 0.0f;
        if (this.shapeType == ShapeType.Line) {
            this.check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                final float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
        }
        else {
            this.check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);
            --segments;
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x, y, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                final float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
        }
        final float temp2 = cx;
        cx = radius;
        cy = 0.0f;
        this.renderer.color(colorBits);
        this.renderer.vertex(x + cx, y + cy, 0.0f);
    }
    
    public void ellipse(final float x, final float y, final float width, final float height) {
        this.ellipse(x, y, width, height, Math.max(1, (int)(12.0f * (float)Math.cbrt(Math.max(width * 0.5f, height * 0.5f)))));
    }
    
    public void ellipse(final float x, final float y, final float width, final float height, final int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be > 0.");
        }
        this.check(ShapeType.Line, ShapeType.Filled, segments * 3);
        final float colorBits = this.color.toFloatBits();
        final float angle = 6.2831855f / segments;
        final float cx = x + width / 2.0f;
        final float cy = y + height / 2.0f;
        if (this.shapeType == ShapeType.Line) {
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + width * 0.5f * MathUtils.cos(i * angle), cy + height * 0.5f * MathUtils.sin(i * angle), 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + width * 0.5f * MathUtils.cos((i + 1) * angle), cy + height * 0.5f * MathUtils.sin((i + 1) * angle), 0.0f);
            }
        }
        else {
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + width * 0.5f * MathUtils.cos(i * angle), cy + height * 0.5f * MathUtils.sin(i * angle), 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx, cy, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + width * 0.5f * MathUtils.cos((i + 1) * angle), cy + height * 0.5f * MathUtils.sin((i + 1) * angle), 0.0f);
            }
        }
    }
    
    public void ellipse(final float x, final float y, final float width, final float height, final float rotation) {
        this.ellipse(x, y, width, height, rotation, Math.max(1, (int)(12.0f * (float)Math.cbrt(Math.max(width * 0.5f, height * 0.5f)))));
    }
    
    public void ellipse(final float x, final float y, final float width, final float height, float rotation, final int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be > 0.");
        }
        this.check(ShapeType.Line, ShapeType.Filled, segments * 3);
        final float colorBits = this.color.toFloatBits();
        final float angle = 6.2831855f / segments;
        rotation = 3.1415927f * rotation / 180.0f;
        final float sin = MathUtils.sin(rotation);
        final float cos = MathUtils.cos(rotation);
        final float cx = x + width / 2.0f;
        final float cy = y + height / 2.0f;
        float x2 = width * 0.5f;
        float y2 = 0.0f;
        if (this.shapeType == ShapeType.Line) {
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + cos * x2 - sin * y2, cy + sin * x2 + cos * y2, 0.0f);
                x2 = width * 0.5f * MathUtils.cos((i + 1) * angle);
                y2 = height * 0.5f * MathUtils.sin((i + 1) * angle);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + cos * x2 - sin * y2, cy + sin * x2 + cos * y2, 0.0f);
            }
        }
        else {
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + cos * x2 - sin * y2, cy + sin * x2 + cos * y2, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx, cy, 0.0f);
                x2 = width * 0.5f * MathUtils.cos((i + 1) * angle);
                y2 = height * 0.5f * MathUtils.sin((i + 1) * angle);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx + cos * x2 - sin * y2, cy + sin * x2 + cos * y2, 0.0f);
            }
        }
    }
    
    public void cone(final float x, final float y, final float z, final float radius, final float height) {
        this.cone(x, y, z, radius, height, Math.max(1, (int)(4.0f * (float)Math.sqrt(radius))));
    }
    
    public void cone(final float x, final float y, final float z, final float radius, final float height, int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be > 0.");
        }
        this.check(ShapeType.Line, ShapeType.Filled, segments * 4 + 2);
        final float colorBits = this.color.toFloatBits();
        final float angle = 6.2831855f / segments;
        final float cos = MathUtils.cos(angle);
        final float sin = MathUtils.sin(angle);
        float cx = radius;
        float cy = 0.0f;
        if (this.shapeType == ShapeType.Line) {
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
                this.renderer.color(colorBits);
                this.renderer.vertex(x, y, z + height);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
                final float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, z);
        }
        else {
            --segments;
            for (int i = 0; i < segments; ++i) {
                this.renderer.color(colorBits);
                this.renderer.vertex(x, y, z);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
                final float temp = cx;
                final float temp2 = cy;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + temp, y + temp2, z);
                this.renderer.color(colorBits);
                this.renderer.vertex(x + cx, y + cy, z);
                this.renderer.color(colorBits);
                this.renderer.vertex(x, y, z + height);
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, z);
        }
        final float temp3 = cx;
        final float temp4 = cy;
        cx = radius;
        cy = 0.0f;
        this.renderer.color(colorBits);
        this.renderer.vertex(x + cx, y + cy, z);
        if (this.shapeType != ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x + temp3, y + temp4, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + cx, y + cy, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + height);
        }
    }
    
    public void polygon(final float[] vertices, final int offset, final int count) {
        if (count < 6) {
            throw new IllegalArgumentException("Polygons must contain at least 3 points.");
        }
        if (count % 2 != 0) {
            throw new IllegalArgumentException("Polygons must have an even number of vertices.");
        }
        this.check(ShapeType.Line, null, count);
        final float colorBits = this.color.toFloatBits();
        final float firstX = vertices[0];
        final float firstY = vertices[1];
        for (int i = offset, n = offset + count; i < n; i += 2) {
            final float x1 = vertices[i];
            final float y1 = vertices[i + 1];
            float x2;
            float y2;
            if (i + 2 >= count) {
                x2 = firstX;
                y2 = firstY;
            }
            else {
                x2 = vertices[i + 2];
                y2 = vertices[i + 3];
            }
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
        }
    }
    
    public void polygon(final float[] vertices) {
        this.polygon(vertices, 0, vertices.length);
    }
    
    public void polyline(final float[] vertices, final int offset, final int count) {
        if (count < 4) {
            throw new IllegalArgumentException("Polylines must contain at least 2 points.");
        }
        if (count % 2 != 0) {
            throw new IllegalArgumentException("Polylines must have an even number of vertices.");
        }
        this.check(ShapeType.Line, null, count);
        final float colorBits = this.color.toFloatBits();
        for (int i = offset, n = offset + count - 2; i < n; i += 2) {
            final float x1 = vertices[i];
            final float y1 = vertices[i + 1];
            final float x2 = vertices[i + 2];
            final float y2 = vertices[i + 3];
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
        }
    }
    
    public void polyline(final float[] vertices) {
        this.polyline(vertices, 0, vertices.length);
    }
    
    private void check(final ShapeType preferred, final ShapeType other, final int newVertices) {
        if (this.shapeType == null) {
            throw new IllegalStateException("begin must be called first.");
        }
        if (this.shapeType != preferred && this.shapeType != other) {
            if (!this.autoShapeType) {
                if (other == null) {
                    throw new IllegalStateException("Must call begin(ShapeType." + preferred + ").");
                }
                throw new IllegalStateException("Must call begin(ShapeType." + preferred + ") or begin(ShapeType." + other + ").");
            }
            else {
                this.end();
                this.begin(preferred);
            }
        }
        else if (this.matrixDirty) {
            final ShapeType type = this.shapeType;
            this.end();
            this.begin(type);
        }
        else if (this.renderer.getMaxVertices() - this.renderer.getNumVertices() < newVertices) {
            final ShapeType type = this.shapeType;
            this.end();
            this.begin(type);
        }
    }
    
    public void end() {
        this.renderer.end();
        this.shapeType = null;
    }
    
    public void flush() {
        final ShapeType type = this.shapeType;
        this.end();
        this.begin(type);
    }
    
    public ShapeType getCurrentType() {
        return this.shapeType;
    }
    
    public ImmediateModeRenderer getRenderer() {
        return this.renderer;
    }
    
    public boolean isDrawing() {
        return this.shapeType != null;
    }
    
    @Override
    public void dispose() {
        this.renderer.dispose();
    }
    
    public enum ShapeType
    {
        Point("Point", 0, 0), 
        Line("Line", 1, 1), 
        Filled("Filled", 2, 4);
        
        private final int glType;
        
        private ShapeType(final String name, final int ordinal, final int glType) {
            this.glType = glType;
        }
        
        public int getGlType() {
            return this.glType;
        }
    }
}

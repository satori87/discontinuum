// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.FloatArray;

public class HorizontalGroup extends WidgetGroup
{
    private float prefWidth;
    private float prefHeight;
    private float lastPrefHeight;
    private boolean sizeInvalid;
    private FloatArray rowSizes;
    private int align;
    private int rowAlign;
    private boolean reverse;
    private boolean round;
    private boolean wrap;
    private boolean expand;
    private float space;
    private float wrapSpace;
    private float fill;
    private float padTop;
    private float padLeft;
    private float padBottom;
    private float padRight;
    
    public HorizontalGroup() {
        this.sizeInvalid = true;
        this.align = 8;
        this.round = true;
        this.setTouchable(Touchable.childrenOnly);
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }
    
    private void computeSize() {
        this.sizeInvalid = false;
        final SnapshotArray<Actor> children = this.getChildren();
        int n = children.size;
        this.prefHeight = 0.0f;
        if (this.wrap) {
            this.prefWidth = 0.0f;
            if (this.rowSizes == null) {
                this.rowSizes = new FloatArray();
            }
            else {
                this.rowSizes.clear();
            }
            final FloatArray rowSizes = this.rowSizes;
            final float space = this.space;
            final float wrapSpace = this.wrapSpace;
            final float pad = this.padLeft + this.padRight;
            final float groupWidth = this.getWidth() - pad;
            float x = 0.0f;
            float y = 0.0f;
            float rowHeight = 0.0f;
            int i = 0;
            int incr = 1;
            if (this.reverse) {
                i = n - 1;
                n = -1;
                incr = -1;
            }
            while (i != n) {
                final Actor child = children.get(i);
                float width;
                float height;
                if (child instanceof Layout) {
                    final Layout layout = (Layout)child;
                    width = layout.getPrefWidth();
                    if (width > groupWidth) {
                        width = Math.max(groupWidth, layout.getMinWidth());
                    }
                    height = layout.getPrefHeight();
                }
                else {
                    width = child.getWidth();
                    height = child.getHeight();
                }
                float incrX = width + ((x > 0.0f) ? space : 0.0f);
                if (x + incrX > groupWidth && x > 0.0f) {
                    rowSizes.add(x);
                    rowSizes.add(rowHeight);
                    this.prefWidth = Math.max(this.prefWidth, x + pad);
                    if (y > 0.0f) {
                        y += wrapSpace;
                    }
                    y += rowHeight;
                    rowHeight = 0.0f;
                    x = 0.0f;
                    incrX = width;
                }
                x += incrX;
                rowHeight = Math.max(rowHeight, height);
                i += incr;
            }
            rowSizes.add(x);
            rowSizes.add(rowHeight);
            this.prefWidth = Math.max(this.prefWidth, x + pad);
            if (y > 0.0f) {
                y += wrapSpace;
            }
            this.prefHeight = Math.max(this.prefHeight, y + rowHeight);
        }
        else {
            this.prefWidth = this.padLeft + this.padRight + this.space * (n - 1);
            for (int j = 0; j < n; ++j) {
                final Actor child2 = children.get(j);
                if (child2 instanceof Layout) {
                    final Layout layout2 = (Layout)child2;
                    this.prefWidth += layout2.getPrefWidth();
                    this.prefHeight = Math.max(this.prefHeight, layout2.getPrefHeight());
                }
                else {
                    this.prefWidth += child2.getWidth();
                    this.prefHeight = Math.max(this.prefHeight, child2.getHeight());
                }
            }
        }
        this.prefHeight += this.padTop + this.padBottom;
        if (this.round) {
            this.prefWidth = (float)Math.round(this.prefWidth);
            this.prefHeight = (float)Math.round(this.prefHeight);
        }
    }
    
    @Override
    public void layout() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        if (this.wrap) {
            this.layoutWrapped();
            return;
        }
        final boolean round = this.round;
        int align = this.align;
        final float space = this.space;
        final float padBottom = this.padBottom;
        final float fill = this.fill;
        final float rowHeight = (this.expand ? this.getHeight() : this.prefHeight) - this.padTop - padBottom;
        float x = this.padLeft;
        if ((align & 0x10) != 0x0) {
            x += this.getWidth() - this.prefWidth;
        }
        else if ((align & 0x8) == 0x0) {
            x += (this.getWidth() - this.prefWidth) / 2.0f;
        }
        float startY;
        if ((align & 0x4) != 0x0) {
            startY = padBottom;
        }
        else if ((align & 0x2) != 0x0) {
            startY = this.getHeight() - this.padTop - rowHeight;
        }
        else {
            startY = padBottom + (this.getHeight() - padBottom - this.padTop - rowHeight) / 2.0f;
        }
        align = this.rowAlign;
        final SnapshotArray<Actor> children = this.getChildren();
        int i = 0;
        int n = children.size;
        int incr = 1;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        final int r = 0;
        while (i != n) {
            final Actor child = children.get(i);
            Layout layout = null;
            float width;
            float height;
            if (child instanceof Layout) {
                layout = (Layout)child;
                width = layout.getPrefWidth();
                height = layout.getPrefHeight();
            }
            else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (fill > 0.0f) {
                height = rowHeight * fill;
            }
            if (layout != null) {
                height = Math.max(height, layout.getMinHeight());
                final float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height > maxHeight) {
                    height = maxHeight;
                }
            }
            float y = startY;
            if ((align & 0x2) != 0x0) {
                y += rowHeight - height;
            }
            else if ((align & 0x4) == 0x0) {
                y += (rowHeight - height) / 2.0f;
            }
            if (round) {
                child.setBounds((float)Math.round(x), (float)Math.round(y), (float)Math.round(width), (float)Math.round(height));
            }
            else {
                child.setBounds(x, y, width, height);
            }
            x += width + space;
            if (layout != null) {
                layout.validate();
            }
            i += incr;
        }
    }
    
    private void layoutWrapped() {
        final float prefHeight = this.getPrefHeight();
        if (prefHeight != this.lastPrefHeight) {
            this.lastPrefHeight = prefHeight;
            this.invalidateHierarchy();
        }
        int align = this.align;
        final boolean round = this.round;
        final float space = this.space;
        final float padBottom = this.padBottom;
        final float fill = this.fill;
        final float wrapSpace = this.wrapSpace;
        final float maxWidth = this.prefWidth - this.padLeft - this.padRight;
        float rowY = prefHeight - this.padTop;
        float groupWidth = this.getWidth();
        float xStart = this.padLeft;
        float x = 0.0f;
        float rowHeight = 0.0f;
        if ((align & 0x2) != 0x0) {
            rowY += this.getHeight() - prefHeight;
        }
        else if ((align & 0x4) == 0x0) {
            rowY += (this.getHeight() - prefHeight) / 2.0f;
        }
        if ((align & 0x10) != 0x0) {
            xStart += groupWidth - this.prefWidth;
        }
        else if ((align & 0x8) == 0x0) {
            xStart += (groupWidth - this.prefWidth) / 2.0f;
        }
        groupWidth -= this.padRight;
        align = this.rowAlign;
        final FloatArray rowSizes = this.rowSizes;
        final SnapshotArray<Actor> children = this.getChildren();
        int i = 0;
        int n = children.size;
        int incr = 1;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        int r = 0;
        while (i != n) {
            final Actor child = children.get(i);
            Layout layout = null;
            float width;
            float height;
            if (child instanceof Layout) {
                layout = (Layout)child;
                width = layout.getPrefWidth();
                if (width > groupWidth) {
                    width = Math.max(groupWidth, layout.getMinWidth());
                }
                height = layout.getPrefHeight();
            }
            else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (x + width > groupWidth || r == 0) {
                x = xStart;
                if ((align & 0x10) != 0x0) {
                    x += maxWidth - rowSizes.get(r);
                }
                else if ((align & 0x8) == 0x0) {
                    x += (maxWidth - rowSizes.get(r)) / 2.0f;
                }
                rowHeight = rowSizes.get(r + 1);
                if (r > 0) {
                    rowY -= wrapSpace;
                }
                rowY -= rowHeight;
                r += 2;
            }
            if (fill > 0.0f) {
                height = rowHeight * fill;
            }
            if (layout != null) {
                height = Math.max(height, layout.getMinHeight());
                final float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height > maxHeight) {
                    height = maxHeight;
                }
            }
            float y = rowY;
            if ((align & 0x2) != 0x0) {
                y += rowHeight - height;
            }
            else if ((align & 0x4) == 0x0) {
                y += (rowHeight - height) / 2.0f;
            }
            if (round) {
                child.setBounds((float)Math.round(x), (float)Math.round(y), (float)Math.round(width), (float)Math.round(height));
            }
            else {
                child.setBounds(x, y, width, height);
            }
            x += width + space;
            if (layout != null) {
                layout.validate();
            }
            i += incr;
        }
    }
    
    @Override
    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefWidth;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefHeight;
    }
    
    public void setRound(final boolean round) {
        this.round = round;
    }
    
    public HorizontalGroup reverse() {
        this.reverse = true;
        return this;
    }
    
    public HorizontalGroup reverse(final boolean reverse) {
        this.reverse = reverse;
        return this;
    }
    
    public boolean getReverse() {
        return this.reverse;
    }
    
    public HorizontalGroup space(final float space) {
        this.space = space;
        return this;
    }
    
    public float getSpace() {
        return this.space;
    }
    
    public HorizontalGroup wrapSpace(final float wrapSpace) {
        this.wrapSpace = wrapSpace;
        return this;
    }
    
    public float getWrapSpace() {
        return this.wrapSpace;
    }
    
    public HorizontalGroup pad(final float pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }
    
    public HorizontalGroup pad(final float top, final float left, final float bottom, final float right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }
    
    public HorizontalGroup padTop(final float padTop) {
        this.padTop = padTop;
        return this;
    }
    
    public HorizontalGroup padLeft(final float padLeft) {
        this.padLeft = padLeft;
        return this;
    }
    
    public HorizontalGroup padBottom(final float padBottom) {
        this.padBottom = padBottom;
        return this;
    }
    
    public HorizontalGroup padRight(final float padRight) {
        this.padRight = padRight;
        return this;
    }
    
    public float getPadTop() {
        return this.padTop;
    }
    
    public float getPadLeft() {
        return this.padLeft;
    }
    
    public float getPadBottom() {
        return this.padBottom;
    }
    
    public float getPadRight() {
        return this.padRight;
    }
    
    public HorizontalGroup align(final int align) {
        this.align = align;
        return this;
    }
    
    public HorizontalGroup center() {
        this.align = 1;
        return this;
    }
    
    public HorizontalGroup top() {
        this.align |= 0x2;
        this.align &= 0xFFFFFFFB;
        return this;
    }
    
    public HorizontalGroup left() {
        this.align |= 0x8;
        this.align &= 0xFFFFFFEF;
        return this;
    }
    
    public HorizontalGroup bottom() {
        this.align |= 0x4;
        this.align &= 0xFFFFFFFD;
        return this;
    }
    
    public HorizontalGroup right() {
        this.align |= 0x10;
        this.align &= 0xFFFFFFF7;
        return this;
    }
    
    public int getAlign() {
        return this.align;
    }
    
    public HorizontalGroup fill() {
        this.fill = 1.0f;
        return this;
    }
    
    public HorizontalGroup fill(final float fill) {
        this.fill = fill;
        return this;
    }
    
    public float getFill() {
        return this.fill;
    }
    
    public HorizontalGroup expand() {
        this.expand = true;
        return this;
    }
    
    public HorizontalGroup expand(final boolean expand) {
        this.expand = expand;
        return this;
    }
    
    public boolean getExpand() {
        return this.expand;
    }
    
    public HorizontalGroup grow() {
        this.expand = true;
        this.fill = 1.0f;
        return this;
    }
    
    public HorizontalGroup wrap() {
        this.wrap = true;
        return this;
    }
    
    public HorizontalGroup wrap(final boolean wrap) {
        this.wrap = wrap;
        return this;
    }
    
    public boolean getWrap() {
        return this.wrap;
    }
    
    public HorizontalGroup rowAlign(final int rowAlign) {
        this.rowAlign = rowAlign;
        return this;
    }
    
    public HorizontalGroup rowCenter() {
        this.rowAlign = 1;
        return this;
    }
    
    public HorizontalGroup rowTop() {
        this.rowAlign |= 0x2;
        this.rowAlign &= 0xFFFFFFFB;
        return this;
    }
    
    public HorizontalGroup rowLeft() {
        this.rowAlign |= 0x8;
        this.rowAlign &= 0xFFFFFFEF;
        return this;
    }
    
    public HorizontalGroup rowBottom() {
        this.rowAlign |= 0x4;
        this.rowAlign &= 0xFFFFFFFD;
        return this;
    }
    
    public HorizontalGroup rowRight() {
        this.rowAlign |= 0x10;
        this.rowAlign &= 0xFFFFFFF7;
        return this;
    }
    
    @Override
    protected void drawDebugBounds(final ShapeRenderer shapes) {
        super.drawDebugBounds(shapes);
        if (!this.getDebug()) {
            return;
        }
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(this.getStage().getDebugColor());
        shapes.rect(this.getX() + this.padLeft, this.getY() + this.padBottom, this.getOriginX(), this.getOriginY(), this.getWidth() - this.padLeft - this.padRight, this.getHeight() - this.padBottom - this.padTop, this.getScaleX(), this.getScaleY(), this.getRotation());
    }
}

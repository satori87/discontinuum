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

public class VerticalGroup extends WidgetGroup
{
    private float prefWidth;
    private float prefHeight;
    private float lastPrefWidth;
    private boolean sizeInvalid;
    private FloatArray columnSizes;
    private int align;
    private int columnAlign;
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
    
    public VerticalGroup() {
        this.sizeInvalid = true;
        this.align = 2;
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
        this.prefWidth = 0.0f;
        if (this.wrap) {
            this.prefHeight = 0.0f;
            if (this.columnSizes == null) {
                this.columnSizes = new FloatArray();
            }
            else {
                this.columnSizes.clear();
            }
            final FloatArray columnSizes = this.columnSizes;
            final float space = this.space;
            final float wrapSpace = this.wrapSpace;
            final float pad = this.padTop + this.padBottom;
            final float groupHeight = this.getHeight() - pad;
            float x = 0.0f;
            float y = 0.0f;
            float columnWidth = 0.0f;
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
                    height = layout.getPrefHeight();
                    if (height > groupHeight) {
                        height = Math.max(groupHeight, layout.getMinHeight());
                    }
                }
                else {
                    width = child.getWidth();
                    height = child.getHeight();
                }
                float incrY = height + ((y > 0.0f) ? space : 0.0f);
                if (y + incrY > groupHeight && y > 0.0f) {
                    columnSizes.add(y);
                    columnSizes.add(columnWidth);
                    this.prefHeight = Math.max(this.prefHeight, y + pad);
                    if (x > 0.0f) {
                        x += wrapSpace;
                    }
                    x += columnWidth;
                    columnWidth = 0.0f;
                    y = 0.0f;
                    incrY = height;
                }
                y += incrY;
                columnWidth = Math.max(columnWidth, width);
                i += incr;
            }
            columnSizes.add(y);
            columnSizes.add(columnWidth);
            this.prefHeight = Math.max(this.prefHeight, y + pad);
            if (x > 0.0f) {
                x += wrapSpace;
            }
            this.prefWidth = Math.max(this.prefWidth, x + columnWidth);
        }
        else {
            this.prefHeight = this.padTop + this.padBottom + this.space * (n - 1);
            for (int j = 0; j < n; ++j) {
                final Actor child2 = children.get(j);
                if (child2 instanceof Layout) {
                    final Layout layout2 = (Layout)child2;
                    this.prefWidth = Math.max(this.prefWidth, layout2.getPrefWidth());
                    this.prefHeight += layout2.getPrefHeight();
                }
                else {
                    this.prefWidth = Math.max(this.prefWidth, child2.getWidth());
                    this.prefHeight += child2.getHeight();
                }
            }
        }
        this.prefWidth += this.padLeft + this.padRight;
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
        final float padLeft = this.padLeft;
        final float fill = this.fill;
        final float columnWidth = (this.expand ? this.getWidth() : this.prefWidth) - padLeft - this.padRight;
        float y = this.prefHeight - this.padTop + space;
        if ((align & 0x2) != 0x0) {
            y += this.getHeight() - this.prefHeight;
        }
        else if ((align & 0x4) == 0x0) {
            y += (this.getHeight() - this.prefHeight) / 2.0f;
        }
        float startX;
        if ((align & 0x8) != 0x0) {
            startX = padLeft;
        }
        else if ((align & 0x10) != 0x0) {
            startX = this.getWidth() - this.padRight - columnWidth;
        }
        else {
            startX = padLeft + (this.getWidth() - padLeft - this.padRight - columnWidth) / 2.0f;
        }
        align = this.columnAlign;
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
                width = columnWidth * fill;
            }
            if (layout != null) {
                width = Math.max(width, layout.getMinWidth());
                final float maxWidth = layout.getMaxWidth();
                if (maxWidth > 0.0f && width > maxWidth) {
                    width = maxWidth;
                }
            }
            float x = startX;
            if ((align & 0x10) != 0x0) {
                x += columnWidth - width;
            }
            else if ((align & 0x8) == 0x0) {
                x += (columnWidth - width) / 2.0f;
            }
            y -= height + space;
            if (round) {
                child.setBounds((float)Math.round(x), (float)Math.round(y), (float)Math.round(width), (float)Math.round(height));
            }
            else {
                child.setBounds(x, y, width, height);
            }
            if (layout != null) {
                layout.validate();
            }
            i += incr;
        }
    }
    
    private void layoutWrapped() {
        final float prefWidth = this.getPrefWidth();
        if (prefWidth != this.lastPrefWidth) {
            this.lastPrefWidth = prefWidth;
            this.invalidateHierarchy();
        }
        int align = this.align;
        final boolean round = this.round;
        final float space = this.space;
        final float padLeft = this.padLeft;
        final float fill = this.fill;
        final float wrapSpace = this.wrapSpace;
        final float maxHeight = this.prefHeight - this.padTop - this.padBottom;
        float columnX = padLeft;
        float groupHeight = this.getHeight();
        float yStart = this.prefHeight - this.padTop + space;
        float y = 0.0f;
        float columnWidth = 0.0f;
        if ((align & 0x10) != 0x0) {
            columnX += this.getWidth() - prefWidth;
        }
        else if ((align & 0x8) == 0x0) {
            columnX += (this.getWidth() - prefWidth) / 2.0f;
        }
        if ((align & 0x2) != 0x0) {
            yStart += groupHeight - this.prefHeight;
        }
        else if ((align & 0x4) == 0x0) {
            yStart += (groupHeight - this.prefHeight) / 2.0f;
        }
        groupHeight -= this.padTop;
        align = this.columnAlign;
        final FloatArray columnSizes = this.columnSizes;
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
                height = layout.getPrefHeight();
                if (height > groupHeight) {
                    height = Math.max(groupHeight, layout.getMinHeight());
                }
            }
            else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (y - height - space < this.padBottom || r == 0) {
                y = yStart;
                if ((align & 0x4) != 0x0) {
                    y -= maxHeight - columnSizes.get(r);
                }
                else if ((align & 0x2) == 0x0) {
                    y -= (maxHeight - columnSizes.get(r)) / 2.0f;
                }
                if (r > 0) {
                    columnX += wrapSpace;
                    columnX += columnWidth;
                }
                columnWidth = columnSizes.get(r + 1);
                r += 2;
            }
            if (fill > 0.0f) {
                width = columnWidth * fill;
            }
            if (layout != null) {
                width = Math.max(width, layout.getMinWidth());
                final float maxWidth = layout.getMaxWidth();
                if (maxWidth > 0.0f && width > maxWidth) {
                    width = maxWidth;
                }
            }
            float x = columnX;
            if ((align & 0x10) != 0x0) {
                x += columnWidth - width;
            }
            else if ((align & 0x8) == 0x0) {
                x += (columnWidth - width) / 2.0f;
            }
            y -= height + space;
            if (round) {
                child.setBounds((float)Math.round(x), (float)Math.round(y), (float)Math.round(width), (float)Math.round(height));
            }
            else {
                child.setBounds(x, y, width, height);
            }
            if (layout != null) {
                layout.validate();
            }
            i += incr;
        }
    }
    
    @Override
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefWidth;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefHeight;
    }
    
    public void setRound(final boolean round) {
        this.round = round;
    }
    
    public VerticalGroup reverse() {
        this.reverse = true;
        return this;
    }
    
    public VerticalGroup reverse(final boolean reverse) {
        this.reverse = reverse;
        return this;
    }
    
    public boolean getReverse() {
        return this.reverse;
    }
    
    public VerticalGroup space(final float space) {
        this.space = space;
        return this;
    }
    
    public float getSpace() {
        return this.space;
    }
    
    public VerticalGroup wrapSpace(final float wrapSpace) {
        this.wrapSpace = wrapSpace;
        return this;
    }
    
    public float getWrapSpace() {
        return this.wrapSpace;
    }
    
    public VerticalGroup pad(final float pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }
    
    public VerticalGroup pad(final float top, final float left, final float bottom, final float right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }
    
    public VerticalGroup padTop(final float padTop) {
        this.padTop = padTop;
        return this;
    }
    
    public VerticalGroup padLeft(final float padLeft) {
        this.padLeft = padLeft;
        return this;
    }
    
    public VerticalGroup padBottom(final float padBottom) {
        this.padBottom = padBottom;
        return this;
    }
    
    public VerticalGroup padRight(final float padRight) {
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
    
    public VerticalGroup align(final int align) {
        this.align = align;
        return this;
    }
    
    public VerticalGroup center() {
        this.align = 1;
        return this;
    }
    
    public VerticalGroup top() {
        this.align |= 0x2;
        this.align &= 0xFFFFFFFB;
        return this;
    }
    
    public VerticalGroup left() {
        this.align |= 0x8;
        this.align &= 0xFFFFFFEF;
        return this;
    }
    
    public VerticalGroup bottom() {
        this.align |= 0x4;
        this.align &= 0xFFFFFFFD;
        return this;
    }
    
    public VerticalGroup right() {
        this.align |= 0x10;
        this.align &= 0xFFFFFFF7;
        return this;
    }
    
    public int getAlign() {
        return this.align;
    }
    
    public VerticalGroup fill() {
        this.fill = 1.0f;
        return this;
    }
    
    public VerticalGroup fill(final float fill) {
        this.fill = fill;
        return this;
    }
    
    public float getFill() {
        return this.fill;
    }
    
    public VerticalGroup expand() {
        this.expand = true;
        return this;
    }
    
    public VerticalGroup expand(final boolean expand) {
        this.expand = expand;
        return this;
    }
    
    public boolean getExpand() {
        return this.expand;
    }
    
    public VerticalGroup grow() {
        this.expand = true;
        this.fill = 1.0f;
        return this;
    }
    
    public VerticalGroup wrap() {
        this.wrap = true;
        return this;
    }
    
    public VerticalGroup wrap(final boolean wrap) {
        this.wrap = wrap;
        return this;
    }
    
    public boolean getWrap() {
        return this.wrap;
    }
    
    public VerticalGroup columnAlign(final int columnAlign) {
        this.columnAlign = columnAlign;
        return this;
    }
    
    public VerticalGroup columnCenter() {
        this.columnAlign = 1;
        return this;
    }
    
    public VerticalGroup columnTop() {
        this.columnAlign |= 0x2;
        this.columnAlign &= 0xFFFFFFFB;
        return this;
    }
    
    public VerticalGroup columnLeft() {
        this.columnAlign |= 0x8;
        this.columnAlign &= 0xFFFFFFEF;
        return this;
    }
    
    public VerticalGroup columnBottom() {
        this.columnAlign |= 0x4;
        this.columnAlign &= 0xFFFFFFFD;
        return this;
    }
    
    public VerticalGroup columnRight() {
        this.columnAlign |= 0x10;
        this.columnAlign &= 0xFFFFFFF7;
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

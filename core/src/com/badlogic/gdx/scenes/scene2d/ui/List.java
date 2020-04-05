// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;

public class List<T> extends Widget implements Cullable
{
    ListStyle style;
    final Array<T> items;
    final ArraySelection<T> selection;
    private Rectangle cullingArea;
    private float prefWidth;
    private float prefHeight;
    float itemHeight;
    private int alignment;
    int touchDown;
    int overIndex;
    
    public List(final Skin skin) {
        this(skin.get(ListStyle.class));
    }
    
    public List(final Skin skin, final String styleName) {
        this(skin.get(styleName, ListStyle.class));
    }
    
    public List(final ListStyle style) {
        this.items = new Array<T>();
        this.selection = new ArraySelection<T>(this.items);
        this.alignment = 8;
        this.touchDown = -1;
        this.overIndex = -1;
        this.selection.setActor(this);
        this.selection.setRequired(true);
        this.setStyle(style);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(final InputEvent event, final int keycode) {
                if (keycode == 29 && UIUtils.ctrl() && List.this.selection.getMultiple()) {
                    List.this.selection.clear();
                    List.this.selection.addAll(List.this.items);
                    return true;
                }
                return false;
            }
            
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer != 0 || button != 0) {
                    return true;
                }
                if (List.this.selection.isDisabled()) {
                    return true;
                }
                List.this.getStage().setKeyboardFocus(List.this);
                if (List.this.items.size == 0) {
                    return true;
                }
                final int index = List.this.getItemIndexAt(y);
                if (index == -1) {
                    return true;
                }
                List.this.selection.choose(List.this.items.get(index));
                List.this.touchDown = index;
                return true;
            }
            
            @Override
            public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer != 0 || button != 0) {
                    return;
                }
                List.this.touchDown = -1;
            }
            
            @Override
            public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
                List.this.overIndex = List.this.getItemIndexAt(y);
            }
            
            @Override
            public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                List.this.overIndex = List.this.getItemIndexAt(y);
                return false;
            }
            
            @Override
            public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
                if (pointer == 0) {
                    List.this.touchDown = -1;
                }
                if (pointer == -1) {
                    List.this.overIndex = -1;
                }
            }
        });
    }
    
    public void setStyle(final ListStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.invalidateHierarchy();
    }
    
    public ListStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void layout() {
        final BitmapFont font = this.style.font;
        final Drawable selectedDrawable = this.style.selection;
        this.itemHeight = font.getCapHeight() - font.getDescent() * 2.0f;
        this.itemHeight += selectedDrawable.getTopHeight() + selectedDrawable.getBottomHeight();
        this.prefWidth = 0.0f;
        final Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
        final GlyphLayout layout = layoutPool.obtain();
        for (int i = 0; i < this.items.size; ++i) {
            layout.setText(font, this.toString(this.items.get(i)));
            this.prefWidth = Math.max(layout.width, this.prefWidth);
        }
        layoutPool.free(layout);
        this.prefWidth += selectedDrawable.getLeftWidth() + selectedDrawable.getRightWidth();
        this.prefHeight = this.items.size * this.itemHeight;
        final Drawable background = this.style.background;
        if (background != null) {
            this.prefWidth += background.getLeftWidth() + background.getRightWidth();
            this.prefHeight += background.getTopHeight() + background.getBottomHeight();
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        final BitmapFont font = this.style.font;
        final Drawable selectedDrawable = this.style.selection;
        final Color fontColorSelected = this.style.fontColorSelected;
        final Color fontColorUnselected = this.style.fontColorUnselected;
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float x = this.getX();
        final float y = this.getY();
        float width = this.getWidth();
        float itemY;
        final float height = itemY = this.getHeight();
        final Drawable background = this.style.background;
        if (background != null) {
            background.draw(batch, x, y, width, height);
            final float leftWidth = background.getLeftWidth();
            x += leftWidth;
            itemY -= background.getTopHeight();
            width -= leftWidth + background.getRightWidth();
        }
        final float textOffsetX = selectedDrawable.getLeftWidth();
        final float textWidth = width - textOffsetX - selectedDrawable.getRightWidth();
        final float textOffsetY = selectedDrawable.getTopHeight() - font.getDescent();
        font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
        for (int i = 0; i < this.items.size; ++i) {
            if (this.cullingArea == null || (itemY - this.itemHeight <= this.cullingArea.y + this.cullingArea.height && itemY >= this.cullingArea.y)) {
                final T item = this.items.get(i);
                final boolean selected = this.selection.contains(item);
                if (selected) {
                    Drawable drawable = selectedDrawable;
                    if (this.touchDown == i && this.style.down != null) {
                        drawable = this.style.down;
                    }
                    drawable.draw(batch, x, y + itemY - this.itemHeight, width, this.itemHeight);
                    font.setColor(fontColorSelected.r, fontColorSelected.g, fontColorSelected.b, fontColorSelected.a * parentAlpha);
                }
                else if (this.overIndex == i && this.style.over != null) {
                    this.style.over.draw(batch, x, y + itemY - this.itemHeight, width, this.itemHeight);
                }
                this.drawItem(batch, font, i, item, x + textOffsetX, y + itemY - textOffsetY, textWidth);
                if (selected) {
                    font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
                }
            }
            else if (itemY < this.cullingArea.y) {
                break;
            }
            itemY -= this.itemHeight;
        }
    }
    
    protected GlyphLayout drawItem(final Batch batch, final BitmapFont font, final int index, final T item, final float x, final float y, final float width) {
        final String string = this.toString(item);
        return font.draw(batch, string, x, y, 0, string.length(), width, this.alignment, false, "...");
    }
    
    public ArraySelection<T> getSelection() {
        return this.selection;
    }
    
    public T getSelected() {
        return this.selection.first();
    }
    
    public void setSelected(final T item) {
        if (this.items.contains(item, false)) {
            this.selection.set(item);
        }
        else if (this.selection.getRequired() && this.items.size > 0) {
            this.selection.set(this.items.first());
        }
        else {
            this.selection.clear();
        }
    }
    
    public int getSelectedIndex() {
        final ObjectSet<T> selected = (ObjectSet<T>)this.selection.items();
        return (selected.size == 0) ? -1 : this.items.indexOf(selected.first(), false);
    }
    
    public void setSelectedIndex(final int index) {
        if (index < -1 || index >= this.items.size) {
            throw new IllegalArgumentException("index must be >= -1 and < " + this.items.size + ": " + index);
        }
        if (index == -1) {
            this.selection.clear();
        }
        else {
            this.selection.set(this.items.get(index));
        }
    }
    
    public T getItemAt(final float y) {
        final int index = this.getItemIndexAt(y);
        if (index == -1) {
            return null;
        }
        return this.items.get(index);
    }
    
    public int getItemIndexAt(float y) {
        float height = this.getHeight();
        final Drawable background = this.style.background;
        if (background != null) {
            height -= background.getTopHeight() + background.getBottomHeight();
            y -= background.getBottomHeight();
        }
        final int index = (int)((height - y) / this.itemHeight);
        if (index < 0 || index >= this.items.size) {
            return -1;
        }
        return index;
    }
    
    public void setItems(final T... newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("newItems cannot be null.");
        }
        final float oldPrefWidth = this.getPrefWidth();
        final float oldPrefHeight = this.getPrefHeight();
        this.items.clear();
        this.items.addAll(newItems);
        this.selection.validate();
        this.invalidate();
        if (oldPrefWidth != this.getPrefWidth() || oldPrefHeight != this.getPrefHeight()) {
            this.invalidateHierarchy();
        }
    }
    
    public void setItems(final Array newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("newItems cannot be null.");
        }
        final float oldPrefWidth = this.getPrefWidth();
        final float oldPrefHeight = this.getPrefHeight();
        if (newItems != this.items) {
            this.items.clear();
            this.items.addAll(newItems);
        }
        this.selection.validate();
        this.invalidate();
        if (oldPrefWidth != this.getPrefWidth() || oldPrefHeight != this.getPrefHeight()) {
            this.invalidateHierarchy();
        }
    }
    
    public void clearItems() {
        if (this.items.size == 0) {
            return;
        }
        this.items.clear();
        this.selection.clear();
        this.invalidateHierarchy();
    }
    
    public Array<T> getItems() {
        return this.items;
    }
    
    public float getItemHeight() {
        return this.itemHeight;
    }
    
    @Override
    public float getPrefWidth() {
        this.validate();
        return this.prefWidth;
    }
    
    @Override
    public float getPrefHeight() {
        this.validate();
        return this.prefHeight;
    }
    
    protected String toString(final T object) {
        return object.toString();
    }
    
    @Override
    public void setCullingArea(final Rectangle cullingArea) {
        this.cullingArea = cullingArea;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public static class ListStyle
    {
        public BitmapFont font;
        public Color fontColorSelected;
        public Color fontColorUnselected;
        public Drawable selection;
        public Drawable down;
        public Drawable over;
        public Drawable background;
        
        public ListStyle() {
            this.fontColorSelected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.fontColorUnselected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public ListStyle(final BitmapFont font, final Color fontColorSelected, final Color fontColorUnselected, final Drawable selection) {
            this.fontColorSelected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.fontColorUnselected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.font = font;
            this.fontColorSelected.set(fontColorSelected);
            this.fontColorUnselected.set(fontColorUnselected);
            this.selection = selection;
        }
        
        public ListStyle(final ListStyle style) {
            this.fontColorSelected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.fontColorUnselected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.font = style.font;
            this.fontColorSelected.set(style.fontColorSelected);
            this.fontColorUnselected.set(style.fontColorUnselected);
            this.selection = style.selection;
            this.down = style.down;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;

public class SelectBox<T> extends Widget implements Disableable
{
    static final Vector2 temp;
    SelectBoxStyle style;
    final Array<T> items;
    final ArraySelection<T> selection;
    SelectBoxList<T> selectBoxList;
    private float prefWidth;
    private float prefHeight;
    private ClickListener clickListener;
    boolean disabled;
    private int alignment;
    
    static {
        temp = new Vector2();
    }
    
    public SelectBox(final Skin skin) {
        this(skin.get(SelectBoxStyle.class));
    }
    
    public SelectBox(final Skin skin, final String styleName) {
        this(skin.get(styleName, SelectBoxStyle.class));
    }
    
    public SelectBox(final SelectBoxStyle style) {
        this.items = new Array<T>();
        this.selection = new ArraySelection<T>(this.items);
        this.alignment = 8;
        this.setStyle(style);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.selection.setActor(this);
        this.selection.setRequired(true);
        this.selectBoxList = new SelectBoxList<T>(this);
        this.addListener(this.clickListener = new ClickListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer == 0 && button != 0) {
                    return false;
                }
                if (SelectBox.this.disabled) {
                    return false;
                }
                if (SelectBox.this.selectBoxList.hasParent()) {
                    SelectBox.this.hideList();
                }
                else {
                    SelectBox.this.showList();
                }
                return true;
            }
        });
    }
    
    public void setMaxListCount(final int maxListCount) {
        this.selectBoxList.maxListCount = maxListCount;
    }
    
    public int getMaxListCount() {
        return this.selectBoxList.maxListCount;
    }
    
    @Override
    protected void setStage(final Stage stage) {
        if (stage == null) {
            this.selectBoxList.hide();
        }
        super.setStage(stage);
    }
    
    public void setStyle(final SelectBoxStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        if (this.selectBoxList != null) {
            this.selectBoxList.setStyle(style.scrollStyle);
            this.selectBoxList.list.setStyle(style.listStyle);
        }
        this.invalidateHierarchy();
    }
    
    public SelectBoxStyle getStyle() {
        return this.style;
    }
    
    public void setItems(final T... newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("newItems cannot be null.");
        }
        final float oldPrefWidth = this.getPrefWidth();
        this.items.clear();
        this.items.addAll(newItems);
        this.selection.validate();
        this.selectBoxList.list.setItems(this.items);
        this.invalidate();
        if (oldPrefWidth != this.getPrefWidth()) {
            this.invalidateHierarchy();
        }
    }
    
    public void setItems(final Array<T> newItems) {
        if (newItems == null) {
            throw new IllegalArgumentException("newItems cannot be null.");
        }
        final float oldPrefWidth = this.getPrefWidth();
        if (newItems != this.items) {
            this.items.clear();
            this.items.addAll((Array<? extends T>)newItems);
        }
        this.selection.validate();
        this.selectBoxList.list.setItems(this.items);
        this.invalidate();
        if (oldPrefWidth != this.getPrefWidth()) {
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
    
    @Override
    public void layout() {
        final Drawable bg = this.style.background;
        final BitmapFont font = this.style.font;
        if (bg != null) {
            this.prefHeight = Math.max(bg.getTopHeight() + bg.getBottomHeight() + font.getCapHeight() - font.getDescent() * 2.0f, bg.getMinHeight());
        }
        else {
            this.prefHeight = font.getCapHeight() - font.getDescent() * 2.0f;
        }
        float maxItemWidth = 0.0f;
        final Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
        final GlyphLayout layout = layoutPool.obtain();
        for (int i = 0; i < this.items.size; ++i) {
            layout.setText(font, this.toString(this.items.get(i)));
            maxItemWidth = Math.max(layout.width, maxItemWidth);
        }
        layoutPool.free(layout);
        this.prefWidth = maxItemWidth;
        if (bg != null) {
            this.prefWidth += bg.getLeftWidth() + bg.getRightWidth();
        }
        final List.ListStyle listStyle = this.style.listStyle;
        final ScrollPane.ScrollPaneStyle scrollStyle = this.style.scrollStyle;
        float listWidth = maxItemWidth + listStyle.selection.getLeftWidth() + listStyle.selection.getRightWidth();
        if (scrollStyle.background != null) {
            listWidth += scrollStyle.background.getLeftWidth() + scrollStyle.background.getRightWidth();
        }
        if (this.selectBoxList == null || !this.selectBoxList.disableY) {
            listWidth += Math.max((this.style.scrollStyle.vScroll != null) ? this.style.scrollStyle.vScroll.getMinWidth() : 0.0f, (this.style.scrollStyle.vScrollKnob != null) ? this.style.scrollStyle.vScrollKnob.getMinWidth() : 0.0f);
        }
        this.prefWidth = Math.max(this.prefWidth, listWidth);
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        Drawable background;
        if (this.disabled && this.style.backgroundDisabled != null) {
            background = this.style.backgroundDisabled;
        }
        else if (this.selectBoxList.hasParent() && this.style.backgroundOpen != null) {
            background = this.style.backgroundOpen;
        }
        else if (this.clickListener.isOver() && this.style.backgroundOver != null) {
            background = this.style.backgroundOver;
        }
        else if (this.style.background != null) {
            background = this.style.background;
        }
        else {
            background = null;
        }
        final BitmapFont font = this.style.font;
        final Color fontColor = (this.disabled && this.style.disabledFontColor != null) ? this.style.disabledFontColor : this.style.fontColor;
        final Color color = this.getColor();
        float x = this.getX();
        float y = this.getY();
        float width = this.getWidth();
        float height = this.getHeight();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (background != null) {
            background.draw(batch, x, y, width, height);
        }
        final T selected = this.selection.first();
        if (selected != null) {
            if (background != null) {
                width -= background.getLeftWidth() + background.getRightWidth();
                height -= background.getBottomHeight() + background.getTopHeight();
                x += background.getLeftWidth();
                y += (int)(height / 2.0f + background.getBottomHeight() + font.getData().capHeight / 2.0f);
            }
            else {
                y += (int)(height / 2.0f + font.getData().capHeight / 2.0f);
            }
            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
            this.drawItem(batch, font, selected, x, y, width);
        }
    }
    
    protected GlyphLayout drawItem(final Batch batch, final BitmapFont font, final T item, final float x, final float y, final float width) {
        final String string = this.toString(item);
        return font.draw(batch, string, x, y, 0, string.length(), width, this.alignment, false, "...");
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
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
        else if (this.items.size > 0) {
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
        this.selection.set(this.items.get(index));
    }
    
    @Override
    public void setDisabled(final boolean disabled) {
        if (disabled && !this.disabled) {
            this.hideList();
        }
        this.disabled = disabled;
    }
    
    @Override
    public boolean isDisabled() {
        return this.disabled;
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
    
    protected String toString(final T item) {
        return item.toString();
    }
    
    public void showList() {
        if (this.items.size == 0) {
            return;
        }
        this.selectBoxList.show(this.getStage());
    }
    
    public void hideList() {
        this.selectBoxList.hide();
    }
    
    public List<T> getList() {
        return this.selectBoxList.list;
    }
    
    public void setScrollingDisabled(final boolean y) {
        this.selectBoxList.setScrollingDisabled(true, y);
        this.invalidateHierarchy();
    }
    
    public ScrollPane getScrollPane() {
        return this.selectBoxList;
    }
    
    protected void onShow(final Actor selectBoxList, final boolean below) {
        selectBoxList.getColor().a = 0.0f;
        selectBoxList.addAction(Actions.fadeIn(0.3f, Interpolation.fade));
    }
    
    protected void onHide(final Actor selectBoxList) {
        selectBoxList.getColor().a = 1.0f;
        selectBoxList.addAction(Actions.sequence(Actions.fadeOut(0.15f, Interpolation.fade), Actions.removeActor()));
    }
    
    static class SelectBoxList<T> extends ScrollPane
    {
        private final SelectBox<T> selectBox;
        int maxListCount;
        private final Vector2 screenPosition;
        final List<T> list;
        private InputListener hideListener;
        private Actor previousScrollFocus;
        
        public SelectBoxList(final SelectBox<T> selectBox) {
            super(null, selectBox.style.scrollStyle);
            this.screenPosition = new Vector2();
            this.selectBox = selectBox;
            this.setOverscroll(false, false);
            this.setFadeScrollBars(false);
            this.setScrollingDisabled(true, false);
            (this.list = new List<T>(selectBox.style.listStyle) {
                @Override
                protected String toString(final T obj) {
                    return selectBox.toString(obj);
                }
            }).setTouchable(Touchable.disabled);
            this.setActor(this.list);
            this.list.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    selectBox.selection.choose((T)SelectBoxList.this.list.getSelected());
                    SelectBoxList.this.hide();
                }
                
                @Override
                public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                    final int index = SelectBoxList.this.list.getItemIndexAt(y);
                    if (index != -1) {
                        SelectBoxList.this.list.setSelectedIndex(index);
                    }
                    return true;
                }
            });
            this.addListener(new InputListener() {
                @Override
                public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
                    if (toActor == null || !SelectBoxList.this.isAscendantOf(toActor)) {
                        SelectBoxList.this.list.selection.set(selectBox.getSelected());
                    }
                }
            });
            this.hideListener = new InputListener() {
                @Override
                public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                    final Actor target = event.getTarget();
                    if (SelectBoxList.this.isAscendantOf(target)) {
                        return false;
                    }
                    SelectBoxList.this.list.selection.set(selectBox.getSelected());
                    SelectBoxList.this.hide();
                    return false;
                }
                
                @Override
                public boolean keyDown(final InputEvent event, final int keycode) {
                    if (keycode == 131) {
                        SelectBoxList.this.hide();
                    }
                    return false;
                }
            };
        }
        
        public void show(final Stage stage) {
            if (this.list.isTouchable()) {
                return;
            }
            stage.removeCaptureListener(this.hideListener);
            stage.addCaptureListener(this.hideListener);
            stage.addActor(this);
            this.selectBox.localToStageCoordinates(this.screenPosition.set(0.0f, 0.0f));
            final float itemHeight = this.list.getItemHeight();
            float height = itemHeight * ((this.maxListCount <= 0) ? this.selectBox.items.size : Math.min(this.maxListCount, this.selectBox.items.size));
            final Drawable scrollPaneBackground = this.getStyle().background;
            if (scrollPaneBackground != null) {
                height += scrollPaneBackground.getTopHeight() + scrollPaneBackground.getBottomHeight();
            }
            final Drawable listBackground = this.list.getStyle().background;
            if (listBackground != null) {
                height += listBackground.getTopHeight() + listBackground.getBottomHeight();
            }
            final float heightBelow = this.screenPosition.y;
            final float heightAbove = stage.getCamera().viewportHeight - this.screenPosition.y - this.selectBox.getHeight();
            boolean below = true;
            if (height > heightBelow) {
                if (heightAbove > heightBelow) {
                    below = false;
                    height = Math.min(height, heightAbove);
                }
                else {
                    height = heightBelow;
                }
            }
            if (below) {
                this.setY(this.screenPosition.y - height);
            }
            else {
                this.setY(this.screenPosition.y + this.selectBox.getHeight());
            }
            this.setX(this.screenPosition.x);
            this.setHeight(height);
            this.validate();
            float width = Math.max(this.getPrefWidth(), this.selectBox.getWidth());
            if (this.getPrefHeight() > height && !this.disableY) {
                width += this.getScrollBarWidth();
            }
            this.setWidth(width);
            this.validate();
            this.scrollTo(0.0f, this.list.getHeight() - this.selectBox.getSelectedIndex() * itemHeight - itemHeight / 2.0f, 0.0f, 0.0f, true, true);
            this.updateVisualScroll();
            this.previousScrollFocus = null;
            final Actor actor = stage.getScrollFocus();
            if (actor != null && !actor.isDescendantOf(this)) {
                this.previousScrollFocus = actor;
            }
            stage.setScrollFocus(this);
            this.list.selection.set(this.selectBox.getSelected());
            this.list.setTouchable(Touchable.enabled);
            this.clearActions();
            this.selectBox.onShow(this, below);
        }
        
        public void hide() {
            if (!this.list.isTouchable() || !this.hasParent()) {
                return;
            }
            this.list.setTouchable(Touchable.disabled);
            final Stage stage = this.getStage();
            if (stage != null) {
                stage.removeCaptureListener(this.hideListener);
                if (this.previousScrollFocus != null && this.previousScrollFocus.getStage() == null) {
                    this.previousScrollFocus = null;
                }
                final Actor actor = stage.getScrollFocus();
                if (actor == null || this.isAscendantOf(actor)) {
                    stage.setScrollFocus(this.previousScrollFocus);
                }
            }
            this.clearActions();
            this.selectBox.onHide(this);
        }
        
        @Override
        public void draw(final Batch batch, final float parentAlpha) {
            this.selectBox.localToStageCoordinates(SelectBox.temp.set(0.0f, 0.0f));
            if (!SelectBox.temp.equals(this.screenPosition)) {
                this.hide();
            }
            super.draw(batch, parentAlpha);
        }
        
        @Override
        public void act(final float delta) {
            super.act(delta);
            this.toFront();
        }
    }
    
    public static class SelectBoxStyle
    {
        public BitmapFont font;
        public Color fontColor;
        public Color disabledFontColor;
        public Drawable background;
        public ScrollPane.ScrollPaneStyle scrollStyle;
        public List.ListStyle listStyle;
        public Drawable backgroundOver;
        public Drawable backgroundOpen;
        public Drawable backgroundDisabled;
        
        public SelectBoxStyle() {
            this.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public SelectBoxStyle(final BitmapFont font, final Color fontColor, final Drawable background, final ScrollPane.ScrollPaneStyle scrollStyle, final List.ListStyle listStyle) {
            this.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.font = font;
            this.fontColor.set(fontColor);
            this.background = background;
            this.scrollStyle = scrollStyle;
            this.listStyle = listStyle;
        }
        
        public SelectBoxStyle(final SelectBoxStyle style) {
            this.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.font = style.font;
            this.fontColor.set(style.fontColor);
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
            this.background = style.background;
            this.backgroundOver = style.backgroundOver;
            this.backgroundOpen = style.backgroundOpen;
            this.backgroundDisabled = style.backgroundDisabled;
            this.scrollStyle = new ScrollPane.ScrollPaneStyle(style.scrollStyle);
            this.listStyle = new List.ListStyle(style.listStyle);
        }
    }
}

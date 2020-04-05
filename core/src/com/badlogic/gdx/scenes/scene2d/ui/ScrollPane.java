// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScrollPane extends WidgetGroup
{
    private ScrollPaneStyle style;
    private Actor widget;
    final Rectangle hScrollBounds;
    final Rectangle vScrollBounds;
    final Rectangle hKnobBounds;
    final Rectangle vKnobBounds;
    private final Rectangle widgetAreaBounds;
    private final Rectangle widgetCullingArea;
    private final Rectangle scissorBounds;
    private ActorGestureListener flickScrollListener;
    boolean scrollX;
    boolean scrollY;
    boolean vScrollOnRight;
    boolean hScrollOnBottom;
    float amountX;
    float amountY;
    float visualAmountX;
    float visualAmountY;
    float maxX;
    float maxY;
    boolean touchScrollH;
    boolean touchScrollV;
    final Vector2 lastPoint;
    float areaWidth;
    float areaHeight;
    boolean fadeScrollBars;
    boolean smoothScrolling;
    boolean scrollBarTouch;
    float fadeAlpha;
    float fadeAlphaSeconds;
    float fadeDelay;
    float fadeDelaySeconds;
    boolean cancelTouchFocus;
    boolean flickScroll;
    float velocityX;
    float velocityY;
    float flingTimer;
    private boolean overscrollX;
    private boolean overscrollY;
    float flingTime;
    private float overscrollDistance;
    private float overscrollSpeedMin;
    private float overscrollSpeedMax;
    private boolean forceScrollX;
    private boolean forceScrollY;
    boolean disableX;
    boolean disableY;
    private boolean clamp;
    private boolean scrollbarsOnTop;
    private boolean variableSizeKnobs;
    int draggingPointer;
    
    public ScrollPane(final Actor widget) {
        this(widget, new ScrollPaneStyle());
    }
    
    public ScrollPane(final Actor widget, final Skin skin) {
        this(widget, skin.get(ScrollPaneStyle.class));
    }
    
    public ScrollPane(final Actor widget, final Skin skin, final String styleName) {
        this(widget, skin.get(styleName, ScrollPaneStyle.class));
    }
    
    public ScrollPane(final Actor widget, final ScrollPaneStyle style) {
        this.hScrollBounds = new Rectangle();
        this.vScrollBounds = new Rectangle();
        this.hKnobBounds = new Rectangle();
        this.vKnobBounds = new Rectangle();
        this.widgetAreaBounds = new Rectangle();
        this.widgetCullingArea = new Rectangle();
        this.scissorBounds = new Rectangle();
        this.vScrollOnRight = true;
        this.hScrollOnBottom = true;
        this.lastPoint = new Vector2();
        this.fadeScrollBars = true;
        this.smoothScrolling = true;
        this.scrollBarTouch = true;
        this.fadeAlphaSeconds = 1.0f;
        this.fadeDelaySeconds = 1.0f;
        this.cancelTouchFocus = true;
        this.flickScroll = true;
        this.overscrollX = true;
        this.overscrollY = true;
        this.flingTime = 1.0f;
        this.overscrollDistance = 50.0f;
        this.overscrollSpeedMin = 30.0f;
        this.overscrollSpeedMax = 200.0f;
        this.clamp = true;
        this.variableSizeKnobs = true;
        this.draggingPointer = -1;
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.setActor(widget);
        this.setSize(150.0f, 150.0f);
        this.addCaptureListener(new InputListener() {
            private float handlePosition;
            
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (ScrollPane.this.draggingPointer != -1) {
                    return false;
                }
                if (pointer == 0 && button != 0) {
                    return false;
                }
                ScrollPane.this.getStage().setScrollFocus(ScrollPane.this);
                if (!ScrollPane.this.flickScroll) {
                    ScrollPane.this.setScrollbarsVisible(true);
                }
                if (ScrollPane.this.fadeAlpha == 0.0f) {
                    return false;
                }
                if (ScrollPane.this.scrollBarTouch && ScrollPane.this.scrollX && ScrollPane.this.hScrollBounds.contains(x, y)) {
                    event.stop();
                    ScrollPane.this.setScrollbarsVisible(true);
                    if (ScrollPane.this.hKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.hKnobBounds.x;
                        ScrollPane.this.touchScrollH = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane.this.setScrollX(ScrollPane.this.amountX + ScrollPane.this.areaWidth * ((x < ScrollPane.this.hKnobBounds.x) ? -1 : 1));
                    return true;
                }
                else {
                    if (!ScrollPane.this.scrollBarTouch || !ScrollPane.this.scrollY || !ScrollPane.this.vScrollBounds.contains(x, y)) {
                        return false;
                    }
                    event.stop();
                    ScrollPane.this.setScrollbarsVisible(true);
                    if (ScrollPane.this.vKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.vKnobBounds.y;
                        ScrollPane.this.touchScrollV = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane.this.setScrollY(ScrollPane.this.amountY + ScrollPane.this.areaHeight * ((y < ScrollPane.this.vKnobBounds.y) ? 1 : -1));
                    return true;
                }
            }
            
            @Override
            public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer != ScrollPane.this.draggingPointer) {
                    return;
                }
                ScrollPane.this.cancel();
            }
            
            @Override
            public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
                if (pointer != ScrollPane.this.draggingPointer) {
                    return;
                }
                if (ScrollPane.this.touchScrollH) {
                    final float delta = x - ScrollPane.this.lastPoint.x;
                    float scrollH = this.handlePosition + delta;
                    this.handlePosition = scrollH;
                    scrollH = Math.max(ScrollPane.this.hScrollBounds.x, scrollH);
                    scrollH = Math.min(ScrollPane.this.hScrollBounds.x + ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width, scrollH);
                    final float total = ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width;
                    if (total != 0.0f) {
                        ScrollPane.this.setScrollPercentX((scrollH - ScrollPane.this.hScrollBounds.x) / total);
                    }
                    ScrollPane.this.lastPoint.set(x, y);
                }
                else if (ScrollPane.this.touchScrollV) {
                    final float delta = y - ScrollPane.this.lastPoint.y;
                    float scrollV = this.handlePosition + delta;
                    this.handlePosition = scrollV;
                    scrollV = Math.max(ScrollPane.this.vScrollBounds.y, scrollV);
                    scrollV = Math.min(ScrollPane.this.vScrollBounds.y + ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height, scrollV);
                    final float total = ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height;
                    if (total != 0.0f) {
                        ScrollPane.this.setScrollPercentY(1.0f - (scrollV - ScrollPane.this.vScrollBounds.y) / total);
                    }
                    ScrollPane.this.lastPoint.set(x, y);
                }
            }
            
            @Override
            public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                if (!ScrollPane.this.flickScroll) {
                    ScrollPane.this.setScrollbarsVisible(true);
                }
                return false;
            }
        });
        this.addListener(this.flickScrollListener = new ActorGestureListener() {
            @Override
            public void pan(final InputEvent event, final float x, final float y, final float deltaX, final float deltaY) {
                ScrollPane.this.setScrollbarsVisible(true);
                final ScrollPane this$0 = ScrollPane.this;
                this$0.amountX -= deltaX;
                final ScrollPane this$2 = ScrollPane.this;
                this$2.amountY += deltaY;
                ScrollPane.this.clamp();
                if (ScrollPane.this.cancelTouchFocus && ((ScrollPane.this.scrollX && deltaX != 0.0f) || (ScrollPane.this.scrollY && deltaY != 0.0f))) {
                    ScrollPane.this.cancelTouchFocus();
                }
            }
            
            @Override
            public void fling(final InputEvent event, final float x, final float y, final int button) {
                if (Math.abs(x) > 150.0f && ScrollPane.this.scrollX) {
                    ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                    ScrollPane.this.velocityX = x;
                    if (ScrollPane.this.cancelTouchFocus) {
                        ScrollPane.this.cancelTouchFocus();
                    }
                }
                if (Math.abs(y) > 150.0f && ScrollPane.this.scrollY) {
                    ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                    ScrollPane.this.velocityY = -y;
                    if (ScrollPane.this.cancelTouchFocus) {
                        ScrollPane.this.cancelTouchFocus();
                    }
                }
            }
            
            @Override
            public boolean handle(final Event event) {
                if (super.handle(event)) {
                    if (((InputEvent)event).getType() == InputEvent.Type.touchDown) {
                        ScrollPane.this.flingTimer = 0.0f;
                    }
                    return true;
                }
                if (event instanceof InputEvent && ((InputEvent)event).isTouchFocusCancel()) {
                    ScrollPane.this.cancel();
                }
                return false;
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean scrolled(final InputEvent event, final float x, final float y, final int amount) {
                ScrollPane.this.setScrollbarsVisible(true);
                if (ScrollPane.this.scrollY) {
                    ScrollPane.this.setScrollY(ScrollPane.this.amountY + ScrollPane.this.getMouseWheelY() * amount);
                }
                else {
                    if (!ScrollPane.this.scrollX) {
                        return false;
                    }
                    ScrollPane.this.setScrollX(ScrollPane.this.amountX + ScrollPane.this.getMouseWheelX() * amount);
                }
                return true;
            }
        });
    }
    
    public void setScrollbarsVisible(final boolean visible) {
        if (visible) {
            this.fadeAlpha = this.fadeAlphaSeconds;
            this.fadeDelay = this.fadeDelaySeconds;
        }
        else {
            this.fadeAlpha = 0.0f;
            this.fadeDelay = 0.0f;
        }
    }
    
    public void cancelTouchFocus() {
        final Stage stage = this.getStage();
        if (stage != null) {
            stage.cancelTouchFocusExcept(this.flickScrollListener, this);
        }
    }
    
    public void cancel() {
        this.draggingPointer = -1;
        this.touchScrollH = false;
        this.touchScrollV = false;
        this.flickScrollListener.getGestureDetector().cancel();
    }
    
    void clamp() {
        if (!this.clamp) {
            return;
        }
        this.scrollX(this.overscrollX ? MathUtils.clamp(this.amountX, -this.overscrollDistance, this.maxX + this.overscrollDistance) : MathUtils.clamp(this.amountX, 0.0f, this.maxX));
        this.scrollY(this.overscrollY ? MathUtils.clamp(this.amountY, -this.overscrollDistance, this.maxY + this.overscrollDistance) : MathUtils.clamp(this.amountY, 0.0f, this.maxY));
    }
    
    public void setStyle(final ScrollPaneStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.invalidateHierarchy();
    }
    
    public ScrollPaneStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void act(final float delta) {
        super.act(delta);
        final boolean panning = this.flickScrollListener.getGestureDetector().isPanning();
        boolean animating = false;
        if (this.fadeAlpha > 0.0f && this.fadeScrollBars && !panning && !this.touchScrollH && !this.touchScrollV) {
            this.fadeDelay -= delta;
            if (this.fadeDelay <= 0.0f) {
                this.fadeAlpha = Math.max(0.0f, this.fadeAlpha - delta);
            }
            animating = true;
        }
        if (this.flingTimer > 0.0f) {
            this.setScrollbarsVisible(true);
            final float alpha = this.flingTimer / this.flingTime;
            this.amountX -= this.velocityX * alpha * delta;
            this.amountY -= this.velocityY * alpha * delta;
            this.clamp();
            if (this.amountX == -this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountX >= this.maxX + this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountY == -this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            if (this.amountY >= this.maxY + this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            this.flingTimer -= delta;
            if (this.flingTimer <= 0.0f) {
                this.velocityX = 0.0f;
                this.velocityY = 0.0f;
            }
            animating = true;
        }
        if (this.smoothScrolling && this.flingTimer <= 0.0f && !panning && (!this.touchScrollH || (this.scrollX && this.maxX / (this.hScrollBounds.width - this.hKnobBounds.width) > this.areaWidth * 0.1f)) && (!this.touchScrollV || (this.scrollY && this.maxY / (this.vScrollBounds.height - this.vKnobBounds.height) > this.areaHeight * 0.1f))) {
            if (this.visualAmountX != this.amountX) {
                if (this.visualAmountX < this.amountX) {
                    this.visualScrollX(Math.min(this.amountX, this.visualAmountX + Math.max(200.0f * delta, (this.amountX - this.visualAmountX) * 7.0f * delta)));
                }
                else {
                    this.visualScrollX(Math.max(this.amountX, this.visualAmountX - Math.max(200.0f * delta, (this.visualAmountX - this.amountX) * 7.0f * delta)));
                }
                animating = true;
            }
            if (this.visualAmountY != this.amountY) {
                if (this.visualAmountY < this.amountY) {
                    this.visualScrollY(Math.min(this.amountY, this.visualAmountY + Math.max(200.0f * delta, (this.amountY - this.visualAmountY) * 7.0f * delta)));
                }
                else {
                    this.visualScrollY(Math.max(this.amountY, this.visualAmountY - Math.max(200.0f * delta, (this.visualAmountY - this.amountY) * 7.0f * delta)));
                }
                animating = true;
            }
        }
        else {
            if (this.visualAmountX != this.amountX) {
                this.visualScrollX(this.amountX);
            }
            if (this.visualAmountY != this.amountY) {
                this.visualScrollY(this.amountY);
            }
        }
        if (!panning) {
            if (this.overscrollX && this.scrollX) {
                if (this.amountX < 0.0f) {
                    this.setScrollbarsVisible(true);
                    this.amountX += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountX / this.overscrollDistance) * delta;
                    if (this.amountX > 0.0f) {
                        this.scrollX(0.0f);
                    }
                    animating = true;
                }
                else if (this.amountX > this.maxX) {
                    this.setScrollbarsVisible(true);
                    this.amountX -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxX - this.amountX) / this.overscrollDistance) * delta;
                    if (this.amountX < this.maxX) {
                        this.scrollX(this.maxX);
                    }
                    animating = true;
                }
            }
            if (this.overscrollY && this.scrollY) {
                if (this.amountY < 0.0f) {
                    this.setScrollbarsVisible(true);
                    this.amountY += (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -this.amountY / this.overscrollDistance) * delta;
                    if (this.amountY > 0.0f) {
                        this.scrollY(0.0f);
                    }
                    animating = true;
                }
                else if (this.amountY > this.maxY) {
                    this.setScrollbarsVisible(true);
                    this.amountY -= (this.overscrollSpeedMin + (this.overscrollSpeedMax - this.overscrollSpeedMin) * -(this.maxY - this.amountY) / this.overscrollDistance) * delta;
                    if (this.amountY < this.maxY) {
                        this.scrollY(this.maxY);
                    }
                    animating = true;
                }
            }
        }
        if (animating) {
            final Stage stage = this.getStage();
            if (stage != null && stage.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
        }
    }
    
    @Override
    public void layout() {
        final Drawable bg = this.style.background;
        final Drawable hScrollKnob = this.style.hScrollKnob;
        final Drawable vScrollKnob = this.style.vScrollKnob;
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        float bgTopHeight = 0.0f;
        float bgBottomHeight = 0.0f;
        if (bg != null) {
            bgLeftWidth = bg.getLeftWidth();
            bgRightWidth = bg.getRightWidth();
            bgTopHeight = bg.getTopHeight();
            bgBottomHeight = bg.getBottomHeight();
        }
        final float width = this.getWidth();
        final float height = this.getHeight();
        float scrollbarHeight = 0.0f;
        if (hScrollKnob != null) {
            scrollbarHeight = hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
        }
        float scrollbarWidth = 0.0f;
        if (vScrollKnob != null) {
            scrollbarWidth = vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
        }
        this.areaWidth = width - bgLeftWidth - bgRightWidth;
        this.areaHeight = height - bgTopHeight - bgBottomHeight;
        if (this.widget == null) {
            return;
        }
        float widgetWidth;
        float widgetHeight;
        if (this.widget instanceof Layout) {
            final Layout layout = (Layout)this.widget;
            widgetWidth = layout.getPrefWidth();
            widgetHeight = layout.getPrefHeight();
        }
        else {
            widgetWidth = this.widget.getWidth();
            widgetHeight = this.widget.getHeight();
        }
        this.scrollX = (this.forceScrollX || (widgetWidth > this.areaWidth && !this.disableX));
        this.scrollY = (this.forceScrollY || (widgetHeight > this.areaHeight && !this.disableY));
        final boolean fade = this.fadeScrollBars;
        if (!fade) {
            if (this.scrollY) {
                this.areaWidth -= scrollbarWidth;
                if (!this.scrollX && widgetWidth > this.areaWidth && !this.disableX) {
                    this.scrollX = true;
                }
            }
            if (this.scrollX) {
                this.areaHeight -= scrollbarHeight;
                if (!this.scrollY && widgetHeight > this.areaHeight && !this.disableY) {
                    this.scrollY = true;
                    this.areaWidth -= scrollbarWidth;
                }
            }
        }
        this.widgetAreaBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, this.areaHeight);
        if (fade) {
            if (this.scrollX && this.scrollY) {
                this.areaHeight -= scrollbarHeight;
                this.areaWidth -= scrollbarWidth;
            }
        }
        else if (this.scrollbarsOnTop) {
            if (this.scrollX) {
                final Rectangle widgetAreaBounds = this.widgetAreaBounds;
                widgetAreaBounds.height += scrollbarHeight;
            }
            if (this.scrollY) {
                final Rectangle widgetAreaBounds2 = this.widgetAreaBounds;
                widgetAreaBounds2.width += scrollbarWidth;
            }
        }
        else {
            if (this.scrollX && this.hScrollOnBottom) {
                final Rectangle widgetAreaBounds3 = this.widgetAreaBounds;
                widgetAreaBounds3.y += scrollbarHeight;
            }
            if (this.scrollY && !this.vScrollOnRight) {
                final Rectangle widgetAreaBounds4 = this.widgetAreaBounds;
                widgetAreaBounds4.x += scrollbarWidth;
            }
        }
        widgetWidth = (this.disableX ? this.areaWidth : Math.max(this.areaWidth, widgetWidth));
        widgetHeight = (this.disableY ? this.areaHeight : Math.max(this.areaHeight, widgetHeight));
        this.maxX = widgetWidth - this.areaWidth;
        this.maxY = widgetHeight - this.areaHeight;
        if (fade && this.scrollX && this.scrollY) {
            this.maxY -= scrollbarHeight;
            this.maxX -= scrollbarWidth;
        }
        this.scrollX(MathUtils.clamp(this.amountX, 0.0f, this.maxX));
        this.scrollY(MathUtils.clamp(this.amountY, 0.0f, this.maxY));
        if (this.scrollX) {
            if (hScrollKnob != null) {
                final float hScrollHeight = (this.style.hScroll != null) ? this.style.hScroll.getMinHeight() : hScrollKnob.getMinHeight();
                final float boundsX = this.vScrollOnRight ? bgLeftWidth : (bgLeftWidth + scrollbarWidth);
                final float boundsY = this.hScrollOnBottom ? bgBottomHeight : (height - bgTopHeight - hScrollHeight);
                this.hScrollBounds.set(boundsX, boundsY, this.areaWidth, hScrollHeight);
                if (this.variableSizeKnobs) {
                    this.hKnobBounds.width = Math.max(hScrollKnob.getMinWidth(), (float)(int)(this.hScrollBounds.width * this.areaWidth / widgetWidth));
                }
                else {
                    this.hKnobBounds.width = hScrollKnob.getMinWidth();
                }
                if (this.hKnobBounds.width > widgetWidth) {
                    this.hKnobBounds.width = 0.0f;
                }
                this.hKnobBounds.height = hScrollKnob.getMinHeight();
                this.hKnobBounds.x = this.hScrollBounds.x + (int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getScrollPercentX());
                this.hKnobBounds.y = this.hScrollBounds.y;
            }
            else {
                this.hScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.hKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        if (this.scrollY) {
            if (vScrollKnob != null) {
                final float vScrollWidth = (this.style.vScroll != null) ? this.style.vScroll.getMinWidth() : vScrollKnob.getMinWidth();
                float boundsY;
                if (this.hScrollOnBottom) {
                    boundsY = height - bgTopHeight - this.areaHeight;
                }
                else {
                    boundsY = bgBottomHeight;
                }
                float boundsX;
                if (this.vScrollOnRight) {
                    boundsX = width - bgRightWidth - vScrollWidth;
                }
                else {
                    boundsX = bgLeftWidth;
                }
                this.vScrollBounds.set(boundsX, boundsY, vScrollWidth, this.areaHeight);
                this.vKnobBounds.width = vScrollKnob.getMinWidth();
                if (this.variableSizeKnobs) {
                    this.vKnobBounds.height = Math.max(vScrollKnob.getMinHeight(), (float)(int)(this.vScrollBounds.height * this.areaHeight / widgetHeight));
                }
                else {
                    this.vKnobBounds.height = vScrollKnob.getMinHeight();
                }
                if (this.vKnobBounds.height > widgetHeight) {
                    this.vKnobBounds.height = 0.0f;
                }
                if (this.vScrollOnRight) {
                    this.vKnobBounds.x = width - bgRightWidth - vScrollKnob.getMinWidth();
                }
                else {
                    this.vKnobBounds.x = bgLeftWidth;
                }
                this.vKnobBounds.y = this.vScrollBounds.y + (int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - this.getScrollPercentY()));
            }
            else {
                this.vScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.vKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        this.updateWidgetPosition();
        this.widget.setSize(widgetWidth, widgetHeight);
        if (this.widget instanceof Layout) {
            ((Layout)this.widget).validate();
        }
    }
    
    private void updateWidgetPosition() {
        float y = this.widgetAreaBounds.y;
        if (!this.scrollY) {
            y -= (int)this.maxY;
        }
        else {
            y -= (int)(this.maxY - this.visualAmountY);
        }
        float x = this.widgetAreaBounds.x;
        if (this.scrollX) {
            x -= (int)this.visualAmountX;
        }
        if (!this.fadeScrollBars && this.scrollbarsOnTop) {
            if (this.scrollX && this.hScrollOnBottom) {
                float scrollbarHeight = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarHeight = this.style.hScrollKnob.getMinHeight();
                }
                if (this.style.hScroll != null) {
                    scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
                }
                y += scrollbarHeight;
            }
            if (this.scrollY && !this.vScrollOnRight) {
                float scrollbarWidth = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarWidth = this.style.hScrollKnob.getMinWidth();
                }
                if (this.style.hScroll != null) {
                    scrollbarWidth = Math.max(scrollbarWidth, this.style.hScroll.getMinWidth());
                }
                x += scrollbarWidth;
            }
        }
        this.widget.setPosition(x, y);
        if (this.widget instanceof Cullable) {
            this.widgetCullingArea.x = this.widgetAreaBounds.x - x;
            this.widgetCullingArea.y = this.widgetAreaBounds.y - y;
            this.widgetCullingArea.width = this.widgetAreaBounds.width;
            this.widgetCullingArea.height = this.widgetAreaBounds.height;
            ((Cullable)this.widget).setCullingArea(this.widgetCullingArea);
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        if (this.widget == null) {
            return;
        }
        this.validate();
        this.applyTransform(batch, this.computeTransform());
        if (this.scrollX) {
            this.hKnobBounds.x = this.hScrollBounds.x + (int)((this.hScrollBounds.width - this.hKnobBounds.width) * this.getVisualScrollPercentX());
        }
        if (this.scrollY) {
            this.vKnobBounds.y = this.vScrollBounds.y + (int)((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - this.getVisualScrollPercentY()));
        }
        this.updateWidgetPosition();
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (this.style.background != null) {
            this.style.background.draw(batch, 0.0f, 0.0f, this.getWidth(), this.getHeight());
        }
        this.getStage().calculateScissors(this.widgetAreaBounds, this.scissorBounds);
        batch.flush();
        if (ScissorStack.pushScissors(this.scissorBounds)) {
            this.drawChildren(batch, parentAlpha);
            batch.flush();
            ScissorStack.popScissors();
        }
        final float alpha = color.a * parentAlpha * Interpolation.fade.apply(this.fadeAlpha / this.fadeAlphaSeconds);
        this.drawScrollBars(batch, color.r, color.g, color.b, alpha);
        this.resetTransform(batch);
    }
    
    protected void drawScrollBars(final Batch batch, final float r, final float g, final float b, final float a) {
        if (a <= 0.0f) {
            return;
        }
        batch.setColor(r, g, b, a);
        final boolean x = this.scrollX && this.hKnobBounds.width > 0.0f;
        final boolean y = this.scrollY && this.vKnobBounds.height > 0.0f;
        if (x && y && this.style.corner != null) {
            this.style.corner.draw(batch, this.hScrollBounds.x + this.hScrollBounds.width, this.hScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.y);
        }
        if (x) {
            if (this.style.hScroll != null) {
                this.style.hScroll.draw(batch, this.hScrollBounds.x, this.hScrollBounds.y, this.hScrollBounds.width, this.hScrollBounds.height);
            }
            if (this.style.hScrollKnob != null) {
                this.style.hScrollKnob.draw(batch, this.hKnobBounds.x, this.hKnobBounds.y, this.hKnobBounds.width, this.hKnobBounds.height);
            }
        }
        if (y) {
            if (this.style.vScroll != null) {
                this.style.vScroll.draw(batch, this.vScrollBounds.x, this.vScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.height);
            }
            if (this.style.vScrollKnob != null) {
                this.style.vScrollKnob.draw(batch, this.vKnobBounds.x, this.vKnobBounds.y, this.vKnobBounds.width, this.vKnobBounds.height);
            }
        }
    }
    
    public void fling(final float flingTime, final float velocityX, final float velocityY) {
        this.flingTimer = flingTime;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    @Override
    public float getPrefWidth() {
        if (this.widget instanceof Layout) {
            float width = ((Layout)this.widget).getPrefWidth();
            if (this.style.background != null) {
                width += this.style.background.getLeftWidth() + this.style.background.getRightWidth();
            }
            if (this.forceScrollY) {
                float scrollbarWidth = 0.0f;
                if (this.style.vScrollKnob != null) {
                    scrollbarWidth = this.style.vScrollKnob.getMinWidth();
                }
                if (this.style.vScroll != null) {
                    scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
                }
                width += scrollbarWidth;
            }
            return width;
        }
        return 150.0f;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.widget instanceof Layout) {
            float height = ((Layout)this.widget).getPrefHeight();
            if (this.style.background != null) {
                height += this.style.background.getTopHeight() + this.style.background.getBottomHeight();
            }
            if (this.forceScrollX) {
                float scrollbarHeight = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarHeight = this.style.hScrollKnob.getMinHeight();
                }
                if (this.style.hScroll != null) {
                    scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
                }
                height += scrollbarHeight;
            }
            return height;
        }
        return 150.0f;
    }
    
    @Override
    public float getMinWidth() {
        return 0.0f;
    }
    
    @Override
    public float getMinHeight() {
        return 0.0f;
    }
    
    public void setActor(final Actor actor) {
        if (this.widget == this) {
            throw new IllegalArgumentException("widget cannot be the ScrollPane.");
        }
        if (this.widget != null) {
            super.removeActor(this.widget);
        }
        this.widget = actor;
        if (this.widget != null) {
            super.addActor(this.widget);
        }
    }
    
    public Actor getActor() {
        return this.widget;
    }
    
    @Deprecated
    public void setWidget(final Actor actor) {
        this.setActor(actor);
    }
    
    @Deprecated
    public Actor getWidget() {
        return this.widget;
    }
    
    @Override
    @Deprecated
    public void addActor(final Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }
    
    @Override
    @Deprecated
    public void addActorAt(final int index, final Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }
    
    @Override
    @Deprecated
    public void addActorBefore(final Actor actorBefore, final Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }
    
    @Override
    @Deprecated
    public void addActorAfter(final Actor actorAfter, final Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }
    
    @Override
    public boolean removeActor(final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.widget) {
            return false;
        }
        this.setActor(null);
        return true;
    }
    
    @Override
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.widget) {
            return false;
        }
        this.widget = null;
        return super.removeActor(actor, unfocus);
    }
    
    @Override
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (x < 0.0f || x >= this.getWidth() || y < 0.0f || y >= this.getHeight()) {
            return null;
        }
        if (touchable && this.getTouchable() == Touchable.enabled && this.isVisible()) {
            if (this.scrollX && this.touchScrollH && this.hScrollBounds.contains(x, y)) {
                return this;
            }
            if (this.scrollY && this.touchScrollV && this.vScrollBounds.contains(x, y)) {
                return this;
            }
        }
        return super.hit(x, y, touchable);
    }
    
    protected void scrollX(final float pixelsX) {
        this.amountX = pixelsX;
    }
    
    protected void scrollY(final float pixelsY) {
        this.amountY = pixelsY;
    }
    
    protected void visualScrollX(final float pixelsX) {
        this.visualAmountX = pixelsX;
    }
    
    protected void visualScrollY(final float pixelsY) {
        this.visualAmountY = pixelsY;
    }
    
    protected float getMouseWheelX() {
        return Math.min(this.areaWidth, Math.max(this.areaWidth * 0.9f, this.maxX * 0.1f) / 4.0f);
    }
    
    protected float getMouseWheelY() {
        return Math.min(this.areaHeight, Math.max(this.areaHeight * 0.9f, this.maxY * 0.1f) / 4.0f);
    }
    
    public void setScrollX(final float pixels) {
        this.scrollX(MathUtils.clamp(pixels, 0.0f, this.maxX));
    }
    
    public float getScrollX() {
        return this.amountX;
    }
    
    public void setScrollY(final float pixels) {
        this.scrollY(MathUtils.clamp(pixels, 0.0f, this.maxY));
    }
    
    public float getScrollY() {
        return this.amountY;
    }
    
    public void updateVisualScroll() {
        this.visualAmountX = this.amountX;
        this.visualAmountY = this.amountY;
    }
    
    public float getVisualScrollX() {
        return this.scrollX ? this.visualAmountX : 0.0f;
    }
    
    public float getVisualScrollY() {
        return this.scrollY ? this.visualAmountY : 0.0f;
    }
    
    public float getVisualScrollPercentX() {
        if (this.maxX == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountX / this.maxX, 0.0f, 1.0f);
    }
    
    public float getVisualScrollPercentY() {
        if (this.maxY == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountY / this.maxY, 0.0f, 1.0f);
    }
    
    public float getScrollPercentX() {
        if (this.maxX == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountX / this.maxX, 0.0f, 1.0f);
    }
    
    public void setScrollPercentX(final float percentX) {
        this.scrollX(this.maxX * MathUtils.clamp(percentX, 0.0f, 1.0f));
    }
    
    public float getScrollPercentY() {
        if (this.maxY == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountY / this.maxY, 0.0f, 1.0f);
    }
    
    public void setScrollPercentY(final float percentY) {
        this.scrollY(this.maxY * MathUtils.clamp(percentY, 0.0f, 1.0f));
    }
    
    public void setFlickScroll(final boolean flickScroll) {
        if (this.flickScroll == flickScroll) {
            return;
        }
        this.flickScroll = flickScroll;
        if (flickScroll) {
            this.addListener(this.flickScrollListener);
        }
        else {
            this.removeListener(this.flickScrollListener);
        }
        this.invalidate();
    }
    
    public void setFlickScrollTapSquareSize(final float halfTapSquareSize) {
        this.flickScrollListener.getGestureDetector().setTapSquareSize(halfTapSquareSize);
    }
    
    public void scrollTo(final float x, final float y, final float width, final float height) {
        this.scrollTo(x, y, width, height, false, false);
    }
    
    public void scrollTo(final float x, final float y, final float width, final float height, final boolean centerHorizontal, final boolean centerVertical) {
        float amountX = this.amountX;
        if (centerHorizontal) {
            amountX = x - this.areaWidth / 2.0f + width / 2.0f;
        }
        else {
            if (x + width > amountX + this.areaWidth) {
                amountX = x + width - this.areaWidth;
            }
            if (x < amountX) {
                amountX = x;
            }
        }
        this.scrollX(MathUtils.clamp(amountX, 0.0f, this.maxX));
        float amountY = this.amountY;
        if (centerVertical) {
            amountY = this.maxY - y + this.areaHeight / 2.0f - height / 2.0f;
        }
        else {
            if (amountY > this.maxY - y - height + this.areaHeight) {
                amountY = this.maxY - y - height + this.areaHeight;
            }
            if (amountY < this.maxY - y) {
                amountY = this.maxY - y;
            }
        }
        this.scrollY(MathUtils.clamp(amountY, 0.0f, this.maxY));
    }
    
    public float getMaxX() {
        return this.maxX;
    }
    
    public float getMaxY() {
        return this.maxY;
    }
    
    public float getScrollBarHeight() {
        if (!this.scrollX) {
            return 0.0f;
        }
        float height = 0.0f;
        if (this.style.hScrollKnob != null) {
            height = this.style.hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            height = Math.max(height, this.style.hScroll.getMinHeight());
        }
        return height;
    }
    
    public float getScrollBarWidth() {
        if (!this.scrollY) {
            return 0.0f;
        }
        float width = 0.0f;
        if (this.style.vScrollKnob != null) {
            width = this.style.vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            width = Math.max(width, this.style.vScroll.getMinWidth());
        }
        return width;
    }
    
    public float getScrollWidth() {
        return this.areaWidth;
    }
    
    public float getScrollHeight() {
        return this.areaHeight;
    }
    
    public boolean isScrollX() {
        return this.scrollX;
    }
    
    public boolean isScrollY() {
        return this.scrollY;
    }
    
    public void setScrollingDisabled(final boolean x, final boolean y) {
        this.disableX = x;
        this.disableY = y;
        this.invalidate();
    }
    
    public boolean isScrollingDisabledX() {
        return this.disableX;
    }
    
    public boolean isScrollingDisabledY() {
        return this.disableY;
    }
    
    public boolean isLeftEdge() {
        return !this.scrollX || this.amountX <= 0.0f;
    }
    
    public boolean isRightEdge() {
        return !this.scrollX || this.amountX >= this.maxX;
    }
    
    public boolean isTopEdge() {
        return !this.scrollY || this.amountY <= 0.0f;
    }
    
    public boolean isBottomEdge() {
        return !this.scrollY || this.amountY >= this.maxY;
    }
    
    public boolean isDragging() {
        return this.draggingPointer != -1;
    }
    
    public boolean isPanning() {
        return this.flickScrollListener.getGestureDetector().isPanning();
    }
    
    public boolean isFlinging() {
        return this.flingTimer > 0.0f;
    }
    
    public void setVelocityX(final float velocityX) {
        this.velocityX = velocityX;
    }
    
    public float getVelocityX() {
        return this.velocityX;
    }
    
    public void setVelocityY(final float velocityY) {
        this.velocityY = velocityY;
    }
    
    public float getVelocityY() {
        return this.velocityY;
    }
    
    public void setOverscroll(final boolean overscrollX, final boolean overscrollY) {
        this.overscrollX = overscrollX;
        this.overscrollY = overscrollY;
    }
    
    public void setupOverscroll(final float distance, final float speedMin, final float speedMax) {
        this.overscrollDistance = distance;
        this.overscrollSpeedMin = speedMin;
        this.overscrollSpeedMax = speedMax;
    }
    
    public float getOverscrollDistance() {
        return this.overscrollDistance;
    }
    
    public void setForceScroll(final boolean x, final boolean y) {
        this.forceScrollX = x;
        this.forceScrollY = y;
    }
    
    public boolean isForceScrollX() {
        return this.forceScrollX;
    }
    
    public boolean isForceScrollY() {
        return this.forceScrollY;
    }
    
    public void setFlingTime(final float flingTime) {
        this.flingTime = flingTime;
    }
    
    public void setClamp(final boolean clamp) {
        this.clamp = clamp;
    }
    
    public void setScrollBarPositions(final boolean bottom, final boolean right) {
        this.hScrollOnBottom = bottom;
        this.vScrollOnRight = right;
    }
    
    public void setFadeScrollBars(final boolean fadeScrollBars) {
        if (this.fadeScrollBars == fadeScrollBars) {
            return;
        }
        if (!(this.fadeScrollBars = fadeScrollBars)) {
            this.fadeAlpha = this.fadeAlphaSeconds;
        }
        this.invalidate();
    }
    
    public void setupFadeScrollBars(final float fadeAlphaSeconds, final float fadeDelaySeconds) {
        this.fadeAlphaSeconds = fadeAlphaSeconds;
        this.fadeDelaySeconds = fadeDelaySeconds;
    }
    
    public boolean getFadeScrollBars() {
        return this.fadeScrollBars;
    }
    
    public void setScrollBarTouch(final boolean scrollBarTouch) {
        this.scrollBarTouch = scrollBarTouch;
    }
    
    public void setSmoothScrolling(final boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
    }
    
    public void setScrollbarsOnTop(final boolean scrollbarsOnTop) {
        this.scrollbarsOnTop = scrollbarsOnTop;
        this.invalidate();
    }
    
    public boolean getVariableSizeKnobs() {
        return this.variableSizeKnobs;
    }
    
    public void setVariableSizeKnobs(final boolean variableSizeKnobs) {
        this.variableSizeKnobs = variableSizeKnobs;
    }
    
    public void setCancelTouchFocus(final boolean cancelTouchFocus) {
        this.cancelTouchFocus = cancelTouchFocus;
    }
    
    @Override
    public void drawDebug(final ShapeRenderer shapes) {
        shapes.flush();
        this.applyTransform(shapes, this.computeTransform());
        if (ScissorStack.pushScissors(this.scissorBounds)) {
            this.drawDebugChildren(shapes);
            ScissorStack.popScissors();
        }
        this.resetTransform(shapes);
    }
    
    public static class ScrollPaneStyle
    {
        public Drawable background;
        public Drawable corner;
        public Drawable hScroll;
        public Drawable hScrollKnob;
        public Drawable vScroll;
        public Drawable vScrollKnob;
        
        public ScrollPaneStyle() {
        }
        
        public ScrollPaneStyle(final Drawable background, final Drawable hScroll, final Drawable hScrollKnob, final Drawable vScroll, final Drawable vScrollKnob) {
            this.background = background;
            this.hScroll = hScroll;
            this.hScrollKnob = hScrollKnob;
            this.vScroll = vScroll;
            this.vScrollKnob = vScrollKnob;
        }
        
        public ScrollPaneStyle(final ScrollPaneStyle style) {
            this.background = style.background;
            this.hScroll = style.hScroll;
            this.hScrollKnob = style.hScrollKnob;
            this.vScroll = style.vScroll;
            this.vScrollKnob = style.vScrollKnob;
        }
    }
}

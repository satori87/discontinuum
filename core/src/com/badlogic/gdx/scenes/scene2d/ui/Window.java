// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.math.Vector2;

public class Window extends Table
{
    private static final Vector2 tmpPosition;
    private static final Vector2 tmpSize;
    private static final int MOVE = 32;
    private WindowStyle style;
    boolean isMovable;
    boolean isModal;
    boolean isResizable;
    int resizeBorder;
    boolean keepWithinStage;
    Label titleLabel;
    Table titleTable;
    boolean drawTitleTable;
    protected int edge;
    protected boolean dragging;
    
    static {
        tmpPosition = new Vector2();
        tmpSize = new Vector2();
    }
    
    public Window(final String title, final Skin skin) {
        this(title, skin.get(WindowStyle.class));
        this.setSkin(skin);
    }
    
    public Window(final String title, final Skin skin, final String styleName) {
        this(title, skin.get(styleName, WindowStyle.class));
        this.setSkin(skin);
    }
    
    public Window(final String title, final WindowStyle style) {
        this.isMovable = true;
        this.resizeBorder = 8;
        this.keepWithinStage = true;
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null.");
        }
        this.setTouchable(Touchable.enabled);
        this.setClip(true);
        (this.titleLabel = new Label(title, new Label.LabelStyle(style.titleFont, style.titleFontColor))).setEllipsis(true);
        this.titleTable = new Table() {
            @Override
            public void draw(final Batch batch, final float parentAlpha) {
                if (Window.this.drawTitleTable) {
                    super.draw(batch, parentAlpha);
                }
            }
        };
        this.titleTable.add(this.titleLabel).expandX().fillX().minWidth(0.0f);
        this.addActor(this.titleTable);
        this.setStyle(style);
        this.setWidth(150.0f);
        this.setHeight(150.0f);
        this.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                Window.this.toFront();
                return false;
            }
        });
        this.addListener(new InputListener() {
            float startX;
            float startY;
            float lastX;
            float lastY;
            
            private void updateEdge(final float x, final float y) {
                float border = Window.this.resizeBorder / 2.0f;
                final float width = Window.this.getWidth();
                final float height = Window.this.getHeight();
                final float padTop = Window.this.getPadTop();
                final float padLeft = Window.this.getPadLeft();
                final float padBottom = Window.this.getPadBottom();
                final float padRight = Window.this.getPadRight();
                final float left = padLeft;
                final float right = width - padRight;
                final float bottom = padBottom;
                Window.this.edge = 0;
                if (Window.this.isResizable && x >= left - border && x <= right + border && y >= bottom - border) {
                    if (x < left + border) {
                        final Window this$0 = Window.this;
                        this$0.edge |= 0x8;
                    }
                    if (x > right - border) {
                        final Window this$2 = Window.this;
                        this$2.edge |= 0x10;
                    }
                    if (y < bottom + border) {
                        final Window this$3 = Window.this;
                        this$3.edge |= 0x4;
                    }
                    if (Window.this.edge != 0) {
                        border += 25.0f;
                    }
                    if (x < left + border) {
                        final Window this$4 = Window.this;
                        this$4.edge |= 0x8;
                    }
                    if (x > right - border) {
                        final Window this$5 = Window.this;
                        this$5.edge |= 0x10;
                    }
                    if (y < bottom + border) {
                        final Window this$6 = Window.this;
                        this$6.edge |= 0x4;
                    }
                }
                if (Window.this.isMovable && Window.this.edge == 0 && y <= height && y >= height - padTop && x >= left && x <= right) {
                    Window.this.edge = 32;
                }
            }
            
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (button == 0) {
                    this.updateEdge(x, y);
                    Window.this.dragging = (Window.this.edge != 0);
                    this.startX = x;
                    this.startY = y;
                    this.lastX = x - Window.this.getWidth();
                    this.lastY = y - Window.this.getHeight();
                }
                return Window.this.edge != 0 || Window.this.isModal;
            }
            
            @Override
            public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                Window.this.dragging = false;
            }
            
            @Override
            public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
                if (!Window.this.dragging) {
                    return;
                }
                float width = Window.this.getWidth();
                float height = Window.this.getHeight();
                float windowX = Window.this.getX();
                float windowY = Window.this.getY();
                final float minWidth = Window.this.getMinWidth();
                final float maxWidth = Window.this.getMaxWidth();
                final float minHeight = Window.this.getMinHeight();
                final float maxHeight = Window.this.getMaxHeight();
                final Stage stage = Window.this.getStage();
                final boolean clampPosition = Window.this.keepWithinStage && Window.this.getParent() == stage.getRoot();
                if ((Window.this.edge & 0x20) != 0x0) {
                    final float amountX = x - this.startX;
                    final float amountY = y - this.startY;
                    windowX += amountX;
                    windowY += amountY;
                }
                if ((Window.this.edge & 0x8) != 0x0) {
                    float amountX = x - this.startX;
                    if (width - amountX < minWidth) {
                        amountX = -(minWidth - width);
                    }
                    if (clampPosition && windowX + amountX < 0.0f) {
                        amountX = -windowX;
                    }
                    width -= amountX;
                    windowX += amountX;
                }
                if ((Window.this.edge & 0x4) != 0x0) {
                    float amountY2 = y - this.startY;
                    if (height - amountY2 < minHeight) {
                        amountY2 = -(minHeight - height);
                    }
                    if (clampPosition && windowY + amountY2 < 0.0f) {
                        amountY2 = -windowY;
                    }
                    height -= amountY2;
                    windowY += amountY2;
                }
                if ((Window.this.edge & 0x10) != 0x0) {
                    float amountX = x - this.lastX - width;
                    if (width + amountX < minWidth) {
                        amountX = minWidth - width;
                    }
                    if (clampPosition && windowX + width + amountX > stage.getWidth()) {
                        amountX = stage.getWidth() - windowX - width;
                    }
                    width += amountX;
                }
                if ((Window.this.edge & 0x2) != 0x0) {
                    float amountY2 = y - this.lastY - height;
                    if (height + amountY2 < minHeight) {
                        amountY2 = minHeight - height;
                    }
                    if (clampPosition && windowY + height + amountY2 > stage.getHeight()) {
                        amountY2 = stage.getHeight() - windowY - height;
                    }
                    height += amountY2;
                }
                Window.this.setBounds((float)Math.round(windowX), (float)Math.round(windowY), (float)Math.round(width), (float)Math.round(height));
            }
            
            @Override
            public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                this.updateEdge(x, y);
                return Window.this.isModal;
            }
            
            @Override
            public boolean scrolled(final InputEvent event, final float x, final float y, final int amount) {
                return Window.this.isModal;
            }
            
            @Override
            public boolean keyDown(final InputEvent event, final int keycode) {
                return Window.this.isModal;
            }
            
            @Override
            public boolean keyUp(final InputEvent event, final int keycode) {
                return Window.this.isModal;
            }
            
            @Override
            public boolean keyTyped(final InputEvent event, final char character) {
                return Window.this.isModal;
            }
        });
    }
    
    public void setStyle(final WindowStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.setBackground(style.background);
        this.titleLabel.setStyle(new Label.LabelStyle(style.titleFont, style.titleFontColor));
        this.invalidateHierarchy();
    }
    
    public WindowStyle getStyle() {
        return this.style;
    }
    
    public void keepWithinStage() {
        if (!this.keepWithinStage) {
            return;
        }
        final Stage stage = this.getStage();
        if (stage == null) {
            return;
        }
        final Camera camera = stage.getCamera();
        if (camera instanceof OrthographicCamera) {
            final OrthographicCamera orthographicCamera = (OrthographicCamera)camera;
            final float parentWidth = stage.getWidth();
            final float parentHeight = stage.getHeight();
            if (this.getX(16) - camera.position.x > parentWidth / 2.0f / orthographicCamera.zoom) {
                this.setPosition(camera.position.x + parentWidth / 2.0f / orthographicCamera.zoom, this.getY(16), 16);
            }
            if (this.getX(8) - camera.position.x < -parentWidth / 2.0f / orthographicCamera.zoom) {
                this.setPosition(camera.position.x - parentWidth / 2.0f / orthographicCamera.zoom, this.getY(8), 8);
            }
            if (this.getY(2) - camera.position.y > parentHeight / 2.0f / orthographicCamera.zoom) {
                this.setPosition(this.getX(2), camera.position.y + parentHeight / 2.0f / orthographicCamera.zoom, 2);
            }
            if (this.getY(4) - camera.position.y < -parentHeight / 2.0f / orthographicCamera.zoom) {
                this.setPosition(this.getX(4), camera.position.y - parentHeight / 2.0f / orthographicCamera.zoom, 4);
            }
        }
        else if (this.getParent() == stage.getRoot()) {
            final float parentWidth2 = stage.getWidth();
            final float parentHeight2 = stage.getHeight();
            if (this.getX() < 0.0f) {
                this.setX(0.0f);
            }
            if (this.getRight() > parentWidth2) {
                this.setX(parentWidth2 - this.getWidth());
            }
            if (this.getY() < 0.0f) {
                this.setY(0.0f);
            }
            if (this.getTop() > parentHeight2) {
                this.setY(parentHeight2 - this.getHeight());
            }
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final Stage stage = this.getStage();
        if (stage.getKeyboardFocus() == null) {
            stage.setKeyboardFocus(this);
        }
        this.keepWithinStage();
        if (this.style.stageBackground != null) {
            this.stageToLocalCoordinates(Window.tmpPosition.set(0.0f, 0.0f));
            this.stageToLocalCoordinates(Window.tmpSize.set(stage.getWidth(), stage.getHeight()));
            this.drawStageBackground(batch, parentAlpha, this.getX() + Window.tmpPosition.x, this.getY() + Window.tmpPosition.y, this.getX() + Window.tmpSize.x, this.getY() + Window.tmpSize.y);
        }
        super.draw(batch, parentAlpha);
    }
    
    protected void drawStageBackground(final Batch batch, final float parentAlpha, final float x, final float y, final float width, final float height) {
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.style.stageBackground.draw(batch, x, y, width, height);
    }
    
    @Override
    protected void drawBackground(final Batch batch, final float parentAlpha, final float x, final float y) {
        super.drawBackground(batch, parentAlpha, x, y);
        this.titleTable.getColor().a = this.getColor().a;
        final float padTop = this.getPadTop();
        final float padLeft = this.getPadLeft();
        this.titleTable.setSize(this.getWidth() - padLeft - this.getPadRight(), padTop);
        this.titleTable.setPosition(padLeft, this.getHeight() - padTop);
        this.drawTitleTable = true;
        this.titleTable.draw(batch, parentAlpha);
        this.drawTitleTable = false;
    }
    
    @Override
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (!this.isVisible()) {
            return null;
        }
        final Actor hit = super.hit(x, y, touchable);
        if (hit == null && this.isModal && (!touchable || this.getTouchable() == Touchable.enabled)) {
            return this;
        }
        final float height = this.getHeight();
        if (hit == null || hit == this) {
            return hit;
        }
        if (y <= height && y >= height - this.getPadTop() && x >= 0.0f && x <= this.getWidth()) {
            Actor current;
            for (current = hit; current.getParent() != this; current = current.getParent()) {}
            if (this.getCell(current) != null) {
                return this;
            }
        }
        return hit;
    }
    
    public boolean isMovable() {
        return this.isMovable;
    }
    
    public void setMovable(final boolean isMovable) {
        this.isMovable = isMovable;
    }
    
    public boolean isModal() {
        return this.isModal;
    }
    
    public void setModal(final boolean isModal) {
        this.isModal = isModal;
    }
    
    public void setKeepWithinStage(final boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }
    
    public boolean isResizable() {
        return this.isResizable;
    }
    
    public void setResizable(final boolean isResizable) {
        this.isResizable = isResizable;
    }
    
    public void setResizeBorder(final int resizeBorder) {
        this.resizeBorder = resizeBorder;
    }
    
    public boolean isDragging() {
        return this.dragging;
    }
    
    @Override
    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), this.titleTable.getPrefWidth() + this.getPadLeft() + this.getPadRight());
    }
    
    public Table getTitleTable() {
        return this.titleTable;
    }
    
    public Label getTitleLabel() {
        return this.titleLabel;
    }
    
    public static class WindowStyle
    {
        public Drawable background;
        public BitmapFont titleFont;
        public Color titleFontColor;
        public Drawable stageBackground;
        
        public WindowStyle() {
            this.titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public WindowStyle(final BitmapFont titleFont, final Color titleFontColor, final Drawable background) {
            this.titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.background = background;
            this.titleFont = titleFont;
            this.titleFontColor.set(titleFontColor);
        }
        
        public WindowStyle(final WindowStyle style) {
            this.titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.background = style.background;
            this.titleFont = style.titleFont;
            this.titleFontColor = new Color(style.titleFontColor);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class Actor
{
    private Stage stage;
    Group parent;
    private final DelayedRemovalArray<EventListener> listeners;
    private final DelayedRemovalArray<EventListener> captureListeners;
    private final Array<Action> actions;
    private String name;
    private Touchable touchable;
    private boolean visible;
    private boolean debug;
    float x;
    float y;
    float width;
    float height;
    float originX;
    float originY;
    float scaleX;
    float scaleY;
    float rotation;
    final Color color;
    private Object userObject;
    
    public Actor() {
        this.listeners = new DelayedRemovalArray<EventListener>(0);
        this.captureListeners = new DelayedRemovalArray<EventListener>(0);
        this.actions = new Array<Action>(0);
        this.touchable = Touchable.enabled;
        this.visible = true;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void draw(final Batch batch, final float parentAlpha) {
    }
    
    public void act(final float delta) {
        final Array<Action> actions = this.actions;
        if (actions.size > 0) {
            if (this.stage != null && this.stage.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
            for (int i = 0; i < actions.size; ++i) {
                final Action action = actions.get(i);
                if (action.act(delta) && i < actions.size) {
                    final Action current = actions.get(i);
                    final int actionIndex = (current == action) ? i : actions.indexOf(action, true);
                    if (actionIndex != -1) {
                        actions.removeIndex(actionIndex);
                        action.setActor(null);
                        --i;
                    }
                }
            }
        }
    }
    
    public boolean fire(final Event event) {
        if (event.getStage() == null) {
            event.setStage(this.getStage());
        }
        event.setTarget(this);
        final Array<Group> ancestors = Pools.obtain((Class<Array<Group>>)Array.class);
        for (Group parent = this.parent; parent != null; parent = parent.parent) {
            ancestors.add(parent);
        }
        try {
            final Object[] ancestorsArray = ancestors.items;
            for (int i = ancestors.size - 1; i >= 0; --i) {
                final Group currentTarget = (Group)ancestorsArray[i];
                currentTarget.notify(event, true);
                if (event.isStopped()) {
                    return event.isCancelled();
                }
            }
            this.notify(event, true);
            if (event.isStopped()) {
                return event.isCancelled();
            }
            this.notify(event, false);
            if (!event.getBubbles()) {
                return event.isCancelled();
            }
            if (event.isStopped()) {
                return event.isCancelled();
            }
            for (int i = 0, n = ancestors.size; i < n; ++i) {
                ((Group)ancestorsArray[i]).notify(event, false);
                if (event.isStopped()) {
                    return event.isCancelled();
                }
            }
            return event.isCancelled();
        }
        finally {
            ancestors.clear();
            Pools.free(ancestors);
        }
    }
    
    public boolean notify(final Event event, final boolean capture) {
        if (event.getTarget() == null) {
            throw new IllegalArgumentException("The event target cannot be null.");
        }
        final DelayedRemovalArray<EventListener> listeners = capture ? this.captureListeners : this.listeners;
        if (listeners.size == 0) {
            return event.isCancelled();
        }
        event.setListenerActor(this);
        event.setCapture(capture);
        if (event.getStage() == null) {
            event.setStage(this.stage);
        }
        listeners.begin();
        for (int i = 0, n = listeners.size; i < n; ++i) {
            final EventListener listener = listeners.get(i);
            if (listener.handle(event)) {
                event.handle();
                if (event instanceof InputEvent) {
                    final InputEvent inputEvent = (InputEvent)event;
                    if (inputEvent.getType() == InputEvent.Type.touchDown) {
                        event.getStage().addTouchFocus(listener, this, inputEvent.getTarget(), inputEvent.getPointer(), inputEvent.getButton());
                    }
                }
            }
        }
        listeners.end();
        return event.isCancelled();
    }
    
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (touchable && this.touchable != Touchable.enabled) {
            return null;
        }
        if (!this.isVisible()) {
            return null;
        }
        return (x >= 0.0f && x < this.width && y >= 0.0f && y < this.height) ? this : null;
    }
    
    public boolean remove() {
        return this.parent != null && this.parent.removeActor(this, true);
    }
    
    public boolean addListener(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null.");
        }
        if (!this.listeners.contains(listener, true)) {
            this.listeners.add(listener);
            return true;
        }
        return false;
    }
    
    public boolean removeListener(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null.");
        }
        return this.listeners.removeValue(listener, true);
    }
    
    public DelayedRemovalArray<EventListener> getListeners() {
        return this.listeners;
    }
    
    public boolean addCaptureListener(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null.");
        }
        if (!this.captureListeners.contains(listener, true)) {
            this.captureListeners.add(listener);
        }
        return true;
    }
    
    public boolean removeCaptureListener(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null.");
        }
        return this.captureListeners.removeValue(listener, true);
    }
    
    public DelayedRemovalArray<EventListener> getCaptureListeners() {
        return this.captureListeners;
    }
    
    public void addAction(final Action action) {
        action.setActor(this);
        this.actions.add(action);
        if (this.stage != null && this.stage.getActionsRequestRendering()) {
            Gdx.graphics.requestRendering();
        }
    }
    
    public void removeAction(final Action action) {
        if (this.actions.removeValue(action, true)) {
            action.setActor(null);
        }
    }
    
    public Array<Action> getActions() {
        return this.actions;
    }
    
    public boolean hasActions() {
        return this.actions.size > 0;
    }
    
    public void clearActions() {
        for (int i = this.actions.size - 1; i >= 0; --i) {
            this.actions.get(i).setActor(null);
        }
        this.actions.clear();
    }
    
    public void clearListeners() {
        this.listeners.clear();
        this.captureListeners.clear();
    }
    
    public void clear() {
        this.clearActions();
        this.clearListeners();
    }
    
    public Stage getStage() {
        return this.stage;
    }
    
    protected void setStage(final Stage stage) {
        this.stage = stage;
    }
    
    public boolean isDescendantOf(final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        Actor parent = this;
        while (parent != actor) {
            parent = parent.parent;
            if (parent == null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAscendantOf(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        while (actor != this) {
            actor = actor.parent;
            if (actor == null) {
                return false;
            }
        }
        return true;
    }
    
    public <T extends Actor> T firstAscendant(final Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        Actor actor = this;
        while (!ClassReflection.isInstance(type, actor)) {
            actor = actor.parent;
            if (actor == null) {
                return null;
            }
        }
        return (T)actor;
    }
    
    public boolean hasParent() {
        return this.parent != null;
    }
    
    public Group getParent() {
        return this.parent;
    }
    
    protected void setParent(final Group parent) {
        this.parent = parent;
    }
    
    public boolean isTouchable() {
        return this.touchable == Touchable.enabled;
    }
    
    public Touchable getTouchable() {
        return this.touchable;
    }
    
    public void setTouchable(final Touchable touchable) {
        this.touchable = touchable;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public boolean ancestorsVisible() {
        Actor actor = this;
        while (actor.isVisible()) {
            actor = actor.parent;
            if (actor == null) {
                return true;
            }
        }
        return false;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getX(final int alignment) {
        float x = this.x;
        if ((alignment & 0x10) != 0x0) {
            x += this.width;
        }
        else if ((alignment & 0x8) == 0x0) {
            x += this.width / 2.0f;
        }
        return x;
    }
    
    public void setX(final float x) {
        if (this.x != x) {
            this.x = x;
            this.positionChanged();
        }
    }
    
    public void setX(float x, final int alignment) {
        if ((alignment & 0x10) != 0x0) {
            x -= this.width;
        }
        else if ((alignment & 0x8) == 0x0) {
            x -= this.width / 2.0f;
        }
        if (this.x != x) {
            this.x = x;
            this.positionChanged();
        }
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        if (this.y != y) {
            this.y = y;
            this.positionChanged();
        }
    }
    
    public void setY(float y, final int alignment) {
        if ((alignment & 0x2) != 0x0) {
            y -= this.height;
        }
        else if ((alignment & 0x4) == 0x0) {
            y -= this.height / 2.0f;
        }
        if (this.y != y) {
            this.y = y;
            this.positionChanged();
        }
    }
    
    public float getY(final int alignment) {
        float y = this.y;
        if ((alignment & 0x2) != 0x0) {
            y += this.height;
        }
        else if ((alignment & 0x4) == 0x0) {
            y += this.height / 2.0f;
        }
        return y;
    }
    
    public void setPosition(final float x, final float y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            this.positionChanged();
        }
    }
    
    public void setPosition(float x, float y, final int alignment) {
        if ((alignment & 0x10) != 0x0) {
            x -= this.width;
        }
        else if ((alignment & 0x8) == 0x0) {
            x -= this.width / 2.0f;
        }
        if ((alignment & 0x2) != 0x0) {
            y -= this.height;
        }
        else if ((alignment & 0x4) == 0x0) {
            y -= this.height / 2.0f;
        }
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            this.positionChanged();
        }
    }
    
    public void moveBy(final float x, final float y) {
        if (x != 0.0f || y != 0.0f) {
            this.x += x;
            this.y += y;
            this.positionChanged();
        }
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        if (this.width != width) {
            this.width = width;
            this.sizeChanged();
        }
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setHeight(final float height) {
        if (this.height != height) {
            this.height = height;
            this.sizeChanged();
        }
    }
    
    public float getTop() {
        return this.y + this.height;
    }
    
    public float getRight() {
        return this.x + this.width;
    }
    
    protected void positionChanged() {
    }
    
    protected void sizeChanged() {
    }
    
    protected void rotationChanged() {
    }
    
    public void setSize(final float width, final float height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            this.sizeChanged();
        }
    }
    
    public void sizeBy(final float size) {
        if (size != 0.0f) {
            this.width += size;
            this.height += size;
            this.sizeChanged();
        }
    }
    
    public void sizeBy(final float width, final float height) {
        if (width != 0.0f || height != 0.0f) {
            this.width += width;
            this.height += height;
            this.sizeChanged();
        }
    }
    
    public void setBounds(final float x, final float y, final float width, final float height) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            this.positionChanged();
        }
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            this.sizeChanged();
        }
    }
    
    public float getOriginX() {
        return this.originX;
    }
    
    public void setOriginX(final float originX) {
        this.originX = originX;
    }
    
    public float getOriginY() {
        return this.originY;
    }
    
    public void setOriginY(final float originY) {
        this.originY = originY;
    }
    
    public void setOrigin(final float originX, final float originY) {
        this.originX = originX;
        this.originY = originY;
    }
    
    public void setOrigin(final int alignment) {
        if ((alignment & 0x8) != 0x0) {
            this.originX = 0.0f;
        }
        else if ((alignment & 0x10) != 0x0) {
            this.originX = this.width;
        }
        else {
            this.originX = this.width / 2.0f;
        }
        if ((alignment & 0x4) != 0x0) {
            this.originY = 0.0f;
        }
        else if ((alignment & 0x2) != 0x0) {
            this.originY = this.height;
        }
        else {
            this.originY = this.height / 2.0f;
        }
    }
    
    public float getScaleX() {
        return this.scaleX;
    }
    
    public void setScaleX(final float scaleX) {
        this.scaleX = scaleX;
    }
    
    public float getScaleY() {
        return this.scaleY;
    }
    
    public void setScaleY(final float scaleY) {
        this.scaleY = scaleY;
    }
    
    public void setScale(final float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
    }
    
    public void setScale(final float scaleX, final float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public void scaleBy(final float scale) {
        this.scaleX += scale;
        this.scaleY += scale;
    }
    
    public void scaleBy(final float scaleX, final float scaleY) {
        this.scaleX += scaleX;
        this.scaleY += scaleY;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final float degrees) {
        if (this.rotation != degrees) {
            this.rotation = degrees;
            this.rotationChanged();
        }
    }
    
    public void rotateBy(final float amountInDegrees) {
        if (amountInDegrees != 0.0f) {
            this.rotation += amountInDegrees;
            this.rotationChanged();
        }
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
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void toFront() {
        this.setZIndex(Integer.MAX_VALUE);
    }
    
    public void toBack() {
        this.setZIndex(0);
    }
    
    public boolean setZIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("ZIndex cannot be < 0.");
        }
        final Group parent = this.parent;
        if (parent == null) {
            return false;
        }
        final Array<Actor> children = parent.children;
        if (children.size == 1) {
            return false;
        }
        index = Math.min(index, children.size - 1);
        if (children.get(index) == this) {
            return false;
        }
        if (!children.removeValue(this, true)) {
            return false;
        }
        children.insert(index, this);
        return true;
    }
    
    public int getZIndex() {
        final Group parent = this.parent;
        if (parent == null) {
            return -1;
        }
        return parent.children.indexOf(this, true);
    }
    
    public boolean clipBegin() {
        return this.clipBegin(this.x, this.y, this.width, this.height);
    }
    
    public boolean clipBegin(final float x, final float y, final float width, final float height) {
        if (width <= 0.0f || height <= 0.0f) {
            return false;
        }
        final Rectangle tableBounds = Rectangle.tmp;
        tableBounds.x = x;
        tableBounds.y = y;
        tableBounds.width = width;
        tableBounds.height = height;
        final Stage stage = this.stage;
        final Rectangle scissorBounds = Pools.obtain(Rectangle.class);
        stage.calculateScissors(tableBounds, scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {
            return true;
        }
        Pools.free(scissorBounds);
        return false;
    }
    
    public void clipEnd() {
        Pools.free(ScissorStack.popScissors());
    }
    
    public Vector2 screenToLocalCoordinates(final Vector2 screenCoords) {
        final Stage stage = this.stage;
        if (stage == null) {
            return screenCoords;
        }
        return this.stageToLocalCoordinates(stage.screenToStageCoordinates(screenCoords));
    }
    
    public Vector2 stageToLocalCoordinates(final Vector2 stageCoords) {
        if (this.parent != null) {
            this.parent.stageToLocalCoordinates(stageCoords);
        }
        this.parentToLocalCoordinates(stageCoords);
        return stageCoords;
    }
    
    public Vector2 parentToLocalCoordinates(final Vector2 parentCoords) {
        final float rotation = this.rotation;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final float childX = this.x;
        final float childY = this.y;
        if (rotation == 0.0f) {
            if (scaleX == 1.0f && scaleY == 1.0f) {
                parentCoords.x -= childX;
                parentCoords.y -= childY;
            }
            else {
                final float originX = this.originX;
                final float originY = this.originY;
                parentCoords.x = (parentCoords.x - childX - originX) / scaleX + originX;
                parentCoords.y = (parentCoords.y - childY - originY) / scaleY + originY;
            }
        }
        else {
            final float cos = (float)Math.cos(rotation * 0.017453292f);
            final float sin = (float)Math.sin(rotation * 0.017453292f);
            final float originX2 = this.originX;
            final float originY2 = this.originY;
            final float tox = parentCoords.x - childX - originX2;
            final float toy = parentCoords.y - childY - originY2;
            parentCoords.x = (tox * cos + toy * sin) / scaleX + originX2;
            parentCoords.y = (tox * -sin + toy * cos) / scaleY + originY2;
        }
        return parentCoords;
    }
    
    public Vector2 localToScreenCoordinates(final Vector2 localCoords) {
        final Stage stage = this.stage;
        if (stage == null) {
            return localCoords;
        }
        return stage.stageToScreenCoordinates(this.localToAscendantCoordinates(null, localCoords));
    }
    
    public Vector2 localToStageCoordinates(final Vector2 localCoords) {
        return this.localToAscendantCoordinates(null, localCoords);
    }
    
    public Vector2 localToParentCoordinates(final Vector2 localCoords) {
        final float rotation = -this.rotation;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final float x = this.x;
        final float y = this.y;
        if (rotation == 0.0f) {
            if (scaleX == 1.0f && scaleY == 1.0f) {
                localCoords.x += x;
                localCoords.y += y;
            }
            else {
                final float originX = this.originX;
                final float originY = this.originY;
                localCoords.x = (localCoords.x - originX) * scaleX + originX + x;
                localCoords.y = (localCoords.y - originY) * scaleY + originY + y;
            }
        }
        else {
            final float cos = (float)Math.cos(rotation * 0.017453292f);
            final float sin = (float)Math.sin(rotation * 0.017453292f);
            final float originX2 = this.originX;
            final float originY2 = this.originY;
            final float tox = (localCoords.x - originX2) * scaleX;
            final float toy = (localCoords.y - originY2) * scaleY;
            localCoords.x = tox * cos + toy * sin + originX2 + x;
            localCoords.y = tox * -sin + toy * cos + originY2 + y;
        }
        return localCoords;
    }
    
    public Vector2 localToAscendantCoordinates(final Actor ascendant, final Vector2 localCoords) {
        Actor actor = this;
        do {
            actor.localToParentCoordinates(localCoords);
            actor = actor.parent;
            if (actor == ascendant) {
                break;
            }
        } while (actor != null);
        return localCoords;
    }
    
    public Vector2 localToActorCoordinates(final Actor actor, final Vector2 localCoords) {
        this.localToStageCoordinates(localCoords);
        return actor.stageToLocalCoordinates(localCoords);
    }
    
    public void drawDebug(final ShapeRenderer shapes) {
        this.drawDebugBounds(shapes);
    }
    
    protected void drawDebugBounds(final ShapeRenderer shapes) {
        if (!this.debug) {
            return;
        }
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(this.stage.getDebugColor());
        shapes.rect(this.x, this.y, this.originX, this.originY, this.width, this.height, this.scaleX, this.scaleY, this.rotation);
    }
    
    public void setDebug(final boolean enabled) {
        this.debug = enabled;
        if (enabled) {
            Stage.debug = true;
        }
    }
    
    public boolean getDebug() {
        return this.debug;
    }
    
    public Actor debug() {
        this.setDebug(true);
        return this;
    }
    
    @Override
    public String toString() {
        String name = this.name;
        if (name == null) {
            name = this.getClass().getName();
            final int dotIndex = name.lastIndexOf(46);
            if (dotIndex != -1) {
                name = name.substring(dotIndex + 1);
            }
        }
        return name;
    }
}

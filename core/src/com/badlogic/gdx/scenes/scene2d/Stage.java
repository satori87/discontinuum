// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.InputAdapter;

public class Stage extends InputAdapter implements Disposable
{
    static boolean debug;
    private Viewport viewport;
    private final Batch batch;
    private boolean ownsBatch;
    private Group root;
    private final Vector2 tempCoords;
    private final Actor[] pointerOverActors;
    private final boolean[] pointerTouched;
    private final int[] pointerScreenX;
    private final int[] pointerScreenY;
    private int mouseScreenX;
    private int mouseScreenY;
    private Actor mouseOverActor;
    private Actor keyboardFocus;
    private Actor scrollFocus;
    private final SnapshotArray<TouchFocus> touchFocuses;
    private boolean actionsRequestRendering;
    private ShapeRenderer debugShapes;
    private boolean debugInvisible;
    private boolean debugAll;
    private boolean debugUnderMouse;
    private boolean debugParentUnderMouse;
    private Table.Debug debugTableUnderMouse;
    private final Color debugColor;
    
    public Stage() {
        this(new ScalingViewport(Scaling.stretch, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight(), new OrthographicCamera()), new SpriteBatch());
        this.ownsBatch = true;
    }
    
    public Stage(final Viewport viewport) {
        this(viewport, new SpriteBatch());
        this.ownsBatch = true;
    }
    
    public Stage(final Viewport viewport, final Batch batch) {
        this.tempCoords = new Vector2();
        this.pointerOverActors = new Actor[20];
        this.pointerTouched = new boolean[20];
        this.pointerScreenX = new int[20];
        this.pointerScreenY = new int[20];
        this.touchFocuses = new SnapshotArray<TouchFocus>(true, 4, TouchFocus.class);
        this.actionsRequestRendering = true;
        this.debugTableUnderMouse = Table.Debug.none;
        this.debugColor = new Color(0.0f, 1.0f, 0.0f, 0.85f);
        if (viewport == null) {
            throw new IllegalArgumentException("viewport cannot be null.");
        }
        if (batch == null) {
            throw new IllegalArgumentException("batch cannot be null.");
        }
        this.viewport = viewport;
        this.batch = batch;
        (this.root = new Group()).setStage(this);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    
    public void draw() {
        final Camera camera = this.viewport.getCamera();
        camera.update();
        if (!this.root.isVisible()) {
            return;
        }
        final Batch batch = this.batch;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        this.root.draw(batch, 1.0f);
        batch.end();
        if (Stage.debug) {
            this.drawDebug();
        }
    }
    
    private void drawDebug() {
        if (this.debugShapes == null) {
            (this.debugShapes = new ShapeRenderer()).setAutoShapeType(true);
        }
        if (this.debugUnderMouse || this.debugParentUnderMouse || this.debugTableUnderMouse != Table.Debug.none) {
            this.screenToStageCoordinates(this.tempCoords.set((float)Gdx.input.getX(), (float)Gdx.input.getY()));
            Actor actor = this.hit(this.tempCoords.x, this.tempCoords.y, true);
            if (actor == null) {
                return;
            }
            if (this.debugParentUnderMouse && actor.parent != null) {
                actor = actor.parent;
            }
            if (this.debugTableUnderMouse == Table.Debug.none) {
                actor.setDebug(true);
            }
            else {
                while (actor != null && !(actor instanceof Table)) {
                    actor = actor.parent;
                }
                if (actor == null) {
                    return;
                }
                ((Table)actor).debug(this.debugTableUnderMouse);
            }
            if (this.debugAll && actor instanceof Group) {
                ((Group)actor).debugAll();
            }
            this.disableDebug(this.root, actor);
        }
        else if (this.debugAll) {
            this.root.debugAll();
        }
        Gdx.gl.glEnable(3042);
        this.debugShapes.setProjectionMatrix(this.viewport.getCamera().combined);
        this.debugShapes.begin();
        this.root.drawDebug(this.debugShapes);
        this.debugShapes.end();
    }
    
    private void disableDebug(final Actor actor, final Actor except) {
        if (actor == except) {
            return;
        }
        actor.setDebug(false);
        if (actor instanceof Group) {
            final SnapshotArray<Actor> children = ((Group)actor).children;
            for (int i = 0, n = children.size; i < n; ++i) {
                this.disableDebug(children.get(i), except);
            }
        }
    }
    
    public void act() {
        this.act(Math.min(Gdx.graphics.getDeltaTime(), 0.033333335f));
    }
    
    public void act(final float delta) {
        for (int pointer = 0, n = this.pointerOverActors.length; pointer < n; ++pointer) {
            final Actor overLast = this.pointerOverActors[pointer];
            if (!this.pointerTouched[pointer]) {
                if (overLast != null) {
                    this.pointerOverActors[pointer] = null;
                    this.screenToStageCoordinates(this.tempCoords.set((float)this.pointerScreenX[pointer], (float)this.pointerScreenY[pointer]));
                    final InputEvent event = Pools.obtain(InputEvent.class);
                    event.setType(InputEvent.Type.exit);
                    event.setStage(this);
                    event.setStageX(this.tempCoords.x);
                    event.setStageY(this.tempCoords.y);
                    event.setRelatedActor(overLast);
                    event.setPointer(pointer);
                    overLast.fire(event);
                    Pools.free(event);
                }
            }
            else {
                this.pointerOverActors[pointer] = this.fireEnterAndExit(overLast, this.pointerScreenX[pointer], this.pointerScreenY[pointer], pointer);
            }
        }
        final Application.ApplicationType type = Gdx.app.getType();
        if (type == Application.ApplicationType.Desktop || type == Application.ApplicationType.Applet || type == Application.ApplicationType.WebGL) {
            this.mouseOverActor = this.fireEnterAndExit(this.mouseOverActor, this.mouseScreenX, this.mouseScreenY, -1);
        }
        this.root.act(delta);
    }
    
    private Actor fireEnterAndExit(final Actor overLast, final int screenX, final int screenY, final int pointer) {
        this.screenToStageCoordinates(this.tempCoords.set((float)screenX, (float)screenY));
        final Actor over = this.hit(this.tempCoords.x, this.tempCoords.y, true);
        if (over == overLast) {
            return overLast;
        }
        if (overLast != null) {
            final InputEvent event = Pools.obtain(InputEvent.class);
            event.setStage(this);
            event.setStageX(this.tempCoords.x);
            event.setStageY(this.tempCoords.y);
            event.setPointer(pointer);
            event.setType(InputEvent.Type.exit);
            event.setRelatedActor(over);
            overLast.fire(event);
            Pools.free(event);
        }
        if (over != null) {
            final InputEvent event = Pools.obtain(InputEvent.class);
            event.setStage(this);
            event.setStageX(this.tempCoords.x);
            event.setStageY(this.tempCoords.y);
            event.setPointer(pointer);
            event.setType(InputEvent.Type.enter);
            event.setRelatedActor(overLast);
            over.fire(event);
            Pools.free(event);
        }
        return over;
    }
    
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        if (!this.isInsideViewport(screenX, screenY)) {
            return false;
        }
        this.pointerTouched[pointer] = true;
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        this.screenToStageCoordinates(this.tempCoords.set((float)screenX, (float)screenY));
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchDown);
        event.setStage(this);
        event.setStageX(this.tempCoords.x);
        event.setStageY(this.tempCoords.y);
        event.setPointer(pointer);
        event.setButton(button);
        final Actor target = this.hit(this.tempCoords.x, this.tempCoords.y, true);
        if (target == null) {
            if (this.root.getTouchable() == Touchable.enabled) {
                this.root.fire(event);
            }
        }
        else {
            target.fire(event);
        }
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        this.mouseScreenX = screenX;
        this.mouseScreenY = screenY;
        if (this.touchFocuses.size == 0) {
            return false;
        }
        this.screenToStageCoordinates(this.tempCoords.set((float)screenX, (float)screenY));
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchDragged);
        event.setStage(this);
        event.setStageX(this.tempCoords.x);
        event.setStageY(this.tempCoords.y);
        event.setPointer(pointer);
        final SnapshotArray<TouchFocus> touchFocuses = this.touchFocuses;
        final TouchFocus[] focuses = touchFocuses.begin();
        for (int i = 0, n = touchFocuses.size; i < n; ++i) {
            final TouchFocus focus = focuses[i];
            if (focus.pointer == pointer) {
                if (touchFocuses.contains(focus, true)) {
                    event.setTarget(focus.target);
                    event.setListenerActor(focus.listenerActor);
                    if (focus.listener.handle(event)) {
                        event.handle();
                    }
                }
            }
        }
        touchFocuses.end();
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        this.pointerTouched[pointer] = false;
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        if (this.touchFocuses.size == 0) {
            return false;
        }
        this.screenToStageCoordinates(this.tempCoords.set((float)screenX, (float)screenY));
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchUp);
        event.setStage(this);
        event.setStageX(this.tempCoords.x);
        event.setStageY(this.tempCoords.y);
        event.setPointer(pointer);
        event.setButton(button);
        final SnapshotArray<TouchFocus> touchFocuses = this.touchFocuses;
        final TouchFocus[] focuses = touchFocuses.begin();
        for (int i = 0, n = touchFocuses.size; i < n; ++i) {
            final TouchFocus focus = focuses[i];
            if (focus.pointer == pointer) {
                if (focus.button == button) {
                    if (touchFocuses.removeValue(focus, true)) {
                        event.setTarget(focus.target);
                        event.setListenerActor(focus.listenerActor);
                        if (focus.listener.handle(event)) {
                            event.handle();
                        }
                        Pools.free(focus);
                    }
                }
            }
        }
        touchFocuses.end();
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        this.mouseScreenX = screenX;
        this.mouseScreenY = screenY;
        if (!this.isInsideViewport(screenX, screenY)) {
            return false;
        }
        this.screenToStageCoordinates(this.tempCoords.set((float)screenX, (float)screenY));
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.mouseMoved);
        event.setStageX(this.tempCoords.x);
        event.setStageY(this.tempCoords.y);
        Actor target = this.hit(this.tempCoords.x, this.tempCoords.y, true);
        if (target == null) {
            target = this.root;
        }
        target.fire(event);
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean scrolled(final int amount) {
        final Actor target = (this.scrollFocus == null) ? this.root : this.scrollFocus;
        this.screenToStageCoordinates(this.tempCoords.set((float)this.mouseScreenX, (float)this.mouseScreenY));
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.scrolled);
        event.setScrollAmount(amount);
        event.setStageX(this.tempCoords.x);
        event.setStageY(this.tempCoords.y);
        target.fire(event);
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean keyDown(final int keyCode) {
        final Actor target = (this.keyboardFocus == null) ? this.root : this.keyboardFocus;
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyDown);
        event.setKeyCode(keyCode);
        target.fire(event);
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean keyUp(final int keyCode) {
        final Actor target = (this.keyboardFocus == null) ? this.root : this.keyboardFocus;
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyUp);
        event.setKeyCode(keyCode);
        target.fire(event);
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    @Override
    public boolean keyTyped(final char character) {
        final Actor target = (this.keyboardFocus == null) ? this.root : this.keyboardFocus;
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyTyped);
        event.setCharacter(character);
        target.fire(event);
        final boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }
    
    public void addTouchFocus(final EventListener listener, final Actor listenerActor, final Actor target, final int pointer, final int button) {
        final TouchFocus focus = Pools.obtain(TouchFocus.class);
        focus.listenerActor = listenerActor;
        focus.target = target;
        focus.listener = listener;
        focus.pointer = pointer;
        focus.button = button;
        this.touchFocuses.add(focus);
    }
    
    public void removeTouchFocus(final EventListener listener, final Actor listenerActor, final Actor target, final int pointer, final int button) {
        final SnapshotArray<TouchFocus> touchFocuses = this.touchFocuses;
        for (int i = touchFocuses.size - 1; i >= 0; --i) {
            final TouchFocus focus = touchFocuses.get(i);
            if (focus.listener == listener && focus.listenerActor == listenerActor && focus.target == target && focus.pointer == pointer && focus.button == button) {
                touchFocuses.removeIndex(i);
                Pools.free(focus);
            }
        }
    }
    
    public void cancelTouchFocus(final Actor actor) {
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.touchUp);
        event.setStageX(-2.14748365E9f);
        event.setStageY(-2.14748365E9f);
        final SnapshotArray<TouchFocus> touchFocuses = this.touchFocuses;
        final TouchFocus[] items = touchFocuses.begin();
        for (int i = 0, n = touchFocuses.size; i < n; ++i) {
            final TouchFocus focus = items[i];
            if (focus.listenerActor == actor) {
                if (touchFocuses.removeValue(focus, true)) {
                    event.setTarget(focus.target);
                    event.setListenerActor(focus.listenerActor);
                    event.setPointer(focus.pointer);
                    event.setButton(focus.button);
                    focus.listener.handle(event);
                }
            }
        }
        touchFocuses.end();
        Pools.free(event);
    }
    
    public void cancelTouchFocus() {
        this.cancelTouchFocusExcept(null, null);
    }
    
    public void cancelTouchFocusExcept(final EventListener exceptListener, final Actor exceptActor) {
        final InputEvent event = Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.touchUp);
        event.setStageX(-2.14748365E9f);
        event.setStageY(-2.14748365E9f);
        final SnapshotArray<TouchFocus> touchFocuses = this.touchFocuses;
        final TouchFocus[] items = touchFocuses.begin();
        for (int i = 0, n = touchFocuses.size; i < n; ++i) {
            final TouchFocus focus = items[i];
            if (focus.listener != exceptListener || focus.listenerActor != exceptActor) {
                if (touchFocuses.removeValue(focus, true)) {
                    event.setTarget(focus.target);
                    event.setListenerActor(focus.listenerActor);
                    event.setPointer(focus.pointer);
                    event.setButton(focus.button);
                    focus.listener.handle(event);
                }
            }
        }
        touchFocuses.end();
        Pools.free(event);
    }
    
    public void addActor(final Actor actor) {
        this.root.addActor(actor);
    }
    
    public void addAction(final Action action) {
        this.root.addAction(action);
    }
    
    public Array<Actor> getActors() {
        return this.root.children;
    }
    
    public boolean addListener(final EventListener listener) {
        return this.root.addListener(listener);
    }
    
    public boolean removeListener(final EventListener listener) {
        return this.root.removeListener(listener);
    }
    
    public boolean addCaptureListener(final EventListener listener) {
        return this.root.addCaptureListener(listener);
    }
    
    public boolean removeCaptureListener(final EventListener listener) {
        return this.root.removeCaptureListener(listener);
    }
    
    public void clear() {
        this.unfocusAll();
        this.root.clear();
    }
    
    public void unfocusAll() {
        this.setScrollFocus(null);
        this.setKeyboardFocus(null);
        this.cancelTouchFocus();
    }
    
    public void unfocus(final Actor actor) {
        this.cancelTouchFocus(actor);
        if (this.scrollFocus != null && this.scrollFocus.isDescendantOf(actor)) {
            this.setScrollFocus(null);
        }
        if (this.keyboardFocus != null && this.keyboardFocus.isDescendantOf(actor)) {
            this.setKeyboardFocus(null);
        }
    }
    
    public boolean setKeyboardFocus(final Actor actor) {
        if (this.keyboardFocus == actor) {
            return true;
        }
        final FocusListener.FocusEvent event = Pools.obtain(FocusListener.FocusEvent.class);
        event.setStage(this);
        event.setType(FocusListener.FocusEvent.Type.keyboard);
        final Actor oldKeyboardFocus = this.keyboardFocus;
        if (oldKeyboardFocus != null) {
            event.setFocused(false);
            event.setRelatedActor(actor);
            oldKeyboardFocus.fire(event);
        }
        boolean success = !event.isCancelled();
        if (success && (this.keyboardFocus = actor) != null) {
            event.setFocused(true);
            event.setRelatedActor(oldKeyboardFocus);
            actor.fire(event);
            success = !event.isCancelled();
            if (!success) {
                this.keyboardFocus = oldKeyboardFocus;
            }
        }
        Pools.free(event);
        return success;
    }
    
    public Actor getKeyboardFocus() {
        return this.keyboardFocus;
    }
    
    public boolean setScrollFocus(final Actor actor) {
        if (this.scrollFocus == actor) {
            return true;
        }
        final FocusListener.FocusEvent event = Pools.obtain(FocusListener.FocusEvent.class);
        event.setStage(this);
        event.setType(FocusListener.FocusEvent.Type.scroll);
        final Actor oldScrollFocus = this.scrollFocus;
        if (oldScrollFocus != null) {
            event.setFocused(false);
            event.setRelatedActor(actor);
            oldScrollFocus.fire(event);
        }
        boolean success = !event.isCancelled();
        if (success && (this.scrollFocus = actor) != null) {
            event.setFocused(true);
            event.setRelatedActor(oldScrollFocus);
            actor.fire(event);
            success = !event.isCancelled();
            if (!success) {
                this.scrollFocus = oldScrollFocus;
            }
        }
        Pools.free(event);
        return success;
    }
    
    public Actor getScrollFocus() {
        return this.scrollFocus;
    }
    
    public Batch getBatch() {
        return this.batch;
    }
    
    public Viewport getViewport() {
        return this.viewport;
    }
    
    public void setViewport(final Viewport viewport) {
        this.viewport = viewport;
    }
    
    public float getWidth() {
        return this.viewport.getWorldWidth();
    }
    
    public float getHeight() {
        return this.viewport.getWorldHeight();
    }
    
    public Camera getCamera() {
        return this.viewport.getCamera();
    }
    
    public Group getRoot() {
        return this.root;
    }
    
    public void setRoot(final Group root) {
        this.root = root;
    }
    
    public Actor hit(final float stageX, final float stageY, final boolean touchable) {
        this.root.parentToLocalCoordinates(this.tempCoords.set(stageX, stageY));
        return this.root.hit(this.tempCoords.x, this.tempCoords.y, touchable);
    }
    
    public Vector2 screenToStageCoordinates(final Vector2 screenCoords) {
        this.viewport.unproject(screenCoords);
        return screenCoords;
    }
    
    public Vector2 stageToScreenCoordinates(final Vector2 stageCoords) {
        this.viewport.project(stageCoords);
        stageCoords.y = this.viewport.getScreenHeight() - stageCoords.y;
        return stageCoords;
    }
    
    public Vector2 toScreenCoordinates(final Vector2 coords, final Matrix4 transformMatrix) {
        return this.viewport.toScreenCoordinates(coords, transformMatrix);
    }
    
    public void calculateScissors(final Rectangle localRect, final Rectangle scissorRect) {
        this.viewport.calculateScissors(this.batch.getTransformMatrix(), localRect, scissorRect);
        Matrix4 transformMatrix;
        if (this.debugShapes != null && this.debugShapes.isDrawing()) {
            transformMatrix = this.debugShapes.getTransformMatrix();
        }
        else {
            transformMatrix = this.batch.getTransformMatrix();
        }
        this.viewport.calculateScissors(transformMatrix, localRect, scissorRect);
    }
    
    public void setActionsRequestRendering(final boolean actionsRequestRendering) {
        this.actionsRequestRendering = actionsRequestRendering;
    }
    
    public boolean getActionsRequestRendering() {
        return this.actionsRequestRendering;
    }
    
    public Color getDebugColor() {
        return this.debugColor;
    }
    
    public void setDebugInvisible(final boolean debugInvisible) {
        this.debugInvisible = debugInvisible;
    }
    
    public void setDebugAll(final boolean debugAll) {
        if (this.debugAll == debugAll) {
            return;
        }
        this.debugAll = debugAll;
        if (debugAll) {
            Stage.debug = true;
        }
        else {
            this.root.setDebug(false, true);
        }
    }
    
    public boolean isDebugAll() {
        return this.debugAll;
    }
    
    public void setDebugUnderMouse(final boolean debugUnderMouse) {
        if (this.debugUnderMouse == debugUnderMouse) {
            return;
        }
        this.debugUnderMouse = debugUnderMouse;
        if (debugUnderMouse) {
            Stage.debug = true;
        }
        else {
            this.root.setDebug(false, true);
        }
    }
    
    public void setDebugParentUnderMouse(final boolean debugParentUnderMouse) {
        if (this.debugParentUnderMouse == debugParentUnderMouse) {
            return;
        }
        this.debugParentUnderMouse = debugParentUnderMouse;
        if (debugParentUnderMouse) {
            Stage.debug = true;
        }
        else {
            this.root.setDebug(false, true);
        }
    }
    
    public void setDebugTableUnderMouse(Table.Debug debugTableUnderMouse) {
        if (debugTableUnderMouse == null) {
            debugTableUnderMouse = Table.Debug.none;
        }
        if (this.debugTableUnderMouse == debugTableUnderMouse) {
            return;
        }
        if ((this.debugTableUnderMouse = debugTableUnderMouse) != Table.Debug.none) {
            Stage.debug = true;
        }
        else {
            this.root.setDebug(false, true);
        }
    }
    
    public void setDebugTableUnderMouse(final boolean debugTableUnderMouse) {
        this.setDebugTableUnderMouse(debugTableUnderMouse ? Table.Debug.all : Table.Debug.none);
    }
    
    @Override
    public void dispose() {
        this.clear();
        if (this.ownsBatch) {
            this.batch.dispose();
        }
    }
    
    protected boolean isInsideViewport(final int screenX, int screenY) {
        final int x0 = this.viewport.getScreenX();
        final int x2 = x0 + this.viewport.getScreenWidth();
        final int y0 = this.viewport.getScreenY();
        final int y2 = y0 + this.viewport.getScreenHeight();
        screenY = Gdx.graphics.getHeight() - 1 - screenY;
        return screenX >= x0 && screenX < x2 && screenY >= y0 && screenY < y2;
    }
    
    public static final class TouchFocus implements Pool.Poolable
    {
        EventListener listener;
        Actor listenerActor;
        Actor target;
        int pointer;
        int button;
        
        @Override
        public void reset() {
            this.listenerActor = null;
            this.listener = null;
            this.target = null;
        }
    }
}

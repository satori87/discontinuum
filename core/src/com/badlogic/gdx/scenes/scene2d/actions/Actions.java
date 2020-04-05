// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.scenes.scene2d.Action;

public class Actions
{
    public static <T extends Action> T action(final Class<T> type) {
        final Pool<T> pool = Pools.get(type);
        final T action = pool.obtain();
        action.setPool(pool);
        return action;
    }
    
    public static AddAction addAction(final Action action) {
        final AddAction addAction = action(AddAction.class);
        addAction.setAction(action);
        return addAction;
    }
    
    public static AddAction addAction(final Action action, final Actor targetActor) {
        final AddAction addAction = action(AddAction.class);
        addAction.setTarget(targetActor);
        addAction.setAction(action);
        return addAction;
    }
    
    public static RemoveAction removeAction(final Action action) {
        final RemoveAction removeAction = action(RemoveAction.class);
        removeAction.setAction(action);
        return removeAction;
    }
    
    public static RemoveAction removeAction(final Action action, final Actor targetActor) {
        final RemoveAction removeAction = action(RemoveAction.class);
        removeAction.setTarget(targetActor);
        removeAction.setAction(action);
        return removeAction;
    }
    
    public static MoveToAction moveTo(final float x, final float y) {
        return moveTo(x, y, 0.0f, null);
    }
    
    public static MoveToAction moveTo(final float x, final float y, final float duration) {
        return moveTo(x, y, duration, null);
    }
    
    public static MoveToAction moveTo(final float x, final float y, final float duration, final Interpolation interpolation) {
        final MoveToAction action = action(MoveToAction.class);
        action.setPosition(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static MoveToAction moveToAligned(final float x, final float y, final int alignment) {
        return moveToAligned(x, y, alignment, 0.0f, null);
    }
    
    public static MoveToAction moveToAligned(final float x, final float y, final int alignment, final float duration) {
        return moveToAligned(x, y, alignment, duration, null);
    }
    
    public static MoveToAction moveToAligned(final float x, final float y, final int alignment, final float duration, final Interpolation interpolation) {
        final MoveToAction action = action(MoveToAction.class);
        action.setPosition(x, y, alignment);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static MoveByAction moveBy(final float amountX, final float amountY) {
        return moveBy(amountX, amountY, 0.0f, null);
    }
    
    public static MoveByAction moveBy(final float amountX, final float amountY, final float duration) {
        return moveBy(amountX, amountY, duration, null);
    }
    
    public static MoveByAction moveBy(final float amountX, final float amountY, final float duration, final Interpolation interpolation) {
        final MoveByAction action = action(MoveByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static SizeToAction sizeTo(final float x, final float y) {
        return sizeTo(x, y, 0.0f, null);
    }
    
    public static SizeToAction sizeTo(final float x, final float y, final float duration) {
        return sizeTo(x, y, duration, null);
    }
    
    public static SizeToAction sizeTo(final float x, final float y, final float duration, final Interpolation interpolation) {
        final SizeToAction action = action(SizeToAction.class);
        action.setSize(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static SizeByAction sizeBy(final float amountX, final float amountY) {
        return sizeBy(amountX, amountY, 0.0f, null);
    }
    
    public static SizeByAction sizeBy(final float amountX, final float amountY, final float duration) {
        return sizeBy(amountX, amountY, duration, null);
    }
    
    public static SizeByAction sizeBy(final float amountX, final float amountY, final float duration, final Interpolation interpolation) {
        final SizeByAction action = action(SizeByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static ScaleToAction scaleTo(final float x, final float y) {
        return scaleTo(x, y, 0.0f, null);
    }
    
    public static ScaleToAction scaleTo(final float x, final float y, final float duration) {
        return scaleTo(x, y, duration, null);
    }
    
    public static ScaleToAction scaleTo(final float x, final float y, final float duration, final Interpolation interpolation) {
        final ScaleToAction action = action(ScaleToAction.class);
        action.setScale(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static ScaleByAction scaleBy(final float amountX, final float amountY) {
        return scaleBy(amountX, amountY, 0.0f, null);
    }
    
    public static ScaleByAction scaleBy(final float amountX, final float amountY, final float duration) {
        return scaleBy(amountX, amountY, duration, null);
    }
    
    public static ScaleByAction scaleBy(final float amountX, final float amountY, final float duration, final Interpolation interpolation) {
        final ScaleByAction action = action(ScaleByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static RotateToAction rotateTo(final float rotation) {
        return rotateTo(rotation, 0.0f, null);
    }
    
    public static RotateToAction rotateTo(final float rotation, final float duration) {
        return rotateTo(rotation, duration, null);
    }
    
    public static RotateToAction rotateTo(final float rotation, final float duration, final Interpolation interpolation) {
        final RotateToAction action = action(RotateToAction.class);
        action.setRotation(rotation);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static RotateByAction rotateBy(final float rotationAmount) {
        return rotateBy(rotationAmount, 0.0f, null);
    }
    
    public static RotateByAction rotateBy(final float rotationAmount, final float duration) {
        return rotateBy(rotationAmount, duration, null);
    }
    
    public static RotateByAction rotateBy(final float rotationAmount, final float duration, final Interpolation interpolation) {
        final RotateByAction action = action(RotateByAction.class);
        action.setAmount(rotationAmount);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static ColorAction color(final Color color) {
        return color(color, 0.0f, null);
    }
    
    public static ColorAction color(final Color color, final float duration) {
        return color(color, duration, null);
    }
    
    public static ColorAction color(final Color color, final float duration, final Interpolation interpolation) {
        final ColorAction action = action(ColorAction.class);
        action.setEndColor(color);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static AlphaAction alpha(final float a) {
        return alpha(a, 0.0f, null);
    }
    
    public static AlphaAction alpha(final float a, final float duration) {
        return alpha(a, duration, null);
    }
    
    public static AlphaAction alpha(final float a, final float duration, final Interpolation interpolation) {
        final AlphaAction action = action(AlphaAction.class);
        action.setAlpha(a);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static AlphaAction fadeOut(final float duration) {
        return alpha(0.0f, duration, null);
    }
    
    public static AlphaAction fadeOut(final float duration, final Interpolation interpolation) {
        final AlphaAction action = action(AlphaAction.class);
        action.setAlpha(0.0f);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static AlphaAction fadeIn(final float duration) {
        return alpha(1.0f, duration, null);
    }
    
    public static AlphaAction fadeIn(final float duration, final Interpolation interpolation) {
        final AlphaAction action = action(AlphaAction.class);
        action.setAlpha(1.0f);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
    
    public static VisibleAction show() {
        return visible(true);
    }
    
    public static VisibleAction hide() {
        return visible(false);
    }
    
    public static VisibleAction visible(final boolean visible) {
        final VisibleAction action = action(VisibleAction.class);
        action.setVisible(visible);
        return action;
    }
    
    public static TouchableAction touchable(final Touchable touchable) {
        final TouchableAction action = action(TouchableAction.class);
        action.setTouchable(touchable);
        return action;
    }
    
    public static RemoveActorAction removeActor() {
        return action(RemoveActorAction.class);
    }
    
    public static RemoveActorAction removeActor(final Actor removeActor) {
        final RemoveActorAction action = action(RemoveActorAction.class);
        action.setTarget(removeActor);
        return action;
    }
    
    public static DelayAction delay(final float duration) {
        final DelayAction action = action(DelayAction.class);
        action.setDuration(duration);
        return action;
    }
    
    public static DelayAction delay(final float duration, final Action delayedAction) {
        final DelayAction action = action(DelayAction.class);
        action.setDuration(duration);
        action.setAction(delayedAction);
        return action;
    }
    
    public static TimeScaleAction timeScale(final float scale, final Action scaledAction) {
        final TimeScaleAction action = action(TimeScaleAction.class);
        action.setScale(scale);
        action.setAction(scaledAction);
        return action;
    }
    
    public static SequenceAction sequence(final Action action1) {
        final SequenceAction action2 = action(SequenceAction.class);
        action2.addAction(action1);
        return action2;
    }
    
    public static SequenceAction sequence(final Action action1, final Action action2) {
        final SequenceAction action3 = action(SequenceAction.class);
        action3.addAction(action1);
        action3.addAction(action2);
        return action3;
    }
    
    public static SequenceAction sequence(final Action action1, final Action action2, final Action action3) {
        final SequenceAction action4 = action(SequenceAction.class);
        action4.addAction(action1);
        action4.addAction(action2);
        action4.addAction(action3);
        return action4;
    }
    
    public static SequenceAction sequence(final Action action1, final Action action2, final Action action3, final Action action4) {
        final SequenceAction action5 = action(SequenceAction.class);
        action5.addAction(action1);
        action5.addAction(action2);
        action5.addAction(action3);
        action5.addAction(action4);
        return action5;
    }
    
    public static SequenceAction sequence(final Action action1, final Action action2, final Action action3, final Action action4, final Action action5) {
        final SequenceAction action6 = action(SequenceAction.class);
        action6.addAction(action1);
        action6.addAction(action2);
        action6.addAction(action3);
        action6.addAction(action4);
        action6.addAction(action5);
        return action6;
    }
    
    public static SequenceAction sequence(final Action... actions) {
        final SequenceAction action = action(SequenceAction.class);
        for (int i = 0, n = actions.length; i < n; ++i) {
            action.addAction(actions[i]);
        }
        return action;
    }
    
    public static SequenceAction sequence() {
        return action(SequenceAction.class);
    }
    
    public static ParallelAction parallel(final Action action1) {
        final ParallelAction action2 = action(ParallelAction.class);
        action2.addAction(action1);
        return action2;
    }
    
    public static ParallelAction parallel(final Action action1, final Action action2) {
        final ParallelAction action3 = action(ParallelAction.class);
        action3.addAction(action1);
        action3.addAction(action2);
        return action3;
    }
    
    public static ParallelAction parallel(final Action action1, final Action action2, final Action action3) {
        final ParallelAction action4 = action(ParallelAction.class);
        action4.addAction(action1);
        action4.addAction(action2);
        action4.addAction(action3);
        return action4;
    }
    
    public static ParallelAction parallel(final Action action1, final Action action2, final Action action3, final Action action4) {
        final ParallelAction action5 = action(ParallelAction.class);
        action5.addAction(action1);
        action5.addAction(action2);
        action5.addAction(action3);
        action5.addAction(action4);
        return action5;
    }
    
    public static ParallelAction parallel(final Action action1, final Action action2, final Action action3, final Action action4, final Action action5) {
        final ParallelAction action6 = action(ParallelAction.class);
        action6.addAction(action1);
        action6.addAction(action2);
        action6.addAction(action3);
        action6.addAction(action4);
        action6.addAction(action5);
        return action6;
    }
    
    public static ParallelAction parallel(final Action... actions) {
        final ParallelAction action = action(ParallelAction.class);
        for (int i = 0, n = actions.length; i < n; ++i) {
            action.addAction(actions[i]);
        }
        return action;
    }
    
    public static ParallelAction parallel() {
        return action(ParallelAction.class);
    }
    
    public static RepeatAction repeat(final int count, final Action repeatedAction) {
        final RepeatAction action = action(RepeatAction.class);
        action.setCount(count);
        action.setAction(repeatedAction);
        return action;
    }
    
    public static RepeatAction forever(final Action repeatedAction) {
        final RepeatAction action = action(RepeatAction.class);
        action.setCount(-1);
        action.setAction(repeatedAction);
        return action;
    }
    
    public static RunnableAction run(final Runnable runnable) {
        final RunnableAction action = action(RunnableAction.class);
        action.setRunnable(runnable);
        return action;
    }
    
    public static LayoutAction layout(final boolean enabled) {
        final LayoutAction action = action(LayoutAction.class);
        action.setLayoutEnabled(enabled);
        return action;
    }
    
    public static AfterAction after(final Action action) {
        final AfterAction afterAction = action(AfterAction.class);
        afterAction.setAction(action);
        return afterAction;
    }
    
    public static AddListenerAction addListener(final EventListener listener, final boolean capture) {
        final AddListenerAction addAction = action(AddListenerAction.class);
        addAction.setListener(listener);
        addAction.setCapture(capture);
        return addAction;
    }
    
    public static AddListenerAction addListener(final EventListener listener, final boolean capture, final Actor targetActor) {
        final AddListenerAction addAction = action(AddListenerAction.class);
        addAction.setTarget(targetActor);
        addAction.setListener(listener);
        addAction.setCapture(capture);
        return addAction;
    }
    
    public static RemoveListenerAction removeListener(final EventListener listener, final boolean capture) {
        final RemoveListenerAction addAction = action(RemoveListenerAction.class);
        addAction.setListener(listener);
        addAction.setCapture(capture);
        return addAction;
    }
    
    public static RemoveListenerAction removeListener(final EventListener listener, final boolean capture, final Actor targetActor) {
        final RemoveListenerAction addAction = action(RemoveListenerAction.class);
        addAction.setTarget(targetActor);
        addAction.setListener(listener);
        addAction.setCapture(capture);
        return addAction;
    }
}

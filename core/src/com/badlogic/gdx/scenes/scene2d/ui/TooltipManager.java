// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import java.util.Iterator;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Files;

public class TooltipManager
{
    private static TooltipManager instance;
    private static Files files;
    public float initialTime;
    public float subsequentTime;
    public float resetTime;
    public boolean enabled;
    public boolean animations;
    public float maxWidth;
    public float offsetX;
    public float offsetY;
    public float edgeDistance;
    final Array<Tooltip> shown;
    float time;
    final Timer.Task resetTask;
    Tooltip showTooltip;
    final Timer.Task showTask;
    
    public TooltipManager() {
        this.initialTime = 2.0f;
        this.subsequentTime = 0.0f;
        this.resetTime = 1.5f;
        this.enabled = true;
        this.animations = true;
        this.maxWidth = 2.14748365E9f;
        this.offsetX = 15.0f;
        this.offsetY = 19.0f;
        this.edgeDistance = 7.0f;
        this.shown = new Array<Tooltip>();
        this.time = this.initialTime;
        this.resetTask = new Timer.Task() {
            @Override
            public void run() {
                TooltipManager.this.time = TooltipManager.this.initialTime;
            }
        };
        this.showTask = new Timer.Task() {
            @Override
            public void run() {
                if (TooltipManager.this.showTooltip == null || TooltipManager.this.showTooltip.targetActor == null) {
                    return;
                }
                final Stage stage = TooltipManager.this.showTooltip.targetActor.getStage();
                if (stage == null) {
                    return;
                }
                stage.addActor(TooltipManager.this.showTooltip.container);
                TooltipManager.this.showTooltip.container.toFront();
                TooltipManager.this.shown.add(TooltipManager.this.showTooltip);
                TooltipManager.this.showTooltip.container.clearActions();
                TooltipManager.this.showAction(TooltipManager.this.showTooltip);
                if (!TooltipManager.this.showTooltip.instant) {
                    TooltipManager.this.time = TooltipManager.this.subsequentTime;
                    TooltipManager.this.resetTask.cancel();
                }
            }
        };
    }
    
    public void touchDown(final Tooltip tooltip) {
        this.showTask.cancel();
        if (tooltip.container.remove()) {
            this.resetTask.cancel();
        }
        this.resetTask.run();
        if (this.enabled || tooltip.always) {
            this.showTooltip = tooltip;
            Timer.schedule(this.showTask, this.time);
        }
    }
    
    public void enter(final Tooltip tooltip) {
        this.showTooltip = tooltip;
        this.showTask.cancel();
        if (this.enabled || tooltip.always) {
            if (this.time == 0.0f || tooltip.instant) {
                this.showTask.run();
            }
            else {
                Timer.schedule(this.showTask, this.time);
            }
        }
    }
    
    public void hide(final Tooltip tooltip) {
        this.showTooltip = null;
        this.showTask.cancel();
        if (tooltip.container.hasParent()) {
            this.shown.removeValue(tooltip, true);
            this.hideAction(tooltip);
            this.resetTask.cancel();
            Timer.schedule(this.resetTask, this.resetTime);
        }
    }
    
    protected void showAction(final Tooltip tooltip) {
        final float actionTime = this.animations ? ((this.time > 0.0f) ? 0.5f : 0.15f) : 0.1f;
        tooltip.container.setTransform(true);
        tooltip.container.getColor().a = 0.2f;
        tooltip.container.setScale(0.05f);
        tooltip.container.addAction(Actions.parallel(Actions.fadeIn(actionTime, Interpolation.fade), Actions.scaleTo(1.0f, 1.0f, actionTime, Interpolation.fade)));
    }
    
    protected void hideAction(final Tooltip tooltip) {
        tooltip.container.addAction(Actions.sequence(Actions.parallel(Actions.alpha(0.2f, 0.2f, Interpolation.fade), Actions.scaleTo(0.05f, 0.05f, 0.2f, Interpolation.fade)), Actions.removeActor()));
    }
    
    public void hideAll() {
        this.resetTask.cancel();
        this.showTask.cancel();
        this.time = this.initialTime;
        this.showTooltip = null;
        for (final Tooltip tooltip : this.shown) {
            tooltip.hide();
        }
        this.shown.clear();
    }
    
    public void instant() {
        this.time = 0.0f;
        this.showTask.run();
        this.showTask.cancel();
    }
    
    public static TooltipManager getInstance() {
        if (TooltipManager.files == null || TooltipManager.files != Gdx.files) {
            TooltipManager.files = Gdx.files;
            TooltipManager.instance = new TooltipManager();
        }
        return TooltipManager.instance;
    }
}

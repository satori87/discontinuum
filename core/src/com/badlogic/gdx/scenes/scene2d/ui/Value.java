// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Value
{
    public static final Fixed zero;
    public static Value minWidth;
    public static Value minHeight;
    public static Value prefWidth;
    public static Value prefHeight;
    public static Value maxWidth;
    public static Value maxHeight;
    
    static {
        zero = new Fixed(0.0f);
        Value.minWidth = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getMinWidth();
                }
                return (context == null) ? 0.0f : context.getWidth();
            }
        };
        Value.minHeight = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getMinHeight();
                }
                return (context == null) ? 0.0f : context.getHeight();
            }
        };
        Value.prefWidth = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getPrefWidth();
                }
                return (context == null) ? 0.0f : context.getWidth();
            }
        };
        Value.prefHeight = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getPrefHeight();
                }
                return (context == null) ? 0.0f : context.getHeight();
            }
        };
        Value.maxWidth = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getMaxWidth();
                }
                return (context == null) ? 0.0f : context.getWidth();
            }
        };
        Value.maxHeight = new Value() {
            @Override
            public float get(final Actor context) {
                if (context instanceof Layout) {
                    return ((Layout)context).getMaxHeight();
                }
                return (context == null) ? 0.0f : context.getHeight();
            }
        };
    }
    
    public float get() {
        return this.get(null);
    }
    
    public abstract float get(final Actor p0);
    
    public static Value percentWidth(final float percent) {
        return new Value() {
            @Override
            public float get(final Actor actor) {
                return actor.getWidth() * percent;
            }
        };
    }
    
    public static Value percentHeight(final float percent) {
        return new Value() {
            @Override
            public float get(final Actor actor) {
                return actor.getHeight() * percent;
            }
        };
    }
    
    public static Value percentWidth(final float percent, final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        return new Value() {
            @Override
            public float get(final Actor context) {
                return actor.getWidth() * percent;
            }
        };
    }
    
    public static Value percentHeight(final float percent, final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        return new Value() {
            @Override
            public float get(final Actor context) {
                return actor.getHeight() * percent;
            }
        };
    }
    
    public static class Fixed extends Value
    {
        private final float value;
        
        public Fixed(final float value) {
            this.value = value;
        }
        
        @Override
        public float get(final Actor context) {
            return this.value;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextTooltip extends Tooltip<Label>
{
    public TextTooltip(final String text, final Skin skin) {
        this(text, TooltipManager.getInstance(), skin.get(TextTooltipStyle.class));
    }
    
    public TextTooltip(final String text, final Skin skin, final String styleName) {
        this(text, TooltipManager.getInstance(), skin.get(styleName, TextTooltipStyle.class));
    }
    
    public TextTooltip(final String text, final TextTooltipStyle style) {
        this(text, TooltipManager.getInstance(), style);
    }
    
    public TextTooltip(final String text, final TooltipManager manager, final Skin skin) {
        this(text, manager, skin.get(TextTooltipStyle.class));
    }
    
    public TextTooltip(final String text, final TooltipManager manager, final Skin skin, final String styleName) {
        this(text, manager, skin.get(styleName, TextTooltipStyle.class));
    }
    
    public TextTooltip(final String text, final TooltipManager manager, final TextTooltipStyle style) {
        super(null, manager);
        final Label label = new Label(text, style.label);
        label.setWrap(true);
        this.container.setActor((T)label);
        this.container.width(new Value() {
            @Override
            public float get(final Actor context) {
                return Math.min(manager.maxWidth, ((Label)TextTooltip.this.container.getActor()).getGlyphLayout().width);
            }
        });
        this.setStyle(style);
    }
    
    public void setStyle(final TextTooltipStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof TextTooltipStyle)) {
            throw new IllegalArgumentException("style must be a TextTooltipStyle.");
        }
        ((Label)this.container.getActor()).setStyle(style.label);
        this.container.setBackground(style.background);
        this.container.maxWidth(style.wrapWidth);
    }
    
    public static class TextTooltipStyle
    {
        public Label.LabelStyle label;
        public Drawable background;
        public float wrapWidth;
        
        public TextTooltipStyle() {
        }
        
        public TextTooltipStyle(final Label.LabelStyle label, final Drawable background) {
            this.label = label;
            this.background = background;
        }
        
        public TextTooltipStyle(final TextTooltipStyle style) {
            this.label = new Label.LabelStyle(style.label);
            this.background = style.background;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Scaling;

public class CheckBox extends TextButton
{
    private Image image;
    private Cell imageCell;
    private CheckBoxStyle style;
    
    public CheckBox(final String text, final Skin skin) {
        this(text, skin.get(CheckBoxStyle.class));
    }
    
    public CheckBox(final String text, final Skin skin, final String styleName) {
        this(text, skin.get(styleName, CheckBoxStyle.class));
    }
    
    public CheckBox(final String text, final CheckBoxStyle style) {
        super(text, style);
        this.clearChildren();
        final Label label = this.getLabel();
        final Image image = new Image(style.checkboxOff, Scaling.none);
        this.image = image;
        this.imageCell = this.add(image);
        this.add(label);
        label.setAlignment(8);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    @Override
    public void setStyle(final ButtonStyle style) {
        if (!(style instanceof CheckBoxStyle)) {
            throw new IllegalArgumentException("style must be a CheckBoxStyle.");
        }
        super.setStyle(style);
        this.style = (CheckBoxStyle)style;
    }
    
    @Override
    public CheckBoxStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        Drawable checkbox = null;
        if (this.isDisabled()) {
            if (this.isChecked && this.style.checkboxOnDisabled != null) {
                checkbox = this.style.checkboxOnDisabled;
            }
            else {
                checkbox = this.style.checkboxOffDisabled;
            }
        }
        if (checkbox == null) {
            final boolean over = this.isOver() && !this.isDisabled();
            if (this.isChecked && this.style.checkboxOn != null) {
                checkbox = ((over && this.style.checkboxOnOver != null) ? this.style.checkboxOnOver : this.style.checkboxOn);
            }
            else if (over && this.style.checkboxOver != null) {
                checkbox = this.style.checkboxOver;
            }
            else {
                checkbox = this.style.checkboxOff;
            }
        }
        this.image.setDrawable(checkbox);
        super.draw(batch, parentAlpha);
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public Cell getImageCell() {
        return this.imageCell;
    }
    
    public static class CheckBoxStyle extends TextButtonStyle
    {
        public Drawable checkboxOn;
        public Drawable checkboxOff;
        public Drawable checkboxOnOver;
        public Drawable checkboxOver;
        public Drawable checkboxOnDisabled;
        public Drawable checkboxOffDisabled;
        
        public CheckBoxStyle() {
        }
        
        public CheckBoxStyle(final Drawable checkboxOff, final Drawable checkboxOn, final BitmapFont font, final Color fontColor) {
            this.checkboxOff = checkboxOff;
            this.checkboxOn = checkboxOn;
            this.font = font;
            this.fontColor = fontColor;
        }
        
        public CheckBoxStyle(final CheckBoxStyle style) {
            super(style);
            this.checkboxOff = style.checkboxOff;
            this.checkboxOn = style.checkboxOn;
            this.checkboxOver = style.checkboxOver;
            this.checkboxOffDisabled = style.checkboxOffDisabled;
            this.checkboxOnDisabled = style.checkboxOnDisabled;
        }
    }
}

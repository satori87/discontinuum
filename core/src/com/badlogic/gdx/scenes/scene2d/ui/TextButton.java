// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class TextButton extends Button
{
    private Label label;
    private TextButtonStyle style;
    
    public TextButton(final String text, final Skin skin) {
        this(text, skin.get(TextButtonStyle.class));
        this.setSkin(skin);
    }
    
    public TextButton(final String text, final Skin skin, final String styleName) {
        this(text, skin.get(styleName, TextButtonStyle.class));
        this.setSkin(skin);
    }
    
    public TextButton(final String text, final TextButtonStyle style) {
        this.setStyle(style);
        this.style = style;
        (this.label = new Label(text, new Label.LabelStyle(style.font, style.fontColor))).setAlignment(1);
        this.add(this.label).expand().fill();
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    @Override
    public void setStyle(final ButtonStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof TextButtonStyle)) {
            throw new IllegalArgumentException("style must be a TextButtonStyle.");
        }
        super.setStyle(style);
        this.style = (TextButtonStyle)style;
        if (this.label != null) {
            final TextButtonStyle textButtonStyle = (TextButtonStyle)style;
            final Label.LabelStyle labelStyle = this.label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = textButtonStyle.fontColor;
            this.label.setStyle(labelStyle);
        }
    }
    
    @Override
    public TextButtonStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        Color fontColor;
        if (this.isDisabled() && this.style.disabledFontColor != null) {
            fontColor = this.style.disabledFontColor;
        }
        else if (this.isPressed() && this.style.downFontColor != null) {
            fontColor = this.style.downFontColor;
        }
        else if (this.isChecked && this.style.checkedFontColor != null) {
            fontColor = ((this.isOver() && this.style.checkedOverFontColor != null) ? this.style.checkedOverFontColor : this.style.checkedFontColor);
        }
        else if (this.isOver() && this.style.overFontColor != null) {
            fontColor = this.style.overFontColor;
        }
        else {
            fontColor = this.style.fontColor;
        }
        if (fontColor != null) {
            this.label.getStyle().fontColor = fontColor;
        }
        super.draw(batch, parentAlpha);
    }
    
    public void setLabel(final Label label) {
        this.getLabelCell().setActor(label);
        this.label = label;
    }
    
    public Label getLabel() {
        return this.label;
    }
    
    public Cell<Label> getLabelCell() {
        return this.getCell(this.label);
    }
    
    public void setText(final String text) {
        this.label.setText(text);
    }
    
    public CharSequence getText() {
        return this.label.getText();
    }
    
    public static class TextButtonStyle extends ButtonStyle
    {
        public BitmapFont font;
        public Color fontColor;
        public Color downFontColor;
        public Color overFontColor;
        public Color checkedFontColor;
        public Color checkedOverFontColor;
        public Color disabledFontColor;
        
        public TextButtonStyle() {
        }
        
        public TextButtonStyle(final Drawable up, final Drawable down, final Drawable checked, final BitmapFont font) {
            super(up, down, checked);
            this.font = font;
        }
        
        public TextButtonStyle(final TextButtonStyle style) {
            super(style);
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            if (style.downFontColor != null) {
                this.downFontColor = new Color(style.downFontColor);
            }
            if (style.overFontColor != null) {
                this.overFontColor = new Color(style.overFontColor);
            }
            if (style.checkedFontColor != null) {
                this.checkedFontColor = new Color(style.checkedFontColor);
            }
            if (style.checkedOverFontColor != null) {
                this.checkedOverFontColor = new Color(style.checkedOverFontColor);
            }
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
        }
    }
}

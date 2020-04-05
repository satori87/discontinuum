// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class Label extends Widget
{
    private static final Color tempColor;
    private static final GlyphLayout prefSizeLayout;
    private LabelStyle style;
    private final GlyphLayout layout;
    private final Vector2 prefSize;
    private final StringBuilder text;
    private int intValue;
    private BitmapFontCache cache;
    private int labelAlign;
    private int lineAlign;
    private boolean wrap;
    private float lastPrefHeight;
    private boolean prefSizeInvalid;
    private float fontScaleX;
    private float fontScaleY;
    private boolean fontScaleChanged;
    private String ellipsis;
    
    static {
        tempColor = new Color();
        prefSizeLayout = new GlyphLayout();
    }
    
    public Label(final CharSequence text, final Skin skin) {
        this(text, skin.get(LabelStyle.class));
    }
    
    public Label(final CharSequence text, final Skin skin, final String styleName) {
        this(text, skin.get(styleName, LabelStyle.class));
    }
    
    public Label(final CharSequence text, final Skin skin, final String fontName, final Color color) {
        this(text, new LabelStyle(skin.getFont(fontName), color));
    }
    
    public Label(final CharSequence text, final Skin skin, final String fontName, final String colorName) {
        this(text, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
    }
    
    public Label(final CharSequence text, final LabelStyle style) {
        this.layout = new GlyphLayout();
        this.prefSize = new Vector2();
        this.text = new StringBuilder();
        this.intValue = Integer.MIN_VALUE;
        this.labelAlign = 8;
        this.lineAlign = 8;
        this.prefSizeInvalid = true;
        this.fontScaleX = 1.0f;
        this.fontScaleY = 1.0f;
        this.fontScaleChanged = false;
        if (text != null) {
            this.text.append(text);
        }
        this.setStyle(style);
        if (text != null && text.length() > 0) {
            this.setSize(this.getPrefWidth(), this.getPrefHeight());
        }
    }
    
    public void setStyle(final LabelStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        if (style.font == null) {
            throw new IllegalArgumentException("Missing LabelStyle font.");
        }
        this.style = style;
        this.cache = style.font.newFontCache();
        this.invalidateHierarchy();
    }
    
    public LabelStyle getStyle() {
        return this.style;
    }
    
    public boolean setText(final int value) {
        if (this.intValue == value) {
            return false;
        }
        this.setText(Integer.toString(value));
        this.intValue = value;
        return true;
    }
    
    public void setText(CharSequence newText) {
        if (newText == null) {
            newText = "";
        }
        if (newText instanceof StringBuilder) {
            if (this.text.equals(newText)) {
                return;
            }
            this.text.setLength(0);
            this.text.append((StringBuilder)newText);
        }
        else {
            if (this.textEquals(newText)) {
                return;
            }
            this.text.setLength(0);
            this.text.append(newText);
        }
        this.intValue = Integer.MIN_VALUE;
        this.invalidateHierarchy();
    }
    
    public boolean textEquals(final CharSequence other) {
        final int length = this.text.length;
        final char[] chars = this.text.chars;
        if (length != other.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (chars[i] != other.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public StringBuilder getText() {
        return this.text;
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.prefSizeInvalid = true;
    }
    
    private void scaleAndComputePrefSize() {
        final BitmapFont font = this.cache.getFont();
        final float oldScaleX = font.getScaleX();
        final float oldScaleY = font.getScaleY();
        if (this.fontScaleChanged) {
            font.getData().setScale(this.fontScaleX, this.fontScaleY);
        }
        this.computePrefSize();
        if (this.fontScaleChanged) {
            font.getData().setScale(oldScaleX, oldScaleY);
        }
    }
    
    private void computePrefSize() {
        this.prefSizeInvalid = false;
        final GlyphLayout prefSizeLayout = Label.prefSizeLayout;
        if (this.wrap && this.ellipsis == null) {
            float width = this.getWidth();
            if (this.style.background != null) {
                width -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
            }
            prefSizeLayout.setText(this.cache.getFont(), this.text, Color.WHITE, width, 8, true);
        }
        else {
            prefSizeLayout.setText(this.cache.getFont(), this.text);
        }
        this.prefSize.set(prefSizeLayout.width, prefSizeLayout.height);
    }
    
    @Override
    public void layout() {
        final BitmapFont font = this.cache.getFont();
        final float oldScaleX = font.getScaleX();
        final float oldScaleY = font.getScaleY();
        if (this.fontScaleChanged) {
            font.getData().setScale(this.fontScaleX, this.fontScaleY);
        }
        final boolean wrap = this.wrap && this.ellipsis == null;
        if (wrap) {
            final float prefHeight = this.getPrefHeight();
            if (prefHeight != this.lastPrefHeight) {
                this.lastPrefHeight = prefHeight;
                this.invalidateHierarchy();
            }
        }
        float width = this.getWidth();
        float height = this.getHeight();
        final Drawable background = this.style.background;
        float x = 0.0f;
        float y = 0.0f;
        if (background != null) {
            x = background.getLeftWidth();
            y = background.getBottomHeight();
            width -= background.getLeftWidth() + background.getRightWidth();
            height -= background.getBottomHeight() + background.getTopHeight();
        }
        final GlyphLayout layout = this.layout;
        float textWidth;
        float textHeight;
        if (wrap || this.text.indexOf("\n") != -1) {
            layout.setText(font, this.text, 0, this.text.length, Color.WHITE, width, this.lineAlign, wrap, this.ellipsis);
            textWidth = layout.width;
            textHeight = layout.height;
            if ((this.labelAlign & 0x8) == 0x0) {
                if ((this.labelAlign & 0x10) != 0x0) {
                    x += width - textWidth;
                }
                else {
                    x += (width - textWidth) / 2.0f;
                }
            }
        }
        else {
            textWidth = width;
            textHeight = font.getData().capHeight;
        }
        if ((this.labelAlign & 0x2) != 0x0) {
            y += (this.cache.getFont().isFlipped() ? 0.0f : (height - textHeight));
            y += this.style.font.getDescent();
        }
        else if ((this.labelAlign & 0x4) != 0x0) {
            y += (this.cache.getFont().isFlipped() ? (height - textHeight) : 0.0f);
            y -= this.style.font.getDescent();
        }
        else {
            y += (height - textHeight) / 2.0f;
        }
        if (!this.cache.getFont().isFlipped()) {
            y += textHeight;
        }
        layout.setText(font, this.text, 0, this.text.length, Color.WHITE, textWidth, this.lineAlign, wrap, this.ellipsis);
        this.cache.setText(layout, x, y);
        if (this.fontScaleChanged) {
            font.getData().setScale(oldScaleX, oldScaleY);
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        final Color set;
        final Color color = set = Label.tempColor.set(this.getColor());
        set.a *= parentAlpha;
        if (this.style.background != null) {
            batch.setColor(color.r, color.g, color.b, color.a);
            this.style.background.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        if (this.style.fontColor != null) {
            color.mul(this.style.fontColor);
        }
        this.cache.tint(color);
        this.cache.setPosition(this.getX(), this.getY());
        this.cache.draw(batch);
    }
    
    @Override
    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.prefSizeInvalid) {
            this.scaleAndComputePrefSize();
        }
        float width = this.prefSize.x;
        final Drawable background = this.style.background;
        if (background != null) {
            width += background.getLeftWidth() + background.getRightWidth();
        }
        return width;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.prefSizeInvalid) {
            this.scaleAndComputePrefSize();
        }
        float descentScaleCorrection = 1.0f;
        if (this.fontScaleChanged) {
            descentScaleCorrection = this.fontScaleY / this.style.font.getScaleY();
        }
        float height = this.prefSize.y - this.style.font.getDescent() * descentScaleCorrection * 2.0f;
        final Drawable background = this.style.background;
        if (background != null) {
            height += background.getTopHeight() + background.getBottomHeight();
        }
        return height;
    }
    
    public GlyphLayout getGlyphLayout() {
        return this.layout;
    }
    
    public void setWrap(final boolean wrap) {
        this.wrap = wrap;
        this.invalidateHierarchy();
    }
    
    public int getLabelAlign() {
        return this.labelAlign;
    }
    
    public int getLineAlign() {
        return this.lineAlign;
    }
    
    public void setAlignment(final int alignment) {
        this.setAlignment(alignment, alignment);
    }
    
    public void setAlignment(final int labelAlign, final int lineAlign) {
        this.labelAlign = labelAlign;
        if ((lineAlign & 0x8) != 0x0) {
            this.lineAlign = 8;
        }
        else if ((lineAlign & 0x10) != 0x0) {
            this.lineAlign = 16;
        }
        else {
            this.lineAlign = 1;
        }
        this.invalidate();
    }
    
    public void setFontScale(final float fontScale) {
        this.setFontScale(fontScale, fontScale);
    }
    
    public void setFontScale(final float fontScaleX, final float fontScaleY) {
        this.fontScaleChanged = true;
        this.fontScaleX = fontScaleX;
        this.fontScaleY = fontScaleY;
        this.invalidateHierarchy();
    }
    
    public float getFontScaleX() {
        return this.fontScaleX;
    }
    
    public void setFontScaleX(final float fontScaleX) {
        this.setFontScale(fontScaleX, this.fontScaleY);
    }
    
    public float getFontScaleY() {
        return this.fontScaleY;
    }
    
    public void setFontScaleY(final float fontScaleY) {
        this.setFontScale(this.fontScaleX, fontScaleY);
    }
    
    public void setEllipsis(final String ellipsis) {
        this.ellipsis = ellipsis;
    }
    
    public void setEllipsis(final boolean ellipsis) {
        if (ellipsis) {
            this.ellipsis = "...";
        }
        else {
            this.ellipsis = null;
        }
    }
    
    protected BitmapFontCache getBitmapFontCache() {
        return this.cache;
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + ": " + (Object)this.text;
    }
    
    public static class LabelStyle
    {
        public BitmapFont font;
        public Color fontColor;
        public Drawable background;
        
        public LabelStyle() {
        }
        
        public LabelStyle(final BitmapFont font, final Color fontColor) {
            this.font = font;
            this.fontColor = fontColor;
        }
        
        public LabelStyle(final LabelStyle style) {
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            this.background = style.background;
        }
    }
}

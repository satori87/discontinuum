// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.NumberUtils;

public class Color
{
    public static final Color WHITE;
    public static final Color LIGHT_GRAY;
    public static final Color GRAY;
    public static final Color DARK_GRAY;
    public static final Color BLACK;
    public static final float WHITE_FLOAT_BITS;
    public static final Color CLEAR;
    public static final Color BLUE;
    public static final Color NAVY;
    public static final Color ROYAL;
    public static final Color SLATE;
    public static final Color SKY;
    public static final Color CYAN;
    public static final Color TEAL;
    public static final Color GREEN;
    public static final Color CHARTREUSE;
    public static final Color LIME;
    public static final Color FOREST;
    public static final Color OLIVE;
    public static final Color YELLOW;
    public static final Color GOLD;
    public static final Color GOLDENROD;
    public static final Color ORANGE;
    public static final Color BROWN;
    public static final Color TAN;
    public static final Color FIREBRICK;
    public static final Color RED;
    public static final Color SCARLET;
    public static final Color CORAL;
    public static final Color SALMON;
    public static final Color PINK;
    public static final Color MAGENTA;
    public static final Color PURPLE;
    public static final Color VIOLET;
    public static final Color MAROON;
    public float r;
    public float g;
    public float b;
    public float a;
    
    static {
        WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        LIGHT_GRAY = new Color(-1077952513);
        GRAY = new Color(2139062271);
        DARK_GRAY = new Color(1061109759);
        BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        WHITE_FLOAT_BITS = Color.WHITE.toFloatBits();
        CLEAR = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
        NAVY = new Color(0.0f, 0.0f, 0.5f, 1.0f);
        ROYAL = new Color(1097458175);
        SLATE = new Color(1887473919);
        SKY = new Color(-2016482305);
        CYAN = new Color(0.0f, 1.0f, 1.0f, 1.0f);
        TEAL = new Color(0.0f, 0.5f, 0.5f, 1.0f);
        GREEN = new Color(16711935);
        CHARTREUSE = new Color(2147418367);
        LIME = new Color(852308735);
        FOREST = new Color(579543807);
        OLIVE = new Color(1804477439);
        YELLOW = new Color(-65281);
        GOLD = new Color(-2686721);
        GOLDENROD = new Color(-626712321);
        ORANGE = new Color(-5963521);
        BROWN = new Color(-1958407169);
        TAN = new Color(-759919361);
        FIREBRICK = new Color(-1306385665);
        RED = new Color(-16776961);
        SCARLET = new Color(-13361921);
        CORAL = new Color(-8433409);
        SALMON = new Color(-92245249);
        PINK = new Color(-9849601);
        MAGENTA = new Color(1.0f, 0.0f, 1.0f, 1.0f);
        PURPLE = new Color(-1608453889);
        VIOLET = new Color(-293409025);
        MAROON = new Color(-1339006721);
    }
    
    public Color() {
    }
    
    public Color(final int rgba8888) {
        rgba8888ToColor(this, rgba8888);
    }
    
    public Color(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.clamp();
    }
    
    public Color(final Color color) {
        this.set(color);
    }
    
    public Color set(final Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
        return this;
    }
    
    public Color mul(final Color color) {
        this.r *= color.r;
        this.g *= color.g;
        this.b *= color.b;
        this.a *= color.a;
        return this.clamp();
    }
    
    public Color mul(final float value) {
        this.r *= value;
        this.g *= value;
        this.b *= value;
        this.a *= value;
        return this.clamp();
    }
    
    public Color add(final Color color) {
        this.r += color.r;
        this.g += color.g;
        this.b += color.b;
        this.a += color.a;
        return this.clamp();
    }
    
    public Color sub(final Color color) {
        this.r -= color.r;
        this.g -= color.g;
        this.b -= color.b;
        this.a -= color.a;
        return this.clamp();
    }
    
    public Color clamp() {
        if (this.r < 0.0f) {
            this.r = 0.0f;
        }
        else if (this.r > 1.0f) {
            this.r = 1.0f;
        }
        if (this.g < 0.0f) {
            this.g = 0.0f;
        }
        else if (this.g > 1.0f) {
            this.g = 1.0f;
        }
        if (this.b < 0.0f) {
            this.b = 0.0f;
        }
        else if (this.b > 1.0f) {
            this.b = 1.0f;
        }
        if (this.a < 0.0f) {
            this.a = 0.0f;
        }
        else if (this.a > 1.0f) {
            this.a = 1.0f;
        }
        return this;
    }
    
    public Color set(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this.clamp();
    }
    
    public Color set(final int rgba) {
        rgba8888ToColor(this, rgba);
        return this;
    }
    
    public Color add(final float r, final float g, final float b, final float a) {
        this.r += r;
        this.g += g;
        this.b += b;
        this.a += a;
        return this.clamp();
    }
    
    public Color sub(final float r, final float g, final float b, final float a) {
        this.r -= r;
        this.g -= g;
        this.b -= b;
        this.a -= a;
        return this.clamp();
    }
    
    public Color mul(final float r, final float g, final float b, final float a) {
        this.r *= r;
        this.g *= g;
        this.b *= b;
        this.a *= a;
        return this.clamp();
    }
    
    public Color lerp(final Color target, final float t) {
        this.r += t * (target.r - this.r);
        this.g += t * (target.g - this.g);
        this.b += t * (target.b - this.b);
        this.a += t * (target.a - this.a);
        return this.clamp();
    }
    
    public Color lerp(final float r, final float g, final float b, final float a, final float t) {
        this.r += t * (r - this.r);
        this.g += t * (g - this.g);
        this.b += t * (b - this.b);
        this.a += t * (a - this.a);
        return this.clamp();
    }
    
    public Color premultiplyAlpha() {
        this.r *= this.a;
        this.g *= this.a;
        this.b *= this.a;
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Color color = (Color)o;
        return this.toIntBits() == color.toIntBits();
    }
    
    @Override
    public int hashCode() {
        int result = (this.r != 0.0f) ? NumberUtils.floatToIntBits(this.r) : 0;
        result = 31 * result + ((this.g != 0.0f) ? NumberUtils.floatToIntBits(this.g) : 0);
        result = 31 * result + ((this.b != 0.0f) ? NumberUtils.floatToIntBits(this.b) : 0);
        result = 31 * result + ((this.a != 0.0f) ? NumberUtils.floatToIntBits(this.a) : 0);
        return result;
    }
    
    public float toFloatBits() {
        final int color = (int)(255.0f * this.a) << 24 | (int)(255.0f * this.b) << 16 | (int)(255.0f * this.g) << 8 | (int)(255.0f * this.r);
        return NumberUtils.intToFloatColor(color);
    }
    
    public int toIntBits() {
        return (int)(255.0f * this.a) << 24 | (int)(255.0f * this.b) << 16 | (int)(255.0f * this.g) << 8 | (int)(255.0f * this.r);
    }
    
    @Override
    public String toString() {
        String value;
        for (value = Integer.toHexString((int)(255.0f * this.r) << 24 | (int)(255.0f * this.g) << 16 | (int)(255.0f * this.b) << 8 | (int)(255.0f * this.a)); value.length() < 8; value = "0" + value) {}
        return value;
    }
    
    public static Color valueOf(String hex) {
        hex = ((hex.charAt(0) == '#') ? hex.substring(1) : hex);
        final int r = Integer.valueOf(hex.substring(0, 2), 16);
        final int g = Integer.valueOf(hex.substring(2, 4), 16);
        final int b = Integer.valueOf(hex.substring(4, 6), 16);
        final int a = (hex.length() != 8) ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }
    
    public static float toFloatBits(final int r, final int g, final int b, final int a) {
        final int color = a << 24 | b << 16 | g << 8 | r;
        final float floatColor = NumberUtils.intToFloatColor(color);
        return floatColor;
    }
    
    public static float toFloatBits(final float r, final float g, final float b, final float a) {
        final int color = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        return NumberUtils.intToFloatColor(color);
    }
    
    public static int toIntBits(final int r, final int g, final int b, final int a) {
        return a << 24 | b << 16 | g << 8 | r;
    }
    
    public static int alpha(final float alpha) {
        return (int)(alpha * 255.0f);
    }
    
    public static int luminanceAlpha(final float luminance, final float alpha) {
        return (int)(luminance * 255.0f) << 8 | (int)(alpha * 255.0f);
    }
    
    public static int rgb565(final float r, final float g, final float b) {
        return (int)(r * 31.0f) << 11 | (int)(g * 63.0f) << 5 | (int)(b * 31.0f);
    }
    
    public static int rgba4444(final float r, final float g, final float b, final float a) {
        return (int)(r * 15.0f) << 12 | (int)(g * 15.0f) << 8 | (int)(b * 15.0f) << 4 | (int)(a * 15.0f);
    }
    
    public static int rgb888(final float r, final float g, final float b) {
        return (int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f);
    }
    
    public static int rgba8888(final float r, final float g, final float b, final float a) {
        return (int)(r * 255.0f) << 24 | (int)(g * 255.0f) << 16 | (int)(b * 255.0f) << 8 | (int)(a * 255.0f);
    }
    
    public static int argb8888(final float a, final float r, final float g, final float b) {
        return (int)(a * 255.0f) << 24 | (int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f);
    }
    
    public static int rgb565(final Color color) {
        return (int)(color.r * 31.0f) << 11 | (int)(color.g * 63.0f) << 5 | (int)(color.b * 31.0f);
    }
    
    public static int rgba4444(final Color color) {
        return (int)(color.r * 15.0f) << 12 | (int)(color.g * 15.0f) << 8 | (int)(color.b * 15.0f) << 4 | (int)(color.a * 15.0f);
    }
    
    public static int rgb888(final Color color) {
        return (int)(color.r * 255.0f) << 16 | (int)(color.g * 255.0f) << 8 | (int)(color.b * 255.0f);
    }
    
    public static int rgba8888(final Color color) {
        return (int)(color.r * 255.0f) << 24 | (int)(color.g * 255.0f) << 16 | (int)(color.b * 255.0f) << 8 | (int)(color.a * 255.0f);
    }
    
    public static int argb8888(final Color color) {
        return (int)(color.a * 255.0f) << 24 | (int)(color.r * 255.0f) << 16 | (int)(color.g * 255.0f) << 8 | (int)(color.b * 255.0f);
    }
    
    public static void rgb565ToColor(final Color color, final int value) {
        color.r = ((value & 0xF800) >>> 11) / 31.0f;
        color.g = ((value & 0x7E0) >>> 5) / 63.0f;
        color.b = ((value & 0x1F) >>> 0) / 31.0f;
    }
    
    public static void rgba4444ToColor(final Color color, final int value) {
        color.r = ((value & 0xF000) >>> 12) / 15.0f;
        color.g = ((value & 0xF00) >>> 8) / 15.0f;
        color.b = ((value & 0xF0) >>> 4) / 15.0f;
        color.a = (value & 0xF) / 15.0f;
    }
    
    public static void rgb888ToColor(final Color color, final int value) {
        color.r = ((value & 0xFF0000) >>> 16) / 255.0f;
        color.g = ((value & 0xFF00) >>> 8) / 255.0f;
        color.b = (value & 0xFF) / 255.0f;
    }
    
    public static void rgba8888ToColor(final Color color, final int value) {
        color.r = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.g = ((value & 0xFF0000) >>> 16) / 255.0f;
        color.b = ((value & 0xFF00) >>> 8) / 255.0f;
        color.a = (value & 0xFF) / 255.0f;
    }
    
    public static void argb8888ToColor(final Color color, final int value) {
        color.a = ((value & 0xFF000000) >>> 24) / 255.0f;
        color.r = ((value & 0xFF0000) >>> 16) / 255.0f;
        color.g = ((value & 0xFF00) >>> 8) / 255.0f;
        color.b = (value & 0xFF) / 255.0f;
    }
    
    public static void abgr8888ToColor(final Color color, final float value) {
        final int c = NumberUtils.floatToIntColor(value);
        color.a = ((c & 0xFF000000) >>> 24) / 255.0f;
        color.b = ((c & 0xFF0000) >>> 16) / 255.0f;
        color.g = ((c & 0xFF00) >>> 8) / 255.0f;
        color.r = (c & 0xFF) / 255.0f;
    }
    
    public Color fromHsv(final float h, final float s, final float v) {
        final float x = (h / 60.0f + 6.0f) % 6.0f;
        final int i = (int)x;
        final float f = x - i;
        final float p = v * (1.0f - s);
        final float q = v * (1.0f - s * f);
        final float t = v * (1.0f - s * (1.0f - f));
        switch (i) {
            case 0: {
                this.r = v;
                this.g = t;
                this.b = p;
                break;
            }
            case 1: {
                this.r = q;
                this.g = v;
                this.b = p;
                break;
            }
            case 2: {
                this.r = p;
                this.g = v;
                this.b = t;
                break;
            }
            case 3: {
                this.r = p;
                this.g = q;
                this.b = v;
                break;
            }
            case 4: {
                this.r = t;
                this.g = p;
                this.b = v;
                break;
            }
            default: {
                this.r = v;
                this.g = p;
                this.b = q;
                break;
            }
        }
        return this.clamp();
    }
    
    public Color fromHsv(final float[] hsv) {
        return this.fromHsv(hsv[0], hsv[1], hsv[2]);
    }
    
    public float[] toHsv(final float[] hsv) {
        final float max = Math.max(Math.max(this.r, this.g), this.b);
        final float min = Math.min(Math.min(this.r, this.g), this.b);
        final float range = max - min;
        if (range == 0.0f) {
            hsv[0] = 0.0f;
        }
        else if (max == this.r) {
            hsv[0] = (60.0f * (this.g - this.b) / range + 360.0f) % 360.0f;
        }
        else if (max == this.g) {
            hsv[0] = 60.0f * (this.b - this.r) / range + 120.0f;
        }
        else {
            hsv[0] = 60.0f * (this.r - this.g) / range + 240.0f;
        }
        if (max > 0.0f) {
            hsv[1] = 1.0f - min / max;
        }
        else {
            hsv[1] = 0.0f;
        }
        hsv[2] = max;
        return hsv;
    }
    
    public Color cpy() {
        return new Color(this);
    }
}

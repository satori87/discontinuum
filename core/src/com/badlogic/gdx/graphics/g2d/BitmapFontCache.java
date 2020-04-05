// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;

public class BitmapFontCache
{
    private static final Color tempColor;
    private final BitmapFont font;
    private boolean integer;
    private final Array<GlyphLayout> layouts;
    private final Array<GlyphLayout> pooledLayouts;
    private int glyphCount;
    private float x;
    private float y;
    private final Color color;
    private float currentTint;
    private float[][] pageVertices;
    private int[] idx;
    private IntArray[] pageGlyphIndices;
    private int[] tempGlyphCount;
    
    static {
        tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public BitmapFontCache(final BitmapFont font) {
        this(font, font.usesIntegerPositions());
    }
    
    public BitmapFontCache(final BitmapFont font, final boolean integer) {
        this.layouts = new Array<GlyphLayout>();
        this.pooledLayouts = new Array<GlyphLayout>();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.font = font;
        this.integer = integer;
        final int pageCount = font.regions.size;
        if (pageCount == 0) {
            throw new IllegalArgumentException("The specified font must contain at least one texture page.");
        }
        this.pageVertices = new float[pageCount][];
        this.idx = new int[pageCount];
        if (pageCount > 1) {
            this.pageGlyphIndices = new IntArray[pageCount];
            for (int i = 0, n = this.pageGlyphIndices.length; i < n; ++i) {
                this.pageGlyphIndices[i] = new IntArray();
            }
        }
        this.tempGlyphCount = new int[pageCount];
    }
    
    public void setPosition(final float x, final float y) {
        this.translate(x - this.x, y - this.y);
    }
    
    public void translate(float xAmount, float yAmount) {
        if (xAmount == 0.0f && yAmount == 0.0f) {
            return;
        }
        if (this.integer) {
            xAmount = (float)Math.round(xAmount);
            yAmount = (float)Math.round(yAmount);
        }
        this.x += xAmount;
        this.y += yAmount;
        final float[][] pageVertices = this.pageVertices;
        for (int i = 0, n = pageVertices.length; i < n; ++i) {
            final float[] vertices = pageVertices[i];
            for (int ii = 0, nn = this.idx[i]; ii < nn; ii += 5) {
                final float[] array = vertices;
                final int n2 = ii;
                array[n2] += xAmount;
                final float[] array2 = vertices;
                final int n3 = ii + 1;
                array2[n3] += yAmount;
            }
        }
    }
    
    public void tint(final Color tint) {
        final float newTint = tint.toFloatBits();
        if (this.currentTint == newTint) {
            return;
        }
        this.currentTint = newTint;
        final int[] tempGlyphCount = this.tempGlyphCount;
        for (int i = 0, n = tempGlyphCount.length; i < n; ++i) {
            tempGlyphCount[i] = 0;
        }
        for (int i = 0, n = this.layouts.size; i < n; ++i) {
            final GlyphLayout layout = this.layouts.get(i);
            for (int ii = 0, nn = layout.runs.size; ii < nn; ++ii) {
                final GlyphLayout.GlyphRun run = layout.runs.get(ii);
                final Array<BitmapFont.Glyph> glyphs = run.glyphs;
                final float colorFloat = BitmapFontCache.tempColor.set(run.color).mul(tint).toFloatBits();
                for (int iii = 0, nnn = glyphs.size; iii < nnn; ++iii) {
                    final BitmapFont.Glyph glyph = glyphs.get(iii);
                    final int page = glyph.page;
                    final int offset = tempGlyphCount[page] * 20 + 2;
                    final int[] array = tempGlyphCount;
                    final int n2 = page;
                    ++array[n2];
                    final float[] vertices = this.pageVertices[page];
                    for (int v = 0; v < 20; v += 5) {
                        vertices[offset + v] = colorFloat;
                    }
                }
            }
        }
    }
    
    public void setAlphas(final float alpha) {
        final int alphaBits = (int)(254.0f * alpha) << 24;
        float prev = 0.0f;
        float newColor = 0.0f;
        for (int j = 0, length = this.pageVertices.length; j < length; ++j) {
            final float[] vertices = this.pageVertices[j];
            for (int i = 2, n = this.idx[j]; i < n; i += 5) {
                final float c = vertices[i];
                if (c == prev && i != 2) {
                    vertices[i] = newColor;
                }
                else {
                    prev = c;
                    int rgba = NumberUtils.floatToIntColor(c);
                    rgba = ((rgba & 0xFFFFFF) | alphaBits);
                    newColor = NumberUtils.intToFloatColor(rgba);
                    vertices[i] = newColor;
                }
            }
        }
    }
    
    public void setColors(final float color) {
        for (int j = 0, length = this.pageVertices.length; j < length; ++j) {
            final float[] vertices = this.pageVertices[j];
            for (int i = 2, n = this.idx[j]; i < n; i += 5) {
                vertices[i] = color;
            }
        }
    }
    
    public void setColors(final Color tint) {
        this.setColors(tint.toFloatBits());
    }
    
    public void setColors(final float r, final float g, final float b, final float a) {
        final int intBits = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        this.setColors(NumberUtils.intToFloatColor(intBits));
    }
    
    public void setColors(final Color tint, final int start, final int end) {
        this.setColors(tint.toFloatBits(), start, end);
    }
    
    public void setColors(final float color, final int start, final int end) {
        if (this.pageVertices.length == 1) {
            final float[] vertices = this.pageVertices[0];
            for (int i = start * 20 + 2, n = end * 20; i < n; i += 5) {
                vertices[i] = color;
            }
            return;
        }
        for (int pageCount = this.pageVertices.length, i = 0; i < pageCount; ++i) {
            final float[] vertices2 = this.pageVertices[i];
            final IntArray glyphIndices = this.pageGlyphIndices[i];
            for (int j = 0, n2 = glyphIndices.size; j < n2; ++j) {
                final int glyphIndex = glyphIndices.items[j];
                if (glyphIndex >= end) {
                    break;
                }
                if (glyphIndex >= start) {
                    for (int off = 0; off < 20; off += 5) {
                        vertices2[off + (j * 20 + 2)] = color;
                    }
                }
            }
        }
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color.set(color);
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
    }
    
    public void draw(final Batch spriteBatch) {
        final Array<TextureRegion> regions = this.font.getRegions();
        for (int j = 0, n = this.pageVertices.length; j < n; ++j) {
            if (this.idx[j] > 0) {
                final float[] vertices = this.pageVertices[j];
                spriteBatch.draw(regions.get(j).getTexture(), vertices, 0, this.idx[j]);
            }
        }
    }
    
    public void draw(final Batch spriteBatch, final int start, final int end) {
        if (this.pageVertices.length == 1) {
            spriteBatch.draw(this.font.getRegion().getTexture(), this.pageVertices[0], start * 20, (end - start) * 20);
            return;
        }
        final Array<TextureRegion> regions = this.font.getRegions();
        for (int i = 0, pageCount = this.pageVertices.length; i < pageCount; ++i) {
            int offset = -1;
            int count = 0;
            final IntArray glyphIndices = this.pageGlyphIndices[i];
            for (int ii = 0, n = glyphIndices.size; ii < n; ++ii) {
                final int glyphIndex = glyphIndices.get(ii);
                if (glyphIndex >= end) {
                    break;
                }
                if (offset == -1 && glyphIndex >= start) {
                    offset = ii;
                }
                if (glyphIndex >= start) {
                    ++count;
                }
            }
            if (offset != -1) {
                if (count != 0) {
                    spriteBatch.draw(regions.get(i).getTexture(), this.pageVertices[i], offset * 20, count * 20);
                }
            }
        }
    }
    
    public void draw(final Batch spriteBatch, final float alphaModulation) {
        if (alphaModulation == 1.0f) {
            this.draw(spriteBatch);
            return;
        }
        final Color color = this.getColor();
        final float oldAlpha = color.a;
        final Color color2 = color;
        color2.a *= alphaModulation;
        this.setColors(color);
        this.draw(spriteBatch);
        color.a = oldAlpha;
        this.setColors(color);
    }
    
    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        Pools.freeAll(this.pooledLayouts, true);
        this.pooledLayouts.clear();
        this.layouts.clear();
        for (int i = 0, n = this.idx.length; i < n; ++i) {
            if (this.pageGlyphIndices != null) {
                this.pageGlyphIndices[i].clear();
            }
            this.idx[i] = 0;
        }
    }
    
    private void requireGlyphs(final GlyphLayout layout) {
        if (this.pageVertices.length == 1) {
            int newGlyphCount = 0;
            for (int i = 0, n = layout.runs.size; i < n; ++i) {
                newGlyphCount += layout.runs.get(i).glyphs.size;
            }
            this.requirePageGlyphs(0, newGlyphCount);
        }
        else {
            final int[] tempGlyphCount = this.tempGlyphCount;
            for (int i = 0, n = tempGlyphCount.length; i < n; ++i) {
                tempGlyphCount[i] = 0;
            }
            for (int i = 0, n = layout.runs.size; i < n; ++i) {
                final Array<BitmapFont.Glyph> glyphs = layout.runs.get(i).glyphs;
                for (int ii = 0, nn = glyphs.size; ii < nn; ++ii) {
                    final int[] array = tempGlyphCount;
                    final int page = glyphs.get(ii).page;
                    ++array[page];
                }
            }
            for (int i = 0, n = tempGlyphCount.length; i < n; ++i) {
                this.requirePageGlyphs(i, tempGlyphCount[i]);
            }
        }
    }
    
    private void requirePageGlyphs(final int page, final int glyphCount) {
        if (this.pageGlyphIndices != null && glyphCount > this.pageGlyphIndices[page].items.length) {
            this.pageGlyphIndices[page].ensureCapacity(glyphCount - this.pageGlyphIndices[page].items.length);
        }
        final int vertexCount = this.idx[page] + glyphCount * 20;
        final float[] vertices = this.pageVertices[page];
        if (vertices == null) {
            this.pageVertices[page] = new float[vertexCount];
        }
        else if (vertices.length < vertexCount) {
            final float[] newVertices = new float[vertexCount];
            System.arraycopy(vertices, 0, newVertices, 0, this.idx[page]);
            this.pageVertices[page] = newVertices;
        }
    }
    
    private void addToCache(final GlyphLayout layout, final float x, final float y) {
        final int pageCount = this.font.regions.size;
        if (this.pageVertices.length < pageCount) {
            final float[][] newPageVertices = new float[pageCount][];
            System.arraycopy(this.pageVertices, 0, newPageVertices, 0, this.pageVertices.length);
            this.pageVertices = newPageVertices;
            final int[] newIdx = new int[pageCount];
            System.arraycopy(this.idx, 0, newIdx, 0, this.idx.length);
            this.idx = newIdx;
            final IntArray[] newPageGlyphIndices = new IntArray[pageCount];
            int pageGlyphIndicesLength = 0;
            if (this.pageGlyphIndices != null) {
                pageGlyphIndicesLength = this.pageGlyphIndices.length;
                System.arraycopy(this.pageGlyphIndices, 0, newPageGlyphIndices, 0, this.pageGlyphIndices.length);
            }
            for (int i = pageGlyphIndicesLength; i < pageCount; ++i) {
                newPageGlyphIndices[i] = new IntArray();
            }
            this.pageGlyphIndices = newPageGlyphIndices;
            this.tempGlyphCount = new int[pageCount];
        }
        this.layouts.add(layout);
        this.requireGlyphs(layout);
        for (int j = 0, n = layout.runs.size; j < n; ++j) {
            final GlyphLayout.GlyphRun run = layout.runs.get(j);
            final Array<BitmapFont.Glyph> glyphs = run.glyphs;
            final FloatArray xAdvances = run.xAdvances;
            final float color = run.color.toFloatBits();
            float gx = x + run.x;
            final float gy = y + run.y;
            for (int ii = 0, nn = glyphs.size; ii < nn; ++ii) {
                final BitmapFont.Glyph glyph = glyphs.get(ii);
                gx += xAdvances.get(ii);
                this.addGlyph(glyph, gx, gy, color);
            }
        }
        this.currentTint = Color.WHITE_FLOAT_BITS;
    }
    
    private void addGlyph(final BitmapFont.Glyph glyph, float x, float y, final float color) {
        final float scaleX = this.font.data.scaleX;
        final float scaleY = this.font.data.scaleY;
        x += glyph.xoffset * scaleX;
        y += glyph.yoffset * scaleY;
        float width = glyph.width * scaleX;
        float height = glyph.height * scaleY;
        final float u = glyph.u;
        final float u2 = glyph.u2;
        final float v = glyph.v;
        final float v2 = glyph.v2;
        if (this.integer) {
            x = (float)Math.round(x);
            y = (float)Math.round(y);
            width = (float)Math.round(width);
            height = (float)Math.round(height);
        }
        final float x2 = x + width;
        final float y2 = y + height;
        final int page = glyph.page;
        int idx = this.idx[page];
        final int[] idx2 = this.idx;
        final int n = page;
        idx2[n] += 20;
        if (this.pageGlyphIndices != null) {
            this.pageGlyphIndices[page].add(this.glyphCount++);
        }
        final float[] vertices = this.pageVertices[page];
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx] = v;
    }
    
    public GlyphLayout setText(final CharSequence str, final float x, final float y) {
        this.clear();
        return this.addText(str, x, y, 0, str.length(), 0.0f, 8, false);
    }
    
    public GlyphLayout setText(final CharSequence str, final float x, final float y, final float targetWidth, final int halign, final boolean wrap) {
        this.clear();
        return this.addText(str, x, y, 0, str.length(), targetWidth, halign, wrap);
    }
    
    public GlyphLayout setText(final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap) {
        this.clear();
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap);
    }
    
    public GlyphLayout setText(final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap, final String truncate) {
        this.clear();
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap, truncate);
    }
    
    public void setText(final GlyphLayout layout, final float x, final float y) {
        this.clear();
        this.addText(layout, x, y);
    }
    
    public GlyphLayout addText(final CharSequence str, final float x, final float y) {
        return this.addText(str, x, y, 0, str.length(), 0.0f, 8, false, null);
    }
    
    public GlyphLayout addText(final CharSequence str, final float x, final float y, final float targetWidth, final int halign, final boolean wrap) {
        return this.addText(str, x, y, 0, str.length(), targetWidth, halign, wrap, null);
    }
    
    public GlyphLayout addText(final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap) {
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap, null);
    }
    
    public GlyphLayout addText(final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap, final String truncate) {
        final GlyphLayout layout = Pools.obtain(GlyphLayout.class);
        this.pooledLayouts.add(layout);
        layout.setText(this.font, str, start, end, this.color, targetWidth, halign, wrap, truncate);
        this.addText(layout, x, y);
        return layout;
    }
    
    public void addText(final GlyphLayout layout, final float x, final float y) {
        this.addToCache(layout, x, y + this.font.data.ascent);
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public BitmapFont getFont() {
        return this.font;
    }
    
    public void setUseIntegerPositions(final boolean use) {
        this.integer = use;
    }
    
    public boolean usesIntegerPositions() {
        return this.integer;
    }
    
    public float[] getVertices() {
        return this.getVertices(0);
    }
    
    public float[] getVertices(final int page) {
        return this.pageVertices[page];
    }
    
    public int getVertexCount(final int page) {
        return this.idx[page];
    }
    
    public Array<GlyphLayout> getLayouts() {
        return this.layouts;
    }
}

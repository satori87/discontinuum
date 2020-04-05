// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class GlyphLayout implements Pool.Poolable
{
    public final Array<GlyphRun> runs;
    public float width;
    public float height;
    private final Array<Color> colorStack;
    
    public GlyphLayout() {
        this.runs = new Array<GlyphRun>();
        this.colorStack = new Array<Color>(4);
    }
    
    public GlyphLayout(final BitmapFont font, final CharSequence str) {
        this.runs = new Array<GlyphRun>();
        this.colorStack = new Array<Color>(4);
        this.setText(font, str);
    }
    
    public GlyphLayout(final BitmapFont font, final CharSequence str, final Color color, final float targetWidth, final int halign, final boolean wrap) {
        this.runs = new Array<GlyphRun>();
        this.colorStack = new Array<Color>(4);
        this.setText(font, str, color, targetWidth, halign, wrap);
    }
    
    public GlyphLayout(final BitmapFont font, final CharSequence str, final int start, final int end, final Color color, final float targetWidth, final int halign, final boolean wrap, final String truncate) {
        this.runs = new Array<GlyphRun>();
        this.colorStack = new Array<Color>(4);
        this.setText(font, str, start, end, color, targetWidth, halign, wrap, truncate);
    }
    
    public void setText(final BitmapFont font, final CharSequence str) {
        this.setText(font, str, 0, str.length(), font.getColor(), 0.0f, 8, false, null);
    }
    
    public void setText(final BitmapFont font, final CharSequence str, final Color color, final float targetWidth, final int halign, final boolean wrap) {
        this.setText(font, str, 0, str.length(), color, targetWidth, halign, wrap, null);
    }
    
    public void setText(final BitmapFont font, final CharSequence str, int start, final int end, Color color, final float targetWidth, final int halign, boolean wrap, final String truncate) {
        final BitmapFont.BitmapFontData fontData = font.data;
        if (truncate != null) {
            wrap = true;
        }
        else if (targetWidth <= fontData.spaceXadvance * 3.0f) {
            wrap = false;
        }
        final boolean markupEnabled = fontData.markupEnabled;
        final Pool<GlyphRun> glyphRunPool = Pools.get(GlyphRun.class);
        final Array<GlyphRun> runs = this.runs;
        glyphRunPool.freeAll(runs);
        runs.clear();
        float x = 0.0f;
        float y = 0.0f;
        float width = 0.0f;
        int lines = 0;
        int blankLines = 0;
        BitmapFont.Glyph lastGlyph = null;
        final Array<Color> colorStack = this.colorStack;
        Color nextColor = color;
        colorStack.add(color);
        final Pool<Color> colorPool = Pools.get(Color.class);
        int runStart = start;
    Label_1246:
        while (true) {
            int runEnd = -1;
            boolean newline = false;
            if (start == end) {
                if (runStart == end) {
                    break;
                }
                runEnd = end;
            }
            else {
                switch (str.charAt(start++)) {
                    case '\n': {
                        runEnd = start - 1;
                        newline = true;
                        break;
                    }
                    case '[': {
                        if (!markupEnabled) {
                            break;
                        }
                        final int length = this.parseColorMarkup(str, start, end, colorPool);
                        if (length >= 0) {
                            runEnd = start - 1;
                            start += length + 1;
                            nextColor = colorStack.peek();
                            break;
                        }
                        if (length == -2) {
                            ++start;
                            continue;
                        }
                        break;
                    }
                }
            }
            if (runEnd != -1) {
                if (runEnd != runStart) {
                    GlyphRun run = glyphRunPool.obtain();
                    run.color.set(color);
                    fontData.getGlyphs(run, str, runStart, runEnd, lastGlyph);
                    if (run.glyphs.size == 0) {
                        glyphRunPool.free(run);
                    }
                    else {
                        if (lastGlyph != null) {
                            x -= (lastGlyph.fixedWidth ? (lastGlyph.xadvance * fontData.scaleX) : ((lastGlyph.width + lastGlyph.xoffset) * fontData.scaleX - fontData.padRight));
                        }
                        lastGlyph = run.glyphs.peek();
                        run.x = x;
                        run.y = y;
                        if (newline || runEnd == end) {
                            this.adjustLastGlyph(fontData, run);
                        }
                        runs.add(run);
                        float[] xAdvances = run.xAdvances.items;
                        int n = run.xAdvances.size;
                        if (!wrap) {
                            float runWidth = 0.0f;
                            for (int i = 0; i < n; ++i) {
                                runWidth += xAdvances[i];
                            }
                            x += runWidth;
                            run.width = runWidth;
                        }
                        else {
                            x += xAdvances[0];
                            run.width = xAdvances[0];
                            if (n >= 1) {
                                x += xAdvances[1];
                                final GlyphRun glyphRun = run;
                                glyphRun.width += xAdvances[1];
                                for (int j = 2; j < n; ++j) {
                                    final BitmapFont.Glyph glyph = run.glyphs.get(j - 1);
                                    final float glyphWidth = (glyph.width + glyph.xoffset) * fontData.scaleX - fontData.padRight;
                                    if (x + glyphWidth <= targetWidth) {
                                        x += xAdvances[j];
                                        final GlyphRun glyphRun2 = run;
                                        glyphRun2.width += xAdvances[j];
                                    }
                                    else {
                                        if (truncate != null) {
                                            this.truncate(fontData, run, targetWidth, truncate, j, glyphRunPool);
                                            x = run.x + run.width;
                                            break Label_1246;
                                        }
                                        int wrapIndex = fontData.getWrapIndex(run.glyphs, j);
                                        if ((run.x == 0.0f && wrapIndex == 0) || wrapIndex >= run.glyphs.size) {
                                            wrapIndex = j - 1;
                                        }
                                        GlyphRun next;
                                        if (wrapIndex == 0) {
                                            next = run;
                                            run.width = 0.0f;
                                            for (int glyphCount = run.glyphs.size; wrapIndex < glyphCount && fontData.isWhitespace((char)run.glyphs.get(wrapIndex).id); ++wrapIndex) {}
                                            if (wrapIndex > 0) {
                                                run.glyphs.removeRange(0, wrapIndex - 1);
                                                run.xAdvances.removeRange(1, wrapIndex);
                                            }
                                            run.xAdvances.set(0, -run.glyphs.first().xoffset * fontData.scaleX - fontData.padLeft);
                                            if (runs.size > 1) {
                                                final GlyphRun previous = runs.get(runs.size - 2);
                                                int lastIndex;
                                                for (lastIndex = previous.glyphs.size - 1; lastIndex > 0; --lastIndex) {
                                                    final BitmapFont.Glyph g = previous.glyphs.get(lastIndex);
                                                    if (!fontData.isWhitespace((char)g.id)) {
                                                        break;
                                                    }
                                                    final GlyphRun glyphRun3 = previous;
                                                    glyphRun3.width -= previous.xAdvances.get(lastIndex + 1);
                                                }
                                                previous.glyphs.truncate(lastIndex + 1);
                                                previous.xAdvances.truncate(lastIndex + 2);
                                                this.adjustLastGlyph(fontData, previous);
                                                width = Math.max(width, previous.x + previous.width);
                                            }
                                        }
                                        else {
                                            next = this.wrap(fontData, run, glyphRunPool, wrapIndex, j);
                                            width = Math.max(width, run.x + run.width);
                                            if (next == null) {
                                                x = 0.0f;
                                                y += fontData.down;
                                                ++lines;
                                                lastGlyph = null;
                                                break;
                                            }
                                            runs.add(next);
                                        }
                                        n = next.xAdvances.size;
                                        xAdvances = next.xAdvances.items;
                                        x = xAdvances[0];
                                        if (n > 1) {
                                            x += xAdvances[1];
                                        }
                                        final GlyphRun glyphRun4 = next;
                                        glyphRun4.width += x;
                                        y += fontData.down;
                                        ++lines;
                                        next.x = 0.0f;
                                        next.y = y;
                                        j = 1;
                                        run = next;
                                        lastGlyph = null;
                                    }
                                }
                            }
                        }
                    }
                }
                if (newline) {
                    width = Math.max(width, x);
                    x = 0.0f;
                    float down = fontData.down;
                    if (runEnd == runStart) {
                        down *= fontData.blankLineScale;
                        ++blankLines;
                    }
                    else {
                        ++lines;
                    }
                    y += down;
                    lastGlyph = null;
                }
                runStart = start;
                color = nextColor;
            }
        }
        width = Math.max(width, x);
        for (int k = 1, n2 = colorStack.size; k < n2; ++k) {
            colorPool.free(colorStack.get(k));
        }
        colorStack.clear();
        if ((halign & 0x8) == 0x0) {
            final boolean center = (halign & 0x1) != 0x0;
            float lineWidth = 0.0f;
            float lineY = -2.14748365E9f;
            int lineStart = 0;
            final int n = runs.size;
            for (int j = 0; j < n; ++j) {
                final GlyphRun run2 = runs.get(j);
                if (run2.y != lineY) {
                    lineY = run2.y;
                    float shift = targetWidth - lineWidth;
                    if (center) {
                        shift /= 2.0f;
                    }
                    while (lineStart < j) {
                        final GlyphRun glyphRun5 = runs.get(lineStart++);
                        glyphRun5.x += shift;
                    }
                    lineWidth = 0.0f;
                }
                lineWidth = Math.max(lineWidth, run2.x + run2.width);
            }
            float shift2 = targetWidth - lineWidth;
            if (center) {
                shift2 /= 2.0f;
            }
            while (lineStart < n) {
                final GlyphRun glyphRun6 = runs.get(lineStart++);
                glyphRun6.x += shift2;
            }
        }
        this.width = width;
        this.height = fontData.capHeight - lines * fontData.down - blankLines * fontData.down * fontData.blankLineScale;
    }
    
    private void truncate(final BitmapFont.BitmapFontData fontData, final GlyphRun run, float targetWidth, final String truncate, final int widthIndex, final Pool<GlyphRun> glyphRunPool) {
        final GlyphRun truncateRun = glyphRunPool.obtain();
        fontData.getGlyphs(truncateRun, truncate, 0, truncate.length(), null);
        float truncateWidth = 0.0f;
        if (truncateRun.xAdvances.size > 0) {
            this.adjustLastGlyph(fontData, truncateRun);
            for (int i = 1, n = truncateRun.xAdvances.size; i < n; ++i) {
                truncateWidth += truncateRun.xAdvances.get(i);
            }
        }
        targetWidth -= truncateWidth;
        int count = 0;
        float width = run.x;
        while (count < run.xAdvances.size) {
            final float xAdvance = run.xAdvances.get(count);
            width += xAdvance;
            if (width > targetWidth) {
                run.width = width - run.x - xAdvance;
                break;
            }
            ++count;
        }
        if (count > 1) {
            run.glyphs.truncate(count - 1);
            run.xAdvances.truncate(count);
            this.adjustLastGlyph(fontData, run);
            if (truncateRun.xAdvances.size > 0) {
                run.xAdvances.addAll(truncateRun.xAdvances, 1, truncateRun.xAdvances.size - 1);
            }
        }
        else {
            run.glyphs.clear();
            run.xAdvances.clear();
            run.xAdvances.addAll(truncateRun.xAdvances);
            if (truncateRun.xAdvances.size > 0) {
                run.width += truncateRun.xAdvances.get(0);
            }
        }
        run.glyphs.addAll(truncateRun.glyphs);
        run.width += truncateWidth;
        glyphRunPool.free(truncateRun);
    }
    
    private GlyphRun wrap(final BitmapFont.BitmapFontData fontData, final GlyphRun first, final Pool<GlyphRun> glyphRunPool, final int wrapIndex, int widthIndex) {
        final Array<BitmapFont.Glyph> glyphs2 = first.glyphs;
        final int glyphCount = first.glyphs.size;
        final FloatArray xAdvances2 = first.xAdvances;
        int firstEnd;
        for (firstEnd = wrapIndex; firstEnd > 0 && fontData.isWhitespace((char)glyphs2.get(firstEnd - 1).id); --firstEnd) {}
        while (true) {
            for (int secondStart = wrapIndex; secondStart < glyphCount; ++secondStart) {
                if (!fontData.isWhitespace((char)glyphs2.get(secondStart).id)) {
                    while (widthIndex < firstEnd) {
                        first.width += xAdvances2.get(widthIndex++);
                    }
                    for (int n = firstEnd + 1; widthIndex > n; first.width -= xAdvances2.get(--widthIndex)) {}
                    GlyphRun second = null;
                    if (secondStart < glyphCount) {
                        second = glyphRunPool.obtain();
                        second.color.set(first.color);
                        final Array<BitmapFont.Glyph> glyphs3 = second.glyphs;
                        glyphs3.addAll(glyphs2, 0, firstEnd);
                        glyphs2.removeRange(0, secondStart - 1);
                        first.glyphs = glyphs3;
                        second.glyphs = glyphs2;
                        final FloatArray xAdvances3 = second.xAdvances;
                        xAdvances3.addAll(xAdvances2, 0, firstEnd + 1);
                        xAdvances2.removeRange(1, secondStart);
                        xAdvances2.set(0, -glyphs2.first().xoffset * fontData.scaleX - fontData.padLeft);
                        first.xAdvances = xAdvances3;
                        second.xAdvances = xAdvances2;
                    }
                    else {
                        glyphs2.truncate(firstEnd);
                        xAdvances2.truncate(firstEnd + 1);
                    }
                    if (firstEnd == 0) {
                        glyphRunPool.free(first);
                        this.runs.pop();
                    }
                    else {
                        this.adjustLastGlyph(fontData, first);
                    }
                    return second;
                }
            }
            continue;
        }
    }
    
    private void adjustLastGlyph(final BitmapFont.BitmapFontData fontData, final GlyphRun run) {
        final BitmapFont.Glyph last = run.glyphs.peek();
        if (last.fixedWidth) {
            return;
        }
        final float width = (last.width + last.xoffset) * fontData.scaleX - fontData.padRight;
        run.width += width - run.xAdvances.peek();
        run.xAdvances.set(run.xAdvances.size - 1, width);
    }
    
    private int parseColorMarkup(final CharSequence str, final int start, final int end, final Pool<Color> colorPool) {
        if (start == end) {
            return -1;
        }
        switch (str.charAt(start)) {
            case '#': {
                int colorInt = 0;
                int i = start + 1;
                while (i < end) {
                    final char ch = str.charAt(i);
                    if (ch == ']') {
                        if (i < start + 2) {
                            break;
                        }
                        if (i > start + 9) {
                            break;
                        }
                        if (i - start <= 7) {
                            for (int ii = 0, nn = 9 - (i - start); ii < nn; ++ii) {
                                colorInt <<= 4;
                            }
                            colorInt |= 0xFF;
                        }
                        final Color color = colorPool.obtain();
                        this.colorStack.add(color);
                        Color.rgba8888ToColor(color, colorInt);
                        return i - start;
                    }
                    else {
                        if (ch >= '0' && ch <= '9') {
                            colorInt = colorInt * 16 + (ch - '0');
                        }
                        else if (ch >= 'a' && ch <= 'f') {
                            colorInt = colorInt * 16 + (ch - 'W');
                        }
                        else {
                            if (ch < 'A' || ch > 'F') {
                                break;
                            }
                            colorInt = colorInt * 16 + (ch - '7');
                        }
                        ++i;
                    }
                }
                return -1;
            }
            case '[': {
                return -2;
            }
            case ']': {
                if (this.colorStack.size > 1) {
                    colorPool.free(this.colorStack.pop());
                }
                return 0;
            }
            default: {
                final int colorStart = start;
                int i = start + 1;
                while (i < end) {
                    final char ch = str.charAt(i);
                    if (ch != ']') {
                        ++i;
                    }
                    else {
                        final Color namedColor = Colors.get(str.subSequence(colorStart, i).toString());
                        if (namedColor == null) {
                            return -1;
                        }
                        final Color color2 = colorPool.obtain();
                        this.colorStack.add(color2);
                        color2.set(namedColor);
                        return i - start;
                    }
                }
                return -1;
            }
        }
    }
    
    @Override
    public void reset() {
        Pools.get(GlyphRun.class).freeAll(this.runs);
        this.runs.clear();
        this.width = 0.0f;
        this.height = 0.0f;
    }
    
    @Override
    public String toString() {
        if (this.runs.size == 0) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder(128);
        buffer.append(this.width);
        buffer.append('x');
        buffer.append(this.height);
        buffer.append('\n');
        for (int i = 0, n = this.runs.size; i < n; ++i) {
            buffer.append(this.runs.get(i).toString());
            buffer.append('\n');
        }
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }
    
    public static class GlyphRun implements Pool.Poolable
    {
        public Array<BitmapFont.Glyph> glyphs;
        public FloatArray xAdvances;
        public float x;
        public float y;
        public float width;
        public final Color color;
        
        public GlyphRun() {
            this.glyphs = new Array<BitmapFont.Glyph>();
            this.xAdvances = new FloatArray();
            this.color = new Color();
        }
        
        @Override
        public void reset() {
            this.glyphs.clear();
            this.xAdvances.clear();
            this.width = 0.0f;
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder(this.glyphs.size);
            final Array<BitmapFont.Glyph> glyphs = this.glyphs;
            for (int i = 0, n = glyphs.size; i < n; ++i) {
                final BitmapFont.Glyph g = glyphs.get(i);
                buffer.append((char)g.id);
            }
            buffer.append(", #");
            buffer.append(this.color);
            buffer.append(", ");
            buffer.append(this.x);
            buffer.append(", ");
            buffer.append(this.y);
            buffer.append(", ");
            buffer.append(this.width);
            return buffer.toString();
        }
    }
}

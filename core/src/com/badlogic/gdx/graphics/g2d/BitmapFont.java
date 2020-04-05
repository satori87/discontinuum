// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.FloatArray;
import java.util.regex.Matcher;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class BitmapFont implements Disposable
{
    private static final int LOG2_PAGE_SIZE = 9;
    private static final int PAGE_SIZE = 512;
    private static final int PAGES = 128;
    final BitmapFontData data;
    Array<TextureRegion> regions;
    private final BitmapFontCache cache;
    private boolean flipped;
    boolean integer;
    private boolean ownsTexture;
    
    public BitmapFont() {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), false, true);
    }
    
    public BitmapFont(final boolean flip) {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), flip, true);
    }
    
    public BitmapFont(final FileHandle fontFile, final TextureRegion region) {
        this(fontFile, region, false);
    }
    
    public BitmapFont(final FileHandle fontFile, final TextureRegion region, final boolean flip) {
        this(new BitmapFontData(fontFile, flip), region, true);
    }
    
    public BitmapFont(final FileHandle fontFile) {
        this(fontFile, false);
    }
    
    public BitmapFont(final FileHandle fontFile, final boolean flip) {
        this(new BitmapFontData(fontFile, flip), (TextureRegion)null, true);
    }
    
    public BitmapFont(final FileHandle fontFile, final FileHandle imageFile, final boolean flip) {
        this(fontFile, imageFile, flip, true);
    }
    
    public BitmapFont(final FileHandle fontFile, final FileHandle imageFile, final boolean flip, final boolean integer) {
        this(new BitmapFontData(fontFile, flip), new TextureRegion(new Texture(imageFile, false)), integer);
        this.ownsTexture = true;
    }
    
    public BitmapFont(final BitmapFontData data, final TextureRegion region, final boolean integer) {
        this(data, (region != null) ? Array.with(region) : null, integer);
    }
    
    public BitmapFont(final BitmapFontData data, final Array<TextureRegion> pageRegions, final boolean integer) {
        this.flipped = data.flipped;
        this.data = data;
        this.integer = integer;
        if (pageRegions == null || pageRegions.size == 0) {
            final int n = data.imagePaths.length;
            this.regions = new Array<TextureRegion>(n);
            for (int i = 0; i < n; ++i) {
                FileHandle file;
                if (data.fontFile == null) {
                    file = Gdx.files.internal(data.imagePaths[i]);
                }
                else {
                    file = Gdx.files.getFileHandle(data.imagePaths[i], data.fontFile.type());
                }
                this.regions.add(new TextureRegion(new Texture(file, false)));
            }
            this.ownsTexture = true;
        }
        else {
            this.regions = pageRegions;
            this.ownsTexture = false;
        }
        this.cache = this.newFontCache();
        this.load(data);
    }
    
    protected void load(final BitmapFontData data) {
        Glyph[][] glyphs;
        for (int length = (glyphs = data.glyphs).length, i = 0; i < length; ++i) {
            final Glyph[] page = glyphs[i];
            if (page != null) {
                Glyph[] array;
                for (int length2 = (array = page).length, j = 0; j < length2; ++j) {
                    final Glyph glyph = array[j];
                    if (glyph != null) {
                        data.setGlyphRegion(glyph, this.regions.get(glyph.page));
                    }
                }
            }
        }
        if (data.missingGlyph != null) {
            data.setGlyphRegion(data.missingGlyph, this.regions.get(data.missingGlyph.page));
        }
    }
    
    public GlyphLayout draw(final Batch batch, final CharSequence str, final float x, final float y) {
        this.cache.clear();
        final GlyphLayout layout = this.cache.addText(str, x, y);
        this.cache.draw(batch);
        return layout;
    }
    
    public GlyphLayout draw(final Batch batch, final CharSequence str, final float x, final float y, final float targetWidth, final int halign, final boolean wrap) {
        this.cache.clear();
        final GlyphLayout layout = this.cache.addText(str, x, y, targetWidth, halign, wrap);
        this.cache.draw(batch);
        return layout;
    }
    
    public GlyphLayout draw(final Batch batch, final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap) {
        this.cache.clear();
        final GlyphLayout layout = this.cache.addText(str, x, y, start, end, targetWidth, halign, wrap);
        this.cache.draw(batch);
        return layout;
    }
    
    public GlyphLayout draw(final Batch batch, final CharSequence str, final float x, final float y, final int start, final int end, final float targetWidth, final int halign, final boolean wrap, final String truncate) {
        this.cache.clear();
        final GlyphLayout layout = this.cache.addText(str, x, y, start, end, targetWidth, halign, wrap, truncate);
        this.cache.draw(batch);
        return layout;
    }
    
    public void draw(final Batch batch, final GlyphLayout layout, final float x, final float y) {
        this.cache.clear();
        this.cache.addText(layout, x, y);
        this.cache.draw(batch);
    }
    
    public Color getColor() {
        return this.cache.getColor();
    }
    
    public void setColor(final Color color) {
        this.cache.getColor().set(color);
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.cache.getColor().set(r, g, b, a);
    }
    
    public float getScaleX() {
        return this.data.scaleX;
    }
    
    public float getScaleY() {
        return this.data.scaleY;
    }
    
    public TextureRegion getRegion() {
        return this.regions.first();
    }
    
    public Array<TextureRegion> getRegions() {
        return this.regions;
    }
    
    public TextureRegion getRegion(final int index) {
        return this.regions.get(index);
    }
    
    public float getLineHeight() {
        return this.data.lineHeight;
    }
    
    public float getSpaceXadvance() {
        return this.data.spaceXadvance;
    }
    
    public float getXHeight() {
        return this.data.xHeight;
    }
    
    public float getCapHeight() {
        return this.data.capHeight;
    }
    
    public float getAscent() {
        return this.data.ascent;
    }
    
    public float getDescent() {
        return this.data.descent;
    }
    
    public boolean isFlipped() {
        return this.flipped;
    }
    
    @Override
    public void dispose() {
        if (this.ownsTexture) {
            for (int i = 0; i < this.regions.size; ++i) {
                this.regions.get(i).getTexture().dispose();
            }
        }
    }
    
    public void setFixedWidthGlyphs(final CharSequence glyphs) {
        final BitmapFontData data = this.data;
        int maxAdvance = 0;
        for (int index = 0, end = glyphs.length(); index < end; ++index) {
            final Glyph g = data.getGlyph(glyphs.charAt(index));
            if (g != null && g.xadvance > maxAdvance) {
                maxAdvance = g.xadvance;
            }
        }
        for (int index = 0, end = glyphs.length(); index < end; ++index) {
            final Glyph g = data.getGlyph(glyphs.charAt(index));
            if (g != null) {
                final Glyph glyph = g;
                glyph.xoffset += Math.round((float)((maxAdvance - g.xadvance) / 2));
                g.xadvance = maxAdvance;
                g.kerning = null;
                g.fixedWidth = true;
            }
        }
    }
    
    public void setUseIntegerPositions(final boolean integer) {
        this.integer = integer;
        this.cache.setUseIntegerPositions(integer);
    }
    
    public boolean usesIntegerPositions() {
        return this.integer;
    }
    
    public BitmapFontCache getCache() {
        return this.cache;
    }
    
    public BitmapFontData getData() {
        return this.data;
    }
    
    public boolean ownsTexture() {
        return this.ownsTexture;
    }
    
    public void setOwnsTexture(final boolean ownsTexture) {
        this.ownsTexture = ownsTexture;
    }
    
    public BitmapFontCache newFontCache() {
        return new BitmapFontCache(this, this.integer);
    }
    
    @Override
    public String toString() {
        if (this.data.fontFile != null) {
            return this.data.fontFile.nameWithoutExtension();
        }
        return super.toString();
    }
    
    static int indexOf(final CharSequence text, final char ch, int start) {
        int n;
        for (n = text.length(); start < n; ++start) {
            if (text.charAt(start) == ch) {
                return start;
            }
        }
        return n;
    }
    
    public static class Glyph
    {
        public int id;
        public int srcX;
        public int srcY;
        public int width;
        public int height;
        public float u;
        public float v;
        public float u2;
        public float v2;
        public int xoffset;
        public int yoffset;
        public int xadvance;
        public byte[][] kerning;
        public boolean fixedWidth;
        public int page;
        
        public Glyph() {
            this.page = 0;
        }
        
        public int getKerning(final char ch) {
            if (this.kerning != null) {
                final byte[] page = this.kerning[ch >>> 9];
                if (page != null) {
                    return page[ch & '\u01ff'];
                }
            }
            return 0;
        }
        
        public void setKerning(final int ch, final int value) {
            if (this.kerning == null) {
                this.kerning = new byte[128][];
            }
            byte[] page = this.kerning[ch >>> 9];
            if (page == null) {
                page = (this.kerning[ch >>> 9] = new byte[512]);
            }
            page[ch & 0x1FF] = (byte)value;
        }
        
        @Override
        public String toString() {
            return Character.toString((char)this.id);
        }
    }
    
    public static class BitmapFontData
    {
        public String[] imagePaths;
        public FileHandle fontFile;
        public boolean flipped;
        public float padTop;
        public float padRight;
        public float padBottom;
        public float padLeft;
        public float lineHeight;
        public float capHeight;
        public float ascent;
        public float descent;
        public float down;
        public float blankLineScale;
        public float scaleX;
        public float scaleY;
        public boolean markupEnabled;
        public float cursorX;
        public final Glyph[][] glyphs;
        public Glyph missingGlyph;
        public float spaceXadvance;
        public float xHeight;
        public char[] breakChars;
        public char[] xChars;
        public char[] capChars;
        
        public BitmapFontData() {
            this.capHeight = 1.0f;
            this.blankLineScale = 1.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.glyphs = new Glyph[128][];
            this.xHeight = 1.0f;
            this.xChars = new char[] { 'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z' };
            this.capChars = new char[] { 'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        }
        
        public BitmapFontData(final FileHandle fontFile, final boolean flip) {
            this.capHeight = 1.0f;
            this.blankLineScale = 1.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.glyphs = new Glyph[128][];
            this.xHeight = 1.0f;
            this.xChars = new char[] { 'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z' };
            this.capChars = new char[] { 'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
            this.load(this.fontFile = fontFile, this.flipped = flip);
        }
        
        public void load(final FileHandle fontFile, final boolean flip) {
            if (this.imagePaths != null) {
                throw new IllegalStateException("Already loaded.");
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fontFile.read()), 512);
            try {
                String line = reader.readLine();
                if (line == null) {
                    throw new GdxRuntimeException("File is empty.");
                }
                line = line.substring(line.indexOf("padding=") + 8);
                final String[] padding = line.substring(0, line.indexOf(32)).split(",", 4);
                if (padding.length != 4) {
                    throw new GdxRuntimeException("Invalid padding.");
                }
                this.padTop = (float)Integer.parseInt(padding[0]);
                this.padRight = (float)Integer.parseInt(padding[1]);
                this.padBottom = (float)Integer.parseInt(padding[2]);
                this.padLeft = (float)Integer.parseInt(padding[3]);
                final float padY = this.padTop + this.padBottom;
                line = reader.readLine();
                if (line == null) {
                    throw new GdxRuntimeException("Missing common header.");
                }
                final String[] common = line.split(" ", 7);
                if (common.length < 3) {
                    throw new GdxRuntimeException("Invalid common header.");
                }
                if (!common[1].startsWith("lineHeight=")) {
                    throw new GdxRuntimeException("Missing: lineHeight");
                }
                this.lineHeight = (float)Integer.parseInt(common[1].substring(11));
                if (!common[2].startsWith("base=")) {
                    throw new GdxRuntimeException("Missing: base");
                }
                final float baseLine = (float)Integer.parseInt(common[2].substring(5));
                int pageCount = 1;
                if (common.length >= 6 && common[5] != null && common[5].startsWith("pages=")) {
                    try {
                        pageCount = Math.max(1, Integer.parseInt(common[5].substring(6)));
                    }
                    catch (NumberFormatException ex3) {}
                }
                this.imagePaths = new String[pageCount];
                for (int p = 0; p < pageCount; ++p) {
                    line = reader.readLine();
                    if (line == null) {
                        throw new GdxRuntimeException("Missing additional page definitions.");
                    }
                    Matcher matcher = Pattern.compile(".*id=(\\d+)").matcher(line);
                    if (matcher.find()) {
                        final String id = matcher.group(1);
                        try {
                            final int pageID = Integer.parseInt(id);
                            if (pageID != p) {
                                throw new GdxRuntimeException("Page IDs must be indices starting at 0: " + id);
                            }
                        }
                        catch (NumberFormatException ex) {
                            throw new GdxRuntimeException("Invalid page id: " + id, ex);
                        }
                    }
                    matcher = Pattern.compile(".*file=\"?([^\"]+)\"?").matcher(line);
                    if (!matcher.find()) {
                        throw new GdxRuntimeException("Missing: file");
                    }
                    final String fileName = matcher.group(1);
                    this.imagePaths[p] = fontFile.parent().child(fileName).path().replaceAll("\\\\", "/");
                }
                this.descent = 0.0f;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.startsWith("kernings ")) {
                        break;
                    }
                    if (!line.startsWith("char ")) {
                        continue;
                    }
                    final Glyph glyph = new Glyph();
                    final StringTokenizer tokens = new StringTokenizer(line, " =");
                    tokens.nextToken();
                    tokens.nextToken();
                    final int ch = Integer.parseInt(tokens.nextToken());
                    if (ch <= 0) {
                        this.missingGlyph = glyph;
                    }
                    else {
                        if (ch > 65535) {
                            continue;
                        }
                        this.setGlyph(ch, glyph);
                    }
                    glyph.id = ch;
                    tokens.nextToken();
                    glyph.srcX = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.srcY = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.width = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.height = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    glyph.xoffset = Integer.parseInt(tokens.nextToken());
                    tokens.nextToken();
                    if (flip) {
                        glyph.yoffset = Integer.parseInt(tokens.nextToken());
                    }
                    else {
                        glyph.yoffset = -(glyph.height + Integer.parseInt(tokens.nextToken()));
                    }
                    tokens.nextToken();
                    glyph.xadvance = Integer.parseInt(tokens.nextToken());
                    if (tokens.hasMoreTokens()) {
                        tokens.nextToken();
                    }
                    if (tokens.hasMoreTokens()) {
                        try {
                            glyph.page = Integer.parseInt(tokens.nextToken());
                        }
                        catch (NumberFormatException ex4) {}
                    }
                    if (glyph.width <= 0 || glyph.height <= 0) {
                        continue;
                    }
                    this.descent = Math.min(baseLine + glyph.yoffset, this.descent);
                }
                this.descent += this.padBottom;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (!line.startsWith("kerning ")) {
                        break;
                    }
                    final StringTokenizer tokens2 = new StringTokenizer(line, " =");
                    tokens2.nextToken();
                    tokens2.nextToken();
                    final int first = Integer.parseInt(tokens2.nextToken());
                    tokens2.nextToken();
                    final int second = Integer.parseInt(tokens2.nextToken());
                    if (first < 0 || first > 65535 || second < 0) {
                        continue;
                    }
                    if (second > 65535) {
                        continue;
                    }
                    final Glyph glyph2 = this.getGlyph((char)first);
                    tokens2.nextToken();
                    final int amount = Integer.parseInt(tokens2.nextToken());
                    if (glyph2 == null) {
                        continue;
                    }
                    glyph2.setKerning(second, amount);
                }
                Glyph spaceGlyph = this.getGlyph(' ');
                if (spaceGlyph == null) {
                    spaceGlyph = new Glyph();
                    spaceGlyph.id = 32;
                    Glyph xadvanceGlyph = this.getGlyph('l');
                    if (xadvanceGlyph == null) {
                        xadvanceGlyph = this.getFirstGlyph();
                    }
                    spaceGlyph.xadvance = xadvanceGlyph.xadvance;
                    this.setGlyph(32, spaceGlyph);
                }
                if (spaceGlyph.width == 0) {
                    spaceGlyph.width = (int)(this.padLeft + spaceGlyph.xadvance + this.padRight);
                    spaceGlyph.xoffset = (int)(-this.padLeft);
                }
                this.spaceXadvance = (float)spaceGlyph.xadvance;
                Glyph xGlyph = null;
                char[] xChars;
                for (int length = (xChars = this.xChars).length, i = 0; i < length; ++i) {
                    final char xChar = xChars[i];
                    xGlyph = this.getGlyph(xChar);
                    if (xGlyph != null) {
                        break;
                    }
                }
                if (xGlyph == null) {
                    xGlyph = this.getFirstGlyph();
                }
                this.xHeight = xGlyph.height - padY;
                Glyph capGlyph = null;
                char[] capChars;
                for (int length2 = (capChars = this.capChars).length, j = 0; j < length2; ++j) {
                    final char capChar = capChars[j];
                    capGlyph = this.getGlyph(capChar);
                    if (capGlyph != null) {
                        break;
                    }
                }
                if (capGlyph == null) {
                    Glyph[][] glyphs;
                    for (int length3 = (glyphs = this.glyphs).length, k = 0; k < length3; ++k) {
                        final Glyph[] page = glyphs[k];
                        if (page != null) {
                            Glyph[] array;
                            for (int length4 = (array = page).length, l = 0; l < length4; ++l) {
                                final Glyph glyph3 = array[l];
                                if (glyph3 != null && glyph3.height != 0) {
                                    if (glyph3.width != 0) {
                                        this.capHeight = Math.max(this.capHeight, (float)glyph3.height);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    this.capHeight = (float)capGlyph.height;
                }
                this.capHeight -= padY;
                this.ascent = baseLine - this.capHeight;
                this.down = -this.lineHeight;
                if (flip) {
                    this.ascent = -this.ascent;
                    this.down = -this.down;
                }
            }
            catch (Exception ex2) {
                throw new GdxRuntimeException("Error loading font file: " + fontFile, ex2);
            }
            finally {
                StreamUtils.closeQuietly(reader);
            }
            StreamUtils.closeQuietly(reader);
        }
        
        public void setGlyphRegion(final Glyph glyph, final TextureRegion region) {
            final Texture texture = region.getTexture();
            final float invTexWidth = 1.0f / texture.getWidth();
            final float invTexHeight = 1.0f / texture.getHeight();
            float offsetX = 0.0f;
            float offsetY = 0.0f;
            final float u = region.u;
            final float v = region.v;
            final float regionWidth = (float)region.getRegionWidth();
            final float regionHeight = (float)region.getRegionHeight();
            if (region instanceof TextureAtlas.AtlasRegion) {
                final TextureAtlas.AtlasRegion atlasRegion = (TextureAtlas.AtlasRegion)region;
                offsetX = atlasRegion.offsetX;
                offsetY = atlasRegion.originalHeight - atlasRegion.packedHeight - atlasRegion.offsetY;
            }
            float x = (float)glyph.srcX;
            float x2 = (float)(glyph.srcX + glyph.width);
            float y = (float)glyph.srcY;
            float y2 = (float)(glyph.srcY + glyph.height);
            if (offsetX > 0.0f) {
                x -= offsetX;
                if (x < 0.0f) {
                    glyph.width += (int)x;
                    glyph.xoffset -= (int)x;
                    x = 0.0f;
                }
                x2 -= offsetX;
                if (x2 > regionWidth) {
                    glyph.width -= (int)(x2 - regionWidth);
                    x2 = regionWidth;
                }
            }
            if (offsetY > 0.0f) {
                y -= offsetY;
                if (y < 0.0f) {
                    glyph.height += (int)y;
                    if (glyph.height < 0) {
                        glyph.height = 0;
                    }
                    y = 0.0f;
                }
                y2 -= offsetY;
                if (y2 > regionHeight) {
                    final float amount = y2 - regionHeight;
                    glyph.height -= (int)amount;
                    glyph.yoffset += (int)amount;
                    y2 = regionHeight;
                }
            }
            glyph.u = u + x * invTexWidth;
            glyph.u2 = u + x2 * invTexWidth;
            if (this.flipped) {
                glyph.v = v + y * invTexHeight;
                glyph.v2 = v + y2 * invTexHeight;
            }
            else {
                glyph.v2 = v + y * invTexHeight;
                glyph.v = v + y2 * invTexHeight;
            }
        }
        
        public void setLineHeight(final float height) {
            this.lineHeight = height * this.scaleY;
            this.down = (this.flipped ? this.lineHeight : (-this.lineHeight));
        }
        
        public void setGlyph(final int ch, final Glyph glyph) {
            Glyph[] page = this.glyphs[ch / 512];
            if (page == null) {
                page = (this.glyphs[ch / 512] = new Glyph[512]);
            }
            page[ch & 0x1FF] = glyph;
        }
        
        public Glyph getFirstGlyph() {
            Glyph[][] glyphs;
            for (int length = (glyphs = this.glyphs).length, i = 0; i < length; ++i) {
                final Glyph[] page = glyphs[i];
                if (page != null) {
                    Glyph[] array;
                    for (int length2 = (array = page).length, j = 0; j < length2; ++j) {
                        final Glyph glyph = array[j];
                        if (glyph != null && glyph.height != 0 && glyph.width != 0) {
                            return glyph;
                        }
                    }
                }
            }
            throw new GdxRuntimeException("No glyphs found.");
        }
        
        public boolean hasGlyph(final char ch) {
            return this.missingGlyph != null || this.getGlyph(ch) != null;
        }
        
        public Glyph getGlyph(final char ch) {
            final Glyph[] page = this.glyphs[ch / '\u0200'];
            if (page != null) {
                return page[ch & '\u01ff'];
            }
            return null;
        }
        
        public void getGlyphs(final GlyphLayout.GlyphRun run, final CharSequence str, int start, final int end, Glyph lastGlyph) {
            final boolean markupEnabled = this.markupEnabled;
            final float scaleX = this.scaleX;
            final Glyph missingGlyph = this.missingGlyph;
            final Array<Glyph> glyphs = run.glyphs;
            final FloatArray xAdvances = run.xAdvances;
            glyphs.ensureCapacity(end - start);
            xAdvances.ensureCapacity(end - start + 1);
            while (start < end) {
                final char ch = str.charAt(start++);
                Glyph glyph = this.getGlyph(ch);
                if (glyph == null) {
                    if (missingGlyph == null) {
                        continue;
                    }
                    glyph = missingGlyph;
                }
                glyphs.add(glyph);
                if (lastGlyph == null) {
                    xAdvances.add(glyph.fixedWidth ? 0.0f : (-glyph.xoffset * scaleX - this.padLeft));
                }
                else {
                    xAdvances.add((lastGlyph.xadvance + lastGlyph.getKerning(ch)) * scaleX);
                }
                lastGlyph = glyph;
                if (markupEnabled && ch == '[' && start < end && str.charAt(start) == '[') {
                    ++start;
                }
            }
            if (lastGlyph != null) {
                final float lastGlyphWidth = lastGlyph.fixedWidth ? (lastGlyph.xadvance * scaleX) : ((lastGlyph.width + lastGlyph.xoffset) * scaleX - this.padRight);
                xAdvances.add(lastGlyphWidth);
            }
        }
        
        public int getWrapIndex(final Array<Glyph> glyphs, final int start) {
            int i = start - 1;
            if (this.isWhitespace((char)glyphs.get(i).id)) {
                return i;
            }
            while (true) {
                while (i > 0) {
                    if (!this.isWhitespace((char)glyphs.get(i).id)) {
                        while (i > 0) {
                            final char ch = (char)glyphs.get(i).id;
                            if (this.isWhitespace(ch) || this.isBreakChar(ch)) {
                                return i + 1;
                            }
                            --i;
                        }
                        return 0;
                    }
                    --i;
                }
                continue;
            }
        }
        
        public boolean isBreakChar(final char c) {
            if (this.breakChars == null) {
                return false;
            }
            char[] breakChars;
            for (int length = (breakChars = this.breakChars).length, i = 0; i < length; ++i) {
                final char br = breakChars[i];
                if (c == br) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isWhitespace(final char c) {
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        public String getImagePath(final int index) {
            return this.imagePaths[index];
        }
        
        public String[] getImagePaths() {
            return this.imagePaths;
        }
        
        public FileHandle getFontFile() {
            return this.fontFile;
        }
        
        public void setScale(final float scaleX, final float scaleY) {
            if (scaleX == 0.0f) {
                throw new IllegalArgumentException("scaleX cannot be 0.");
            }
            if (scaleY == 0.0f) {
                throw new IllegalArgumentException("scaleY cannot be 0.");
            }
            final float x = scaleX / this.scaleX;
            final float y = scaleY / this.scaleY;
            this.lineHeight *= y;
            this.spaceXadvance *= x;
            this.xHeight *= y;
            this.capHeight *= y;
            this.ascent *= y;
            this.descent *= y;
            this.down *= y;
            this.padLeft *= x;
            this.padRight *= x;
            this.padTop *= y;
            this.padBottom *= y;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }
        
        public void setScale(final float scaleXY) {
            this.setScale(scaleXY, scaleXY);
        }
        
        public void scale(final float amount) {
            this.setScale(this.scaleX + amount, this.scaleY + amount);
        }
    }
}

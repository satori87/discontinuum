// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.graphics.Pixmap;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.util.Iterator;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import java.util.Comparator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Disposable;

public class TextureAtlas implements Disposable
{
    static final String[] tuple;
    private final ObjectSet<Texture> textures;
    private final Array<AtlasRegion> regions;
    static final Comparator<TextureAtlasData.Region> indexComparator;
    
    static {
        tuple = new String[4];
        indexComparator = new Comparator<TextureAtlasData.Region>() {
            @Override
            public int compare(final TextureAtlasData.Region region1, final TextureAtlasData.Region region2) {
                int i1 = region1.index;
                if (i1 == -1) {
                    i1 = Integer.MAX_VALUE;
                }
                int i2 = region2.index;
                if (i2 == -1) {
                    i2 = Integer.MAX_VALUE;
                }
                return i1 - i2;
            }
        };
    }
    
    public TextureAtlas() {
        this.textures = new ObjectSet<Texture>(4);
        this.regions = new Array<AtlasRegion>();
    }
    
    public TextureAtlas(final String internalPackFile) {
        this(Gdx.files.internal(internalPackFile));
    }
    
    public TextureAtlas(final FileHandle packFile) {
        this(packFile, packFile.parent());
    }
    
    public TextureAtlas(final FileHandle packFile, final boolean flip) {
        this(packFile, packFile.parent(), flip);
    }
    
    public TextureAtlas(final FileHandle packFile, final FileHandle imagesDir) {
        this(packFile, imagesDir, false);
    }
    
    public TextureAtlas(final FileHandle packFile, final FileHandle imagesDir, final boolean flip) {
        this(new TextureAtlasData(packFile, imagesDir, flip));
    }
    
    public TextureAtlas(final TextureAtlasData data) {
        this.textures = new ObjectSet<Texture>(4);
        this.regions = new Array<AtlasRegion>();
        if (data != null) {
            this.load(data);
        }
    }
    
    private void load(final TextureAtlasData data) {
        final ObjectMap<TextureAtlasData.Page, Texture> pageToTexture = new ObjectMap<TextureAtlasData.Page, Texture>();
        for (final TextureAtlasData.Page page : data.pages) {
            Texture texture = null;
            if (page.texture == null) {
                texture = new Texture(page.textureFile, page.format, page.useMipMaps);
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            }
            else {
                texture = page.texture;
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            }
            this.textures.add(texture);
            pageToTexture.put(page, texture);
        }
        for (final TextureAtlasData.Region region : data.regions) {
            final int width = region.width;
            final int height = region.height;
            final AtlasRegion atlasRegion = new AtlasRegion(pageToTexture.get(region.page), region.left, region.top, region.rotate ? height : width, region.rotate ? width : height);
            atlasRegion.index = region.index;
            atlasRegion.name = region.name;
            atlasRegion.offsetX = region.offsetX;
            atlasRegion.offsetY = region.offsetY;
            atlasRegion.originalHeight = region.originalHeight;
            atlasRegion.originalWidth = region.originalWidth;
            atlasRegion.rotate = region.rotate;
            atlasRegion.splits = region.splits;
            atlasRegion.pads = region.pads;
            if (region.flip) {
                atlasRegion.flip(false, true);
            }
            this.regions.add(atlasRegion);
        }
    }
    
    public AtlasRegion addRegion(final String name, final Texture texture, final int x, final int y, final int width, final int height) {
        this.textures.add(texture);
        final AtlasRegion region = new AtlasRegion(texture, x, y, width, height);
        region.name = name;
        region.originalWidth = width;
        region.originalHeight = height;
        region.index = -1;
        this.regions.add(region);
        return region;
    }
    
    public AtlasRegion addRegion(final String name, final TextureRegion textureRegion) {
        return this.addRegion(name, textureRegion.texture, textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }
    
    public Array<AtlasRegion> getRegions() {
        return this.regions;
    }
    
    public AtlasRegion findRegion(final String name) {
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            if (this.regions.get(i).name.equals(name)) {
                return this.regions.get(i);
            }
        }
        return null;
    }
    
    public AtlasRegion findRegion(final String name, final int index) {
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            final AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                if (region.index == index) {
                    return region;
                }
            }
        }
        return null;
    }
    
    public Array<AtlasRegion> findRegions(final String name) {
        final Array<AtlasRegion> matched = new Array<AtlasRegion>(AtlasRegion.class);
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            final AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(new AtlasRegion(region));
            }
        }
        return matched;
    }
    
    public Array<Sprite> createSprites() {
        final Array sprites = new Array(true, this.regions.size, Sprite.class);
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            sprites.add(this.newSprite(this.regions.get(i)));
        }
        return (Array<Sprite>)sprites;
    }
    
    public Sprite createSprite(final String name) {
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            if (this.regions.get(i).name.equals(name)) {
                return this.newSprite(this.regions.get(i));
            }
        }
        return null;
    }
    
    public Sprite createSprite(final String name, final int index) {
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            final AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                if (region.index == index) {
                    return this.newSprite(this.regions.get(i));
                }
            }
        }
        return null;
    }
    
    public Array<Sprite> createSprites(final String name) {
        final Array<Sprite> matched = new Array<Sprite>(Sprite.class);
        for (int i = 0, n = this.regions.size; i < n; ++i) {
            final AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(this.newSprite(region));
            }
        }
        return matched;
    }
    
    private Sprite newSprite(final AtlasRegion region) {
        if (region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
            return new AtlasSprite(region);
        }
        if (region.rotate) {
            final Sprite sprite = new Sprite(region);
            sprite.setBounds(0.0f, 0.0f, (float)region.getRegionHeight(), (float)region.getRegionWidth());
            sprite.rotate90(true);
            return sprite;
        }
        return new Sprite(region);
    }
    
    public NinePatch createPatch(final String name) {
        int i = 0;
        final int n = this.regions.size;
        while (i < n) {
            final AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                final int[] splits = region.splits;
                if (splits == null) {
                    throw new IllegalArgumentException("Region does not have ninepatch splits: " + name);
                }
                final NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                if (region.pads != null) {
                    patch.setPadding((float)region.pads[0], (float)region.pads[1], (float)region.pads[2], (float)region.pads[3]);
                }
                return patch;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    public ObjectSet<Texture> getTextures() {
        return this.textures;
    }
    
    @Override
    public void dispose() {
        for (final Texture texture : this.textures) {
            texture.dispose();
        }
        this.textures.clear();
    }
    
    static String readValue(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        final int colon = line.indexOf(58);
        if (colon == -1) {
            throw new GdxRuntimeException("Invalid line: " + line);
        }
        return line.substring(colon + 1).trim();
    }
    
    static int readTuple(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        final int colon = line.indexOf(58);
        if (colon == -1) {
            throw new GdxRuntimeException("Invalid line: " + line);
        }
        int i = 0;
        int lastMatch = colon + 1;
        for (i = 0; i < 3; ++i) {
            final int comma = line.indexOf(44, lastMatch);
            if (comma == -1) {
                break;
            }
            TextureAtlas.tuple[i] = line.substring(lastMatch, comma).trim();
            lastMatch = comma + 1;
        }
        TextureAtlas.tuple[i] = line.substring(lastMatch).trim();
        return i + 1;
    }
    
    public static class TextureAtlasData
    {
        final Array<Page> pages;
        final Array<Region> regions;
        
        public TextureAtlasData(final FileHandle packFile, final FileHandle imagesDir, final boolean flip) {
            this.pages = new Array<Page>();
            this.regions = new Array<Region>();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 64);
            try {
                Page pageImage = null;
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.trim().length() == 0) {
                        pageImage = null;
                    }
                    else if (pageImage == null) {
                        final FileHandle file = imagesDir.child(line);
                        float width = 0.0f;
                        float height = 0.0f;
                        if (TextureAtlas.readTuple(reader) == 2) {
                            width = (float)Integer.parseInt(TextureAtlas.tuple[0]);
                            height = (float)Integer.parseInt(TextureAtlas.tuple[1]);
                            TextureAtlas.readTuple(reader);
                        }
                        final Pixmap.Format format = Pixmap.Format.valueOf(TextureAtlas.tuple[0]);
                        TextureAtlas.readTuple(reader);
                        final Texture.TextureFilter min = Texture.TextureFilter.valueOf(TextureAtlas.tuple[0]);
                        final Texture.TextureFilter max = Texture.TextureFilter.valueOf(TextureAtlas.tuple[1]);
                        final String direction = TextureAtlas.readValue(reader);
                        Texture.TextureWrap repeatX = Texture.TextureWrap.ClampToEdge;
                        Texture.TextureWrap repeatY = Texture.TextureWrap.ClampToEdge;
                        if (direction.equals("x")) {
                            repeatX = Texture.TextureWrap.Repeat;
                        }
                        else if (direction.equals("y")) {
                            repeatY = Texture.TextureWrap.Repeat;
                        }
                        else if (direction.equals("xy")) {
                            repeatX = Texture.TextureWrap.Repeat;
                            repeatY = Texture.TextureWrap.Repeat;
                        }
                        pageImage = new Page(file, width, height, min.isMipMap(), format, min, max, repeatX, repeatY);
                        this.pages.add(pageImage);
                    }
                    else {
                        final boolean rotate = Boolean.valueOf(TextureAtlas.readValue(reader));
                        TextureAtlas.readTuple(reader);
                        final int left = Integer.parseInt(TextureAtlas.tuple[0]);
                        final int top = Integer.parseInt(TextureAtlas.tuple[1]);
                        TextureAtlas.readTuple(reader);
                        final int width2 = Integer.parseInt(TextureAtlas.tuple[0]);
                        final int height2 = Integer.parseInt(TextureAtlas.tuple[1]);
                        final Region region = new Region();
                        region.page = pageImage;
                        region.left = left;
                        region.top = top;
                        region.width = width2;
                        region.height = height2;
                        region.name = line;
                        region.rotate = rotate;
                        if (TextureAtlas.readTuple(reader) == 4) {
                            region.splits = new int[] { Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3]) };
                            if (TextureAtlas.readTuple(reader) == 4) {
                                region.pads = new int[] { Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3]) };
                                TextureAtlas.readTuple(reader);
                            }
                        }
                        region.originalWidth = Integer.parseInt(TextureAtlas.tuple[0]);
                        region.originalHeight = Integer.parseInt(TextureAtlas.tuple[1]);
                        TextureAtlas.readTuple(reader);
                        region.offsetX = (float)Integer.parseInt(TextureAtlas.tuple[0]);
                        region.offsetY = (float)Integer.parseInt(TextureAtlas.tuple[1]);
                        region.index = Integer.parseInt(TextureAtlas.readValue(reader));
                        if (flip) {
                            region.flip = true;
                        }
                        this.regions.add(region);
                    }
                }
            }
            catch (Exception ex) {
                throw new GdxRuntimeException("Error reading pack file: " + packFile, ex);
            }
            finally {
                StreamUtils.closeQuietly(reader);
            }
            StreamUtils.closeQuietly(reader);
            this.regions.sort(TextureAtlas.indexComparator);
        }
        
        public Array<Page> getPages() {
            return this.pages;
        }
        
        public Array<Region> getRegions() {
            return this.regions;
        }
        
        public static class Page
        {
            public final FileHandle textureFile;
            public Texture texture;
            public final float width;
            public final float height;
            public final boolean useMipMaps;
            public final Pixmap.Format format;
            public final Texture.TextureFilter minFilter;
            public final Texture.TextureFilter magFilter;
            public final Texture.TextureWrap uWrap;
            public final Texture.TextureWrap vWrap;
            
            public Page(final FileHandle handle, final float width, final float height, final boolean useMipMaps, final Pixmap.Format format, final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final Texture.TextureWrap uWrap, final Texture.TextureWrap vWrap) {
                this.width = width;
                this.height = height;
                this.textureFile = handle;
                this.useMipMaps = useMipMaps;
                this.format = format;
                this.minFilter = minFilter;
                this.magFilter = magFilter;
                this.uWrap = uWrap;
                this.vWrap = vWrap;
            }
        }
        
        public static class Region
        {
            public Page page;
            public int index;
            public String name;
            public float offsetX;
            public float offsetY;
            public int originalWidth;
            public int originalHeight;
            public boolean rotate;
            public int left;
            public int top;
            public int width;
            public int height;
            public boolean flip;
            public int[] splits;
            public int[] pads;
        }
    }
    
    public static class AtlasRegion extends TextureRegion
    {
        public int index;
        public String name;
        public float offsetX;
        public float offsetY;
        public int packedWidth;
        public int packedHeight;
        public int originalWidth;
        public int originalHeight;
        public boolean rotate;
        public int[] splits;
        public int[] pads;
        
        public AtlasRegion(final Texture texture, final int x, final int y, final int width, final int height) {
            super(texture, x, y, width, height);
            this.originalWidth = width;
            this.originalHeight = height;
            this.packedWidth = width;
            this.packedHeight = height;
        }
        
        public AtlasRegion(final AtlasRegion region) {
            this.setRegion(region);
            this.index = region.index;
            this.name = region.name;
            this.offsetX = region.offsetX;
            this.offsetY = region.offsetY;
            this.packedWidth = region.packedWidth;
            this.packedHeight = region.packedHeight;
            this.originalWidth = region.originalWidth;
            this.originalHeight = region.originalHeight;
            this.rotate = region.rotate;
            this.splits = region.splits;
        }
        
        @Override
        public void flip(final boolean x, final boolean y) {
            super.flip(x, y);
            if (x) {
                this.offsetX = this.originalWidth - this.offsetX - this.getRotatedPackedWidth();
            }
            if (y) {
                this.offsetY = this.originalHeight - this.offsetY - this.getRotatedPackedHeight();
            }
        }
        
        public float getRotatedPackedWidth() {
            return (float)(this.rotate ? this.packedHeight : this.packedWidth);
        }
        
        public float getRotatedPackedHeight() {
            return (float)(this.rotate ? this.packedWidth : this.packedHeight);
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public static class AtlasSprite extends Sprite
    {
        final AtlasRegion region;
        float originalOffsetX;
        float originalOffsetY;
        
        public AtlasSprite(final AtlasRegion region) {
            this.region = new AtlasRegion(region);
            this.originalOffsetX = region.offsetX;
            this.originalOffsetY = region.offsetY;
            this.setRegion(region);
            this.setOrigin(region.originalWidth / 2.0f, region.originalHeight / 2.0f);
            final int width = region.getRegionWidth();
            final int height = region.getRegionHeight();
            if (region.rotate) {
                super.rotate90(true);
                super.setBounds(region.offsetX, region.offsetY, (float)height, (float)width);
            }
            else {
                super.setBounds(region.offsetX, region.offsetY, (float)width, (float)height);
            }
            this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public AtlasSprite(final AtlasSprite sprite) {
            this.region = sprite.region;
            this.originalOffsetX = sprite.originalOffsetX;
            this.originalOffsetY = sprite.originalOffsetY;
            this.set(sprite);
        }
        
        @Override
        public void setPosition(final float x, final float y) {
            super.setPosition(x + this.region.offsetX, y + this.region.offsetY);
        }
        
        @Override
        public void setX(final float x) {
            super.setX(x + this.region.offsetX);
        }
        
        @Override
        public void setY(final float y) {
            super.setY(y + this.region.offsetY);
        }
        
        @Override
        public void setBounds(final float x, final float y, final float width, final float height) {
            final float widthRatio = width / this.region.originalWidth;
            final float heightRatio = height / this.region.originalHeight;
            this.region.offsetX = this.originalOffsetX * widthRatio;
            this.region.offsetY = this.originalOffsetY * heightRatio;
            final int packedWidth = this.region.rotate ? this.region.packedHeight : this.region.packedWidth;
            final int packedHeight = this.region.rotate ? this.region.packedWidth : this.region.packedHeight;
            super.setBounds(x + this.region.offsetX, y + this.region.offsetY, packedWidth * widthRatio, packedHeight * heightRatio);
        }
        
        @Override
        public void setSize(final float width, final float height) {
            this.setBounds(this.getX(), this.getY(), width, height);
        }
        
        @Override
        public void setOrigin(final float originX, final float originY) {
            super.setOrigin(originX - this.region.offsetX, originY - this.region.offsetY);
        }
        
        @Override
        public void setOriginCenter() {
            super.setOrigin(this.width / 2.0f - this.region.offsetX, this.height / 2.0f - this.region.offsetY);
        }
        
        @Override
        public void flip(final boolean x, final boolean y) {
            if (this.region.rotate) {
                super.flip(y, x);
            }
            else {
                super.flip(x, y);
            }
            final float oldOriginX = this.getOriginX();
            final float oldOriginY = this.getOriginY();
            final float oldOffsetX = this.region.offsetX;
            final float oldOffsetY = this.region.offsetY;
            final float widthRatio = this.getWidthRatio();
            final float heightRatio = this.getHeightRatio();
            this.region.offsetX = this.originalOffsetX;
            this.region.offsetY = this.originalOffsetY;
            this.region.flip(x, y);
            this.originalOffsetX = this.region.offsetX;
            this.originalOffsetY = this.region.offsetY;
            final AtlasRegion region = this.region;
            region.offsetX *= widthRatio;
            final AtlasRegion region2 = this.region;
            region2.offsetY *= heightRatio;
            this.translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            this.setOrigin(oldOriginX, oldOriginY);
        }
        
        @Override
        public void rotate90(final boolean clockwise) {
            super.rotate90(clockwise);
            final float oldOriginX = this.getOriginX();
            final float oldOriginY = this.getOriginY();
            final float oldOffsetX = this.region.offsetX;
            final float oldOffsetY = this.region.offsetY;
            final float widthRatio = this.getWidthRatio();
            final float heightRatio = this.getHeightRatio();
            if (clockwise) {
                this.region.offsetX = oldOffsetY;
                this.region.offsetY = this.region.originalHeight * heightRatio - oldOffsetX - this.region.packedWidth * widthRatio;
            }
            else {
                this.region.offsetX = this.region.originalWidth * widthRatio - oldOffsetY - this.region.packedHeight * heightRatio;
                this.region.offsetY = oldOffsetX;
            }
            this.translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            this.setOrigin(oldOriginX, oldOriginY);
        }
        
        @Override
        public float getX() {
            return super.getX() - this.region.offsetX;
        }
        
        @Override
        public float getY() {
            return super.getY() - this.region.offsetY;
        }
        
        @Override
        public float getOriginX() {
            return super.getOriginX() + this.region.offsetX;
        }
        
        @Override
        public float getOriginY() {
            return super.getOriginY() + this.region.offsetY;
        }
        
        @Override
        public float getWidth() {
            return super.getWidth() / this.region.getRotatedPackedWidth() * this.region.originalWidth;
        }
        
        @Override
        public float getHeight() {
            return super.getHeight() / this.region.getRotatedPackedHeight() * this.region.originalHeight;
        }
        
        public float getWidthRatio() {
            return super.getWidth() / this.region.getRotatedPackedWidth();
        }
        
        public float getHeightRatio() {
            return super.getHeight() / this.region.getRotatedPackedHeight();
        }
        
        public AtlasRegion getAtlasRegion() {
            return this.region;
        }
        
        @Override
        public String toString() {
            return this.region.toString();
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import java.util.Comparator;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.Arrays;
import com.badlogic.gdx.graphics.Texture;
import java.util.Iterator;
import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

public class PixmapPacker implements Disposable
{
    boolean packToTexture;
    boolean disposed;
    int pageWidth;
    int pageHeight;
    Pixmap.Format pageFormat;
    int padding;
    boolean duplicateBorder;
    Color transparentColor;
    final Array<Page> pages;
    PackStrategy packStrategy;
    private Color c;
    
    public PixmapPacker(final int pageWidth, final int pageHeight, final Pixmap.Format pageFormat, final int padding, final boolean duplicateBorder) {
        this(pageWidth, pageHeight, pageFormat, padding, duplicateBorder, new GuillotineStrategy());
    }
    
    public PixmapPacker(final int pageWidth, final int pageHeight, final Pixmap.Format pageFormat, final int padding, final boolean duplicateBorder, final PackStrategy packStrategy) {
        this.transparentColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        this.pages = new Array<Page>();
        this.c = new Color();
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.pageFormat = pageFormat;
        this.padding = padding;
        this.duplicateBorder = duplicateBorder;
        this.packStrategy = packStrategy;
    }
    
    public void sort(final Array<Pixmap> images) {
        this.packStrategy.sort(images);
    }
    
    public synchronized Rectangle pack(final Pixmap image) {
        return this.pack(null, image);
    }
    
    public synchronized Rectangle pack(String name, Pixmap image) {
        if (this.disposed) {
            return null;
        }
        if (name != null && this.getRect(name) != null) {
            throw new GdxRuntimeException("Pixmap has already been packed with name: " + name);
        }
        final boolean isPatch = name != null && name.endsWith(".9");
        Pixmap pixmapToDispose = null;
        PixmapPackerRectangle rect;
        if (isPatch) {
            rect = new PixmapPackerRectangle(0, 0, image.getWidth() - 2, image.getHeight() - 2);
            pixmapToDispose = new Pixmap(image.getWidth() - 2, image.getHeight() - 2, image.getFormat());
            rect.splits = this.getSplits(image);
            rect.pads = this.getPads(image, rect.splits);
            pixmapToDispose.drawPixmap(image, 0, 0, 1, 1, image.getWidth() - 1, image.getHeight() - 1);
            image = pixmapToDispose;
            name = name.split("\\.")[0];
        }
        else {
            rect = new PixmapPackerRectangle(0, 0, image.getWidth(), image.getHeight());
        }
        if (rect.getWidth() <= this.pageWidth && rect.getHeight() <= this.pageHeight) {
            final Page page = this.packStrategy.pack(this, name, rect);
            if (name != null) {
                page.rects.put(name, rect);
                page.addedRects.add(name);
            }
            final int rectX = (int)rect.x;
            final int rectY = (int)rect.y;
            final int rectWidth = (int)rect.width;
            final int rectHeight = (int)rect.height;
            if (this.packToTexture && !this.duplicateBorder && page.texture != null && !page.dirty) {
                page.texture.bind();
                Gdx.gl.glTexSubImage2D(page.texture.glTarget, 0, rectX, rectY, rectWidth, rectHeight, image.getGLFormat(), image.getGLType(), image.getPixels());
            }
            else {
                page.dirty = true;
            }
            page.image.setBlending(Pixmap.Blending.None);
            page.image.drawPixmap(image, rectX, rectY);
            if (this.duplicateBorder) {
                final int imageWidth = image.getWidth();
                final int imageHeight = image.getHeight();
                page.image.drawPixmap(image, 0, 0, 1, 1, rectX - 1, rectY - 1, 1, 1);
                page.image.drawPixmap(image, imageWidth - 1, 0, 1, 1, rectX + rectWidth, rectY - 1, 1, 1);
                page.image.drawPixmap(image, 0, imageHeight - 1, 1, 1, rectX - 1, rectY + rectHeight, 1, 1);
                page.image.drawPixmap(image, imageWidth - 1, imageHeight - 1, 1, 1, rectX + rectWidth, rectY + rectHeight, 1, 1);
                page.image.drawPixmap(image, 0, 0, imageWidth, 1, rectX, rectY - 1, rectWidth, 1);
                page.image.drawPixmap(image, 0, imageHeight - 1, imageWidth, 1, rectX, rectY + rectHeight, rectWidth, 1);
                page.image.drawPixmap(image, 0, 0, 1, imageHeight, rectX - 1, rectY, 1, rectHeight);
                page.image.drawPixmap(image, imageWidth - 1, 0, 1, imageHeight, rectX + rectWidth, rectY, 1, rectHeight);
            }
            if (pixmapToDispose != null) {
                pixmapToDispose.dispose();
            }
            return rect;
        }
        if (name == null) {
            throw new GdxRuntimeException("Page size too small for pixmap.");
        }
        throw new GdxRuntimeException("Page size too small for pixmap: " + name);
    }
    
    public Array<Page> getPages() {
        return this.pages;
    }
    
    public synchronized Rectangle getRect(final String name) {
        for (final Page page : this.pages) {
            final Rectangle rect = page.rects.get(name);
            if (rect != null) {
                return rect;
            }
        }
        return null;
    }
    
    public synchronized Page getPage(final String name) {
        for (final Page page : this.pages) {
            final Rectangle rect = page.rects.get(name);
            if (rect != null) {
                return page;
            }
        }
        return null;
    }
    
    public synchronized int getPageIndex(final String name) {
        for (int i = 0; i < this.pages.size; ++i) {
            final Rectangle rect = this.pages.get(i).rects.get(name);
            if (rect != null) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public synchronized void dispose() {
        for (final Page page : this.pages) {
            if (page.texture == null) {
                page.image.dispose();
            }
        }
        this.disposed = true;
    }
    
    public synchronized TextureAtlas generateTextureAtlas(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean useMipMaps) {
        final TextureAtlas atlas = new TextureAtlas();
        this.updateTextureAtlas(atlas, minFilter, magFilter, useMipMaps);
        return atlas;
    }
    
    public synchronized void updateTextureAtlas(final TextureAtlas atlas, final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean useMipMaps) {
        this.updatePageTextures(minFilter, magFilter, useMipMaps);
        for (final Page page : this.pages) {
            if (page.addedRects.size > 0) {
                for (final String name : page.addedRects) {
                    final PixmapPackerRectangle rect = page.rects.get(name);
                    if (rect.splits != null) {
                        final TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                        region.splits = rect.splits;
                        region.pads = rect.pads;
                        region.name = name;
                        region.index = -1;
                        atlas.getRegions().add(region);
                    }
                    else {
                        final TextureRegion region2 = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                        atlas.addRegion(name, region2);
                    }
                }
                page.addedRects.clear();
                atlas.getTextures().add(page.texture);
            }
        }
    }
    
    public synchronized void updateTextureRegions(final Array<TextureRegion> regions, final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean useMipMaps) {
        this.updatePageTextures(minFilter, magFilter, useMipMaps);
        while (regions.size < this.pages.size) {
            regions.add(new TextureRegion(this.pages.get(regions.size).texture));
        }
    }
    
    public synchronized void updatePageTextures(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean useMipMaps) {
        for (final Page page : this.pages) {
            page.updateTexture(minFilter, magFilter, useMipMaps);
        }
    }
    
    public int getPageWidth() {
        return this.pageWidth;
    }
    
    public void setPageWidth(final int pageWidth) {
        this.pageWidth = pageWidth;
    }
    
    public int getPageHeight() {
        return this.pageHeight;
    }
    
    public void setPageHeight(final int pageHeight) {
        this.pageHeight = pageHeight;
    }
    
    public Pixmap.Format getPageFormat() {
        return this.pageFormat;
    }
    
    public void setPageFormat(final Pixmap.Format pageFormat) {
        this.pageFormat = pageFormat;
    }
    
    public int getPadding() {
        return this.padding;
    }
    
    public void setPadding(final int padding) {
        this.padding = padding;
    }
    
    public boolean getDuplicateBorder() {
        return this.duplicateBorder;
    }
    
    public void setDuplicateBorder(final boolean duplicateBorder) {
        this.duplicateBorder = duplicateBorder;
    }
    
    public boolean getPackToTexture() {
        return this.packToTexture;
    }
    
    public void setPackToTexture(final boolean packToTexture) {
        this.packToTexture = packToTexture;
    }
    
    public Color getTransparentColor() {
        return this.transparentColor;
    }
    
    public void setTransparentColor(final Color color) {
        this.transparentColor.set(color);
    }
    
    private int[] getSplits(final Pixmap raster) {
        int startX = this.getSplitPoint(raster, 1, 0, true, true);
        int endX = this.getSplitPoint(raster, startX, 0, false, true);
        int startY = this.getSplitPoint(raster, 0, 1, true, false);
        int endY = this.getSplitPoint(raster, 0, startY, false, false);
        this.getSplitPoint(raster, endX + 1, 0, true, true);
        this.getSplitPoint(raster, 0, endY + 1, true, false);
        if (startX == 0 && endX == 0 && startY == 0 && endY == 0) {
            return null;
        }
        if (startX != 0) {
            --startX;
            endX = raster.getWidth() - 2 - (endX - 1);
        }
        else {
            endX = raster.getWidth() - 2;
        }
        if (startY != 0) {
            --startY;
            endY = raster.getHeight() - 2 - (endY - 1);
        }
        else {
            endY = raster.getHeight() - 2;
        }
        return new int[] { startX, endX, startY, endY };
    }
    
    private int[] getPads(final Pixmap raster, final int[] splits) {
        final int bottom = raster.getHeight() - 1;
        final int right = raster.getWidth() - 1;
        int startX = this.getSplitPoint(raster, 1, bottom, true, true);
        int startY = this.getSplitPoint(raster, right, 1, true, false);
        int endX = 0;
        int endY = 0;
        if (startX != 0) {
            endX = this.getSplitPoint(raster, startX + 1, bottom, false, true);
        }
        if (startY != 0) {
            endY = this.getSplitPoint(raster, right, startY + 1, false, false);
        }
        this.getSplitPoint(raster, endX + 1, bottom, true, true);
        this.getSplitPoint(raster, right, endY + 1, true, false);
        if (startX == 0 && endX == 0 && startY == 0 && endY == 0) {
            return null;
        }
        if (startX == 0 && endX == 0) {
            startX = -1;
            endX = -1;
        }
        else if (startX > 0) {
            --startX;
            endX = raster.getWidth() - 2 - (endX - 1);
        }
        else {
            endX = raster.getWidth() - 2;
        }
        if (startY == 0 && endY == 0) {
            startY = -1;
            endY = -1;
        }
        else if (startY > 0) {
            --startY;
            endY = raster.getHeight() - 2 - (endY - 1);
        }
        else {
            endY = raster.getHeight() - 2;
        }
        final int[] pads = { startX, endX, startY, endY };
        if (splits != null && Arrays.equals(pads, splits)) {
            return null;
        }
        return pads;
    }
    
    private int getSplitPoint(final Pixmap raster, final int startX, final int startY, final boolean startPoint, final boolean xAxis) {
        final int[] rgba = new int[4];
        int next = xAxis ? startX : startY;
        final int end = xAxis ? raster.getWidth() : raster.getHeight();
        final int breakA = startPoint ? 255 : 0;
        int x = startX;
        int y = startY;
        while (next != end) {
            if (xAxis) {
                x = next;
            }
            else {
                y = next;
            }
            final int colint = raster.getPixel(x, y);
            this.c.set(colint);
            rgba[0] = (int)(this.c.r * 255.0f);
            rgba[1] = (int)(this.c.g * 255.0f);
            rgba[2] = (int)(this.c.b * 255.0f);
            rgba[3] = (int)(this.c.a * 255.0f);
            if (rgba[3] == breakA) {
                return next;
            }
            if (!startPoint && (rgba[0] != 0 || rgba[1] != 0 || rgba[2] != 0 || rgba[3] != 255)) {
                System.out.println(String.valueOf(x) + "  " + y + " " + rgba + " ");
            }
            ++next;
        }
        return 0;
    }
    
    public static class Page
    {
        OrderedMap<String, PixmapPackerRectangle> rects;
        Pixmap image;
        Texture texture;
        final Array<String> addedRects;
        boolean dirty;
        
        public Page(final PixmapPacker packer) {
            this.rects = new OrderedMap<String, PixmapPackerRectangle>();
            this.addedRects = new Array<String>();
            this.image = new Pixmap(packer.pageWidth, packer.pageHeight, packer.pageFormat);
            final Color transparentColor = packer.getTransparentColor();
            this.image.setColor(transparentColor);
            this.image.fill();
        }
        
        public Pixmap getPixmap() {
            return this.image;
        }
        
        public OrderedMap<String, PixmapPackerRectangle> getRects() {
            return this.rects;
        }
        
        public Texture getTexture() {
            return this.texture;
        }
        
        public boolean updateTexture(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean useMipMaps) {
            if (this.texture != null) {
                if (!this.dirty) {
                    return false;
                }
                this.texture.load(this.texture.getTextureData());
            }
            else {
                (this.texture = new Texture(new PixmapTextureData(this.image, this.image.getFormat(), useMipMaps, false, true)) {
                    @Override
                    public void dispose() {
                        super.dispose();
                        Page.this.image.dispose();
                    }
                }).setFilter(minFilter, magFilter);
            }
            this.dirty = false;
            return true;
        }
    }
    
    public static class GuillotineStrategy implements PackStrategy
    {
        Comparator<Pixmap> comparator;
        
        @Override
        public void sort(final Array<Pixmap> pixmaps) {
            if (this.comparator == null) {
                this.comparator = new Comparator<Pixmap>() {
                    @Override
                    public int compare(final Pixmap o1, final Pixmap o2) {
                        return Math.max(o1.getWidth(), o1.getHeight()) - Math.max(o2.getWidth(), o2.getHeight());
                    }
                };
            }
            pixmaps.sort(this.comparator);
        }
        
        @Override
        public Page pack(final PixmapPacker packer, final String name, final Rectangle rect) {
            GuillotinePage page;
            if (packer.pages.size == 0) {
                page = new GuillotinePage(packer);
                packer.pages.add(page);
            }
            else {
                page = packer.pages.peek();
            }
            final int padding = packer.padding;
            rect.width += padding;
            rect.height += padding;
            Node node = this.insert(page.root, rect);
            if (node == null) {
                page = new GuillotinePage(packer);
                packer.pages.add(page);
                node = this.insert(page.root, rect);
            }
            node.full = true;
            rect.set(node.rect.x, node.rect.y, node.rect.width - padding, node.rect.height - padding);
            return page;
        }
        
        private Node insert(final Node node, final Rectangle rect) {
            if (!node.full && node.leftChild != null && node.rightChild != null) {
                Node newNode = this.insert(node.leftChild, rect);
                if (newNode == null) {
                    newNode = this.insert(node.rightChild, rect);
                }
                return newNode;
            }
            if (node.full) {
                return null;
            }
            if (node.rect.width == rect.width && node.rect.height == rect.height) {
                return node;
            }
            if (node.rect.width < rect.width || node.rect.height < rect.height) {
                return null;
            }
            node.leftChild = new Node();
            node.rightChild = new Node();
            final int deltaWidth = (int)node.rect.width - (int)rect.width;
            final int deltaHeight = (int)node.rect.height - (int)rect.height;
            if (deltaWidth > deltaHeight) {
                node.leftChild.rect.x = node.rect.x;
                node.leftChild.rect.y = node.rect.y;
                node.leftChild.rect.width = rect.width;
                node.leftChild.rect.height = node.rect.height;
                node.rightChild.rect.x = node.rect.x + rect.width;
                node.rightChild.rect.y = node.rect.y;
                node.rightChild.rect.width = node.rect.width - rect.width;
                node.rightChild.rect.height = node.rect.height;
            }
            else {
                node.leftChild.rect.x = node.rect.x;
                node.leftChild.rect.y = node.rect.y;
                node.leftChild.rect.width = node.rect.width;
                node.leftChild.rect.height = rect.height;
                node.rightChild.rect.x = node.rect.x;
                node.rightChild.rect.y = node.rect.y + rect.height;
                node.rightChild.rect.width = node.rect.width;
                node.rightChild.rect.height = node.rect.height - rect.height;
            }
            return this.insert(node.leftChild, rect);
        }
        
        static final class Node
        {
            public Node leftChild;
            public Node rightChild;
            public final Rectangle rect;
            public boolean full;
            
            Node() {
                this.rect = new Rectangle();
            }
        }
        
        static class GuillotinePage extends Page
        {
            Node root;
            
            public GuillotinePage(final PixmapPacker packer) {
                super(packer);
                this.root = new Node();
                this.root.rect.x = (float)packer.padding;
                this.root.rect.y = (float)packer.padding;
                this.root.rect.width = (float)(packer.pageWidth - packer.padding * 2);
                this.root.rect.height = (float)(packer.pageHeight - packer.padding * 2);
            }
        }
    }
    
    public static class SkylineStrategy implements PackStrategy
    {
        Comparator<Pixmap> comparator;
        
        @Override
        public void sort(final Array<Pixmap> images) {
            if (this.comparator == null) {
                this.comparator = new Comparator<Pixmap>() {
                    @Override
                    public int compare(final Pixmap o1, final Pixmap o2) {
                        return o1.getHeight() - o2.getHeight();
                    }
                };
            }
            images.sort(this.comparator);
        }
        
        @Override
        public Page pack(final PixmapPacker packer, final String name, final Rectangle rect) {
            final int padding = packer.padding;
            final int pageWidth = packer.pageWidth - padding * 2;
            final int pageHeight = packer.pageHeight - padding * 2;
            final int rectWidth = (int)rect.width + padding;
            final int rectHeight = (int)rect.height + padding;
            for (int i = 0, n = packer.pages.size; i < n; ++i) {
                final SkylinePage page = packer.pages.get(i);
                SkylinePage.Row bestRow = null;
                for (int ii = 0, nn = page.rows.size - 1; ii < nn; ++ii) {
                    final SkylinePage.Row row = page.rows.get(ii);
                    if (row.x + rectWidth < pageWidth) {
                        if (row.y + rectHeight < pageHeight) {
                            if (rectHeight <= row.height) {
                                if (bestRow == null || row.height < bestRow.height) {
                                    bestRow = row;
                                }
                            }
                        }
                    }
                }
                if (bestRow == null) {
                    final SkylinePage.Row row2 = page.rows.peek();
                    if (row2.y + rectHeight >= pageHeight) {
                        continue;
                    }
                    if (row2.x + rectWidth < pageWidth) {
                        row2.height = Math.max(row2.height, rectHeight);
                        bestRow = row2;
                    }
                    else if (row2.y + row2.height + rectHeight < pageHeight) {
                        bestRow = new SkylinePage.Row();
                        bestRow.y = row2.y + row2.height;
                        bestRow.height = rectHeight;
                        page.rows.add(bestRow);
                    }
                }
                if (bestRow != null) {
                    rect.x = (float)bestRow.x;
                    rect.y = (float)bestRow.y;
                    final SkylinePage.Row row4 = bestRow;
                    row4.x += rectWidth;
                    return page;
                }
            }
            final SkylinePage page2 = new SkylinePage(packer);
            packer.pages.add(page2);
            final SkylinePage.Row row3 = new SkylinePage.Row();
            row3.x = padding + rectWidth;
            row3.y = padding;
            row3.height = rectHeight;
            page2.rows.add(row3);
            rect.x = (float)padding;
            rect.y = (float)padding;
            return page2;
        }
        
        static class SkylinePage extends Page
        {
            Array<Row> rows;
            
            public SkylinePage(final PixmapPacker packer) {
                super(packer);
                this.rows = new Array<Row>();
            }
            
            static class Row
            {
                int x;
                int y;
                int height;
            }
        }
    }
    
    public static class PixmapPackerRectangle extends Rectangle
    {
        int[] splits;
        int[] pads;
        
        PixmapPackerRectangle(final int x, final int y, final int width, final int height) {
            super((float)x, (float)y, (float)width, (float)height);
        }
    }
    
    public interface PackStrategy
    {
        void sort(final Array<Pixmap> p0);
        
        Page pack(final PixmapPacker p0, final String p1, final Rectangle p2);
    }
}

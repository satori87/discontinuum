// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import java.util.Iterator;
import java.io.Writer;
import com.badlogic.gdx.graphics.PixmapIO;
import java.io.IOException;
import com.badlogic.gdx.files.FileHandle;

public class PixmapPackerIO
{
    public void save(final FileHandle file, final PixmapPacker packer) throws IOException {
        this.save(file, packer, new SaveParameters());
    }
    
    public void save(final FileHandle file, final PixmapPacker packer, final SaveParameters parameters) throws IOException {
        final Writer writer = file.writer(false);
        int index = 0;
        for (final PixmapPacker.Page page : packer.pages) {
            if (page.rects.size > 0) {
                final FileHandle pageFile = file.sibling(String.valueOf(file.nameWithoutExtension()) + "_" + ++index + parameters.format.getExtension());
                switch (parameters.format) {
                    case CIM: {
                        PixmapIO.writeCIM(pageFile, page.image);
                        break;
                    }
                    case PNG: {
                        PixmapIO.writePNG(pageFile, page.image);
                        break;
                    }
                }
                writer.write("\n");
                writer.write(String.valueOf(pageFile.name()) + "\n");
                writer.write("size: " + page.image.getWidth() + "," + page.image.getHeight() + "\n");
                writer.write("format: " + packer.pageFormat.name() + "\n");
                writer.write("filter: " + parameters.minFilter.name() + "," + parameters.magFilter.name() + "\n");
                writer.write("repeat: none\n");
                for (final String name : page.rects.keys()) {
                    writer.write(String.valueOf(name) + "\n");
                    final PixmapPacker.PixmapPackerRectangle rect = page.rects.get(name);
                    writer.write("  rotate: false\n");
                    writer.write("  xy: " + (int)rect.x + "," + (int)rect.y + "\n");
                    writer.write("  size: " + (int)rect.width + "," + (int)rect.height + "\n");
                    if (rect.splits != null) {
                        writer.write("  split: " + rect.splits[0] + ", " + rect.splits[1] + ", " + rect.splits[2] + ", " + rect.splits[3] + "\n");
                        if (rect.pads != null) {
                            writer.write("  pad: " + rect.pads[0] + ", " + rect.pads[1] + ", " + rect.pads[2] + ", " + rect.pads[3] + "\n");
                        }
                    }
                    writer.write("  orig: " + (int)rect.width + "," + (int)rect.height + "\n");
                    writer.write("  offset: 0, 0\n");
                    writer.write("  index: -1\n");
                }
            }
        }
        writer.close();
    }
    
    public enum ImageFormat
    {
        CIM("CIM", 0, ".cim"), 
        PNG("PNG", 1, ".png");
        
        private final String extension;
        
        public String getExtension() {
            return this.extension;
        }
        
        private ImageFormat(final String name, final int ordinal, final String extension) {
            this.extension = extension;
        }
    }
    
    public static class SaveParameters
    {
        public ImageFormat format;
        public Texture.TextureFilter minFilter;
        public Texture.TextureFilter magFilter;
        
        public SaveParameters() {
            this.format = ImageFormat.PNG;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedReader;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;

public class PolygonRegionLoader extends SynchronousAssetLoader<PolygonRegion, PolygonRegionParameters>
{
    private PolygonRegionParameters defaultParameters;
    private EarClippingTriangulator triangulator;
    
    public PolygonRegionLoader() {
        this(new InternalFileHandleResolver());
    }
    
    public PolygonRegionLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.defaultParameters = new PolygonRegionParameters();
        this.triangulator = new EarClippingTriangulator();
    }
    
    @Override
    public PolygonRegion load(final AssetManager manager, final String fileName, final FileHandle file, final PolygonRegionParameters parameter) {
        final Texture texture = manager.get(manager.getDependencies(fileName).first());
        return this.load(new TextureRegion(texture), file);
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, PolygonRegionParameters params) {
        if (params == null) {
            params = this.defaultParameters;
        }
        String image = null;
        try {
            final BufferedReader reader = file.reader(params.readerBuffer);
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith(params.texturePrefix)) {
                    image = line.substring(params.texturePrefix.length());
                    break;
                }
            }
            reader.close();
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Error reading " + fileName, e);
        }
        if (image == null && params.textureExtensions != null) {
            String[] textureExtensions;
            for (int length = (textureExtensions = params.textureExtensions).length, i = 0; i < length; ++i) {
                final String extension = textureExtensions[i];
                final FileHandle sibling = file.sibling(file.nameWithoutExtension().concat("." + extension));
                if (sibling.exists()) {
                    image = sibling.name();
                }
            }
        }
        if (image != null) {
            final Array<AssetDescriptor> deps = new Array<AssetDescriptor>(1);
            deps.add(new AssetDescriptor(file.sibling(image), Texture.class));
            return deps;
        }
        return null;
    }
    
    public PolygonRegion load(final TextureRegion textureRegion, final FileHandle file) {
        final BufferedReader reader = file.reader(256);
        Label_0157: {
            try {
                String line;
                do {
                    line = reader.readLine();
                    if (line == null) {
                        break Label_0157;
                    }
                } while (!line.startsWith("s"));
                final String[] polygonStrings = line.substring(1).trim().split(",");
                final float[] vertices = new float[polygonStrings.length];
                for (int i = 0, n = vertices.length; i < n; ++i) {
                    vertices[i] = Float.parseFloat(polygonStrings[i]);
                }
                return new PolygonRegion(textureRegion, vertices, this.triangulator.computeTriangles(vertices).toArray());
            }
            catch (IOException ex) {
                throw new GdxRuntimeException("Error reading polygon shape file: " + file, ex);
            }
            finally {
                StreamUtils.closeQuietly(reader);
            }
        }
        StreamUtils.closeQuietly(reader);
        throw new GdxRuntimeException("Polygon shape not found: " + file);
    }
    
    public static class PolygonRegionParameters extends AssetLoaderParameters<PolygonRegion>
    {
        public String texturePrefix;
        public int readerBuffer;
        public String[] textureExtensions;
        
        public PolygonRegionParameters() {
            this.texturePrefix = "i ";
            this.readerBuffer = 1024;
            this.textureExtensions = new String[] { "png", "PNG", "jpeg", "JPEG", "jpg", "JPG", "cim", "CIM", "etc1", "ETC1", "ktx", "KTX", "zktx", "ZKTX" };
        }
    }
}

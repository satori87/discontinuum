// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.graphics.Texture;
import java.util.StringTokenizer;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Iterator;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public abstract class BaseTmxMapLoader<P extends AssetLoaderParameters<TiledMap>> extends AsynchronousAssetLoader<TiledMap, P>
{
    protected static final int FLAG_FLIP_HORIZONTALLY = Integer.MIN_VALUE;
    protected static final int FLAG_FLIP_VERTICALLY = 1073741824;
    protected static final int FLAG_FLIP_DIAGONALLY = 536870912;
    protected static final int MASK_CLEAR = -536870912;
    protected XmlReader xml;
    protected XmlReader.Element root;
    protected boolean convertObjectToTileSpace;
    protected boolean flipY;
    protected int mapTileWidth;
    protected int mapTileHeight;
    protected int mapWidthInPixels;
    protected int mapHeightInPixels;
    protected TiledMap map;
    
    public BaseTmxMapLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.xml = new XmlReader();
        this.flipY = true;
    }
    
    protected void loadTileGroup(final TiledMap map, final MapLayers parentLayers, final XmlReader.Element element, final FileHandle tmxFile, final ImageResolver imageResolver) {
        if (element.getName().equals("group")) {
            final MapGroupLayer groupLayer = new MapGroupLayer();
            this.loadBasicLayerInfo(groupLayer, element);
            final XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                this.loadProperties(groupLayer.getProperties(), properties);
            }
            for (int i = 0, j = element.getChildCount(); i < j; ++i) {
                final XmlReader.Element child = element.getChild(i);
                this.loadLayer(map, groupLayer.getLayers(), child, tmxFile, imageResolver);
            }
            for (final MapLayer layer : groupLayer.getLayers()) {
                layer.setParent(groupLayer);
            }
            parentLayers.add(groupLayer);
        }
    }
    
    protected void loadLayer(final TiledMap map, final MapLayers parentLayers, final XmlReader.Element element, final FileHandle tmxFile, final ImageResolver imageResolver) {
        final String name = element.getName();
        if (name.equals("group")) {
            this.loadTileGroup(map, parentLayers, element, tmxFile, imageResolver);
        }
        else if (name.equals("layer")) {
            this.loadTileLayer(map, parentLayers, element);
        }
        else if (name.equals("objectgroup")) {
            this.loadObjectGroup(map, parentLayers, element);
        }
        else if (name.equals("imagelayer")) {
            this.loadImageLayer(map, parentLayers, element, tmxFile, imageResolver);
        }
    }
    
    protected void loadTileLayer(final TiledMap map, final MapLayers parentLayers, final XmlReader.Element element) {
        if (element.getName().equals("layer")) {
            final int width = element.getIntAttribute("width", 0);
            final int height = element.getIntAttribute("height", 0);
            final int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            final int tileHeight = map.getProperties().get("tileheight", Integer.class);
            final TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
            this.loadBasicLayerInfo(layer, element);
            final int[] ids = getTileIds(element, width, height);
            final TiledMapTileSets tilesets = map.getTileSets();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    final int id = ids[y * width + x];
                    final boolean flipHorizontally = (id & Integer.MIN_VALUE) != 0x0;
                    final boolean flipVertically = (id & 0x40000000) != 0x0;
                    final boolean flipDiagonally = (id & 0x20000000) != 0x0;
                    final TiledMapTile tile = tilesets.getTile(id & 0x1FFFFFFF);
                    if (tile != null) {
                        final TiledMapTileLayer.Cell cell = this.createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                        cell.setTile(tile);
                        layer.setCell(x, this.flipY ? (height - 1 - y) : y, cell);
                    }
                }
            }
            final XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }
    
    protected void loadObjectGroup(final TiledMap map, final MapLayers parentLayers, final XmlReader.Element element) {
        if (element.getName().equals("objectgroup")) {
            final MapLayer layer = new MapLayer();
            this.loadBasicLayerInfo(layer, element);
            final XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            for (final XmlReader.Element objectElement : element.getChildrenByName("object")) {
                this.loadObject(map, layer, objectElement);
            }
            parentLayers.add(layer);
        }
    }
    
    protected void loadImageLayer(final TiledMap map, final MapLayers parentLayers, final XmlReader.Element element, final FileHandle tmxFile, final ImageResolver imageResolver) {
        if (element.getName().equals("imagelayer")) {
            int x = 0;
            int y = 0;
            if (element.hasAttribute("offsetx")) {
                x = Integer.parseInt(element.getAttribute("offsetx", "0"));
            }
            else {
                x = Integer.parseInt(element.getAttribute("x", "0"));
            }
            if (element.hasAttribute("offsety")) {
                y = Integer.parseInt(element.getAttribute("offsety", "0"));
            }
            else {
                y = Integer.parseInt(element.getAttribute("y", "0"));
            }
            if (this.flipY) {
                y = this.mapHeightInPixels - y;
            }
            TextureRegion texture = null;
            final XmlReader.Element image = element.getChildByName("image");
            if (image != null) {
                final String source = image.getAttribute("source");
                final FileHandle handle = getRelativeFileHandle(tmxFile, source);
                texture = imageResolver.getImage(handle.path());
                y -= texture.getRegionHeight();
            }
            final TiledMapImageLayer layer = new TiledMapImageLayer(texture, (float)x, (float)y);
            this.loadBasicLayerInfo(layer, element);
            final XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }
    
    protected void loadBasicLayerInfo(final MapLayer layer, final XmlReader.Element element) {
        final String name = element.getAttribute("name", null);
        final float opacity = Float.parseFloat(element.getAttribute("opacity", "1.0"));
        final boolean visible = element.getIntAttribute("visible", 1) == 1;
        final float offsetX = element.getFloatAttribute("offsetx", 0.0f);
        final float offsetY = element.getFloatAttribute("offsety", 0.0f);
        layer.setName(name);
        layer.setOpacity(opacity);
        layer.setVisible(visible);
        layer.setOffsetX(offsetX);
        layer.setOffsetY(offsetY);
    }
    
    protected void loadObject(final TiledMap map, final MapLayer layer, final XmlReader.Element element) {
        this.loadObject(map, layer.getObjects(), element, (float)this.mapHeightInPixels);
    }
    
    protected void loadObject(final TiledMap map, final TiledMapTile tile, final XmlReader.Element element) {
        this.loadObject(map, tile.getObjects(), element, (float)tile.getTextureRegion().getRegionHeight());
    }
    
    protected void loadObject(final TiledMap map, final MapObjects objects, final XmlReader.Element element, final float heightInPixels) {
        if (element.getName().equals("object")) {
            MapObject object = null;
            final float scaleX = this.convertObjectToTileSpace ? (1.0f / this.mapTileWidth) : 1.0f;
            final float scaleY = this.convertObjectToTileSpace ? (1.0f / this.mapTileHeight) : 1.0f;
            final float x = element.getFloatAttribute("x", 0.0f) * scaleX;
            final float y = (this.flipY ? (heightInPixels - element.getFloatAttribute("y", 0.0f)) : element.getFloatAttribute("y", 0.0f)) * scaleY;
            final float width = element.getFloatAttribute("width", 0.0f) * scaleX;
            final float height = element.getFloatAttribute("height", 0.0f) * scaleY;
            if (element.getChildCount() > 0) {
                XmlReader.Element child = null;
                if ((child = element.getChildByName("polygon")) != null) {
                    final String[] points = child.getAttribute("points").split(" ");
                    final float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; ++i) {
                        final String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (this.flipY ? -1 : 1);
                    }
                    final Polygon polygon = new Polygon(vertices);
                    polygon.setPosition(x, y);
                    object = new PolygonMapObject(polygon);
                }
                else if ((child = element.getChildByName("polyline")) != null) {
                    final String[] points = child.getAttribute("points").split(" ");
                    final float[] vertices = new float[points.length * 2];
                    for (int i = 0; i < points.length; ++i) {
                        final String[] point = points[i].split(",");
                        vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
                        vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (this.flipY ? -1 : 1);
                    }
                    final Polyline polyline = new Polyline(vertices);
                    polyline.setPosition(x, y);
                    object = new PolylineMapObject(polyline);
                }
                else if ((child = element.getChildByName("ellipse")) != null) {
                    object = new EllipseMapObject(x, this.flipY ? (y - height) : y, width, height);
                }
            }
            if (object == null) {
                String gid = null;
                if ((gid = element.getAttribute("gid", null)) != null) {
                    final int id = (int)Long.parseLong(gid);
                    final boolean flipHorizontally = (id & Integer.MIN_VALUE) != 0x0;
                    final boolean flipVertically = (id & 0x40000000) != 0x0;
                    final TiledMapTile tile = map.getTileSets().getTile(id & 0x1FFFFFFF);
                    final TiledMapTileMapObject tiledMapTileMapObject = new TiledMapTileMapObject(tile, flipHorizontally, flipVertically);
                    final TextureRegion textureRegion = tiledMapTileMapObject.getTextureRegion();
                    tiledMapTileMapObject.getProperties().put("gid", id);
                    tiledMapTileMapObject.setX(x);
                    tiledMapTileMapObject.setY(this.flipY ? y : (y - height));
                    final float objectWidth = element.getFloatAttribute("width", (float)textureRegion.getRegionWidth());
                    final float objectHeight = element.getFloatAttribute("height", (float)textureRegion.getRegionHeight());
                    tiledMapTileMapObject.setScaleX(scaleX * (objectWidth / textureRegion.getRegionWidth()));
                    tiledMapTileMapObject.setScaleY(scaleY * (objectHeight / textureRegion.getRegionHeight()));
                    tiledMapTileMapObject.setRotation(element.getFloatAttribute("rotation", 0.0f));
                    object = tiledMapTileMapObject;
                }
                else {
                    object = new RectangleMapObject(x, this.flipY ? (y - height) : y, width, height);
                }
            }
            object.setName(element.getAttribute("name", null));
            final String rotation = element.getAttribute("rotation", null);
            if (rotation != null) {
                object.getProperties().put("rotation", Float.parseFloat(rotation));
            }
            final String type = element.getAttribute("type", null);
            if (type != null) {
                object.getProperties().put("type", type);
            }
            final int id2 = element.getIntAttribute("id", 0);
            if (id2 != 0) {
                object.getProperties().put("id", id2);
            }
            object.getProperties().put("x", x);
            if (object instanceof TiledMapTileMapObject) {
                object.getProperties().put("y", y);
            }
            else {
                object.getProperties().put("y", this.flipY ? (y - height) : y);
            }
            object.getProperties().put("width", width);
            object.getProperties().put("height", height);
            object.setVisible(element.getIntAttribute("visible", 1) == 1);
            final XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                this.loadProperties(object.getProperties(), properties);
            }
            objects.add(object);
        }
    }
    
    protected void loadProperties(final MapProperties properties, final XmlReader.Element element) {
        if (element == null) {
            return;
        }
        if (element.getName().equals("properties")) {
            for (final XmlReader.Element property : element.getChildrenByName("property")) {
                final String name = property.getAttribute("name", null);
                String value = property.getAttribute("value", null);
                final String type = property.getAttribute("type", null);
                if (value == null) {
                    value = property.getText();
                }
                final Object castValue = this.castProperty(name, value, type);
                properties.put(name, castValue);
            }
        }
    }
    
    private Object castProperty(final String name, final String value, final String type) {
        if (type == null) {
            return value;
        }
        if (type.equals("int")) {
            return Integer.valueOf(value);
        }
        if (type.equals("float")) {
            return Float.valueOf(value);
        }
        if (type.equals("bool")) {
            return Boolean.valueOf(value);
        }
        if (type.equals("color")) {
            final String opaqueColor = value.substring(3);
            final String alpha = value.substring(1, 3);
            return Color.valueOf(String.valueOf(opaqueColor) + alpha);
        }
        throw new GdxRuntimeException("Wrong type given for property " + name + ", given : " + type + ", supported : string, bool, int, float, color");
    }
    
    protected TiledMapTileLayer.Cell createTileLayerCell(final boolean flipHorizontally, final boolean flipVertically, final boolean flipDiagonally) {
        final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        if (flipDiagonally) {
            if (flipHorizontally && flipVertically) {
                cell.setFlipHorizontally(true);
                cell.setRotation(3);
            }
            else if (flipHorizontally) {
                cell.setRotation(3);
            }
            else if (flipVertically) {
                cell.setRotation(1);
            }
            else {
                cell.setFlipVertically(true);
                cell.setRotation(3);
            }
        }
        else {
            cell.setFlipHorizontally(flipHorizontally);
            cell.setFlipVertically(flipVertically);
        }
        return cell;
    }
    
    public static int[] getTileIds(final XmlReader.Element element, final int width, final int height) {
        final XmlReader.Element data = element.getChildByName("data");
        final String encoding = data.getAttribute("encoding", null);
        if (encoding == null) {
            throw new GdxRuntimeException("Unsupported encoding (XML) for TMX Layer Data");
        }
        final int[] ids = new int[width * height];
        if (encoding.equals("csv")) {
            final String[] array = data.getText().split(",");
            for (int i = 0; i < array.length; ++i) {
                ids[i] = (int)Long.parseLong(array[i].trim());
            }
        }
        else {
            if (!encoding.equals("base64")) {
                throw new GdxRuntimeException("Unrecognised encoding (" + encoding + ") for TMX Layer Data");
            }
            InputStream is = null;
            try {
                final String compression = data.getAttribute("compression", null);
                final byte[] bytes = Base64Coder.decode(data.getText());
                if (compression == null) {
                    is = new ByteArrayInputStream(bytes);
                }
                else if (compression.equals("gzip")) {
                    is = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length));
                }
                else {
                    if (!compression.equals("zlib")) {
                        throw new GdxRuntimeException("Unrecognised compression (" + compression + ") for TMX Layer Data");
                    }
                    is = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
                }
                final byte[] temp = new byte[4];
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        int read;
                        int curr;
                        for (read = is.read(temp); read < temp.length; read += curr) {
                            curr = is.read(temp, read, temp.length - read);
                            if (curr == -1) {
                                break;
                            }
                        }
                        if (read != temp.length) {
                            throw new GdxRuntimeException("Error Reading TMX Layer Data: Premature end of tile data");
                        }
                        ids[y * width + x] = (unsignedByteToInt(temp[0]) | unsignedByteToInt(temp[1]) << 8 | unsignedByteToInt(temp[2]) << 16 | unsignedByteToInt(temp[3]) << 24);
                    }
                }
            }
            catch (IOException e) {
                throw new GdxRuntimeException("Error Reading TMX Layer Data - IOException: " + e.getMessage());
            }
            finally {
                StreamUtils.closeQuietly(is);
            }
            StreamUtils.closeQuietly(is);
        }
        return ids;
    }
    
    protected static int unsignedByteToInt(final byte b) {
        return b & 0xFF;
    }
    
    protected static FileHandle getRelativeFileHandle(final FileHandle file, final String path) {
        final StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            final String token = tokenizer.nextToken();
            if (token.equals("..")) {
                result = result.parent();
            }
            else {
                result = result.child(token);
            }
        }
        return result;
    }
    
    public static class Parameters extends AssetLoaderParameters<TiledMap>
    {
        public boolean generateMipMaps;
        public Texture.TextureFilter textureMinFilter;
        public Texture.TextureFilter textureMagFilter;
        public boolean convertObjectToTileSpace;
        public boolean flipY;
        
        public Parameters() {
            this.generateMipMaps = false;
            this.textureMinFilter = Texture.TextureFilter.Nearest;
            this.textureMagFilter = Texture.TextureFilter.Nearest;
            this.convertObjectToTileSpace = false;
            this.flipY = true;
        }
    }
}

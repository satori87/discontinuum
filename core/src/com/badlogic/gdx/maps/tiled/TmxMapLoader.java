// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import java.util.Iterator;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class TmxMapLoader extends BaseTmxMapLoader<Parameters>
{
    public TmxMapLoader() {
        super(new InternalFileHandleResolver());
    }
    
    public TmxMapLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    public TiledMap load(final String fileName) {
        return this.load(fileName, new Parameters());
    }
    
    public TiledMap load(final String fileName, final Parameters parameters) {
        try {
            this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
            this.flipY = parameters.flipY;
            final FileHandle tmxFile = this.resolve(fileName);
            this.root = this.xml.parse(tmxFile);
            final ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
            final Array<FileHandle> textureFiles = this.loadTilesets(this.root, tmxFile);
            textureFiles.addAll(this.loadImages(this.root, tmxFile));
            for (final FileHandle textureFile : textureFiles) {
                final Texture texture = new Texture(textureFile, parameters.generateMipMaps);
                texture.setFilter(parameters.textureMinFilter, parameters.textureMagFilter);
                textures.put(textureFile.path(), texture);
            }
            final ImageResolver.DirectImageResolver imageResolver = new ImageResolver.DirectImageResolver(textures);
            final TiledMap map = this.loadTilemap(this.root, tmxFile, imageResolver);
            map.setOwnedResources(textures.values().toArray());
            return map;
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle tmxFile, final Parameters parameter) {
        this.map = null;
        if (parameter != null) {
            this.convertObjectToTileSpace = parameter.convertObjectToTileSpace;
            this.flipY = parameter.flipY;
        }
        else {
            this.convertObjectToTileSpace = false;
            this.flipY = true;
        }
        try {
            this.map = this.loadTilemap(this.root, tmxFile, new ImageResolver.AssetManagerImageResolver(manager));
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    public TiledMap loadSync(final AssetManager manager, final String fileName, final FileHandle file, final Parameters parameter) {
        return this.map;
    }
    
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle tmxFile, final Parameters parameter) {
        final Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        try {
            this.root = this.xml.parse(tmxFile);
            final boolean generateMipMaps = parameter != null && parameter.generateMipMaps;
            final TextureLoader.TextureParameter texParams = new TextureLoader.TextureParameter();
            texParams.genMipMaps = generateMipMaps;
            if (parameter != null) {
                texParams.minFilter = parameter.textureMinFilter;
                texParams.magFilter = parameter.textureMagFilter;
            }
            for (final FileHandle image : this.loadTilesets(this.root, tmxFile)) {
                dependencies.add(new AssetDescriptor(image, Texture.class, texParams));
            }
            for (final FileHandle image : this.loadImages(this.root, tmxFile)) {
                dependencies.add(new AssetDescriptor(image, Texture.class, texParams));
            }
            return dependencies;
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    protected TiledMap loadTilemap(final XmlReader.Element root, final FileHandle tmxFile, final ImageResolver imageResolver) {
        final TiledMap map = new TiledMap();
        final String mapOrientation = root.getAttribute("orientation", null);
        final int mapWidth = root.getIntAttribute("width", 0);
        final int mapHeight = root.getIntAttribute("height", 0);
        final int tileWidth = root.getIntAttribute("tilewidth", 0);
        final int tileHeight = root.getIntAttribute("tileheight", 0);
        final int hexSideLength = root.getIntAttribute("hexsidelength", 0);
        final String staggerAxis = root.getAttribute("staggeraxis", null);
        final String staggerIndex = root.getAttribute("staggerindex", null);
        final String mapBackgroundColor = root.getAttribute("backgroundcolor", null);
        final MapProperties mapProperties = map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        mapProperties.put("hexsidelength", hexSideLength);
        if (staggerAxis != null) {
            mapProperties.put("staggeraxis", staggerAxis);
        }
        if (staggerIndex != null) {
            mapProperties.put("staggerindex", staggerIndex);
        }
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        this.mapTileWidth = tileWidth;
        this.mapTileHeight = tileHeight;
        this.mapWidthInPixels = mapWidth * tileWidth;
        this.mapHeightInPixels = mapHeight * tileHeight;
        if (mapOrientation != null && "staggered".equals(mapOrientation) && mapHeight > 1) {
            this.mapWidthInPixels += tileWidth / 2;
            this.mapHeightInPixels = this.mapHeightInPixels / 2 + tileHeight / 2;
        }
        final XmlReader.Element properties = root.getChildByName("properties");
        if (properties != null) {
            this.loadProperties(map.getProperties(), properties);
        }
        final Array<XmlReader.Element> tilesets = root.getChildrenByName("tileset");
        for (final XmlReader.Element element : tilesets) {
            this.loadTileSet(map, element, tmxFile, imageResolver);
            root.removeChild(element);
        }
        for (int i = 0, j = root.getChildCount(); i < j; ++i) {
            final XmlReader.Element element2 = root.getChild(i);
            this.loadLayer(map, map.getLayers(), element2, tmxFile, imageResolver);
        }
        return map;
    }
    
    protected Array<FileHandle> loadTilesets(final XmlReader.Element root, final FileHandle tmxFile) throws IOException {
        final Array<FileHandle> images = new Array<FileHandle>();
        for (XmlReader.Element tileset : root.getChildrenByName("tileset")) {
            final String source = tileset.getAttribute("source", null);
            if (source != null) {
                final FileHandle tsxFile = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, source);
                tileset = this.xml.parse(tsxFile);
                final XmlReader.Element imageElement = tileset.getChildByName("image");
                if (imageElement != null) {
                    final String imageSource = tileset.getChildByName("image").getAttribute("source");
                    final FileHandle image = BaseTmxMapLoader.getRelativeFileHandle(tsxFile, imageSource);
                    images.add(image);
                }
                else {
                    for (final XmlReader.Element tile : tileset.getChildrenByName("tile")) {
                        final String imageSource2 = tile.getChildByName("image").getAttribute("source");
                        final FileHandle image2 = BaseTmxMapLoader.getRelativeFileHandle(tsxFile, imageSource2);
                        images.add(image2);
                    }
                }
            }
            else {
                final XmlReader.Element imageElement2 = tileset.getChildByName("image");
                if (imageElement2 != null) {
                    final String imageSource3 = tileset.getChildByName("image").getAttribute("source");
                    final FileHandle image3 = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, imageSource3);
                    images.add(image3);
                }
                else {
                    for (final XmlReader.Element tile2 : tileset.getChildrenByName("tile")) {
                        final String imageSource4 = tile2.getChildByName("image").getAttribute("source");
                        final FileHandle image4 = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, imageSource4);
                        images.add(image4);
                    }
                }
            }
        }
        return images;
    }
    
    protected Array<FileHandle> loadImages(final XmlReader.Element root, final FileHandle tmxFile) throws IOException {
        final Array<FileHandle> images = new Array<FileHandle>();
        for (final XmlReader.Element imageLayer : root.getChildrenByName("imagelayer")) {
            final XmlReader.Element image = imageLayer.getChildByName("image");
            final String source = image.getAttribute("source", null);
            if (source != null) {
                final FileHandle handle = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, source);
                if (images.contains(handle, false)) {
                    continue;
                }
                images.add(handle);
            }
        }
        return images;
    }
    
    protected void loadTileSet(final TiledMap map, XmlReader.Element element, final FileHandle tmxFile, final ImageResolver imageResolver) {
        if (element.getName().equals("tileset")) {
            String name = element.get("name", null);
            final int firstgid = element.getIntAttribute("firstgid", 1);
            int tilewidth = element.getIntAttribute("tilewidth", 0);
            int tileheight = element.getIntAttribute("tileheight", 0);
            int spacing = element.getIntAttribute("spacing", 0);
            int margin = element.getIntAttribute("margin", 0);
            final String source = element.getAttribute("source", null);
            int offsetX = 0;
            int offsetY = 0;
            String imageSource = "";
            int imageWidth = 0;
            int imageHeight = 0;
            FileHandle image = null;
            Label_0365: {
                if (source != null) {
                    final FileHandle tsx = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, source);
                    try {
                        element = this.xml.parse(tsx);
                        name = element.get("name", null);
                        tilewidth = element.getIntAttribute("tilewidth", 0);
                        tileheight = element.getIntAttribute("tileheight", 0);
                        spacing = element.getIntAttribute("spacing", 0);
                        margin = element.getIntAttribute("margin", 0);
                        final XmlReader.Element offset = element.getChildByName("tileoffset");
                        if (offset != null) {
                            offsetX = offset.getIntAttribute("x", 0);
                            offsetY = offset.getIntAttribute("y", 0);
                        }
                        final XmlReader.Element imageElement = element.getChildByName("image");
                        if (imageElement != null) {
                            imageSource = imageElement.getAttribute("source");
                            imageWidth = imageElement.getIntAttribute("width", 0);
                            imageHeight = imageElement.getIntAttribute("height", 0);
                            image = BaseTmxMapLoader.getRelativeFileHandle(tsx, imageSource);
                        }
                        break Label_0365;
                    }
                    catch (SerializationException e) {
                        throw new GdxRuntimeException("Error parsing external tileset.");
                    }
                }
                final XmlReader.Element offset2 = element.getChildByName("tileoffset");
                if (offset2 != null) {
                    offsetX = offset2.getIntAttribute("x", 0);
                    offsetY = offset2.getIntAttribute("y", 0);
                }
                final XmlReader.Element imageElement2 = element.getChildByName("image");
                if (imageElement2 != null) {
                    imageSource = imageElement2.getAttribute("source");
                    imageWidth = imageElement2.getIntAttribute("width", 0);
                    imageHeight = imageElement2.getIntAttribute("height", 0);
                    image = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, imageSource);
                }
            }
            final TiledMapTileSet tileset = new TiledMapTileSet();
            tileset.setName(name);
            tileset.getProperties().put("firstgid", firstgid);
            if (image != null) {
                final TextureRegion texture = imageResolver.getImage(image.path());
                final MapProperties props = tileset.getProperties();
                props.put("imagesource", imageSource);
                props.put("imagewidth", imageWidth);
                props.put("imageheight", imageHeight);
                props.put("tilewidth", tilewidth);
                props.put("tileheight", tileheight);
                props.put("margin", margin);
                props.put("spacing", spacing);
                final int stopWidth = texture.getRegionWidth() - tilewidth;
                final int stopHeight = texture.getRegionHeight() - tileheight;
                int id = firstgid;
                for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
                    for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
                        final TextureRegion tileRegion = new TextureRegion(texture, x, y, tilewidth, tileheight);
                        final TiledMapTile tile = new StaticTiledMapTile(tileRegion);
                        tile.setId(id);
                        tile.setOffsetX((float)offsetX);
                        tile.setOffsetY((float)(this.flipY ? (-offsetY) : offsetY));
                        tileset.putTile(id++, tile);
                    }
                }
            }
            else {
                final Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");
                for (final XmlReader.Element tileElement : tileElements) {
                    final XmlReader.Element imageElement3 = tileElement.getChildByName("image");
                    if (imageElement3 != null) {
                        imageSource = imageElement3.getAttribute("source");
                        imageWidth = imageElement3.getIntAttribute("width", 0);
                        imageHeight = imageElement3.getIntAttribute("height", 0);
                        if (source != null) {
                            image = BaseTmxMapLoader.getRelativeFileHandle(BaseTmxMapLoader.getRelativeFileHandle(tmxFile, source), imageSource);
                        }
                        else {
                            image = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, imageSource);
                        }
                    }
                    final TextureRegion texture2 = imageResolver.getImage(image.path());
                    final TiledMapTile tile2 = new StaticTiledMapTile(texture2);
                    tile2.setId(firstgid + tileElement.getIntAttribute("id"));
                    tile2.setOffsetX((float)offsetX);
                    tile2.setOffsetY((float)(this.flipY ? (-offsetY) : offsetY));
                    tileset.putTile(tile2.getId(), tile2);
                }
            }
            final Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");
            final Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();
            for (final XmlReader.Element tileElement2 : tileElements) {
                final int localtid = tileElement2.getIntAttribute("id", 0);
                TiledMapTile tile2 = tileset.getTile(firstgid + localtid);
                if (tile2 != null) {
                    final XmlReader.Element animationElement = tileElement2.getChildByName("animation");
                    if (animationElement != null) {
                        final Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        final IntArray intervals = new IntArray();
                        for (final XmlReader.Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile)tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }
                        final AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile2.getId());
                        animatedTiles.add(animatedTile);
                        tile2 = animatedTile;
                    }
                    final XmlReader.Element objectgroupElement = tileElement2.getChildByName("objectgroup");
                    if (objectgroupElement != null) {
                        for (final XmlReader.Element objectElement : objectgroupElement.getChildrenByName("object")) {
                            this.loadObject(map, tile2, objectElement);
                        }
                    }
                    final String terrain = tileElement2.getAttribute("terrain", null);
                    if (terrain != null) {
                        tile2.getProperties().put("terrain", terrain);
                    }
                    final String probability = tileElement2.getAttribute("probability", null);
                    if (probability != null) {
                        tile2.getProperties().put("probability", probability);
                    }
                    final XmlReader.Element properties = tileElement2.getChildByName("properties");
                    if (properties == null) {
                        continue;
                    }
                    this.loadProperties(tile2.getProperties(), properties);
                }
            }
            for (final AnimatedTiledMapTile tile3 : animatedTiles) {
                tileset.putTile(tile3.getId(), tile3);
            }
            final XmlReader.Element properties2 = element.getChildByName("properties");
            if (properties2 != null) {
                this.loadProperties(tileset.getProperties(), properties2);
            }
            map.getTileSets().addTileSet(tileset);
        }
    }
    
    public static class Parameters extends BaseTmxMapLoader.Parameters
    {
    }
}

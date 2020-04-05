// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.assets.AssetManager;
import java.io.IOException;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class AtlasTmxMapLoader extends BaseTmxMapLoader<AtlasTiledMapLoaderParameters>
{
    protected Array<Texture> trackedTextures;
    
    public AtlasTmxMapLoader() {
        super(new InternalFileHandleResolver());
        this.trackedTextures = new Array<Texture>();
    }
    
    public AtlasTmxMapLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.trackedTextures = new Array<Texture>();
    }
    
    public TiledMap load(final String fileName) {
        return this.load(fileName, new AtlasTiledMapLoaderParameters());
    }
    
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle tmxFile, final AtlasTiledMapLoaderParameters parameter) {
        final Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        try {
            this.root = this.xml.parse(tmxFile);
            final XmlReader.Element properties = this.root.getChildByName("properties");
            if (properties != null) {
                for (final XmlReader.Element property : properties.getChildrenByName("property")) {
                    final String name = property.getAttribute("name");
                    final String value = property.getAttribute("value");
                    if (name.startsWith("atlas")) {
                        final FileHandle atlasHandle = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, value);
                        dependencies.add(new AssetDescriptor(atlasHandle, TextureAtlas.class));
                    }
                }
            }
        }
        catch (SerializationException e) {
            throw new GdxRuntimeException("Unable to parse .tmx file.");
        }
        return dependencies;
    }
    
    public TiledMap load(final String fileName, final AtlasTiledMapLoaderParameters parameter) {
        try {
            if (parameter != null) {
                this.convertObjectToTileSpace = parameter.convertObjectToTileSpace;
                this.flipY = parameter.flipY;
            }
            else {
                this.convertObjectToTileSpace = false;
                this.flipY = true;
            }
            final FileHandle tmxFile = this.resolve(fileName);
            this.root = this.xml.parse(tmxFile);
            final ObjectMap<String, TextureAtlas> atlases = new ObjectMap<String, TextureAtlas>();
            final FileHandle atlasFile = this.loadAtlas(this.root, tmxFile);
            if (atlasFile == null) {
                throw new GdxRuntimeException("Couldn't load atlas");
            }
            final TextureAtlas atlas = new TextureAtlas(atlasFile);
            atlases.put(atlasFile.path(), atlas);
            final AtlasResolver.DirectAtlasResolver atlasResolver = new AtlasResolver.DirectAtlasResolver(atlases);
            final TiledMap map = this.loadMap(this.root, tmxFile, atlasResolver);
            map.setOwnedResources(atlases.values().toArray());
            this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
            return map;
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    protected FileHandle loadAtlas(final XmlReader.Element root, final FileHandle tmxFile) throws IOException {
        final XmlReader.Element e = root.getChildByName("properties");
        if (e != null) {
            for (final XmlReader.Element property : e.getChildrenByName("property")) {
                final String name = property.getAttribute("name", null);
                String value = property.getAttribute("value", null);
                if (name.equals("atlas")) {
                    if (value == null) {
                        value = property.getText();
                    }
                    if (value == null) {
                        continue;
                    }
                    if (value.length() == 0) {
                        continue;
                    }
                    return BaseTmxMapLoader.getRelativeFileHandle(tmxFile, value);
                }
            }
        }
        final FileHandle atlasFile = tmxFile.sibling(String.valueOf(tmxFile.nameWithoutExtension()) + ".atlas");
        return atlasFile.exists() ? atlasFile : null;
    }
    
    private void setTextureFilters(final Texture.TextureFilter min, final Texture.TextureFilter mag) {
        for (final Texture texture : this.trackedTextures) {
            texture.setFilter(min, mag);
        }
        this.trackedTextures.clear();
    }
    
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle tmxFile, final AtlasTiledMapLoaderParameters parameter) {
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
            this.map = this.loadMap(this.root, tmxFile, new AtlasResolver.AssetManagerAtlasResolver(manager));
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    public TiledMap loadSync(final AssetManager manager, final String fileName, final FileHandle file, final AtlasTiledMapLoaderParameters parameter) {
        if (parameter != null) {
            this.setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        }
        return this.map;
    }
    
    protected TiledMap loadMap(final XmlReader.Element root, final FileHandle tmxFile, final AtlasResolver resolver) {
        final TiledMap map = new TiledMap();
        final String mapOrientation = root.getAttribute("orientation", null);
        final int mapWidth = root.getIntAttribute("width", 0);
        final int mapHeight = root.getIntAttribute("height", 0);
        final int tileWidth = root.getIntAttribute("tilewidth", 0);
        final int tileHeight = root.getIntAttribute("tileheight", 0);
        final String mapBackgroundColor = root.getAttribute("backgroundcolor", null);
        final MapProperties mapProperties = map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
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
        for (int i = 0, j = root.getChildCount(); i < j; ++i) {
            final XmlReader.Element element = root.getChild(i);
            final String elementName = element.getName();
            if (elementName.equals("properties")) {
                this.loadProperties(map.getProperties(), element);
            }
            else if (elementName.equals("tileset")) {
                this.loadTileset(map, element, tmxFile, resolver);
            }
            else if (elementName.equals("layer")) {
                this.loadTileLayer(map, map.getLayers(), element);
            }
            else if (elementName.equals("objectgroup")) {
                this.loadObjectGroup(map, map.getLayers(), element);
            }
        }
        return map;
    }
    
    protected void loadTileset(final TiledMap map, XmlReader.Element element, final FileHandle tmxFile, final AtlasResolver resolver) {
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
            Label_0371: {
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
                        break Label_0371;
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
            String atlasFilePath = map.getProperties().get("atlas", String.class);
            if (atlasFilePath == null) {
                final FileHandle atlasFile = tmxFile.sibling(String.valueOf(tmxFile.nameWithoutExtension()) + ".atlas");
                if (atlasFile.exists()) {
                    atlasFilePath = atlasFile.name();
                }
            }
            if (atlasFilePath == null) {
                throw new GdxRuntimeException("The map is missing the 'atlas' property");
            }
            FileHandle atlasHandle = BaseTmxMapLoader.getRelativeFileHandle(tmxFile, atlasFilePath);
            atlasHandle = this.resolve(atlasHandle.path());
            final TextureAtlas atlas = resolver.getAtlas(atlasHandle.path());
            final String regionsName = name;
            for (final Texture texture : atlas.getTextures()) {
                this.trackedTextures.add(texture);
            }
            final TiledMapTileSet tileset = new TiledMapTileSet();
            final MapProperties props = tileset.getProperties();
            tileset.setName(name);
            props.put("firstgid", firstgid);
            props.put("imagesource", imageSource);
            props.put("imagewidth", imageWidth);
            props.put("imageheight", imageHeight);
            props.put("tilewidth", tilewidth);
            props.put("tileheight", tileheight);
            props.put("margin", margin);
            props.put("spacing", spacing);
            if (imageSource != null && imageSource.length() > 0) {
                final int lastgid = firstgid + imageWidth / tilewidth * (imageHeight / tileheight) - 1;
                for (final TextureAtlas.AtlasRegion region : atlas.findRegions(regionsName)) {
                    if (region != null) {
                        final int tileid = region.index + firstgid;
                        if (tileid < firstgid || tileid > lastgid) {
                            continue;
                        }
                        final StaticTiledMapTile tile = new StaticTiledMapTile(region);
                        tile.setId(tileid);
                        tile.setOffsetX((float)offsetX);
                        tile.setOffsetY((float)(this.flipY ? (-offsetY) : offsetY));
                        tileset.putTile(tileid, tile);
                    }
                }
            }
            for (final XmlReader.Element tileElement : element.getChildrenByName("tile")) {
                final int tileid2 = firstgid + tileElement.getIntAttribute("id", 0);
                TiledMapTile tile2 = tileset.getTile(tileid2);
                if (tile2 == null) {
                    final XmlReader.Element imageElement3 = tileElement.getChildByName("image");
                    if (imageElement3 != null) {
                        String regionName = imageElement3.getAttribute("source");
                        regionName = regionName.substring(0, regionName.lastIndexOf(46));
                        final TextureAtlas.AtlasRegion region2 = atlas.findRegion(regionName);
                        if (region2 == null) {
                            throw new GdxRuntimeException("Tileset region not found: " + regionName);
                        }
                        tile2 = new StaticTiledMapTile(region2);
                        tile2.setId(tileid2);
                        tile2.setOffsetX((float)offsetX);
                        tile2.setOffsetY((float)(this.flipY ? (-offsetY) : offsetY));
                        tileset.putTile(tileid2, tile2);
                    }
                }
                if (tile2 != null) {
                    final String terrain = tileElement.getAttribute("terrain", null);
                    if (terrain != null) {
                        tile2.getProperties().put("terrain", terrain);
                    }
                    final String probability = tileElement.getAttribute("probability", null);
                    if (probability != null) {
                        tile2.getProperties().put("probability", probability);
                    }
                    final XmlReader.Element properties = tileElement.getChildByName("properties");
                    if (properties == null) {
                        continue;
                    }
                    this.loadProperties(tile2.getProperties(), properties);
                }
            }
            final Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");
            final Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();
            for (final XmlReader.Element tileElement2 : tileElements) {
                final int localtid = tileElement2.getIntAttribute("id", 0);
                TiledMapTile tile3 = tileset.getTile(firstgid + localtid);
                if (tile3 != null) {
                    final XmlReader.Element animationElement = tileElement2.getChildByName("animation");
                    if (animationElement != null) {
                        final Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
                        final IntArray intervals = new IntArray();
                        for (final XmlReader.Element frameElement : animationElement.getChildrenByName("frame")) {
                            staticTiles.add((StaticTiledMapTile)tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }
                        final AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile3.getId());
                        animatedTiles.add(animatedTile);
                        tile3 = animatedTile;
                    }
                    final XmlReader.Element objectgroupElement = tileElement2.getChildByName("objectgroup");
                    if (objectgroupElement != null) {
                        for (final XmlReader.Element objectElement : objectgroupElement.getChildrenByName("object")) {
                            this.loadObject(map, tile3, objectElement);
                        }
                    }
                    final String terrain2 = tileElement2.getAttribute("terrain", null);
                    if (terrain2 != null) {
                        tile3.getProperties().put("terrain", terrain2);
                    }
                    final String probability2 = tileElement2.getAttribute("probability", null);
                    if (probability2 != null) {
                        tile3.getProperties().put("probability", probability2);
                    }
                    final XmlReader.Element properties2 = tileElement2.getChildByName("properties");
                    if (properties2 == null) {
                        continue;
                    }
                    this.loadProperties(tile3.getProperties(), properties2);
                }
            }
            for (final AnimatedTiledMapTile tile4 : animatedTiles) {
                tileset.putTile(tile4.getId(), tile4);
            }
            final XmlReader.Element properties3 = element.getChildByName("properties");
            if (properties3 != null) {
                this.loadProperties(tileset.getProperties(), properties3);
            }
            map.getTileSets().addTileSet(tileset);
        }
    }
    
    public static class AtlasTiledMapLoaderParameters extends Parameters
    {
        public boolean forceTextureFilters;
        
        public AtlasTiledMapLoaderParameters() {
            this.forceTextureFilters = false;
        }
    }
    
    private interface AtlasResolver
    {
        TextureAtlas getAtlas(final String p0);
        
        public static class DirectAtlasResolver implements AtlasResolver
        {
            private final ObjectMap<String, TextureAtlas> atlases;
            
            public DirectAtlasResolver(final ObjectMap<String, TextureAtlas> atlases) {
                this.atlases = atlases;
            }
            
            @Override
            public TextureAtlas getAtlas(final String name) {
                return this.atlases.get(name);
            }
        }
        
        public static class AssetManagerAtlasResolver implements AtlasResolver
        {
            private final AssetManager assetManager;
            
            public AssetManagerAtlasResolver(final AssetManager assetManager) {
                this.assetManager = assetManager;
            }
            
            @Override
            public TextureAtlas getAtlas(final String name) {
                return this.assetManager.get(name, TextureAtlas.class);
            }
        }
    }
}

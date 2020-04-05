// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import java.util.StringTokenizer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import java.util.Iterator;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;

public class TideMapLoader extends SynchronousAssetLoader<TiledMap, Parameters>
{
    private XmlReader xml;
    private XmlReader.Element root;
    
    public TideMapLoader() {
        super(new InternalFileHandleResolver());
        this.xml = new XmlReader();
    }
    
    public TideMapLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.xml = new XmlReader();
    }
    
    public TiledMap load(final String fileName) {
        try {
            final FileHandle tideFile = this.resolve(fileName);
            this.root = this.xml.parse(tideFile);
            final ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
            for (final FileHandle textureFile : this.loadTileSheets(this.root, tideFile)) {
                textures.put(textureFile.path(), new Texture(textureFile));
            }
            final ImageResolver.DirectImageResolver imageResolver = new ImageResolver.DirectImageResolver(textures);
            final TiledMap map = this.loadMap(this.root, tideFile, imageResolver);
            map.setOwnedResources(textures.values().toArray());
            return map;
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    @Override
    public TiledMap load(final AssetManager assetManager, final String fileName, final FileHandle tideFile, final Parameters parameter) {
        try {
            return this.loadMap(this.root, tideFile, new ImageResolver.AssetManagerImageResolver(assetManager));
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle tmxFile, final Parameters parameter) {
        final Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        try {
            this.root = this.xml.parse(tmxFile);
            for (final FileHandle image : this.loadTileSheets(this.root, tmxFile)) {
                dependencies.add(new AssetDescriptor(image.path(), Texture.class));
            }
            return dependencies;
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }
    
    private TiledMap loadMap(final XmlReader.Element root, final FileHandle tmxFile, final ImageResolver imageResolver) {
        final TiledMap map = new TiledMap();
        final XmlReader.Element properties = root.getChildByName("Properties");
        if (properties != null) {
            this.loadProperties(map.getProperties(), properties);
        }
        final XmlReader.Element tilesheets = root.getChildByName("TileSheets");
        for (final XmlReader.Element tilesheet : tilesheets.getChildrenByName("TileSheet")) {
            this.loadTileSheet(map, tilesheet, tmxFile, imageResolver);
        }
        final XmlReader.Element layers = root.getChildByName("Layers");
        for (final XmlReader.Element layer : layers.getChildrenByName("Layer")) {
            this.loadLayer(map, layer);
        }
        return map;
    }
    
    private Array<FileHandle> loadTileSheets(final XmlReader.Element root, final FileHandle tideFile) throws IOException {
        final Array<FileHandle> images = new Array<FileHandle>();
        final XmlReader.Element tilesheets = root.getChildByName("TileSheets");
        for (final XmlReader.Element tileset : tilesheets.getChildrenByName("TileSheet")) {
            final XmlReader.Element imageSource = tileset.getChildByName("ImageSource");
            final FileHandle image = getRelativeFileHandle(tideFile, imageSource.getText());
            images.add(image);
        }
        return images;
    }
    
    private void loadTileSheet(final TiledMap map, final XmlReader.Element element, final FileHandle tideFile, final ImageResolver imageResolver) {
        if (element.getName().equals("TileSheet")) {
            final String id = element.getAttribute("Id");
            final String description = element.getChildByName("Description").getText();
            final String imageSource = element.getChildByName("ImageSource").getText();
            final XmlReader.Element alignment = element.getChildByName("Alignment");
            final String sheetSize = alignment.getAttribute("SheetSize");
            final String tileSize = alignment.getAttribute("TileSize");
            final String margin = alignment.getAttribute("Margin");
            final String spacing = alignment.getAttribute("Spacing");
            final String[] sheetSizeParts = sheetSize.split(" x ");
            final int sheetSizeX = Integer.parseInt(sheetSizeParts[0]);
            final int sheetSizeY = Integer.parseInt(sheetSizeParts[1]);
            final String[] tileSizeParts = tileSize.split(" x ");
            final int tileSizeX = Integer.parseInt(tileSizeParts[0]);
            final int tileSizeY = Integer.parseInt(tileSizeParts[1]);
            final String[] marginParts = margin.split(" x ");
            final int marginX = Integer.parseInt(marginParts[0]);
            final int marginY = Integer.parseInt(marginParts[1]);
            final String[] spacingParts = margin.split(" x ");
            final int spacingX = Integer.parseInt(spacingParts[0]);
            final int spacingY = Integer.parseInt(spacingParts[1]);
            final FileHandle image = getRelativeFileHandle(tideFile, imageSource);
            final TextureRegion texture = imageResolver.getImage(image.path());
            final TiledMapTileSets tilesets = map.getTileSets();
            int firstgid = 1;
            for (final TiledMapTileSet tileset : tilesets) {
                firstgid += tileset.size();
            }
            TiledMapTileSet tileset = new TiledMapTileSet();
            tileset.setName(id);
            tileset.getProperties().put("firstgid", firstgid);
            int gid = firstgid;
            final int stopWidth = texture.getRegionWidth() - tileSizeX;
            for (int stopHeight = texture.getRegionHeight() - tileSizeY, y = marginY; y <= stopHeight; y += tileSizeY + spacingY) {
                for (int x = marginX; x <= stopWidth; x += tileSizeX + spacingX) {
                    final TiledMapTile tile = new StaticTiledMapTile(new TextureRegion(texture, x, y, tileSizeX, tileSizeY));
                    tile.setId(gid);
                    tileset.putTile(gid++, tile);
                }
            }
            final XmlReader.Element properties = element.getChildByName("Properties");
            if (properties != null) {
                this.loadProperties(tileset.getProperties(), properties);
            }
            tilesets.addTileSet(tileset);
        }
    }
    
    private void loadLayer(final TiledMap map, final XmlReader.Element element) {
        if (element.getName().equals("Layer")) {
            final String id = element.getAttribute("Id");
            final String visible = element.getAttribute("Visible");
            final XmlReader.Element dimensions = element.getChildByName("Dimensions");
            final String layerSize = dimensions.getAttribute("LayerSize");
            final String tileSize = dimensions.getAttribute("TileSize");
            final String[] layerSizeParts = layerSize.split(" x ");
            final int layerSizeX = Integer.parseInt(layerSizeParts[0]);
            final int layerSizeY = Integer.parseInt(layerSizeParts[1]);
            final String[] tileSizeParts = tileSize.split(" x ");
            final int tileSizeX = Integer.parseInt(tileSizeParts[0]);
            final int tileSizeY = Integer.parseInt(tileSizeParts[1]);
            final TiledMapTileLayer layer = new TiledMapTileLayer(layerSizeX, layerSizeY, tileSizeX, tileSizeY);
            layer.setName(id);
            layer.setVisible(visible.equalsIgnoreCase("True"));
            final XmlReader.Element tileArray = element.getChildByName("TileArray");
            final Array<XmlReader.Element> rows = tileArray.getChildrenByName("Row");
            final TiledMapTileSets tilesets = map.getTileSets();
            TiledMapTileSet currentTileSet = null;
            int firstgid = 0;
            for (int row = 0, rowCount = rows.size; row < rowCount; ++row) {
                final XmlReader.Element currentRow = rows.get(row);
                final int y = rowCount - 1 - row;
                int x = 0;
                for (int child = 0, childCount = currentRow.getChildCount(); child < childCount; ++child) {
                    final XmlReader.Element currentChild = currentRow.getChild(child);
                    final String name = currentChild.getName();
                    if (name.equals("TileSheet")) {
                        currentTileSet = tilesets.getTileSet(currentChild.getAttribute("Ref"));
                        firstgid = currentTileSet.getProperties().get("firstgid", Integer.class);
                    }
                    else if (name.equals("Null")) {
                        x += currentChild.getIntAttribute("Count");
                    }
                    else if (name.equals("Static")) {
                        final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(currentTileSet.getTile(firstgid + currentChild.getIntAttribute("Index")));
                        layer.setCell(x++, y, cell);
                    }
                    else if (name.equals("Animated")) {
                        final int interval = currentChild.getInt("Interval");
                        final XmlReader.Element frames = currentChild.getChildByName("Frames");
                        final Array<StaticTiledMapTile> frameTiles = new Array<StaticTiledMapTile>();
                        for (int frameChild = 0, frameChildCount = frames.getChildCount(); frameChild < frameChildCount; ++frameChild) {
                            final XmlReader.Element frame = frames.getChild(frameChild);
                            final String frameName = frame.getName();
                            if (frameName.equals("TileSheet")) {
                                currentTileSet = tilesets.getTileSet(frame.getAttribute("Ref"));
                                firstgid = currentTileSet.getProperties().get("firstgid", Integer.class);
                            }
                            else if (frameName.equals("Static")) {
                                frameTiles.add((StaticTiledMapTile)currentTileSet.getTile(firstgid + frame.getIntAttribute("Index")));
                            }
                        }
                        final TiledMapTileLayer.Cell cell2 = new TiledMapTileLayer.Cell();
                        cell2.setTile(new AnimatedTiledMapTile(interval / 1000.0f, frameTiles));
                        layer.setCell(x++, y, cell2);
                    }
                }
            }
            final XmlReader.Element properties = element.getChildByName("Properties");
            if (properties != null) {
                this.loadProperties(layer.getProperties(), properties);
            }
            map.getLayers().add(layer);
        }
    }
    
    private void loadProperties(final MapProperties properties, final XmlReader.Element element) {
        if (element.getName().equals("Properties")) {
            for (final XmlReader.Element property : element.getChildrenByName("Property")) {
                final String key = property.getAttribute("Key", null);
                final String type = property.getAttribute("Type", null);
                final String value = property.getText();
                if (type.equals("Int32")) {
                    properties.put(key, Integer.parseInt(value));
                }
                else if (type.equals("String")) {
                    properties.put(key, value);
                }
                else if (type.equals("Boolean")) {
                    properties.put(key, value.equalsIgnoreCase("true"));
                }
                else {
                    properties.put(key, value);
                }
            }
        }
    }
    
    private static FileHandle getRelativeFileHandle(final FileHandle file, final String path) {
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
    }
}

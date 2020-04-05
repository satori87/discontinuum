// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import java.util.Iterator;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Disposable;

public class Skin implements Disposable
{
    ObjectMap<Class, ObjectMap<String, Object>> resources;
    TextureAtlas atlas;
    private final ObjectMap<String, Class> jsonClassTags;
    private static final Class[] defaultTagClasses;
    
    static {
        defaultTagClasses = new Class[] { BitmapFont.class, Color.class, TintedDrawable.class, NinePatchDrawable.class, SpriteDrawable.class, TextureRegionDrawable.class, TiledDrawable.class, Button.ButtonStyle.class, CheckBox.CheckBoxStyle.class, ImageButton.ImageButtonStyle.class, ImageTextButton.ImageTextButtonStyle.class, Label.LabelStyle.class, List.ListStyle.class, ProgressBar.ProgressBarStyle.class, ScrollPane.ScrollPaneStyle.class, SelectBox.SelectBoxStyle.class, Slider.SliderStyle.class, SplitPane.SplitPaneStyle.class, TextButton.TextButtonStyle.class, TextField.TextFieldStyle.class, TextTooltip.TextTooltipStyle.class, Touchpad.TouchpadStyle.class, Tree.TreeStyle.class, Window.WindowStyle.class };
    }
    
    public Skin() {
        this.resources = new ObjectMap<Class, ObjectMap<String, Object>>();
        this.jsonClassTags = new ObjectMap<String, Class>(Skin.defaultTagClasses.length);
        Class[] defaultTagClasses;
        for (int length = (defaultTagClasses = Skin.defaultTagClasses).length, i = 0; i < length; ++i) {
            final Class c = defaultTagClasses[i];
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
    }
    
    public Skin(final FileHandle skinFile) {
        this.resources = new ObjectMap<Class, ObjectMap<String, Object>>();
        this.jsonClassTags = new ObjectMap<String, Class>(Skin.defaultTagClasses.length);
        Class[] defaultTagClasses;
        for (int length = (defaultTagClasses = Skin.defaultTagClasses).length, i = 0; i < length; ++i) {
            final Class c = defaultTagClasses[i];
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        final FileHandle atlasFile = skinFile.sibling(String.valueOf(skinFile.nameWithoutExtension()) + ".atlas");
        if (atlasFile.exists()) {
            this.addRegions(this.atlas = new TextureAtlas(atlasFile));
        }
        this.load(skinFile);
    }
    
    public Skin(final FileHandle skinFile, final TextureAtlas atlas) {
        this.resources = new ObjectMap<Class, ObjectMap<String, Object>>();
        this.jsonClassTags = new ObjectMap<String, Class>(Skin.defaultTagClasses.length);
        Class[] defaultTagClasses;
        for (int length = (defaultTagClasses = Skin.defaultTagClasses).length, i = 0; i < length; ++i) {
            final Class c = defaultTagClasses[i];
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        this.addRegions(this.atlas = atlas);
        this.load(skinFile);
    }
    
    public Skin(final TextureAtlas atlas) {
        this.resources = new ObjectMap<Class, ObjectMap<String, Object>>();
        this.jsonClassTags = new ObjectMap<String, Class>(Skin.defaultTagClasses.length);
        Class[] defaultTagClasses;
        for (int length = (defaultTagClasses = Skin.defaultTagClasses).length, i = 0; i < length; ++i) {
            final Class c = defaultTagClasses[i];
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        this.addRegions(this.atlas = atlas);
    }
    
    public void load(final FileHandle skinFile) {
        try {
            this.getJsonLoader(skinFile).fromJson(Skin.class, skinFile);
        }
        catch (SerializationException ex) {
            throw new SerializationException("Error reading file: " + skinFile, ex);
        }
    }
    
    public void addRegions(final TextureAtlas atlas) {
        final Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
        for (int i = 0, n = regions.size; i < n; ++i) {
            final TextureAtlas.AtlasRegion region = regions.get(i);
            String name = region.name;
            if (region.index != -1) {
                name = String.valueOf(name) + "_" + region.index;
            }
            this.add(name, region, TextureRegion.class);
        }
    }
    
    public void add(final String name, final Object resource) {
        this.add(name, resource, resource.getClass());
    }
    
    public void add(final String name, final Object resource, final Class type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null.");
        }
        ObjectMap<String, Object> typeResources = this.resources.get(type);
        if (typeResources == null) {
            typeResources = new ObjectMap<String, Object>((type == TextureRegion.class || type == Drawable.class || type == Sprite.class) ? 256 : 64);
            this.resources.put(type, typeResources);
        }
        typeResources.put(name, resource);
    }
    
    public void remove(final String name, final Class type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        final ObjectMap<String, Object> typeResources = this.resources.get(type);
        typeResources.remove(name);
    }
    
    public <T> T get(final Class<T> type) {
        return this.get("default", type);
    }
    
    public <T> T get(final String name, final Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        if (type == Drawable.class) {
            return (T)this.getDrawable(name);
        }
        if (type == TextureRegion.class) {
            return (T)this.getRegion(name);
        }
        if (type == NinePatch.class) {
            return (T)this.getPatch(name);
        }
        if (type == Sprite.class) {
            return (T)this.getSprite(name);
        }
        final ObjectMap<String, Object> typeResources = this.resources.get(type);
        if (typeResources == null) {
            throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
        }
        final Object resource = typeResources.get(name);
        if (resource == null) {
            throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
        }
        return (T)resource;
    }
    
    public <T> T optional(final String name, final Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        final ObjectMap<String, Object> typeResources = this.resources.get(type);
        if (typeResources == null) {
            return null;
        }
        return (T)typeResources.get(name);
    }
    
    public boolean has(final String name, final Class type) {
        final ObjectMap<String, Object> typeResources = this.resources.get(type);
        return typeResources != null && typeResources.containsKey(name);
    }
    
    public <T> ObjectMap<String, T> getAll(final Class<T> type) {
        return (ObjectMap<String, T>)this.resources.get(type);
    }
    
    public Color getColor(final String name) {
        return this.get(name, Color.class);
    }
    
    public BitmapFont getFont(final String name) {
        return this.get(name, BitmapFont.class);
    }
    
    public TextureRegion getRegion(final String name) {
        TextureRegion region = this.optional(name, TextureRegion.class);
        if (region != null) {
            return region;
        }
        final Texture texture = this.optional(name, Texture.class);
        if (texture == null) {
            throw new GdxRuntimeException("No TextureRegion or Texture registered with name: " + name);
        }
        region = new TextureRegion(texture);
        this.add(name, region, TextureRegion.class);
        return region;
    }
    
    public Array<TextureRegion> getRegions(final String regionName) {
        Array<TextureRegion> regions = null;
        int i = 0;
        TextureRegion region = this.optional(String.valueOf(regionName) + "_" + i++, TextureRegion.class);
        if (region != null) {
            regions = new Array<TextureRegion>();
            while (region != null) {
                regions.add(region);
                region = this.optional(String.valueOf(regionName) + "_" + i++, TextureRegion.class);
            }
        }
        return regions;
    }
    
    public TiledDrawable getTiledDrawable(final String name) {
        TiledDrawable tiled = this.optional(name, TiledDrawable.class);
        if (tiled != null) {
            return tiled;
        }
        tiled = new TiledDrawable(this.getRegion(name));
        tiled.setName(name);
        this.add(name, tiled, TiledDrawable.class);
        return tiled;
    }
    
    public NinePatch getPatch(final String name) {
        NinePatch patch = this.optional(name, NinePatch.class);
        if (patch != null) {
            return patch;
        }
        try {
            final TextureRegion region = this.getRegion(name);
            if (region instanceof TextureAtlas.AtlasRegion) {
                final int[] splits = ((TextureAtlas.AtlasRegion)region).splits;
                if (splits != null) {
                    patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                    final int[] pads = ((TextureAtlas.AtlasRegion)region).pads;
                    if (pads != null) {
                        patch.setPadding((float)pads[0], (float)pads[1], (float)pads[2], (float)pads[3]);
                    }
                }
            }
            if (patch == null) {
                patch = new NinePatch(region);
            }
            this.add(name, patch, NinePatch.class);
            return patch;
        }
        catch (GdxRuntimeException ex) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
        }
    }
    
    public Sprite getSprite(final String name) {
        Sprite sprite = this.optional(name, Sprite.class);
        if (sprite != null) {
            return sprite;
        }
        try {
            final TextureRegion textureRegion = this.getRegion(name);
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                final TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)textureRegion;
                if (region.rotate || region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
                    sprite = new TextureAtlas.AtlasSprite(region);
                }
            }
            if (sprite == null) {
                sprite = new Sprite(textureRegion);
            }
            this.add(name, sprite, Sprite.class);
            return sprite;
        }
        catch (GdxRuntimeException ex) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
        }
    }
    
    public Drawable getDrawable(final String name) {
        Drawable drawable = this.optional(name, Drawable.class);
        if (drawable != null) {
            return drawable;
        }
        try {
            final TextureRegion textureRegion = this.getRegion(name);
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                final TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion)textureRegion;
                if (region.splits != null) {
                    drawable = new NinePatchDrawable(this.getPatch(name));
                }
                else if (region.rotate || region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
                    drawable = new SpriteDrawable(this.getSprite(name));
                }
            }
            if (drawable == null) {
                drawable = new TextureRegionDrawable(textureRegion);
            }
        }
        catch (GdxRuntimeException ex) {}
        if (drawable == null) {
            final NinePatch patch = this.optional(name, NinePatch.class);
            if (patch != null) {
                drawable = new NinePatchDrawable(patch);
            }
            else {
                final Sprite sprite = this.optional(name, Sprite.class);
                if (sprite == null) {
                    throw new GdxRuntimeException("No Drawable, NinePatch, TextureRegion, Texture, or Sprite registered with name: " + name);
                }
                drawable = new SpriteDrawable(sprite);
            }
        }
        if (drawable instanceof BaseDrawable) {
            ((BaseDrawable)drawable).setName(name);
        }
        this.add(name, drawable, Drawable.class);
        return drawable;
    }
    
    public String find(final Object resource) {
        if (resource == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        final ObjectMap<String, Object> typeResources = this.resources.get(resource.getClass());
        if (typeResources == null) {
            return null;
        }
        return typeResources.findKey(resource, true);
    }
    
    public Drawable newDrawable(final String name) {
        return this.newDrawable(this.getDrawable(name));
    }
    
    public Drawable newDrawable(final String name, final float r, final float g, final float b, final float a) {
        return this.newDrawable(this.getDrawable(name), new Color(r, g, b, a));
    }
    
    public Drawable newDrawable(final String name, final Color tint) {
        return this.newDrawable(this.getDrawable(name), tint);
    }
    
    public Drawable newDrawable(final Drawable drawable) {
        if (drawable instanceof TiledDrawable) {
            return new TiledDrawable((TextureRegionDrawable)drawable);
        }
        if (drawable instanceof TextureRegionDrawable) {
            return new TextureRegionDrawable((TextureRegionDrawable)drawable);
        }
        if (drawable instanceof NinePatchDrawable) {
            return new NinePatchDrawable((NinePatchDrawable)drawable);
        }
        if (drawable instanceof SpriteDrawable) {
            return new SpriteDrawable((SpriteDrawable)drawable);
        }
        throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
    }
    
    public Drawable newDrawable(final Drawable drawable, final float r, final float g, final float b, final float a) {
        return this.newDrawable(drawable, new Color(r, g, b, a));
    }
    
    public Drawable newDrawable(final Drawable drawable, final Color tint) {
        Drawable newDrawable;
        if (drawable instanceof TextureRegionDrawable) {
            newDrawable = ((TextureRegionDrawable)drawable).tint(tint);
        }
        else if (drawable instanceof NinePatchDrawable) {
            newDrawable = ((NinePatchDrawable)drawable).tint(tint);
        }
        else {
            if (!(drawable instanceof SpriteDrawable)) {
                throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
            }
            newDrawable = ((SpriteDrawable)drawable).tint(tint);
        }
        if (newDrawable instanceof BaseDrawable) {
            final BaseDrawable named = (BaseDrawable)newDrawable;
            if (drawable instanceof BaseDrawable) {
                named.setName(String.valueOf(((BaseDrawable)drawable).getName()) + " (" + tint + ")");
            }
            else {
                named.setName(" (" + tint + ")");
            }
        }
        return newDrawable;
    }
    
    public void setEnabled(final Actor actor, final boolean enabled) {
        Method method = findMethod(actor.getClass(), "getStyle");
        if (method == null) {
            return;
        }
        Object style;
        try {
            style = method.invoke(actor, new Object[0]);
        }
        catch (Exception ignored) {
            return;
        }
        String name = this.find(style);
        if (name == null) {
            return;
        }
        name = String.valueOf(name.replace("-disabled", "")) + (enabled ? "" : "-disabled");
        style = this.get(name, style.getClass());
        method = findMethod(actor.getClass(), "setStyle");
        if (method == null) {
            return;
        }
        try {
            method.invoke(actor, style);
        }
        catch (Exception ex) {}
    }
    
    public TextureAtlas getAtlas() {
        return this.atlas;
    }
    
    @Override
    public void dispose() {
        if (this.atlas != null) {
            this.atlas.dispose();
        }
        for (final ObjectMap<String, Object> entry : this.resources.values()) {
            for (final Object resource : entry.values()) {
                if (resource instanceof Disposable) {
                    ((Disposable)resource).dispose();
                }
            }
        }
    }
    
    protected Json getJsonLoader(final FileHandle skinFile) {
        final Skin skin = this;
        final Json json = new Json() {
            private static final String parentFieldName = "parent";
            
            @Override
            public <T> T readValue(final Class<T> type, final Class elementType, final JsonValue jsonData) {
                if (jsonData.isString() && !ClassReflection.isAssignableFrom(CharSequence.class, type)) {
                    return Skin.this.get(jsonData.asString(), type);
                }
                return super.readValue(type, elementType, jsonData);
            }
            
            @Override
            protected boolean ignoreUnknownField(final Class type, final String fieldName) {
                return fieldName.equals("parent");
            }
            
            @Override
            public void readFields(final Object object, final JsonValue jsonMap) {
                if (jsonMap.has("parent")) {
                    final String parentName = this.readValue("parent", String.class, jsonMap);
                    Class parentType = object.getClass();
                    while (true) {
                        try {
                            this.copyFields(Skin.this.get(parentName, (Class<Object>)parentType), object);
                        }
                        catch (GdxRuntimeException ex) {
                            parentType = parentType.getSuperclass();
                            if (parentType == Object.class) {
                                final SerializationException se = new SerializationException("Unable to find parent resource with name: " + parentName);
                                se.addTrace(jsonMap.child.trace());
                                throw se;
                            }
                            continue;
                        }
                        break;
                    }
                }
                super.readFields(object, jsonMap);
            }
        };
        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setSerializer(Skin.class, new Json.ReadOnlySerializer<Skin>() {
            @Override
            public Skin read(final Json json, final JsonValue typeToValueMap, final Class ignored) {
                for (JsonValue valueMap = typeToValueMap.child; valueMap != null; valueMap = valueMap.next) {
                    try {
                        Class type = json.getClass(valueMap.name());
                        if (type == null) {
                            type = ClassReflection.forName(valueMap.name());
                        }
                        this.readNamedObjects(json, type, valueMap);
                    }
                    catch (ReflectionException ex) {
                        throw new SerializationException(ex);
                    }
                }
                return skin;
            }
            
            private void readNamedObjects(final Json json, final Class type, final JsonValue valueMap) {
                final Class addType = (type == TintedDrawable.class) ? Drawable.class : type;
                for (JsonValue valueEntry = valueMap.child; valueEntry != null; valueEntry = valueEntry.next) {
                    final Object object = json.readValue((Class<Object>)type, valueEntry);
                    if (object != null) {
                        try {
                            Skin.this.add(valueEntry.name, object, addType);
                            if (addType != Drawable.class && ClassReflection.isAssignableFrom(Drawable.class, addType)) {
                                Skin.this.add(valueEntry.name, object, Drawable.class);
                            }
                        }
                        catch (Exception ex) {
                            throw new SerializationException("Error reading " + ClassReflection.getSimpleName(type) + ": " + valueEntry.name, ex);
                        }
                    }
                }
            }
        });
        json.setSerializer(BitmapFont.class, new Json.ReadOnlySerializer<BitmapFont>() {
            @Override
            public BitmapFont read(final Json json, final JsonValue jsonData, final Class type) {
                final String path = json.readValue("file", String.class, jsonData);
                final int scaledSize = json.readValue("scaledSize", Integer.TYPE, -1, jsonData);
                final Boolean flip = json.readValue("flip", Boolean.class, false, jsonData);
                final Boolean markupEnabled = json.readValue("markupEnabled", Boolean.class, false, jsonData);
                FileHandle fontFile = skinFile.parent().child(path);
                if (!fontFile.exists()) {
                    fontFile = Gdx.files.internal(path);
                }
                if (!fontFile.exists()) {
                    throw new SerializationException("Font file not found: " + fontFile);
                }
                final String regionName = fontFile.nameWithoutExtension();
                try {
                    final Array<TextureRegion> regions = skin.getRegions(regionName);
                    BitmapFont font;
                    if (regions != null) {
                        font = new BitmapFont(new BitmapFont.BitmapFontData(fontFile, flip), regions, true);
                    }
                    else {
                        final TextureRegion region = skin.optional(regionName, TextureRegion.class);
                        if (region != null) {
                            font = new BitmapFont(fontFile, region, flip);
                        }
                        else {
                            final FileHandle imageFile = fontFile.parent().child(String.valueOf(regionName) + ".png");
                            if (imageFile.exists()) {
                                font = new BitmapFont(fontFile, imageFile, flip);
                            }
                            else {
                                font = new BitmapFont(fontFile, flip);
                            }
                        }
                    }
                    font.getData().markupEnabled = markupEnabled;
                    if (scaledSize != -1) {
                        font.getData().setScale(scaledSize / font.getCapHeight());
                    }
                    return font;
                }
                catch (RuntimeException ex) {
                    throw new SerializationException("Error loading bitmap font: " + fontFile, ex);
                }
            }
        });
        json.setSerializer(Color.class, new Json.ReadOnlySerializer<Color>() {
            @Override
            public Color read(final Json json, final JsonValue jsonData, final Class type) {
                if (jsonData.isString()) {
                    return Skin.this.get(jsonData.asString(), Color.class);
                }
                final String hex = json.readValue("hex", String.class, (String)null, jsonData);
                if (hex != null) {
                    return Color.valueOf(hex);
                }
                final float r = json.readValue("r", Float.TYPE, 0.0f, jsonData);
                final float g = json.readValue("g", Float.TYPE, 0.0f, jsonData);
                final float b = json.readValue("b", Float.TYPE, 0.0f, jsonData);
                final float a = json.readValue("a", Float.TYPE, 1.0f, jsonData);
                return new Color(r, g, b, a);
            }
        });
        json.setSerializer(TintedDrawable.class, new Json.ReadOnlySerializer() {
            @Override
            public Object read(final Json json, final JsonValue jsonData, final Class type) {
                final String name = json.readValue("name", String.class, jsonData);
                final Color color = json.readValue("color", Color.class, jsonData);
                final Drawable drawable = Skin.this.newDrawable(name, color);
                if (drawable instanceof BaseDrawable) {
                    final BaseDrawable named = (BaseDrawable)drawable;
                    named.setName(String.valueOf(jsonData.name) + " (" + name + ", " + color + ")");
                }
                return drawable;
            }
        });
        for (final ObjectMap.Entry<String, Class> entry : this.jsonClassTags) {
            json.addClassTag(entry.key, entry.value);
        }
        return json;
    }
    
    public ObjectMap<String, Class> getJsonClassTags() {
        return this.jsonClassTags;
    }
    
    private static Method findMethod(final Class type, final String name) {
        final Method[] methods = ClassReflection.getMethods(type);
        for (int i = 0, n = methods.length; i < n; ++i) {
            final Method method = methods[i];
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
    
    public static class TintedDrawable
    {
        public String name;
        public Color color;
    }
}

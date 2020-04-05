// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.ObjectMap;

public final class Colors
{
    private static final ObjectMap<String, Color> map;
    
    static {
        map = new ObjectMap<String, Color>();
        reset();
    }
    
    public static ObjectMap<String, Color> getColors() {
        return Colors.map;
    }
    
    public static Color get(final String name) {
        return Colors.map.get(name);
    }
    
    public static Color put(final String name, final Color color) {
        return Colors.map.put(name, color);
    }
    
    public static void reset() {
        Colors.map.clear();
        Colors.map.put("CLEAR", Color.CLEAR);
        Colors.map.put("BLACK", Color.BLACK);
        Colors.map.put("WHITE", Color.WHITE);
        Colors.map.put("LIGHT_GRAY", Color.LIGHT_GRAY);
        Colors.map.put("GRAY", Color.GRAY);
        Colors.map.put("DARK_GRAY", Color.DARK_GRAY);
        Colors.map.put("BLUE", Color.BLUE);
        Colors.map.put("NAVY", Color.NAVY);
        Colors.map.put("ROYAL", Color.ROYAL);
        Colors.map.put("SLATE", Color.SLATE);
        Colors.map.put("SKY", Color.SKY);
        Colors.map.put("CYAN", Color.CYAN);
        Colors.map.put("TEAL", Color.TEAL);
        Colors.map.put("GREEN", Color.GREEN);
        Colors.map.put("CHARTREUSE", Color.CHARTREUSE);
        Colors.map.put("LIME", Color.LIME);
        Colors.map.put("FOREST", Color.FOREST);
        Colors.map.put("OLIVE", Color.OLIVE);
        Colors.map.put("YELLOW", Color.YELLOW);
        Colors.map.put("GOLD", Color.GOLD);
        Colors.map.put("GOLDENROD", Color.GOLDENROD);
        Colors.map.put("ORANGE", Color.ORANGE);
        Colors.map.put("BROWN", Color.BROWN);
        Colors.map.put("TAN", Color.TAN);
        Colors.map.put("FIREBRICK", Color.FIREBRICK);
        Colors.map.put("RED", Color.RED);
        Colors.map.put("SCARLET", Color.SCARLET);
        Colors.map.put("CORAL", Color.CORAL);
        Colors.map.put("SALMON", Color.SALMON);
        Colors.map.put("PINK", Color.PINK);
        Colors.map.put("MAGENTA", Color.MAGENTA);
        Colors.map.put("PURPLE", Color.PURPLE);
        Colors.map.put("VIOLET", Color.VIOLET);
        Colors.map.put("MAROON", Color.MAROON);
    }
    
    private Colors() {
    }
}

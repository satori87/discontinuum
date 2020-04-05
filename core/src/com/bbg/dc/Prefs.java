// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

public class Prefs
{
    public static final int MAPWIDTH = 64;
    public static final int NUMMAPS = 100;
    public static final short CATEGORY_PLAYER = 1;
    public static final short CATEGORY_MONSTER = 2;
    public static final short CATEGORY_BOUNDARY = 4;
    public static final short CATEGORY_WALL = 8;
    public static final boolean PRELOADSPRITES = false;
    public static final boolean USEWEATHER = true;
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;
    public static final int CAMBOUNDINGRADIUS = 48;
    public static final int PEEKWIDTH = 64;
    public static final int PATHSPERTICK = 6;
    public static final int NODEPATHSPERTICK = 2;
    public static final float volume = 0.01f;
    
    public static String getCardinalDirectionName(final int d) {
        switch (d) {
            case 0: {
                return "North";
            }
            case 2: {
                return "South";
            }
            case 1: {
                return "West";
            }
            case 3: {
                return "East";
            }
            default: {
                return "???";
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.utils.ObjectIntMap;

public interface Input
{
    float getAccelerometerX();
    
    float getAccelerometerY();
    
    float getAccelerometerZ();
    
    float getGyroscopeX();
    
    float getGyroscopeY();
    
    float getGyroscopeZ();
    
    int getX();
    
    int getX(final int p0);
    
    int getDeltaX();
    
    int getDeltaX(final int p0);
    
    int getY();
    
    int getY(final int p0);
    
    int getDeltaY();
    
    int getDeltaY(final int p0);
    
    boolean isTouched();
    
    boolean justTouched();
    
    boolean isTouched(final int p0);
    
    float getPressure();
    
    float getPressure(final int p0);
    
    boolean isButtonPressed(final int p0);
    
    boolean isKeyPressed(final int p0);
    
    boolean isKeyJustPressed(final int p0);
    
    void getTextInput(final TextInputListener p0, final String p1, final String p2, final String p3);
    
    void setOnscreenKeyboardVisible(final boolean p0);
    
    void vibrate(final int p0);
    
    void vibrate(final long[] p0, final int p1);
    
    void cancelVibrate();
    
    float getAzimuth();
    
    float getPitch();
    
    float getRoll();
    
    void getRotationMatrix(final float[] p0);
    
    long getCurrentEventTime();
    
    void setCatchBackKey(final boolean p0);
    
    boolean isCatchBackKey();
    
    void setCatchMenuKey(final boolean p0);
    
    boolean isCatchMenuKey();
    
    void setInputProcessor(final InputProcessor p0);
    
    InputProcessor getInputProcessor();
    
    boolean isPeripheralAvailable(final Peripheral p0);
    
    int getRotation();
    
    Orientation getNativeOrientation();
    
    void setCursorCatched(final boolean p0);
    
    boolean isCursorCatched();
    
    void setCursorPosition(final int p0, final int p1);
    
    public enum Orientation
    {
        Landscape("Landscape", 0), 
        Portrait("Portrait", 1);
        
        private Orientation(final String name, final int ordinal) {
        }
    }
    
    public enum Peripheral
    {
        HardwareKeyboard("HardwareKeyboard", 0), 
        OnscreenKeyboard("OnscreenKeyboard", 1), 
        MultitouchScreen("MultitouchScreen", 2), 
        Accelerometer("Accelerometer", 3), 
        Compass("Compass", 4), 
        Vibrator("Vibrator", 5), 
        Gyroscope("Gyroscope", 6), 
        RotationVector("RotationVector", 7), 
        Pressure("Pressure", 8);
        
        private Peripheral(final String name, final int ordinal) {
        }
    }
    
    public static class Buttons
    {
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int MIDDLE = 2;
        public static final int BACK = 3;
        public static final int FORWARD = 4;
    }
    
    public static class Keys
    {
        public static final int ANY_KEY = -1;
        public static final int NUM_0 = 7;
        public static final int NUM_1 = 8;
        public static final int NUM_2 = 9;
        public static final int NUM_3 = 10;
        public static final int NUM_4 = 11;
        public static final int NUM_5 = 12;
        public static final int NUM_6 = 13;
        public static final int NUM_7 = 14;
        public static final int NUM_8 = 15;
        public static final int NUM_9 = 16;
        public static final int A = 29;
        public static final int ALT_LEFT = 57;
        public static final int ALT_RIGHT = 58;
        public static final int APOSTROPHE = 75;
        public static final int AT = 77;
        public static final int B = 30;
        public static final int BACK = 4;
        public static final int BACKSLASH = 73;
        public static final int C = 31;
        public static final int CALL = 5;
        public static final int CAMERA = 27;
        public static final int CLEAR = 28;
        public static final int COMMA = 55;
        public static final int D = 32;
        public static final int DEL = 67;
        public static final int BACKSPACE = 67;
        public static final int FORWARD_DEL = 112;
        public static final int DPAD_CENTER = 23;
        public static final int DPAD_DOWN = 20;
        public static final int DPAD_LEFT = 21;
        public static final int DPAD_RIGHT = 22;
        public static final int DPAD_UP = 19;
        public static final int CENTER = 23;
        public static final int DOWN = 20;
        public static final int LEFT = 21;
        public static final int RIGHT = 22;
        public static final int UP = 19;
        public static final int E = 33;
        public static final int ENDCALL = 6;
        public static final int ENTER = 66;
        public static final int ENVELOPE = 65;
        public static final int EQUALS = 70;
        public static final int EXPLORER = 64;
        public static final int F = 34;
        public static final int FOCUS = 80;
        public static final int G = 35;
        public static final int GRAVE = 68;
        public static final int H = 36;
        public static final int HEADSETHOOK = 79;
        public static final int HOME = 3;
        public static final int I = 37;
        public static final int J = 38;
        public static final int K = 39;
        public static final int L = 40;
        public static final int LEFT_BRACKET = 71;
        public static final int M = 41;
        public static final int MEDIA_FAST_FORWARD = 90;
        public static final int MEDIA_NEXT = 87;
        public static final int MEDIA_PLAY_PAUSE = 85;
        public static final int MEDIA_PREVIOUS = 88;
        public static final int MEDIA_REWIND = 89;
        public static final int MEDIA_STOP = 86;
        public static final int MENU = 82;
        public static final int MINUS = 69;
        public static final int MUTE = 91;
        public static final int N = 42;
        public static final int NOTIFICATION = 83;
        public static final int NUM = 78;
        public static final int O = 43;
        public static final int P = 44;
        public static final int PERIOD = 56;
        public static final int PLUS = 81;
        public static final int POUND = 18;
        public static final int POWER = 26;
        public static final int Q = 45;
        public static final int R = 46;
        public static final int RIGHT_BRACKET = 72;
        public static final int S = 47;
        public static final int SEARCH = 84;
        public static final int SEMICOLON = 74;
        public static final int SHIFT_LEFT = 59;
        public static final int SHIFT_RIGHT = 60;
        public static final int SLASH = 76;
        public static final int SOFT_LEFT = 1;
        public static final int SOFT_RIGHT = 2;
        public static final int SPACE = 62;
        public static final int STAR = 17;
        public static final int SYM = 63;
        public static final int T = 48;
        public static final int TAB = 61;
        public static final int U = 49;
        public static final int UNKNOWN = 0;
        public static final int V = 50;
        public static final int VOLUME_DOWN = 25;
        public static final int VOLUME_UP = 24;
        public static final int W = 51;
        public static final int X = 52;
        public static final int Y = 53;
        public static final int Z = 54;
        public static final int META_ALT_LEFT_ON = 16;
        public static final int META_ALT_ON = 2;
        public static final int META_ALT_RIGHT_ON = 32;
        public static final int META_SHIFT_LEFT_ON = 64;
        public static final int META_SHIFT_ON = 1;
        public static final int META_SHIFT_RIGHT_ON = 128;
        public static final int META_SYM_ON = 4;
        public static final int CONTROL_LEFT = 129;
        public static final int CONTROL_RIGHT = 130;
        public static final int ESCAPE = 131;
        public static final int END = 132;
        public static final int INSERT = 133;
        public static final int PAGE_UP = 92;
        public static final int PAGE_DOWN = 93;
        public static final int PICTSYMBOLS = 94;
        public static final int SWITCH_CHARSET = 95;
        public static final int BUTTON_CIRCLE = 255;
        public static final int BUTTON_A = 96;
        public static final int BUTTON_B = 97;
        public static final int BUTTON_C = 98;
        public static final int BUTTON_X = 99;
        public static final int BUTTON_Y = 100;
        public static final int BUTTON_Z = 101;
        public static final int BUTTON_L1 = 102;
        public static final int BUTTON_R1 = 103;
        public static final int BUTTON_L2 = 104;
        public static final int BUTTON_R2 = 105;
        public static final int BUTTON_THUMBL = 106;
        public static final int BUTTON_THUMBR = 107;
        public static final int BUTTON_START = 108;
        public static final int BUTTON_SELECT = 109;
        public static final int BUTTON_MODE = 110;
        public static final int NUMPAD_0 = 144;
        public static final int NUMPAD_1 = 145;
        public static final int NUMPAD_2 = 146;
        public static final int NUMPAD_3 = 147;
        public static final int NUMPAD_4 = 148;
        public static final int NUMPAD_5 = 149;
        public static final int NUMPAD_6 = 150;
        public static final int NUMPAD_7 = 151;
        public static final int NUMPAD_8 = 152;
        public static final int NUMPAD_9 = 153;
        public static final int COLON = 243;
        public static final int F1 = 244;
        public static final int F2 = 245;
        public static final int F3 = 246;
        public static final int F4 = 247;
        public static final int F5 = 248;
        public static final int F6 = 249;
        public static final int F7 = 250;
        public static final int F8 = 251;
        public static final int F9 = 252;
        public static final int F10 = 253;
        public static final int F11 = 254;
        public static final int F12 = 255;
        private static ObjectIntMap<String> keyNames;
        
        public static String toString(final int keycode) {
            if (keycode < 0) {
                throw new IllegalArgumentException("keycode cannot be negative, keycode: " + keycode);
            }
            if (keycode > 255) {
                throw new IllegalArgumentException("keycode cannot be greater than 255, keycode: " + keycode);
            }
            switch (keycode) {
                case 0: {
                    return "Unknown";
                }
                case 1: {
                    return "Soft Left";
                }
                case 2: {
                    return "Soft Right";
                }
                case 3: {
                    return "Home";
                }
                case 4: {
                    return "Back";
                }
                case 5: {
                    return "Call";
                }
                case 6: {
                    return "End Call";
                }
                case 7: {
                    return "0";
                }
                case 8: {
                    return "1";
                }
                case 9: {
                    return "2";
                }
                case 10: {
                    return "3";
                }
                case 11: {
                    return "4";
                }
                case 12: {
                    return "5";
                }
                case 13: {
                    return "6";
                }
                case 14: {
                    return "7";
                }
                case 15: {
                    return "8";
                }
                case 16: {
                    return "9";
                }
                case 17: {
                    return "*";
                }
                case 18: {
                    return "#";
                }
                case 19: {
                    return "Up";
                }
                case 20: {
                    return "Down";
                }
                case 21: {
                    return "Left";
                }
                case 22: {
                    return "Right";
                }
                case 23: {
                    return "Center";
                }
                case 24: {
                    return "Volume Up";
                }
                case 25: {
                    return "Volume Down";
                }
                case 26: {
                    return "Power";
                }
                case 27: {
                    return "Camera";
                }
                case 28: {
                    return "Clear";
                }
                case 29: {
                    return "A";
                }
                case 30: {
                    return "B";
                }
                case 31: {
                    return "C";
                }
                case 32: {
                    return "D";
                }
                case 33: {
                    return "E";
                }
                case 34: {
                    return "F";
                }
                case 35: {
                    return "G";
                }
                case 36: {
                    return "H";
                }
                case 37: {
                    return "I";
                }
                case 38: {
                    return "J";
                }
                case 39: {
                    return "K";
                }
                case 40: {
                    return "L";
                }
                case 41: {
                    return "M";
                }
                case 42: {
                    return "N";
                }
                case 43: {
                    return "O";
                }
                case 44: {
                    return "P";
                }
                case 45: {
                    return "Q";
                }
                case 46: {
                    return "R";
                }
                case 47: {
                    return "S";
                }
                case 48: {
                    return "T";
                }
                case 49: {
                    return "U";
                }
                case 50: {
                    return "V";
                }
                case 51: {
                    return "W";
                }
                case 52: {
                    return "X";
                }
                case 53: {
                    return "Y";
                }
                case 54: {
                    return "Z";
                }
                case 55: {
                    return ",";
                }
                case 56: {
                    return ".";
                }
                case 57: {
                    return "L-Alt";
                }
                case 58: {
                    return "R-Alt";
                }
                case 59: {
                    return "L-Shift";
                }
                case 60: {
                    return "R-Shift";
                }
                case 61: {
                    return "Tab";
                }
                case 62: {
                    return "Space";
                }
                case 63: {
                    return "SYM";
                }
                case 64: {
                    return "Explorer";
                }
                case 65: {
                    return "Envelope";
                }
                case 66: {
                    return "Enter";
                }
                case 67: {
                    return "Delete";
                }
                case 68: {
                    return "`";
                }
                case 69: {
                    return "-";
                }
                case 70: {
                    return "=";
                }
                case 71: {
                    return "[";
                }
                case 72: {
                    return "]";
                }
                case 73: {
                    return "\\";
                }
                case 74: {
                    return ";";
                }
                case 75: {
                    return "'";
                }
                case 76: {
                    return "/";
                }
                case 77: {
                    return "@";
                }
                case 78: {
                    return "Num";
                }
                case 79: {
                    return "Headset Hook";
                }
                case 80: {
                    return "Focus";
                }
                case 81: {
                    return "Plus";
                }
                case 82: {
                    return "Menu";
                }
                case 83: {
                    return "Notification";
                }
                case 84: {
                    return "Search";
                }
                case 85: {
                    return "Play/Pause";
                }
                case 86: {
                    return "Stop Media";
                }
                case 87: {
                    return "Next Media";
                }
                case 88: {
                    return "Prev Media";
                }
                case 89: {
                    return "Rewind";
                }
                case 90: {
                    return "Fast Forward";
                }
                case 91: {
                    return "Mute";
                }
                case 92: {
                    return "Page Up";
                }
                case 93: {
                    return "Page Down";
                }
                case 94: {
                    return "PICTSYMBOLS";
                }
                case 95: {
                    return "SWITCH_CHARSET";
                }
                case 96: {
                    return "A Button";
                }
                case 97: {
                    return "B Button";
                }
                case 98: {
                    return "C Button";
                }
                case 99: {
                    return "X Button";
                }
                case 100: {
                    return "Y Button";
                }
                case 101: {
                    return "Z Button";
                }
                case 102: {
                    return "L1 Button";
                }
                case 103: {
                    return "R1 Button";
                }
                case 104: {
                    return "L2 Button";
                }
                case 105: {
                    return "R2 Button";
                }
                case 106: {
                    return "Left Thumb";
                }
                case 107: {
                    return "Right Thumb";
                }
                case 108: {
                    return "Start";
                }
                case 109: {
                    return "Select";
                }
                case 110: {
                    return "Button Mode";
                }
                case 112: {
                    return "Forward Delete";
                }
                case 129: {
                    return "L-Ctrl";
                }
                case 130: {
                    return "R-Ctrl";
                }
                case 131: {
                    return "Escape";
                }
                case 132: {
                    return "End";
                }
                case 133: {
                    return "Insert";
                }
                case 144: {
                    return "Numpad 0";
                }
                case 145: {
                    return "Numpad 1";
                }
                case 146: {
                    return "Numpad 2";
                }
                case 147: {
                    return "Numpad 3";
                }
                case 148: {
                    return "Numpad 4";
                }
                case 149: {
                    return "Numpad 5";
                }
                case 150: {
                    return "Numpad 6";
                }
                case 151: {
                    return "Numpad 7";
                }
                case 152: {
                    return "Numpad 8";
                }
                case 153: {
                    return "Numpad 9";
                }
                case 243: {
                    return ":";
                }
                case 244: {
                    return "F1";
                }
                case 245: {
                    return "F2";
                }
                case 246: {
                    return "F3";
                }
                case 247: {
                    return "F4";
                }
                case 248: {
                    return "F5";
                }
                case 249: {
                    return "F6";
                }
                case 250: {
                    return "F7";
                }
                case 251: {
                    return "F8";
                }
                case 252: {
                    return "F9";
                }
                case 253: {
                    return "F10";
                }
                case 254: {
                    return "F11";
                }
                case 255: {
                    return "F12";
                }
                default: {
                    return null;
                }
            }
        }
        
        public static int valueOf(final String keyname) {
            if (Keys.keyNames == null) {
                initializeKeyNames();
            }
            return Keys.keyNames.get(keyname, -1);
        }
        
        private static void initializeKeyNames() {
            Keys.keyNames = new ObjectIntMap<String>();
            for (int i = 0; i < 256; ++i) {
                final String name = toString(i);
                if (name != null) {
                    Keys.keyNames.put(name, i);
                }
            }
        }
    }
    
    public interface TextInputListener
    {
        void input(final String p0);
        
        void canceled();
    }
}

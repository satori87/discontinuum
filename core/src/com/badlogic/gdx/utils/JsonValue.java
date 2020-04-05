// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.IOException;
import java.io.Writer;

public class JsonValue implements Iterable<JsonValue>
{
    private ValueType type;
    private String stringValue;
    private double doubleValue;
    private long longValue;
    public String name;
    public JsonValue child;
    public JsonValue next;
    public JsonValue prev;
    public JsonValue parent;
    public int size;
    
    public JsonValue(final ValueType type) {
        this.type = type;
    }
    
    public JsonValue(final String value) {
        this.set(value);
    }
    
    public JsonValue(final double value) {
        this.set(value, null);
    }
    
    public JsonValue(final long value) {
        this.set(value, null);
    }
    
    public JsonValue(final double value, final String stringValue) {
        this.set(value, stringValue);
    }
    
    public JsonValue(final long value, final String stringValue) {
        this.set(value, stringValue);
    }
    
    public JsonValue(final boolean value) {
        this.set(value);
    }
    
    public JsonValue get(int index) {
        JsonValue current;
        for (current = this.child; current != null && index > 0; --index, current = current.next) {}
        return current;
    }
    
    public JsonValue get(final String name) {
        JsonValue current;
        for (current = this.child; current != null && (current.name == null || !current.name.equalsIgnoreCase(name)); current = current.next) {}
        return current;
    }
    
    public boolean has(final String name) {
        return this.get(name) != null;
    }
    
    public JsonValue require(int index) {
        JsonValue current;
        for (current = this.child; current != null && index > 0; --index, current = current.next) {}
        if (current == null) {
            throw new IllegalArgumentException("Child not found with index: " + index);
        }
        return current;
    }
    
    public JsonValue require(final String name) {
        JsonValue current;
        for (current = this.child; current != null && (current.name == null || !current.name.equalsIgnoreCase(name)); current = current.next) {}
        if (current == null) {
            throw new IllegalArgumentException("Child not found with name: " + name);
        }
        return current;
    }
    
    public JsonValue remove(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            return null;
        }
        if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) {
                this.child.prev = null;
            }
        }
        else {
            child.prev.next = child.next;
            if (child.next != null) {
                child.next.prev = child.prev;
            }
        }
        --this.size;
        return child;
    }
    
    public JsonValue remove(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            return null;
        }
        if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) {
                this.child.prev = null;
            }
        }
        else {
            child.prev.next = child.next;
            if (child.next != null) {
                child.next.prev = child.prev;
            }
        }
        --this.size;
        return child;
    }
    
    @Deprecated
    public int size() {
        return this.size;
    }
    
    public String asString() {
        switch (this.type) {
            case stringValue: {
                return this.stringValue;
            }
            case doubleValue: {
                return (this.stringValue != null) ? this.stringValue : Double.toString(this.doubleValue);
            }
            case longValue: {
                return (this.stringValue != null) ? this.stringValue : Long.toString(this.longValue);
            }
            case booleanValue: {
                return (this.longValue != 0L) ? "true" : "false";
            }
            case nullValue: {
                return null;
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to string: " + this.type);
            }
        }
    }
    
    public float asFloat() {
        switch (this.type) {
            case stringValue: {
                return Float.parseFloat(this.stringValue);
            }
            case doubleValue: {
                return (float)this.doubleValue;
            }
            case longValue: {
                return (float)this.longValue;
            }
            case booleanValue: {
                return (float)((this.longValue != 0L) ? 1 : 0);
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to float: " + this.type);
            }
        }
    }
    
    public double asDouble() {
        switch (this.type) {
            case stringValue: {
                return Double.parseDouble(this.stringValue);
            }
            case doubleValue: {
                return this.doubleValue;
            }
            case longValue: {
                return (double)this.longValue;
            }
            case booleanValue: {
                return (this.longValue != 0L) ? 1 : 0;
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to double: " + this.type);
            }
        }
    }
    
    public long asLong() {
        switch (this.type) {
            case stringValue: {
                return Long.parseLong(this.stringValue);
            }
            case doubleValue: {
                return (long)this.doubleValue;
            }
            case longValue: {
                return this.longValue;
            }
            case booleanValue: {
                return (this.longValue != 0L) ? 1 : 0;
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to long: " + this.type);
            }
        }
    }
    
    public int asInt() {
        switch (this.type) {
            case stringValue: {
                return Integer.parseInt(this.stringValue);
            }
            case doubleValue: {
                return (int)this.doubleValue;
            }
            case longValue: {
                return (int)this.longValue;
            }
            case booleanValue: {
                return (this.longValue != 0L) ? 1 : 0;
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to int: " + this.type);
            }
        }
    }
    
    public boolean asBoolean() {
        switch (this.type) {
            case stringValue: {
                return this.stringValue.equalsIgnoreCase("true");
            }
            case doubleValue: {
                return this.doubleValue != 0.0;
            }
            case longValue: {
                return this.longValue != 0L;
            }
            case booleanValue: {
                return this.longValue != 0L;
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to boolean: " + this.type);
            }
        }
    }
    
    public byte asByte() {
        switch (this.type) {
            case stringValue: {
                return Byte.parseByte(this.stringValue);
            }
            case doubleValue: {
                return (byte)this.doubleValue;
            }
            case longValue: {
                return (byte)this.longValue;
            }
            case booleanValue: {
                return (byte)((this.longValue != 0L) ? 1 : 0);
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to byte: " + this.type);
            }
        }
    }
    
    public short asShort() {
        switch (this.type) {
            case stringValue: {
                return Short.parseShort(this.stringValue);
            }
            case doubleValue: {
                return (short)this.doubleValue;
            }
            case longValue: {
                return (short)this.longValue;
            }
            case booleanValue: {
                return (short)((this.longValue != 0L) ? 1 : 0);
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to short: " + this.type);
            }
        }
    }
    
    public char asChar() {
        switch (this.type) {
            case stringValue: {
                return (this.stringValue.length() == 0) ? '\0' : this.stringValue.charAt(0);
            }
            case doubleValue: {
                return (char)this.doubleValue;
            }
            case longValue: {
                return (char)this.longValue;
            }
            case booleanValue: {
                return (char)((this.longValue != 0L) ? 1 : 0);
            }
            default: {
                throw new IllegalStateException("Value cannot be converted to char: " + this.type);
            }
        }
    }
    
    public String[] asStringArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final String[] array = new String[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            String v = null;
            switch (value.type) {
                case stringValue: {
                    v = value.stringValue;
                    break;
                }
                case doubleValue: {
                    v = ((this.stringValue != null) ? this.stringValue : Double.toString(value.doubleValue));
                    break;
                }
                case longValue: {
                    v = ((this.stringValue != null) ? this.stringValue : Long.toString(value.longValue));
                    break;
                }
                case booleanValue: {
                    v = ((value.longValue != 0L) ? "true" : "false");
                    break;
                }
                case nullValue: {
                    v = null;
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to string: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public float[] asFloatArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final float[] array = new float[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            float v = 0.0f;
            switch (value.type) {
                case stringValue: {
                    v = Float.parseFloat(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (float)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (float)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = (float)((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to float: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public double[] asDoubleArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final double[] array = new double[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            double v = 0.0;
            switch (value.type) {
                case stringValue: {
                    v = Double.parseDouble(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (double)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = ((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to double: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public long[] asLongArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final long[] array = new long[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            long v = 0L;
            switch (value.type) {
                case stringValue: {
                    v = Long.parseLong(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (long)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = value.longValue;
                    break;
                }
                case booleanValue: {
                    v = ((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to long: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public int[] asIntArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final int[] array = new int[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            int v = 0;
            switch (value.type) {
                case stringValue: {
                    v = Integer.parseInt(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (int)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (int)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = ((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to int: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public boolean[] asBooleanArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final boolean[] array = new boolean[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            boolean v = false;
            switch (value.type) {
                case stringValue: {
                    v = Boolean.parseBoolean(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (value.doubleValue == 0.0);
                    break;
                }
                case longValue: {
                    v = (value.longValue == 0L);
                    break;
                }
                case booleanValue: {
                    v = (value.longValue != 0L);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to boolean: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public byte[] asByteArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final byte[] array = new byte[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            byte v = 0;
            switch (value.type) {
                case stringValue: {
                    v = Byte.parseByte(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (byte)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (byte)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = (byte)((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to byte: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public short[] asShortArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final short[] array = new short[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            short v = 0;
            switch (value.type) {
                case stringValue: {
                    v = Short.parseShort(value.stringValue);
                    break;
                }
                case doubleValue: {
                    v = (short)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (short)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = (short)((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to short: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public char[] asCharArray() {
        if (this.type != ValueType.array) {
            throw new IllegalStateException("Value is not an array: " + this.type);
        }
        final char[] array = new char[this.size];
        int i = 0;
        for (JsonValue value = this.child; value != null; value = value.next, ++i) {
            char v = '\0';
            switch (value.type) {
                case stringValue: {
                    v = ((value.stringValue.length() == 0) ? '\0' : value.stringValue.charAt(0));
                    break;
                }
                case doubleValue: {
                    v = (char)value.doubleValue;
                    break;
                }
                case longValue: {
                    v = (char)value.longValue;
                    break;
                }
                case booleanValue: {
                    v = (char)((value.longValue != 0L) ? 1 : 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Value cannot be converted to char: " + value.type);
                }
            }
            array[i] = v;
        }
        return array;
    }
    
    public boolean hasChild(final String name) {
        return this.getChild(name) != null;
    }
    
    public JsonValue getChild(final String name) {
        final JsonValue child = this.get(name);
        return (child == null) ? null : child.child;
    }
    
    public String getString(final String name, final String defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asString();
    }
    
    public float getFloat(final String name, final float defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asFloat();
    }
    
    public double getDouble(final String name, final double defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asDouble();
    }
    
    public long getLong(final String name, final long defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asLong();
    }
    
    public int getInt(final String name, final int defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asInt();
    }
    
    public boolean getBoolean(final String name, final boolean defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asBoolean();
    }
    
    public byte getByte(final String name, final byte defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asByte();
    }
    
    public short getShort(final String name, final short defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asShort();
    }
    
    public char getChar(final String name, final char defaultValue) {
        final JsonValue child = this.get(name);
        return (child == null || !child.isValue()) ? defaultValue : child.asChar();
    }
    
    public String getString(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asString();
    }
    
    public float getFloat(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asFloat();
    }
    
    public double getDouble(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asDouble();
    }
    
    public long getLong(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asLong();
    }
    
    public int getInt(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asInt();
    }
    
    public boolean getBoolean(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asBoolean();
    }
    
    public byte getByte(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asByte();
    }
    
    public short getShort(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asShort();
    }
    
    public char getChar(final String name) {
        final JsonValue child = this.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Named value not found: " + name);
        }
        return child.asChar();
    }
    
    public String getString(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asString();
    }
    
    public float getFloat(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asFloat();
    }
    
    public double getDouble(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asDouble();
    }
    
    public long getLong(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asLong();
    }
    
    public int getInt(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asInt();
    }
    
    public boolean getBoolean(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asBoolean();
    }
    
    public byte getByte(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asByte();
    }
    
    public short getShort(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asShort();
    }
    
    public char getChar(final int index) {
        final JsonValue child = this.get(index);
        if (child == null) {
            throw new IllegalArgumentException("Indexed value not found: " + this.name);
        }
        return child.asChar();
    }
    
    public ValueType type() {
        return this.type;
    }
    
    public void setType(final ValueType type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        this.type = type;
    }
    
    public boolean isArray() {
        return this.type == ValueType.array;
    }
    
    public boolean isObject() {
        return this.type == ValueType.object;
    }
    
    public boolean isString() {
        return this.type == ValueType.stringValue;
    }
    
    public boolean isNumber() {
        return this.type == ValueType.doubleValue || this.type == ValueType.longValue;
    }
    
    public boolean isDouble() {
        return this.type == ValueType.doubleValue;
    }
    
    public boolean isLong() {
        return this.type == ValueType.longValue;
    }
    
    public boolean isBoolean() {
        return this.type == ValueType.booleanValue;
    }
    
    public boolean isNull() {
        return this.type == ValueType.nullValue;
    }
    
    public boolean isValue() {
        switch (this.type) {
            case stringValue:
            case doubleValue:
            case longValue:
            case booleanValue:
            case nullValue: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String name() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public JsonValue parent() {
        return this.parent;
    }
    
    public JsonValue child() {
        return this.child;
    }
    
    public void addChild(final String name, final JsonValue value) {
        value.name = name;
        this.addChild(value);
    }
    
    public void addChild(final JsonValue value) {
        value.parent = this;
        JsonValue current = this.child;
        if (current == null) {
            this.child = value;
            return;
        }
        while (current.next != null) {
            current = current.next;
        }
        current.next = value;
    }
    
    public JsonValue next() {
        return this.next;
    }
    
    public void setNext(final JsonValue next) {
        this.next = next;
    }
    
    public JsonValue prev() {
        return this.prev;
    }
    
    public void setPrev(final JsonValue prev) {
        this.prev = prev;
    }
    
    public void set(final String value) {
        this.stringValue = value;
        this.type = ((value == null) ? ValueType.nullValue : ValueType.stringValue);
    }
    
    public void set(final double value, final String stringValue) {
        this.doubleValue = value;
        this.longValue = (long)value;
        this.stringValue = stringValue;
        this.type = ValueType.doubleValue;
    }
    
    public void set(final long value, final String stringValue) {
        this.longValue = value;
        this.doubleValue = (double)value;
        this.stringValue = stringValue;
        this.type = ValueType.longValue;
    }
    
    public void set(final boolean value) {
        this.longValue = (value ? 1 : 0);
        this.type = ValueType.booleanValue;
    }
    
    public String toJson(final JsonWriter.OutputType outputType) {
        if (this.isValue()) {
            return this.asString();
        }
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(512);
        this.json(this, buffer, outputType);
        return buffer.toString();
    }
    
    private void json(final JsonValue object, final com.badlogic.gdx.utils.StringBuilder buffer, final JsonWriter.OutputType outputType) {
        if (object.isObject()) {
            if (object.child == null) {
                buffer.append("{}");
            }
            else {
                final int start = buffer.length();
                buffer.append('{');
                final int i = 0;
                for (JsonValue child = object.child; child != null; child = child.next) {
                    buffer.append(outputType.quoteName(child.name));
                    buffer.append(':');
                    this.json(child, buffer, outputType);
                    if (child.next != null) {
                        buffer.append(',');
                    }
                }
                buffer.append('}');
            }
        }
        else if (object.isArray()) {
            if (object.child == null) {
                buffer.append("[]");
            }
            else {
                final int start = buffer.length();
                buffer.append('[');
                for (JsonValue child2 = object.child; child2 != null; child2 = child2.next) {
                    this.json(child2, buffer, outputType);
                    if (child2.next != null) {
                        buffer.append(',');
                    }
                }
                buffer.append(']');
            }
        }
        else if (object.isString()) {
            buffer.append(outputType.quoteValue(object.asString()));
        }
        else if (object.isDouble()) {
            final double doubleValue = object.asDouble();
            final long longValue = object.asLong();
            buffer.append((doubleValue == longValue) ? ((double)longValue) : doubleValue);
        }
        else if (object.isLong()) {
            buffer.append(object.asLong());
        }
        else if (object.isBoolean()) {
            buffer.append(object.asBoolean());
        }
        else {
            if (!object.isNull()) {
                throw new SerializationException("Unknown object type: " + object);
            }
            buffer.append("null");
        }
    }
    
    @Override
    public String toString() {
        if (this.isValue()) {
            return (this.name == null) ? this.asString() : (String.valueOf(this.name) + ": " + this.asString());
        }
        return String.valueOf((this.name == null) ? "" : new StringBuilder(String.valueOf(this.name)).append(": ").toString()) + this.prettyPrint(JsonWriter.OutputType.minimal, 0);
    }
    
    public String prettyPrint(final JsonWriter.OutputType outputType, final int singleLineColumns) {
        final PrettyPrintSettings settings = new PrettyPrintSettings();
        settings.outputType = outputType;
        settings.singleLineColumns = singleLineColumns;
        return this.prettyPrint(settings);
    }
    
    public String prettyPrint(final PrettyPrintSettings settings) {
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(512);
        this.prettyPrint(this, buffer, 0, settings);
        return buffer.toString();
    }
    
    private void prettyPrint(final JsonValue object, final com.badlogic.gdx.utils.StringBuilder buffer, final int indent, final PrettyPrintSettings settings) {
        final JsonWriter.OutputType outputType = settings.outputType;
        if (object.isObject()) {
            if (object.child == null) {
                buffer.append("{}");
            }
            else {
                boolean newLines = !isFlat(object);
                final int start = buffer.length();
                Label_0052: {
                    break Label_0052;
                    JsonValue child = null;
                    do {
                        if (newLines) {
                            indent(indent, buffer);
                        }
                        buffer.append(outputType.quoteName(child.name));
                        buffer.append(": ");
                        this.prettyPrint(child, buffer, indent + 1, settings);
                        if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child.next != null) {
                            buffer.append(',');
                        }
                        buffer.append(newLines ? '\n' : ' ');
                        if (!newLines && buffer.length() - start > settings.singleLineColumns) {
                            buffer.setLength(start);
                            newLines = true;
                            buffer.append(newLines ? "{\n" : "{ ");
                            final int i = 0;
                            child = object.child;
                        }
                        else {
                            child = child.next;
                        }
                    } while (child != null);
                }
                if (newLines) {
                    indent(indent - 1, buffer);
                }
                buffer.append('}');
            }
        }
        else if (object.isArray()) {
            if (object.child == null) {
                buffer.append("[]");
            }
            else {
                boolean newLines = !isFlat(object);
                final boolean wrap = settings.wrapNumericArrays || !isNumeric(object);
                final int start2 = buffer.length();
                Label_0306: {
                    break Label_0306;
                    JsonValue child = null;
                    do {
                        if (newLines) {
                            indent(indent, buffer);
                        }
                        this.prettyPrint(child, buffer, indent + 1, settings);
                        if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child.next != null) {
                            buffer.append(',');
                        }
                        buffer.append(newLines ? '\n' : ' ');
                        if (wrap && !newLines && buffer.length() - start2 > settings.singleLineColumns) {
                            buffer.setLength(start2);
                            newLines = true;
                            buffer.append(newLines ? "[\n" : "[ ");
                            child = object.child;
                        }
                        else {
                            child = child.next;
                        }
                    } while (child != null);
                }
                if (newLines) {
                    indent(indent - 1, buffer);
                }
                buffer.append(']');
            }
        }
        else if (object.isString()) {
            buffer.append(outputType.quoteValue(object.asString()));
        }
        else if (object.isDouble()) {
            final double doubleValue = object.asDouble();
            final long longValue = object.asLong();
            buffer.append((doubleValue == longValue) ? ((double)longValue) : doubleValue);
        }
        else if (object.isLong()) {
            buffer.append(object.asLong());
        }
        else if (object.isBoolean()) {
            buffer.append(object.asBoolean());
        }
        else {
            if (!object.isNull()) {
                throw new SerializationException("Unknown object type: " + object);
            }
            buffer.append("null");
        }
    }
    
    public void prettyPrint(final JsonWriter.OutputType outputType, final Writer writer) throws IOException {
        final PrettyPrintSettings settings = new PrettyPrintSettings();
        settings.outputType = outputType;
        this.prettyPrint(this, writer, 0, settings);
    }
    
    private void prettyPrint(final JsonValue object, final Writer writer, final int indent, final PrettyPrintSettings settings) throws IOException {
        final JsonWriter.OutputType outputType = settings.outputType;
        if (object.isObject()) {
            if (object.child == null) {
                writer.append((CharSequence)"{}");
            }
            else {
                final boolean newLines = !isFlat(object) || object.size > 6;
                writer.append((CharSequence)(newLines ? "{\n" : "{ "));
                final int i = 0;
                for (JsonValue child = object.child; child != null; child = child.next) {
                    if (newLines) {
                        indent(indent, writer);
                    }
                    writer.append((CharSequence)outputType.quoteName(child.name));
                    writer.append((CharSequence)": ");
                    this.prettyPrint(child, writer, indent + 1, settings);
                    if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child.next != null) {
                        writer.append(',');
                    }
                    writer.append(newLines ? '\n' : ' ');
                }
                if (newLines) {
                    indent(indent - 1, writer);
                }
                writer.append('}');
            }
        }
        else if (object.isArray()) {
            if (object.child == null) {
                writer.append((CharSequence)"[]");
            }
            else {
                final boolean newLines = !isFlat(object);
                writer.append((CharSequence)(newLines ? "[\n" : "[ "));
                final int i = 0;
                for (JsonValue child = object.child; child != null; child = child.next) {
                    if (newLines) {
                        indent(indent, writer);
                    }
                    this.prettyPrint(child, writer, indent + 1, settings);
                    if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child.next != null) {
                        writer.append(',');
                    }
                    writer.append(newLines ? '\n' : ' ');
                }
                if (newLines) {
                    indent(indent - 1, writer);
                }
                writer.append(']');
            }
        }
        else if (object.isString()) {
            writer.append((CharSequence)outputType.quoteValue(object.asString()));
        }
        else if (object.isDouble()) {
            final double doubleValue = object.asDouble();
            final long longValue = object.asLong();
            writer.append((CharSequence)Double.toString((doubleValue == longValue) ? ((double)longValue) : doubleValue));
        }
        else if (object.isLong()) {
            writer.append((CharSequence)Long.toString(object.asLong()));
        }
        else if (object.isBoolean()) {
            writer.append((CharSequence)Boolean.toString(object.asBoolean()));
        }
        else {
            if (!object.isNull()) {
                throw new SerializationException("Unknown object type: " + object);
            }
            writer.append((CharSequence)"null");
        }
    }
    
    private static boolean isFlat(final JsonValue object) {
        for (JsonValue child = object.child; child != null; child = child.next) {
            if (child.isObject() || child.isArray()) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isNumeric(final JsonValue object) {
        for (JsonValue child = object.child; child != null; child = child.next) {
            if (!child.isNumber()) {
                return false;
            }
        }
        return true;
    }
    
    private static void indent(final int count, final com.badlogic.gdx.utils.StringBuilder buffer) {
        for (int i = 0; i < count; ++i) {
            buffer.append('\t');
        }
    }
    
    private static void indent(final int count, final Writer buffer) throws IOException {
        for (int i = 0; i < count; ++i) {
            buffer.append('\t');
        }
    }
    
    @Override
    public JsonIterator iterator() {
        return new JsonIterator();
    }
    
    public String trace() {
        if (this.parent != null) {
            String trace;
            if (this.parent.type == ValueType.array) {
                trace = "[]";
                int i = 0;
                for (JsonValue child = this.parent.child; child != null; child = child.next, ++i) {
                    if (child == this) {
                        trace = "[" + i + "]";
                        break;
                    }
                }
            }
            else if (this.name.indexOf(46) != -1) {
                trace = ".\"" + this.name.replace("\"", "\\\"") + "\"";
            }
            else {
                trace = String.valueOf('.') + this.name;
            }
            return String.valueOf(this.parent.trace()) + trace;
        }
        if (this.type == ValueType.array) {
            return "[]";
        }
        if (this.type == ValueType.object) {
            return "{}";
        }
        return "";
    }
    
    public class JsonIterator implements Iterator<JsonValue>, Iterable<JsonValue>
    {
        JsonValue entry;
        JsonValue current;
        
        public JsonIterator() {
            this.entry = JsonValue.this.child;
        }
        
        @Override
        public boolean hasNext() {
            return this.entry != null;
        }
        
        @Override
        public JsonValue next() {
            this.current = this.entry;
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            this.entry = this.current.next;
            return this.current;
        }
        
        @Override
        public void remove() {
            if (this.current.prev == null) {
                JsonValue.this.child = this.current.next;
                if (JsonValue.this.child != null) {
                    JsonValue.this.child.prev = null;
                }
            }
            else {
                this.current.prev.next = this.current.next;
                if (this.current.next != null) {
                    this.current.next.prev = this.current.prev;
                }
            }
            final JsonValue this$0 = JsonValue.this;
            --this$0.size;
        }
        
        @Override
        public Iterator<JsonValue> iterator() {
            return this;
        }
    }
    
    public enum ValueType
    {
        object("object", 0), 
        array("array", 1), 
        stringValue("stringValue", 2), 
        doubleValue("doubleValue", 3), 
        longValue("longValue", 4), 
        booleanValue("booleanValue", 5), 
        nullValue("nullValue", 6);
        
        private ValueType(final String name, final int ordinal) {
        }
    }
    
    public static class PrettyPrintSettings
    {
        public JsonWriter.OutputType outputType;
        public int singleLineColumns;
        public boolean wrapNumericArrays;
    }
}

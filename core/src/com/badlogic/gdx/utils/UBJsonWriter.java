// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.Closeable;

public class UBJsonWriter implements Closeable
{
    final DataOutputStream out;
    private JsonObject current;
    private boolean named;
    private final Array<JsonObject> stack;
    
    public UBJsonWriter(OutputStream out) {
        this.stack = new Array<JsonObject>();
        if (!(out instanceof DataOutputStream)) {
            out = new DataOutputStream(out);
        }
        this.out = (DataOutputStream)out;
    }
    
    public UBJsonWriter object() throws IOException {
        if (this.current != null && !this.current.array) {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        }
        this.stack.add(this.current = new JsonObject(false));
        return this;
    }
    
    public UBJsonWriter object(final String name) throws IOException {
        this.name(name).object();
        return this;
    }
    
    public UBJsonWriter array() throws IOException {
        if (this.current != null && !this.current.array) {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        }
        this.stack.add(this.current = new JsonObject(true));
        return this;
    }
    
    public UBJsonWriter array(final String name) throws IOException {
        this.name(name).array();
        return this;
    }
    
    public UBJsonWriter name(final String name) throws IOException {
        if (this.current == null || this.current.array) {
            throw new IllegalStateException("Current item must be an object.");
        }
        final byte[] bytes = name.getBytes("UTF-8");
        if (bytes.length <= 127) {
            this.out.writeByte(105);
            this.out.writeByte(bytes.length);
        }
        else if (bytes.length <= 32767) {
            this.out.writeByte(73);
            this.out.writeShort(bytes.length);
        }
        else {
            this.out.writeByte(108);
            this.out.writeInt(bytes.length);
        }
        this.out.write(bytes);
        this.named = true;
        return this;
    }
    
    public UBJsonWriter value(final byte value) throws IOException {
        this.checkName();
        this.out.writeByte(105);
        this.out.writeByte(value);
        return this;
    }
    
    public UBJsonWriter value(final short value) throws IOException {
        this.checkName();
        this.out.writeByte(73);
        this.out.writeShort(value);
        return this;
    }
    
    public UBJsonWriter value(final int value) throws IOException {
        this.checkName();
        this.out.writeByte(108);
        this.out.writeInt(value);
        return this;
    }
    
    public UBJsonWriter value(final long value) throws IOException {
        this.checkName();
        this.out.writeByte(76);
        this.out.writeLong(value);
        return this;
    }
    
    public UBJsonWriter value(final float value) throws IOException {
        this.checkName();
        this.out.writeByte(100);
        this.out.writeFloat(value);
        return this;
    }
    
    public UBJsonWriter value(final double value) throws IOException {
        this.checkName();
        this.out.writeByte(68);
        this.out.writeDouble(value);
        return this;
    }
    
    public UBJsonWriter value(final boolean value) throws IOException {
        this.checkName();
        this.out.writeByte(value ? 84 : 70);
        return this;
    }
    
    public UBJsonWriter value(final char value) throws IOException {
        this.checkName();
        this.out.writeByte(73);
        this.out.writeChar(value);
        return this;
    }
    
    public UBJsonWriter value(final String value) throws IOException {
        this.checkName();
        final byte[] bytes = value.getBytes("UTF-8");
        this.out.writeByte(83);
        if (bytes.length <= 127) {
            this.out.writeByte(105);
            this.out.writeByte(bytes.length);
        }
        else if (bytes.length <= 32767) {
            this.out.writeByte(73);
            this.out.writeShort(bytes.length);
        }
        else {
            this.out.writeByte(108);
            this.out.writeInt(bytes.length);
        }
        this.out.write(bytes);
        return this;
    }
    
    public UBJsonWriter value(final byte[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(105);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeByte(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final short[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(73);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeShort(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final int[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(108);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeInt(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final long[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(76);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeLong(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final float[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(100);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeFloat(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final double[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(68);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeDouble(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final boolean[] values) throws IOException {
        this.array();
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeByte(values[i] ? 84 : 70);
        }
        this.pop();
        return this;
    }
    
    public UBJsonWriter value(final char[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(67);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            this.out.writeChar(values[i]);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final String[] values) throws IOException {
        this.array();
        this.out.writeByte(36);
        this.out.writeByte(83);
        this.out.writeByte(35);
        this.value(values.length);
        for (int i = 0, n = values.length; i < n; ++i) {
            final byte[] bytes = values[i].getBytes("UTF-8");
            if (bytes.length <= 127) {
                this.out.writeByte(105);
                this.out.writeByte(bytes.length);
            }
            else if (bytes.length <= 32767) {
                this.out.writeByte(73);
                this.out.writeShort(bytes.length);
            }
            else {
                this.out.writeByte(108);
                this.out.writeInt(bytes.length);
            }
            this.out.write(bytes);
        }
        this.pop(true);
        return this;
    }
    
    public UBJsonWriter value(final JsonValue value) throws IOException {
        if (value.isObject()) {
            if (value.name != null) {
                this.object(value.name);
            }
            else {
                this.object();
            }
            for (JsonValue child = value.child; child != null; child = child.next) {
                this.value(child);
            }
            this.pop();
        }
        else if (value.isArray()) {
            if (value.name != null) {
                this.array(value.name);
            }
            else {
                this.array();
            }
            for (JsonValue child = value.child; child != null; child = child.next) {
                this.value(child);
            }
            this.pop();
        }
        else if (value.isBoolean()) {
            if (value.name != null) {
                this.name(value.name);
            }
            this.value(value.asBoolean());
        }
        else if (value.isDouble()) {
            if (value.name != null) {
                this.name(value.name);
            }
            this.value(value.asDouble());
        }
        else if (value.isLong()) {
            if (value.name != null) {
                this.name(value.name);
            }
            this.value(value.asLong());
        }
        else if (value.isString()) {
            if (value.name != null) {
                this.name(value.name);
            }
            this.value(value.asString());
        }
        else {
            if (!value.isNull()) {
                throw new IOException("Unhandled JsonValue type");
            }
            if (value.name != null) {
                this.name(value.name);
            }
            this.value();
        }
        return this;
    }
    
    public UBJsonWriter value(final Object object) throws IOException {
        if (object == null) {
            return this.value();
        }
        if (object instanceof Number) {
            final Number number = (Number)object;
            if (object instanceof Byte) {
                return this.value(number.byteValue());
            }
            if (object instanceof Short) {
                return this.value(number.shortValue());
            }
            if (object instanceof Integer) {
                return this.value(number.intValue());
            }
            if (object instanceof Long) {
                return this.value(number.longValue());
            }
            if (object instanceof Float) {
                return this.value(number.floatValue());
            }
            if (object instanceof Double) {
                return this.value(number.doubleValue());
            }
            return this;
        }
        else {
            if (object instanceof Character) {
                return this.value((char)object);
            }
            if (object instanceof CharSequence) {
                return this.value(object.toString());
            }
            throw new IOException("Unknown object type.");
        }
    }
    
    public UBJsonWriter value() throws IOException {
        this.checkName();
        this.out.writeByte(90);
        return this;
    }
    
    public UBJsonWriter set(final String name, final byte value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final short value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final int value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final long value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final float value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final double value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final boolean value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final char value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final String value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final byte[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final short[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final int[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final long[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final float[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final double[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final boolean[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final char[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name, final String[] value) throws IOException {
        return this.name(name).value(value);
    }
    
    public UBJsonWriter set(final String name) throws IOException {
        return this.name(name).value();
    }
    
    private void checkName() {
        if (this.current != null && !this.current.array) {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        }
    }
    
    public UBJsonWriter pop() throws IOException {
        return this.pop(false);
    }
    
    protected UBJsonWriter pop(final boolean silent) throws IOException {
        if (this.named) {
            throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        }
        if (silent) {
            this.stack.pop();
        }
        else {
            this.stack.pop().close();
        }
        this.current = ((this.stack.size == 0) ? null : this.stack.peek());
        return this;
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        while (this.stack.size > 0) {
            this.pop();
        }
        this.out.close();
    }
    
    private class JsonObject
    {
        final boolean array;
        
        JsonObject(final boolean array) throws IOException {
            this.array = array;
            UBJsonWriter.this.out.writeByte(array ? 91 : 123);
        }
        
        void close() throws IOException {
            UBJsonWriter.this.out.writeByte(this.array ? 93 : 125);
        }
    }
}

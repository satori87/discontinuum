// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.regex.Pattern;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter extends Writer
{
    final Writer writer;
    private final Array<JsonObject> stack;
    private JsonObject current;
    private boolean named;
    private OutputType outputType;
    private boolean quoteLongValues;
    
    public JsonWriter(final Writer writer) {
        this.stack = new Array<JsonObject>();
        this.outputType = OutputType.json;
        this.quoteLongValues = false;
        this.writer = writer;
    }
    
    public Writer getWriter() {
        return this.writer;
    }
    
    public void setOutputType(final OutputType outputType) {
        this.outputType = outputType;
    }
    
    public void setQuoteLongValues(final boolean quoteLongValues) {
        this.quoteLongValues = quoteLongValues;
    }
    
    public JsonWriter name(final String name) throws IOException {
        if (this.current == null || this.current.array) {
            throw new IllegalStateException("Current item must be an object.");
        }
        if (!this.current.needsComma) {
            this.current.needsComma = true;
        }
        else {
            this.writer.write(44);
        }
        this.writer.write(this.outputType.quoteName(name));
        this.writer.write(58);
        this.named = true;
        return this;
    }
    
    public JsonWriter object() throws IOException {
        this.requireCommaOrName();
        this.stack.add(this.current = new JsonObject(false));
        return this;
    }
    
    public JsonWriter array() throws IOException {
        this.requireCommaOrName();
        this.stack.add(this.current = new JsonObject(true));
        return this;
    }
    
    public JsonWriter value(Object value) throws IOException {
        if (this.quoteLongValues && (value instanceof Long || value instanceof Double || value instanceof BigDecimal || value instanceof BigInteger)) {
            value = value.toString();
        }
        else if (value instanceof Number) {
            final Number number = (Number)value;
            final long longValue = number.longValue();
            if (number.doubleValue() == longValue) {
                value = longValue;
            }
        }
        this.requireCommaOrName();
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }
    
    public JsonWriter json(final String json) throws IOException {
        this.requireCommaOrName();
        this.writer.write(json);
        return this;
    }
    
    private void requireCommaOrName() throws IOException {
        if (this.current == null) {
            return;
        }
        if (this.current.array) {
            if (!this.current.needsComma) {
                this.current.needsComma = true;
            }
            else {
                this.writer.write(44);
            }
        }
        else {
            if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            }
            this.named = false;
        }
    }
    
    public JsonWriter object(final String name) throws IOException {
        return this.name(name).object();
    }
    
    public JsonWriter array(final String name) throws IOException {
        return this.name(name).array();
    }
    
    public JsonWriter set(final String name, final Object value) throws IOException {
        return this.name(name).value(value);
    }
    
    public JsonWriter json(final String name, final String json) throws IOException {
        return this.name(name).json(json);
    }
    
    public JsonWriter pop() throws IOException {
        if (this.named) {
            throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        }
        this.stack.pop().close();
        this.current = ((this.stack.size == 0) ? null : this.stack.peek());
        return this;
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    @Override
    public void close() throws IOException {
        while (this.stack.size > 0) {
            this.pop();
        }
        this.writer.close();
    }
    
    public enum OutputType
    {
        json("json", 0), 
        javascript("javascript", 1), 
        minimal("minimal", 2);
        
        private static Pattern javascriptPattern;
        private static Pattern minimalNamePattern;
        private static Pattern minimalValuePattern;
        
        static {
            OutputType.javascriptPattern = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
            OutputType.minimalNamePattern = Pattern.compile("^[^\":,}/ ][^:]*$");
            OutputType.minimalValuePattern = Pattern.compile("^[^\":,{\\[\\]/ ][^}\\],]*$");
        }
        
        private OutputType(final String name, final int ordinal) {
        }
        
        public String quoteValue(final Object value) {
            if (value == null) {
                return "null";
            }
            final String string = value.toString();
            if (value instanceof Number || value instanceof Boolean) {
                return string;
            }
            final StringBuilder buffer = new StringBuilder(string);
            buffer.replace('\\', "\\\\").replace('\r', "\\r").replace('\n', "\\n").replace('\t', "\\t");
            if (this == OutputType.minimal && !string.equals("true") && !string.equals("false") && !string.equals("null") && !string.contains("//") && !string.contains("/*")) {
                final int length = buffer.length();
                if (length > 0 && buffer.charAt(length - 1) != ' ' && OutputType.minimalValuePattern.matcher(buffer).matches()) {
                    return buffer.toString();
                }
            }
            return String.valueOf('\"') + buffer.replace('\"', "\\\"").toString() + '\"';
        }
        
        public String quoteName(final String value) {
            final StringBuilder buffer = new StringBuilder(value);
            buffer.replace('\\', "\\\\").replace('\r', "\\r").replace('\n', "\\n").replace('\t', "\\t");
            switch (this) {
                case minimal: {
                    if (!value.contains("//") && !value.contains("/*") && OutputType.minimalNamePattern.matcher(buffer).matches()) {
                        return buffer.toString();
                    }
                }
                case javascript: {
                    if (OutputType.javascriptPattern.matcher(buffer).matches()) {
                        return buffer.toString();
                    }
                    break;
                }
            }
            return String.valueOf('\"') + buffer.replace('\"', "\\\"").toString() + '\"';
        }
    }
    
    private class JsonObject
    {
        final boolean array;
        boolean needsComma;
        
        JsonObject(final boolean array) throws IOException {
            this.array = array;
            JsonWriter.this.writer.write(array ? 91 : 123);
        }
        
        void close() throws IOException {
            JsonWriter.this.writer.write(this.array ? 93 : 125);
        }
    }
}

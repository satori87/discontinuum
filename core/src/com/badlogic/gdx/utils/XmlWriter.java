// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Writer;

public class XmlWriter extends Writer
{
    private final Writer writer;
    private final Array<String> stack;
    private String currentElement;
    private boolean indentNextClose;
    public int indent;
    
    public XmlWriter(final Writer writer) {
        this.stack = new Array<String>();
        this.writer = writer;
    }
    
    private void indent() throws IOException {
        int count = this.indent;
        if (this.currentElement != null) {
            ++count;
        }
        for (int i = 0; i < count; ++i) {
            this.writer.write(9);
        }
    }
    
    public XmlWriter element(final String name) throws IOException {
        if (this.startElementContent()) {
            this.writer.write(10);
        }
        this.indent();
        this.writer.write(60);
        this.writer.write(name);
        this.currentElement = name;
        return this;
    }
    
    public XmlWriter element(final String name, final Object text) throws IOException {
        return this.element(name).text(text).pop();
    }
    
    private boolean startElementContent() throws IOException {
        if (this.currentElement == null) {
            return false;
        }
        ++this.indent;
        this.stack.add(this.currentElement);
        this.currentElement = null;
        this.writer.write(">");
        return true;
    }
    
    public XmlWriter attribute(final String name, final Object value) throws IOException {
        if (this.currentElement == null) {
            throw new IllegalStateException();
        }
        this.writer.write(32);
        this.writer.write(name);
        this.writer.write("=\"");
        this.writer.write((value == null) ? "null" : value.toString());
        this.writer.write(34);
        return this;
    }
    
    public XmlWriter text(final Object text) throws IOException {
        this.startElementContent();
        final String string = (text == null) ? "null" : text.toString();
        this.indentNextClose = (string.length() > 64);
        if (this.indentNextClose) {
            this.writer.write(10);
            this.indent();
        }
        this.writer.write(string);
        if (this.indentNextClose) {
            this.writer.write(10);
        }
        return this;
    }
    
    public XmlWriter pop() throws IOException {
        if (this.currentElement != null) {
            this.writer.write("/>\n");
            this.currentElement = null;
        }
        else {
            this.indent = Math.max(this.indent - 1, 0);
            if (this.indentNextClose) {
                this.indent();
            }
            this.writer.write("</");
            this.writer.write(this.stack.pop());
            this.writer.write(">\n");
        }
        this.indentNextClose = true;
        return this;
    }
    
    @Override
    public void close() throws IOException {
        while (this.stack.size != 0) {
            this.pop();
        }
        this.writer.close();
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.startElementContent();
        this.writer.write(cbuf, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }
}

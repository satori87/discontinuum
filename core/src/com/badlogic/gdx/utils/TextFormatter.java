// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Locale;
import java.text.MessageFormat;

class TextFormatter
{
    private MessageFormat messageFormat;
    private StringBuilder buffer;
    
    public TextFormatter(final Locale locale, final boolean useMessageFormat) {
        this.buffer = new StringBuilder();
        if (useMessageFormat) {
            this.messageFormat = new MessageFormat("", locale);
        }
    }
    
    public String format(final String pattern, final Object... args) {
        if (this.messageFormat != null) {
            this.messageFormat.applyPattern(this.replaceEscapeChars(pattern));
            return this.messageFormat.format(args);
        }
        return this.simpleFormat(pattern, args);
    }
    
    private String replaceEscapeChars(final String pattern) {
        this.buffer.setLength(0);
        boolean changed = false;
        for (int len = pattern.length(), i = 0; i < len; ++i) {
            final char ch = pattern.charAt(i);
            if (ch == '\'') {
                changed = true;
                this.buffer.append("''");
            }
            else if (ch == '{') {
                int j;
                for (j = i + 1; j < len && pattern.charAt(j) == '{'; ++j) {}
                int escaped = (j - i) / 2;
                if (escaped > 0) {
                    changed = true;
                    this.buffer.append('\'');
                    do {
                        this.buffer.append('{');
                    } while (--escaped > 0);
                    this.buffer.append('\'');
                }
                if ((j - i) % 2 != 0) {
                    this.buffer.append('{');
                }
                i = j - 1;
            }
            else {
                this.buffer.append(ch);
            }
        }
        return changed ? this.buffer.toString() : pattern;
    }
    
    private String simpleFormat(final String pattern, final Object... args) {
        this.buffer.setLength(0);
        boolean changed = false;
        int placeholder = -1;
        for (int patternLength = pattern.length(), i = 0; i < patternLength; ++i) {
            final char ch = pattern.charAt(i);
            if (placeholder < 0) {
                if (ch == '{') {
                    changed = true;
                    if (i + 1 < patternLength && pattern.charAt(i + 1) == '{') {
                        this.buffer.append(ch);
                        ++i;
                    }
                    else {
                        placeholder = 0;
                    }
                }
                else {
                    this.buffer.append(ch);
                }
            }
            else if (ch == '}') {
                if (placeholder >= args.length) {
                    throw new IllegalArgumentException("Argument index out of bounds: " + placeholder);
                }
                if (pattern.charAt(i - 1) == '{') {
                    throw new IllegalArgumentException("Missing argument index after a left curly brace");
                }
                if (args[placeholder] == null) {
                    this.buffer.append("null");
                }
                else {
                    this.buffer.append(args[placeholder].toString());
                }
                placeholder = -1;
            }
            else {
                if (ch < '0' || ch > '9') {
                    throw new IllegalArgumentException("Unexpected '" + ch + "' while parsing argument index");
                }
                placeholder = placeholder * 10 + (ch - '0');
            }
        }
        if (placeholder >= 0) {
            throw new IllegalArgumentException("Unmatched braces in the pattern.");
        }
        return changed ? this.buffer.toString() : pattern;
    }
}

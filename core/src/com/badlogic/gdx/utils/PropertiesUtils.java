// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Iterator;
import java.util.Date;
import java.io.Writer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;

public final class PropertiesUtils
{
    private static final int NONE = 0;
    private static final int SLASH = 1;
    private static final int UNICODE = 2;
    private static final int CONTINUE = 3;
    private static final int KEY_DONE = 4;
    private static final int IGNORE = 5;
    private static final String LINE_SEPARATOR = "\n";
    
    private PropertiesUtils() {
    }
    
    public static void load(final ObjectMap<String, String> properties, final Reader reader) throws IOException {
        if (properties == null) {
            throw new NullPointerException("ObjectMap cannot be null");
        }
        if (reader == null) {
            throw new NullPointerException("Reader cannot be null");
        }
        int mode = 0;
        int unicode = 0;
        int count = 0;
        char[] buf = new char[40];
        int offset = 0;
        int keyLength = -1;
        boolean firstChar = true;
        final BufferedReader br = new BufferedReader(reader);
        while (true) {
            int intVal = br.read();
            if (intVal == -1) {
                if (mode == 2 && count <= 4) {
                    throw new IllegalArgumentException("Invalid Unicode sequence: expected format \\uxxxx");
                }
                if (keyLength == -1 && offset > 0) {
                    keyLength = offset;
                }
                if (keyLength >= 0) {
                    final String temp = new String(buf, 0, offset);
                    final String key = temp.substring(0, keyLength);
                    String value = temp.substring(keyLength);
                    if (mode == 1) {
                        value = String.valueOf(value) + "\u0000";
                    }
                    properties.put(key, value);
                }
            }
            else {
                char nextChar = (char)intVal;
                if (offset == buf.length) {
                    final char[] newBuf = new char[buf.length * 2];
                    System.arraycopy(buf, 0, newBuf, 0, offset);
                    buf = newBuf;
                }
                if (mode == 2) {
                    final int digit = Character.digit(nextChar, 16);
                    if (digit >= 0) {
                        unicode = (unicode << 4) + digit;
                        if (++count < 4) {
                            continue;
                        }
                    }
                    else if (count <= 4) {
                        throw new IllegalArgumentException("Invalid Unicode sequence: illegal character");
                    }
                    mode = 0;
                    buf[offset++] = (char)unicode;
                    if (nextChar != '\n') {
                        continue;
                    }
                }
                if (mode == 1) {
                    mode = 0;
                    switch (nextChar) {
                        case '\r': {
                            mode = 3;
                            continue;
                        }
                        case '\n': {
                            mode = 5;
                            continue;
                        }
                        case 'b': {
                            nextChar = '\b';
                            break;
                        }
                        case 'f': {
                            nextChar = '\f';
                            break;
                        }
                        case 'n': {
                            nextChar = '\n';
                            break;
                        }
                        case 'r': {
                            nextChar = '\r';
                            break;
                        }
                        case 't': {
                            nextChar = '\t';
                            break;
                        }
                        case 'u': {
                            mode = 2;
                            count = (unicode = 0);
                            continue;
                        }
                    }
                }
                else {
                    switch (nextChar) {
                        case '!':
                        case '#': {
                            if (firstChar) {
                                do {
                                    intVal = br.read();
                                    if (intVal == -1) {
                                        break;
                                    }
                                    nextChar = (char)intVal;
                                    if (nextChar != '\r') {
                                        continue;
                                    }
                                    break;
                                } while (nextChar != '\n');
                                continue;
                            }
                            break;
                        }
                        case '\n': {
                            if (mode == 3) {
                                mode = 5;
                                continue;
                            }
                        }
                        case '\r': {
                            mode = 0;
                            firstChar = true;
                            if (offset > 0 || (offset == 0 && keyLength == 0)) {
                                if (keyLength == -1) {
                                    keyLength = offset;
                                }
                                final String temp = new String(buf, 0, offset);
                                properties.put(temp.substring(0, keyLength), temp.substring(keyLength));
                            }
                            keyLength = -1;
                            offset = 0;
                            continue;
                        }
                        case '\\': {
                            if (mode == 4) {
                                keyLength = offset;
                            }
                            mode = 1;
                            continue;
                        }
                        case ':':
                        case '=': {
                            if (keyLength == -1) {
                                mode = 0;
                                keyLength = offset;
                                continue;
                            }
                            break;
                        }
                    }
                    if (Character.isSpace(nextChar)) {
                        if (mode == 3) {
                            mode = 5;
                        }
                        if (offset == 0 || offset == keyLength) {
                            continue;
                        }
                        if (mode == 5) {
                            continue;
                        }
                        if (keyLength == -1) {
                            mode = 4;
                            continue;
                        }
                    }
                    if (mode == 5 || mode == 3) {
                        mode = 0;
                    }
                }
                firstChar = false;
                if (mode == 4) {
                    keyLength = offset;
                    mode = 0;
                }
                buf[offset++] = nextChar;
            }
        }
    }
    
    public static void store(final ObjectMap<String, String> properties, final Writer writer, final String comment) throws IOException {
        storeImpl(properties, writer, comment, false);
    }
    
    private static void storeImpl(final ObjectMap<String, String> properties, final Writer writer, final String comment, final boolean escapeUnicode) throws IOException {
        if (comment != null) {
            writeComment(writer, comment);
        }
        writer.write("#");
        writer.write(new Date().toString());
        writer.write("\n");
        final com.badlogic.gdx.utils.StringBuilder sb = new com.badlogic.gdx.utils.StringBuilder(200);
        for (final ObjectMap.Entry<String, String> entry : properties.entries()) {
            dumpString(sb, entry.key, true, escapeUnicode);
            sb.append('=');
            dumpString(sb, entry.value, false, escapeUnicode);
            writer.write("\n");
            writer.write(sb.toString());
            sb.setLength(0);
        }
        writer.flush();
    }
    
    private static void dumpString(final com.badlogic.gdx.utils.StringBuilder outBuffer, final String string, final boolean escapeSpace, final boolean escapeUnicode) {
        for (int len = string.length(), i = 0; i < len; ++i) {
            final char ch = string.charAt(i);
            if (ch > '=' && ch < '\u007f') {
                outBuffer.append((ch == '\\') ? "\\\\" : Character.valueOf(ch));
            }
            else {
                switch (ch) {
                    case ' ': {
                        if (i == 0 || escapeSpace) {
                            outBuffer.append("\\ ");
                            break;
                        }
                        outBuffer.append(ch);
                        break;
                    }
                    case '\n': {
                        outBuffer.append("\\n");
                        break;
                    }
                    case '\r': {
                        outBuffer.append("\\r");
                        break;
                    }
                    case '\t': {
                        outBuffer.append("\\t");
                        break;
                    }
                    case '\f': {
                        outBuffer.append("\\f");
                        break;
                    }
                    case '!':
                    case '#':
                    case ':':
                    case '=': {
                        outBuffer.append('\\').append(ch);
                        break;
                    }
                    default: {
                        if ((ch < ' ' || ch > '~') & escapeUnicode) {
                            final String hex = Integer.toHexString(ch);
                            outBuffer.append("\\u");
                            for (int j = 0; j < 4 - hex.length(); ++j) {
                                outBuffer.append('0');
                            }
                            outBuffer.append(hex);
                            break;
                        }
                        outBuffer.append(ch);
                        break;
                    }
                }
            }
        }
    }
    
    private static void writeComment(final Writer writer, final String comment) throws IOException {
        writer.write("#");
        final int len = comment.length();
        int curIndex = 0;
        int lastIndex = 0;
        while (curIndex < len) {
            final char c = comment.charAt(curIndex);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (lastIndex != curIndex) {
                    writer.write(comment.substring(lastIndex, curIndex));
                }
                if (c > '\u00ff') {
                    final String hex = Integer.toHexString(c);
                    writer.write("\\u");
                    for (int j = 0; j < 4 - hex.length(); ++j) {
                        writer.write(48);
                    }
                    writer.write(hex);
                }
                else {
                    writer.write("\n");
                    if (c == '\r' && curIndex != len - 1 && comment.charAt(curIndex + 1) == '\n') {
                        ++curIndex;
                    }
                    if (curIndex == len - 1 || (comment.charAt(curIndex + 1) != '#' && comment.charAt(curIndex + 1) != '!')) {
                        writer.write("#");
                    }
                }
                lastIndex = curIndex + 1;
            }
            ++curIndex;
        }
        if (lastIndex != curIndex) {
            writer.write(comment.substring(lastIndex, curIndex));
        }
        writer.write("\n");
    }
}

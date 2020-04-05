// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.util.Arrays;

public class StringBuilder implements Appendable, CharSequence
{
    static final int INITIAL_CAPACITY = 16;
    public char[] chars;
    public int length;
    private static final char[] digits;
    
    static {
        digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    }
    
    public static int numChars(int value, final int radix) {
        int result = (value < 0) ? 2 : 1;
        while ((value /= radix) != 0) {
            ++result;
        }
        return result;
    }
    
    public static int numChars(long value, final int radix) {
        int result = (value < 0L) ? 2 : 1;
        while ((value /= radix) != 0L) {
            ++result;
        }
        return result;
    }
    
    final char[] getValue() {
        return this.chars;
    }
    
    public StringBuilder() {
        this.chars = new char[16];
    }
    
    public StringBuilder(final int capacity) {
        if (capacity < 0) {
            throw new NegativeArraySizeException();
        }
        this.chars = new char[capacity];
    }
    
    public StringBuilder(final CharSequence seq) {
        this(seq.toString());
    }
    
    public StringBuilder(final StringBuilder builder) {
        this.length = builder.length;
        this.chars = new char[this.length + 16];
        System.arraycopy(builder.chars, 0, this.chars, 0, this.length);
    }
    
    public StringBuilder(final String string) {
        this.length = string.length();
        this.chars = new char[this.length + 16];
        string.getChars(0, this.length, this.chars, 0);
    }
    
    private void enlargeBuffer(final int min) {
        final int newSize = (this.chars.length >> 1) + this.chars.length + 2;
        final char[] newData = new char[(min > newSize) ? min : newSize];
        System.arraycopy(this.chars, 0, newData, 0, this.length);
        this.chars = newData;
    }
    
    final void appendNull() {
        final int newSize = this.length + 4;
        if (newSize > this.chars.length) {
            this.enlargeBuffer(newSize);
        }
        this.chars[this.length++] = 'n';
        this.chars[this.length++] = 'u';
        this.chars[this.length++] = 'l';
        this.chars[this.length++] = 'l';
    }
    
    final void append0(final char[] value) {
        final int newSize = this.length + value.length;
        if (newSize > this.chars.length) {
            this.enlargeBuffer(newSize);
        }
        System.arraycopy(value, 0, this.chars, this.length, value.length);
        this.length = newSize;
    }
    
    final void append0(final char[] value, final int offset, final int length) {
        if (offset > value.length || offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset out of bounds: " + offset);
        }
        if (length < 0 || value.length - offset < length) {
            throw new ArrayIndexOutOfBoundsException("Length out of bounds: " + length);
        }
        final int newSize = this.length + length;
        if (newSize > this.chars.length) {
            this.enlargeBuffer(newSize);
        }
        System.arraycopy(value, offset, this.chars, this.length, length);
        this.length = newSize;
    }
    
    final void append0(final char ch) {
        if (this.length == this.chars.length) {
            this.enlargeBuffer(this.length + 1);
        }
        this.chars[this.length++] = ch;
    }
    
    final void append0(final String string) {
        if (string == null) {
            this.appendNull();
            return;
        }
        final int adding = string.length();
        final int newSize = this.length + adding;
        if (newSize > this.chars.length) {
            this.enlargeBuffer(newSize);
        }
        string.getChars(0, adding, this.chars, this.length);
        this.length = newSize;
    }
    
    final void append0(CharSequence s, final int start, final int end) {
        if (s == null) {
            s = "null";
        }
        if (start < 0 || end < 0 || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        this.append0(s.subSequence(start, end).toString());
    }
    
    public int capacity() {
        return this.chars.length;
    }
    
    @Override
    public char charAt(final int index) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return this.chars[index];
    }
    
    final void delete0(final int start, int end) {
        if (start >= 0) {
            if (end > this.length) {
                end = this.length;
            }
            if (end == start) {
                return;
            }
            if (end > start) {
                final int count = this.length - end;
                if (count >= 0) {
                    System.arraycopy(this.chars, end, this.chars, start, count);
                }
                this.length -= end - start;
                return;
            }
        }
        throw new StringIndexOutOfBoundsException();
    }
    
    final void deleteCharAt0(final int location) {
        if (location < 0 || location >= this.length) {
            throw new StringIndexOutOfBoundsException(location);
        }
        final int count = this.length - location - 1;
        if (count > 0) {
            System.arraycopy(this.chars, location + 1, this.chars, location, count);
        }
        --this.length;
    }
    
    public void ensureCapacity(final int min) {
        if (min > this.chars.length) {
            final int twice = (this.chars.length << 1) + 2;
            this.enlargeBuffer((twice > min) ? twice : min);
        }
    }
    
    public void getChars(final int start, final int end, final char[] dest, final int destStart) {
        if (start > this.length || end > this.length || start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        System.arraycopy(this.chars, start, dest, destStart, end - start);
    }
    
    final void insert0(final int index, final char[] value) {
        if (index < 0 || index > this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (value.length != 0) {
            this.move(value.length, index);
            System.arraycopy(value, 0, value, index, value.length);
            this.length += value.length;
        }
    }
    
    final void insert0(final int index, final char[] value, final int start, final int length) {
        if (index < 0 || index > length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (start >= 0 && length >= 0 && length <= value.length - start) {
            if (length != 0) {
                this.move(length, index);
                System.arraycopy(value, start, this.chars, index, length);
                this.length += length;
            }
            return;
        }
        throw new StringIndexOutOfBoundsException("offset " + start + ", length " + length + ", char[].length " + value.length);
    }
    
    final void insert0(final int index, final char ch) {
        if (index < 0 || index > this.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.move(1, index);
        this.chars[index] = ch;
        ++this.length;
    }
    
    final void insert0(final int index, String string) {
        if (index >= 0 && index <= this.length) {
            if (string == null) {
                string = "null";
            }
            final int min = string.length();
            if (min != 0) {
                this.move(min, index);
                string.getChars(0, min, this.chars, index);
                this.length += min;
            }
            return;
        }
        throw new StringIndexOutOfBoundsException(index);
    }
    
    final void insert0(final int index, CharSequence s, final int start, final int end) {
        if (s == null) {
            s = "null";
        }
        if (index < 0 || index > this.length || start < 0 || end < 0 || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        this.insert0(index, s.subSequence(start, end).toString());
    }
    
    @Override
    public int length() {
        return this.length;
    }
    
    private void move(final int size, final int index) {
        if (this.chars.length - this.length >= size) {
            System.arraycopy(this.chars, index, this.chars, index + size, this.length - index);
            return;
        }
        final int a = this.length + size;
        final int b = (this.chars.length << 1) + 2;
        final int newSize = (a > b) ? a : b;
        final char[] newData = new char[newSize];
        System.arraycopy(this.chars, 0, newData, 0, index);
        System.arraycopy(this.chars, index, newData, index + size, this.length - index);
        this.chars = newData;
    }
    
    final void replace0(final int start, int end, final String string) {
        if (start >= 0) {
            if (end > this.length) {
                end = this.length;
            }
            if (end > start) {
                final int stringLength = string.length();
                final int diff = end - start - stringLength;
                if (diff > 0) {
                    System.arraycopy(this.chars, end, this.chars, start + stringLength, this.length - end);
                }
                else if (diff < 0) {
                    this.move(-diff, end);
                }
                string.getChars(0, stringLength, this.chars, start);
                this.length -= diff;
                return;
            }
            if (start == end) {
                if (string == null) {
                    throw new NullPointerException();
                }
                this.insert0(start, string);
                return;
            }
        }
        throw new StringIndexOutOfBoundsException();
    }
    
    final void reverse0() {
        if (this.length < 2) {
            return;
        }
        int end = this.length - 1;
        char frontHigh = this.chars[0];
        char endLow = this.chars[end];
        boolean allowFrontSur = true;
        boolean allowEndSur = true;
        for (int i = 0, mid = this.length / 2; i < mid; ++i, --end) {
            final char frontLow = this.chars[i + 1];
            final char endHigh = this.chars[end - 1];
            final boolean surAtFront = allowFrontSur && frontLow >= '\udc00' && frontLow <= '\udfff' && frontHigh >= '\ud800' && frontHigh <= '\udbff';
            if (surAtFront && this.length < 3) {
                return;
            }
            final boolean surAtEnd = allowEndSur && endHigh >= '\ud800' && endHigh <= '\udbff' && endLow >= '\udc00' && endLow <= '\udfff';
            allowEndSur = (allowFrontSur = true);
            if (surAtFront == surAtEnd) {
                if (surAtFront) {
                    this.chars[end] = frontLow;
                    this.chars[end - 1] = frontHigh;
                    this.chars[i] = endHigh;
                    this.chars[i + 1] = endLow;
                    frontHigh = this.chars[i + 2];
                    endLow = this.chars[end - 2];
                    ++i;
                    --end;
                }
                else {
                    this.chars[end] = frontHigh;
                    this.chars[i] = endLow;
                    frontHigh = frontLow;
                    endLow = endHigh;
                }
            }
            else if (surAtFront) {
                this.chars[end] = frontLow;
                this.chars[i] = endLow;
                endLow = endHigh;
                allowFrontSur = false;
            }
            else {
                this.chars[end] = frontHigh;
                this.chars[i] = endHigh;
                frontHigh = frontLow;
                allowEndSur = false;
            }
        }
        if ((this.length & 0x1) == 0x1 && (!allowFrontSur || !allowEndSur)) {
            this.chars[end] = (allowFrontSur ? endLow : frontHigh);
        }
    }
    
    public void setCharAt(final int index, final char ch) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        this.chars[index] = ch;
    }
    
    public void setLength(final int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        if (newLength > this.chars.length) {
            this.enlargeBuffer(newLength);
        }
        else if (this.length < newLength) {
            Arrays.fill(this.chars, this.length, newLength, '\0');
        }
        this.length = newLength;
    }
    
    public String substring(final int start) {
        if (start < 0 || start > this.length) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (start == this.length) {
            return "";
        }
        return new String(this.chars, start, this.length - start);
    }
    
    public String substring(final int start, final int end) {
        if (start < 0 || start > end || end > this.length) {
            throw new StringIndexOutOfBoundsException();
        }
        if (start == end) {
            return "";
        }
        return new String(this.chars, start, end - start);
    }
    
    @Override
    public String toString() {
        if (this.length == 0) {
            return "";
        }
        return new String(this.chars, 0, this.length);
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.substring(start, end);
    }
    
    public int indexOf(final String string) {
        return this.indexOf(string, 0);
    }
    
    public int indexOf(final String subString, int start) {
        if (start < 0) {
            start = 0;
        }
        final int subCount = subString.length();
        if (subCount <= 0) {
            return (start < this.length || start == 0) ? start : this.length;
        }
        if (subCount + start > this.length) {
            return -1;
        }
        final char firstChar = subString.charAt(0);
        while (true) {
            int i = start;
            boolean found = false;
            while (i < this.length) {
                if (this.chars[i] == firstChar) {
                    found = true;
                    break;
                }
                ++i;
            }
            if (!found || subCount + i > this.length) {
                return -1;
            }
            int o1 = i;
            int o2 = 0;
            while (++o2 < subCount && this.chars[++o1] == subString.charAt(o2)) {}
            if (o2 == subCount) {
                return i;
            }
            start = i + 1;
        }
    }
    
    public int lastIndexOf(final String string) {
        return this.lastIndexOf(string, this.length);
    }
    
    public int lastIndexOf(final String subString, int start) {
        final int subCount = subString.length();
        if (subCount > this.length || start < 0) {
            return -1;
        }
        if (subCount <= 0) {
            return (start < this.length) ? start : this.length;
        }
        if (start > this.length - subCount) {
            start = this.length - subCount;
        }
        final char firstChar = subString.charAt(0);
        while (true) {
            int i = start;
            boolean found = false;
            while (i >= 0) {
                if (this.chars[i] == firstChar) {
                    found = true;
                    break;
                }
                --i;
            }
            if (!found) {
                return -1;
            }
            int o1 = i;
            int o2 = 0;
            while (++o2 < subCount && this.chars[++o1] == subString.charAt(o2)) {}
            if (o2 == subCount) {
                return i;
            }
            start = i - 1;
        }
    }
    
    public void trimToSize() {
        if (this.length < this.chars.length) {
            final char[] newValue = new char[this.length];
            System.arraycopy(this.chars, 0, newValue, 0, this.length);
            this.chars = newValue;
        }
    }
    
    public int codePointAt(final int index) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAt(this.chars, index, this.length);
    }
    
    public int codePointBefore(final int index) {
        if (index < 1 || index > this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointBefore(this.chars, index);
    }
    
    public int codePointCount(final int beginIndex, final int endIndex) {
        if (beginIndex < 0 || endIndex > this.length || beginIndex > endIndex) {
            throw new StringIndexOutOfBoundsException();
        }
        return Character.codePointCount(this.chars, beginIndex, endIndex - beginIndex);
    }
    
    public int offsetByCodePoints(final int index, final int codePointOffset) {
        return Character.offsetByCodePoints(this.chars, 0, this.length, index, codePointOffset);
    }
    
    public StringBuilder append(final boolean b) {
        this.append0(b ? "true" : "false");
        return this;
    }
    
    @Override
    public StringBuilder append(final char c) {
        this.append0(c);
        return this;
    }
    
    public StringBuilder append(final int value) {
        return this.append(value, 0);
    }
    
    public StringBuilder append(final int value, final int minLength) {
        return this.append(value, minLength, '0');
    }
    
    public StringBuilder append(int value, final int minLength, final char prefix) {
        if (value == Integer.MIN_VALUE) {
            this.append0("-2147483648");
            return this;
        }
        if (value < 0) {
            this.append0('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - numChars(value, 10); j > 0; --j) {
                this.append(prefix);
            }
        }
        if (value >= 10000) {
            if (value >= 1000000000) {
                this.append0(StringBuilder.digits[(int)(value % 10000000000L / 1000000000L)]);
            }
            if (value >= 100000000) {
                this.append0(StringBuilder.digits[value % 1000000000 / 100000000]);
            }
            if (value >= 10000000) {
                this.append0(StringBuilder.digits[value % 100000000 / 10000000]);
            }
            if (value >= 1000000) {
                this.append0(StringBuilder.digits[value % 10000000 / 1000000]);
            }
            if (value >= 100000) {
                this.append0(StringBuilder.digits[value % 1000000 / 100000]);
            }
            this.append0(StringBuilder.digits[value % 100000 / 10000]);
        }
        if (value >= 1000) {
            this.append0(StringBuilder.digits[value % 10000 / 1000]);
        }
        if (value >= 100) {
            this.append0(StringBuilder.digits[value % 1000 / 100]);
        }
        if (value >= 10) {
            this.append0(StringBuilder.digits[value % 100 / 10]);
        }
        this.append0(StringBuilder.digits[value % 10]);
        return this;
    }
    
    public StringBuilder append(final long value) {
        return this.append(value, 0);
    }
    
    public StringBuilder append(final long value, final int minLength) {
        return this.append(value, minLength, '0');
    }
    
    public StringBuilder append(long value, final int minLength, final char prefix) {
        if (value == Long.MIN_VALUE) {
            this.append0("-9223372036854775808");
            return this;
        }
        if (value < 0L) {
            this.append0('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - numChars(value, 10); j > 0; --j) {
                this.append(prefix);
            }
        }
        if (value >= 10000L) {
            if (value >= 1000000000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 1.0E19 / 1.0E18)]);
            }
            if (value >= 100000000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 1000000000000000000L / 100000000000000000L)]);
            }
            if (value >= 10000000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 100000000000000000L / 10000000000000000L)]);
            }
            if (value >= 1000000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 10000000000000000L / 1000000000000000L)]);
            }
            if (value >= 100000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 1000000000000000L / 100000000000000L)]);
            }
            if (value >= 10000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 100000000000000L / 10000000000000L)]);
            }
            if (value >= 1000000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 10000000000000L / 1000000000000L)]);
            }
            if (value >= 100000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 1000000000000L / 100000000000L)]);
            }
            if (value >= 10000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 100000000000L / 10000000000L)]);
            }
            if (value >= 1000000000L) {
                this.append0(StringBuilder.digits[(int)(value % 10000000000L / 1000000000L)]);
            }
            if (value >= 100000000L) {
                this.append0(StringBuilder.digits[(int)(value % 1000000000L / 100000000L)]);
            }
            if (value >= 10000000L) {
                this.append0(StringBuilder.digits[(int)(value % 100000000L / 10000000L)]);
            }
            if (value >= 1000000L) {
                this.append0(StringBuilder.digits[(int)(value % 10000000L / 1000000L)]);
            }
            if (value >= 100000L) {
                this.append0(StringBuilder.digits[(int)(value % 1000000L / 100000L)]);
            }
            this.append0(StringBuilder.digits[(int)(value % 100000L / 10000L)]);
        }
        if (value >= 1000L) {
            this.append0(StringBuilder.digits[(int)(value % 10000L / 1000L)]);
        }
        if (value >= 100L) {
            this.append0(StringBuilder.digits[(int)(value % 1000L / 100L)]);
        }
        if (value >= 10L) {
            this.append0(StringBuilder.digits[(int)(value % 100L / 10L)]);
        }
        this.append0(StringBuilder.digits[(int)(value % 10L)]);
        return this;
    }
    
    public StringBuilder append(final float f) {
        this.append0(Float.toString(f));
        return this;
    }
    
    public StringBuilder append(final double d) {
        this.append0(Double.toString(d));
        return this;
    }
    
    public StringBuilder append(final Object obj) {
        if (obj == null) {
            this.appendNull();
        }
        else {
            this.append0(obj.toString());
        }
        return this;
    }
    
    public StringBuilder append(final String str) {
        this.append0(str);
        return this;
    }
    
    public StringBuilder appendLine(final String str) {
        this.append0(str);
        this.append0('\n');
        return this;
    }
    
    public StringBuilder append(final char[] ch) {
        this.append0(ch);
        return this;
    }
    
    public StringBuilder append(final char[] str, final int offset, final int len) {
        this.append0(str, offset, len);
        return this;
    }
    
    @Override
    public StringBuilder append(final CharSequence csq) {
        if (csq == null) {
            this.appendNull();
        }
        else if (csq instanceof StringBuilder) {
            final StringBuilder builder = (StringBuilder)csq;
            this.append0(builder.chars, 0, builder.length);
        }
        else {
            this.append0(csq.toString());
        }
        return this;
    }
    
    public StringBuilder append(final StringBuilder builder) {
        if (builder == null) {
            this.appendNull();
        }
        else {
            this.append0(builder.chars, 0, builder.length);
        }
        return this;
    }
    
    @Override
    public StringBuilder append(final CharSequence csq, final int start, final int end) {
        this.append0(csq, start, end);
        return this;
    }
    
    public StringBuilder append(final StringBuilder builder, final int start, final int end) {
        if (builder == null) {
            this.appendNull();
        }
        else {
            this.append0(builder.chars, start, end);
        }
        return this;
    }
    
    public StringBuilder appendCodePoint(final int codePoint) {
        this.append0(Character.toChars(codePoint));
        return this;
    }
    
    public StringBuilder delete(final int start, final int end) {
        this.delete0(start, end);
        return this;
    }
    
    public StringBuilder deleteCharAt(final int index) {
        this.deleteCharAt0(index);
        return this;
    }
    
    public void clear() {
        this.length = 0;
    }
    
    public StringBuilder insert(final int offset, final boolean b) {
        this.insert0(offset, b ? "true" : "false");
        return this;
    }
    
    public StringBuilder insert(final int offset, final char c) {
        this.insert0(offset, c);
        return this;
    }
    
    public StringBuilder insert(final int offset, final int i) {
        this.insert0(offset, Integer.toString(i));
        return this;
    }
    
    public StringBuilder insert(final int offset, final long l) {
        this.insert0(offset, Long.toString(l));
        return this;
    }
    
    public StringBuilder insert(final int offset, final float f) {
        this.insert0(offset, Float.toString(f));
        return this;
    }
    
    public StringBuilder insert(final int offset, final double d) {
        this.insert0(offset, Double.toString(d));
        return this;
    }
    
    public StringBuilder insert(final int offset, final Object obj) {
        this.insert0(offset, (obj == null) ? "null" : obj.toString());
        return this;
    }
    
    public StringBuilder insert(final int offset, final String str) {
        this.insert0(offset, str);
        return this;
    }
    
    public StringBuilder insert(final int offset, final char[] ch) {
        this.insert0(offset, ch);
        return this;
    }
    
    public StringBuilder insert(final int offset, final char[] str, final int strOffset, final int strLen) {
        this.insert0(offset, str, strOffset, strLen);
        return this;
    }
    
    public StringBuilder insert(final int offset, final CharSequence s) {
        this.insert0(offset, (s == null) ? "null" : s.toString());
        return this;
    }
    
    public StringBuilder insert(final int offset, final CharSequence s, final int start, final int end) {
        this.insert0(offset, s, start, end);
        return this;
    }
    
    public StringBuilder replace(final int start, final int end, final String str) {
        this.replace0(start, end, str);
        return this;
    }
    
    public StringBuilder replace(final String find, final String replace) {
        final int findLength = find.length();
        final int replaceLength = replace.length();
        int index = 0;
        while (true) {
            index = this.indexOf(find, index);
            if (index == -1) {
                break;
            }
            this.replace0(index, index + findLength, replace);
            index += replaceLength;
        }
        return this;
    }
    
    public StringBuilder replace(final char find, final String replace) {
        final int replaceLength = replace.length();
        int index = 0;
        while (index != this.length) {
            if (this.chars[index] == find) {
                this.replace0(index, index + 1, replace);
                index += replaceLength;
            }
            else {
                ++index;
            }
        }
        return this;
    }
    
    public StringBuilder reverse() {
        this.reverse0();
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 + this.length;
        result = 31 * result + Arrays.hashCode(this.chars);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final StringBuilder other = (StringBuilder)obj;
        final int length = this.length;
        if (length != other.length) {
            return false;
        }
        final char[] chars = this.chars;
        final char[] chars2 = other.chars;
        if (chars == chars2) {
            return true;
        }
        if (chars == null || chars2 == null) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (chars[i] != chars2[i]) {
                return false;
            }
        }
        return true;
    }
}

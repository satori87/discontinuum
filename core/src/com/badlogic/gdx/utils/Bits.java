// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class Bits
{
    long[] bits;
    
    public Bits() {
        this.bits = new long[1];
    }
    
    public Bits(final int nbits) {
        this.bits = new long[1];
        this.checkCapacity(nbits >>> 6);
    }
    
    public boolean get(final int index) {
        final int word = index >>> 6;
        return word < this.bits.length && (this.bits[word] & 1L << (index & 0x3F)) != 0x0L;
    }
    
    public boolean getAndClear(final int index) {
        final int word = index >>> 6;
        if (word >= this.bits.length) {
            return false;
        }
        final long oldBits = this.bits[word];
        final long[] bits = this.bits;
        final int n = word;
        bits[n] &= ~(1L << (index & 0x3F));
        return this.bits[word] != oldBits;
    }
    
    public boolean getAndSet(final int index) {
        final int word = index >>> 6;
        this.checkCapacity(word);
        final long oldBits = this.bits[word];
        final long[] bits = this.bits;
        final int n = word;
        bits[n] |= 1L << (index & 0x3F);
        return this.bits[word] == oldBits;
    }
    
    public void set(final int index) {
        final int word = index >>> 6;
        this.checkCapacity(word);
        final long[] bits = this.bits;
        final int n = word;
        bits[n] |= 1L << (index & 0x3F);
    }
    
    public void flip(final int index) {
        final int word = index >>> 6;
        this.checkCapacity(word);
        final long[] bits = this.bits;
        final int n = word;
        bits[n] ^= 1L << (index & 0x3F);
    }
    
    private void checkCapacity(final int len) {
        if (len >= this.bits.length) {
            final long[] newBits = new long[len + 1];
            System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
            this.bits = newBits;
        }
    }
    
    public void clear(final int index) {
        final int word = index >>> 6;
        if (word >= this.bits.length) {
            return;
        }
        final long[] bits = this.bits;
        final int n = word;
        bits[n] &= ~(1L << (index & 0x3F));
    }
    
    public void clear() {
        final long[] bits = this.bits;
        for (int length = bits.length, i = 0; i < length; ++i) {
            bits[i] = 0L;
        }
    }
    
    public int numBits() {
        return this.bits.length << 6;
    }
    
    public int length() {
        final long[] bits = this.bits;
        for (int word = bits.length - 1; word >= 0; --word) {
            final long bitsAtWord = bits[word];
            if (bitsAtWord != 0L) {
                for (int bit = 63; bit >= 0; --bit) {
                    if ((bitsAtWord & 1L << (bit & 0x3F)) != 0x0L) {
                        return (word << 6) + bit + 1;
                    }
                }
            }
        }
        return 0;
    }
    
    public boolean isEmpty() {
        final long[] bits = this.bits;
        for (int length = bits.length, i = 0; i < length; ++i) {
            if (bits[i] != 0L) {
                return false;
            }
        }
        return true;
    }
    
    public int nextSetBit(final int fromIndex) {
        final long[] bits = this.bits;
        int word = fromIndex >>> 6;
        final int bitsLength = bits.length;
        if (word >= bitsLength) {
            return -1;
        }
        long bitsAtWord = bits[word];
        if (bitsAtWord != 0L) {
            for (int i = fromIndex & 0x3F; i < 64; ++i) {
                if ((bitsAtWord & 1L << (i & 0x3F)) != 0x0L) {
                    return (word << 6) + i;
                }
            }
        }
        ++word;
        while (word < bitsLength) {
            if (word != 0) {
                bitsAtWord = bits[word];
                if (bitsAtWord != 0L) {
                    for (int i = 0; i < 64; ++i) {
                        if ((bitsAtWord & 1L << (i & 0x3F)) != 0x0L) {
                            return (word << 6) + i;
                        }
                    }
                }
            }
            ++word;
        }
        return -1;
    }
    
    public int nextClearBit(final int fromIndex) {
        final long[] bits = this.bits;
        int word = fromIndex >>> 6;
        final int bitsLength = bits.length;
        if (word >= bitsLength) {
            return bits.length << 6;
        }
        long bitsAtWord = bits[word];
        for (int i = fromIndex & 0x3F; i < 64; ++i) {
            if ((bitsAtWord & 1L << (i & 0x3F)) == 0x0L) {
                return (word << 6) + i;
            }
        }
        ++word;
        while (word < bitsLength) {
            if (word == 0) {
                return word << 6;
            }
            bitsAtWord = bits[word];
            for (int i = 0; i < 64; ++i) {
                if ((bitsAtWord & 1L << (i & 0x3F)) == 0x0L) {
                    return (word << 6) + i;
                }
            }
            ++word;
        }
        return bits.length << 6;
    }
    
    public void and(final Bits other) {
        final int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; ++i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] &= other.bits[i];
        }
        if (this.bits.length > commonWords) {
            for (int i = commonWords, s = this.bits.length; s > i; ++i) {
                this.bits[i] = 0L;
            }
        }
    }
    
    public void andNot(final Bits other) {
        for (int i = 0, j = this.bits.length, k = other.bits.length; i < j && i < k; ++i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] &= ~other.bits[i];
        }
    }
    
    public void or(final Bits other) {
        final int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; ++i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] |= other.bits[i];
        }
        if (commonWords < other.bits.length) {
            this.checkCapacity(other.bits.length);
            for (int i = commonWords, s = other.bits.length; s > i; ++i) {
                this.bits[i] = other.bits[i];
            }
        }
    }
    
    public void xor(final Bits other) {
        final int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; ++i) {
            final long[] bits = this.bits;
            final int n = i;
            bits[n] ^= other.bits[i];
        }
        if (commonWords < other.bits.length) {
            this.checkCapacity(other.bits.length);
            for (int i = commonWords, s = other.bits.length; s > i; ++i) {
                this.bits[i] = other.bits[i];
            }
        }
    }
    
    public boolean intersects(final Bits other) {
        final long[] bits = this.bits;
        final long[] otherBits = other.bits;
        for (int i = Math.min(bits.length, otherBits.length) - 1; i >= 0; --i) {
            if ((bits[i] & otherBits[i]) != 0x0L) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAll(final Bits other) {
        final long[] bits = this.bits;
        final long[] otherBits = other.bits;
        int otherBitsLength;
        int i;
        int bitsLength;
        for (otherBitsLength = otherBits.length, bitsLength = (i = bits.length); i < otherBitsLength; ++i) {
            if (otherBits[i] != 0L) {
                return false;
            }
        }
        for (i = Math.min(bitsLength, otherBitsLength) - 1; i >= 0; --i) {
            if ((bits[i] & otherBits[i]) != otherBits[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int word = this.length() >>> 6;
        int hash = 0;
        for (int i = 0; word >= i; ++i) {
            hash = 127 * hash + (int)(this.bits[i] ^ this.bits[i] >>> 32);
        }
        return hash;
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
        final Bits other = (Bits)obj;
        final long[] otherBits = other.bits;
        for (int commonWords = Math.min(this.bits.length, otherBits.length), i = 0; commonWords > i; ++i) {
            if (this.bits[i] != otherBits[i]) {
                return false;
            }
        }
        return this.bits.length == otherBits.length || this.length() == other.length();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

class ComparableTimSort
{
    private static final int MIN_MERGE = 32;
    private Object[] a;
    private static final int MIN_GALLOP = 7;
    private int minGallop;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private Object[] tmp;
    private int tmpCount;
    private int stackSize;
    private final int[] runBase;
    private final int[] runLen;
    private static final boolean DEBUG = false;
    
    ComparableTimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }
    
    public void doSort(final Object[] a, int lo, final int hi) {
        this.stackSize = 0;
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }
        if (nRemaining < 32) {
            final int initRunLen = countRunAndMakeAscending(a, lo, hi);
            binarySort(a, lo, hi, lo + initRunLen);
            return;
        }
        this.a = a;
        this.tmpCount = 0;
        final int minRun = minRunLength(nRemaining);
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi);
            if (runLen < minRun) {
                final int force = (nRemaining <= minRun) ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen);
                runLen = force;
            }
            this.pushRun(lo, runLen);
            this.mergeCollapse();
            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining != 0);
        this.mergeForceCollapse();
        this.a = null;
        final Object[] tmp = this.tmp;
        for (int i = 0, n = this.tmpCount; i < n; ++i) {
            tmp[i] = null;
        }
    }
    
    private ComparableTimSort(final Object[] a) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.a = a;
        final int len = a.length;
        final Object[] newArray = new Object[(len < 512) ? (len >>> 1) : 256];
        this.tmp = newArray;
        final int stackLen = (len < 120) ? 5 : ((len < 1542) ? 10 : ((len < 119151) ? 19 : 40));
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }
    
    static void sort(final Object[] a) {
        sort(a, 0, a.length);
    }
    
    static void sort(final Object[] a, int lo, final int hi) {
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }
        if (nRemaining < 32) {
            final int initRunLen = countRunAndMakeAscending(a, lo, hi);
            binarySort(a, lo, hi, lo + initRunLen);
            return;
        }
        final ComparableTimSort ts = new ComparableTimSort(a);
        final int minRun = minRunLength(nRemaining);
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi);
            if (runLen < minRun) {
                final int force = (nRemaining <= minRun) ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen);
                runLen = force;
            }
            ts.pushRun(lo, runLen);
            ts.mergeCollapse();
            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining != 0);
        ts.mergeForceCollapse();
    }
    
    private static void binarySort(final Object[] a, final int lo, final int hi, int start) {
        if (start == lo) {
            ++start;
        }
        while (start < hi) {
            final Comparable<Object> pivot = (Comparable<Object>)a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                final int mid = left + right >>> 1;
                if (pivot.compareTo(a[mid]) < 0) {
                    right = mid;
                }
                else {
                    left = mid + 1;
                }
            }
            final int n = start - left;
            switch (n) {
                case 2: {
                    a[left + 2] = a[left + 1];
                }
                case 1: {
                    a[left + 1] = a[left];
                    break;
                }
                default: {
                    System.arraycopy(a, left, a, left + 1, n);
                    break;
                }
            }
            a[left] = pivot;
            ++start;
        }
    }
    
    private static int countRunAndMakeAscending(final Object[] a, final int lo, final int hi) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        if (((Comparable)a[runHi++]).compareTo(a[lo]) < 0) {
            while (runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) < 0) {
                ++runHi;
            }
            reverseRange(a, lo, runHi);
        }
        else {
            while (runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) >= 0) {
                ++runHi;
            }
        }
        return runHi - lo;
    }
    
    private static void reverseRange(final Object[] a, int lo, int hi) {
        --hi;
        while (lo < hi) {
            final Object t = a[lo];
            a[lo++] = a[hi];
            a[hi--] = t;
        }
    }
    
    private static int minRunLength(int n) {
        int r = 0;
        while (n >= 32) {
            r |= (n & 0x1);
            n >>= 1;
        }
        return n + r;
    }
    
    private void pushRun(final int runBase, final int runLen) {
        this.runBase[this.stackSize] = runBase;
        this.runLen[this.stackSize] = runLen;
        ++this.stackSize;
    }
    
    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    --n;
                }
                this.mergeAt(n);
            }
            else {
                if (this.runLen[n] > this.runLen[n + 1]) {
                    break;
                }
                this.mergeAt(n);
            }
        }
    }
    
    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                --n;
            }
            this.mergeAt(n);
        }
    }
    
    private void mergeAt(final int i) {
        int base1 = this.runBase[i];
        int len1 = this.runLen[i];
        final int base2 = this.runBase[i + 1];
        int len2 = this.runLen[i + 1];
        this.runLen[i] = len1 + len2;
        if (i == this.stackSize - 3) {
            this.runBase[i + 1] = this.runBase[i + 2];
            this.runLen[i + 1] = this.runLen[i + 2];
        }
        --this.stackSize;
        final int k = gallopRight((Comparable<Object>)this.a[base2], this.a, base1, len1, 0);
        base1 += k;
        len1 -= k;
        if (len1 == 0) {
            return;
        }
        len2 = gallopLeft((Comparable<Object>)this.a[base1 + len1 - 1], this.a, base2, len2, len2 - 1);
        if (len2 == 0) {
            return;
        }
        if (len1 <= len2) {
            this.mergeLo(base1, len1, base2, len2);
        }
        else {
            this.mergeHi(base1, len1, base2, len2);
        }
    }
    
    private static int gallopLeft(final Comparable<Object> key, final Object[] a, final int base, final int len, final int hint) {
        int lastOfs = 0;
        int ofs = 1;
        if (key.compareTo(a[base + hint]) > 0) {
            int maxOfs;
            for (maxOfs = len - hint; ofs < maxOfs && key.compareTo(a[base + hint + ofs]) > 0; ofs = maxOfs) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) {}
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            lastOfs += hint;
            ofs += hint;
        }
        else {
            int maxOfs;
            for (maxOfs = hint + 1; ofs < maxOfs && key.compareTo(a[base + hint - ofs]) <= 0; ofs = maxOfs) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) {}
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            final int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }
        ++lastOfs;
        while (lastOfs < ofs) {
            final int m = lastOfs + (ofs - lastOfs >>> 1);
            if (key.compareTo(a[base + m]) > 0) {
                lastOfs = m + 1;
            }
            else {
                ofs = m;
            }
        }
        return ofs;
    }
    
    private static int gallopRight(final Comparable<Object> key, final Object[] a, final int base, final int len, final int hint) {
        int ofs = 1;
        int lastOfs = 0;
        if (key.compareTo(a[base + hint]) < 0) {
            int maxOfs;
            for (maxOfs = hint + 1; ofs < maxOfs && key.compareTo(a[base + hint - ofs]) < 0; ofs = maxOfs) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) {}
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            final int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }
        else {
            int maxOfs;
            for (maxOfs = len - hint; ofs < maxOfs && key.compareTo(a[base + hint + ofs]) >= 0; ofs = maxOfs) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if (ofs <= 0) {}
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            lastOfs += hint;
            ofs += hint;
        }
        ++lastOfs;
        while (lastOfs < ofs) {
            final int m = lastOfs + (ofs - lastOfs >>> 1);
            if (key.compareTo(a[base + m]) < 0) {
                ofs = m;
            }
            else {
                lastOfs = m + 1;
            }
        }
        return ofs;
    }
    
    private void mergeLo(final int base1, int len1, final int base2, int len2) {
        final Object[] a = this.a;
        final Object[] tmp = this.ensureCapacity(len1);
        System.arraycopy(a, base1, tmp, 0, len1);
        int cursor1 = 0;
        int cursor2 = base2;
        int dest = base1;
        a[dest++] = a[cursor2++];
        if (--len2 == 0) {
            System.arraycopy(tmp, cursor1, a, dest, len1);
            return;
        }
        if (len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
            return;
        }
        int minGallop = this.minGallop;
    Label_0440:
        while (true) {
            int count1 = 0;
            int count2 = 0;
            do {
                if (((Comparable)a[cursor2]).compareTo(tmp[cursor1]) < 0) {
                    a[dest++] = a[cursor2++];
                    ++count2;
                    count1 = 0;
                    if (--len2 == 0) {
                        break Label_0440;
                    }
                    continue;
                }
                else {
                    a[dest++] = tmp[cursor1++];
                    ++count1;
                    count2 = 0;
                    if (--len1 == 1) {
                        break Label_0440;
                    }
                    continue;
                }
            } while ((count1 | count2) < minGallop);
            do {
                count1 = gallopRight((Comparable<Object>)a[cursor2], tmp, cursor1, len1, 0);
                if (count1 != 0) {
                    System.arraycopy(tmp, cursor1, a, dest, count1);
                    dest += count1;
                    cursor1 += count1;
                    len1 -= count1;
                    if (len1 <= 1) {
                        break Label_0440;
                    }
                }
                a[dest++] = a[cursor2++];
                if (--len2 == 0) {
                    break Label_0440;
                }
                count2 = gallopLeft((Comparable<Object>)tmp[cursor1], a, cursor2, len2, 0);
                if (count2 != 0) {
                    System.arraycopy(a, cursor2, a, dest, count2);
                    dest += count2;
                    cursor2 += count2;
                    len2 -= count2;
                    if (len2 == 0) {
                        break Label_0440;
                    }
                }
                a[dest++] = tmp[cursor1++];
                if (--len1 == 1) {
                    break Label_0440;
                }
                --minGallop;
            } while (count1 >= 7 | count2 >= 7);
            if (minGallop < 0) {
                minGallop = 0;
            }
            minGallop += 2;
        }
        this.minGallop = ((minGallop < 1) ? 1 : minGallop);
        if (len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
        }
        else {
            if (len1 == 0) {
                throw new IllegalArgumentException("Comparison method violates its general contract!");
            }
            System.arraycopy(tmp, cursor1, a, dest, len1);
        }
    }
    
    private void mergeHi(final int base1, int len1, final int base2, int len2) {
        final Object[] a = this.a;
        final Object[] tmp = this.ensureCapacity(len2);
        System.arraycopy(a, base2, tmp, 0, len2);
        int cursor1 = base1 + len1 - 1;
        int cursor2 = len2 - 1;
        int dest = base2 + len2 - 1;
        a[dest--] = a[cursor1--];
        if (--len1 == 0) {
            System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
            return;
        }
        if (len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
            return;
        }
        int minGallop = this.minGallop;
    Label_0487:
        while (true) {
            int count1 = 0;
            int count2 = 0;
            do {
                if (((Comparable)tmp[cursor2]).compareTo(a[cursor1]) < 0) {
                    a[dest--] = a[cursor1--];
                    ++count1;
                    count2 = 0;
                    if (--len1 == 0) {
                        break Label_0487;
                    }
                    continue;
                }
                else {
                    a[dest--] = tmp[cursor2--];
                    ++count2;
                    count1 = 0;
                    if (--len2 == 1) {
                        break Label_0487;
                    }
                    continue;
                }
            } while ((count1 | count2) < minGallop);
            do {
                count1 = len1 - gallopRight((Comparable<Object>)tmp[cursor2], a, base1, len1, len1 - 1);
                if (count1 != 0) {
                    dest -= count1;
                    cursor1 -= count1;
                    len1 -= count1;
                    System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
                    if (len1 == 0) {
                        break Label_0487;
                    }
                }
                a[dest--] = tmp[cursor2--];
                if (--len2 == 1) {
                    break Label_0487;
                }
                count2 = len2 - gallopLeft((Comparable<Object>)a[cursor1], tmp, 0, len2, len2 - 1);
                if (count2 != 0) {
                    dest -= count2;
                    cursor2 -= count2;
                    len2 -= count2;
                    System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
                    if (len2 <= 1) {
                        break Label_0487;
                    }
                }
                a[dest--] = a[cursor1--];
                if (--len1 == 0) {
                    break Label_0487;
                }
                --minGallop;
            } while (count1 >= 7 | count2 >= 7);
            if (minGallop < 0) {
                minGallop = 0;
            }
            minGallop += 2;
        }
        this.minGallop = ((minGallop < 1) ? 1 : minGallop);
        if (len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
        }
        else {
            if (len2 == 0) {
                throw new IllegalArgumentException("Comparison method violates its general contract!");
            }
            System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
        }
    }
    
    private Object[] ensureCapacity(final int minCapacity) {
        this.tmpCount = Math.max(this.tmpCount, minCapacity);
        if (this.tmp.length < minCapacity) {
            int newSize = minCapacity;
            newSize |= newSize >> 1;
            newSize |= newSize >> 2;
            newSize |= newSize >> 4;
            newSize |= newSize >> 8;
            newSize |= newSize >> 16;
            if (++newSize < 0) {
                newSize = minCapacity;
            }
            else {
                newSize = Math.min(newSize, this.a.length >>> 1);
            }
            final Object[] newArray = new Object[newSize];
            this.tmp = newArray;
        }
        return this.tmp;
    }
    
    private static void rangeCheck(final int arrayLen, final int fromIndex, final int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLen) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }
}

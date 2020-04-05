// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.compression.lzma;

import com.badlogic.gdx.utils.compression.ICodeProgress;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.badlogic.gdx.utils.compression.rangecoder.BitTreeEncoder;
import com.badlogic.gdx.utils.compression.lz.BinTree;

public class Encoder
{
    public static final int EMatchFinderTypeBT2 = 0;
    public static final int EMatchFinderTypeBT4 = 1;
    static final int kIfinityPrice = 268435455;
    static byte[] g_FastPos;
    int _state;
    byte _previousByte;
    int[] _repDistances;
    static final int kDefaultDictionaryLogSize = 22;
    static final int kNumFastBytesDefault = 32;
    public static final int kNumLenSpecSymbols = 16;
    static final int kNumOpts = 4096;
    Optimal[] _optimum;
    BinTree _matchFinder;
    com.badlogic.gdx.utils.compression.rangecoder.Encoder _rangeEncoder;
    short[] _isMatch;
    short[] _isRep;
    short[] _isRepG0;
    short[] _isRepG1;
    short[] _isRepG2;
    short[] _isRep0Long;
    BitTreeEncoder[] _posSlotEncoder;
    short[] _posEncoders;
    BitTreeEncoder _posAlignEncoder;
    LenPriceTableEncoder _lenEncoder;
    LenPriceTableEncoder _repMatchLenEncoder;
    LiteralEncoder _literalEncoder;
    int[] _matchDistances;
    int _numFastBytes;
    int _longestMatchLength;
    int _numDistancePairs;
    int _additionalOffset;
    int _optimumEndIndex;
    int _optimumCurrentIndex;
    boolean _longestMatchWasFound;
    int[] _posSlotPrices;
    int[] _distancesPrices;
    int[] _alignPrices;
    int _alignPriceCount;
    int _distTableSize;
    int _posStateBits;
    int _posStateMask;
    int _numLiteralPosStateBits;
    int _numLiteralContextBits;
    int _dictionarySize;
    int _dictionarySizePrev;
    int _numFastBytesPrev;
    long nowPos64;
    boolean _finished;
    InputStream _inStream;
    int _matchFinderType;
    boolean _writeEndMark;
    boolean _needReleaseMFStream;
    int[] reps;
    int[] repLens;
    int backRes;
    long[] processedInSize;
    long[] processedOutSize;
    boolean[] finished;
    public static final int kPropSize = 5;
    byte[] properties;
    int[] tempPrices;
    int _matchPriceCount;
    
    static {
        Encoder.g_FastPos = new byte[2048];
        final int kFastSlots = 22;
        int c = 2;
        Encoder.g_FastPos[0] = 0;
        Encoder.g_FastPos[1] = 1;
        for (int slotFast = 2; slotFast < kFastSlots; ++slotFast) {
            for (int k = 1 << (slotFast >> 1) - 1, j = 0; j < k; ++j, ++c) {
                Encoder.g_FastPos[c] = (byte)slotFast;
            }
        }
    }
    
    static int GetPosSlot(final int pos) {
        if (pos < 2048) {
            return Encoder.g_FastPos[pos];
        }
        if (pos < 2097152) {
            return Encoder.g_FastPos[pos >> 10] + 20;
        }
        return Encoder.g_FastPos[pos >> 20] + 40;
    }
    
    static int GetPosSlot2(final int pos) {
        if (pos < 131072) {
            return Encoder.g_FastPos[pos >> 6] + 12;
        }
        if (pos < 134217728) {
            return Encoder.g_FastPos[pos >> 16] + 32;
        }
        return Encoder.g_FastPos[pos >> 26] + 52;
    }
    
    void BaseInit() {
        this._state = Base.StateInit();
        this._previousByte = 0;
        for (int i = 0; i < 4; ++i) {
            this._repDistances[i] = 0;
        }
    }
    
    void Create() {
        if (this._matchFinder == null) {
            final BinTree bt = new BinTree();
            int numHashBytes = 4;
            if (this._matchFinderType == 0) {
                numHashBytes = 2;
            }
            bt.SetType(numHashBytes);
            this._matchFinder = bt;
        }
        this._literalEncoder.Create(this._numLiteralPosStateBits, this._numLiteralContextBits);
        if (this._dictionarySize == this._dictionarySizePrev && this._numFastBytesPrev == this._numFastBytes) {
            return;
        }
        this._matchFinder.Create(this._dictionarySize, 4096, this._numFastBytes, 274);
        this._dictionarySizePrev = this._dictionarySize;
        this._numFastBytesPrev = this._numFastBytes;
    }
    
    public Encoder() {
        this._state = Base.StateInit();
        this._repDistances = new int[4];
        this._optimum = new Optimal[4096];
        this._matchFinder = null;
        this._rangeEncoder = new com.badlogic.gdx.utils.compression.rangecoder.Encoder();
        this._isMatch = new short[192];
        this._isRep = new short[12];
        this._isRepG0 = new short[12];
        this._isRepG1 = new short[12];
        this._isRepG2 = new short[12];
        this._isRep0Long = new short[192];
        this._posSlotEncoder = new BitTreeEncoder[4];
        this._posEncoders = new short[114];
        this._posAlignEncoder = new BitTreeEncoder(4);
        this._lenEncoder = new LenPriceTableEncoder();
        this._repMatchLenEncoder = new LenPriceTableEncoder();
        this._literalEncoder = new LiteralEncoder();
        this._matchDistances = new int[548];
        this._numFastBytes = 32;
        this._posSlotPrices = new int[256];
        this._distancesPrices = new int[512];
        this._alignPrices = new int[16];
        this._distTableSize = 44;
        this._posStateBits = 2;
        this._posStateMask = 3;
        this._numLiteralPosStateBits = 0;
        this._numLiteralContextBits = 3;
        this._dictionarySize = 4194304;
        this._dictionarySizePrev = -1;
        this._numFastBytesPrev = -1;
        this._matchFinderType = 1;
        this._writeEndMark = false;
        this._needReleaseMFStream = false;
        this.reps = new int[4];
        this.repLens = new int[4];
        this.processedInSize = new long[1];
        this.processedOutSize = new long[1];
        this.finished = new boolean[1];
        this.properties = new byte[5];
        this.tempPrices = new int[128];
        for (int i = 0; i < 4096; ++i) {
            this._optimum[i] = new Optimal();
        }
        for (int i = 0; i < 4; ++i) {
            this._posSlotEncoder[i] = new BitTreeEncoder(6);
        }
    }
    
    void SetWriteEndMarkerMode(final boolean writeEndMarker) {
        this._writeEndMark = writeEndMarker;
    }
    
    void Init() {
        this.BaseInit();
        this._rangeEncoder.Init();
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isMatch);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep0Long);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG0);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG1);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG2);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._posEncoders);
        this._literalEncoder.Init();
        for (int i = 0; i < 4; ++i) {
            this._posSlotEncoder[i].Init();
        }
        this._lenEncoder.Init(1 << this._posStateBits);
        this._repMatchLenEncoder.Init(1 << this._posStateBits);
        this._posAlignEncoder.Init();
        this._longestMatchWasFound = false;
        this._optimumEndIndex = 0;
        this._optimumCurrentIndex = 0;
        this._additionalOffset = 0;
    }
    
    int ReadMatchDistances() throws IOException {
        int lenRes = 0;
        this._numDistancePairs = this._matchFinder.GetMatches(this._matchDistances);
        if (this._numDistancePairs > 0) {
            lenRes = this._matchDistances[this._numDistancePairs - 2];
            if (lenRes == this._numFastBytes) {
                lenRes += this._matchFinder.GetMatchLen(lenRes - 1, this._matchDistances[this._numDistancePairs - 1], 273 - lenRes);
            }
        }
        ++this._additionalOffset;
        return lenRes;
    }
    
    void MovePos(final int num) throws IOException {
        if (num > 0) {
            this._matchFinder.Skip(num);
            this._additionalOffset += num;
        }
    }
    
    int GetRepLen1Price(final int state, final int posState) {
        return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep0Long[(state << 4) + posState]);
    }
    
    int GetPureRepPrice(final int repIndex, final int state, final int posState) {
        int price;
        if (repIndex == 0) {
            price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]);
            price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep0Long[(state << 4) + posState]);
        }
        else {
            price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG0[state]);
            if (repIndex == 1) {
                price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG1[state]);
            }
            else {
                price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG1[state]);
                price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this._isRepG2[state], repIndex - 2);
            }
        }
        return price;
    }
    
    int GetRepPrice(final int repIndex, final int len, final int state, final int posState) {
        final int price = this._repMatchLenEncoder.GetPrice(len - 2, posState);
        return price + this.GetPureRepPrice(repIndex, state, posState);
    }
    
    int GetPosLenPrice(final int pos, final int len, final int posState) {
        final int lenToPosState = Base.GetLenToPosState(len);
        int price;
        if (pos < 128) {
            price = this._distancesPrices[lenToPosState * 128 + pos];
        }
        else {
            price = this._posSlotPrices[(lenToPosState << 6) + GetPosSlot2(pos)] + this._alignPrices[pos & 0xF];
        }
        return price + this._lenEncoder.GetPrice(len - 2, posState);
    }
    
    int Backward(int cur) {
        this._optimumEndIndex = cur;
        int posMem = this._optimum[cur].PosPrev;
        int backMem = this._optimum[cur].BackPrev;
        do {
            if (this._optimum[cur].Prev1IsChar) {
                this._optimum[posMem].MakeAsChar();
                this._optimum[posMem].PosPrev = posMem - 1;
                if (this._optimum[cur].Prev2) {
                    this._optimum[posMem - 1].Prev1IsChar = false;
                    this._optimum[posMem - 1].PosPrev = this._optimum[cur].PosPrev2;
                    this._optimum[posMem - 1].BackPrev = this._optimum[cur].BackPrev2;
                }
            }
            final int posPrev = posMem;
            final int backCur = backMem;
            backMem = this._optimum[posPrev].BackPrev;
            posMem = this._optimum[posPrev].PosPrev;
            this._optimum[posPrev].BackPrev = backCur;
            this._optimum[posPrev].PosPrev = cur;
            cur = posPrev;
        } while (cur > 0);
        this.backRes = this._optimum[0].BackPrev;
        return this._optimumCurrentIndex = this._optimum[0].PosPrev;
    }
    
    int GetOptimum(int position) throws IOException {
        if (this._optimumEndIndex != this._optimumCurrentIndex) {
            final int lenRes = this._optimum[this._optimumCurrentIndex].PosPrev - this._optimumCurrentIndex;
            this.backRes = this._optimum[this._optimumCurrentIndex].BackPrev;
            this._optimumCurrentIndex = this._optimum[this._optimumCurrentIndex].PosPrev;
            return lenRes;
        }
        final int n = 0;
        this._optimumEndIndex = n;
        this._optimumCurrentIndex = n;
        int lenMain;
        if (!this._longestMatchWasFound) {
            lenMain = this.ReadMatchDistances();
        }
        else {
            lenMain = this._longestMatchLength;
            this._longestMatchWasFound = false;
        }
        int numDistancePairs = this._numDistancePairs;
        int numAvailableBytes = this._matchFinder.GetNumAvailableBytes() + 1;
        if (numAvailableBytes < 2) {
            this.backRes = -1;
            return 1;
        }
        if (numAvailableBytes > 273) {
            numAvailableBytes = 273;
        }
        int repMaxIndex = 0;
        for (int i = 0; i < 4; ++i) {
            this.reps[i] = this._repDistances[i];
            this.repLens[i] = this._matchFinder.GetMatchLen(-1, this.reps[i], 273);
            if (this.repLens[i] > this.repLens[repMaxIndex]) {
                repMaxIndex = i;
            }
        }
        if (this.repLens[repMaxIndex] >= this._numFastBytes) {
            this.backRes = repMaxIndex;
            final int lenRes2 = this.repLens[repMaxIndex];
            this.MovePos(lenRes2 - 1);
            return lenRes2;
        }
        if (lenMain >= this._numFastBytes) {
            this.backRes = this._matchDistances[numDistancePairs - 1] + 4;
            this.MovePos(lenMain - 1);
            return lenMain;
        }
        byte currentByte = this._matchFinder.GetIndexByte(-1);
        byte matchByte = this._matchFinder.GetIndexByte(0 - this._repDistances[0] - 1 - 1);
        if (lenMain < 2 && currentByte != matchByte && this.repLens[repMaxIndex] < 2) {
            this.backRes = -1;
            return 1;
        }
        this._optimum[0].State = this._state;
        int posState = position & this._posStateMask;
        this._optimum[1].Price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(this._state << 4) + posState]) + this._literalEncoder.GetSubCoder(position, this._previousByte).GetPrice(!Base.StateIsCharState(this._state), matchByte, currentByte);
        this._optimum[1].MakeAsChar();
        int matchPrice = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(this._state << 4) + posState]);
        int repMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[this._state]);
        if (matchByte == currentByte) {
            final int shortRepPrice = repMatchPrice + this.GetRepLen1Price(this._state, posState);
            if (shortRepPrice < this._optimum[1].Price) {
                this._optimum[1].Price = shortRepPrice;
                this._optimum[1].MakeAsShortRep();
            }
        }
        int lenEnd = (lenMain >= this.repLens[repMaxIndex]) ? lenMain : this.repLens[repMaxIndex];
        if (lenEnd < 2) {
            this.backRes = this._optimum[1].BackPrev;
            return 1;
        }
        this._optimum[1].PosPrev = 0;
        this._optimum[0].Backs0 = this.reps[0];
        this._optimum[0].Backs1 = this.reps[1];
        this._optimum[0].Backs2 = this.reps[2];
        this._optimum[0].Backs3 = this.reps[3];
        int len = lenEnd;
        do {
            this._optimum[len--].Price = 268435455;
        } while (len >= 2);
        for (int i = 0; i < 4; ++i) {
            int repLen = this.repLens[i];
            if (repLen >= 2) {
                final int price = repMatchPrice + this.GetPureRepPrice(i, this._state, posState);
                do {
                    final int curAndLenPrice = price + this._repMatchLenEncoder.GetPrice(repLen - 2, posState);
                    final Optimal optimum = this._optimum[repLen];
                    if (curAndLenPrice < optimum.Price) {
                        optimum.Price = curAndLenPrice;
                        optimum.PosPrev = 0;
                        optimum.BackPrev = i;
                        optimum.Prev1IsChar = false;
                    }
                } while (--repLen >= 2);
            }
        }
        int normalMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[this._state]);
        len = ((this.repLens[0] >= 2) ? (this.repLens[0] + 1) : 2);
        if (len <= lenMain) {
            int offs;
            for (offs = 0; len > this._matchDistances[offs]; offs += 2) {}
            while (true) {
                final int distance = this._matchDistances[offs + 1];
                final int curAndLenPrice2 = normalMatchPrice + this.GetPosLenPrice(distance, len, posState);
                final Optimal optimum2 = this._optimum[len];
                if (curAndLenPrice2 < optimum2.Price) {
                    optimum2.Price = curAndLenPrice2;
                    optimum2.PosPrev = 0;
                    optimum2.BackPrev = distance + 4;
                    optimum2.Prev1IsChar = false;
                }
                if (len == this._matchDistances[offs]) {
                    offs += 2;
                    if (offs == numDistancePairs) {
                        break;
                    }
                }
                ++len;
            }
        }
        int cur = 0;
        while (++cur != lenEnd) {
            int newLen = this.ReadMatchDistances();
            numDistancePairs = this._numDistancePairs;
            if (newLen >= this._numFastBytes) {
                this._longestMatchLength = newLen;
                this._longestMatchWasFound = true;
                return this.Backward(cur);
            }
            ++position;
            int posPrev = this._optimum[cur].PosPrev;
            int state;
            if (this._optimum[cur].Prev1IsChar) {
                --posPrev;
                if (this._optimum[cur].Prev2) {
                    state = this._optimum[this._optimum[cur].PosPrev2].State;
                    if (this._optimum[cur].BackPrev2 < 4) {
                        state = Base.StateUpdateRep(state);
                    }
                    else {
                        state = Base.StateUpdateMatch(state);
                    }
                }
                else {
                    state = this._optimum[posPrev].State;
                }
                state = Base.StateUpdateChar(state);
            }
            else {
                state = this._optimum[posPrev].State;
            }
            if (posPrev == cur - 1) {
                if (this._optimum[cur].IsShortRep()) {
                    state = Base.StateUpdateShortRep(state);
                }
                else {
                    state = Base.StateUpdateChar(state);
                }
            }
            else {
                int pos;
                if (this._optimum[cur].Prev1IsChar && this._optimum[cur].Prev2) {
                    posPrev = this._optimum[cur].PosPrev2;
                    pos = this._optimum[cur].BackPrev2;
                    state = Base.StateUpdateRep(state);
                }
                else {
                    pos = this._optimum[cur].BackPrev;
                    if (pos < 4) {
                        state = Base.StateUpdateRep(state);
                    }
                    else {
                        state = Base.StateUpdateMatch(state);
                    }
                }
                final Optimal opt = this._optimum[posPrev];
                if (pos < 4) {
                    if (pos == 0) {
                        this.reps[0] = opt.Backs0;
                        this.reps[1] = opt.Backs1;
                        this.reps[2] = opt.Backs2;
                        this.reps[3] = opt.Backs3;
                    }
                    else if (pos == 1) {
                        this.reps[0] = opt.Backs1;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs2;
                        this.reps[3] = opt.Backs3;
                    }
                    else if (pos == 2) {
                        this.reps[0] = opt.Backs2;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs1;
                        this.reps[3] = opt.Backs3;
                    }
                    else {
                        this.reps[0] = opt.Backs3;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs1;
                        this.reps[3] = opt.Backs2;
                    }
                }
                else {
                    this.reps[0] = pos - 4;
                    this.reps[1] = opt.Backs0;
                    this.reps[2] = opt.Backs1;
                    this.reps[3] = opt.Backs2;
                }
            }
            this._optimum[cur].State = state;
            this._optimum[cur].Backs0 = this.reps[0];
            this._optimum[cur].Backs1 = this.reps[1];
            this._optimum[cur].Backs2 = this.reps[2];
            this._optimum[cur].Backs3 = this.reps[3];
            final int curPrice = this._optimum[cur].Price;
            currentByte = this._matchFinder.GetIndexByte(-1);
            matchByte = this._matchFinder.GetIndexByte(0 - this.reps[0] - 1 - 1);
            posState = (position & this._posStateMask);
            final int curAnd1Price = curPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state << 4) + posState]) + this._literalEncoder.GetSubCoder(position, this._matchFinder.GetIndexByte(-2)).GetPrice(!Base.StateIsCharState(state), matchByte, currentByte);
            final Optimal nextOptimum = this._optimum[cur + 1];
            boolean nextIsChar = false;
            if (curAnd1Price < nextOptimum.Price) {
                nextOptimum.Price = curAnd1Price;
                nextOptimum.PosPrev = cur;
                nextOptimum.MakeAsChar();
                nextIsChar = true;
            }
            matchPrice = curPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state << 4) + posState]);
            repMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state]);
            if (matchByte == currentByte && (nextOptimum.PosPrev >= cur || nextOptimum.BackPrev != 0)) {
                final int shortRepPrice2 = repMatchPrice + this.GetRepLen1Price(state, posState);
                if (shortRepPrice2 <= nextOptimum.Price) {
                    nextOptimum.Price = shortRepPrice2;
                    nextOptimum.PosPrev = cur;
                    nextOptimum.MakeAsShortRep();
                    nextIsChar = true;
                }
            }
            int numAvailableBytesFull = this._matchFinder.GetNumAvailableBytes() + 1;
            numAvailableBytesFull = (numAvailableBytes = Math.min(4095 - cur, numAvailableBytesFull));
            if (numAvailableBytes < 2) {
                continue;
            }
            if (numAvailableBytes > this._numFastBytes) {
                numAvailableBytes = this._numFastBytes;
            }
            if (!nextIsChar && matchByte != currentByte) {
                final int t = Math.min(numAvailableBytesFull - 1, this._numFastBytes);
                final int lenTest2 = this._matchFinder.GetMatchLen(0, this.reps[0], t);
                if (lenTest2 >= 2) {
                    final int state2 = Base.StateUpdateChar(state);
                    final int posStateNext = position + 1 & this._posStateMask;
                    final int nextRepMatchPrice = curAnd1Price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posStateNext]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state2]);
                    int offset;
                    for (offset = cur + 1 + lenTest2; lenEnd < offset; this._optimum[++lenEnd].Price = 268435455) {}
                    final int curAndLenPrice3 = nextRepMatchPrice + this.GetRepPrice(0, lenTest2, state2, posStateNext);
                    final Optimal optimum3 = this._optimum[offset];
                    if (curAndLenPrice3 < optimum3.Price) {
                        optimum3.Price = curAndLenPrice3;
                        optimum3.PosPrev = cur + 1;
                        optimum3.BackPrev = 0;
                        optimum3.Prev1IsChar = true;
                        optimum3.Prev2 = false;
                    }
                }
            }
            int startLen = 2;
            for (int repIndex = 0; repIndex < 4; ++repIndex) {
                int lenTest3 = this._matchFinder.GetMatchLen(-1, this.reps[repIndex], numAvailableBytes);
                if (lenTest3 >= 2) {
                    final int lenTestTemp = lenTest3;
                    while (true) {
                        if (lenEnd >= cur + lenTest3) {
                            final int curAndLenPrice4 = repMatchPrice + this.GetRepPrice(repIndex, lenTest3, state, posState);
                            final Optimal optimum4 = this._optimum[cur + lenTest3];
                            if (curAndLenPrice4 < optimum4.Price) {
                                optimum4.Price = curAndLenPrice4;
                                optimum4.PosPrev = cur;
                                optimum4.BackPrev = repIndex;
                                optimum4.Prev1IsChar = false;
                            }
                            if (--lenTest3 < 2) {
                                break;
                            }
                            continue;
                        }
                        else {
                            this._optimum[++lenEnd].Price = 268435455;
                        }
                    }
                    lenTest3 = lenTestTemp;
                    if (repIndex == 0) {
                        startLen = lenTest3 + 1;
                    }
                    if (lenTest3 < numAvailableBytesFull) {
                        final int t2 = Math.min(numAvailableBytesFull - 1 - lenTest3, this._numFastBytes);
                        final int lenTest4 = this._matchFinder.GetMatchLen(lenTest3, this.reps[repIndex], t2);
                        if (lenTest4 >= 2) {
                            int state3 = Base.StateUpdateRep(state);
                            int posStateNext2 = position + lenTest3 & this._posStateMask;
                            final int curAndLenCharPrice = repMatchPrice + this.GetRepPrice(repIndex, lenTest3, state, posState) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state3 << 4) + posStateNext2]) + this._literalEncoder.GetSubCoder(position + lenTest3, this._matchFinder.GetIndexByte(lenTest3 - 1 - 1)).GetPrice(true, this._matchFinder.GetIndexByte(lenTest3 - 1 - (this.reps[repIndex] + 1)), this._matchFinder.GetIndexByte(lenTest3 - 1));
                            state3 = Base.StateUpdateChar(state3);
                            posStateNext2 = (position + lenTest3 + 1 & this._posStateMask);
                            final int nextMatchPrice = curAndLenCharPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state3 << 4) + posStateNext2]);
                            final int nextRepMatchPrice2 = nextMatchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state3]);
                            int offset2;
                            for (offset2 = lenTest3 + 1 + lenTest4; lenEnd < cur + offset2; this._optimum[++lenEnd].Price = 268435455) {}
                            final int curAndLenPrice5 = nextRepMatchPrice2 + this.GetRepPrice(0, lenTest4, state3, posStateNext2);
                            final Optimal optimum5 = this._optimum[cur + offset2];
                            if (curAndLenPrice5 < optimum5.Price) {
                                optimum5.Price = curAndLenPrice5;
                                optimum5.PosPrev = cur + lenTest3 + 1;
                                optimum5.BackPrev = 0;
                                optimum5.Prev1IsChar = true;
                                optimum5.Prev2 = true;
                                optimum5.PosPrev2 = cur;
                                optimum5.BackPrev2 = repIndex;
                            }
                        }
                    }
                }
            }
            if (newLen > numAvailableBytes) {
                for (newLen = numAvailableBytes, numDistancePairs = 0; newLen > this._matchDistances[numDistancePairs]; numDistancePairs += 2) {}
                this._matchDistances[numDistancePairs] = newLen;
                numDistancePairs += 2;
            }
            if (newLen < startLen) {
                continue;
            }
            normalMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[state]);
            while (lenEnd < cur + newLen) {
                this._optimum[++lenEnd].Price = 268435455;
            }
            int offs2;
            for (offs2 = 0; startLen > this._matchDistances[offs2]; offs2 += 2) {}
            int lenTest3 = startLen;
            while (true) {
                final int curBack = this._matchDistances[offs2 + 1];
                int curAndLenPrice4 = normalMatchPrice + this.GetPosLenPrice(curBack, lenTest3, posState);
                Optimal optimum4 = this._optimum[cur + lenTest3];
                if (curAndLenPrice4 < optimum4.Price) {
                    optimum4.Price = curAndLenPrice4;
                    optimum4.PosPrev = cur;
                    optimum4.BackPrev = curBack + 4;
                    optimum4.Prev1IsChar = false;
                }
                if (lenTest3 == this._matchDistances[offs2]) {
                    if (lenTest3 < numAvailableBytesFull) {
                        final int t3 = Math.min(numAvailableBytesFull - 1 - lenTest3, this._numFastBytes);
                        final int lenTest5 = this._matchFinder.GetMatchLen(lenTest3, curBack, t3);
                        if (lenTest5 >= 2) {
                            int state4 = Base.StateUpdateMatch(state);
                            int posStateNext3 = position + lenTest3 & this._posStateMask;
                            final int curAndLenCharPrice2 = curAndLenPrice4 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state4 << 4) + posStateNext3]) + this._literalEncoder.GetSubCoder(position + lenTest3, this._matchFinder.GetIndexByte(lenTest3 - 1 - 1)).GetPrice(true, this._matchFinder.GetIndexByte(lenTest3 - (curBack + 1) - 1), this._matchFinder.GetIndexByte(lenTest3 - 1));
                            state4 = Base.StateUpdateChar(state4);
                            posStateNext3 = (position + lenTest3 + 1 & this._posStateMask);
                            final int nextMatchPrice2 = curAndLenCharPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state4 << 4) + posStateNext3]);
                            final int nextRepMatchPrice3 = nextMatchPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state4]);
                            int offset3;
                            for (offset3 = lenTest3 + 1 + lenTest5; lenEnd < cur + offset3; this._optimum[++lenEnd].Price = 268435455) {}
                            curAndLenPrice4 = nextRepMatchPrice3 + this.GetRepPrice(0, lenTest5, state4, posStateNext3);
                            optimum4 = this._optimum[cur + offset3];
                            if (curAndLenPrice4 < optimum4.Price) {
                                optimum4.Price = curAndLenPrice4;
                                optimum4.PosPrev = cur + lenTest3 + 1;
                                optimum4.BackPrev = 0;
                                optimum4.Prev1IsChar = true;
                                optimum4.Prev2 = true;
                                optimum4.PosPrev2 = cur;
                                optimum4.BackPrev2 = curBack + 4;
                            }
                        }
                    }
                    offs2 += 2;
                    if (offs2 == numDistancePairs) {
                        break;
                    }
                }
                ++lenTest3;
            }
        }
        return this.Backward(cur);
    }
    
    boolean ChangePair(final int smallDist, final int bigDist) {
        final int kDif = 7;
        return smallDist < 1 << 32 - kDif && bigDist >= smallDist << kDif;
    }
    
    void WriteEndMarker(final int posState) throws IOException {
        if (!this._writeEndMark) {
            return;
        }
        this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, 1);
        this._rangeEncoder.Encode(this._isRep, this._state, 0);
        this._state = Base.StateUpdateMatch(this._state);
        final int len = 2;
        this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
        final int posSlot = 63;
        final int lenToPosState = Base.GetLenToPosState(len);
        this._posSlotEncoder[lenToPosState].Encode(this._rangeEncoder, posSlot);
        final int footerBits = 30;
        final int posReduced = (1 << footerBits) - 1;
        this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
        this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 0xF);
    }
    
    void Flush(final int nowPos) throws IOException {
        this.ReleaseMFStream();
        this.WriteEndMarker(nowPos & this._posStateMask);
        this._rangeEncoder.FlushData();
        this._rangeEncoder.FlushStream();
    }
    
    public void CodeOneBlock(final long[] inSize, final long[] outSize, final boolean[] finished) throws IOException {
        outSize[0] = (inSize[0] = 0L);
        finished[0] = true;
        if (this._inStream != null) {
            this._matchFinder.SetStream(this._inStream);
            this._matchFinder.Init();
            this._needReleaseMFStream = true;
            this._inStream = null;
        }
        if (this._finished) {
            return;
        }
        this._finished = true;
        final long progressPosValuePrev = this.nowPos64;
        if (this.nowPos64 == 0L) {
            if (this._matchFinder.GetNumAvailableBytes() == 0) {
                this.Flush((int)this.nowPos64);
                return;
            }
            this.ReadMatchDistances();
            final int posState = (int)this.nowPos64 & this._posStateMask;
            this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, 0);
            this._state = Base.StateUpdateChar(this._state);
            final byte curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
            this._literalEncoder.GetSubCoder((int)this.nowPos64, this._previousByte).Encode(this._rangeEncoder, curByte);
            this._previousByte = curByte;
            --this._additionalOffset;
            ++this.nowPos64;
        }
        if (this._matchFinder.GetNumAvailableBytes() == 0) {
            this.Flush((int)this.nowPos64);
            return;
        }
        while (true) {
            final int len = this.GetOptimum((int)this.nowPos64);
            int pos = this.backRes;
            final int posState2 = (int)this.nowPos64 & this._posStateMask;
            final int complexState = (this._state << 4) + posState2;
            if (len == 1 && pos == -1) {
                this._rangeEncoder.Encode(this._isMatch, complexState, 0);
                final byte curByte2 = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                final LiteralEncoder.Encoder2 subCoder = this._literalEncoder.GetSubCoder((int)this.nowPos64, this._previousByte);
                if (!Base.StateIsCharState(this._state)) {
                    final byte matchByte = this._matchFinder.GetIndexByte(0 - this._repDistances[0] - 1 - this._additionalOffset);
                    subCoder.EncodeMatched(this._rangeEncoder, matchByte, curByte2);
                }
                else {
                    subCoder.Encode(this._rangeEncoder, curByte2);
                }
                this._previousByte = curByte2;
                this._state = Base.StateUpdateChar(this._state);
            }
            else {
                this._rangeEncoder.Encode(this._isMatch, complexState, 1);
                if (pos < 4) {
                    this._rangeEncoder.Encode(this._isRep, this._state, 1);
                    if (pos == 0) {
                        this._rangeEncoder.Encode(this._isRepG0, this._state, 0);
                        if (len == 1) {
                            this._rangeEncoder.Encode(this._isRep0Long, complexState, 0);
                        }
                        else {
                            this._rangeEncoder.Encode(this._isRep0Long, complexState, 1);
                        }
                    }
                    else {
                        this._rangeEncoder.Encode(this._isRepG0, this._state, 1);
                        if (pos == 1) {
                            this._rangeEncoder.Encode(this._isRepG1, this._state, 0);
                        }
                        else {
                            this._rangeEncoder.Encode(this._isRepG1, this._state, 1);
                            this._rangeEncoder.Encode(this._isRepG2, this._state, pos - 2);
                        }
                    }
                    if (len == 1) {
                        this._state = Base.StateUpdateShortRep(this._state);
                    }
                    else {
                        this._repMatchLenEncoder.Encode(this._rangeEncoder, len - 2, posState2);
                        this._state = Base.StateUpdateRep(this._state);
                    }
                    final int distance = this._repDistances[pos];
                    if (pos != 0) {
                        for (int i = pos; i >= 1; --i) {
                            this._repDistances[i] = this._repDistances[i - 1];
                        }
                        this._repDistances[0] = distance;
                    }
                }
                else {
                    this._rangeEncoder.Encode(this._isRep, this._state, 0);
                    this._state = Base.StateUpdateMatch(this._state);
                    this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState2);
                    pos -= 4;
                    final int posSlot = GetPosSlot(pos);
                    final int lenToPosState = Base.GetLenToPosState(len);
                    this._posSlotEncoder[lenToPosState].Encode(this._rangeEncoder, posSlot);
                    if (posSlot >= 4) {
                        final int footerBits = (posSlot >> 1) - 1;
                        final int baseVal = (0x2 | (posSlot & 0x1)) << footerBits;
                        final int posReduced = pos - baseVal;
                        if (posSlot < 14) {
                            BitTreeEncoder.ReverseEncode(this._posEncoders, baseVal - posSlot - 1, this._rangeEncoder, footerBits, posReduced);
                        }
                        else {
                            this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
                            this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 0xF);
                            ++this._alignPriceCount;
                        }
                    }
                    final int distance2 = pos;
                    for (int j = 3; j >= 1; --j) {
                        this._repDistances[j] = this._repDistances[j - 1];
                    }
                    this._repDistances[0] = distance2;
                    ++this._matchPriceCount;
                }
                this._previousByte = this._matchFinder.GetIndexByte(len - 1 - this._additionalOffset);
            }
            this._additionalOffset -= len;
            this.nowPos64 += len;
            if (this._additionalOffset == 0) {
                if (this._matchPriceCount >= 128) {
                    this.FillDistancesPrices();
                }
                if (this._alignPriceCount >= 16) {
                    this.FillAlignPrices();
                }
                inSize[0] = this.nowPos64;
                outSize[0] = this._rangeEncoder.GetProcessedSizeAdd();
                if (this._matchFinder.GetNumAvailableBytes() == 0) {
                    this.Flush((int)this.nowPos64);
                    return;
                }
                if (this.nowPos64 - progressPosValuePrev >= 4096L) {
                    finished[0] = (this._finished = false);
                    return;
                }
                continue;
            }
        }
    }
    
    void ReleaseMFStream() {
        if (this._matchFinder != null && this._needReleaseMFStream) {
            this._matchFinder.ReleaseStream();
            this._needReleaseMFStream = false;
        }
    }
    
    void SetOutStream(final OutputStream outStream) {
        this._rangeEncoder.SetStream(outStream);
    }
    
    void ReleaseOutStream() {
        this._rangeEncoder.ReleaseStream();
    }
    
    void ReleaseStreams() {
        this.ReleaseMFStream();
        this.ReleaseOutStream();
    }
    
    void SetStreams(final InputStream inStream, final OutputStream outStream, final long inSize, final long outSize) {
        this._inStream = inStream;
        this._finished = false;
        this.Create();
        this.SetOutStream(outStream);
        this.Init();
        this.FillDistancesPrices();
        this.FillAlignPrices();
        this._lenEncoder.SetTableSize(this._numFastBytes + 1 - 2);
        this._lenEncoder.UpdateTables(1 << this._posStateBits);
        this._repMatchLenEncoder.SetTableSize(this._numFastBytes + 1 - 2);
        this._repMatchLenEncoder.UpdateTables(1 << this._posStateBits);
        this.nowPos64 = 0L;
    }
    
    public void Code(final InputStream inStream, final OutputStream outStream, final long inSize, final long outSize, final ICodeProgress progress) throws IOException {
        this._needReleaseMFStream = false;
        try {
            this.SetStreams(inStream, outStream, inSize, outSize);
            while (true) {
                this.CodeOneBlock(this.processedInSize, this.processedOutSize, this.finished);
                if (this.finished[0]) {
                    break;
                }
                if (progress == null) {
                    continue;
                }
                progress.SetProgress(this.processedInSize[0], this.processedOutSize[0]);
            }
        }
        finally {
            this.ReleaseStreams();
        }
    }
    
    public void WriteCoderProperties(final OutputStream outStream) throws IOException {
        this.properties[0] = (byte)((this._posStateBits * 5 + this._numLiteralPosStateBits) * 9 + this._numLiteralContextBits);
        for (int i = 0; i < 4; ++i) {
            this.properties[1 + i] = (byte)(this._dictionarySize >> 8 * i);
        }
        outStream.write(this.properties, 0, 5);
    }
    
    void FillDistancesPrices() {
        for (int i = 4; i < 128; ++i) {
            final int posSlot = GetPosSlot(i);
            final int footerBits = (posSlot >> 1) - 1;
            final int baseVal = (0x2 | (posSlot & 0x1)) << footerBits;
            this.tempPrices[i] = BitTreeEncoder.ReverseGetPrice(this._posEncoders, baseVal - posSlot - 1, footerBits, i - baseVal);
        }
        for (int lenToPosState = 0; lenToPosState < 4; ++lenToPosState) {
            final BitTreeEncoder encoder = this._posSlotEncoder[lenToPosState];
            final int st = lenToPosState << 6;
            for (int posSlot = 0; posSlot < this._distTableSize; ++posSlot) {
                this._posSlotPrices[st + posSlot] = encoder.GetPrice(posSlot);
            }
            for (int posSlot = 14; posSlot < this._distTableSize; ++posSlot) {
                final int[] posSlotPrices = this._posSlotPrices;
                final int n = st + posSlot;
                posSlotPrices[n] += (posSlot >> 1) - 1 - 4 << 6;
            }
            final int st2 = lenToPosState * 128;
            int j;
            for (j = 0; j < 4; ++j) {
                this._distancesPrices[st2 + j] = this._posSlotPrices[st + j];
            }
            while (j < 128) {
                this._distancesPrices[st2 + j] = this._posSlotPrices[st + GetPosSlot(j)] + this.tempPrices[j];
                ++j;
            }
        }
        this._matchPriceCount = 0;
    }
    
    void FillAlignPrices() {
        for (int i = 0; i < 16; ++i) {
            this._alignPrices[i] = this._posAlignEncoder.ReverseGetPrice(i);
        }
        this._alignPriceCount = 0;
    }
    
    public boolean SetAlgorithm(final int algorithm) {
        return true;
    }
    
    public boolean SetDictionarySize(final int dictionarySize) {
        final int kDicLogSizeMaxCompress = 29;
        if (dictionarySize < 1 || dictionarySize > 1 << kDicLogSizeMaxCompress) {
            return false;
        }
        this._dictionarySize = dictionarySize;
        int dicLogSize;
        for (dicLogSize = 0; dictionarySize > 1 << dicLogSize; ++dicLogSize) {}
        this._distTableSize = dicLogSize * 2;
        return true;
    }
    
    public boolean SetNumFastBytes(final int numFastBytes) {
        if (numFastBytes < 5 || numFastBytes > 273) {
            return false;
        }
        this._numFastBytes = numFastBytes;
        return true;
    }
    
    public boolean SetMatchFinder(final int matchFinderIndex) {
        if (matchFinderIndex < 0 || matchFinderIndex > 2) {
            return false;
        }
        final int matchFinderIndexPrev = this._matchFinderType;
        this._matchFinderType = matchFinderIndex;
        if (this._matchFinder != null && matchFinderIndexPrev != this._matchFinderType) {
            this._dictionarySizePrev = -1;
            this._matchFinder = null;
        }
        return true;
    }
    
    public boolean SetLcLpPb(final int lc, final int lp, final int pb) {
        if (lp < 0 || lp > 4 || lc < 0 || lc > 8 || pb < 0 || pb > 4) {
            return false;
        }
        this._numLiteralPosStateBits = lp;
        this._numLiteralContextBits = lc;
        this._posStateBits = pb;
        this._posStateMask = (1 << this._posStateBits) - 1;
        return true;
    }
    
    public void SetEndMarkerMode(final boolean endMarkerMode) {
        this._writeEndMark = endMarkerMode;
    }
    
    class LiteralEncoder
    {
        Encoder2[] m_Coders;
        int m_NumPrevBits;
        int m_NumPosBits;
        int m_PosMask;
        
        public void Create(final int numPosBits, final int numPrevBits) {
            if (this.m_Coders != null && this.m_NumPrevBits == numPrevBits && this.m_NumPosBits == numPosBits) {
                return;
            }
            this.m_NumPosBits = numPosBits;
            this.m_PosMask = (1 << numPosBits) - 1;
            this.m_NumPrevBits = numPrevBits;
            final int numStates = 1 << this.m_NumPrevBits + this.m_NumPosBits;
            this.m_Coders = new Encoder2[numStates];
            for (int i = 0; i < numStates; ++i) {
                this.m_Coders[i] = new Encoder2();
            }
        }
        
        public void Init() {
            for (int numStates = 1 << this.m_NumPrevBits + this.m_NumPosBits, i = 0; i < numStates; ++i) {
                this.m_Coders[i].Init();
            }
        }
        
        public Encoder2 GetSubCoder(final int pos, final byte prevByte) {
            return this.m_Coders[((pos & this.m_PosMask) << this.m_NumPrevBits) + ((prevByte & 0xFF) >>> 8 - this.m_NumPrevBits)];
        }
        
        class Encoder2
        {
            short[] m_Encoders;
            
            Encoder2() {
                this.m_Encoders = new short[768];
            }
            
            public void Init() {
                com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this.m_Encoders);
            }
            
            public void Encode(final com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, final byte symbol) throws IOException {
                int context = 1;
                for (int i = 7; i >= 0; --i) {
                    final int bit = symbol >> i & 0x1;
                    rangeEncoder.Encode(this.m_Encoders, context, bit);
                    context = (context << 1 | bit);
                }
            }
            
            public void EncodeMatched(final com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, final byte matchByte, final byte symbol) throws IOException {
                int context = 1;
                boolean same = true;
                for (int i = 7; i >= 0; --i) {
                    final int bit = symbol >> i & 0x1;
                    int state = context;
                    if (same) {
                        final int matchBit = matchByte >> i & 0x1;
                        state += 1 + matchBit << 8;
                        same = (matchBit == bit);
                    }
                    rangeEncoder.Encode(this.m_Encoders, state, bit);
                    context = (context << 1 | bit);
                }
            }
            
            public int GetPrice(final boolean matchMode, final byte matchByte, final byte symbol) {
                int price = 0;
                int context = 1;
                int i = 7;
                if (matchMode) {
                    while (i >= 0) {
                        final int matchBit = matchByte >> i & 0x1;
                        final int bit = symbol >> i & 0x1;
                        price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[(1 + matchBit << 8) + context], bit);
                        context = (context << 1 | bit);
                        if (matchBit != bit) {
                            --i;
                            break;
                        }
                        --i;
                    }
                }
                while (i >= 0) {
                    final int bit2 = symbol >> i & 0x1;
                    price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[context], bit2);
                    context = (context << 1 | bit2);
                    --i;
                }
                return price;
            }
        }
    }
    
    class LenEncoder
    {
        short[] _choice;
        BitTreeEncoder[] _lowCoder;
        BitTreeEncoder[] _midCoder;
        BitTreeEncoder _highCoder;
        
        public LenEncoder() {
            this._choice = new short[2];
            this._lowCoder = new BitTreeEncoder[16];
            this._midCoder = new BitTreeEncoder[16];
            this._highCoder = new BitTreeEncoder(8);
            for (int posState = 0; posState < 16; ++posState) {
                this._lowCoder[posState] = new BitTreeEncoder(3);
                this._midCoder[posState] = new BitTreeEncoder(3);
            }
        }
        
        public void Init(final int numPosStates) {
            com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._choice);
            for (int posState = 0; posState < numPosStates; ++posState) {
                this._lowCoder[posState].Init();
                this._midCoder[posState].Init();
            }
            this._highCoder.Init();
        }
        
        public void Encode(final com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, final int posState) throws IOException {
            if (symbol < 8) {
                rangeEncoder.Encode(this._choice, 0, 0);
                this._lowCoder[posState].Encode(rangeEncoder, symbol);
            }
            else {
                symbol -= 8;
                rangeEncoder.Encode(this._choice, 0, 1);
                if (symbol < 8) {
                    rangeEncoder.Encode(this._choice, 1, 0);
                    this._midCoder[posState].Encode(rangeEncoder, symbol);
                }
                else {
                    rangeEncoder.Encode(this._choice, 1, 1);
                    this._highCoder.Encode(rangeEncoder, symbol - 8);
                }
            }
        }
        
        public void SetPrices(final int posState, final int numSymbols, final int[] prices, final int st) {
            final int a0 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[0]);
            final int a2 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[0]);
            final int b0 = a2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[1]);
            final int b2 = a2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[1]);
            int i;
            for (i = 0, i = 0; i < 8; ++i) {
                if (i >= numSymbols) {
                    return;
                }
                prices[st + i] = a0 + this._lowCoder[posState].GetPrice(i);
            }
            while (i < 16) {
                if (i >= numSymbols) {
                    return;
                }
                prices[st + i] = b0 + this._midCoder[posState].GetPrice(i - 8);
                ++i;
            }
            while (i < numSymbols) {
                prices[st + i] = b2 + this._highCoder.GetPrice(i - 8 - 8);
                ++i;
            }
        }
    }
    
    class LenPriceTableEncoder extends LenEncoder
    {
        int[] _prices;
        int _tableSize;
        int[] _counters;
        
        LenPriceTableEncoder() {
            this._prices = new int[4352];
            this._counters = new int[16];
        }
        
        public void SetTableSize(final int tableSize) {
            this._tableSize = tableSize;
        }
        
        public int GetPrice(final int symbol, final int posState) {
            return this._prices[posState * 272 + symbol];
        }
        
        void UpdateTable(final int posState) {
            this.SetPrices(posState, this._tableSize, this._prices, posState * 272);
            this._counters[posState] = this._tableSize;
        }
        
        public void UpdateTables(final int numPosStates) {
            for (int posState = 0; posState < numPosStates; ++posState) {
                this.UpdateTable(posState);
            }
        }
        
        @Override
        public void Encode(final com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, final int symbol, final int posState) throws IOException {
            super.Encode(rangeEncoder, symbol, posState);
            final int[] counters = this._counters;
            final int n = counters[posState] - 1;
            counters[posState] = n;
            if (n == 0) {
                this.UpdateTable(posState);
            }
        }
    }
    
    class Optimal
    {
        public int State;
        public boolean Prev1IsChar;
        public boolean Prev2;
        public int PosPrev2;
        public int BackPrev2;
        public int Price;
        public int PosPrev;
        public int BackPrev;
        public int Backs0;
        public int Backs1;
        public int Backs2;
        public int Backs3;
        
        public void MakeAsChar() {
            this.BackPrev = -1;
            this.Prev1IsChar = false;
        }
        
        public void MakeAsShortRep() {
            this.BackPrev = 0;
            this.Prev1IsChar = false;
        }
        
        public boolean IsShortRep() {
            return this.BackPrev == 0;
        }
    }
}

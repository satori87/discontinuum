// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.compression;

import com.badlogic.gdx.utils.compression.lzma.Decoder;
import java.io.IOException;
import com.badlogic.gdx.utils.compression.lzma.Encoder;
import java.io.OutputStream;
import java.io.InputStream;

public class Lzma
{
    public static void compress(final InputStream in, final OutputStream out) throws IOException {
        final CommandLine params = new CommandLine();
        boolean eos = false;
        if (params.Eos) {
            eos = true;
        }
        final Encoder encoder = new Encoder();
        if (!encoder.SetAlgorithm(params.Algorithm)) {
            throw new RuntimeException("Incorrect compression mode");
        }
        if (!encoder.SetDictionarySize(params.DictionarySize)) {
            throw new RuntimeException("Incorrect dictionary size");
        }
        if (!encoder.SetNumFastBytes(params.Fb)) {
            throw new RuntimeException("Incorrect -fb value");
        }
        if (!encoder.SetMatchFinder(params.MatchFinder)) {
            throw new RuntimeException("Incorrect -mf value");
        }
        if (!encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb)) {
            throw new RuntimeException("Incorrect -lc or -lp or -pb value");
        }
        encoder.SetEndMarkerMode(eos);
        encoder.WriteCoderProperties(out);
        long fileSize;
        if (eos) {
            fileSize = -1L;
        }
        else if ((fileSize = in.available()) == 0L) {
            fileSize = -1L;
        }
        for (int i = 0; i < 8; ++i) {
            out.write((int)(fileSize >>> 8 * i) & 0xFF);
        }
        encoder.Code(in, out, -1L, -1L, null);
    }
    
    public static void decompress(final InputStream in, final OutputStream out) throws IOException {
        final int propertiesSize = 5;
        final byte[] properties = new byte[propertiesSize];
        if (in.read(properties, 0, propertiesSize) != propertiesSize) {
            throw new RuntimeException("input .lzma file is too short");
        }
        final Decoder decoder = new Decoder();
        if (!decoder.SetDecoderProperties(properties)) {
            throw new RuntimeException("Incorrect stream properties");
        }
        long outSize = 0L;
        for (int i = 0; i < 8; ++i) {
            final int v = in.read();
            if (v < 0) {
                throw new RuntimeException("Can't read stream size");
            }
            outSize |= (long)v << 8 * i;
        }
        if (!decoder.Code(in, out, outSize)) {
            throw new RuntimeException("Error in data stream");
        }
    }
    
    static class CommandLine
    {
        public static final int kEncode = 0;
        public static final int kDecode = 1;
        public static final int kBenchmak = 2;
        public int Command;
        public int NumBenchmarkPasses;
        public int DictionarySize;
        public boolean DictionarySizeIsDefined;
        public int Lc;
        public int Lp;
        public int Pb;
        public int Fb;
        public boolean FbIsDefined;
        public boolean Eos;
        public int Algorithm;
        public int MatchFinder;
        public String InFile;
        public String OutFile;
        
        CommandLine() {
            this.Command = -1;
            this.NumBenchmarkPasses = 10;
            this.DictionarySize = 8388608;
            this.DictionarySizeIsDefined = false;
            this.Lc = 3;
            this.Lp = 0;
            this.Pb = 2;
            this.Fb = 128;
            this.FbIsDefined = false;
            this.Eos = false;
            this.Algorithm = 2;
            this.MatchFinder = 1;
        }
    }
}

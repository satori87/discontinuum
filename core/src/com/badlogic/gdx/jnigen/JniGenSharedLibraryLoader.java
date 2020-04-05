// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.util.zip.CRC32;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipFile;
import java.util.Set;

public class JniGenSharedLibraryLoader
{
    private static Set<String> loadedLibraries;
    private String nativesJar;
    private SharedLibraryFinder libraryFinder;
    private ZipFile nativesZip;
    
    static {
        JniGenSharedLibraryLoader.loadedLibraries = new HashSet<String>();
    }
    
    public JniGenSharedLibraryLoader() {
        this.nativesZip = null;
    }
    
    public JniGenSharedLibraryLoader(final String nativesJar) {
        this.nativesZip = null;
        this.nativesJar = nativesJar;
    }
    
    public JniGenSharedLibraryLoader(final String nativesJar, final SharedLibraryFinder libraryFinder) {
        this.nativesZip = null;
        this.nativesJar = nativesJar;
        this.libraryFinder = libraryFinder;
        if (nativesJar != null) {
            try {
                this.nativesZip = new ZipFile(nativesJar);
            }
            catch (IOException e) {
                this.nativesZip = null;
            }
        }
    }
    
    public void setSharedLibraryFinder(final SharedLibraryFinder libraryFinder) {
        this.libraryFinder = libraryFinder;
        if (this.nativesJar != null) {
            try {
                this.nativesZip = new ZipFile(this.nativesJar);
            }
            catch (IOException e) {
                this.nativesZip = null;
            }
        }
    }
    
    public String crc(final InputStream input) {
        if (input == null) {
            return new StringBuilder().append(System.nanoTime()).toString();
        }
        final CRC32 crc = new CRC32();
        final byte[] buffer = new byte[4096];
        try {
            while (true) {
                final int length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                crc.update(buffer, 0, length);
            }
        }
        catch (Exception ex) {
            try {
                input.close();
            }
            catch (Exception ex2) {}
        }
        return Long.toString(crc.getValue());
    }
    
    private boolean loadLibrary(final String sharedLibName) {
        if (sharedLibName == null) {
            return false;
        }
        final String path = this.extractLibrary(sharedLibName);
        if (path != null) {
            System.load(path);
        }
        return path != null;
    }
    
    private String extractLibrary(final String sharedLibName) {
        final String srcCrc = this.crc(JniGenSharedLibraryLoader.class.getResourceAsStream("/" + sharedLibName));
        final File nativesDir = new File(String.valueOf(System.getProperty("java.io.tmpdir")) + "/jnigen/" + srcCrc);
        final File nativeFile = new File(nativesDir, sharedLibName);
        String extractedCrc = null;
        if (nativeFile.exists()) {
            try {
                extractedCrc = this.crc(new FileInputStream(nativeFile));
            }
            catch (FileNotFoundException ex2) {}
        }
        if (extractedCrc != null) {
            if (extractedCrc.equals(srcCrc)) {
                return nativeFile.exists() ? nativeFile.getAbsolutePath() : null;
            }
        }
        try {
            InputStream input = null;
            if (this.nativesJar == null) {
                input = JniGenSharedLibraryLoader.class.getResourceAsStream("/" + sharedLibName);
            }
            else {
                input = this.getFromJar(this.nativesJar, sharedLibName);
            }
            if (input == null) {
                return null;
            }
            nativeFile.getParentFile().mkdirs();
            final FileOutputStream output = new FileOutputStream(nativeFile);
            final byte[] buffer = new byte[4096];
            while (true) {
                final int length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                output.write(buffer, 0, length);
            }
            input.close();
            output.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return nativeFile.exists() ? nativeFile.getAbsolutePath() : null;
    }
    
    private InputStream getFromJar(final String jarFile, final String sharedLibrary) throws IOException {
        final ZipFile file = new ZipFile(this.nativesJar);
        final ZipEntry entry = file.getEntry(sharedLibrary);
        return file.getInputStream(entry);
    }
    
    public synchronized void load(final String sharedLibName) {
        if (JniGenSharedLibraryLoader.loadedLibraries.contains(sharedLibName)) {
            return;
        }
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        boolean isLinux = System.getProperty("os.name").contains("Linux");
        boolean isMac = System.getProperty("os.name").contains("Mac");
        boolean isAndroid = false;
        boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
        final boolean isArm = System.getProperty("os.arch").equals("arm");
        final String vm = System.getProperty("java.vm.name");
        if (vm != null && vm.contains("Dalvik")) {
            isAndroid = true;
            isWindows = false;
            isLinux = false;
            isMac = false;
            is64Bit = false;
        }
        boolean loaded = false;
        if (isWindows) {
            if (this.libraryFinder != null) {
                loaded = this.loadLibrary(this.libraryFinder.getSharedLibraryNameWindows(sharedLibName, is64Bit, this.nativesZip));
            }
            else if (!is64Bit) {
                loaded = this.loadLibrary(String.valueOf(sharedLibName) + ".dll");
            }
            else {
                loaded = this.loadLibrary(String.valueOf(sharedLibName) + "64.dll");
            }
        }
        if (isLinux) {
            if (this.libraryFinder != null) {
                loaded = this.loadLibrary(this.libraryFinder.getSharedLibraryNameLinux(sharedLibName, is64Bit, isArm, this.nativesZip));
            }
            else if (!is64Bit) {
                if (isArm) {
                    loaded = this.loadLibrary("lib" + sharedLibName + "Arm.so");
                }
                else {
                    loaded = this.loadLibrary("lib" + sharedLibName + ".so");
                }
            }
            else if (isArm) {
                loaded = this.loadLibrary("lib" + sharedLibName + "Arm64.so");
            }
            else {
                loaded = this.loadLibrary("lib" + sharedLibName + "64.so");
            }
        }
        if (isMac) {
            if (this.libraryFinder != null) {
                loaded = this.loadLibrary(this.libraryFinder.getSharedLibraryNameMac(sharedLibName, is64Bit, this.nativesZip));
            }
            else if (!is64Bit) {
                loaded = this.loadLibrary("lib" + sharedLibName + ".dylib");
            }
            else {
                loaded = this.loadLibrary("lib" + sharedLibName + "64.dylib");
            }
        }
        if (isAndroid) {
            if (this.libraryFinder != null) {
                System.loadLibrary(this.libraryFinder.getSharedLibraryNameAndroid(sharedLibName, this.nativesZip));
            }
            else {
                System.loadLibrary(sharedLibName);
            }
            loaded = true;
        }
        if (loaded) {
            JniGenSharedLibraryLoader.loadedLibraries.add(sharedLibName);
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.io.FileOutputStream;
import java.util.UUID;
import java.io.File;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.io.Closeable;
import java.util.zip.CRC32;
import java.io.InputStream;
import java.util.HashSet;

public class SharedLibraryLoader
{
    public static boolean isWindows;
    public static boolean isLinux;
    public static boolean isMac;
    public static boolean isIos;
    public static boolean isAndroid;
    public static boolean isARM;
    public static boolean is64Bit;
    public static String abi;
    private static final HashSet<String> loadedLibraries;
    private String nativesJar;
    
    static {
        SharedLibraryLoader.isWindows = System.getProperty("os.name").contains("Windows");
        SharedLibraryLoader.isLinux = System.getProperty("os.name").contains("Linux");
        SharedLibraryLoader.isMac = System.getProperty("os.name").contains("Mac");
        SharedLibraryLoader.isIos = false;
        SharedLibraryLoader.isAndroid = false;
        SharedLibraryLoader.isARM = System.getProperty("os.arch").startsWith("arm");
        SharedLibraryLoader.is64Bit = (System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64"));
        SharedLibraryLoader.abi = ((System.getProperty("sun.arch.abi") != null) ? System.getProperty("sun.arch.abi") : "");
        final boolean isMOEiOS = "iOS".equals(System.getProperty("moe.platform.name"));
        final String vm = System.getProperty("java.runtime.name");
        if (vm != null && vm.contains("Android Runtime")) {
            SharedLibraryLoader.isAndroid = true;
            SharedLibraryLoader.isWindows = false;
            SharedLibraryLoader.isLinux = false;
            SharedLibraryLoader.isMac = false;
            SharedLibraryLoader.is64Bit = false;
        }
        if (isMOEiOS || (!SharedLibraryLoader.isAndroid && !SharedLibraryLoader.isWindows && !SharedLibraryLoader.isLinux && !SharedLibraryLoader.isMac)) {
            SharedLibraryLoader.isIos = true;
            SharedLibraryLoader.isAndroid = false;
            SharedLibraryLoader.isWindows = false;
            SharedLibraryLoader.isLinux = false;
            SharedLibraryLoader.isMac = false;
            SharedLibraryLoader.is64Bit = false;
        }
        loadedLibraries = new HashSet<String>();
    }
    
    public SharedLibraryLoader() {
    }
    
    public SharedLibraryLoader(final String nativesJar) {
        this.nativesJar = nativesJar;
    }
    
    public String crc(final InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null.");
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
            return Long.toString(crc.getValue(), 16);
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
        StreamUtils.closeQuietly(input);
        return Long.toString(crc.getValue(), 16);
    }
    
    public String mapLibraryName(final String libraryName) {
        if (SharedLibraryLoader.isWindows) {
            return String.valueOf(libraryName) + (SharedLibraryLoader.is64Bit ? "64.dll" : ".dll");
        }
        if (SharedLibraryLoader.isLinux) {
            return "lib" + libraryName + (SharedLibraryLoader.isARM ? ("arm" + SharedLibraryLoader.abi) : "") + (SharedLibraryLoader.is64Bit ? "64.so" : ".so");
        }
        if (SharedLibraryLoader.isMac) {
            return "lib" + libraryName + (SharedLibraryLoader.is64Bit ? "64.dylib" : ".dylib");
        }
        return libraryName;
    }
    
    public void load(final String libraryName) {
        if (SharedLibraryLoader.isIos) {
            return;
        }
        synchronized (SharedLibraryLoader.class) {
            if (isLoaded(libraryName)) {
                // monitorexit(SharedLibraryLoader.class)
                return;
            }
            final String platformName = this.mapLibraryName(libraryName);
            try {
                if (SharedLibraryLoader.isAndroid) {
                    System.loadLibrary(platformName);
                }
                else {
                    this.loadFile(platformName);
                }
                setLoaded(libraryName);
            }
            catch (Throwable ex) {
                throw new GdxRuntimeException("Couldn't load shared library '" + platformName + "' for target: " + System.getProperty("os.name") + (SharedLibraryLoader.is64Bit ? ", 64-bit" : ", 32-bit"), ex);
            }
        }
        // monitorexit(SharedLibraryLoader.class)
    }
    
    private InputStream readFile(final String path) {
        if (this.nativesJar == null) {
            final InputStream input = SharedLibraryLoader.class.getResourceAsStream("/" + path);
            if (input == null) {
                throw new GdxRuntimeException("Unable to read file for extraction: " + path);
            }
            return input;
        }
        else {
            try {
                final ZipFile file = new ZipFile(this.nativesJar);
                final ZipEntry entry = file.getEntry(path);
                if (entry == null) {
                    throw new GdxRuntimeException("Couldn't find '" + path + "' in JAR: " + this.nativesJar);
                }
                return file.getInputStream(entry);
            }
            catch (IOException ex) {
                throw new GdxRuntimeException("Error reading '" + path + "' in JAR: " + this.nativesJar, ex);
            }
        }
    }
    
    public File extractFile(final String sourcePath, String dirName) throws IOException {
        try {
            final String sourceCrc = this.crc(this.readFile(sourcePath));
            if (dirName == null) {
                dirName = sourceCrc;
            }
            File extractedFile = this.getExtractedFile(dirName, new File(sourcePath).getName());
            if (extractedFile == null) {
                extractedFile = this.getExtractedFile(UUID.randomUUID().toString(), new File(sourcePath).getName());
                if (extractedFile == null) {
                    throw new GdxRuntimeException("Unable to find writable path to extract file. Is the user home directory writable?");
                }
            }
            return this.extractFile(sourcePath, sourceCrc, extractedFile);
        }
        catch (RuntimeException ex) {
            final File file = new File(System.getProperty("java.library.path"), sourcePath);
            if (file.exists()) {
                return file;
            }
            throw ex;
        }
    }
    
    public void extractFileTo(final String sourcePath, final File dir) throws IOException {
        this.extractFile(sourcePath, this.crc(this.readFile(sourcePath)), new File(dir, new File(sourcePath).getName()));
    }
    
    private File getExtractedFile(final String dirName, final String fileName) {
        final File idealFile = new File(String.valueOf(System.getProperty("java.io.tmpdir")) + "/libgdx" + System.getProperty("user.name") + "/" + dirName, fileName);
        if (this.canWrite(idealFile)) {
            return idealFile;
        }
        try {
            File file = File.createTempFile(dirName, null);
            if (file.delete()) {
                file = new File(file, fileName);
                if (this.canWrite(file)) {
                    return file;
                }
            }
        }
        catch (IOException ex) {}
        File file = new File(String.valueOf(System.getProperty("user.home")) + "/.libgdx/" + dirName, fileName);
        if (this.canWrite(file)) {
            return file;
        }
        file = new File(".temp/" + dirName, fileName);
        if (this.canWrite(file)) {
            return file;
        }
        if (System.getenv("APP_SANDBOX_CONTAINER_ID") != null) {
            return idealFile;
        }
        return null;
    }
    
    private boolean canWrite(final File file) {
        final File parent = file.getParentFile();
        File testFile;
        if (file.exists()) {
            if (!file.canWrite() || !this.canExecute(file)) {
                return false;
            }
            testFile = new File(parent, UUID.randomUUID().toString());
        }
        else {
            parent.mkdirs();
            if (!parent.isDirectory()) {
                return false;
            }
            testFile = file;
        }
        try {
            new FileOutputStream(testFile).close();
            return this.canExecute(testFile);
        }
        catch (Throwable ex) {
            return false;
        }
        finally {
            testFile.delete();
        }
    }
    
    private boolean canExecute(final File file) {
        try {
            final Method canExecute = File.class.getMethod("canExecute", (Class<?>[])new Class[0]);
            if (canExecute.invoke(file, new Object[0])) {
                return true;
            }
            final Method setExecutable = File.class.getMethod("setExecutable", Boolean.TYPE, Boolean.TYPE);
            setExecutable.invoke(file, true, false);
            return (boolean)canExecute.invoke(file, new Object[0]);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private File extractFile(final String sourcePath, final String sourceCrc, final File extractedFile) throws IOException {
        String extractedCrc = null;
        if (extractedFile.exists()) {
            try {
                extractedCrc = this.crc(new FileInputStream(extractedFile));
            }
            catch (FileNotFoundException ex2) {}
        }
        if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
            InputStream input = null;
            FileOutputStream output = null;
            try {
                input = this.readFile(sourcePath);
                extractedFile.getParentFile().mkdirs();
                output = new FileOutputStream(extractedFile);
                final byte[] buffer = new byte[4096];
                while (true) {
                    final int length = input.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    output.write(buffer, 0, length);
                }
            }
            catch (IOException ex) {
                throw new GdxRuntimeException("Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath(), ex);
            }
            finally {
                StreamUtils.closeQuietly(input);
                StreamUtils.closeQuietly(output);
            }
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }
        return extractedFile;
    }
    
    private void loadFile(final String sourcePath) {
        final String sourceCrc = this.crc(this.readFile(sourcePath));
        final String fileName = new File(sourcePath).getName();
        File file = new File(String.valueOf(System.getProperty("java.io.tmpdir")) + "/libgdx" + System.getProperty("user.name") + "/" + sourceCrc, fileName);
        final Throwable ex = this.loadFile(sourcePath, sourceCrc, file);
        if (ex == null) {
            return;
        }
        try {
            file = File.createTempFile(sourceCrc, null);
            if (file.delete() && this.loadFile(sourcePath, sourceCrc, file) == null) {
                return;
            }
        }
        catch (Throwable t) {}
        file = new File(String.valueOf(System.getProperty("user.home")) + "/.libgdx/" + sourceCrc, fileName);
        if (this.loadFile(sourcePath, sourceCrc, file) == null) {
            return;
        }
        file = new File(".temp/" + sourceCrc, fileName);
        if (this.loadFile(sourcePath, sourceCrc, file) == null) {
            return;
        }
        file = new File(System.getProperty("java.library.path"), sourcePath);
        if (file.exists()) {
            System.load(file.getAbsolutePath());
            return;
        }
        throw new GdxRuntimeException(ex);
    }
    
    private Throwable loadFile(final String sourcePath, final String sourceCrc, final File extractedFile) {
        try {
            System.load(this.extractFile(sourcePath, sourceCrc, extractedFile).getAbsolutePath());
            return null;
        }
        catch (Throwable ex) {
            return ex;
        }
    }
    
    public static synchronized void setLoaded(final String libraryName) {
        SharedLibraryLoader.loadedLibraries.add(libraryName);
    }
    
    public static synchronized boolean isLoaded(final String libraryName) {
        return SharedLibraryLoader.loadedLibraries.contains(libraryName);
    }
}

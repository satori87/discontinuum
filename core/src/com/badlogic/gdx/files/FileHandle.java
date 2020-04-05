// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.files;

import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.InputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import java.io.File;

public class FileHandle
{
    protected File file;
    protected Files.FileType type;
    
    protected FileHandle() {
    }
    
    public FileHandle(final String fileName) {
        this.file = new File(fileName);
        this.type = Files.FileType.Absolute;
    }
    
    public FileHandle(final File file) {
        this.file = file;
        this.type = Files.FileType.Absolute;
    }
    
    protected FileHandle(final String fileName, final Files.FileType type) {
        this.type = type;
        this.file = new File(fileName);
    }
    
    protected FileHandle(final File file, final Files.FileType type) {
        this.file = file;
        this.type = type;
    }
    
    public String path() {
        return this.file.getPath().replace('\\', '/');
    }
    
    public String name() {
        return this.file.getName();
    }
    
    public String extension() {
        final String name = this.file.getName();
        final int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            return "";
        }
        return name.substring(dotIndex + 1);
    }
    
    public String nameWithoutExtension() {
        final String name = this.file.getName();
        final int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            return name;
        }
        return name.substring(0, dotIndex);
    }
    
    public String pathWithoutExtension() {
        final String path = this.file.getPath().replace('\\', '/');
        final int dotIndex = path.lastIndexOf(46);
        if (dotIndex == -1) {
            return path;
        }
        return path.substring(0, dotIndex);
    }
    
    public Files.FileType type() {
        return this.type;
    }
    
    public File file() {
        if (this.type == Files.FileType.External) {
            return new File(Gdx.files.getExternalStoragePath(), this.file.getPath());
        }
        return this.file;
    }
    
    public InputStream read() {
        if (this.type == Files.FileType.Classpath || (this.type == Files.FileType.Internal && !this.file().exists()) || (this.type == Files.FileType.Local && !this.file().exists())) {
            final InputStream input = FileHandle.class.getResourceAsStream("/" + this.file.getPath().replace('\\', '/'));
            if (input == null) {
                throw new GdxRuntimeException("File not found: " + this.file + " (" + this.type + ")");
            }
            return input;
        }
        else {
            try {
                return new FileInputStream(this.file());
            }
            catch (Exception ex) {
                if (this.file().isDirectory()) {
                    throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
            }
        }
    }
    
    public BufferedInputStream read(final int bufferSize) {
        return new BufferedInputStream(this.read(), bufferSize);
    }
    
    public Reader reader() {
        return new InputStreamReader(this.read());
    }
    
    public Reader reader(final String charset) {
        final InputStream stream = this.read();
        try {
            return new InputStreamReader(stream, charset);
        }
        catch (UnsupportedEncodingException ex) {
            StreamUtils.closeQuietly(stream);
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }
    
    public BufferedReader reader(final int bufferSize) {
        return new BufferedReader(new InputStreamReader(this.read()), bufferSize);
    }
    
    public BufferedReader reader(final int bufferSize, final String charset) {
        try {
            return new BufferedReader(new InputStreamReader(this.read(), charset), bufferSize);
        }
        catch (UnsupportedEncodingException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }
    
    public String readString() {
        return this.readString(null);
    }
    
    public String readString(final String charset) {
        final StringBuilder output = new StringBuilder(this.estimateLength());
        InputStreamReader reader = null;
        try {
            if (charset == null) {
                reader = new InputStreamReader(this.read());
            }
            else {
                reader = new InputStreamReader(this.read(), charset);
            }
            final char[] buffer = new char[256];
            while (true) {
                final int length = reader.read(buffer);
                if (length == -1) {
                    break;
                }
                output.append(buffer, 0, length);
            }
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error reading layout file: " + this, ex);
        }
        finally {
            StreamUtils.closeQuietly(reader);
        }
        StreamUtils.closeQuietly(reader);
        return output.toString();
    }
    
    public byte[] readBytes() {
        final InputStream input = this.read();
        try {
            return StreamUtils.copyStreamToByteArray(input, this.estimateLength());
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
    }
    
    private int estimateLength() {
        final int length = (int)this.length();
        return (length != 0) ? length : 512;
    }
    
    public int readBytes(final byte[] bytes, final int offset, final int size) {
        final InputStream input = this.read();
        int position = 0;
        try {
            while (true) {
                final int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) {
                    break;
                }
                position += count;
            }
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
        finally {
            StreamUtils.closeQuietly(input);
        }
        StreamUtils.closeQuietly(input);
        return position - offset;
    }
    
    public ByteBuffer map() {
        return this.map(FileChannel.MapMode.READ_ONLY);
    }
    
    public ByteBuffer map(final FileChannel.MapMode mode) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot map a classpath file: " + this);
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.file, (mode == FileChannel.MapMode.READ_ONLY) ? "r" : "rw");
            final FileChannel fileChannel = raf.getChannel();
            final ByteBuffer map = fileChannel.map(mode, 0L, this.file.length());
            map.order(ByteOrder.nativeOrder());
            return map;
        }
        catch (Exception ex) {
            throw new GdxRuntimeException("Error memory mapping file: " + this + " (" + this.type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(raf);
        }
    }
    
    public OutputStream write(final boolean append) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        }
        this.parent().mkdirs();
        try {
            return new FileOutputStream(this.file(), append);
        }
        catch (Exception ex) {
            if (this.file().isDirectory()) {
                throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
    }
    
    public OutputStream write(final boolean append, final int bufferSize) {
        return new BufferedOutputStream(this.write(append), bufferSize);
    }
    
    public void write(final InputStream input, final boolean append) {
        OutputStream output = null;
        try {
            output = this.write(append);
            StreamUtils.copyStream(input, output);
        }
        catch (Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }
        StreamUtils.closeQuietly(input);
        StreamUtils.closeQuietly(output);
    }
    
    public Writer writer(final boolean append) {
        return this.writer(append, null);
    }
    
    public Writer writer(final boolean append, final String charset) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        }
        this.parent().mkdirs();
        try {
            final FileOutputStream output = new FileOutputStream(this.file(), append);
            if (charset == null) {
                return new OutputStreamWriter(output);
            }
            return new OutputStreamWriter(output, charset);
        }
        catch (IOException ex) {
            if (this.file().isDirectory()) {
                throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
    }
    
    public void writeString(final String string, final boolean append) {
        this.writeString(string, append, null);
    }
    
    public void writeString(final String string, final boolean append, final String charset) {
        Writer writer = null;
        try {
            writer = this.writer(append, charset);
            writer.write(string);
        }
        catch (Exception ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(writer);
        }
        StreamUtils.closeQuietly(writer);
    }
    
    public void writeBytes(final byte[] bytes, final boolean append) {
        final OutputStream output = this.write(append);
        try {
            output.write(bytes);
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(output);
        }
        StreamUtils.closeQuietly(output);
    }
    
    public void writeBytes(final byte[] bytes, final int offset, final int length, final boolean append) {
        final OutputStream output = this.write(append);
        try {
            output.write(bytes, offset, length);
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            StreamUtils.closeQuietly(output);
        }
        StreamUtils.closeQuietly(output);
    }
    
    public FileHandle[] list() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final String[] relativePaths = this.file().list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        final FileHandle[] handles = new FileHandle[relativePaths.length];
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            handles[i] = this.child(relativePaths[i]);
        }
        return handles;
    }
    
    public FileHandle[] list(final FileFilter filter) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final File file = this.file();
        final String[] relativePaths = file.list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            final String path = relativePaths[i];
            final FileHandle child = this.child(path);
            if (filter.accept(child.file())) {
                handles[count] = child;
                ++count;
            }
        }
        if (count < relativePaths.length) {
            final FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }
    
    public FileHandle[] list(final FilenameFilter filter) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final File file = this.file();
        final String[] relativePaths = file.list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            final String path = relativePaths[i];
            if (filter.accept(file, path)) {
                handles[count] = this.child(path);
                ++count;
            }
        }
        if (count < relativePaths.length) {
            final FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }
    
    public FileHandle[] list(final String suffix) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final String[] relativePaths = this.file().list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            final String path = relativePaths[i];
            if (path.endsWith(suffix)) {
                handles[count] = this.child(path);
                ++count;
            }
        }
        if (count < relativePaths.length) {
            final FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }
    
    public boolean isDirectory() {
        return this.type != Files.FileType.Classpath && this.file().isDirectory();
    }
    
    public FileHandle child(final String name) {
        if (this.file.getPath().length() == 0) {
            return new FileHandle(new File(name), this.type);
        }
        return new FileHandle(new File(this.file, name), this.type);
    }
    
    public FileHandle sibling(final String name) {
        if (this.file.getPath().length() == 0) {
            throw new GdxRuntimeException("Cannot get the sibling of the root.");
        }
        return new FileHandle(new File(this.file.getParent(), name), this.type);
    }
    
    public FileHandle parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            if (this.type == Files.FileType.Absolute) {
                parent = new File("/");
            }
            else {
                parent = new File("");
            }
        }
        return new FileHandle(parent, this.type);
    }
    
    public void mkdirs() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot mkdirs with a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + this.file);
        }
        this.file().mkdirs();
    }
    
    public boolean exists() {
        switch (this.type) {
            case Internal: {
                if (this.file().exists()) {
                    return true;
                }
                return FileHandle.class.getResource("/" + this.file.getPath().replace('\\', '/')) != null;
            }
            case Classpath: {
                return FileHandle.class.getResource("/" + this.file.getPath().replace('\\', '/')) != null;
            }
            default: {
                return this.file().exists();
            }
        }
    }
    
    public boolean delete() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
        return this.file().delete();
    }
    
    public boolean deleteDirectory() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
        return deleteDirectory(this.file());
    }
    
    public void emptyDirectory() {
        this.emptyDirectory(false);
    }
    
    public void emptyDirectory(final boolean preserveTree) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        }
        if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
        emptyDirectory(this.file(), preserveTree);
    }
    
    public void copyTo(FileHandle dest) {
        if (!this.isDirectory()) {
            if (dest.isDirectory()) {
                dest = dest.child(this.name());
            }
            copyFile(this, dest);
            return;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
            }
        }
        else {
            dest.mkdirs();
            if (!dest.isDirectory()) {
                throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
            }
        }
        copyDirectory(this, dest.child(this.name()));
    }
    
    public void moveTo(final FileHandle dest) {
        switch (this.type) {
            case Classpath: {
                throw new GdxRuntimeException("Cannot move a classpath file: " + this.file);
            }
            case Internal: {
                throw new GdxRuntimeException("Cannot move an internal file: " + this.file);
            }
            case External:
            case Absolute: {
                if (this.file().renameTo(dest.file())) {
                    return;
                }
                break;
            }
        }
        this.copyTo(dest);
        this.delete();
        if (this.exists() && this.isDirectory()) {
            this.deleteDirectory();
        }
    }
    
    public long length() {
        if (this.type == Files.FileType.Classpath || (this.type == Files.FileType.Internal && !this.file.exists())) {
            final InputStream input = this.read();
            try {
                return input.available();
            }
            catch (Exception ex) {}
            finally {
                StreamUtils.closeQuietly(input);
            }
            return 0L;
        }
        return this.file().length();
    }
    
    public long lastModified() {
        return this.file().lastModified();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FileHandle)) {
            return false;
        }
        final FileHandle other = (FileHandle)obj;
        return this.type == other.type && this.path().equals(other.path());
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + this.type.hashCode();
        hash = hash * 67 + this.path().hashCode();
        return hash;
    }
    
    @Override
    public String toString() {
        return this.file.getPath().replace('\\', '/');
    }
    
    public static FileHandle tempFile(final String prefix) {
        try {
            return new FileHandle(File.createTempFile(prefix, null));
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }
    
    public static FileHandle tempDirectory(final String prefix) {
        try {
            final File file = File.createTempFile(prefix, null);
            if (!file.delete()) {
                throw new IOException("Unable to delete temp file: " + file);
            }
            if (!file.mkdir()) {
                throw new IOException("Unable to create temp directory: " + file);
            }
            return new FileHandle(file);
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }
    
    private static void emptyDirectory(final File file, final boolean preserveTree) {
        if (file.exists()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, n = files.length; i < n; ++i) {
                    if (!files[i].isDirectory()) {
                        files[i].delete();
                    }
                    else if (preserveTree) {
                        emptyDirectory(files[i], true);
                    }
                    else {
                        deleteDirectory(files[i]);
                    }
                }
            }
        }
    }
    
    private static boolean deleteDirectory(final File file) {
        emptyDirectory(file, false);
        return file.delete();
    }
    
    private static void copyFile(final FileHandle source, final FileHandle dest) {
        try {
            dest.write(source.read(), false);
        }
        catch (Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" + "To destination: " + dest.file + " (" + dest.type + ")", ex);
        }
    }
    
    private static void copyDirectory(final FileHandle sourceDir, final FileHandle destDir) {
        destDir.mkdirs();
        final FileHandle[] files = sourceDir.list();
        for (int i = 0, n = files.length; i < n; ++i) {
            final FileHandle srcFile = files[i];
            final FileHandle destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile);
            }
            else {
                copyFile(srcFile, destFile);
            }
        }
    }
}

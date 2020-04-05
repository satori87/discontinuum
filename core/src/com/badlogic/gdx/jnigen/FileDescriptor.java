// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

public class FileDescriptor
{
    protected File file;
    protected FileType type;
    
    protected FileDescriptor() {
    }
    
    public FileDescriptor(final String fileName) {
        this.file = new File(fileName);
        this.type = FileType.Absolute;
    }
    
    public FileDescriptor(final File file) {
        this.file = file;
        this.type = FileType.Absolute;
    }
    
    protected FileDescriptor(final String fileName, final FileType type) {
        this.type = type;
        this.file = new File(fileName);
    }
    
    protected FileDescriptor(final File file, final FileType type) {
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
    
    public FileType type() {
        return this.type;
    }
    
    public File file() {
        return this.file;
    }
    
    public InputStream read() {
        if (this.type == FileType.Classpath && !this.file.exists()) {
            final InputStream input = FileDescriptor.class.getResourceAsStream("/" + this.file.getPath().replace('\\', '/'));
            if (input == null) {
                throw new RuntimeException("File not found: " + this.file + " (" + this.type + ")");
            }
            return input;
        }
        else {
            try {
                return new FileInputStream(this.file());
            }
            catch (FileNotFoundException ex) {
                if (this.file().isDirectory()) {
                    throw new RuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new RuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
            }
        }
    }
    
    public Reader reader() {
        return new InputStreamReader(this.read());
    }
    
    public Reader reader(final String charset) {
        try {
            return new InputStreamReader(this.read(), charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
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
            throw new RuntimeException("Error reading file: " + this, ex);
        }
    }
    
    public String readString() {
        return this.readString(null);
    }
    
    public String readString(final String charset) {
        final StringBuilder output = new StringBuilder(512);
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
            throw new RuntimeException("Error reading layout file: " + this, ex);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException ex2) {}
        }
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException ex3) {}
        return output.toString();
    }
    
    public byte[] readBytes() {
        int length = (int)this.length();
        if (length == 0) {
            length = 512;
        }
        byte[] buffer = new byte[length];
        int position = 0;
        final InputStream input = this.read();
        try {
            while (true) {
                final int count = input.read(buffer, position, buffer.length - position);
                if (count == -1) {
                    break;
                }
                position += count;
                if (position != buffer.length) {
                    continue;
                }
                final byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, position);
                buffer = newBuffer;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex2) {}
        }
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException ex3) {}
        if (position < buffer.length) {
            final byte[] newBuffer2 = new byte[position];
            System.arraycopy(buffer, 0, newBuffer2, 0, position);
            buffer = newBuffer2;
        }
        return buffer;
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
            throw new RuntimeException("Error reading file: " + this, ex);
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex2) {}
        }
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException ex3) {}
        return position - offset;
    }
    
    public OutputStream write(final boolean append) {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot write to a classpath file: " + this.file);
        }
        this.parent().mkdirs();
        try {
            return new FileOutputStream(this.file(), append);
        }
        catch (FileNotFoundException ex) {
            if (this.file().isDirectory()) {
                throw new RuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new RuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
    }
    
    public void write(final InputStream input, final boolean append) {
        OutputStream output = null;
        try {
            output = this.write(append);
            final byte[] buffer = new byte[4096];
            while (true) {
                final int length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                output.write(buffer, 0, length);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error stream writing to file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (Exception ex2) {}
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (Exception ex3) {}
        }
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (Exception ex4) {}
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (Exception ex5) {}
    }
    
    public Writer writer(final boolean append) {
        return this.writer(append, null);
    }
    
    public Writer writer(final boolean append, final String charset) {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot write to a classpath file: " + this.file);
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
                throw new RuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new RuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
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
            throw new RuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (Exception ex2) {}
        }
        try {
            if (writer != null) {
                writer.close();
            }
        }
        catch (Exception ex3) {}
    }
    
    public void writeBytes(final byte[] bytes, final boolean append) {
        final OutputStream output = this.write(append);
        try {
            output.write(bytes);
        }
        catch (IOException ex) {
            throw new RuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        }
        finally {
            try {
                output.close();
            }
            catch (IOException ex2) {}
        }
        try {
            output.close();
        }
        catch (IOException ex3) {}
    }
    
    public FileDescriptor[] list() {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final String[] relativePaths = this.file().list();
        if (relativePaths == null) {
            return new FileDescriptor[0];
        }
        final FileDescriptor[] handles = new FileDescriptor[relativePaths.length];
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            handles[i] = this.child(relativePaths[i]);
        }
        return handles;
    }
    
    public FileDescriptor[] list(final String suffix) {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot list a classpath directory: " + this.file);
        }
        final String[] relativePaths = this.file().list();
        if (relativePaths == null) {
            return new FileDescriptor[0];
        }
        FileDescriptor[] handles = new FileDescriptor[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; ++i) {
            final String path = relativePaths[i];
            if (path.endsWith(suffix)) {
                handles[count] = this.child(path);
                ++count;
            }
        }
        if (count < relativePaths.length) {
            final FileDescriptor[] newHandles = new FileDescriptor[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }
    
    public boolean isDirectory() {
        return this.type != FileType.Classpath && this.file().isDirectory();
    }
    
    public FileDescriptor child(final String name) {
        if (this.file.getPath().length() == 0) {
            return new FileDescriptor(new File(name), this.type);
        }
        return new FileDescriptor(new File(this.file, name), this.type);
    }
    
    public FileDescriptor parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            if (this.type == FileType.Absolute) {
                parent = new File("/");
            }
            else {
                parent = new File("");
            }
        }
        return new FileDescriptor(parent, this.type);
    }
    
    public boolean mkdirs() {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot mkdirs with a classpath file: " + this.file);
        }
        return this.file().mkdirs();
    }
    
    public boolean exists() {
        if (this.type == FileType.Classpath) {
            return FileDescriptor.class.getResource("/" + this.file.getPath().replace('\\', '/')) != null;
        }
        return this.file().exists();
    }
    
    public boolean delete() {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot delete a classpath file: " + this.file);
        }
        return this.file().delete();
    }
    
    public boolean deleteDirectory() {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot delete a classpath file: " + this.file);
        }
        return deleteDirectory(this.file());
    }
    
    public void copyTo(FileDescriptor dest) {
        if (!this.isDirectory()) {
            if (dest.isDirectory()) {
                dest = dest.child(this.name());
            }
            copyFile(this, dest);
            return;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                throw new RuntimeException("Destination exists but is not a directory: " + dest);
            }
        }
        else {
            dest.mkdirs();
            if (!dest.isDirectory()) {
                throw new RuntimeException("Destination directory cannot be created: " + dest);
            }
        }
        dest = dest.child(this.name());
        copyDirectory(this, dest);
    }
    
    public void moveTo(final FileDescriptor dest) {
        if (this.type == FileType.Classpath) {
            throw new RuntimeException("Cannot move a classpath file: " + this.file);
        }
        this.copyTo(dest);
        this.delete();
    }
    
    public long length() {
        if (this.type == FileType.Classpath || !this.file.exists()) {
            final InputStream input = this.read();
            try {
                return input.available();
            }
            catch (Exception ex) {}
            finally {
                try {
                    input.close();
                }
                catch (IOException ex2) {}
            }
            return 0L;
        }
        return this.file().length();
    }
    
    public long lastModified() {
        return this.file().lastModified();
    }
    
    @Override
    public String toString() {
        return this.file.getPath();
    }
    
    public static FileDescriptor tempFile(final String prefix) {
        try {
            return new FileDescriptor(File.createTempFile(prefix, null));
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to create temp file.", ex);
        }
    }
    
    public static FileDescriptor tempDirectory(final String prefix) {
        try {
            final File file = File.createTempFile(prefix, null);
            if (!file.delete()) {
                throw new IOException("Unable to delete temp file: " + file);
            }
            if (!file.mkdir()) {
                throw new IOException("Unable to create temp directory: " + file);
            }
            return new FileDescriptor(file);
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to create temp file.", ex);
        }
    }
    
    private static boolean deleteDirectory(final File file) {
        if (file.exists()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, n = files.length; i < n; ++i) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return file.delete();
    }
    
    private static void copyFile(final FileDescriptor source, final FileDescriptor dest) {
        try {
            dest.write(source.read(), false);
        }
        catch (Exception ex) {
            throw new RuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" + "To destination: " + dest.file + " (" + dest.type + ")", ex);
        }
    }
    
    private static void copyDirectory(final FileDescriptor sourceDir, final FileDescriptor destDir) {
        destDir.mkdirs();
        final FileDescriptor[] files = sourceDir.list();
        for (int i = 0, n = files.length; i < n; ++i) {
            final FileDescriptor srcFile = files[i];
            final FileDescriptor destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile);
            }
            else {
                copyFile(srcFile, destFile);
            }
        }
    }
    
    public enum FileType
    {
        Classpath("Classpath", 0), 
        Absolute("Absolute", 1);
        
        private FileType(final String name, final int ordinal) {
        }
    }
}

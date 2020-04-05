// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen.parsing;

import java.util.Iterator;
import java.util.ArrayList;

public interface JavaMethodParser
{
    ArrayList<JavaSegment> parse(final String p0) throws Exception;
    
    public enum ArgumentType
    {
        Boolean("Boolean", 0, "jboolean"), 
        Byte("Byte", 1, "jbyte"), 
        Char("Char", 2, "jchar"), 
        Short("Short", 3, "jshort"), 
        Integer("Integer", 4, "jint"), 
        Long("Long", 5, "jlong"), 
        Float("Float", 6, "jfloat"), 
        Double("Double", 7, "jdouble"), 
        Buffer("Buffer", 8, "jobject"), 
        ByteBuffer("ByteBuffer", 9, "jobject"), 
        CharBuffer("CharBuffer", 10, "jobject"), 
        ShortBuffer("ShortBuffer", 11, "jobject"), 
        IntBuffer("IntBuffer", 12, "jobject"), 
        LongBuffer("LongBuffer", 13, "jobject"), 
        FloatBuffer("FloatBuffer", 14, "jobject"), 
        DoubleBuffer("DoubleBuffer", 15, "jobject"), 
        BooleanArray("BooleanArray", 16, "jbooleanArray"), 
        ByteArray("ByteArray", 17, "jbyteArray"), 
        CharArray("CharArray", 18, "jcharArray"), 
        ShortArray("ShortArray", 19, "jshortArray"), 
        IntegerArray("IntegerArray", 20, "jintArray"), 
        LongArray("LongArray", 21, "jlongArray"), 
        FloatArray("FloatArray", 22, "jfloatArray"), 
        DoubleArray("DoubleArray", 23, "jdoubleArray"), 
        String("String", 24, "jstring"), 
        Class("Class", 25, "jclass"), 
        Throwable("Throwable", 26, "jthrowable"), 
        Object("Object", 27, "jobject"), 
        ObjectArray("ObjectArray", 28, "jobjectArray");
        
        private final String jniType;
        
        private ArgumentType(final String name, final int ordinal, final String jniType) {
            this.jniType = jniType;
        }
        
        public boolean isPrimitiveArray() {
            return this.toString().endsWith("Array") && this != ArgumentType.ObjectArray;
        }
        
        public boolean isBuffer() {
            return this.toString().endsWith("Buffer");
        }
        
        public boolean isObject() {
            return this.toString().equals("Object") || this == ArgumentType.ObjectArray;
        }
        
        public boolean isString() {
            return this.toString().equals("String");
        }
        
        public boolean isPlainOldDataType() {
            return !this.isString() && !this.isPrimitiveArray() && !this.isBuffer() && !this.isObject();
        }
        
        public String getBufferCType() {
            if (!this.isBuffer()) {
                throw new RuntimeException("ArgumentType " + this + " is not a Buffer!");
            }
            if (this == ArgumentType.Buffer) {
                return "unsigned char*";
            }
            if (this == ArgumentType.ByteBuffer) {
                return "char*";
            }
            if (this == ArgumentType.CharBuffer) {
                return "unsigned short*";
            }
            if (this == ArgumentType.ShortBuffer) {
                return "short*";
            }
            if (this == ArgumentType.IntBuffer) {
                return "int*";
            }
            if (this == ArgumentType.LongBuffer) {
                return "long long*";
            }
            if (this == ArgumentType.FloatBuffer) {
                return "float*";
            }
            if (this == ArgumentType.DoubleBuffer) {
                return "double*";
            }
            throw new RuntimeException("Unknown Buffer type " + this);
        }
        
        public String getArrayCType() {
            if (!this.isPrimitiveArray()) {
                throw new RuntimeException("ArgumentType " + this + " is not an Array!");
            }
            if (this == ArgumentType.BooleanArray) {
                return "bool*";
            }
            if (this == ArgumentType.ByteArray) {
                return "char*";
            }
            if (this == ArgumentType.CharArray) {
                return "unsigned short*";
            }
            if (this == ArgumentType.ShortArray) {
                return "short*";
            }
            if (this == ArgumentType.IntegerArray) {
                return "int*";
            }
            if (this == ArgumentType.LongArray) {
                return "long long*";
            }
            if (this == ArgumentType.FloatArray) {
                return "float*";
            }
            if (this == ArgumentType.DoubleArray) {
                return "double*";
            }
            throw new RuntimeException("Unknown Array type " + this);
        }
        
        public String getJniType() {
            return this.jniType;
        }
    }
    
    public static class JniSection implements JavaSegment
    {
        private String nativeCode;
        private final int startIndex;
        private final int endIndex;
        
        public JniSection(final String nativeCode, final int startIndex, final int endIndex) {
            this.nativeCode = nativeCode;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
        
        public String getNativeCode() {
            return this.nativeCode;
        }
        
        public void setNativeCode(final String nativeCode) {
            this.nativeCode = nativeCode;
        }
        
        @Override
        public int getStartIndex() {
            return this.startIndex;
        }
        
        @Override
        public int getEndIndex() {
            return this.endIndex;
        }
        
        @Override
        public String toString() {
            return "JniSection [nativeCode=" + this.nativeCode + ", startIndex=" + this.startIndex + ", endIndex=" + this.endIndex + "]";
        }
    }
    
    public static class Argument
    {
        final ArgumentType type;
        private final String name;
        
        public Argument(final ArgumentType type, final String name) {
            this.type = type;
            this.name = name;
        }
        
        public ArgumentType getType() {
            return this.type;
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public String toString() {
            return "Argument [type=" + this.type + ", name=" + this.name + "]";
        }
    }
    
    public static class JavaMethod implements JavaSegment
    {
        private final String className;
        private final String name;
        private final boolean isStatic;
        private boolean isManual;
        private final String returnType;
        private String nativeCode;
        private final ArrayList<Argument> arguments;
        private final boolean hasDisposableArgument;
        private final int startIndex;
        private final int endIndex;
        
        public JavaMethod(final String className, final String name, final boolean isStatic, final String returnType, final String nativeCode, final ArrayList<Argument> arguments, final int startIndex, final int endIndex) {
            this.className = className;
            this.name = name;
            this.isStatic = isStatic;
            this.returnType = returnType;
            this.nativeCode = nativeCode;
            this.arguments = arguments;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            for (final Argument arg : arguments) {
                if (arg.type.isPrimitiveArray() || arg.type.isBuffer() || arg.type.isString()) {
                    this.hasDisposableArgument = true;
                    return;
                }
            }
            this.hasDisposableArgument = false;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean isStatic() {
            return this.isStatic;
        }
        
        public void setManual(final boolean isManual) {
            this.isManual = isManual;
        }
        
        public boolean isManual() {
            return this.isManual;
        }
        
        public String getReturnType() {
            return this.returnType;
        }
        
        public String getNativeCode() {
            return this.nativeCode;
        }
        
        public void setNativeCode(final String nativeCode) {
            this.nativeCode = nativeCode;
        }
        
        public ArrayList<Argument> getArguments() {
            return this.arguments;
        }
        
        public boolean hasDisposableArgument() {
            return this.hasDisposableArgument;
        }
        
        @Override
        public int getStartIndex() {
            return this.startIndex;
        }
        
        @Override
        public int getEndIndex() {
            return this.endIndex;
        }
        
        public CharSequence getClassName() {
            return this.className;
        }
        
        @Override
        public String toString() {
            return "JavaMethod [className=" + this.className + ", name=" + this.name + ", isStatic=" + this.isStatic + ", returnType=" + this.returnType + ", nativeCode=" + this.nativeCode + ", arguments=" + this.arguments + ", hasDisposableArgument=" + this.hasDisposableArgument + ", startIndex=" + this.startIndex + ", endIndex=" + this.endIndex + "]";
        }
    }
    
    public interface JavaSegment
    {
        int getStartIndex();
        
        int getEndIndex();
    }
}

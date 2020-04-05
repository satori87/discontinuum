// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen.parsing;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import com.github.javaparser.ast.CompilationUnit;
import java.io.InputStream;
import com.github.javaparser.JavaParser;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.Stack;
import java.util.Map;

public class RobustJavaMethodParser implements JavaMethodParser
{
    private static final String JNI_MANUAL = "MANUAL";
    private static final Map<String, ArgumentType> plainOldDataTypes;
    private static final Map<String, ArgumentType> arrayTypes;
    private static final Map<String, ArgumentType> bufferTypes;
    private static final Map<String, ArgumentType> otherTypes;
    public static String CustomIgnoreTag;
    Stack<TypeDeclaration> classStack;
    
    static {
        RobustJavaMethodParser.CustomIgnoreTag = "";
        (plainOldDataTypes = new HashMap<String, ArgumentType>()).put("boolean", ArgumentType.Boolean);
        RobustJavaMethodParser.plainOldDataTypes.put("byte", ArgumentType.Byte);
        RobustJavaMethodParser.plainOldDataTypes.put("char", ArgumentType.Char);
        RobustJavaMethodParser.plainOldDataTypes.put("short", ArgumentType.Short);
        RobustJavaMethodParser.plainOldDataTypes.put("int", ArgumentType.Integer);
        RobustJavaMethodParser.plainOldDataTypes.put("long", ArgumentType.Long);
        RobustJavaMethodParser.plainOldDataTypes.put("float", ArgumentType.Float);
        RobustJavaMethodParser.plainOldDataTypes.put("double", ArgumentType.Double);
        (arrayTypes = new HashMap<String, ArgumentType>()).put("boolean", ArgumentType.BooleanArray);
        RobustJavaMethodParser.arrayTypes.put("byte", ArgumentType.ByteArray);
        RobustJavaMethodParser.arrayTypes.put("char", ArgumentType.CharArray);
        RobustJavaMethodParser.arrayTypes.put("short", ArgumentType.ShortArray);
        RobustJavaMethodParser.arrayTypes.put("int", ArgumentType.IntegerArray);
        RobustJavaMethodParser.arrayTypes.put("long", ArgumentType.LongArray);
        RobustJavaMethodParser.arrayTypes.put("float", ArgumentType.FloatArray);
        RobustJavaMethodParser.arrayTypes.put("double", ArgumentType.DoubleArray);
        (bufferTypes = new HashMap<String, ArgumentType>()).put("Buffer", ArgumentType.Buffer);
        RobustJavaMethodParser.bufferTypes.put("ByteBuffer", ArgumentType.ByteBuffer);
        RobustJavaMethodParser.bufferTypes.put("CharBuffer", ArgumentType.CharBuffer);
        RobustJavaMethodParser.bufferTypes.put("ShortBuffer", ArgumentType.ShortBuffer);
        RobustJavaMethodParser.bufferTypes.put("IntBuffer", ArgumentType.IntBuffer);
        RobustJavaMethodParser.bufferTypes.put("LongBuffer", ArgumentType.LongBuffer);
        RobustJavaMethodParser.bufferTypes.put("FloatBuffer", ArgumentType.FloatBuffer);
        RobustJavaMethodParser.bufferTypes.put("DoubleBuffer", ArgumentType.DoubleBuffer);
        (otherTypes = new HashMap<String, ArgumentType>()).put("String", ArgumentType.String);
        RobustJavaMethodParser.otherTypes.put("Class", ArgumentType.Class);
        RobustJavaMethodParser.otherTypes.put("Throwable", ArgumentType.Throwable);
    }
    
    public RobustJavaMethodParser() {
        this.classStack = new Stack<TypeDeclaration>();
    }
    
    @Override
    public ArrayList<JavaSegment> parse(final String classFile) throws Exception {
        final CompilationUnit unit = JavaParser.parse((InputStream)new ByteArrayInputStream(classFile.getBytes()));
        final ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();
        this.getJavaMethods(methods, this.getOuterClass(unit));
        final ArrayList<JniSection> methodBodies = this.getNativeCodeBodies(classFile);
        final ArrayList<JniSection> sections = this.getJniSections(classFile);
        this.alignMethodBodies(methods, methodBodies);
        final ArrayList<JavaSegment> segments = this.sortMethodsAndSections(methods, sections);
        return segments;
    }
    
    private ArrayList<JavaSegment> sortMethodsAndSections(final ArrayList<JavaMethod> methods, final ArrayList<JniSection> sections) {
        final ArrayList<JavaSegment> segments = new ArrayList<JavaSegment>();
        segments.addAll(methods);
        segments.addAll(sections);
        Collections.sort(segments, new Comparator<JavaSegment>() {
            @Override
            public int compare(final JavaSegment o1, final JavaSegment o2) {
                return o1.getStartIndex() - o2.getStartIndex();
            }
        });
        return segments;
    }
    
    private void alignMethodBodies(final ArrayList<JavaMethod> methods, final ArrayList<JniSection> methodBodies) {
        for (final JavaMethod method : methods) {
            for (final JniSection section : methodBodies) {
                if (method.getEndIndex() == section.getStartIndex()) {
                    if (section.getNativeCode().startsWith("MANUAL")) {
                        section.setNativeCode(section.getNativeCode().substring("MANUAL".length()));
                        method.setManual(true);
                    }
                    method.setNativeCode(section.getNativeCode());
                    break;
                }
            }
        }
    }
    
    private void getJavaMethods(final ArrayList<JavaMethod> methods, final TypeDeclaration type) {
        this.classStack.push(type);
        if (type.getMembers() != null) {
            for (final BodyDeclaration member : type.getMembers()) {
                if (member instanceof ClassOrInterfaceDeclaration || member instanceof EnumDeclaration) {
                    this.getJavaMethods(methods, (TypeDeclaration)member);
                }
                else {
                    if (!(member instanceof MethodDeclaration)) {
                        continue;
                    }
                    final MethodDeclaration method = (MethodDeclaration)member;
                    if (!ModifierSet.hasModifier(((MethodDeclaration)member).getModifiers(), 256)) {
                        continue;
                    }
                    methods.add(this.createMethod(method));
                }
            }
        }
        this.classStack.pop();
    }
    
    private JavaMethod createMethod(final MethodDeclaration method) {
        final String className = this.classStack.peek().getName();
        final String name = method.getName();
        final boolean isStatic = ModifierSet.hasModifier(method.getModifiers(), 8);
        final String returnType = method.getType().toString();
        final ArrayList<Argument> arguments = new ArrayList<Argument>();
        if (method.getParameters() != null) {
            for (final Parameter parameter : method.getParameters()) {
                arguments.add(new Argument(this.getArgumentType(parameter), parameter.getId().getName()));
            }
        }
        return new JavaMethod(className, name, isStatic, returnType, null, arguments, method.getBeginLine(), method.getEndLine());
    }
    
    private ArgumentType getArgumentType(final Parameter parameter) {
        final String[] typeTokens = parameter.getType().toString().split("\\.");
        String type = typeTokens[typeTokens.length - 1];
        int arrayDim = 0;
        for (int i = 0; i < type.length(); ++i) {
            if (type.charAt(i) == '[') {
                ++arrayDim;
            }
        }
        type = type.replace("[", "").replace("]", "");
        if (arrayDim >= 1) {
            if (arrayDim > 1) {
                return ArgumentType.ObjectArray;
            }
            final ArgumentType arrayType = RobustJavaMethodParser.arrayTypes.get(type);
            if (arrayType == null) {
                return ArgumentType.ObjectArray;
            }
            return arrayType;
        }
        else {
            if (RobustJavaMethodParser.plainOldDataTypes.containsKey(type)) {
                return RobustJavaMethodParser.plainOldDataTypes.get(type);
            }
            if (RobustJavaMethodParser.bufferTypes.containsKey(type)) {
                return RobustJavaMethodParser.bufferTypes.get(type);
            }
            if (RobustJavaMethodParser.otherTypes.containsKey(type)) {
                return RobustJavaMethodParser.otherTypes.get(type);
            }
            return ArgumentType.Object;
        }
    }
    
    private TypeDeclaration getOuterClass(final CompilationUnit unit) {
        for (final TypeDeclaration type : unit.getTypes()) {
            if (type instanceof ClassOrInterfaceDeclaration || type instanceof EnumDeclaration) {
                return type;
            }
        }
        throw new RuntimeException("Couldn't find class, is your java file empty?");
    }
    
    private ArrayList<JniSection> getJniSections(final String classFile) {
        final ArrayList<JniSection> sections = this.getComments(classFile);
        final Iterator<JniSection> iter = sections.iterator();
        while (iter.hasNext()) {
            final JniSection section = iter.next();
            if (!section.getNativeCode().startsWith("JNI")) {
                iter.remove();
            }
            else {
                section.setNativeCode(section.getNativeCode().substring(3));
            }
        }
        return sections;
    }
    
    private ArrayList<JniSection> getNativeCodeBodies(final String classFile) {
        final ArrayList<JniSection> sections = this.getComments(classFile);
        final Iterator<JniSection> iter = sections.iterator();
        while (iter.hasNext()) {
            final JniSection section = iter.next();
            if (section.getNativeCode().startsWith("JNI")) {
                iter.remove();
            }
            if (section.getNativeCode().startsWith("-{")) {
                iter.remove();
            }
            if (!RobustJavaMethodParser.CustomIgnoreTag.isEmpty() && section.getNativeCode().startsWith(RobustJavaMethodParser.CustomIgnoreTag)) {
                iter.remove();
            }
        }
        return sections;
    }
    
    private ArrayList<JniSection> getComments(final String classFile) {
        final ArrayList<JniSection> sections = new ArrayList<JniSection>();
        boolean inComment = false;
        int start = 0;
        int startLine = 0;
        int line = 1;
        for (int i = 0; i < classFile.length() - 2; ++i) {
            final char c1 = classFile.charAt(i);
            final char c2 = classFile.charAt(i + 1);
            final char c3 = classFile.charAt(i + 2);
            if (c1 == '\n') {
                ++line;
            }
            if (!inComment) {
                if (c1 == '/' && c2 == '*' && c3 != '*') {
                    inComment = true;
                    start = i;
                    startLine = line;
                }
            }
            else if (c1 == '*' && c2 == '/') {
                sections.add(new JniSection(classFile.substring(start + 2, i), startLine, line));
                inComment = false;
            }
        }
        return sections;
    }
}

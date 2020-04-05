// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.Iterator;
import java.io.InputStream;
import java.util.ArrayList;
import com.badlogic.gdx.jnigen.parsing.JniHeaderCMethodParser;
import com.badlogic.gdx.jnigen.parsing.RobustJavaMethodParser;
import com.badlogic.gdx.jnigen.parsing.CMethodParser;
import com.badlogic.gdx.jnigen.parsing.JavaMethodParser;

public class NativeCodeGenerator
{
    private static final String JNI_METHOD_MARKER = "native";
    private static final String JNI_ARG_PREFIX = "obj_";
    private static final String JNI_RETURN_VALUE = "JNI_returnValue";
    private static final String JNI_WRAPPER_PREFIX = "wrapped_";
    FileDescriptor sourceDir;
    String classpath;
    FileDescriptor jniDir;
    String[] includes;
    String[] excludes;
    AntPathMatcher matcher;
    JavaMethodParser javaMethodParser;
    CMethodParser cMethodParser;
    CMethodParser.CMethodParserResult cResult;
    
    public NativeCodeGenerator() {
        this.matcher = new AntPathMatcher();
        this.javaMethodParser = new RobustJavaMethodParser();
        this.cMethodParser = new JniHeaderCMethodParser();
    }
    
    public void generate() throws Exception {
        this.generate("src", "bin", "jni", null, null);
    }
    
    public void generate(final String sourceDir, final String classpath, final String jniDir) throws Exception {
        this.generate(sourceDir, classpath, jniDir, null, null);
    }
    
    public void generate(final String sourceDir, final String classpath, final String jniDir, final String[] includes, final String[] excludes) throws Exception {
        this.sourceDir = new FileDescriptor(sourceDir);
        this.jniDir = new FileDescriptor(jniDir);
        this.classpath = classpath;
        this.includes = includes;
        this.excludes = excludes;
        if (!this.sourceDir.exists()) {
            throw new Exception("Java source directory '" + sourceDir + "' does not exist");
        }
        if (!this.jniDir.exists() && !this.jniDir.mkdirs()) {
            throw new Exception("Couldn't create JNI directory '" + jniDir + "'");
        }
        this.processDirectory(this.sourceDir);
    }
    
    private void processDirectory(final FileDescriptor dir) throws Exception {
        final FileDescriptor[] files = dir.list();
        FileDescriptor[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final FileDescriptor file = array[i];
            if (file.isDirectory()) {
                if (!file.path().contains(".svn")) {
                    if (this.excludes == null || !this.matcher.match(file.path(), this.excludes)) {
                        this.processDirectory(file);
                    }
                }
            }
            else if (file.extension().equals("java")) {
                if (!file.name().contains("NativeCodeGenerator")) {
                    if (this.includes == null || this.matcher.match(file.path(), this.includes)) {
                        if (this.excludes == null || !this.matcher.match(file.path(), this.excludes)) {
                            final String className = this.getFullyQualifiedClassName(file);
                            final FileDescriptor hFile = new FileDescriptor(String.valueOf(this.jniDir.path()) + "/" + className + ".h");
                            final FileDescriptor cppFile = new FileDescriptor(this.jniDir + "/" + className + ".cpp");
                            if (file.lastModified() < cppFile.lastModified()) {
                                System.out.println("C/C++ for '" + file.path() + "' up to date");
                            }
                            else {
                                final String javaContent = file.readString();
                                if (javaContent.contains("native")) {
                                    final ArrayList<JavaMethodParser.JavaSegment> javaSegments = this.javaMethodParser.parse(javaContent);
                                    if (javaSegments.size() == 0) {
                                        System.out.println("Skipping '" + file + "', no JNI code found.");
                                    }
                                    else {
                                        System.out.print("Generating C/C++ for '" + file + "'...");
                                        this.generateHFile(file);
                                        this.generateCppFile(javaSegments, hFile, cppFile);
                                        System.out.println("done");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private String getFullyQualifiedClassName(final FileDescriptor file) {
        String className = file.path().replace(this.sourceDir.path(), "").replace('\\', '.').replace('/', '.').replace(".java", "");
        if (className.startsWith(".")) {
            className = className.substring(1);
        }
        return className;
    }
    
    private void generateHFile(final FileDescriptor file) throws Exception {
        final String className = this.getFullyQualifiedClassName(file);
        final String command = "javah -classpath " + this.classpath + " -o " + this.jniDir.path() + "/" + className + ".h " + className;
        final Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        if (process.exitValue() != 0) {
            System.out.println();
            System.out.println("Command: " + command);
            final InputStream errorStream = process.getErrorStream();
            int c = 0;
            while ((c = errorStream.read()) != -1) {
                System.out.print((char)c);
            }
        }
    }
    
    protected void emitHeaderInclude(final StringBuffer buffer, final String fileName) {
        buffer.append("#include <" + fileName + ">\n");
    }
    
    private void generateCppFile(final ArrayList<JavaMethodParser.JavaSegment> javaSegments, final FileDescriptor hFile, final FileDescriptor cppFile) throws Exception {
        final String headerFileContent = hFile.readString();
        final ArrayList<CMethodParser.CMethod> cMethods = this.cMethodParser.parse(headerFileContent).getMethods();
        final StringBuffer buffer = new StringBuffer();
        this.emitHeaderInclude(buffer, hFile.name());
        for (final JavaMethodParser.JavaSegment segment : javaSegments) {
            if (segment instanceof JavaMethodParser.JniSection) {
                this.emitJniSection(buffer, (JavaMethodParser.JniSection)segment);
            }
            if (segment instanceof JavaMethodParser.JavaMethod) {
                final JavaMethodParser.JavaMethod javaMethod = (JavaMethodParser.JavaMethod)segment;
                if (javaMethod.getNativeCode() == null) {
                    throw new RuntimeException("Method '" + javaMethod.getName() + "' has no body");
                }
                final CMethodParser.CMethod cMethod = this.findCMethod(javaMethod, cMethods);
                if (cMethod == null) {
                    throw new RuntimeException("Couldn't find C method for Java method '" + (Object)javaMethod.getClassName() + "#" + javaMethod.getName() + "'");
                }
                this.emitJavaMethod(buffer, javaMethod, cMethod);
            }
        }
        cppFile.writeString(buffer.toString(), false, "UTF-8");
    }
    
    private CMethodParser.CMethod findCMethod(final JavaMethodParser.JavaMethod javaMethod, final ArrayList<CMethodParser.CMethod> cMethods) {
        for (final CMethodParser.CMethod cMethod : cMethods) {
            final String javaMethodName = javaMethod.getName().replace("_", "_1");
            final String javaClassName = javaMethod.getClassName().toString().replace("_", "_1");
            if ((cMethod.getHead().endsWith(String.valueOf(javaClassName) + "_" + javaMethodName) || cMethod.getHead().contains(String.valueOf(javaClassName) + "_" + javaMethodName + "__")) && cMethod.getArgumentTypes().length - 2 == javaMethod.getArguments().size()) {
                boolean match = true;
                for (int i = 2; i < cMethod.getArgumentTypes().length; ++i) {
                    final String cType = cMethod.getArgumentTypes()[i];
                    final String javaType = javaMethod.getArguments().get(i - 2).getType().getJniType();
                    if (!cType.equals(javaType)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return cMethod;
                }
                continue;
            }
        }
        return null;
    }
    
    private void emitLineMarker(final StringBuffer buffer, final int line) {
        buffer.append("\n//@line:");
        buffer.append(line);
        buffer.append("\n");
    }
    
    private void emitJniSection(final StringBuffer buffer, final JavaMethodParser.JniSection section) {
        this.emitLineMarker(buffer, section.getStartIndex());
        buffer.append(section.getNativeCode().replace("\r", ""));
    }
    
    private void emitJavaMethod(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod, final CMethodParser.CMethod cMethod) {
        final StringBuffer jniSetupCode = new StringBuffer();
        final StringBuffer jniCleanupCode = new StringBuffer();
        final StringBuffer additionalArgs = new StringBuffer();
        final StringBuffer wrapperArgs = new StringBuffer();
        this.emitJniSetupCode(jniSetupCode, javaMethod, additionalArgs, wrapperArgs);
        this.emitJniCleanupCode(jniCleanupCode, javaMethod, cMethod);
        final boolean isManual = javaMethod.isManual();
        if (javaMethod.hasDisposableArgument() && javaMethod.getNativeCode().contains("return")) {
            if (isManual) {
                this.emitMethodSignature(buffer, javaMethod, cMethod, null, false);
                this.emitMethodBody(buffer, javaMethod);
                buffer.append("}\n\n");
            }
            else {
                final String wrappedMethodName = this.emitMethodSignature(buffer, javaMethod, cMethod, additionalArgs.toString());
                this.emitMethodBody(buffer, javaMethod);
                buffer.append("}\n\n");
                this.emitMethodSignature(buffer, javaMethod, cMethod, null);
                if (!isManual) {
                    buffer.append(jniSetupCode);
                }
                if (cMethod.getReturnType().equals("void")) {
                    buffer.append("\t" + wrappedMethodName + "(" + wrapperArgs.toString() + ");\n\n");
                    if (!isManual) {
                        buffer.append(jniCleanupCode);
                    }
                    buffer.append("\treturn;\n");
                }
                else {
                    buffer.append("\t" + cMethod.getReturnType() + " " + "JNI_returnValue" + " = " + wrappedMethodName + "(" + wrapperArgs.toString() + ");\n\n");
                    if (!isManual) {
                        buffer.append(jniCleanupCode);
                    }
                    buffer.append("\treturn JNI_returnValue;\n");
                }
                buffer.append("}\n\n");
            }
        }
        else {
            this.emitMethodSignature(buffer, javaMethod, cMethod, null);
            if (!isManual) {
                buffer.append(jniSetupCode);
            }
            this.emitMethodBody(buffer, javaMethod);
            if (!isManual) {
                buffer.append(jniCleanupCode);
            }
            buffer.append("}\n\n");
        }
    }
    
    protected void emitMethodBody(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod) {
        this.emitLineMarker(buffer, javaMethod.getEndIndex());
        buffer.append(javaMethod.getNativeCode());
        buffer.append("\n");
    }
    
    private String emitMethodSignature(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod, final CMethodParser.CMethod cMethod, final String additionalArguments) {
        return this.emitMethodSignature(buffer, javaMethod, cMethod, additionalArguments, true);
    }
    
    private String emitMethodSignature(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod, final CMethodParser.CMethod cMethod, final String additionalArguments, final boolean appendPrefix) {
        String wrappedMethodName = null;
        if (additionalArguments != null) {
            final String[] tokens = cMethod.getHead().replace("\r\n", "").replace("\n", "").split(" ");
            wrappedMethodName = "wrapped_" + tokens[3];
            buffer.append("static inline ");
            buffer.append(tokens[1]);
            buffer.append(" ");
            buffer.append(wrappedMethodName);
            buffer.append("\n");
        }
        else {
            buffer.append(cMethod.getHead());
        }
        if (javaMethod.isStatic()) {
            buffer.append("(JNIEnv* env, jclass clazz");
        }
        else {
            buffer.append("(JNIEnv* env, jobject object");
        }
        if (javaMethod.getArguments().size() > 0) {
            buffer.append(", ");
        }
        for (int i = 0; i < javaMethod.getArguments().size(); ++i) {
            buffer.append(cMethod.getArgumentTypes()[i + 2]);
            buffer.append(" ");
            final JavaMethodParser.Argument javaArg = javaMethod.getArguments().get(i);
            if (!javaArg.getType().isPlainOldDataType() && !javaArg.getType().isObject() && appendPrefix) {
                buffer.append("obj_");
            }
            buffer.append(javaArg.getName());
            if (i < javaMethod.getArguments().size() - 1) {
                buffer.append(", ");
            }
        }
        if (additionalArguments != null) {
            buffer.append(additionalArguments);
        }
        buffer.append(") {\n");
        return wrappedMethodName;
    }
    
    private void emitJniSetupCode(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod, final StringBuffer additionalArgs, final StringBuffer wrapperArgs) {
        if (javaMethod.isStatic()) {
            wrapperArgs.append("env, clazz, ");
        }
        else {
            wrapperArgs.append("env, object, ");
        }
        for (int i = 0; i < javaMethod.getArguments().size(); ++i) {
            final JavaMethodParser.Argument arg = javaMethod.getArguments().get(i);
            if (!arg.getType().isPlainOldDataType() && !arg.getType().isObject()) {
                wrapperArgs.append("obj_");
            }
            wrapperArgs.append(arg.getName());
            if (i < javaMethod.getArguments().size() - 1) {
                wrapperArgs.append(", ");
            }
        }
        for (final JavaMethodParser.Argument arg2 : javaMethod.getArguments()) {
            if (arg2.getType().isBuffer()) {
                final String type = arg2.getType().getBufferCType();
                buffer.append("\t" + type + " " + arg2.getName() + " = (" + type + ")(" + "obj_" + arg2.getName() + "?env->GetDirectBufferAddress(" + "obj_" + arg2.getName() + "):0);\n");
                additionalArgs.append(", ");
                additionalArgs.append(type);
                additionalArgs.append(" ");
                additionalArgs.append(arg2.getName());
                wrapperArgs.append(", ");
                wrapperArgs.append(arg2.getName());
            }
        }
        for (final JavaMethodParser.Argument arg2 : javaMethod.getArguments()) {
            if (arg2.getType().isString()) {
                final String type = "char*";
                buffer.append("\t" + type + " " + arg2.getName() + " = (" + type + ")env->GetStringUTFChars(" + "obj_" + arg2.getName() + ", 0);\n");
                additionalArgs.append(", ");
                additionalArgs.append(type);
                additionalArgs.append(" ");
                additionalArgs.append(arg2.getName());
                wrapperArgs.append(", ");
                wrapperArgs.append(arg2.getName());
            }
        }
        for (final JavaMethodParser.Argument arg2 : javaMethod.getArguments()) {
            if (arg2.getType().isPrimitiveArray()) {
                final String type = arg2.getType().getArrayCType();
                buffer.append("\t" + type + " " + arg2.getName() + " = (" + type + ")env->GetPrimitiveArrayCritical(" + "obj_" + arg2.getName() + ", 0);\n");
                additionalArgs.append(", ");
                additionalArgs.append(type);
                additionalArgs.append(" ");
                additionalArgs.append(arg2.getName());
                wrapperArgs.append(", ");
                wrapperArgs.append(arg2.getName());
            }
        }
        buffer.append("\n");
    }
    
    private void emitJniCleanupCode(final StringBuffer buffer, final JavaMethodParser.JavaMethod javaMethod, final CMethodParser.CMethod cMethod) {
        for (final JavaMethodParser.Argument arg : javaMethod.getArguments()) {
            if (arg.getType().isPrimitiveArray()) {
                buffer.append("\tenv->ReleasePrimitiveArrayCritical(obj_" + arg.getName() + ", " + arg.getName() + ", 0);\n");
            }
        }
        for (final JavaMethodParser.Argument arg : javaMethod.getArguments()) {
            if (arg.getType().isString()) {
                buffer.append("\tenv->ReleaseStringUTFChars(obj_" + arg.getName() + ", " + arg.getName() + ");\n");
            }
        }
        buffer.append("\n");
    }
}

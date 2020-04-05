// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.util.Iterator;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Disposable;

public class ShaderProgram implements Disposable
{
    public static final String POSITION_ATTRIBUTE = "a_position";
    public static final String NORMAL_ATTRIBUTE = "a_normal";
    public static final String COLOR_ATTRIBUTE = "a_color";
    public static final String TEXCOORD_ATTRIBUTE = "a_texCoord";
    public static final String TANGENT_ATTRIBUTE = "a_tangent";
    public static final String BINORMAL_ATTRIBUTE = "a_binormal";
    public static final String BONEWEIGHT_ATTRIBUTE = "a_boneWeight";
    public static boolean pedantic;
    public static String prependVertexCode;
    public static String prependFragmentCode;
    private static final ObjectMap<Application, Array<ShaderProgram>> shaders;
    private String log;
    private boolean isCompiled;
    private final ObjectIntMap<String> uniforms;
    private final ObjectIntMap<String> uniformTypes;
    private final ObjectIntMap<String> uniformSizes;
    private String[] uniformNames;
    private final ObjectIntMap<String> attributes;
    private final ObjectIntMap<String> attributeTypes;
    private final ObjectIntMap<String> attributeSizes;
    private String[] attributeNames;
    private int program;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private final FloatBuffer matrix;
    private final String vertexShaderSource;
    private final String fragmentShaderSource;
    private boolean invalidated;
    private int refCount;
    static final IntBuffer intbuf;
    IntBuffer params;
    IntBuffer type;
    
    static {
        ShaderProgram.pedantic = true;
        ShaderProgram.prependVertexCode = "";
        ShaderProgram.prependFragmentCode = "";
        shaders = new ObjectMap<Application, Array<ShaderProgram>>();
        intbuf = BufferUtils.newIntBuffer(1);
    }
    
    public ShaderProgram(String vertexShader, String fragmentShader) {
        this.log = "";
        this.uniforms = new ObjectIntMap<String>();
        this.uniformTypes = new ObjectIntMap<String>();
        this.uniformSizes = new ObjectIntMap<String>();
        this.attributes = new ObjectIntMap<String>();
        this.attributeTypes = new ObjectIntMap<String>();
        this.attributeSizes = new ObjectIntMap<String>();
        this.refCount = 0;
        this.params = BufferUtils.newIntBuffer(1);
        this.type = BufferUtils.newIntBuffer(1);
        if (vertexShader == null) {
            throw new IllegalArgumentException("vertex shader must not be null");
        }
        if (fragmentShader == null) {
            throw new IllegalArgumentException("fragment shader must not be null");
        }
        if (ShaderProgram.prependVertexCode != null && ShaderProgram.prependVertexCode.length() > 0) {
            vertexShader = String.valueOf(ShaderProgram.prependVertexCode) + vertexShader;
        }
        if (ShaderProgram.prependFragmentCode != null && ShaderProgram.prependFragmentCode.length() > 0) {
            fragmentShader = String.valueOf(ShaderProgram.prependFragmentCode) + fragmentShader;
        }
        this.vertexShaderSource = vertexShader;
        this.fragmentShaderSource = fragmentShader;
        this.matrix = BufferUtils.newFloatBuffer(16);
        this.compileShaders(vertexShader, fragmentShader);
        if (this.isCompiled()) {
            this.fetchAttributes();
            this.fetchUniforms();
            this.addManagedShader(Gdx.app, this);
        }
    }
    
    public ShaderProgram(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }
    
    private void compileShaders(final String vertexShader, final String fragmentShader) {
        this.vertexShaderHandle = this.loadShader(35633, vertexShader);
        this.fragmentShaderHandle = this.loadShader(35632, fragmentShader);
        if (this.vertexShaderHandle == -1 || this.fragmentShaderHandle == -1) {
            this.isCompiled = false;
            return;
        }
        this.program = this.linkProgram(this.createProgram());
        if (this.program == -1) {
            this.isCompiled = false;
            return;
        }
        this.isCompiled = true;
    }
    
    private int loadShader(final int type, final String source) {
        final GL20 gl = Gdx.gl20;
        final IntBuffer intbuf = BufferUtils.newIntBuffer(1);
        final int shader = gl.glCreateShader(type);
        if (shader == 0) {
            return -1;
        }
        gl.glShaderSource(shader, source);
        gl.glCompileShader(shader);
        gl.glGetShaderiv(shader, 35713, intbuf);
        final int compiled = intbuf.get(0);
        if (compiled == 0) {
            final String infoLog = gl.glGetShaderInfoLog(shader);
            this.log = String.valueOf(this.log) + ((type == 35633) ? "Vertex shader\n" : "Fragment shader:\n");
            this.log = String.valueOf(this.log) + infoLog;
            return -1;
        }
        return shader;
    }
    
    protected int createProgram() {
        final GL20 gl = Gdx.gl20;
        final int program = gl.glCreateProgram();
        return (program != 0) ? program : -1;
    }
    
    private int linkProgram(final int program) {
        final GL20 gl = Gdx.gl20;
        if (program == -1) {
            return -1;
        }
        gl.glAttachShader(program, this.vertexShaderHandle);
        gl.glAttachShader(program, this.fragmentShaderHandle);
        gl.glLinkProgram(program);
        final ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        final IntBuffer intbuf = tmp.asIntBuffer();
        gl.glGetProgramiv(program, 35714, intbuf);
        final int linked = intbuf.get(0);
        if (linked == 0) {
            this.log = Gdx.gl20.glGetProgramInfoLog(program);
            return -1;
        }
        return program;
    }
    
    public String getLog() {
        if (this.isCompiled) {
            return this.log = Gdx.gl20.glGetProgramInfoLog(this.program);
        }
        return this.log;
    }
    
    public boolean isCompiled() {
        return this.isCompiled;
    }
    
    private int fetchAttributeLocation(final String name) {
        final GL20 gl = Gdx.gl20;
        int location;
        if ((location = this.attributes.get(name, -2)) == -2) {
            location = gl.glGetAttribLocation(this.program, name);
            this.attributes.put(name, location);
        }
        return location;
    }
    
    private int fetchUniformLocation(final String name) {
        return this.fetchUniformLocation(name, ShaderProgram.pedantic);
    }
    
    public int fetchUniformLocation(final String name, final boolean pedantic) {
        final GL20 gl = Gdx.gl20;
        int location;
        if ((location = this.uniforms.get(name, -2)) == -2) {
            location = gl.glGetUniformLocation(this.program, name);
            if (location == -1 && pedantic) {
                throw new IllegalArgumentException("no uniform with name '" + name + "' in shader");
            }
            this.uniforms.put(name, location);
        }
        return location;
    }
    
    public void setUniformi(final String name, final int value) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform1i(location, value);
    }
    
    public void setUniformi(final int location, final int value) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform1i(location, value);
    }
    
    public void setUniformi(final String name, final int value1, final int value2) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform2i(location, value1, value2);
    }
    
    public void setUniformi(final int location, final int value1, final int value2) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform2i(location, value1, value2);
    }
    
    public void setUniformi(final String name, final int value1, final int value2, final int value3) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform3i(location, value1, value2, value3);
    }
    
    public void setUniformi(final int location, final int value1, final int value2, final int value3) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform3i(location, value1, value2, value3);
    }
    
    public void setUniformi(final String name, final int value1, final int value2, final int value3, final int value4) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform4i(location, value1, value2, value3, value4);
    }
    
    public void setUniformi(final int location, final int value1, final int value2, final int value3, final int value4) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform4i(location, value1, value2, value3, value4);
    }
    
    public void setUniformf(final String name, final float value) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform1f(location, value);
    }
    
    public void setUniformf(final int location, final float value) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform1f(location, value);
    }
    
    public void setUniformf(final String name, final float value1, final float value2) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform2f(location, value1, value2);
    }
    
    public void setUniformf(final int location, final float value1, final float value2) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform2f(location, value1, value2);
    }
    
    public void setUniformf(final String name, final float value1, final float value2, final float value3) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform3f(location, value1, value2, value3);
    }
    
    public void setUniformf(final int location, final float value1, final float value2, final float value3) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform3f(location, value1, value2, value3);
    }
    
    public void setUniformf(final String name, final float value1, final float value2, final float value3, final float value4) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform4f(location, value1, value2, value3, value4);
    }
    
    public void setUniformf(final int location, final float value1, final float value2, final float value3, final float value4) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform4f(location, value1, value2, value3, value4);
    }
    
    public void setUniform1fv(final String name, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform1fv(location, length, values, offset);
    }
    
    public void setUniform1fv(final int location, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform1fv(location, length, values, offset);
    }
    
    public void setUniform2fv(final String name, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform2fv(location, length / 2, values, offset);
    }
    
    public void setUniform2fv(final int location, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform2fv(location, length / 2, values, offset);
    }
    
    public void setUniform3fv(final String name, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform3fv(location, length / 3, values, offset);
    }
    
    public void setUniform3fv(final int location, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform3fv(location, length / 3, values, offset);
    }
    
    public void setUniform4fv(final String name, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchUniformLocation(name);
        gl.glUniform4fv(location, length / 4, values, offset);
    }
    
    public void setUniform4fv(final int location, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniform4fv(location, length / 4, values, offset);
    }
    
    public void setUniformMatrix(final String name, final Matrix4 matrix) {
        this.setUniformMatrix(name, matrix, false);
    }
    
    public void setUniformMatrix(final String name, final Matrix4 matrix, final boolean transpose) {
        this.setUniformMatrix(this.fetchUniformLocation(name), matrix, transpose);
    }
    
    public void setUniformMatrix(final int location, final Matrix4 matrix) {
        this.setUniformMatrix(location, matrix, false);
    }
    
    public void setUniformMatrix(final int location, final Matrix4 matrix, final boolean transpose) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniformMatrix4fv(location, 1, transpose, matrix.val, 0);
    }
    
    public void setUniformMatrix(final String name, final Matrix3 matrix) {
        this.setUniformMatrix(name, matrix, false);
    }
    
    public void setUniformMatrix(final String name, final Matrix3 matrix, final boolean transpose) {
        this.setUniformMatrix(this.fetchUniformLocation(name), matrix, transpose);
    }
    
    public void setUniformMatrix(final int location, final Matrix3 matrix) {
        this.setUniformMatrix(location, matrix, false);
    }
    
    public void setUniformMatrix(final int location, final Matrix3 matrix, final boolean transpose) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniformMatrix3fv(location, 1, transpose, matrix.val, 0);
    }
    
    public void setUniformMatrix3fv(final String name, final FloatBuffer buffer, final int count, final boolean transpose) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        buffer.position(0);
        final int location = this.fetchUniformLocation(name);
        gl.glUniformMatrix3fv(location, count, transpose, buffer);
    }
    
    public void setUniformMatrix4fv(final String name, final FloatBuffer buffer, final int count, final boolean transpose) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        buffer.position(0);
        final int location = this.fetchUniformLocation(name);
        gl.glUniformMatrix4fv(location, count, transpose, buffer);
    }
    
    public void setUniformMatrix4fv(final int location, final float[] values, final int offset, final int length) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUniformMatrix4fv(location, length / 16, false, values, offset);
    }
    
    public void setUniformMatrix4fv(final String name, final float[] values, final int offset, final int length) {
        this.setUniformMatrix4fv(this.fetchUniformLocation(name), values, offset, length);
    }
    
    public void setUniformf(final String name, final Vector2 values) {
        this.setUniformf(name, values.x, values.y);
    }
    
    public void setUniformf(final int location, final Vector2 values) {
        this.setUniformf(location, values.x, values.y);
    }
    
    public void setUniformf(final String name, final Vector3 values) {
        this.setUniformf(name, values.x, values.y, values.z);
    }
    
    public void setUniformf(final int location, final Vector3 values) {
        this.setUniformf(location, values.x, values.y, values.z);
    }
    
    public void setUniformf(final String name, final Color values) {
        this.setUniformf(name, values.r, values.g, values.b, values.a);
    }
    
    public void setUniformf(final int location, final Color values) {
        this.setUniformf(location, values.r, values.g, values.b, values.a);
    }
    
    public void setVertexAttribute(final String name, final int size, final int type, final boolean normalize, final int stride, final Buffer buffer) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchAttributeLocation(name);
        if (location == -1) {
            return;
        }
        gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }
    
    public void setVertexAttribute(final int location, final int size, final int type, final boolean normalize, final int stride, final Buffer buffer) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }
    
    public void setVertexAttribute(final String name, final int size, final int type, final boolean normalize, final int stride, final int offset) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchAttributeLocation(name);
        if (location == -1) {
            return;
        }
        gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }
    
    public void setVertexAttribute(final int location, final int size, final int type, final boolean normalize, final int stride, final int offset) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }
    
    public void begin() {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glUseProgram(this.program);
    }
    
    public void end() {
        final GL20 gl = Gdx.gl20;
        gl.glUseProgram(0);
    }
    
    @Override
    public void dispose() {
        final GL20 gl = Gdx.gl20;
        gl.glUseProgram(0);
        gl.glDeleteShader(this.vertexShaderHandle);
        gl.glDeleteShader(this.fragmentShaderHandle);
        gl.glDeleteProgram(this.program);
        if (ShaderProgram.shaders.get(Gdx.app) != null) {
            ShaderProgram.shaders.get(Gdx.app).removeValue(this, true);
        }
    }
    
    public void disableVertexAttribute(final String name) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchAttributeLocation(name);
        if (location == -1) {
            return;
        }
        gl.glDisableVertexAttribArray(location);
    }
    
    public void disableVertexAttribute(final int location) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glDisableVertexAttribArray(location);
    }
    
    public void enableVertexAttribute(final String name) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        final int location = this.fetchAttributeLocation(name);
        if (location == -1) {
            return;
        }
        gl.glEnableVertexAttribArray(location);
    }
    
    public void enableVertexAttribute(final int location) {
        final GL20 gl = Gdx.gl20;
        this.checkManaged();
        gl.glEnableVertexAttribArray(location);
    }
    
    private void checkManaged() {
        if (this.invalidated) {
            this.compileShaders(this.vertexShaderSource, this.fragmentShaderSource);
            this.invalidated = false;
        }
    }
    
    private void addManagedShader(final Application app, final ShaderProgram shaderProgram) {
        Array<ShaderProgram> managedResources = ShaderProgram.shaders.get(app);
        if (managedResources == null) {
            managedResources = new Array<ShaderProgram>();
        }
        managedResources.add(shaderProgram);
        ShaderProgram.shaders.put(app, managedResources);
    }
    
    public static void invalidateAllShaderPrograms(final Application app) {
        if (Gdx.gl20 == null) {
            return;
        }
        final Array<ShaderProgram> shaderArray = ShaderProgram.shaders.get(app);
        if (shaderArray == null) {
            return;
        }
        for (int i = 0; i < shaderArray.size; ++i) {
            shaderArray.get(i).invalidated = true;
            shaderArray.get(i).checkManaged();
        }
    }
    
    public static void clearAllShaderPrograms(final Application app) {
        ShaderProgram.shaders.remove(app);
    }
    
    public static String getManagedStatus() {
        final StringBuilder builder = new StringBuilder();
        final int i = 0;
        builder.append("Managed shaders/app: { ");
        for (final Application app : ShaderProgram.shaders.keys()) {
            builder.append(ShaderProgram.shaders.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
    
    public static int getNumManagedShaderPrograms() {
        return ShaderProgram.shaders.get(Gdx.app).size;
    }
    
    public void setAttributef(final String name, final float value1, final float value2, final float value3, final float value4) {
        final GL20 gl = Gdx.gl20;
        final int location = this.fetchAttributeLocation(name);
        gl.glVertexAttrib4f(location, value1, value2, value3, value4);
    }
    
    private void fetchUniforms() {
        this.params.clear();
        Gdx.gl20.glGetProgramiv(this.program, 35718, this.params);
        final int numUniforms = this.params.get(0);
        this.uniformNames = new String[numUniforms];
        for (int i = 0; i < numUniforms; ++i) {
            this.params.clear();
            this.params.put(0, 1);
            this.type.clear();
            final String name = Gdx.gl20.glGetActiveUniform(this.program, i, this.params, this.type);
            final int location = Gdx.gl20.glGetUniformLocation(this.program, name);
            this.uniforms.put(name, location);
            this.uniformTypes.put(name, this.type.get(0));
            this.uniformSizes.put(name, this.params.get(0));
            this.uniformNames[i] = name;
        }
    }
    
    private void fetchAttributes() {
        this.params.clear();
        Gdx.gl20.glGetProgramiv(this.program, 35721, this.params);
        final int numAttributes = this.params.get(0);
        this.attributeNames = new String[numAttributes];
        for (int i = 0; i < numAttributes; ++i) {
            this.params.clear();
            this.params.put(0, 1);
            this.type.clear();
            final String name = Gdx.gl20.glGetActiveAttrib(this.program, i, this.params, this.type);
            final int location = Gdx.gl20.glGetAttribLocation(this.program, name);
            this.attributes.put(name, location);
            this.attributeTypes.put(name, this.type.get(0));
            this.attributeSizes.put(name, this.params.get(0));
            this.attributeNames[i] = name;
        }
    }
    
    public boolean hasAttribute(final String name) {
        return this.attributes.containsKey(name);
    }
    
    public int getAttributeType(final String name) {
        return this.attributeTypes.get(name, 0);
    }
    
    public int getAttributeLocation(final String name) {
        return this.attributes.get(name, -1);
    }
    
    public int getAttributeSize(final String name) {
        return this.attributeSizes.get(name, 0);
    }
    
    public boolean hasUniform(final String name) {
        return this.uniforms.containsKey(name);
    }
    
    public int getUniformType(final String name) {
        return this.uniformTypes.get(name, 0);
    }
    
    public int getUniformLocation(final String name) {
        return this.uniforms.get(name, -1);
    }
    
    public int getUniformSize(final String name) {
        return this.uniformSizes.get(name, 0);
    }
    
    public String[] getAttributes() {
        return this.attributeNames;
    }
    
    public String[] getUniforms() {
        return this.uniformNames;
    }
    
    public String getVertexShaderSource() {
        return this.vertexShaderSource;
    }
    
    public String getFragmentShaderSource() {
        return this.fragmentShaderSource;
    }
}

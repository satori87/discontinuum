// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Mesh;

public class ImmediateModeRenderer20 implements ImmediateModeRenderer
{
    private int primitiveType;
    private int vertexIdx;
    private int numSetTexCoords;
    private final int maxVertices;
    private int numVertices;
    private final Mesh mesh;
    private ShaderProgram shader;
    private boolean ownsShader;
    private final int numTexCoords;
    private final int vertexSize;
    private final int normalOffset;
    private final int colorOffset;
    private final int texCoordOffset;
    private final Matrix4 projModelView;
    private final float[] vertices;
    private final String[] shaderUniformNames;
    
    public ImmediateModeRenderer20(final boolean hasNormals, final boolean hasColors, final int numTexCoords) {
        this(5000, hasNormals, hasColors, numTexCoords, createDefaultShader(hasNormals, hasColors, numTexCoords));
        this.ownsShader = true;
    }
    
    public ImmediateModeRenderer20(final int maxVertices, final boolean hasNormals, final boolean hasColors, final int numTexCoords) {
        this(maxVertices, hasNormals, hasColors, numTexCoords, createDefaultShader(hasNormals, hasColors, numTexCoords));
        this.ownsShader = true;
    }
    
    public ImmediateModeRenderer20(final int maxVertices, final boolean hasNormals, final boolean hasColors, final int numTexCoords, final ShaderProgram shader) {
        this.projModelView = new Matrix4();
        this.maxVertices = maxVertices;
        this.numTexCoords = numTexCoords;
        this.shader = shader;
        final VertexAttribute[] attribs = this.buildVertexAttributes(hasNormals, hasColors, numTexCoords);
        this.mesh = new Mesh(false, maxVertices, 0, attribs);
        this.vertices = new float[maxVertices * (this.mesh.getVertexAttributes().vertexSize / 4)];
        this.vertexSize = this.mesh.getVertexAttributes().vertexSize / 4;
        this.normalOffset = ((this.mesh.getVertexAttribute(8) != null) ? (this.mesh.getVertexAttribute(8).offset / 4) : 0);
        this.colorOffset = ((this.mesh.getVertexAttribute(4) != null) ? (this.mesh.getVertexAttribute(4).offset / 4) : 0);
        this.texCoordOffset = ((this.mesh.getVertexAttribute(16) != null) ? (this.mesh.getVertexAttribute(16).offset / 4) : 0);
        this.shaderUniformNames = new String[numTexCoords];
        for (int i = 0; i < numTexCoords; ++i) {
            this.shaderUniformNames[i] = "u_sampler" + i;
        }
    }
    
    private VertexAttribute[] buildVertexAttributes(final boolean hasNormals, final boolean hasColor, final int numTexCoords) {
        final Array<VertexAttribute> attribs = new Array<VertexAttribute>();
        attribs.add(new VertexAttribute(1, 3, "a_position"));
        if (hasNormals) {
            attribs.add(new VertexAttribute(8, 3, "a_normal"));
        }
        if (hasColor) {
            attribs.add(new VertexAttribute(4, 4, "a_color"));
        }
        for (int i = 0; i < numTexCoords; ++i) {
            attribs.add(new VertexAttribute(16, 2, "a_texCoord" + i));
        }
        final VertexAttribute[] array = new VertexAttribute[attribs.size];
        for (int j = 0; j < attribs.size; ++j) {
            array[j] = attribs.get(j);
        }
        return array;
    }
    
    public void setShader(final ShaderProgram shader) {
        if (this.ownsShader) {
            this.shader.dispose();
        }
        this.shader = shader;
        this.ownsShader = false;
    }
    
    @Override
    public void begin(final Matrix4 projModelView, final int primitiveType) {
        this.projModelView.set(projModelView);
        this.primitiveType = primitiveType;
    }
    
    @Override
    public void color(final Color color) {
        this.vertices[this.vertexIdx + this.colorOffset] = color.toFloatBits();
    }
    
    @Override
    public void color(final float r, final float g, final float b, final float a) {
        this.vertices[this.vertexIdx + this.colorOffset] = Color.toFloatBits(r, g, b, a);
    }
    
    @Override
    public void color(final float colorBits) {
        this.vertices[this.vertexIdx + this.colorOffset] = colorBits;
    }
    
    @Override
    public void texCoord(final float u, final float v) {
        final int idx = this.vertexIdx + this.texCoordOffset;
        this.vertices[idx + this.numSetTexCoords] = u;
        this.vertices[idx + this.numSetTexCoords + 1] = v;
        this.numSetTexCoords += 2;
    }
    
    @Override
    public void normal(final float x, final float y, final float z) {
        final int idx = this.vertexIdx + this.normalOffset;
        this.vertices[idx] = x;
        this.vertices[idx + 1] = y;
        this.vertices[idx + 2] = z;
    }
    
    @Override
    public void vertex(final float x, final float y, final float z) {
        final int idx = this.vertexIdx;
        this.vertices[idx] = x;
        this.vertices[idx + 1] = y;
        this.vertices[idx + 2] = z;
        this.numSetTexCoords = 0;
        this.vertexIdx += this.vertexSize;
        ++this.numVertices;
    }
    
    @Override
    public void flush() {
        if (this.numVertices == 0) {
            return;
        }
        this.shader.begin();
        this.shader.setUniformMatrix("u_projModelView", this.projModelView);
        for (int i = 0; i < this.numTexCoords; ++i) {
            this.shader.setUniformi(this.shaderUniformNames[i], i);
        }
        this.mesh.setVertices(this.vertices, 0, this.vertexIdx);
        this.mesh.render(this.shader, this.primitiveType);
        this.shader.end();
        this.numSetTexCoords = 0;
        this.vertexIdx = 0;
        this.numVertices = 0;
    }
    
    @Override
    public void end() {
        this.flush();
    }
    
    @Override
    public int getNumVertices() {
        return this.numVertices;
    }
    
    @Override
    public int getMaxVertices() {
        return this.maxVertices;
    }
    
    @Override
    public void dispose() {
        if (this.ownsShader && this.shader != null) {
            this.shader.dispose();
        }
        this.mesh.dispose();
    }
    
    private static String createVertexShader(final boolean hasNormals, final boolean hasColors, final int numTexCoords) {
        String shader = "attribute vec4 a_position;\n" + (hasNormals ? "attribute vec3 a_normal;\n" : "") + (hasColors ? "attribute vec4 a_color;\n" : "");
        for (int i = 0; i < numTexCoords; ++i) {
            shader = String.valueOf(shader) + "attribute vec2 a_texCoord" + i + ";\n";
        }
        shader = String.valueOf(shader) + "uniform mat4 u_projModelView;\n";
        shader = String.valueOf(shader) + (hasColors ? "varying vec4 v_col;\n" : "");
        for (int i = 0; i < numTexCoords; ++i) {
            shader = String.valueOf(shader) + "varying vec2 v_tex" + i + ";\n";
        }
        shader = String.valueOf(shader) + "void main() {\n   gl_Position = u_projModelView * a_position;\n" + (hasColors ? "   v_col = a_color;\n" : "");
        for (int i = 0; i < numTexCoords; ++i) {
            shader = String.valueOf(shader) + "   v_tex" + i + " = " + "a_texCoord" + i + ";\n";
        }
        shader = String.valueOf(shader) + "   gl_PointSize = 1.0;\n";
        shader = String.valueOf(shader) + "}\n";
        return shader;
    }
    
    private static String createFragmentShader(final boolean hasNormals, final boolean hasColors, final int numTexCoords) {
        String shader = "#ifdef GL_ES\nprecision mediump float;\n#endif\n";
        if (hasColors) {
            shader = String.valueOf(shader) + "varying vec4 v_col;\n";
        }
        for (int i = 0; i < numTexCoords; ++i) {
            shader = String.valueOf(shader) + "varying vec2 v_tex" + i + ";\n";
            shader = String.valueOf(shader) + "uniform sampler2D u_sampler" + i + ";\n";
        }
        shader = String.valueOf(shader) + "void main() {\n   gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
        if (numTexCoords > 0) {
            shader = String.valueOf(shader) + " * ";
        }
        for (int i = 0; i < numTexCoords; ++i) {
            if (i == numTexCoords - 1) {
                shader = String.valueOf(shader) + " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
            }
            else {
                shader = String.valueOf(shader) + " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
            }
        }
        shader = String.valueOf(shader) + ";\n}";
        return shader;
    }
    
    public static ShaderProgram createDefaultShader(final boolean hasNormals, final boolean hasColors, final int numTexCoords) {
        final String vertexShader = createVertexShader(hasNormals, hasColors, numTexCoords);
        final String fragmentShader = createFragmentShader(hasNormals, hasColors, numTexCoords);
        final ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
        return program;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix3;
import java.util.Iterator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.IndexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectWithVAO;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import java.util.Map;
import com.badlogic.gdx.utils.Disposable;

public class Mesh implements Disposable
{
    static final Map<Application, Array<Mesh>> meshes;
    final VertexData vertices;
    final IndexData indices;
    boolean autoBind;
    final boolean isVertexArray;
    private final Vector3 tmpV;
    
    static {
        meshes = new HashMap<Application, Array<Mesh>>();
    }
    
    protected Mesh(final VertexData vertices, final IndexData indices, final boolean isVertexArray) {
        this.autoBind = true;
        this.tmpV = new Vector3();
        this.vertices = vertices;
        this.indices = indices;
        this.isVertexArray = isVertexArray;
        addManagedMesh(Gdx.app, this);
    }
    
    public Mesh(final boolean isStatic, final int maxVertices, final int maxIndices, final VertexAttribute... attributes) {
        this.autoBind = true;
        this.tmpV = new Vector3();
        this.vertices = this.makeVertexBuffer(isStatic, maxVertices, new VertexAttributes(attributes));
        this.indices = new IndexBufferObject(isStatic, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }
    
    public Mesh(final boolean isStatic, final int maxVertices, final int maxIndices, final VertexAttributes attributes) {
        this.autoBind = true;
        this.tmpV = new Vector3();
        this.vertices = this.makeVertexBuffer(isStatic, maxVertices, attributes);
        this.indices = new IndexBufferObject(isStatic, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }
    
    public Mesh(final boolean staticVertices, final boolean staticIndices, final int maxVertices, final int maxIndices, final VertexAttributes attributes) {
        this.autoBind = true;
        this.tmpV = new Vector3();
        this.vertices = this.makeVertexBuffer(staticVertices, maxVertices, attributes);
        this.indices = new IndexBufferObject(staticIndices, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }
    
    private VertexData makeVertexBuffer(final boolean isStatic, final int maxVertices, final VertexAttributes vertexAttributes) {
        if (Gdx.gl30 != null) {
            return new VertexBufferObjectWithVAO(isStatic, maxVertices, vertexAttributes);
        }
        return new VertexBufferObject(isStatic, maxVertices, vertexAttributes);
    }
    
    public Mesh(final VertexDataType type, final boolean isStatic, final int maxVertices, final int maxIndices, final VertexAttribute... attributes) {
        this(type, isStatic, maxVertices, maxIndices, new VertexAttributes(attributes));
    }
    
    public Mesh(final VertexDataType type, final boolean isStatic, final int maxVertices, final int maxIndices, final VertexAttributes attributes) {
        this.autoBind = true;
        this.tmpV = new Vector3();
        switch (type) {
            case VertexBufferObject: {
                this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
                this.indices = new IndexBufferObject(isStatic, maxIndices);
                this.isVertexArray = false;
                break;
            }
            case VertexBufferObjectSubData: {
                this.vertices = new VertexBufferObjectSubData(isStatic, maxVertices, attributes);
                this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
                this.isVertexArray = false;
                break;
            }
            case VertexBufferObjectWithVAO: {
                this.vertices = new VertexBufferObjectWithVAO(isStatic, maxVertices, attributes);
                this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
                this.isVertexArray = false;
                break;
            }
            default: {
                this.vertices = new VertexArray(maxVertices, attributes);
                this.indices = new IndexArray(maxIndices);
                this.isVertexArray = true;
                break;
            }
        }
        addManagedMesh(Gdx.app, this);
    }
    
    public Mesh setVertices(final float[] vertices) {
        this.vertices.setVertices(vertices, 0, vertices.length);
        return this;
    }
    
    public Mesh setVertices(final float[] vertices, final int offset, final int count) {
        this.vertices.setVertices(vertices, offset, count);
        return this;
    }
    
    public Mesh updateVertices(final int targetOffset, final float[] source) {
        return this.updateVertices(targetOffset, source, 0, source.length);
    }
    
    public Mesh updateVertices(final int targetOffset, final float[] source, final int sourceOffset, final int count) {
        this.vertices.updateVertices(targetOffset, source, sourceOffset, count);
        return this;
    }
    
    public float[] getVertices(final float[] vertices) {
        return this.getVertices(0, -1, vertices);
    }
    
    public float[] getVertices(final int srcOffset, final float[] vertices) {
        return this.getVertices(srcOffset, -1, vertices);
    }
    
    public float[] getVertices(final int srcOffset, final int count, final float[] vertices) {
        return this.getVertices(srcOffset, count, vertices, 0);
    }
    
    public float[] getVertices(final int srcOffset, int count, final float[] vertices, final int destOffset) {
        final int max = this.getNumVertices() * this.getVertexSize() / 4;
        if (count == -1) {
            count = max - srcOffset;
            if (count > vertices.length - destOffset) {
                count = vertices.length - destOffset;
            }
        }
        if (srcOffset < 0 || count <= 0 || srcOffset + count > max || destOffset < 0 || destOffset >= vertices.length) {
            throw new IndexOutOfBoundsException();
        }
        if (vertices.length - destOffset < count) {
            throw new IllegalArgumentException("not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
        }
        final int pos = this.getVerticesBuffer().position();
        this.getVerticesBuffer().position(srcOffset);
        this.getVerticesBuffer().get(vertices, destOffset, count);
        this.getVerticesBuffer().position(pos);
        return vertices;
    }
    
    public Mesh setIndices(final short[] indices) {
        this.indices.setIndices(indices, 0, indices.length);
        return this;
    }
    
    public Mesh setIndices(final short[] indices, final int offset, final int count) {
        this.indices.setIndices(indices, offset, count);
        return this;
    }
    
    public void getIndices(final short[] indices) {
        this.getIndices(indices, 0);
    }
    
    public void getIndices(final short[] indices, final int destOffset) {
        this.getIndices(0, indices, destOffset);
    }
    
    public void getIndices(final int srcOffset, final short[] indices, final int destOffset) {
        this.getIndices(srcOffset, -1, indices, destOffset);
    }
    
    public void getIndices(final int srcOffset, int count, final short[] indices, final int destOffset) {
        final int max = this.getNumIndices();
        if (count < 0) {
            count = max - srcOffset;
        }
        if (srcOffset < 0 || srcOffset >= max || srcOffset + count > max) {
            throw new IllegalArgumentException("Invalid range specified, offset: " + srcOffset + ", count: " + count + ", max: " + max);
        }
        if (indices.length - destOffset < count) {
            throw new IllegalArgumentException("not enough room in indices array, has " + indices.length + " shorts, needs " + count);
        }
        final int pos = this.getIndicesBuffer().position();
        this.getIndicesBuffer().position(srcOffset);
        this.getIndicesBuffer().get(indices, destOffset, count);
        this.getIndicesBuffer().position(pos);
    }
    
    public int getNumIndices() {
        return this.indices.getNumIndices();
    }
    
    public int getNumVertices() {
        return this.vertices.getNumVertices();
    }
    
    public int getMaxVertices() {
        return this.vertices.getNumMaxVertices();
    }
    
    public int getMaxIndices() {
        return this.indices.getNumMaxIndices();
    }
    
    public int getVertexSize() {
        return this.vertices.getAttributes().vertexSize;
    }
    
    public void setAutoBind(final boolean autoBind) {
        this.autoBind = autoBind;
    }
    
    public void bind(final ShaderProgram shader) {
        this.bind(shader, null);
    }
    
    public void bind(final ShaderProgram shader, final int[] locations) {
        this.vertices.bind(shader, locations);
        if (this.indices.getNumIndices() > 0) {
            this.indices.bind();
        }
    }
    
    public void unbind(final ShaderProgram shader) {
        this.unbind(shader, null);
    }
    
    public void unbind(final ShaderProgram shader, final int[] locations) {
        this.vertices.unbind(shader, locations);
        if (this.indices.getNumIndices() > 0) {
            this.indices.unbind();
        }
    }
    
    public void render(final ShaderProgram shader, final int primitiveType) {
        this.render(shader, primitiveType, 0, (this.indices.getNumMaxIndices() > 0) ? this.getNumIndices() : this.getNumVertices(), this.autoBind);
    }
    
    public void render(final ShaderProgram shader, final int primitiveType, final int offset, final int count) {
        this.render(shader, primitiveType, offset, count, this.autoBind);
    }
    
    public void render(final ShaderProgram shader, final int primitiveType, final int offset, final int count, final boolean autoBind) {
        if (count == 0) {
            return;
        }
        if (autoBind) {
            this.bind(shader);
        }
        if (this.isVertexArray) {
            if (this.indices.getNumIndices() > 0) {
                final ShortBuffer buffer = this.indices.getBuffer();
                final int oldPosition = buffer.position();
                final int oldLimit = buffer.limit();
                buffer.position(offset);
                buffer.limit(offset + count);
                Gdx.gl20.glDrawElements(primitiveType, count, 5123, buffer);
                buffer.position(oldPosition);
                buffer.limit(oldLimit);
            }
            else {
                Gdx.gl20.glDrawArrays(primitiveType, offset, count);
            }
        }
        else if (this.indices.getNumIndices() > 0) {
            if (count + offset > this.indices.getNumMaxIndices()) {
                throw new GdxRuntimeException("Mesh attempting to access memory outside of the index buffer (count: " + count + ", offset: " + offset + ", max: " + this.indices.getNumMaxIndices() + ")");
            }
            Gdx.gl20.glDrawElements(primitiveType, count, 5123, offset * 2);
        }
        else {
            Gdx.gl20.glDrawArrays(primitiveType, offset, count);
        }
        if (autoBind) {
            this.unbind(shader);
        }
    }
    
    @Override
    public void dispose() {
        if (Mesh.meshes.get(Gdx.app) != null) {
            Mesh.meshes.get(Gdx.app).removeValue(this, true);
        }
        this.vertices.dispose();
        this.indices.dispose();
    }
    
    public VertexAttribute getVertexAttribute(final int usage) {
        final VertexAttributes attributes = this.vertices.getAttributes();
        for (int len = attributes.size(), i = 0; i < len; ++i) {
            if (attributes.get(i).usage == usage) {
                return attributes.get(i);
            }
        }
        return null;
    }
    
    public VertexAttributes getVertexAttributes() {
        return this.vertices.getAttributes();
    }
    
    public FloatBuffer getVerticesBuffer() {
        return this.vertices.getBuffer();
    }
    
    public BoundingBox calculateBoundingBox() {
        final BoundingBox bbox = new BoundingBox();
        this.calculateBoundingBox(bbox);
        return bbox;
    }
    
    public void calculateBoundingBox(final BoundingBox bbox) {
        final int numVertices = this.getNumVertices();
        if (numVertices == 0) {
            throw new GdxRuntimeException("No vertices defined");
        }
        final FloatBuffer verts = this.vertices.getBuffer();
        bbox.inf();
        final VertexAttribute posAttrib = this.getVertexAttribute(1);
        final int offset = posAttrib.offset / 4;
        final int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        int idx = offset;
        switch (posAttrib.numComponents) {
            case 1: {
                for (int i = 0; i < numVertices; ++i) {
                    bbox.ext(verts.get(idx), 0.0f, 0.0f);
                    idx += vertexSize;
                }
                break;
            }
            case 2: {
                for (int i = 0; i < numVertices; ++i) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), 0.0f);
                    idx += vertexSize;
                }
                break;
            }
            case 3: {
                for (int i = 0; i < numVertices; ++i) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                    idx += vertexSize;
                }
                break;
            }
        }
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out, final int offset, final int count) {
        return this.extendBoundingBox(out.inf(), offset, count);
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out, final int offset, final int count, final Matrix4 transform) {
        return this.extendBoundingBox(out.inf(), offset, count, transform);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out, final int offset, final int count) {
        return this.extendBoundingBox(out, offset, count, null);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out, final int offset, final int count, final Matrix4 transform) {
        final int numIndices = this.getNumIndices();
        final int numVertices = this.getNumVertices();
        final int max = (numIndices == 0) ? numVertices : numIndices;
        if (offset < 0 || count < 1 || offset + count > max) {
            throw new GdxRuntimeException("Invalid part specified ( offset=" + offset + ", count=" + count + ", max=" + max + " )");
        }
        final FloatBuffer verts = this.vertices.getBuffer();
        final ShortBuffer index = this.indices.getBuffer();
        final VertexAttribute posAttrib = this.getVertexAttribute(1);
        final int posoff = posAttrib.offset / 4;
        final int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        final int end = offset + count;
        switch (posAttrib.numComponents) {
            case 1: {
                if (numIndices > 0) {
                    for (int i = offset; i < end; ++i) {
                        final int idx = index.get(i) * vertexSize + posoff;
                        this.tmpV.set(verts.get(idx), 0.0f, 0.0f);
                        if (transform != null) {
                            this.tmpV.mul(transform);
                        }
                        out.ext(this.tmpV);
                    }
                    break;
                }
                for (int i = offset; i < end; ++i) {
                    final int idx = i * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), 0.0f, 0.0f);
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    out.ext(this.tmpV);
                }
                break;
            }
            case 2: {
                if (numIndices > 0) {
                    for (int i = offset; i < end; ++i) {
                        final int idx = index.get(i) * vertexSize + posoff;
                        this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0f);
                        if (transform != null) {
                            this.tmpV.mul(transform);
                        }
                        out.ext(this.tmpV);
                    }
                    break;
                }
                for (int i = offset; i < end; ++i) {
                    final int idx = i * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0f);
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    out.ext(this.tmpV);
                }
                break;
            }
            case 3: {
                if (numIndices > 0) {
                    for (int i = offset; i < end; ++i) {
                        final int idx = index.get(i) * vertexSize + posoff;
                        this.tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                        if (transform != null) {
                            this.tmpV.mul(transform);
                        }
                        out.ext(this.tmpV);
                    }
                    break;
                }
                for (int i = offset; i < end; ++i) {
                    final int idx = i * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    out.ext(this.tmpV);
                }
                break;
            }
        }
        return out;
    }
    
    public float calculateRadiusSquared(final float centerX, final float centerY, final float centerZ, final int offset, final int count, final Matrix4 transform) {
        final int numIndices = this.getNumIndices();
        if (offset < 0 || count < 1 || offset + count > numIndices) {
            throw new GdxRuntimeException("Not enough indices");
        }
        final FloatBuffer verts = this.vertices.getBuffer();
        final ShortBuffer index = this.indices.getBuffer();
        final VertexAttribute posAttrib = this.getVertexAttribute(1);
        final int posoff = posAttrib.offset / 4;
        final int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        final int end = offset + count;
        float result = 0.0f;
        switch (posAttrib.numComponents) {
            case 1: {
                for (int i = offset; i < end; ++i) {
                    final int idx = index.get(i) * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), 0.0f, 0.0f);
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    final float r = this.tmpV.sub(centerX, centerY, centerZ).len2();
                    if (r > result) {
                        result = r;
                    }
                }
                break;
            }
            case 2: {
                for (int i = offset; i < end; ++i) {
                    final int idx = index.get(i) * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0f);
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    final float r = this.tmpV.sub(centerX, centerY, centerZ).len2();
                    if (r > result) {
                        result = r;
                    }
                }
                break;
            }
            case 3: {
                for (int i = offset; i < end; ++i) {
                    final int idx = index.get(i) * vertexSize + posoff;
                    this.tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                    if (transform != null) {
                        this.tmpV.mul(transform);
                    }
                    final float r = this.tmpV.sub(centerX, centerY, centerZ).len2();
                    if (r > result) {
                        result = r;
                    }
                }
                break;
            }
        }
        return result;
    }
    
    public float calculateRadius(final float centerX, final float centerY, final float centerZ, final int offset, final int count, final Matrix4 transform) {
        return (float)Math.sqrt(this.calculateRadiusSquared(centerX, centerY, centerZ, offset, count, transform));
    }
    
    public float calculateRadius(final Vector3 center, final int offset, final int count, final Matrix4 transform) {
        return this.calculateRadius(center.x, center.y, center.z, offset, count, transform);
    }
    
    public float calculateRadius(final float centerX, final float centerY, final float centerZ, final int offset, final int count) {
        return this.calculateRadius(centerX, centerY, centerZ, offset, count, null);
    }
    
    public float calculateRadius(final Vector3 center, final int offset, final int count) {
        return this.calculateRadius(center.x, center.y, center.z, offset, count, null);
    }
    
    public float calculateRadius(final float centerX, final float centerY, final float centerZ) {
        return this.calculateRadius(centerX, centerY, centerZ, 0, this.getNumIndices(), null);
    }
    
    public float calculateRadius(final Vector3 center) {
        return this.calculateRadius(center.x, center.y, center.z, 0, this.getNumIndices(), null);
    }
    
    public ShortBuffer getIndicesBuffer() {
        return this.indices.getBuffer();
    }
    
    private static void addManagedMesh(final Application app, final Mesh mesh) {
        Array<Mesh> managedResources = Mesh.meshes.get(app);
        if (managedResources == null) {
            managedResources = new Array<Mesh>();
        }
        managedResources.add(mesh);
        Mesh.meshes.put(app, managedResources);
    }
    
    public static void invalidateAllMeshes(final Application app) {
        final Array<Mesh> meshesArray = Mesh.meshes.get(app);
        if (meshesArray == null) {
            return;
        }
        for (int i = 0; i < meshesArray.size; ++i) {
            meshesArray.get(i).vertices.invalidate();
            meshesArray.get(i).indices.invalidate();
        }
    }
    
    public static void clearAllMeshes(final Application app) {
        Mesh.meshes.remove(app);
    }
    
    public static String getManagedStatus() {
        final StringBuilder builder = new StringBuilder();
        final int i = 0;
        builder.append("Managed meshes/app: { ");
        for (final Application app : Mesh.meshes.keySet()) {
            builder.append(Mesh.meshes.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
    
    public void scale(final float scaleX, final float scaleY, final float scaleZ) {
        final VertexAttribute posAttr = this.getVertexAttribute(1);
        final int offset = posAttr.offset / 4;
        final int numComponents = posAttr.numComponents;
        final int numVertices = this.getNumVertices();
        final int vertexSize = this.getVertexSize() / 4;
        final float[] vertices = new float[numVertices * vertexSize];
        this.getVertices(vertices);
        int idx = offset;
        switch (numComponents) {
            case 1: {
                for (int i = 0; i < numVertices; ++i) {
                    final float[] array = vertices;
                    final int n = idx;
                    array[n] *= scaleX;
                    idx += vertexSize;
                }
                break;
            }
            case 2: {
                for (int i = 0; i < numVertices; ++i) {
                    final float[] array2 = vertices;
                    final int n2 = idx;
                    array2[n2] *= scaleX;
                    final float[] array3 = vertices;
                    final int n3 = idx + 1;
                    array3[n3] *= scaleY;
                    idx += vertexSize;
                }
                break;
            }
            case 3: {
                for (int i = 0; i < numVertices; ++i) {
                    final float[] array4 = vertices;
                    final int n4 = idx;
                    array4[n4] *= scaleX;
                    final float[] array5 = vertices;
                    final int n5 = idx + 1;
                    array5[n5] *= scaleY;
                    final float[] array6 = vertices;
                    final int n6 = idx + 2;
                    array6[n6] *= scaleZ;
                    idx += vertexSize;
                }
                break;
            }
        }
        this.setVertices(vertices);
    }
    
    public void transform(final Matrix4 matrix) {
        this.transform(matrix, 0, this.getNumVertices());
    }
    
    public void transform(final Matrix4 matrix, final int start, final int count) {
        final VertexAttribute posAttr = this.getVertexAttribute(1);
        final int posOffset = posAttr.offset / 4;
        final int stride = this.getVertexSize() / 4;
        final int numComponents = posAttr.numComponents;
        final int numVertices = this.getNumVertices();
        final float[] vertices = new float[count * stride];
        this.getVertices(start * stride, count * stride, vertices);
        transform(matrix, vertices, stride, posOffset, numComponents, 0, count);
        this.updateVertices(start * stride, vertices);
    }
    
    public static void transform(final Matrix4 matrix, final float[] vertices, final int vertexSize, final int offset, final int dimensions, final int start, final int count) {
        if (offset < 0 || dimensions < 1 || offset + dimensions > vertexSize) {
            throw new IndexOutOfBoundsException();
        }
        if (start < 0 || count < 1 || (start + count) * vertexSize > vertices.length) {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices.length);
        }
        final Vector3 tmp = new Vector3();
        int idx = offset + start * vertexSize;
        switch (dimensions) {
            case 1: {
                for (int i = 0; i < count; ++i) {
                    tmp.set(vertices[idx], 0.0f, 0.0f).mul(matrix);
                    vertices[idx] = tmp.x;
                    idx += vertexSize;
                }
                break;
            }
            case 2: {
                for (int i = 0; i < count; ++i) {
                    tmp.set(vertices[idx], vertices[idx + 1], 0.0f).mul(matrix);
                    vertices[idx] = tmp.x;
                    vertices[idx + 1] = tmp.y;
                    idx += vertexSize;
                }
                break;
            }
            case 3: {
                for (int i = 0; i < count; ++i) {
                    tmp.set(vertices[idx], vertices[idx + 1], vertices[idx + 2]).mul(matrix);
                    vertices[idx] = tmp.x;
                    vertices[idx + 1] = tmp.y;
                    vertices[idx + 2] = tmp.z;
                    idx += vertexSize;
                }
                break;
            }
        }
    }
    
    public void transformUV(final Matrix3 matrix) {
        this.transformUV(matrix, 0, this.getNumVertices());
    }
    
    protected void transformUV(final Matrix3 matrix, final int start, final int count) {
        final VertexAttribute posAttr = this.getVertexAttribute(16);
        final int offset = posAttr.offset / 4;
        final int vertexSize = this.getVertexSize() / 4;
        final int numVertices = this.getNumVertices();
        final float[] vertices = new float[numVertices * vertexSize];
        this.getVertices(0, vertices.length, vertices);
        transformUV(matrix, vertices, vertexSize, offset, start, count);
        this.setVertices(vertices, 0, vertices.length);
    }
    
    public static void transformUV(final Matrix3 matrix, final float[] vertices, final int vertexSize, final int offset, final int start, final int count) {
        if (start < 0 || count < 1 || (start + count) * vertexSize > vertices.length) {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices.length);
        }
        final Vector2 tmp = new Vector2();
        int idx = offset + start * vertexSize;
        for (int i = 0; i < count; ++i) {
            tmp.set(vertices[idx], vertices[idx + 1]).mul(matrix);
            vertices[idx] = tmp.x;
            vertices[idx + 1] = tmp.y;
            idx += vertexSize;
        }
    }
    
    public Mesh copy(final boolean isStatic, final boolean removeDuplicates, final int[] usage) {
        final int vertexSize = this.getVertexSize() / 4;
        int numVertices = this.getNumVertices();
        float[] vertices = new float[numVertices * vertexSize];
        this.getVertices(0, vertices.length, vertices);
        short[] checks = null;
        VertexAttribute[] attrs = null;
        int newVertexSize = 0;
        if (usage != null) {
            int size = 0;
            int as = 0;
            for (int i = 0; i < usage.length; ++i) {
                if (this.getVertexAttribute(usage[i]) != null) {
                    size += this.getVertexAttribute(usage[i]).numComponents;
                    ++as;
                }
            }
            if (size > 0) {
                attrs = new VertexAttribute[as];
                checks = new short[size];
                int idx = -1;
                int ai = -1;
                for (int j = 0; j < usage.length; ++j) {
                    final VertexAttribute a = this.getVertexAttribute(usage[j]);
                    if (a != null) {
                        for (int k = 0; k < a.numComponents; ++k) {
                            checks[++idx] = (short)(a.offset + k);
                        }
                        attrs[++ai] = a.copy();
                        newVertexSize += a.numComponents;
                    }
                }
            }
        }
        if (checks == null) {
            checks = new short[vertexSize];
            for (short l = 0; l < vertexSize; ++l) {
                checks[l] = l;
            }
            newVertexSize = vertexSize;
        }
        final int numIndices = this.getNumIndices();
        short[] indices = null;
        if (numIndices > 0) {
            indices = new short[numIndices];
            this.getIndices(indices);
            if (removeDuplicates || newVertexSize != vertexSize) {
                final float[] tmp = new float[vertices.length];
                int size2 = 0;
                for (int j = 0; j < numIndices; ++j) {
                    final int idx2 = indices[j] * vertexSize;
                    short newIndex = -1;
                    if (removeDuplicates) {
                        for (short m = 0; m < size2 && newIndex < 0; ++m) {
                            final int idx3 = m * newVertexSize;
                            boolean found = true;
                            for (int k2 = 0; k2 < checks.length && found; ++k2) {
                                if (tmp[idx3 + k2] != vertices[idx2 + checks[k2]]) {
                                    found = false;
                                }
                            }
                            if (found) {
                                newIndex = m;
                            }
                        }
                    }
                    if (newIndex > 0) {
                        indices[j] = newIndex;
                    }
                    else {
                        final int idx4 = size2 * newVertexSize;
                        for (int j2 = 0; j2 < checks.length; ++j2) {
                            tmp[idx4 + j2] = vertices[idx2 + checks[j2]];
                        }
                        indices[j] = (short)size2;
                        ++size2;
                    }
                }
                vertices = tmp;
                numVertices = size2;
            }
        }
        Mesh result;
        if (attrs == null) {
            result = new Mesh(isStatic, numVertices, (indices == null) ? 0 : indices.length, this.getVertexAttributes());
        }
        else {
            result = new Mesh(isStatic, numVertices, (indices == null) ? 0 : indices.length, attrs);
        }
        result.setVertices(vertices, 0, numVertices * newVertexSize);
        if (indices != null) {
            result.setIndices(indices);
        }
        return result;
    }
    
    public Mesh copy(final boolean isStatic) {
        return this.copy(isStatic, false, null);
    }
    
    public enum VertexDataType
    {
        VertexArray("VertexArray", 0), 
        VertexBufferObject("VertexBufferObject", 1), 
        VertexBufferObjectSubData("VertexBufferObjectSubData", 2), 
        VertexBufferObjectWithVAO("VertexBufferObjectWithVAO", 3);
        
        private VertexDataType(final String name, final int ordinal) {
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.PatchShapeBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class MeshBuilder implements MeshPartBuilder
{
    private static final ShortArray tmpIndices;
    private static final FloatArray tmpVertices;
    private final VertexInfo vertTmp1;
    private final VertexInfo vertTmp2;
    private final VertexInfo vertTmp3;
    private final VertexInfo vertTmp4;
    private final Color tempC1;
    private VertexAttributes attributes;
    private FloatArray vertices;
    private ShortArray indices;
    private int stride;
    private int vindex;
    private int istart;
    private int posOffset;
    private int posSize;
    private int norOffset;
    private int biNorOffset;
    private int tangentOffset;
    private int colOffset;
    private int colSize;
    private int cpOffset;
    private int uvOffset;
    private MeshPart part;
    private Array<MeshPart> parts;
    private final Color color;
    private boolean hasColor;
    private int primitiveType;
    private float uOffset;
    private float uScale;
    private float vOffset;
    private float vScale;
    private boolean hasUVTransform;
    private float[] vertex;
    private boolean vertexTransformationEnabled;
    private final Matrix4 positionTransform;
    private final Matrix3 normalTransform;
    private final BoundingBox bounds;
    private short lastIndex;
    private static final Vector3 vTmp;
    private final Vector3 tmpNormal;
    private static IntIntMap indicesMap;
    
    static {
        tmpIndices = new ShortArray();
        tmpVertices = new FloatArray();
        vTmp = new Vector3();
        MeshBuilder.indicesMap = null;
    }
    
    public MeshBuilder() {
        this.vertTmp1 = new VertexInfo();
        this.vertTmp2 = new VertexInfo();
        this.vertTmp3 = new VertexInfo();
        this.vertTmp4 = new VertexInfo();
        this.tempC1 = new Color();
        this.vertices = new FloatArray();
        this.indices = new ShortArray();
        this.parts = new Array<MeshPart>();
        this.color = new Color(Color.WHITE);
        this.hasColor = false;
        this.uOffset = 0.0f;
        this.uScale = 1.0f;
        this.vOffset = 0.0f;
        this.vScale = 1.0f;
        this.hasUVTransform = false;
        this.vertexTransformationEnabled = false;
        this.positionTransform = new Matrix4();
        this.normalTransform = new Matrix3();
        this.bounds = new BoundingBox();
        this.lastIndex = -1;
        this.tmpNormal = new Vector3();
    }
    
    public static VertexAttributes createAttributes(final long usage) {
        final Array<VertexAttribute> attrs = new Array<VertexAttribute>();
        if ((usage & 0x1L) == 0x1L) {
            attrs.add(new VertexAttribute(1, 3, "a_position"));
        }
        if ((usage & 0x2L) == 0x2L) {
            attrs.add(new VertexAttribute(2, 4, "a_color"));
        }
        if ((usage & 0x4L) == 0x4L) {
            attrs.add(new VertexAttribute(4, 4, "a_color"));
        }
        if ((usage & 0x8L) == 0x8L) {
            attrs.add(new VertexAttribute(8, 3, "a_normal"));
        }
        if ((usage & 0x10L) == 0x10L) {
            attrs.add(new VertexAttribute(16, 2, "a_texCoord0"));
        }
        final VertexAttribute[] attributes = new VertexAttribute[attrs.size];
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i] = attrs.get(i);
        }
        return new VertexAttributes(attributes);
    }
    
    public void begin(final long attributes) {
        this.begin(createAttributes(attributes), -1);
    }
    
    public void begin(final VertexAttributes attributes) {
        this.begin(attributes, -1);
    }
    
    public void begin(final long attributes, final int primitiveType) {
        this.begin(createAttributes(attributes), primitiveType);
    }
    
    public void begin(final VertexAttributes attributes, final int primitiveType) {
        if (this.attributes != null) {
            throw new RuntimeException("Call end() first");
        }
        this.attributes = attributes;
        this.vertices.clear();
        this.indices.clear();
        this.parts.clear();
        this.vindex = 0;
        this.lastIndex = -1;
        this.istart = 0;
        this.part = null;
        this.stride = attributes.vertexSize / 4;
        if (this.vertex == null || this.vertex.length < this.stride) {
            this.vertex = new float[this.stride];
        }
        VertexAttribute a = attributes.findByUsage(1);
        if (a == null) {
            throw new GdxRuntimeException("Cannot build mesh without position attribute");
        }
        this.posOffset = a.offset / 4;
        this.posSize = a.numComponents;
        a = attributes.findByUsage(8);
        this.norOffset = ((a == null) ? -1 : (a.offset / 4));
        a = attributes.findByUsage(256);
        this.biNorOffset = ((a == null) ? -1 : (a.offset / 4));
        a = attributes.findByUsage(128);
        this.tangentOffset = ((a == null) ? -1 : (a.offset / 4));
        a = attributes.findByUsage(2);
        this.colOffset = ((a == null) ? -1 : (a.offset / 4));
        this.colSize = ((a == null) ? 0 : a.numComponents);
        a = attributes.findByUsage(4);
        this.cpOffset = ((a == null) ? -1 : (a.offset / 4));
        a = attributes.findByUsage(16);
        this.uvOffset = ((a == null) ? -1 : (a.offset / 4));
        this.setColor(null);
        this.setVertexTransform(null);
        this.setUVRange(null);
        this.primitiveType = primitiveType;
        this.bounds.inf();
    }
    
    private void endpart() {
        if (this.part != null) {
            this.bounds.getCenter(this.part.center);
            this.bounds.getDimensions(this.part.halfExtents).scl(0.5f);
            this.part.radius = this.part.halfExtents.len();
            this.bounds.inf();
            this.part.offset = this.istart;
            this.part.size = this.indices.size - this.istart;
            this.istart = this.indices.size;
            this.part = null;
        }
    }
    
    public MeshPart part(final String id, final int primitiveType) {
        return this.part(id, primitiveType, new MeshPart());
    }
    
    public MeshPart part(final String id, final int primitiveType, final MeshPart meshPart) {
        if (this.attributes == null) {
            throw new RuntimeException("Call begin() first");
        }
        this.endpart();
        this.part = meshPart;
        this.part.id = id;
        this.part.primitiveType = primitiveType;
        this.primitiveType = primitiveType;
        this.parts.add(this.part);
        this.setColor(null);
        this.setVertexTransform(null);
        this.setUVRange(null);
        return this.part;
    }
    
    public Mesh end(final Mesh mesh) {
        this.endpart();
        if (this.attributes == null) {
            throw new GdxRuntimeException("Call begin() first");
        }
        if (!this.attributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Mesh attributes don't match");
        }
        if (mesh.getMaxVertices() * this.stride < this.vertices.size) {
            throw new GdxRuntimeException("Mesh can't hold enough vertices: " + mesh.getMaxVertices() + " * " + this.stride + " < " + this.vertices.size);
        }
        if (mesh.getMaxIndices() < this.indices.size) {
            throw new GdxRuntimeException("Mesh can't hold enough indices: " + mesh.getMaxIndices() + " < " + this.indices.size);
        }
        mesh.setVertices(this.vertices.items, 0, this.vertices.size);
        mesh.setIndices(this.indices.items, 0, this.indices.size);
        for (final MeshPart p : this.parts) {
            p.mesh = mesh;
        }
        this.parts.clear();
        this.attributes = null;
        this.vertices.clear();
        this.indices.clear();
        return mesh;
    }
    
    public Mesh end() {
        return this.end(new Mesh(true, this.vertices.size / this.stride, this.indices.size, this.attributes));
    }
    
    public void clear() {
        this.vertices.clear();
        this.indices.clear();
        this.parts.clear();
        this.vindex = 0;
        this.lastIndex = -1;
        this.istart = 0;
        this.part = null;
    }
    
    public int getFloatsPerVertex() {
        return this.stride;
    }
    
    public int getNumVertices() {
        return this.vertices.size / this.stride;
    }
    
    public void getVertices(final float[] out, final int destOffset) {
        if (this.attributes == null) {
            throw new GdxRuntimeException("Must be called in between #begin and #end");
        }
        if (destOffset < 0 || destOffset > out.length - this.vertices.size) {
            throw new GdxRuntimeException("Array to small or offset out of range");
        }
        System.arraycopy(this.vertices.items, 0, out, destOffset, this.vertices.size);
    }
    
    protected float[] getVertices() {
        return this.vertices.items;
    }
    
    public int getNumIndices() {
        return this.indices.size;
    }
    
    public void getIndices(final short[] out, final int destOffset) {
        if (this.attributes == null) {
            throw new GdxRuntimeException("Must be called in between #begin and #end");
        }
        if (destOffset < 0 || destOffset > out.length - this.indices.size) {
            throw new GdxRuntimeException("Array to small or offset out of range");
        }
        System.arraycopy(this.indices.items, 0, out, destOffset, this.indices.size);
    }
    
    protected short[] getIndices() {
        return this.indices.items;
    }
    
    @Override
    public VertexAttributes getAttributes() {
        return this.attributes;
    }
    
    @Override
    public MeshPart getMeshPart() {
        return this.part;
    }
    
    @Override
    public int getPrimitiveType() {
        return this.primitiveType;
    }
    
    @Override
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        this.hasColor = !this.color.equals(Color.WHITE);
    }
    
    @Override
    public void setColor(final Color color) {
        final Color color2 = this.color;
        final boolean hasColor = color != null;
        this.hasColor = hasColor;
        color2.set((!hasColor) ? Color.WHITE : color);
    }
    
    @Override
    public void setUVRange(final float u1, final float v1, final float u2, final float v2) {
        this.uOffset = u1;
        this.vOffset = v1;
        this.uScale = u2 - u1;
        this.vScale = v2 - v1;
        this.hasUVTransform = (!MathUtils.isZero(u1) || !MathUtils.isZero(v1) || !MathUtils.isEqual(u2, 1.0f) || !MathUtils.isEqual(v2, 1.0f));
    }
    
    @Override
    public void setUVRange(final TextureRegion region) {
        final boolean hasUVTransform = region != null;
        this.hasUVTransform = hasUVTransform;
        if (!hasUVTransform) {
            final float n = 0.0f;
            this.vOffset = n;
            this.uOffset = n;
            final float n2 = 1.0f;
            this.vScale = n2;
            this.uScale = n2;
        }
        else {
            this.setUVRange(region.getU(), region.getV(), region.getU2(), region.getV2());
        }
    }
    
    @Override
    public Matrix4 getVertexTransform(final Matrix4 out) {
        return out.set(this.positionTransform);
    }
    
    @Override
    public void setVertexTransform(final Matrix4 transform) {
        this.vertexTransformationEnabled = (transform != null);
        if (this.vertexTransformationEnabled) {
            this.positionTransform.set(transform);
            this.normalTransform.set(transform).inv().transpose();
        }
        else {
            this.positionTransform.idt();
            this.normalTransform.idt();
        }
    }
    
    @Override
    public boolean isVertexTransformationEnabled() {
        return this.vertexTransformationEnabled;
    }
    
    @Override
    public void setVertexTransformationEnabled(final boolean enabled) {
        this.vertexTransformationEnabled = enabled;
    }
    
    @Override
    public void ensureVertices(final int numVertices) {
        this.vertices.ensureCapacity(this.stride * numVertices);
    }
    
    @Override
    public void ensureIndices(final int numIndices) {
        this.indices.ensureCapacity(numIndices);
    }
    
    @Override
    public void ensureCapacity(final int numVertices, final int numIndices) {
        this.ensureVertices(numVertices);
        this.ensureIndices(numIndices);
    }
    
    @Override
    public void ensureTriangleIndices(final int numTriangles) {
        if (this.primitiveType == 1) {
            this.ensureIndices(6 * numTriangles);
        }
        else {
            if (this.primitiveType != 4 && this.primitiveType != 0) {
                throw new GdxRuntimeException("Incorrect primtive type");
            }
            this.ensureIndices(3 * numTriangles);
        }
    }
    
    @Deprecated
    public void ensureTriangles(final int numVertices, final int numTriangles) {
        this.ensureVertices(numVertices);
        this.ensureTriangleIndices(numTriangles);
    }
    
    @Deprecated
    public void ensureTriangles(final int numTriangles) {
        this.ensureVertices(3 * numTriangles);
        this.ensureTriangleIndices(numTriangles);
    }
    
    @Override
    public void ensureRectangleIndices(final int numRectangles) {
        if (this.primitiveType == 0) {
            this.ensureIndices(4 * numRectangles);
        }
        else if (this.primitiveType == 1) {
            this.ensureIndices(8 * numRectangles);
        }
        else {
            this.ensureIndices(6 * numRectangles);
        }
    }
    
    @Deprecated
    public void ensureRectangles(final int numVertices, final int numRectangles) {
        this.ensureVertices(numVertices);
        this.ensureRectangleIndices(numRectangles);
    }
    
    @Deprecated
    public void ensureRectangles(final int numRectangles) {
        this.ensureVertices(4 * numRectangles);
        this.ensureRectangleIndices(numRectangles);
    }
    
    @Override
    public short lastIndex() {
        return this.lastIndex;
    }
    
    private static final void transformPosition(final float[] values, final int offset, final int size, final Matrix4 transform) {
        if (size > 2) {
            MeshBuilder.vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform);
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
            values[offset + 2] = MeshBuilder.vTmp.z;
        }
        else if (size > 1) {
            MeshBuilder.vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform);
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
        }
        else {
            values[offset] = MeshBuilder.vTmp.set(values[offset], 0.0f, 0.0f).mul(transform).x;
        }
    }
    
    private static final void transformNormal(final float[] values, final int offset, final int size, final Matrix3 transform) {
        if (size > 2) {
            MeshBuilder.vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform).nor();
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
            values[offset + 2] = MeshBuilder.vTmp.z;
        }
        else if (size > 1) {
            MeshBuilder.vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform).nor();
            values[offset] = MeshBuilder.vTmp.x;
            values[offset + 1] = MeshBuilder.vTmp.y;
        }
        else {
            values[offset] = MeshBuilder.vTmp.set(values[offset], 0.0f, 0.0f).mul(transform).nor().x;
        }
    }
    
    private final void addVertex(final float[] values, final int offset) {
        final int o = this.vertices.size;
        this.vertices.addAll(values, offset, this.stride);
        this.lastIndex = (short)(this.vindex++);
        if (this.vertexTransformationEnabled) {
            transformPosition(this.vertices.items, o + this.posOffset, this.posSize, this.positionTransform);
            if (this.norOffset >= 0) {
                transformNormal(this.vertices.items, o + this.norOffset, 3, this.normalTransform);
            }
            if (this.biNorOffset >= 0) {
                transformNormal(this.vertices.items, o + this.biNorOffset, 3, this.normalTransform);
            }
            if (this.tangentOffset >= 0) {
                transformNormal(this.vertices.items, o + this.tangentOffset, 3, this.normalTransform);
            }
        }
        final float x = this.vertices.items[o + this.posOffset];
        final float y = (this.posSize > 1) ? this.vertices.items[o + this.posOffset + 1] : 0.0f;
        final float z = (this.posSize > 2) ? this.vertices.items[o + this.posOffset + 2] : 0.0f;
        this.bounds.ext(x, y, z);
        if (this.hasColor) {
            if (this.colOffset >= 0) {
                final float[] items = this.vertices.items;
                final int n = o + this.colOffset;
                items[n] *= this.color.r;
                final float[] items2 = this.vertices.items;
                final int n2 = o + this.colOffset + 1;
                items2[n2] *= this.color.g;
                final float[] items3 = this.vertices.items;
                final int n3 = o + this.colOffset + 2;
                items3[n3] *= this.color.b;
                if (this.colSize > 3) {
                    final float[] items4 = this.vertices.items;
                    final int n4 = o + this.colOffset + 3;
                    items4[n4] *= this.color.a;
                }
            }
            else if (this.cpOffset >= 0) {
                Color.abgr8888ToColor(this.tempC1, this.vertices.items[o + this.cpOffset]);
                this.vertices.items[o + this.cpOffset] = this.tempC1.mul(this.color).toFloatBits();
            }
        }
        if (this.hasUVTransform && this.uvOffset >= 0) {
            this.vertices.items[o + this.uvOffset] = this.uOffset + this.uScale * this.vertices.items[o + this.uvOffset];
            this.vertices.items[o + this.uvOffset + 1] = this.vOffset + this.vScale * this.vertices.items[o + this.uvOffset + 1];
        }
    }
    
    @Override
    public short vertex(final Vector3 pos, Vector3 nor, Color col, final Vector2 uv) {
        if (this.vindex > 32767) {
            throw new GdxRuntimeException("Too many vertices used");
        }
        this.vertex[this.posOffset] = pos.x;
        if (this.posSize > 1) {
            this.vertex[this.posOffset + 1] = pos.y;
        }
        if (this.posSize > 2) {
            this.vertex[this.posOffset + 2] = pos.z;
        }
        if (this.norOffset >= 0) {
            if (nor == null) {
                nor = this.tmpNormal.set(pos).nor();
            }
            this.vertex[this.norOffset] = nor.x;
            this.vertex[this.norOffset + 1] = nor.y;
            this.vertex[this.norOffset + 2] = nor.z;
        }
        if (this.colOffset >= 0) {
            if (col == null) {
                col = Color.WHITE;
            }
            this.vertex[this.colOffset] = col.r;
            this.vertex[this.colOffset + 1] = col.g;
            this.vertex[this.colOffset + 2] = col.b;
            if (this.colSize > 3) {
                this.vertex[this.colOffset + 3] = col.a;
            }
        }
        else if (this.cpOffset > 0) {
            if (col == null) {
                col = Color.WHITE;
            }
            this.vertex[this.cpOffset] = col.toFloatBits();
        }
        if (uv != null && this.uvOffset >= 0) {
            this.vertex[this.uvOffset] = uv.x;
            this.vertex[this.uvOffset + 1] = uv.y;
        }
        this.addVertex(this.vertex, 0);
        return this.lastIndex;
    }
    
    @Override
    public short vertex(final float... values) {
        for (int n = values.length - this.stride, i = 0; i <= n; i += this.stride) {
            this.addVertex(values, i);
        }
        return this.lastIndex;
    }
    
    @Override
    public short vertex(final VertexInfo info) {
        return this.vertex(info.hasPosition ? info.position : null, info.hasNormal ? info.normal : null, info.hasColor ? info.color : null, info.hasUV ? info.uv : null);
    }
    
    @Override
    public void index(final short value) {
        this.indices.add(value);
    }
    
    @Override
    public void index(final short value1, final short value2) {
        this.ensureIndices(2);
        this.indices.add(value1);
        this.indices.add(value2);
    }
    
    @Override
    public void index(final short value1, final short value2, final short value3) {
        this.ensureIndices(3);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
    }
    
    @Override
    public void index(final short value1, final short value2, final short value3, final short value4) {
        this.ensureIndices(4);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
    }
    
    @Override
    public void index(final short value1, final short value2, final short value3, final short value4, final short value5, final short value6) {
        this.ensureIndices(6);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
    }
    
    @Override
    public void index(final short value1, final short value2, final short value3, final short value4, final short value5, final short value6, final short value7, final short value8) {
        this.ensureIndices(8);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
        this.indices.add(value7);
        this.indices.add(value8);
    }
    
    @Override
    public void line(final short index1, final short index2) {
        if (this.primitiveType != 1) {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
        this.index(index1, index2);
    }
    
    @Override
    public void line(final VertexInfo p1, final VertexInfo p2) {
        this.ensureVertices(2);
        this.line(this.vertex(p1), this.vertex(p2));
    }
    
    @Override
    public void line(final Vector3 p1, final Vector3 p2) {
        this.line(this.vertTmp1.set(p1, null, null, null), this.vertTmp2.set(p2, null, null, null));
    }
    
    @Override
    public void line(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        this.line(this.vertTmp1.set(null, null, null, null).setPos(x1, y1, z1), this.vertTmp2.set(null, null, null, null).setPos(x2, y2, z2));
    }
    
    @Override
    public void line(final Vector3 p1, final Color c1, final Vector3 p2, final Color c2) {
        this.line(this.vertTmp1.set(p1, null, c1, null), this.vertTmp2.set(p2, null, c2, null));
    }
    
    @Override
    public void triangle(final short index1, final short index2, final short index3) {
        if (this.primitiveType == 4 || this.primitiveType == 0) {
            this.index(index1, index2, index3);
        }
        else {
            if (this.primitiveType != 1) {
                throw new GdxRuntimeException("Incorrect primitive type");
            }
            this.index(index1, index2, index2, index3, index3, index1);
        }
    }
    
    @Override
    public void triangle(final VertexInfo p1, final VertexInfo p2, final VertexInfo p3) {
        this.ensureVertices(3);
        this.triangle(this.vertex(p1), this.vertex(p2), this.vertex(p3));
    }
    
    @Override
    public void triangle(final Vector3 p1, final Vector3 p2, final Vector3 p3) {
        this.triangle(this.vertTmp1.set(p1, null, null, null), this.vertTmp2.set(p2, null, null, null), this.vertTmp3.set(p3, null, null, null));
    }
    
    @Override
    public void triangle(final Vector3 p1, final Color c1, final Vector3 p2, final Color c2, final Vector3 p3, final Color c3) {
        this.triangle(this.vertTmp1.set(p1, null, c1, null), this.vertTmp2.set(p2, null, c2, null), this.vertTmp3.set(p3, null, c3, null));
    }
    
    @Override
    public void rect(final short corner00, final short corner10, final short corner11, final short corner01) {
        if (this.primitiveType == 4) {
            this.index(corner00, corner10, corner11, corner11, corner01, corner00);
        }
        else if (this.primitiveType == 1) {
            this.index(corner00, corner10, corner10, corner11, corner11, corner01, corner01, corner00);
        }
        else {
            if (this.primitiveType != 0) {
                throw new GdxRuntimeException("Incorrect primitive type");
            }
            this.index(corner00, corner10, corner11, corner01);
        }
    }
    
    @Override
    public void rect(final VertexInfo corner00, final VertexInfo corner10, final VertexInfo corner11, final VertexInfo corner01) {
        this.ensureVertices(4);
        this.rect(this.vertex(corner00), this.vertex(corner10), this.vertex(corner11), this.vertex(corner01));
    }
    
    @Override
    public void rect(final Vector3 corner00, final Vector3 corner10, final Vector3 corner11, final Vector3 corner01, final Vector3 normal) {
        this.rect(this.vertTmp1.set(corner00, normal, null, null).setUV(0.0f, 1.0f), this.vertTmp2.set(corner10, normal, null, null).setUV(1.0f, 1.0f), this.vertTmp3.set(corner11, normal, null, null).setUV(1.0f, 0.0f), this.vertTmp4.set(corner01, normal, null, null).setUV(0.0f, 0.0f));
    }
    
    @Override
    public void rect(final float x00, final float y00, final float z00, final float x10, final float y10, final float z10, final float x11, final float y11, final float z11, final float x01, final float y01, final float z01, final float normalX, final float normalY, final float normalZ) {
        this.rect(this.vertTmp1.set(null, null, null, null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(0.0f, 1.0f), this.vertTmp2.set(null, null, null, null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(1.0f, 1.0f), this.vertTmp3.set(null, null, null, null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(1.0f, 0.0f), this.vertTmp4.set(null, null, null, null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(0.0f, 0.0f));
    }
    
    @Override
    public void addMesh(final Mesh mesh) {
        this.addMesh(mesh, 0, mesh.getNumIndices());
    }
    
    @Override
    public void addMesh(final MeshPart meshpart) {
        if (meshpart.primitiveType != this.primitiveType) {
            throw new GdxRuntimeException("Primitive type doesn't match");
        }
        this.addMesh(meshpart.mesh, meshpart.offset, meshpart.size);
    }
    
    @Override
    public void addMesh(final Mesh mesh, final int indexOffset, final int numIndices) {
        if (!this.attributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Vertex attributes do not match");
        }
        if (numIndices <= 0) {
            return;
        }
        final int numFloats = mesh.getNumVertices() * this.stride;
        MeshBuilder.tmpVertices.clear();
        MeshBuilder.tmpVertices.ensureCapacity(numFloats);
        MeshBuilder.tmpVertices.size = numFloats;
        mesh.getVertices(MeshBuilder.tmpVertices.items);
        MeshBuilder.tmpIndices.clear();
        MeshBuilder.tmpIndices.ensureCapacity(numIndices);
        mesh.getIndices(indexOffset, MeshBuilder.tmpIndices.size = numIndices, MeshBuilder.tmpIndices.items, 0);
        this.addMesh(MeshBuilder.tmpVertices.items, MeshBuilder.tmpIndices.items, 0, numIndices);
    }
    
    @Override
    public void addMesh(final float[] vertices, final short[] indices, final int indexOffset, final int numIndices) {
        if (MeshBuilder.indicesMap == null) {
            MeshBuilder.indicesMap = new IntIntMap(numIndices);
        }
        else {
            MeshBuilder.indicesMap.clear();
            MeshBuilder.indicesMap.ensureCapacity(numIndices);
        }
        this.ensureIndices(numIndices);
        final int numVertices = vertices.length / this.stride;
        this.ensureVertices((numVertices < numIndices) ? numVertices : numIndices);
        for (int i = 0; i < numIndices; ++i) {
            final int sidx = indices[indexOffset + i];
            int didx = MeshBuilder.indicesMap.get(sidx, -1);
            if (didx < 0) {
                this.addVertex(vertices, sidx * this.stride);
                MeshBuilder.indicesMap.put(sidx, didx = this.lastIndex);
            }
            this.index((short)didx);
        }
    }
    
    @Override
    public void addMesh(final float[] vertices, final short[] indices) {
        final short offset = (short)(this.lastIndex + 1);
        final int numVertices = vertices.length / this.stride;
        this.ensureVertices(numVertices);
        for (int v = 0; v < vertices.length; v += this.stride) {
            this.addVertex(vertices, v);
        }
        this.ensureIndices(indices.length);
        for (int i = 0; i < indices.length; ++i) {
            this.index((short)(indices[i] + offset));
        }
    }
    
    @Deprecated
    @Override
    public void patch(final VertexInfo corner00, final VertexInfo corner10, final VertexInfo corner11, final VertexInfo corner01, final int divisionsU, final int divisionsV) {
        PatchShapeBuilder.build(this, corner00, corner10, corner11, corner01, divisionsU, divisionsV);
    }
    
    @Deprecated
    @Override
    public void patch(final Vector3 corner00, final Vector3 corner10, final Vector3 corner11, final Vector3 corner01, final Vector3 normal, final int divisionsU, final int divisionsV) {
        PatchShapeBuilder.build(this, corner00, corner10, corner11, corner01, normal, divisionsU, divisionsV);
    }
    
    @Deprecated
    @Override
    public void patch(final float x00, final float y00, final float z00, final float x10, final float y10, final float z10, final float x11, final float y11, final float z11, final float x01, final float y01, final float z01, final float normalX, final float normalY, final float normalZ, final int divisionsU, final int divisionsV) {
        PatchShapeBuilder.build(this, x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ, divisionsU, divisionsV);
    }
    
    @Deprecated
    @Override
    public void box(final VertexInfo corner000, final VertexInfo corner010, final VertexInfo corner100, final VertexInfo corner110, final VertexInfo corner001, final VertexInfo corner011, final VertexInfo corner101, final VertexInfo corner111) {
        BoxShapeBuilder.build(this, corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111);
    }
    
    @Deprecated
    @Override
    public void box(final Vector3 corner000, final Vector3 corner010, final Vector3 corner100, final Vector3 corner110, final Vector3 corner001, final Vector3 corner011, final Vector3 corner101, final Vector3 corner111) {
        BoxShapeBuilder.build(this, corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111);
    }
    
    @Deprecated
    @Override
    public void box(final Matrix4 transform) {
        BoxShapeBuilder.build(this, transform);
    }
    
    @Deprecated
    @Override
    public void box(final float width, final float height, final float depth) {
        BoxShapeBuilder.build(this, width, height, depth);
    }
    
    @Deprecated
    @Override
    public void box(final float x, final float y, final float z, final float width, final float height, final float depth) {
        BoxShapeBuilder.build(this, x, y, z, width, height, depth);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final Vector3 center, final Vector3 normal) {
        EllipseShapeBuilder.build(this, radius, divisions, center, normal);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal) {
        EllipseShapeBuilder.build(this, radius, divisions, center, normal, tangent, binormal);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final Vector3 center, final Vector3 normal, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, radius, divisions, center, normal, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal, final float angleFrom, final float angleTo) {
        this.circle(radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void circle(final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal) {
        EllipseShapeBuilder.build(this, width, height, divisions, center, normal);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal) {
        EllipseShapeBuilder.build(this, width, height, divisions, center, normal, tangent, binormal);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, divisions, center, normal, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, divisions, center, normal, tangent, binormal, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final Vector3 center, final Vector3 normal) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, center, normal);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void ellipse(final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void cylinder(final float width, final float height, final float depth, final int divisions) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions);
    }
    
    @Deprecated
    @Override
    public void cylinder(final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void cylinder(final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo, final boolean close) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo, close);
    }
    
    @Deprecated
    @Override
    public void cone(final float width, final float height, final float depth, final int divisions) {
        this.cone(width, height, depth, divisions, 0.0f, 360.0f);
    }
    
    @Deprecated
    @Override
    public void cone(final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo) {
        ConeShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo);
    }
    
    @Deprecated
    @Override
    public void sphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV) {
        SphereShapeBuilder.build(this, width, height, depth, divisionsU, divisionsV);
    }
    
    @Deprecated
    @Override
    public void sphere(final Matrix4 transform, final float width, final float height, final float depth, final int divisionsU, final int divisionsV) {
        SphereShapeBuilder.build(this, transform, width, height, depth, divisionsU, divisionsV);
    }
    
    @Deprecated
    @Override
    public void sphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        SphereShapeBuilder.build(this, width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }
    
    @Deprecated
    @Override
    public void sphere(final Matrix4 transform, final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        SphereShapeBuilder.build(this, transform, width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }
    
    @Deprecated
    @Override
    public void capsule(final float radius, final float height, final int divisions) {
        CapsuleShapeBuilder.build(this, radius, height, divisions);
    }
    
    @Deprecated
    @Override
    public void arrow(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float capLength, final float stemThickness, final int divisions) {
        ArrowShapeBuilder.build(this, x1, y1, z1, x2, y2, z2, capLength, stemThickness, divisions);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.Model;

public class ModelBuilder
{
    private Model model;
    private Node node;
    private Array<MeshBuilder> builders;
    private Matrix4 tmpTransform;
    
    public ModelBuilder() {
        this.builders = new Array<MeshBuilder>();
        this.tmpTransform = new Matrix4();
    }
    
    private MeshBuilder getBuilder(final VertexAttributes attributes) {
        for (final MeshBuilder mb : this.builders) {
            if (mb.getAttributes().equals(attributes) && mb.lastIndex() < 16383) {
                return mb;
            }
        }
        final MeshBuilder result = new MeshBuilder();
        result.begin(attributes);
        this.builders.add(result);
        return result;
    }
    
    public void begin() {
        if (this.model != null) {
            throw new GdxRuntimeException("Call end() first");
        }
        this.node = null;
        this.model = new Model();
        this.builders.clear();
    }
    
    public Model end() {
        if (this.model == null) {
            throw new GdxRuntimeException("Call begin() first");
        }
        final Model result = this.model;
        this.endnode();
        this.model = null;
        for (final MeshBuilder mb : this.builders) {
            mb.end();
        }
        this.builders.clear();
        rebuildReferences(result);
        return result;
    }
    
    private void endnode() {
        if (this.node != null) {
            this.node = null;
        }
    }
    
    protected Node node(final Node node) {
        if (this.model == null) {
            throw new GdxRuntimeException("Call begin() first");
        }
        this.endnode();
        this.model.nodes.add(node);
        return this.node = node;
    }
    
    public Node node() {
        final Node node = new Node();
        this.node(node);
        node.id = "node" + this.model.nodes.size;
        return node;
    }
    
    public Node node(final String id, final Model model) {
        final Node node = new Node();
        node.id = id;
        node.addChildren(model.nodes);
        this.node(node);
        for (final Disposable disposable : model.getManagedDisposables()) {
            this.manage(disposable);
        }
        return node;
    }
    
    public void manage(final Disposable disposable) {
        if (this.model == null) {
            throw new GdxRuntimeException("Call begin() first");
        }
        this.model.manageDisposable(disposable);
    }
    
    public void part(final MeshPart meshpart, final Material material) {
        if (this.node == null) {
            this.node();
        }
        this.node.parts.add(new NodePart(meshpart, material));
    }
    
    public MeshPart part(final String id, final Mesh mesh, final int primitiveType, final int offset, final int size, final Material material) {
        final MeshPart meshPart = new MeshPart();
        meshPart.id = id;
        meshPart.primitiveType = primitiveType;
        meshPart.mesh = mesh;
        meshPart.offset = offset;
        meshPart.size = size;
        this.part(meshPart, material);
        return meshPart;
    }
    
    public MeshPart part(final String id, final Mesh mesh, final int primitiveType, final Material material) {
        return this.part(id, mesh, primitiveType, 0, mesh.getNumIndices(), material);
    }
    
    public MeshPartBuilder part(final String id, final int primitiveType, final VertexAttributes attributes, final Material material) {
        final MeshBuilder builder = this.getBuilder(attributes);
        this.part(builder.part(id, primitiveType), material);
        return builder;
    }
    
    public MeshPartBuilder part(final String id, final int primitiveType, final long attributes, final Material material) {
        return this.part(id, primitiveType, MeshBuilder.createAttributes(attributes), material);
    }
    
    public Model createBox(final float width, final float height, final float depth, final Material material, final long attributes) {
        return this.createBox(width, height, depth, 4, material, attributes);
    }
    
    public Model createBox(final float width, final float height, final float depth, final int primitiveType, final Material material, final long attributes) {
        this.begin();
        this.part("box", primitiveType, attributes, material).box(width, height, depth);
        return this.end();
    }
    
    public Model createRect(final float x00, final float y00, final float z00, final float x10, final float y10, final float z10, final float x11, final float y11, final float z11, final float x01, final float y01, final float z01, final float normalX, final float normalY, final float normalZ, final Material material, final long attributes) {
        return this.createRect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ, 4, material, attributes);
    }
    
    public Model createRect(final float x00, final float y00, final float z00, final float x10, final float y10, final float z10, final float x11, final float y11, final float z11, final float x01, final float y01, final float z01, final float normalX, final float normalY, final float normalZ, final int primitiveType, final Material material, final long attributes) {
        this.begin();
        this.part("rect", primitiveType, attributes, material).rect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ);
        return this.end();
    }
    
    public Model createCylinder(final float width, final float height, final float depth, final int divisions, final Material material, final long attributes) {
        return this.createCylinder(width, height, depth, divisions, 4, material, attributes);
    }
    
    public Model createCylinder(final float width, final float height, final float depth, final int divisions, final int primitiveType, final Material material, final long attributes) {
        return this.createCylinder(width, height, depth, divisions, primitiveType, material, attributes, 0.0f, 360.0f);
    }
    
    public Model createCylinder(final float width, final float height, final float depth, final int divisions, final Material material, final long attributes, final float angleFrom, final float angleTo) {
        return this.createCylinder(width, height, depth, divisions, 4, material, attributes, angleFrom, angleTo);
    }
    
    public Model createCylinder(final float width, final float height, final float depth, final int divisions, final int primitiveType, final Material material, final long attributes, final float angleFrom, final float angleTo) {
        this.begin();
        this.part("cylinder", primitiveType, attributes, material).cylinder(width, height, depth, divisions, angleFrom, angleTo);
        return this.end();
    }
    
    public Model createCone(final float width, final float height, final float depth, final int divisions, final Material material, final long attributes) {
        return this.createCone(width, height, depth, divisions, 4, material, attributes);
    }
    
    public Model createCone(final float width, final float height, final float depth, final int divisions, final int primitiveType, final Material material, final long attributes) {
        return this.createCone(width, height, depth, divisions, primitiveType, material, attributes, 0.0f, 360.0f);
    }
    
    public Model createCone(final float width, final float height, final float depth, final int divisions, final Material material, final long attributes, final float angleFrom, final float angleTo) {
        return this.createCone(width, height, depth, divisions, 4, material, attributes, angleFrom, angleTo);
    }
    
    public Model createCone(final float width, final float height, final float depth, final int divisions, final int primitiveType, final Material material, final long attributes, final float angleFrom, final float angleTo) {
        this.begin();
        this.part("cone", primitiveType, attributes, material).cone(width, height, depth, divisions, angleFrom, angleTo);
        return this.end();
    }
    
    public Model createSphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final Material material, final long attributes) {
        return this.createSphere(width, height, depth, divisionsU, divisionsV, 4, material, attributes);
    }
    
    public Model createSphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final int primitiveType, final Material material, final long attributes) {
        return this.createSphere(width, height, depth, divisionsU, divisionsV, primitiveType, material, attributes, 0.0f, 360.0f, 0.0f, 180.0f);
    }
    
    public Model createSphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final Material material, final long attributes, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        return this.createSphere(width, height, depth, divisionsU, divisionsV, 4, material, attributes, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }
    
    public Model createSphere(final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final int primitiveType, final Material material, final long attributes, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        this.begin();
        this.part("cylinder", primitiveType, attributes, material).sphere(width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
        return this.end();
    }
    
    public Model createCapsule(final float radius, final float height, final int divisions, final Material material, final long attributes) {
        return this.createCapsule(radius, height, divisions, 4, material, attributes);
    }
    
    public Model createCapsule(final float radius, final float height, final int divisions, final int primitiveType, final Material material, final long attributes) {
        this.begin();
        this.part("capsule", primitiveType, attributes, material).capsule(radius, height, divisions);
        return this.end();
    }
    
    public static void rebuildReferences(final Model model) {
        model.materials.clear();
        model.meshes.clear();
        model.meshParts.clear();
        for (final Node node : model.nodes) {
            rebuildReferences(model, node);
        }
    }
    
    private static void rebuildReferences(final Model model, final Node node) {
        for (final NodePart mpm : node.parts) {
            if (!model.materials.contains(mpm.material, true)) {
                model.materials.add(mpm.material);
            }
            if (!model.meshParts.contains(mpm.meshPart, true)) {
                model.meshParts.add(mpm.meshPart);
                if (!model.meshes.contains(mpm.meshPart.mesh, true)) {
                    model.meshes.add(mpm.meshPart.mesh);
                }
                model.manageDisposable(mpm.meshPart.mesh);
            }
        }
        for (final Node child : node.getChildren()) {
            rebuildReferences(model, child);
        }
    }
    
    public Model createXYZCoordinates(final float axisLength, final float capLength, final float stemThickness, final int divisions, final int primitiveType, final Material material, final long attributes) {
        this.begin();
        final Node node = this.node();
        final MeshPartBuilder partBuilder = this.part("xyz", primitiveType, attributes, material);
        partBuilder.setColor(Color.RED);
        partBuilder.arrow(0.0f, 0.0f, 0.0f, axisLength, 0.0f, 0.0f, capLength, stemThickness, divisions);
        partBuilder.setColor(Color.GREEN);
        partBuilder.arrow(0.0f, 0.0f, 0.0f, 0.0f, axisLength, 0.0f, capLength, stemThickness, divisions);
        partBuilder.setColor(Color.BLUE);
        partBuilder.arrow(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, axisLength, capLength, stemThickness, divisions);
        return this.end();
    }
    
    public Model createXYZCoordinates(final float axisLength, final Material material, final long attributes) {
        return this.createXYZCoordinates(axisLength, 0.1f, 0.1f, 5, 4, material, attributes);
    }
    
    public Model createArrow(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float capLength, final float stemThickness, final int divisions, final int primitiveType, final Material material, final long attributes) {
        this.begin();
        this.part("arrow", primitiveType, attributes, material).arrow(x1, y1, z1, x2, y2, z2, capLength, stemThickness, divisions);
        return this.end();
    }
    
    public Model createArrow(final Vector3 from, final Vector3 to, final Material material, final long attributes) {
        return this.createArrow(from.x, from.y, from.z, to.x, to.y, to.z, 0.1f, 0.1f, 5, 4, material, attributes);
    }
    
    public Model createLineGrid(final int xDivisions, final int zDivisions, final float xSize, final float zSize, final Material material, final long attributes) {
        this.begin();
        final MeshPartBuilder partBuilder = this.part("lines", 1, attributes, material);
        final float xlength = xDivisions * xSize;
        final float zlength = zDivisions * zSize;
        final float hxlength = xlength / 2.0f;
        final float hzlength = zlength / 2.0f;
        float x1 = -hxlength;
        float y1 = 0.0f;
        float z1 = hzlength;
        float x2 = -hxlength;
        float y2 = 0.0f;
        float z2 = -hzlength;
        for (int i = 0; i <= xDivisions; ++i) {
            partBuilder.line(x1, y1, z1, x2, y2, z2);
            x1 += xSize;
            x2 += xSize;
        }
        x1 = -hxlength;
        y1 = 0.0f;
        z1 = -hzlength;
        x2 = hxlength;
        y2 = 0.0f;
        z2 = -hzlength;
        for (int j = 0; j <= zDivisions; ++j) {
            partBuilder.line(x1, y1, z1, x2, y2, z2);
            z1 += zSize;
            z2 += zSize;
        }
        return this.end();
    }
}

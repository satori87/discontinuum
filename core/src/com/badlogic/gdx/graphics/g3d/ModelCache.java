// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import java.util.Comparator;
import java.util.Iterator;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ModelCache implements Disposable, RenderableProvider
{
    private Array<Renderable> renderables;
    private FlushablePool<Renderable> renderablesPool;
    private FlushablePool<MeshPart> meshPartPool;
    private Array<Renderable> items;
    private Array<Renderable> tmp;
    private MeshBuilder meshBuilder;
    private boolean building;
    private RenderableSorter sorter;
    private MeshPool meshPool;
    private Camera camera;
    
    public ModelCache() {
        this(new Sorter(), new SimpleMeshPool());
    }
    
    public ModelCache(final RenderableSorter sorter, final MeshPool meshPool) {
        this.renderables = new Array<Renderable>();
        this.renderablesPool = new FlushablePool<Renderable>() {
            @Override
            protected Renderable newObject() {
                return new Renderable();
            }
        };
        this.meshPartPool = new FlushablePool<MeshPart>() {
            @Override
            protected MeshPart newObject() {
                return new MeshPart();
            }
        };
        this.items = new Array<Renderable>();
        this.tmp = new Array<Renderable>();
        this.sorter = sorter;
        this.meshPool = meshPool;
        this.meshBuilder = new MeshBuilder();
    }
    
    public void begin() {
        this.begin(null);
    }
    
    public void begin(final Camera camera) {
        if (this.building) {
            throw new GdxRuntimeException("Call end() after calling begin()");
        }
        this.building = true;
        this.camera = camera;
        this.renderablesPool.flush();
        this.renderables.clear();
        this.items.clear();
        this.meshPartPool.flush();
        this.meshPool.flush();
    }
    
    private Renderable obtainRenderable(final Material material, final int primitiveType) {
        final Renderable result = this.renderablesPool.obtain();
        result.bones = null;
        result.environment = null;
        result.material = material;
        result.meshPart.mesh = null;
        result.meshPart.offset = 0;
        result.meshPart.size = 0;
        result.meshPart.primitiveType = primitiveType;
        result.meshPart.center.set(0.0f, 0.0f, 0.0f);
        result.meshPart.halfExtents.set(0.0f, 0.0f, 0.0f);
        result.meshPart.radius = -1.0f;
        result.shader = null;
        result.userData = null;
        result.worldTransform.idt();
        return result;
    }
    
    public void end() {
        if (!this.building) {
            throw new GdxRuntimeException("Call begin() prior to calling end()");
        }
        this.building = false;
        if (this.items.size == 0) {
            return;
        }
        this.sorter.sort(this.camera, this.items);
        final int itemCount = this.items.size;
        final int initCount = this.renderables.size;
        final Renderable first = this.items.get(0);
        VertexAttributes vertexAttributes = first.meshPart.mesh.getVertexAttributes();
        Material material = first.material;
        int primitiveType = first.meshPart.primitiveType;
        int offset = this.renderables.size;
        this.meshBuilder.begin(vertexAttributes);
        MeshPart part = this.meshBuilder.part("", primitiveType, this.meshPartPool.obtain());
        this.renderables.add(this.obtainRenderable(material, primitiveType));
        for (int i = 0, n = this.items.size; i < n; ++i) {
            final Renderable renderable = this.items.get(i);
            final VertexAttributes va = renderable.meshPart.mesh.getVertexAttributes();
            final Material mat = renderable.material;
            final int pt = renderable.meshPart.primitiveType;
            final boolean sameMesh = va.equals(vertexAttributes) && renderable.meshPart.size + this.meshBuilder.getNumVertices() < 32767;
            final boolean samePart = sameMesh && pt == primitiveType && mat.same(material, true);
            if (!samePart) {
                if (!sameMesh) {
                    for (Mesh mesh = this.meshBuilder.end(this.meshPool.obtain(vertexAttributes, this.meshBuilder.getNumVertices(), this.meshBuilder.getNumIndices())); offset < this.renderables.size; this.renderables.get(offset++).meshPart.mesh = mesh) {}
                    this.meshBuilder.begin(vertexAttributes = va);
                }
                final MeshPart newPart = this.meshBuilder.part("", pt, this.meshPartPool.obtain());
                final Renderable previous = this.renderables.get(this.renderables.size - 1);
                previous.meshPart.offset = part.offset;
                previous.meshPart.size = part.size;
                part = newPart;
                this.renderables.add(this.obtainRenderable(material = mat, primitiveType = pt));
            }
            this.meshBuilder.setVertexTransform(renderable.worldTransform);
            this.meshBuilder.addMesh(renderable.meshPart.mesh, renderable.meshPart.offset, renderable.meshPart.size);
        }
        for (Mesh mesh2 = this.meshBuilder.end(this.meshPool.obtain(vertexAttributes, this.meshBuilder.getNumVertices(), this.meshBuilder.getNumIndices())); offset < this.renderables.size; this.renderables.get(offset++).meshPart.mesh = mesh2) {}
        final Renderable previous2 = this.renderables.get(this.renderables.size - 1);
        previous2.meshPart.offset = part.offset;
        previous2.meshPart.size = part.size;
    }
    
    public void add(final Renderable renderable) {
        if (!this.building) {
            throw new GdxRuntimeException("Can only add items to the ModelCache in between .begin() and .end()");
        }
        if (renderable.bones == null) {
            this.items.add(renderable);
        }
        else {
            this.renderables.add(renderable);
        }
    }
    
    public void add(final RenderableProvider renderableProvider) {
        renderableProvider.getRenderables(this.tmp, this.renderablesPool);
        for (int i = 0, n = this.tmp.size; i < n; ++i) {
            this.add(this.tmp.get(i));
        }
        this.tmp.clear();
    }
    
    public <T extends RenderableProvider> void add(final Iterable<T> renderableProviders) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            this.add(renderableProvider);
        }
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        if (this.building) {
            throw new GdxRuntimeException("Cannot render a ModelCache in between .begin() and .end()");
        }
        for (final Renderable r : this.renderables) {
            r.shader = null;
            r.environment = null;
        }
        renderables.addAll(this.renderables);
    }
    
    @Override
    public void dispose() {
        if (this.building) {
            throw new GdxRuntimeException("Cannot dispose a ModelCache in between .begin() and .end()");
        }
        this.meshPool.dispose();
    }
    
    public static class Sorter implements RenderableSorter, Comparator<Renderable>
    {
        @Override
        public void sort(final Camera camera, final Array<Renderable> renderables) {
            renderables.sort(this);
        }
        
        @Override
        public int compare(final Renderable arg0, final Renderable arg1) {
            final VertexAttributes va0 = arg0.meshPart.mesh.getVertexAttributes();
            final VertexAttributes va2 = arg1.meshPart.mesh.getVertexAttributes();
            final int vc = va0.compareTo(va2);
            if (vc != 0) {
                return vc;
            }
            final int mc = arg0.material.compareTo((Attributes)arg1.material);
            if (mc == 0) {
                return arg0.meshPart.primitiveType - arg1.meshPart.primitiveType;
            }
            return mc;
        }
    }
    
    public static class SimpleMeshPool implements MeshPool
    {
        private Array<Mesh> freeMeshes;
        private Array<Mesh> usedMeshes;
        
        public SimpleMeshPool() {
            this.freeMeshes = new Array<Mesh>();
            this.usedMeshes = new Array<Mesh>();
        }
        
        @Override
        public void flush() {
            this.freeMeshes.addAll(this.usedMeshes);
            this.usedMeshes.clear();
        }
        
        @Override
        public Mesh obtain(final VertexAttributes vertexAttributes, int vertexCount, int indexCount) {
            for (int i = 0, n = this.freeMeshes.size; i < n; ++i) {
                final Mesh mesh = this.freeMeshes.get(i);
                if (mesh.getVertexAttributes().equals(vertexAttributes) && mesh.getMaxVertices() >= vertexCount && mesh.getMaxIndices() >= indexCount) {
                    this.freeMeshes.removeIndex(i);
                    this.usedMeshes.add(mesh);
                    return mesh;
                }
            }
            vertexCount = 32768;
            indexCount = Math.max(32768, 1 << 32 - Integer.numberOfLeadingZeros(indexCount - 1));
            final Mesh result = new Mesh(false, vertexCount, indexCount, vertexAttributes);
            this.usedMeshes.add(result);
            return result;
        }
        
        @Override
        public void dispose() {
            for (final Mesh m : this.usedMeshes) {
                m.dispose();
            }
            this.usedMeshes.clear();
            for (final Mesh m : this.freeMeshes) {
                m.dispose();
            }
            this.freeMeshes.clear();
        }
    }
    
    public static class TightMeshPool implements MeshPool
    {
        private Array<Mesh> freeMeshes;
        private Array<Mesh> usedMeshes;
        
        public TightMeshPool() {
            this.freeMeshes = new Array<Mesh>();
            this.usedMeshes = new Array<Mesh>();
        }
        
        @Override
        public void flush() {
            this.freeMeshes.addAll(this.usedMeshes);
            this.usedMeshes.clear();
        }
        
        @Override
        public Mesh obtain(final VertexAttributes vertexAttributes, final int vertexCount, final int indexCount) {
            for (int i = 0, n = this.freeMeshes.size; i < n; ++i) {
                final Mesh mesh = this.freeMeshes.get(i);
                if (mesh.getVertexAttributes().equals(vertexAttributes) && mesh.getMaxVertices() == vertexCount && mesh.getMaxIndices() == indexCount) {
                    this.freeMeshes.removeIndex(i);
                    this.usedMeshes.add(mesh);
                    return mesh;
                }
            }
            final Mesh result = new Mesh(true, vertexCount, indexCount, vertexAttributes);
            this.usedMeshes.add(result);
            return result;
        }
        
        @Override
        public void dispose() {
            for (final Mesh m : this.usedMeshes) {
                m.dispose();
            }
            this.usedMeshes.clear();
            for (final Mesh m : this.freeMeshes) {
                m.dispose();
            }
            this.freeMeshes.clear();
        }
    }
    
    public interface MeshPool extends Disposable
    {
        Mesh obtain(final VertexAttributes p0, final int p1, final int p2);
        
        void flush();
    }
}

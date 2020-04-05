// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import java.nio.Buffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Model implements Disposable
{
    public final Array<Material> materials;
    public final Array<Node> nodes;
    public final Array<Animation> animations;
    public final Array<Mesh> meshes;
    public final Array<MeshPart> meshParts;
    protected final Array<Disposable> disposables;
    private ObjectMap<NodePart, ArrayMap<String, Matrix4>> nodePartBones;
    
    public Model() {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.meshes = new Array<Mesh>();
        this.meshParts = new Array<MeshPart>();
        this.disposables = new Array<Disposable>();
        this.nodePartBones = new ObjectMap<NodePart, ArrayMap<String, Matrix4>>();
    }
    
    public Model(final ModelData modelData) {
        this(modelData, new TextureProvider.FileTextureProvider());
    }
    
    public Model(final ModelData modelData, final TextureProvider textureProvider) {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.meshes = new Array<Mesh>();
        this.meshParts = new Array<MeshPart>();
        this.disposables = new Array<Disposable>();
        this.nodePartBones = new ObjectMap<NodePart, ArrayMap<String, Matrix4>>();
        this.load(modelData, textureProvider);
    }
    
    protected void load(final ModelData modelData, final TextureProvider textureProvider) {
        this.loadMeshes(modelData.meshes);
        this.loadMaterials(modelData.materials, textureProvider);
        this.loadNodes(modelData.nodes);
        this.loadAnimations(modelData.animations);
        this.calculateTransforms();
    }
    
    protected void loadAnimations(final Iterable<ModelAnimation> modelAnimations) {
        for (final ModelAnimation anim : modelAnimations) {
            final Animation animation = new Animation();
            animation.id = anim.id;
            for (final ModelNodeAnimation nanim : anim.nodeAnimations) {
                final Node node = this.getNode(nanim.nodeId);
                if (node == null) {
                    continue;
                }
                final NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;
                if (nanim.translation != null) {
                    (nodeAnim.translation = new Array<NodeKeyframe<Vector3>>()).ensureCapacity(nanim.translation.size);
                    for (final ModelNodeKeyframe<Vector3> kf : nanim.translation) {
                        if (kf.keytime > animation.duration) {
                            animation.duration = kf.keytime;
                        }
                        nodeAnim.translation.add(new NodeKeyframe<Vector3>(kf.keytime, new Vector3((kf.value == null) ? node.translation : kf.value)));
                    }
                }
                if (nanim.rotation != null) {
                    (nodeAnim.rotation = new Array<NodeKeyframe<Quaternion>>()).ensureCapacity(nanim.rotation.size);
                    for (final ModelNodeKeyframe<Quaternion> kf2 : nanim.rotation) {
                        if (kf2.keytime > animation.duration) {
                            animation.duration = kf2.keytime;
                        }
                        nodeAnim.rotation.add(new NodeKeyframe<Quaternion>(kf2.keytime, new Quaternion((kf2.value == null) ? node.rotation : kf2.value)));
                    }
                }
                if (nanim.scaling != null) {
                    (nodeAnim.scaling = new Array<NodeKeyframe<Vector3>>()).ensureCapacity(nanim.scaling.size);
                    for (final ModelNodeKeyframe<Vector3> kf : nanim.scaling) {
                        if (kf.keytime > animation.duration) {
                            animation.duration = kf.keytime;
                        }
                        nodeAnim.scaling.add(new NodeKeyframe<Vector3>(kf.keytime, new Vector3((kf.value == null) ? node.scale : kf.value)));
                    }
                }
                if ((nodeAnim.translation == null || nodeAnim.translation.size <= 0) && (nodeAnim.rotation == null || nodeAnim.rotation.size <= 0) && (nodeAnim.scaling == null || nodeAnim.scaling.size <= 0)) {
                    continue;
                }
                animation.nodeAnimations.add(nodeAnim);
            }
            if (animation.nodeAnimations.size > 0) {
                this.animations.add(animation);
            }
        }
    }
    
    protected void loadNodes(final Iterable<ModelNode> modelNodes) {
        this.nodePartBones.clear();
        for (final ModelNode node : modelNodes) {
            this.nodes.add(this.loadNode(node));
        }
        for (final ObjectMap.Entry<NodePart, ArrayMap<String, Matrix4>> e : this.nodePartBones.entries()) {
            if (e.key.invBoneBindTransforms == null) {
                e.key.invBoneBindTransforms = new ArrayMap<Node, Matrix4>(Node.class, Matrix4.class);
            }
            e.key.invBoneBindTransforms.clear();
            for (final ObjectMap.Entry<String, Matrix4> b : e.value.entries()) {
                e.key.invBoneBindTransforms.put(this.getNode(b.key), new Matrix4(b.value).inv());
            }
        }
    }
    
    protected Node loadNode(final ModelNode modelNode) {
        final Node node = new Node();
        node.id = modelNode.id;
        if (modelNode.translation != null) {
            node.translation.set(modelNode.translation);
        }
        if (modelNode.rotation != null) {
            node.rotation.set(modelNode.rotation);
        }
        if (modelNode.scale != null) {
            node.scale.set(modelNode.scale);
        }
        if (modelNode.parts != null) {
            ModelNodePart[] parts;
            for (int length = (parts = modelNode.parts).length, i = 0; i < length; ++i) {
                final ModelNodePart modelNodePart = parts[i];
                MeshPart meshPart = null;
                Material meshMaterial = null;
                if (modelNodePart.meshPartId != null) {
                    for (final MeshPart part : this.meshParts) {
                        if (modelNodePart.meshPartId.equals(part.id)) {
                            meshPart = part;
                            break;
                        }
                    }
                }
                if (modelNodePart.materialId != null) {
                    for (final Material material : this.materials) {
                        if (modelNodePart.materialId.equals(material.id)) {
                            meshMaterial = material;
                            break;
                        }
                    }
                }
                if (meshPart == null || meshMaterial == null) {
                    throw new GdxRuntimeException("Invalid node: " + node.id);
                }
                if (meshPart != null && meshMaterial != null) {
                    final NodePart nodePart = new NodePart();
                    nodePart.meshPart = meshPart;
                    nodePart.material = meshMaterial;
                    node.parts.add(nodePart);
                    if (modelNodePart.bones != null) {
                        this.nodePartBones.put(nodePart, modelNodePart.bones);
                    }
                }
            }
        }
        if (modelNode.children != null) {
            ModelNode[] children;
            for (int length2 = (children = modelNode.children).length, j = 0; j < length2; ++j) {
                final ModelNode child = children[j];
                node.addChild(this.loadNode(child));
            }
        }
        return node;
    }
    
    protected void loadMeshes(final Iterable<ModelMesh> meshes) {
        for (final ModelMesh mesh : meshes) {
            this.convertMesh(mesh);
        }
    }
    
    protected void convertMesh(final ModelMesh modelMesh) {
        int numIndices = 0;
        ModelMeshPart[] parts;
        for (int length = (parts = modelMesh.parts).length, i = 0; i < length; ++i) {
            final ModelMeshPart part = parts[i];
            numIndices += part.indices.length;
        }
        final VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
        final int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);
        final Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
        this.meshes.add(mesh);
        this.disposables.add(mesh);
        BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
        int offset = 0;
        mesh.getIndicesBuffer().clear();
        ModelMeshPart[] parts2;
        for (int length2 = (parts2 = modelMesh.parts).length, j = 0; j < length2; ++j) {
            final ModelMeshPart part2 = parts2[j];
            final MeshPart meshPart = new MeshPart();
            meshPart.id = part2.id;
            meshPart.primitiveType = part2.primitiveType;
            meshPart.offset = offset;
            meshPart.size = part2.indices.length;
            meshPart.mesh = mesh;
            mesh.getIndicesBuffer().put(part2.indices);
            offset += meshPart.size;
            this.meshParts.add(meshPart);
        }
        mesh.getIndicesBuffer().position(0);
        for (final MeshPart part3 : this.meshParts) {
            part3.update();
        }
    }
    
    protected void loadMaterials(final Iterable<ModelMaterial> modelMaterials, final TextureProvider textureProvider) {
        for (final ModelMaterial mtl : modelMaterials) {
            this.materials.add(this.convertMaterial(mtl, textureProvider));
        }
    }
    
    protected Material convertMaterial(final ModelMaterial mtl, final TextureProvider textureProvider) {
        final Material result = new Material();
        result.id = mtl.id;
        if (mtl.ambient != null) {
            result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
        }
        if (mtl.diffuse != null) {
            result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
        }
        if (mtl.specular != null) {
            result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
        }
        if (mtl.emissive != null) {
            result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
        }
        if (mtl.reflection != null) {
            result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
        }
        if (mtl.shininess > 0.0f) {
            result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
        }
        if (mtl.opacity != 1.0f) {
            result.set(new BlendingAttribute(770, 771, mtl.opacity));
        }
        final ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
        if (mtl.textures != null) {
            for (final ModelTexture tex : mtl.textures) {
                Texture texture;
                if (textures.containsKey(tex.fileName)) {
                    texture = textures.get(tex.fileName);
                }
                else {
                    texture = textureProvider.load(tex.fileName);
                    textures.put(tex.fileName, texture);
                    this.disposables.add(texture);
                }
                final TextureDescriptor descriptor = new TextureDescriptor((T)texture);
                descriptor.minFilter = texture.getMinFilter();
                descriptor.magFilter = texture.getMagFilter();
                descriptor.uWrap = texture.getUWrap();
                descriptor.vWrap = texture.getVWrap();
                final float offsetU = (tex.uvTranslation == null) ? 0.0f : tex.uvTranslation.x;
                final float offsetV = (tex.uvTranslation == null) ? 0.0f : tex.uvTranslation.y;
                final float scaleU = (tex.uvScaling == null) ? 1.0f : tex.uvScaling.x;
                final float scaleV = (tex.uvScaling == null) ? 1.0f : tex.uvScaling.y;
                switch (tex.usage) {
                    default: {
                        continue;
                    }
                    case 2: {
                        result.set(new TextureAttribute(TextureAttribute.Diffuse, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 5: {
                        result.set(new TextureAttribute(TextureAttribute.Specular, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 8: {
                        result.set(new TextureAttribute(TextureAttribute.Bump, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 7: {
                        result.set(new TextureAttribute(TextureAttribute.Normal, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 4: {
                        result.set(new TextureAttribute(TextureAttribute.Ambient, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 3: {
                        result.set(new TextureAttribute(TextureAttribute.Emissive, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                    case 10: {
                        result.set(new TextureAttribute(TextureAttribute.Reflection, descriptor, offsetU, offsetV, scaleU, scaleV));
                        continue;
                    }
                }
            }
        }
        return result;
    }
    
    public void manageDisposable(final Disposable disposable) {
        if (!this.disposables.contains(disposable, true)) {
            this.disposables.add(disposable);
        }
    }
    
    public Iterable<Disposable> getManagedDisposables() {
        return this.disposables;
    }
    
    @Override
    public void dispose() {
        for (final Disposable disposable : this.disposables) {
            disposable.dispose();
        }
    }
    
    public void calculateTransforms() {
        final int n = this.nodes.size;
        for (int i = 0; i < n; ++i) {
            this.nodes.get(i).calculateTransforms(true);
        }
        for (int i = 0; i < n; ++i) {
            this.nodes.get(i).calculateBoneTransforms(true);
        }
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out) {
        out.inf();
        return this.extendBoundingBox(out);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out) {
        for (int n = this.nodes.size, i = 0; i < n; ++i) {
            this.nodes.get(i).extendBoundingBox(out);
        }
        return out;
    }
    
    public Animation getAnimation(final String id) {
        return this.getAnimation(id, true);
    }
    
    public Animation getAnimation(final String id, final boolean ignoreCase) {
        final int n = this.animations.size;
        if (ignoreCase) {
            for (int i = 0; i < n; ++i) {
                final Animation animation;
                if ((animation = this.animations.get(i)).id.equalsIgnoreCase(id)) {
                    return animation;
                }
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Animation animation;
                if ((animation = this.animations.get(i)).id.equals(id)) {
                    return animation;
                }
            }
        }
        return null;
    }
    
    public Material getMaterial(final String id) {
        return this.getMaterial(id, true);
    }
    
    public Material getMaterial(final String id, final boolean ignoreCase) {
        final int n = this.materials.size;
        if (ignoreCase) {
            for (int i = 0; i < n; ++i) {
                final Material material;
                if ((material = this.materials.get(i)).id.equalsIgnoreCase(id)) {
                    return material;
                }
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Material material;
                if ((material = this.materials.get(i)).id.equals(id)) {
                    return material;
                }
            }
        }
        return null;
    }
    
    public Node getNode(final String id) {
        return this.getNode(id, true);
    }
    
    public Node getNode(final String id, final boolean recursive) {
        return this.getNode(id, recursive, false);
    }
    
    public Node getNode(final String id, final boolean recursive, final boolean ignoreCase) {
        return Node.getNode(this.nodes, id, recursive, ignoreCase);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.VertexAttribute;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.assets.loaders.ModelLoader;

public class G3dModelLoader extends ModelLoader<ModelParameters>
{
    public static final short VERSION_HI = 0;
    public static final short VERSION_LO = 1;
    protected final BaseJsonReader reader;
    private final Quaternion tempQ;
    
    public G3dModelLoader(final BaseJsonReader reader) {
        this(reader, null);
    }
    
    public G3dModelLoader(final BaseJsonReader reader, final FileHandleResolver resolver) {
        super(resolver);
        this.tempQ = new Quaternion();
        this.reader = reader;
    }
    
    @Override
    public ModelData loadModelData(final FileHandle fileHandle, final ModelParameters parameters) {
        return this.parseModel(fileHandle);
    }
    
    public ModelData parseModel(final FileHandle handle) {
        final JsonValue json = this.reader.parse(handle);
        final ModelData model = new ModelData();
        final JsonValue version = json.require("version");
        model.version[0] = version.getShort(0);
        model.version[1] = version.getShort(1);
        if (model.version[0] != 0 || model.version[1] != 1) {
            throw new GdxRuntimeException("Model version not supported");
        }
        model.id = json.getString("id", "");
        this.parseMeshes(model, json);
        this.parseMaterials(model, json, handle.parent().path());
        this.parseNodes(model, json);
        this.parseAnimations(model, json);
        return model;
    }
    
    private void parseMeshes(final ModelData model, final JsonValue json) {
        final JsonValue meshes = json.get("meshes");
        if (meshes != null) {
            model.meshes.ensureCapacity(meshes.size);
            for (JsonValue mesh = meshes.child; mesh != null; mesh = mesh.next) {
                final ModelMesh jsonMesh = new ModelMesh();
                final String id = mesh.getString("id", "");
                jsonMesh.id = id;
                final JsonValue attributes = mesh.require("attributes");
                jsonMesh.attributes = this.parseAttributes(attributes);
                jsonMesh.vertices = mesh.require("vertices").asFloatArray();
                final JsonValue meshParts = mesh.require("parts");
                final Array<ModelMeshPart> parts = new Array<ModelMeshPart>();
                for (JsonValue meshPart = meshParts.child; meshPart != null; meshPart = meshPart.next) {
                    final ModelMeshPart jsonPart = new ModelMeshPart();
                    final String partId = meshPart.getString("id", null);
                    if (partId == null) {
                        throw new GdxRuntimeException("Not id given for mesh part");
                    }
                    for (final ModelMeshPart other : parts) {
                        if (other.id.equals(partId)) {
                            throw new GdxRuntimeException("Mesh part with id '" + partId + "' already in defined");
                        }
                    }
                    jsonPart.id = partId;
                    final String type = meshPart.getString("type", null);
                    if (type == null) {
                        throw new GdxRuntimeException("No primitive type given for mesh part '" + partId + "'");
                    }
                    jsonPart.primitiveType = this.parseType(type);
                    jsonPart.indices = meshPart.require("indices").asShortArray();
                    parts.add(jsonPart);
                }
                jsonMesh.parts = parts.toArray(ModelMeshPart.class);
                model.meshes.add(jsonMesh);
            }
        }
    }
    
    private int parseType(final String type) {
        if (type.equals("TRIANGLES")) {
            return 4;
        }
        if (type.equals("LINES")) {
            return 1;
        }
        if (type.equals("POINTS")) {
            return 0;
        }
        if (type.equals("TRIANGLE_STRIP")) {
            return 5;
        }
        if (type.equals("LINE_STRIP")) {
            return 3;
        }
        throw new GdxRuntimeException("Unknown primitive type '" + type + "', should be one of triangle, trianglestrip, line, linestrip, lineloop or point");
    }
    
    private VertexAttribute[] parseAttributes(final JsonValue attributes) {
        final Array<VertexAttribute> vertexAttributes = new Array<VertexAttribute>();
        int unit = 0;
        int blendWeightCount = 0;
        for (JsonValue value = attributes.child; value != null; value = value.next) {
            final String attr;
            final String attribute = attr = value.asString();
            if (attr.equals("POSITION")) {
                vertexAttributes.add(VertexAttribute.Position());
            }
            else if (attr.equals("NORMAL")) {
                vertexAttributes.add(VertexAttribute.Normal());
            }
            else if (attr.equals("COLOR")) {
                vertexAttributes.add(VertexAttribute.ColorUnpacked());
            }
            else if (attr.equals("COLORPACKED")) {
                vertexAttributes.add(VertexAttribute.ColorPacked());
            }
            else if (attr.equals("TANGENT")) {
                vertexAttributes.add(VertexAttribute.Tangent());
            }
            else if (attr.equals("BINORMAL")) {
                vertexAttributes.add(VertexAttribute.Binormal());
            }
            else if (attr.startsWith("TEXCOORD")) {
                vertexAttributes.add(VertexAttribute.TexCoords(unit++));
            }
            else {
                if (!attr.startsWith("BLENDWEIGHT")) {
                    throw new GdxRuntimeException("Unknown vertex attribute '" + attr + "', should be one of position, normal, uv, tangent or binormal");
                }
                vertexAttributes.add(VertexAttribute.BoneWeight(blendWeightCount++));
            }
        }
        return vertexAttributes.toArray(VertexAttribute.class);
    }
    
    private void parseMaterials(final ModelData model, final JsonValue json, final String materialDir) {
        final JsonValue materials = json.get("materials");
        if (materials != null) {
            model.materials.ensureCapacity(materials.size);
            for (JsonValue material = materials.child; material != null; material = material.next) {
                final ModelMaterial jsonMaterial = new ModelMaterial();
                final String id = material.getString("id", null);
                if (id == null) {
                    throw new GdxRuntimeException("Material needs an id.");
                }
                jsonMaterial.id = id;
                final JsonValue diffuse = material.get("diffuse");
                if (diffuse != null) {
                    jsonMaterial.diffuse = this.parseColor(diffuse);
                }
                final JsonValue ambient = material.get("ambient");
                if (ambient != null) {
                    jsonMaterial.ambient = this.parseColor(ambient);
                }
                final JsonValue emissive = material.get("emissive");
                if (emissive != null) {
                    jsonMaterial.emissive = this.parseColor(emissive);
                }
                final JsonValue specular = material.get("specular");
                if (specular != null) {
                    jsonMaterial.specular = this.parseColor(specular);
                }
                final JsonValue reflection = material.get("reflection");
                if (reflection != null) {
                    jsonMaterial.reflection = this.parseColor(reflection);
                }
                jsonMaterial.shininess = material.getFloat("shininess", 0.0f);
                jsonMaterial.opacity = material.getFloat("opacity", 1.0f);
                final JsonValue textures = material.get("textures");
                if (textures != null) {
                    for (JsonValue texture = textures.child; texture != null; texture = texture.next) {
                        final ModelTexture jsonTexture = new ModelTexture();
                        final String textureId = texture.getString("id", null);
                        if (textureId == null) {
                            throw new GdxRuntimeException("Texture has no id.");
                        }
                        jsonTexture.id = textureId;
                        final String fileName = texture.getString("filename", null);
                        if (fileName == null) {
                            throw new GdxRuntimeException("Texture needs filename.");
                        }
                        jsonTexture.fileName = String.valueOf(materialDir) + ((materialDir.length() == 0 || materialDir.endsWith("/")) ? "" : "/") + fileName;
                        jsonTexture.uvTranslation = this.readVector2(texture.get("uvTranslation"), 0.0f, 0.0f);
                        jsonTexture.uvScaling = this.readVector2(texture.get("uvScaling"), 1.0f, 1.0f);
                        final String textureType = texture.getString("type", null);
                        if (textureType == null) {
                            throw new GdxRuntimeException("Texture needs type.");
                        }
                        jsonTexture.usage = this.parseTextureUsage(textureType);
                        if (jsonMaterial.textures == null) {
                            jsonMaterial.textures = new Array<ModelTexture>();
                        }
                        jsonMaterial.textures.add(jsonTexture);
                    }
                }
                model.materials.add(jsonMaterial);
            }
        }
    }
    
    private int parseTextureUsage(final String value) {
        if (value.equalsIgnoreCase("AMBIENT")) {
            return 4;
        }
        if (value.equalsIgnoreCase("BUMP")) {
            return 8;
        }
        if (value.equalsIgnoreCase("DIFFUSE")) {
            return 2;
        }
        if (value.equalsIgnoreCase("EMISSIVE")) {
            return 3;
        }
        if (value.equalsIgnoreCase("NONE")) {
            return 1;
        }
        if (value.equalsIgnoreCase("NORMAL")) {
            return 7;
        }
        if (value.equalsIgnoreCase("REFLECTION")) {
            return 10;
        }
        if (value.equalsIgnoreCase("SHININESS")) {
            return 6;
        }
        if (value.equalsIgnoreCase("SPECULAR")) {
            return 5;
        }
        if (value.equalsIgnoreCase("TRANSPARENCY")) {
            return 9;
        }
        return 0;
    }
    
    private Color parseColor(final JsonValue colorArray) {
        if (colorArray.size >= 3) {
            return new Color(colorArray.getFloat(0), colorArray.getFloat(1), colorArray.getFloat(2), 1.0f);
        }
        throw new GdxRuntimeException("Expected Color values <> than three.");
    }
    
    private Vector2 readVector2(final JsonValue vectorArray, final float x, final float y) {
        if (vectorArray == null) {
            return new Vector2(x, y);
        }
        if (vectorArray.size == 2) {
            return new Vector2(vectorArray.getFloat(0), vectorArray.getFloat(1));
        }
        throw new GdxRuntimeException("Expected Vector2 values <> than two.");
    }
    
    private Array<ModelNode> parseNodes(final ModelData model, final JsonValue json) {
        final JsonValue nodes = json.get("nodes");
        if (nodes != null) {
            model.nodes.ensureCapacity(nodes.size);
            for (JsonValue node = nodes.child; node != null; node = node.next) {
                model.nodes.add(this.parseNodesRecursively(node));
            }
        }
        return model.nodes;
    }
    
    private ModelNode parseNodesRecursively(final JsonValue json) {
        final ModelNode jsonNode = new ModelNode();
        final String id = json.getString("id", null);
        if (id == null) {
            throw new GdxRuntimeException("Node id missing.");
        }
        jsonNode.id = id;
        final JsonValue translation = json.get("translation");
        if (translation != null && translation.size != 3) {
            throw new GdxRuntimeException("Node translation incomplete");
        }
        jsonNode.translation = ((translation == null) ? null : new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2)));
        final JsonValue rotation = json.get("rotation");
        if (rotation != null && rotation.size != 4) {
            throw new GdxRuntimeException("Node rotation incomplete");
        }
        jsonNode.rotation = ((rotation == null) ? null : new Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3)));
        final JsonValue scale = json.get("scale");
        if (scale != null && scale.size != 3) {
            throw new GdxRuntimeException("Node scale incomplete");
        }
        jsonNode.scale = ((scale == null) ? null : new Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2)));
        final String meshId = json.getString("mesh", null);
        if (meshId != null) {
            jsonNode.meshId = meshId;
        }
        final JsonValue materials = json.get("parts");
        if (materials != null) {
            jsonNode.parts = new ModelNodePart[materials.size];
            int i = 0;
            for (JsonValue material = materials.child; material != null; material = material.next, ++i) {
                final ModelNodePart nodePart = new ModelNodePart();
                final String meshPartId = material.getString("meshpartid", null);
                final String materialId = material.getString("materialid", null);
                if (meshPartId == null || materialId == null) {
                    throw new GdxRuntimeException("Node " + id + " part is missing meshPartId or materialId");
                }
                nodePart.materialId = materialId;
                nodePart.meshPartId = meshPartId;
                final JsonValue bones = material.get("bones");
                if (bones != null) {
                    nodePart.bones = new ArrayMap<String, Matrix4>(true, bones.size, String.class, Matrix4.class);
                    int j = 0;
                    for (JsonValue bone = bones.child; bone != null; bone = bone.next, ++j) {
                        final String nodeId = bone.getString("node", null);
                        if (nodeId == null) {
                            throw new GdxRuntimeException("Bone node ID missing");
                        }
                        final Matrix4 transform = new Matrix4();
                        JsonValue val = bone.get("translation");
                        if (val != null && val.size >= 3) {
                            transform.translate(val.getFloat(0), val.getFloat(1), val.getFloat(2));
                        }
                        val = bone.get("rotation");
                        if (val != null && val.size >= 4) {
                            transform.rotate(this.tempQ.set(val.getFloat(0), val.getFloat(1), val.getFloat(2), val.getFloat(3)));
                        }
                        val = bone.get("scale");
                        if (val != null && val.size >= 3) {
                            transform.scale(val.getFloat(0), val.getFloat(1), val.getFloat(2));
                        }
                        nodePart.bones.put(nodeId, transform);
                    }
                }
                jsonNode.parts[i] = nodePart;
            }
        }
        final JsonValue children = json.get("children");
        if (children != null) {
            jsonNode.children = new ModelNode[children.size];
            int k = 0;
            for (JsonValue child = children.child; child != null; child = child.next, ++k) {
                jsonNode.children[k] = this.parseNodesRecursively(child);
            }
        }
        return jsonNode;
    }
    
    private void parseAnimations(final ModelData model, final JsonValue json) {
        final JsonValue animations = json.get("animations");
        if (animations == null) {
            return;
        }
        model.animations.ensureCapacity(animations.size);
        for (JsonValue anim = animations.child; anim != null; anim = anim.next) {
            final JsonValue nodes = anim.get("bones");
            if (nodes != null) {
                final ModelAnimation animation = new ModelAnimation();
                model.animations.add(animation);
                animation.nodeAnimations.ensureCapacity(nodes.size);
                animation.id = anim.getString("id");
                for (JsonValue node = nodes.child; node != null; node = node.next) {
                    final ModelNodeAnimation nodeAnim = new ModelNodeAnimation();
                    animation.nodeAnimations.add(nodeAnim);
                    nodeAnim.nodeId = node.getString("boneId");
                    final JsonValue keyframes = node.get("keyframes");
                    if (keyframes != null && keyframes.isArray()) {
                        for (JsonValue keyframe = keyframes.child; keyframe != null; keyframe = keyframe.next) {
                            final float keytime = keyframe.getFloat("keytime", 0.0f) / 1000.0f;
                            final JsonValue translation = keyframe.get("translation");
                            if (translation != null && translation.size == 3) {
                                if (nodeAnim.translation == null) {
                                    nodeAnim.translation = new Array<ModelNodeKeyframe<Vector3>>();
                                }
                                final ModelNodeKeyframe<Vector3> tkf = new ModelNodeKeyframe<Vector3>();
                                tkf.keytime = keytime;
                                tkf.value = new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
                                nodeAnim.translation.add(tkf);
                            }
                            final JsonValue rotation = keyframe.get("rotation");
                            if (rotation != null && rotation.size == 4) {
                                if (nodeAnim.rotation == null) {
                                    nodeAnim.rotation = new Array<ModelNodeKeyframe<Quaternion>>();
                                }
                                final ModelNodeKeyframe<Quaternion> rkf = new ModelNodeKeyframe<Quaternion>();
                                rkf.keytime = keytime;
                                rkf.value = new Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));
                                nodeAnim.rotation.add(rkf);
                            }
                            final JsonValue scale = keyframe.get("scale");
                            if (scale != null && scale.size == 3) {
                                if (nodeAnim.scaling == null) {
                                    nodeAnim.scaling = new Array<ModelNodeKeyframe<Vector3>>();
                                }
                                final ModelNodeKeyframe<Vector3> skf = new ModelNodeKeyframe<Vector3>();
                                skf.keytime = keytime;
                                skf.value = new Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                                nodeAnim.scaling.add(skf);
                            }
                        }
                    }
                    else {
                        final JsonValue translationKF = node.get("translation");
                        if (translationKF != null && translationKF.isArray()) {
                            (nodeAnim.translation = new Array<ModelNodeKeyframe<Vector3>>()).ensureCapacity(translationKF.size);
                            for (JsonValue keyframe2 = translationKF.child; keyframe2 != null; keyframe2 = keyframe2.next) {
                                final ModelNodeKeyframe<Vector3> kf = new ModelNodeKeyframe<Vector3>();
                                nodeAnim.translation.add(kf);
                                kf.keytime = keyframe2.getFloat("keytime", 0.0f) / 1000.0f;
                                final JsonValue translation2 = keyframe2.get("value");
                                if (translation2 != null && translation2.size >= 3) {
                                    kf.value = new Vector3(translation2.getFloat(0), translation2.getFloat(1), translation2.getFloat(2));
                                }
                            }
                        }
                        final JsonValue rotationKF = node.get("rotation");
                        if (rotationKF != null && rotationKF.isArray()) {
                            (nodeAnim.rotation = new Array<ModelNodeKeyframe<Quaternion>>()).ensureCapacity(rotationKF.size);
                            for (JsonValue keyframe3 = rotationKF.child; keyframe3 != null; keyframe3 = keyframe3.next) {
                                final ModelNodeKeyframe<Quaternion> kf2 = new ModelNodeKeyframe<Quaternion>();
                                nodeAnim.rotation.add(kf2);
                                kf2.keytime = keyframe3.getFloat("keytime", 0.0f) / 1000.0f;
                                final JsonValue rotation2 = keyframe3.get("value");
                                if (rotation2 != null && rotation2.size >= 4) {
                                    kf2.value = new Quaternion(rotation2.getFloat(0), rotation2.getFloat(1), rotation2.getFloat(2), rotation2.getFloat(3));
                                }
                            }
                        }
                        final JsonValue scalingKF = node.get("scaling");
                        if (scalingKF != null && scalingKF.isArray()) {
                            (nodeAnim.scaling = new Array<ModelNodeKeyframe<Vector3>>()).ensureCapacity(scalingKF.size);
                            for (JsonValue keyframe4 = scalingKF.child; keyframe4 != null; keyframe4 = keyframe4.next) {
                                final ModelNodeKeyframe<Vector3> kf3 = new ModelNodeKeyframe<Vector3>();
                                nodeAnim.scaling.add(kf3);
                                kf3.keytime = keyframe4.getFloat("keytime", 0.0f) / 1000.0f;
                                final JsonValue scaling = keyframe4.get("value");
                                if (scaling != null && scaling.size >= 3) {
                                    kf3.value = new Vector3(scaling.getFloat(0), scaling.getFloat(1), scaling.getFloat(2));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

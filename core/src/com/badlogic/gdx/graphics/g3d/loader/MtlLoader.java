// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.loader;

import java.util.Iterator;
import java.io.IOException;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.utils.Array;

class MtlLoader
{
    public Array<ModelMaterial> materials;
    
    MtlLoader() {
        this.materials = new Array<ModelMaterial>();
    }
    
    public void load(final FileHandle file) {
        String curMatName = "default";
        Color difcolor = Color.WHITE;
        Color speccolor = Color.WHITE;
        float opacity = 1.0f;
        float shininess = 0.0f;
        String texFilename = null;
        if (file == null || !file.exists()) {
            return;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && line.charAt(0) == '\t') {
                    line = line.substring(1).trim();
                }
                final String[] tokens = line.split("\\s+");
                if (tokens[0].length() == 0) {
                    continue;
                }
                if (tokens[0].charAt(0) == '#') {
                    continue;
                }
                final String key = tokens[0].toLowerCase();
                if (key.equals("newmtl")) {
                    final ModelMaterial mat = new ModelMaterial();
                    mat.id = curMatName;
                    mat.diffuse = new Color(difcolor);
                    mat.specular = new Color(speccolor);
                    mat.opacity = opacity;
                    mat.shininess = shininess;
                    if (texFilename != null) {
                        final ModelTexture tex = new ModelTexture();
                        tex.usage = 2;
                        tex.fileName = new String(texFilename);
                        if (mat.textures == null) {
                            mat.textures = new Array<ModelTexture>(1);
                        }
                        mat.textures.add(tex);
                    }
                    this.materials.add(mat);
                    if (tokens.length > 1) {
                        curMatName = tokens[1];
                        curMatName = curMatName.replace('.', '_');
                    }
                    else {
                        curMatName = "default";
                    }
                    difcolor = Color.WHITE;
                    speccolor = Color.WHITE;
                    opacity = 1.0f;
                    shininess = 0.0f;
                }
                else if (key.equals("kd") || key.equals("ks")) {
                    final float r = Float.parseFloat(tokens[1]);
                    final float g = Float.parseFloat(tokens[2]);
                    final float b = Float.parseFloat(tokens[3]);
                    float a = 1.0f;
                    if (tokens.length > 4) {
                        a = Float.parseFloat(tokens[4]);
                    }
                    if (tokens[0].toLowerCase().equals("kd")) {
                        difcolor = new Color();
                        difcolor.set(r, g, b, a);
                    }
                    else {
                        speccolor = new Color();
                        speccolor.set(r, g, b, a);
                    }
                }
                else if (key.equals("tr") || key.equals("d")) {
                    opacity = Float.parseFloat(tokens[1]);
                }
                else if (key.equals("ns")) {
                    shininess = Float.parseFloat(tokens[1]);
                }
                else {
                    if (!key.equals("map_kd")) {
                        continue;
                    }
                    texFilename = file.parent().child(tokens[1]).path();
                }
            }
            reader.close();
        }
        catch (IOException e) {
            return;
        }
        final ModelMaterial mat2 = new ModelMaterial();
        mat2.id = curMatName;
        mat2.diffuse = new Color(difcolor);
        mat2.specular = new Color(speccolor);
        mat2.opacity = opacity;
        mat2.shininess = shininess;
        if (texFilename != null) {
            final ModelTexture tex2 = new ModelTexture();
            tex2.usage = 2;
            tex2.fileName = new String(texFilename);
            if (mat2.textures == null) {
                mat2.textures = new Array<ModelTexture>(1);
            }
            mat2.textures.add(tex2);
        }
        this.materials.add(mat2);
    }
    
    public ModelMaterial getMaterial(final String name) {
        for (final ModelMaterial m : this.materials) {
            if (m.id.equals(name)) {
                return m;
            }
        }
        final ModelMaterial mat = new ModelMaterial();
        mat.id = name;
        mat.diffuse = new Color(Color.WHITE);
        this.materials.add(mat);
        return mat;
    }
}

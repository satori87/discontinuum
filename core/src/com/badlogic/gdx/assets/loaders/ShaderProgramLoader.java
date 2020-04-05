// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderProgramLoader extends AsynchronousAssetLoader<ShaderProgram, ShaderProgramParameter>
{
    private String vertexFileSuffix;
    private String fragmentFileSuffix;
    
    public ShaderProgramLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.vertexFileSuffix = ".vert";
        this.fragmentFileSuffix = ".frag";
    }
    
    public ShaderProgramLoader(final FileHandleResolver resolver, final String vertexFileSuffix, final String fragmentFileSuffix) {
        super(resolver);
        this.vertexFileSuffix = ".vert";
        this.fragmentFileSuffix = ".frag";
        this.vertexFileSuffix = vertexFileSuffix;
        this.fragmentFileSuffix = fragmentFileSuffix;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final ShaderProgramParameter parameter) {
        return null;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final ShaderProgramParameter parameter) {
    }
    
    @Override
    public ShaderProgram loadSync(final AssetManager manager, final String fileName, final FileHandle file, final ShaderProgramParameter parameter) {
        String vertFileName = null;
        String fragFileName = null;
        if (parameter != null) {
            if (parameter.vertexFile != null) {
                vertFileName = parameter.vertexFile;
            }
            if (parameter.fragmentFile != null) {
                fragFileName = parameter.fragmentFile;
            }
        }
        if (vertFileName == null && fileName.endsWith(this.fragmentFileSuffix)) {
            vertFileName = String.valueOf(fileName.substring(0, fileName.length() - this.fragmentFileSuffix.length())) + this.vertexFileSuffix;
        }
        if (fragFileName == null && fileName.endsWith(this.vertexFileSuffix)) {
            fragFileName = String.valueOf(fileName.substring(0, fileName.length() - this.vertexFileSuffix.length())) + this.fragmentFileSuffix;
        }
        final FileHandle vertexFile = (vertFileName == null) ? file : this.resolve(vertFileName);
        final FileHandle fragmentFile = (fragFileName == null) ? file : this.resolve(fragFileName);
        String vertexCode = vertexFile.readString();
        String fragmentCode = vertexFile.equals(fragmentFile) ? vertexCode : fragmentFile.readString();
        if (parameter != null) {
            if (parameter.prependVertexCode != null) {
                vertexCode = String.valueOf(parameter.prependVertexCode) + vertexCode;
            }
            if (parameter.prependFragmentCode != null) {
                fragmentCode = String.valueOf(parameter.prependFragmentCode) + fragmentCode;
            }
        }
        final ShaderProgram shaderProgram = new ShaderProgram(vertexCode, fragmentCode);
        if ((parameter == null || parameter.logOnCompileFailure) && !shaderProgram.isCompiled()) {
            manager.getLogger().error("ShaderProgram " + fileName + " failed to compile:\n" + shaderProgram.getLog());
        }
        return shaderProgram;
    }
    
    public static class ShaderProgramParameter extends AssetLoaderParameters<ShaderProgram>
    {
        public String vertexFile;
        public String fragmentFile;
        public boolean logOnCompileFailure;
        public String prependVertexCode;
        public String prependFragmentCode;
        
        public ShaderProgramParameter() {
            this.logOnCompileFailure = true;
        }
    }
}

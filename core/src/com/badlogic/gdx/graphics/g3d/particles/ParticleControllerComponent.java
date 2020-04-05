// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Disposable;

public abstract class ParticleControllerComponent implements Disposable, Json.Serializable, ResourceData.Configurable
{
    protected static final Vector3 TMP_V1;
    protected static final Vector3 TMP_V2;
    protected static final Vector3 TMP_V3;
    protected static final Vector3 TMP_V4;
    protected static final Vector3 TMP_V5;
    protected static final Vector3 TMP_V6;
    protected static final Quaternion TMP_Q;
    protected static final Quaternion TMP_Q2;
    protected static final Matrix3 TMP_M3;
    protected static final Matrix4 TMP_M4;
    protected ParticleController controller;
    
    static {
        TMP_V1 = new Vector3();
        TMP_V2 = new Vector3();
        TMP_V3 = new Vector3();
        TMP_V4 = new Vector3();
        TMP_V5 = new Vector3();
        TMP_V6 = new Vector3();
        TMP_Q = new Quaternion();
        TMP_Q2 = new Quaternion();
        TMP_M3 = new Matrix3();
        TMP_M4 = new Matrix4();
    }
    
    public void activateParticles(final int startIndex, final int count) {
    }
    
    public void killParticles(final int startIndex, final int count) {
    }
    
    public void update() {
    }
    
    public void init() {
    }
    
    public void start() {
    }
    
    public void end() {
    }
    
    @Override
    public void dispose() {
    }
    
    public abstract ParticleControllerComponent copy();
    
    public void allocateChannels() {
    }
    
    public void set(final ParticleController particleController) {
        this.controller = particleController;
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData data) {
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData data) {
    }
    
    @Override
    public void write(final Json json) {
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
    }
}

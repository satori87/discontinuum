// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class DynamicsModifier extends Influencer
{
    protected static final Vector3 TMP_V1;
    protected static final Vector3 TMP_V2;
    protected static final Vector3 TMP_V3;
    protected static final Quaternion TMP_Q;
    public boolean isGlobal;
    protected ParallelArray.FloatChannel lifeChannel;
    
    static {
        TMP_V1 = new Vector3();
        TMP_V2 = new Vector3();
        TMP_V3 = new Vector3();
        TMP_Q = new Quaternion();
    }
    
    public DynamicsModifier() {
        this.isGlobal = false;
    }
    
    public DynamicsModifier(final DynamicsModifier modifier) {
        this.isGlobal = false;
        this.isGlobal = modifier.isGlobal;
    }
    
    @Override
    public void allocateChannels() {
        this.lifeChannel = this.controller.particles.addChannel(ParticleChannels.Life);
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("isGlobal", this.isGlobal);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.isGlobal = json.readValue("isGlobal", Boolean.TYPE, jsonData);
    }
    
    public static class BrownianAcceleration extends Strength
    {
        ParallelArray.FloatChannel accelerationChannel;
        
        public BrownianAcceleration() {
        }
        
        public BrownianAcceleration(final BrownianAcceleration rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.accelerationChannel = this.controller.particles.addChannel(ParticleChannels.Acceleration);
        }
        
        @Override
        public void update() {
            for (int lifeOffset = 2, strengthOffset = 0, forceOffset = 0, i = 0, c = this.controller.particles.size; i < c; ++i, strengthOffset += this.strengthChannel.strideSize, forceOffset += this.accelerationChannel.strideSize, lifeOffset += this.lifeChannel.strideSize) {
                final float strength = this.strengthChannel.data[strengthOffset + 0] + this.strengthChannel.data[strengthOffset + 1] * this.strengthValue.getScale(this.lifeChannel.data[lifeOffset]);
                BrownianAcceleration.TMP_V3.set(MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f), MathUtils.random(-1.0f, 1.0f)).nor().scl(strength);
                final float[] data = this.accelerationChannel.data;
                final int n = forceOffset + 0;
                data[n] += BrownianAcceleration.TMP_V3.x;
                final float[] data2 = this.accelerationChannel.data;
                final int n2 = forceOffset + 1;
                data2[n2] += BrownianAcceleration.TMP_V3.y;
                final float[] data3 = this.accelerationChannel.data;
                final int n3 = forceOffset + 2;
                data3[n3] += BrownianAcceleration.TMP_V3.z;
            }
        }
        
        @Override
        public BrownianAcceleration copy() {
            return new BrownianAcceleration(this);
        }
    }
    
    public static class CentripetalAcceleration extends Strength
    {
        ParallelArray.FloatChannel accelerationChannel;
        ParallelArray.FloatChannel positionChannel;
        
        public CentripetalAcceleration() {
        }
        
        public CentripetalAcceleration(final CentripetalAcceleration rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.accelerationChannel = this.controller.particles.addChannel(ParticleChannels.Acceleration);
            this.positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
        }
        
        @Override
        public void update() {
            float cx = 0.0f;
            float cy = 0.0f;
            float cz = 0.0f;
            if (!this.isGlobal) {
                final float[] val = this.controller.transform.val;
                cx = val[12];
                cy = val[13];
                cz = val[14];
            }
            for (int lifeOffset = 2, strengthOffset = 0, positionOffset = 0, forceOffset = 0, i = 0, c = this.controller.particles.size; i < c; ++i, positionOffset += this.positionChannel.strideSize, strengthOffset += this.strengthChannel.strideSize, forceOffset += this.accelerationChannel.strideSize, lifeOffset += this.lifeChannel.strideSize) {
                final float strength = this.strengthChannel.data[strengthOffset + 0] + this.strengthChannel.data[strengthOffset + 1] * this.strengthValue.getScale(this.lifeChannel.data[lifeOffset]);
                CentripetalAcceleration.TMP_V3.set(this.positionChannel.data[positionOffset + 0] - cx, this.positionChannel.data[positionOffset + 1] - cy, this.positionChannel.data[positionOffset + 2] - cz).nor().scl(strength);
                final float[] data = this.accelerationChannel.data;
                final int n = forceOffset + 0;
                data[n] += CentripetalAcceleration.TMP_V3.x;
                final float[] data2 = this.accelerationChannel.data;
                final int n2 = forceOffset + 1;
                data2[n2] += CentripetalAcceleration.TMP_V3.y;
                final float[] data3 = this.accelerationChannel.data;
                final int n3 = forceOffset + 2;
                data3[n3] += CentripetalAcceleration.TMP_V3.z;
            }
        }
        
        @Override
        public CentripetalAcceleration copy() {
            return new CentripetalAcceleration(this);
        }
    }
    
    public static class PolarAcceleration extends Angular
    {
        ParallelArray.FloatChannel directionalVelocityChannel;
        
        public PolarAcceleration() {
        }
        
        public PolarAcceleration(final PolarAcceleration rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.directionalVelocityChannel = this.controller.particles.addChannel(ParticleChannels.Acceleration);
        }
        
        @Override
        public void update() {
            int i = 0;
            int l = 2;
            int s = 0;
            for (int a = 0, c = i + this.controller.particles.size * this.directionalVelocityChannel.strideSize; i < c; i += this.directionalVelocityChannel.strideSize, a += this.angularChannel.strideSize, l += this.lifeChannel.strideSize) {
                final float lifePercent = this.lifeChannel.data[l];
                final float strength = this.strengthChannel.data[s + 0] + this.strengthChannel.data[s + 1] * this.strengthValue.getScale(lifePercent);
                final float phi = this.angularChannel.data[a + 2] + this.angularChannel.data[a + 3] * this.phiValue.getScale(lifePercent);
                final float theta = this.angularChannel.data[a + 0] + this.angularChannel.data[a + 1] * this.thetaValue.getScale(lifePercent);
                final float cosTheta = MathUtils.cosDeg(theta);
                final float sinTheta = MathUtils.sinDeg(theta);
                final float cosPhi = MathUtils.cosDeg(phi);
                final float sinPhi = MathUtils.sinDeg(phi);
                PolarAcceleration.TMP_V3.set(cosTheta * sinPhi, cosPhi, sinTheta * sinPhi).nor().scl(strength);
                final float[] data = this.directionalVelocityChannel.data;
                final int n = i + 0;
                data[n] += PolarAcceleration.TMP_V3.x;
                final float[] data2 = this.directionalVelocityChannel.data;
                final int n2 = i + 1;
                data2[n2] += PolarAcceleration.TMP_V3.y;
                final float[] data3 = this.directionalVelocityChannel.data;
                final int n3 = i + 2;
                data3[n3] += PolarAcceleration.TMP_V3.z;
                s += this.strengthChannel.strideSize;
            }
        }
        
        @Override
        public PolarAcceleration copy() {
            return new PolarAcceleration(this);
        }
    }
    
    public static class Rotational2D extends Strength
    {
        ParallelArray.FloatChannel rotationalVelocity2dChannel;
        
        public Rotational2D() {
        }
        
        public Rotational2D(final Rotational2D rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.rotationalVelocity2dChannel = this.controller.particles.addChannel(ParticleChannels.AngularVelocity2D);
        }
        
        @Override
        public void update() {
            int i = 0;
            int l = 2;
            int s = 0;
            for (int c = i + this.controller.particles.size * this.rotationalVelocity2dChannel.strideSize; i < c; i += this.rotationalVelocity2dChannel.strideSize, l += this.lifeChannel.strideSize) {
                final float[] data = this.rotationalVelocity2dChannel.data;
                final int n = i;
                data[n] += this.strengthChannel.data[s + 0] + this.strengthChannel.data[s + 1] * this.strengthValue.getScale(this.lifeChannel.data[l]);
                s += this.strengthChannel.strideSize;
            }
        }
        
        @Override
        public Rotational2D copy() {
            return new Rotational2D(this);
        }
    }
    
    public static class Rotational3D extends Angular
    {
        ParallelArray.FloatChannel rotationChannel;
        ParallelArray.FloatChannel rotationalForceChannel;
        
        public Rotational3D() {
        }
        
        public Rotational3D(final Rotational3D rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation3D);
            this.rotationalForceChannel = this.controller.particles.addChannel(ParticleChannels.AngularVelocity3D);
        }
        
        @Override
        public void update() {
            int i = 0;
            int l = 2;
            int s = 0;
            for (int a = 0, c = this.controller.particles.size * this.rotationalForceChannel.strideSize; i < c; i += this.rotationalForceChannel.strideSize, a += this.angularChannel.strideSize, l += this.lifeChannel.strideSize) {
                final float lifePercent = this.lifeChannel.data[l];
                final float strength = this.strengthChannel.data[s + 0] + this.strengthChannel.data[s + 1] * this.strengthValue.getScale(lifePercent);
                final float phi = this.angularChannel.data[a + 2] + this.angularChannel.data[a + 3] * this.phiValue.getScale(lifePercent);
                final float theta = this.angularChannel.data[a + 0] + this.angularChannel.data[a + 1] * this.thetaValue.getScale(lifePercent);
                final float cosTheta = MathUtils.cosDeg(theta);
                final float sinTheta = MathUtils.sinDeg(theta);
                final float cosPhi = MathUtils.cosDeg(phi);
                final float sinPhi = MathUtils.sinDeg(phi);
                Rotational3D.TMP_V3.set(cosTheta * sinPhi, cosPhi, sinTheta * sinPhi);
                Rotational3D.TMP_V3.scl(strength * 0.017453292f);
                final float[] data = this.rotationalForceChannel.data;
                final int n = i + 0;
                data[n] += Rotational3D.TMP_V3.x;
                final float[] data2 = this.rotationalForceChannel.data;
                final int n2 = i + 1;
                data2[n2] += Rotational3D.TMP_V3.y;
                final float[] data3 = this.rotationalForceChannel.data;
                final int n3 = i + 2;
                data3[n3] += Rotational3D.TMP_V3.z;
                s += this.strengthChannel.strideSize;
            }
        }
        
        @Override
        public Rotational3D copy() {
            return new Rotational3D(this);
        }
    }
    
    public static class TangentialAcceleration extends Angular
    {
        ParallelArray.FloatChannel directionalVelocityChannel;
        ParallelArray.FloatChannel positionChannel;
        
        public TangentialAcceleration() {
        }
        
        public TangentialAcceleration(final TangentialAcceleration rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.directionalVelocityChannel = this.controller.particles.addChannel(ParticleChannels.Acceleration);
            this.positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
        }
        
        @Override
        public void update() {
            int i = 0;
            int l = 2;
            int s = 0;
            for (int a = 0, positionOffset = 0, c = i + this.controller.particles.size * this.directionalVelocityChannel.strideSize; i < c; i += this.directionalVelocityChannel.strideSize, a += this.angularChannel.strideSize, l += this.lifeChannel.strideSize, positionOffset += this.positionChannel.strideSize) {
                final float lifePercent = this.lifeChannel.data[l];
                final float strength = this.strengthChannel.data[s + 0] + this.strengthChannel.data[s + 1] * this.strengthValue.getScale(lifePercent);
                final float phi = this.angularChannel.data[a + 2] + this.angularChannel.data[a + 3] * this.phiValue.getScale(lifePercent);
                final float theta = this.angularChannel.data[a + 0] + this.angularChannel.data[a + 1] * this.thetaValue.getScale(lifePercent);
                final float cosTheta = MathUtils.cosDeg(theta);
                final float sinTheta = MathUtils.sinDeg(theta);
                final float cosPhi = MathUtils.cosDeg(phi);
                final float sinPhi = MathUtils.sinDeg(phi);
                TangentialAcceleration.TMP_V3.set(cosTheta * sinPhi, cosPhi, sinTheta * sinPhi).crs(this.positionChannel.data[positionOffset + 0], this.positionChannel.data[positionOffset + 1], this.positionChannel.data[positionOffset + 2]).nor().scl(strength);
                final float[] data = this.directionalVelocityChannel.data;
                final int n = i + 0;
                data[n] += TangentialAcceleration.TMP_V3.x;
                final float[] data2 = this.directionalVelocityChannel.data;
                final int n2 = i + 1;
                data2[n2] += TangentialAcceleration.TMP_V3.y;
                final float[] data3 = this.directionalVelocityChannel.data;
                final int n3 = i + 2;
                data3[n3] += TangentialAcceleration.TMP_V3.z;
                s += this.strengthChannel.strideSize;
            }
        }
        
        @Override
        public TangentialAcceleration copy() {
            return new TangentialAcceleration(this);
        }
    }
    
    public static class FaceDirection extends DynamicsModifier
    {
        ParallelArray.FloatChannel rotationChannel;
        ParallelArray.FloatChannel accellerationChannel;
        
        public FaceDirection() {
        }
        
        public FaceDirection(final FaceDirection rotation) {
            super(rotation);
        }
        
        @Override
        public void allocateChannels() {
            this.rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation3D);
            this.accellerationChannel = this.controller.particles.addChannel(ParticleChannels.Acceleration);
        }
        
        @Override
        public void update() {
            for (int i = 0, accelOffset = 0, c = i + this.controller.particles.size * this.rotationChannel.strideSize; i < c; i += this.rotationChannel.strideSize, accelOffset += this.accellerationChannel.strideSize) {
                final Vector3 axisZ = FaceDirection.TMP_V1.set(this.accellerationChannel.data[accelOffset + 0], this.accellerationChannel.data[accelOffset + 1], this.accellerationChannel.data[accelOffset + 2]).nor();
                final Vector3 axisY = FaceDirection.TMP_V2.set(FaceDirection.TMP_V1).crs(Vector3.Y).nor().crs(FaceDirection.TMP_V1).nor();
                final Vector3 axisX = FaceDirection.TMP_V3.set(axisY).crs(axisZ).nor();
                FaceDirection.TMP_Q.setFromAxes(false, axisX.x, axisY.x, axisZ.x, axisX.y, axisY.y, axisZ.y, axisX.z, axisY.z, axisZ.z);
                this.rotationChannel.data[i + 0] = FaceDirection.TMP_Q.x;
                this.rotationChannel.data[i + 1] = FaceDirection.TMP_Q.y;
                this.rotationChannel.data[i + 2] = FaceDirection.TMP_Q.z;
                this.rotationChannel.data[i + 3] = FaceDirection.TMP_Q.w;
            }
        }
        
        @Override
        public ParticleControllerComponent copy() {
            return new FaceDirection(this);
        }
    }
    
    public abstract static class Strength extends DynamicsModifier
    {
        protected ParallelArray.FloatChannel strengthChannel;
        public ScaledNumericValue strengthValue;
        
        public Strength() {
            this.strengthValue = new ScaledNumericValue();
        }
        
        public Strength(final Strength rotation) {
            super(rotation);
            (this.strengthValue = new ScaledNumericValue()).load(rotation.strengthValue);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            ParticleChannels.Interpolation.id = this.controller.particleChannels.newId();
            this.strengthChannel = this.controller.particles.addChannel(ParticleChannels.Interpolation);
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex * this.strengthChannel.strideSize, c = i + count * this.strengthChannel.strideSize; i < c; i += this.strengthChannel.strideSize) {
                final float start = this.strengthValue.newLowValue();
                float diff = this.strengthValue.newHighValue();
                if (!this.strengthValue.isRelative()) {
                    diff -= start;
                }
                this.strengthChannel.data[i + 0] = start;
                this.strengthChannel.data[i + 1] = diff;
            }
        }
        
        @Override
        public void write(final Json json) {
            super.write(json);
            json.writeValue("strengthValue", this.strengthValue);
        }
        
        @Override
        public void read(final Json json, final JsonValue jsonData) {
            super.read(json, jsonData);
            this.strengthValue = json.readValue("strengthValue", ScaledNumericValue.class, jsonData);
        }
    }
    
    public abstract static class Angular extends Strength
    {
        protected ParallelArray.FloatChannel angularChannel;
        public ScaledNumericValue thetaValue;
        public ScaledNumericValue phiValue;
        
        public Angular() {
            this.thetaValue = new ScaledNumericValue();
            this.phiValue = new ScaledNumericValue();
        }
        
        public Angular(final Angular value) {
            super(value);
            this.thetaValue = new ScaledNumericValue();
            this.phiValue = new ScaledNumericValue();
            this.thetaValue.load(value.thetaValue);
            this.phiValue.load(value.phiValue);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            ParticleChannels.Interpolation4.id = this.controller.particleChannels.newId();
            this.angularChannel = this.controller.particles.addChannel(ParticleChannels.Interpolation4);
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            super.activateParticles(startIndex, count);
            for (int i = startIndex * this.angularChannel.strideSize, c = i + count * this.angularChannel.strideSize; i < c; i += this.angularChannel.strideSize) {
                float start = this.thetaValue.newLowValue();
                float diff = this.thetaValue.newHighValue();
                if (!this.thetaValue.isRelative()) {
                    diff -= start;
                }
                this.angularChannel.data[i + 0] = start;
                this.angularChannel.data[i + 1] = diff;
                start = this.phiValue.newLowValue();
                diff = this.phiValue.newHighValue();
                if (!this.phiValue.isRelative()) {
                    diff -= start;
                }
                this.angularChannel.data[i + 2] = start;
                this.angularChannel.data[i + 3] = diff;
            }
        }
        
        @Override
        public void write(final Json json) {
            super.write(json);
            json.writeValue("thetaValue", this.thetaValue);
            json.writeValue("phiValue", this.phiValue);
        }
        
        @Override
        public void read(final Json json, final JsonValue jsonData) {
            super.read(json, jsonData);
            this.thetaValue = json.readValue("thetaValue", ScaledNumericValue.class, jsonData);
            this.phiValue = json.readValue("phiValue", ScaledNumericValue.class, jsonData);
        }
    }
}

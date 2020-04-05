// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.utils.Array;

public class DynamicsInfluencer extends Influencer
{
    public Array<DynamicsModifier> velocities;
    private ParallelArray.FloatChannel accellerationChannel;
    private ParallelArray.FloatChannel positionChannel;
    private ParallelArray.FloatChannel previousPositionChannel;
    private ParallelArray.FloatChannel rotationChannel;
    private ParallelArray.FloatChannel angularVelocityChannel;
    boolean hasAcceleration;
    boolean has2dAngularVelocity;
    boolean has3dAngularVelocity;
    
    public DynamicsInfluencer() {
        this.velocities = new Array<DynamicsModifier>(true, 3, DynamicsModifier.class);
    }
    
    public DynamicsInfluencer(final DynamicsModifier... velocities) {
        this.velocities = new Array<DynamicsModifier>(true, velocities.length, DynamicsModifier.class);
        for (final DynamicsModifier value : velocities) {
            this.velocities.add((DynamicsModifier)value.copy());
        }
    }
    
    public DynamicsInfluencer(final DynamicsInfluencer velocityInfluencer) {
        this((DynamicsModifier[])velocityInfluencer.velocities.toArray(DynamicsModifier.class));
    }
    
    @Override
    public void allocateChannels() {
        for (int k = 0; k < this.velocities.size; ++k) {
            this.velocities.items[k].allocateChannels();
        }
        this.accellerationChannel = this.controller.particles.getChannel(ParticleChannels.Acceleration);
        this.hasAcceleration = (this.accellerationChannel != null);
        if (this.hasAcceleration) {
            this.positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
            this.previousPositionChannel = this.controller.particles.addChannel(ParticleChannels.PreviousPosition);
        }
        this.angularVelocityChannel = this.controller.particles.getChannel(ParticleChannels.AngularVelocity2D);
        this.has2dAngularVelocity = (this.angularVelocityChannel != null);
        if (this.has2dAngularVelocity) {
            this.rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation2D);
            this.has3dAngularVelocity = false;
        }
        else {
            this.angularVelocityChannel = this.controller.particles.getChannel(ParticleChannels.AngularVelocity3D);
            this.has3dAngularVelocity = (this.angularVelocityChannel != null);
            if (this.has3dAngularVelocity) {
                this.rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation3D);
            }
        }
    }
    
    @Override
    public void set(final ParticleController particleController) {
        super.set(particleController);
        for (int k = 0; k < this.velocities.size; ++k) {
            this.velocities.items[k].set(particleController);
        }
    }
    
    @Override
    public void init() {
        for (int k = 0; k < this.velocities.size; ++k) {
            this.velocities.items[k].init();
        }
    }
    
    @Override
    public void activateParticles(final int startIndex, final int count) {
        if (this.hasAcceleration) {
            for (int i = startIndex * this.positionChannel.strideSize, c = i + count * this.positionChannel.strideSize; i < c; i += this.positionChannel.strideSize) {
                this.previousPositionChannel.data[i + 0] = this.positionChannel.data[i + 0];
                this.previousPositionChannel.data[i + 1] = this.positionChannel.data[i + 1];
                this.previousPositionChannel.data[i + 2] = this.positionChannel.data[i + 2];
            }
        }
        if (this.has2dAngularVelocity) {
            for (int i = startIndex * this.rotationChannel.strideSize, c = i + count * this.rotationChannel.strideSize; i < c; i += this.rotationChannel.strideSize) {
                this.rotationChannel.data[i + 0] = 1.0f;
                this.rotationChannel.data[i + 1] = 0.0f;
            }
        }
        else if (this.has3dAngularVelocity) {
            for (int i = startIndex * this.rotationChannel.strideSize, c = i + count * this.rotationChannel.strideSize; i < c; i += this.rotationChannel.strideSize) {
                this.rotationChannel.data[i + 0] = 0.0f;
                this.rotationChannel.data[i + 1] = 0.0f;
                this.rotationChannel.data[i + 2] = 0.0f;
                this.rotationChannel.data[i + 3] = 1.0f;
            }
        }
        for (int k = 0; k < this.velocities.size; ++k) {
            this.velocities.items[k].activateParticles(startIndex, count);
        }
    }
    
    @Override
    public void update() {
        if (this.hasAcceleration) {
            Arrays.fill(this.accellerationChannel.data, 0, this.controller.particles.size * this.accellerationChannel.strideSize, 0.0f);
        }
        if (this.has2dAngularVelocity || this.has3dAngularVelocity) {
            Arrays.fill(this.angularVelocityChannel.data, 0, this.controller.particles.size * this.angularVelocityChannel.strideSize, 0.0f);
        }
        for (int k = 0; k < this.velocities.size; ++k) {
            this.velocities.items[k].update();
        }
        if (this.hasAcceleration) {
            for (int i = 0, offset = 0; i < this.controller.particles.size; ++i, offset += this.positionChannel.strideSize) {
                final float x = this.positionChannel.data[offset + 0];
                final float y = this.positionChannel.data[offset + 1];
                final float z = this.positionChannel.data[offset + 2];
                this.positionChannel.data[offset + 0] = 2.0f * x - this.previousPositionChannel.data[offset + 0] + this.accellerationChannel.data[offset + 0] * this.controller.deltaTimeSqr;
                this.positionChannel.data[offset + 1] = 2.0f * y - this.previousPositionChannel.data[offset + 1] + this.accellerationChannel.data[offset + 1] * this.controller.deltaTimeSqr;
                this.positionChannel.data[offset + 2] = 2.0f * z - this.previousPositionChannel.data[offset + 2] + this.accellerationChannel.data[offset + 2] * this.controller.deltaTimeSqr;
                this.previousPositionChannel.data[offset + 0] = x;
                this.previousPositionChannel.data[offset + 1] = y;
                this.previousPositionChannel.data[offset + 2] = z;
            }
        }
        if (this.has2dAngularVelocity) {
            for (int i = 0, offset = 0; i < this.controller.particles.size; ++i, offset += this.rotationChannel.strideSize) {
                final float rotation = this.angularVelocityChannel.data[i] * this.controller.deltaTime;
                if (rotation != 0.0f) {
                    final float cosBeta = MathUtils.cosDeg(rotation);
                    final float sinBeta = MathUtils.sinDeg(rotation);
                    final float currentCosine = this.rotationChannel.data[offset + 0];
                    final float currentSine = this.rotationChannel.data[offset + 1];
                    final float newCosine = currentCosine * cosBeta - currentSine * sinBeta;
                    final float newSine = currentSine * cosBeta + currentCosine * sinBeta;
                    this.rotationChannel.data[offset + 0] = newCosine;
                    this.rotationChannel.data[offset + 1] = newSine;
                }
            }
        }
        else if (this.has3dAngularVelocity) {
            for (int i = 0, offset = 0, angularOffset = 0; i < this.controller.particles.size; ++i, offset += this.rotationChannel.strideSize, angularOffset += this.angularVelocityChannel.strideSize) {
                final float wx = this.angularVelocityChannel.data[angularOffset + 0];
                final float wy = this.angularVelocityChannel.data[angularOffset + 1];
                final float wz = this.angularVelocityChannel.data[angularOffset + 2];
                final float qx = this.rotationChannel.data[offset + 0];
                final float qy = this.rotationChannel.data[offset + 1];
                final float qz = this.rotationChannel.data[offset + 2];
                final float qw = this.rotationChannel.data[offset + 3];
                DynamicsInfluencer.TMP_Q.set(wx, wy, wz, 0.0f).mul(qx, qy, qz, qw).mul(0.5f * this.controller.deltaTime).add(qx, qy, qz, qw).nor();
                this.rotationChannel.data[offset + 0] = DynamicsInfluencer.TMP_Q.x;
                this.rotationChannel.data[offset + 1] = DynamicsInfluencer.TMP_Q.y;
                this.rotationChannel.data[offset + 2] = DynamicsInfluencer.TMP_Q.z;
                this.rotationChannel.data[offset + 3] = DynamicsInfluencer.TMP_Q.w;
            }
        }
    }
    
    @Override
    public DynamicsInfluencer copy() {
        return new DynamicsInfluencer(this);
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("velocities", this.velocities, Array.class, DynamicsModifier.class);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.velocities.addAll(json.readValue("velocities", (Class<Array<? extends DynamicsModifier>>)Array.class, DynamicsModifier.class, jsonData));
    }
}

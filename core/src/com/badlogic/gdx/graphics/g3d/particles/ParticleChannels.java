// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import java.util.Arrays;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class ParticleChannels
{
    private static int currentGlobalId;
    public static final ParallelArray.ChannelDescriptor Life;
    public static final ParallelArray.ChannelDescriptor Position;
    public static final ParallelArray.ChannelDescriptor PreviousPosition;
    public static final ParallelArray.ChannelDescriptor Color;
    public static final ParallelArray.ChannelDescriptor TextureRegion;
    public static final ParallelArray.ChannelDescriptor Rotation2D;
    public static final ParallelArray.ChannelDescriptor Rotation3D;
    public static final ParallelArray.ChannelDescriptor Scale;
    public static final ParallelArray.ChannelDescriptor ModelInstance;
    public static final ParallelArray.ChannelDescriptor ParticleController;
    public static final ParallelArray.ChannelDescriptor Acceleration;
    public static final ParallelArray.ChannelDescriptor AngularVelocity2D;
    public static final ParallelArray.ChannelDescriptor AngularVelocity3D;
    public static final ParallelArray.ChannelDescriptor Interpolation;
    public static final ParallelArray.ChannelDescriptor Interpolation4;
    public static final ParallelArray.ChannelDescriptor Interpolation6;
    public static final int CurrentLifeOffset = 0;
    public static final int TotalLifeOffset = 1;
    public static final int LifePercentOffset = 2;
    public static final int RedOffset = 0;
    public static final int GreenOffset = 1;
    public static final int BlueOffset = 2;
    public static final int AlphaOffset = 3;
    public static final int InterpolationStartOffset = 0;
    public static final int InterpolationDiffOffset = 1;
    public static final int VelocityStrengthStartOffset = 0;
    public static final int VelocityStrengthDiffOffset = 1;
    public static final int VelocityThetaStartOffset = 0;
    public static final int VelocityThetaDiffOffset = 1;
    public static final int VelocityPhiStartOffset = 2;
    public static final int VelocityPhiDiffOffset = 3;
    public static final int XOffset = 0;
    public static final int YOffset = 1;
    public static final int ZOffset = 2;
    public static final int WOffset = 3;
    public static final int UOffset = 0;
    public static final int VOffset = 1;
    public static final int U2Offset = 2;
    public static final int V2Offset = 3;
    public static final int HalfWidthOffset = 4;
    public static final int HalfHeightOffset = 5;
    public static final int CosineOffset = 0;
    public static final int SineOffset = 1;
    private int currentId;
    
    static {
        Life = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 3);
        Position = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 3);
        PreviousPosition = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 3);
        Color = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 4);
        TextureRegion = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 6);
        Rotation2D = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 2);
        Rotation3D = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 4);
        Scale = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 1);
        ModelInstance = new ParallelArray.ChannelDescriptor(newGlobalId(), ModelInstance.class, 1);
        ParticleController = new ParallelArray.ChannelDescriptor(newGlobalId(), ParticleController.class, 1);
        Acceleration = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 3);
        AngularVelocity2D = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 1);
        AngularVelocity3D = new ParallelArray.ChannelDescriptor(newGlobalId(), Float.TYPE, 3);
        Interpolation = new ParallelArray.ChannelDescriptor(-1, Float.TYPE, 2);
        Interpolation4 = new ParallelArray.ChannelDescriptor(-1, Float.TYPE, 4);
        Interpolation6 = new ParallelArray.ChannelDescriptor(-1, Float.TYPE, 6);
    }
    
    public static int newGlobalId() {
        return ParticleChannels.currentGlobalId++;
    }
    
    public ParticleChannels() {
        this.resetIds();
    }
    
    public int newId() {
        return this.currentId++;
    }
    
    protected void resetIds() {
        this.currentId = ParticleChannels.currentGlobalId;
    }
    
    public static class ColorInitializer implements ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>
    {
        private static ColorInitializer instance;
        
        public static ColorInitializer get() {
            if (ColorInitializer.instance == null) {
                ColorInitializer.instance = new ColorInitializer();
            }
            return ColorInitializer.instance;
        }
        
        @Override
        public void init(final ParallelArray.FloatChannel channel) {
            Arrays.fill(channel.data, 0, channel.data.length, 1.0f);
        }
    }
    
    public static class Rotation2dInitializer implements ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>
    {
        private static Rotation2dInitializer instance;
        
        public static Rotation2dInitializer get() {
            if (Rotation2dInitializer.instance == null) {
                Rotation2dInitializer.instance = new Rotation2dInitializer();
            }
            return Rotation2dInitializer.instance;
        }
        
        @Override
        public void init(final ParallelArray.FloatChannel channel) {
            for (int i = 0, c = channel.data.length; i < c; i += channel.strideSize) {
                channel.data[i + 0] = 1.0f;
                channel.data[i + 1] = 0.0f;
            }
        }
    }
    
    public static class Rotation3dInitializer implements ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>
    {
        private static Rotation3dInitializer instance;
        
        public static Rotation3dInitializer get() {
            if (Rotation3dInitializer.instance == null) {
                Rotation3dInitializer.instance = new Rotation3dInitializer();
            }
            return Rotation3dInitializer.instance;
        }
        
        @Override
        public void init(final ParallelArray.FloatChannel channel) {
            for (int i = 0, c = channel.data.length; i < c; i += channel.strideSize) {
                final float[] data = channel.data;
                final int n = i + 0;
                final float[] data2 = channel.data;
                final int n2 = i + 1;
                final float[] data3 = channel.data;
                final int n3 = i + 2;
                final float n4 = 0.0f;
                data3[n3] = n4;
                data[n] = (data2[n2] = n4);
                channel.data[i + 3] = 1.0f;
            }
        }
    }
    
    public static class ScaleInitializer implements ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>
    {
        private static ScaleInitializer instance;
        
        public static ScaleInitializer get() {
            if (ScaleInitializer.instance == null) {
                ScaleInitializer.instance = new ScaleInitializer();
            }
            return ScaleInitializer.instance;
        }
        
        @Override
        public void init(final ParallelArray.FloatChannel channel) {
            Arrays.fill(channel.data, 0, channel.data.length, 1.0f);
        }
    }
    
    public static class TextureRegionInitializer implements ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>
    {
        private static TextureRegionInitializer instance;
        
        public static TextureRegionInitializer get() {
            if (TextureRegionInitializer.instance == null) {
                TextureRegionInitializer.instance = new TextureRegionInitializer();
            }
            return TextureRegionInitializer.instance;
        }
        
        @Override
        public void init(final ParallelArray.FloatChannel channel) {
            for (int i = 0, c = channel.data.length; i < c; i += channel.strideSize) {
                channel.data[i + 0] = 0.0f;
                channel.data[i + 1] = 0.0f;
                channel.data[i + 2] = 1.0f;
                channel.data[i + 3] = 1.0f;
                channel.data[i + 4] = 0.5f;
                channel.data[i + 5] = 0.5f;
            }
        }
    }
}

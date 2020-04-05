// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import java.util.Arrays;
import java.util.Iterator;
import java.io.Writer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.io.IOException;
import java.io.BufferedReader;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class ParticleEmitter
{
    private static final int UPDATE_SCALE = 1;
    private static final int UPDATE_ANGLE = 2;
    private static final int UPDATE_ROTATION = 4;
    private static final int UPDATE_VELOCITY = 8;
    private static final int UPDATE_WIND = 16;
    private static final int UPDATE_GRAVITY = 32;
    private static final int UPDATE_TINT = 64;
    private static final int UPDATE_SPRITE = 128;
    private RangedNumericValue delayValue;
    private IndependentScaledNumericValue lifeOffsetValue;
    private RangedNumericValue durationValue;
    private IndependentScaledNumericValue lifeValue;
    private ScaledNumericValue emissionValue;
    private ScaledNumericValue xScaleValue;
    private ScaledNumericValue yScaleValue;
    private ScaledNumericValue rotationValue;
    private ScaledNumericValue velocityValue;
    private ScaledNumericValue angleValue;
    private ScaledNumericValue windValue;
    private ScaledNumericValue gravityValue;
    private ScaledNumericValue transparencyValue;
    private GradientColorValue tintValue;
    private RangedNumericValue xOffsetValue;
    private RangedNumericValue yOffsetValue;
    private ScaledNumericValue spawnWidthValue;
    private ScaledNumericValue spawnHeightValue;
    private SpawnShapeValue spawnShapeValue;
    private RangedNumericValue[] xSizeValues;
    private RangedNumericValue[] ySizeValues;
    private RangedNumericValue[] motionValues;
    private float accumulator;
    private Array<Sprite> sprites;
    private SpriteMode spriteMode;
    private Particle[] particles;
    private int minParticleCount;
    private int maxParticleCount;
    private float x;
    private float y;
    private String name;
    private Array<String> imagePaths;
    private int activeCount;
    private boolean[] active;
    private boolean firstUpdate;
    private boolean flipX;
    private boolean flipY;
    private int updateFlags;
    private boolean allowCompletion;
    private BoundingBox bounds;
    private int emission;
    private int emissionDiff;
    private int emissionDelta;
    private int lifeOffset;
    private int lifeOffsetDiff;
    private int life;
    private int lifeDiff;
    private float spawnWidth;
    private float spawnWidthDiff;
    private float spawnHeight;
    private float spawnHeightDiff;
    public float duration;
    public float durationTimer;
    private float delay;
    private float delayTimer;
    private boolean attached;
    private boolean continuous;
    private boolean aligned;
    private boolean behind;
    private boolean additive;
    private boolean premultipliedAlpha;
    boolean cleansUpBlendFunction;
    
    public ParticleEmitter() {
        this.delayValue = new RangedNumericValue();
        this.lifeOffsetValue = new IndependentScaledNumericValue();
        this.durationValue = new RangedNumericValue();
        this.lifeValue = new IndependentScaledNumericValue();
        this.emissionValue = new ScaledNumericValue();
        this.xScaleValue = new ScaledNumericValue();
        this.yScaleValue = new ScaledNumericValue();
        this.rotationValue = new ScaledNumericValue();
        this.velocityValue = new ScaledNumericValue();
        this.angleValue = new ScaledNumericValue();
        this.windValue = new ScaledNumericValue();
        this.gravityValue = new ScaledNumericValue();
        this.transparencyValue = new ScaledNumericValue();
        this.tintValue = new GradientColorValue();
        this.xOffsetValue = new ScaledNumericValue();
        this.yOffsetValue = new ScaledNumericValue();
        this.spawnWidthValue = new ScaledNumericValue();
        this.spawnHeightValue = new ScaledNumericValue();
        this.spawnShapeValue = new SpawnShapeValue();
        this.spriteMode = SpriteMode.single;
        this.maxParticleCount = 4;
        this.duration = 1.0f;
        this.additive = true;
        this.premultipliedAlpha = false;
        this.cleansUpBlendFunction = true;
        this.initialize();
    }
    
    public ParticleEmitter(final BufferedReader reader) throws IOException {
        this.delayValue = new RangedNumericValue();
        this.lifeOffsetValue = new IndependentScaledNumericValue();
        this.durationValue = new RangedNumericValue();
        this.lifeValue = new IndependentScaledNumericValue();
        this.emissionValue = new ScaledNumericValue();
        this.xScaleValue = new ScaledNumericValue();
        this.yScaleValue = new ScaledNumericValue();
        this.rotationValue = new ScaledNumericValue();
        this.velocityValue = new ScaledNumericValue();
        this.angleValue = new ScaledNumericValue();
        this.windValue = new ScaledNumericValue();
        this.gravityValue = new ScaledNumericValue();
        this.transparencyValue = new ScaledNumericValue();
        this.tintValue = new GradientColorValue();
        this.xOffsetValue = new ScaledNumericValue();
        this.yOffsetValue = new ScaledNumericValue();
        this.spawnWidthValue = new ScaledNumericValue();
        this.spawnHeightValue = new ScaledNumericValue();
        this.spawnShapeValue = new SpawnShapeValue();
        this.spriteMode = SpriteMode.single;
        this.maxParticleCount = 4;
        this.duration = 1.0f;
        this.additive = true;
        this.premultipliedAlpha = false;
        this.cleansUpBlendFunction = true;
        this.initialize();
        this.load(reader);
    }
    
    public ParticleEmitter(final ParticleEmitter emitter) {
        this.delayValue = new RangedNumericValue();
        this.lifeOffsetValue = new IndependentScaledNumericValue();
        this.durationValue = new RangedNumericValue();
        this.lifeValue = new IndependentScaledNumericValue();
        this.emissionValue = new ScaledNumericValue();
        this.xScaleValue = new ScaledNumericValue();
        this.yScaleValue = new ScaledNumericValue();
        this.rotationValue = new ScaledNumericValue();
        this.velocityValue = new ScaledNumericValue();
        this.angleValue = new ScaledNumericValue();
        this.windValue = new ScaledNumericValue();
        this.gravityValue = new ScaledNumericValue();
        this.transparencyValue = new ScaledNumericValue();
        this.tintValue = new GradientColorValue();
        this.xOffsetValue = new ScaledNumericValue();
        this.yOffsetValue = new ScaledNumericValue();
        this.spawnWidthValue = new ScaledNumericValue();
        this.spawnHeightValue = new ScaledNumericValue();
        this.spawnShapeValue = new SpawnShapeValue();
        this.spriteMode = SpriteMode.single;
        this.maxParticleCount = 4;
        this.duration = 1.0f;
        this.additive = true;
        this.premultipliedAlpha = false;
        this.cleansUpBlendFunction = true;
        this.sprites = new Array<Sprite>(emitter.sprites);
        this.name = emitter.name;
        this.imagePaths = new Array<String>(emitter.imagePaths);
        this.setMaxParticleCount(emitter.maxParticleCount);
        this.minParticleCount = emitter.minParticleCount;
        this.delayValue.load(emitter.delayValue);
        this.durationValue.load(emitter.durationValue);
        this.emissionValue.load(emitter.emissionValue);
        this.lifeValue.load(emitter.lifeValue);
        this.lifeOffsetValue.load(emitter.lifeOffsetValue);
        this.xScaleValue.load(emitter.xScaleValue);
        this.yScaleValue.load(emitter.yScaleValue);
        this.rotationValue.load(emitter.rotationValue);
        this.velocityValue.load(emitter.velocityValue);
        this.angleValue.load(emitter.angleValue);
        this.windValue.load(emitter.windValue);
        this.gravityValue.load(emitter.gravityValue);
        this.transparencyValue.load(emitter.transparencyValue);
        this.tintValue.load(emitter.tintValue);
        this.xOffsetValue.load(emitter.xOffsetValue);
        this.yOffsetValue.load(emitter.yOffsetValue);
        this.spawnWidthValue.load(emitter.spawnWidthValue);
        this.spawnHeightValue.load(emitter.spawnHeightValue);
        this.spawnShapeValue.load(emitter.spawnShapeValue);
        this.attached = emitter.attached;
        this.continuous = emitter.continuous;
        this.aligned = emitter.aligned;
        this.behind = emitter.behind;
        this.additive = emitter.additive;
        this.premultipliedAlpha = emitter.premultipliedAlpha;
        this.cleansUpBlendFunction = emitter.cleansUpBlendFunction;
        this.spriteMode = emitter.spriteMode;
    }
    
    private void initialize() {
        this.sprites = new Array<Sprite>();
        this.imagePaths = new Array<String>();
        this.durationValue.setAlwaysActive(true);
        this.emissionValue.setAlwaysActive(true);
        this.lifeValue.setAlwaysActive(true);
        this.xScaleValue.setAlwaysActive(true);
        this.transparencyValue.setAlwaysActive(true);
        this.spawnShapeValue.setAlwaysActive(true);
        this.spawnWidthValue.setAlwaysActive(true);
        this.spawnHeightValue.setAlwaysActive(true);
    }
    
    public void setMaxParticleCount(final int maxParticleCount) {
        this.maxParticleCount = maxParticleCount;
        this.active = new boolean[maxParticleCount];
        this.activeCount = 0;
        this.particles = new Particle[maxParticleCount];
    }
    
    public void addParticle() {
        final int activeCount = this.activeCount;
        if (activeCount == this.maxParticleCount) {
            return;
        }
        final boolean[] active = this.active;
        for (int i = 0, n = active.length; i < n; ++i) {
            if (!active[i]) {
                this.activateParticle(i);
                active[i] = true;
                this.activeCount = activeCount + 1;
                break;
            }
        }
    }
    
    public void addParticles(int count) {
        count = Math.min(count, this.maxParticleCount - this.activeCount);
        if (count == 0) {
            return;
        }
        final boolean[] active = this.active;
        int index = 0;
        final int n = active.length;
        int i = 0;
    Label_0072:
        while (i < count) {
            while (index < n) {
                if (!active[index]) {
                    this.activateParticle(index);
                    active[index++] = true;
                    ++i;
                    continue Label_0072;
                }
                ++index;
            }
            break;
        }
        this.activeCount += count;
    }
    
    public void update(final float delta) {
        this.accumulator += delta * 1000.0f;
        if (this.accumulator < 1.0f) {
            return;
        }
        final int deltaMillis = (int)this.accumulator;
        this.accumulator -= deltaMillis;
        if (this.delayTimer < this.delay) {
            this.delayTimer += deltaMillis;
        }
        else {
            boolean done = false;
            if (this.firstUpdate) {
                this.firstUpdate = false;
                this.addParticle();
            }
            if (this.durationTimer < this.duration) {
                this.durationTimer += deltaMillis;
            }
            else if (!this.continuous || this.allowCompletion) {
                done = true;
            }
            else {
                this.restart();
            }
            if (!done) {
                this.emissionDelta += deltaMillis;
                float emissionTime = this.emission + this.emissionDiff * this.emissionValue.getScale(this.durationTimer / this.duration);
                if (emissionTime > 0.0f) {
                    emissionTime = 1000.0f / emissionTime;
                    if (this.emissionDelta >= emissionTime) {
                        int emitCount = (int)(this.emissionDelta / emissionTime);
                        emitCount = Math.min(emitCount, this.maxParticleCount - this.activeCount);
                        this.emissionDelta -= (int)(emitCount * emissionTime);
                        this.emissionDelta %= (int)emissionTime;
                        this.addParticles(emitCount);
                    }
                }
                if (this.activeCount < this.minParticleCount) {
                    this.addParticles(this.minParticleCount - this.activeCount);
                }
            }
        }
        final boolean[] active = this.active;
        int activeCount = this.activeCount;
        final Particle[] particles = this.particles;
        for (int i = 0, n = active.length; i < n; ++i) {
            if (active[i] && !this.updateParticle(particles[i], delta, deltaMillis)) {
                active[i] = false;
                --activeCount;
            }
        }
        this.activeCount = activeCount;
    }
    
    public void draw(final Batch batch) {
        if (this.premultipliedAlpha) {
            batch.setBlendFunction(1, 771);
        }
        else if (this.additive) {
            batch.setBlendFunction(770, 1);
        }
        else {
            batch.setBlendFunction(770, 771);
        }
        final Particle[] particles = this.particles;
        final boolean[] active = this.active;
        for (int i = 0, n = active.length; i < n; ++i) {
            if (active[i]) {
                particles[i].draw(batch);
            }
        }
        if (this.cleansUpBlendFunction && (this.additive || this.premultipliedAlpha)) {
            batch.setBlendFunction(770, 771);
        }
    }
    
    public void draw(final Batch batch, final float delta) {
        this.accumulator += delta * 1000.0f;
        if (this.accumulator < 1.0f) {
            this.draw(batch);
            return;
        }
        final int deltaMillis = (int)this.accumulator;
        this.accumulator -= deltaMillis;
        if (this.premultipliedAlpha) {
            batch.setBlendFunction(1, 771);
        }
        else if (this.additive) {
            batch.setBlendFunction(770, 1);
        }
        else {
            batch.setBlendFunction(770, 771);
        }
        final Particle[] particles = this.particles;
        final boolean[] active = this.active;
        int activeCount = this.activeCount;
        for (int i = 0, n = active.length; i < n; ++i) {
            if (active[i]) {
                final Particle particle = particles[i];
                if (this.updateParticle(particle, delta, deltaMillis)) {
                    particle.draw(batch);
                }
                else {
                    active[i] = false;
                    --activeCount;
                }
            }
        }
        this.activeCount = activeCount;
        if (this.cleansUpBlendFunction && (this.additive || this.premultipliedAlpha)) {
            batch.setBlendFunction(770, 771);
        }
        if (this.delayTimer < this.delay) {
            this.delayTimer += deltaMillis;
            return;
        }
        if (this.firstUpdate) {
            this.firstUpdate = false;
            this.addParticle();
        }
        if (this.durationTimer < this.duration) {
            this.durationTimer += deltaMillis;
        }
        else {
            if (!this.continuous || this.allowCompletion) {
                return;
            }
            this.restart();
        }
        this.emissionDelta += deltaMillis;
        float emissionTime = this.emission + this.emissionDiff * this.emissionValue.getScale(this.durationTimer / this.duration);
        if (emissionTime > 0.0f) {
            emissionTime = 1000.0f / emissionTime;
            if (this.emissionDelta >= emissionTime) {
                int emitCount = (int)(this.emissionDelta / emissionTime);
                emitCount = Math.min(emitCount, this.maxParticleCount - activeCount);
                this.emissionDelta -= (int)(emitCount * emissionTime);
                this.emissionDelta %= (int)emissionTime;
                this.addParticles(emitCount);
            }
        }
        if (activeCount < this.minParticleCount) {
            this.addParticles(this.minParticleCount - activeCount);
        }
    }
    
    public void start() {
        this.firstUpdate = true;
        this.allowCompletion = false;
        this.restart();
    }
    
    public void reset() {
        this.emissionDelta = 0;
        this.durationTimer = this.duration;
        final boolean[] active = this.active;
        for (int i = 0, n = active.length; i < n; ++i) {
            active[i] = false;
        }
        this.activeCount = 0;
        this.start();
    }
    
    private void restart() {
        this.delay = (this.delayValue.active ? this.delayValue.newLowValue() : 0.0f);
        this.delayTimer = 0.0f;
        this.durationTimer -= this.duration;
        this.duration = this.durationValue.newLowValue();
        this.emission = (int)this.emissionValue.newLowValue();
        this.emissionDiff = (int)this.emissionValue.newHighValue();
        if (!this.emissionValue.isRelative()) {
            this.emissionDiff -= this.emission;
        }
        if (!this.lifeValue.independent) {
            this.generateLifeValues();
        }
        if (!this.lifeOffsetValue.independent) {
            this.generateLifeOffsetValues();
        }
        this.spawnWidth = this.spawnWidthValue.newLowValue();
        this.spawnWidthDiff = this.spawnWidthValue.newHighValue();
        if (!this.spawnWidthValue.isRelative()) {
            this.spawnWidthDiff -= this.spawnWidth;
        }
        this.spawnHeight = this.spawnHeightValue.newLowValue();
        this.spawnHeightDiff = this.spawnHeightValue.newHighValue();
        if (!this.spawnHeightValue.isRelative()) {
            this.spawnHeightDiff -= this.spawnHeight;
        }
        this.updateFlags = 0;
        if (this.angleValue.active && this.angleValue.timeline.length > 1) {
            this.updateFlags |= 0x2;
        }
        if (this.velocityValue.active) {
            this.updateFlags |= 0x8;
        }
        if (this.xScaleValue.timeline.length > 1) {
            this.updateFlags |= 0x1;
        }
        if (this.yScaleValue.active && this.yScaleValue.timeline.length > 1) {
            this.updateFlags |= 0x1;
        }
        if (this.rotationValue.active && this.rotationValue.timeline.length > 1) {
            this.updateFlags |= 0x4;
        }
        if (this.windValue.active) {
            this.updateFlags |= 0x10;
        }
        if (this.gravityValue.active) {
            this.updateFlags |= 0x20;
        }
        if (this.tintValue.timeline.length > 1) {
            this.updateFlags |= 0x40;
        }
        if (this.spriteMode == SpriteMode.animated) {
            this.updateFlags |= 0x80;
        }
    }
    
    protected Particle newParticle(final Sprite sprite) {
        return new Particle(sprite);
    }
    
    protected Particle[] getParticles() {
        return this.particles;
    }
    
    private void activateParticle(final int index) {
        Sprite sprite = null;
        switch (this.spriteMode) {
            case single:
            case animated: {
                sprite = this.sprites.first();
                break;
            }
            case random: {
                sprite = this.sprites.random();
                break;
            }
        }
        Particle particle = this.particles[index];
        if (particle == null) {
            particle = (this.particles[index] = this.newParticle(sprite));
            particle.flip(this.flipX, this.flipY);
        }
        else {
            particle.set(sprite);
        }
        final float percent = this.durationTimer / this.duration;
        final int updateFlags = this.updateFlags;
        if (this.lifeValue.independent) {
            this.generateLifeValues();
        }
        if (this.lifeOffsetValue.independent) {
            this.generateLifeOffsetValues();
        }
        final Particle particle2 = particle;
        final Particle particle3 = particle;
        final int n = this.life + (int)(this.lifeDiff * this.lifeValue.getScale(percent));
        particle3.life = n;
        particle2.currentLife = n;
        if (this.velocityValue.active) {
            particle.velocity = this.velocityValue.newLowValue();
            particle.velocityDiff = this.velocityValue.newHighValue();
            if (!this.velocityValue.isRelative()) {
                final Particle particle4 = particle;
                particle4.velocityDiff -= particle.velocity;
            }
        }
        particle.angle = this.angleValue.newLowValue();
        particle.angleDiff = this.angleValue.newHighValue();
        if (!this.angleValue.isRelative()) {
            final Particle particle5 = particle;
            particle5.angleDiff -= particle.angle;
        }
        float angle = 0.0f;
        if ((updateFlags & 0x2) == 0x0) {
            angle = particle.angle + particle.angleDiff * this.angleValue.getScale(0.0f);
            particle.angle = angle;
            particle.angleCos = MathUtils.cosDeg(angle);
            particle.angleSin = MathUtils.sinDeg(angle);
        }
        final float spriteWidth = sprite.getWidth();
        final float spriteHeight = sprite.getHeight();
        particle.xScale = this.xScaleValue.newLowValue() / spriteWidth;
        particle.xScaleDiff = this.xScaleValue.newHighValue() / spriteWidth;
        if (!this.xScaleValue.isRelative()) {
            final Particle particle6 = particle;
            particle6.xScaleDiff -= particle.xScale;
        }
        if (this.yScaleValue.active) {
            particle.yScale = this.yScaleValue.newLowValue() / spriteHeight;
            particle.yScaleDiff = this.yScaleValue.newHighValue() / spriteHeight;
            if (!this.yScaleValue.isRelative()) {
                final Particle particle7 = particle;
                particle7.yScaleDiff -= particle.yScale;
            }
            particle.setScale(particle.xScale + particle.xScaleDiff * this.xScaleValue.getScale(0.0f), particle.yScale + particle.yScaleDiff * this.yScaleValue.getScale(0.0f));
        }
        else {
            particle.setScale(particle.xScale + particle.xScaleDiff * this.xScaleValue.getScale(0.0f));
        }
        if (this.rotationValue.active) {
            particle.rotation = this.rotationValue.newLowValue();
            particle.rotationDiff = this.rotationValue.newHighValue();
            if (!this.rotationValue.isRelative()) {
                final Particle particle8 = particle;
                particle8.rotationDiff -= particle.rotation;
            }
            float rotation = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(0.0f);
            if (this.aligned) {
                rotation += angle;
            }
            particle.setRotation(rotation);
        }
        if (this.windValue.active) {
            particle.wind = this.windValue.newLowValue();
            particle.windDiff = this.windValue.newHighValue();
            if (!this.windValue.isRelative()) {
                final Particle particle9 = particle;
                particle9.windDiff -= particle.wind;
            }
        }
        if (this.gravityValue.active) {
            particle.gravity = this.gravityValue.newLowValue();
            particle.gravityDiff = this.gravityValue.newHighValue();
            if (!this.gravityValue.isRelative()) {
                final Particle particle10 = particle;
                particle10.gravityDiff -= particle.gravity;
            }
        }
        float[] color = particle.tint;
        if (color == null) {
            color = (particle.tint = new float[3]);
        }
        final float[] temp = this.tintValue.getColor(0.0f);
        color[0] = temp[0];
        color[1] = temp[1];
        color[2] = temp[2];
        particle.transparency = this.transparencyValue.newLowValue();
        particle.transparencyDiff = this.transparencyValue.newHighValue() - particle.transparency;
        float x = this.x;
        if (this.xOffsetValue.active) {
            x += this.xOffsetValue.newLowValue();
        }
        float y = this.y;
        if (this.yOffsetValue.active) {
            y += this.yOffsetValue.newLowValue();
        }
        switch (this.spawnShapeValue.shape) {
            case square: {
                final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
                final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
                x += MathUtils.random(width) - width / 2.0f;
                y += MathUtils.random(height) - height / 2.0f;
                break;
            }
            case ellipse: {
                final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
                final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
                final float radiusX = width / 2.0f;
                final float radiusY = height / 2.0f;
                if (radiusX == 0.0f) {
                    break;
                }
                if (radiusY == 0.0f) {
                    break;
                }
                final float scaleY = radiusX / radiusY;
                if (!this.spawnShapeValue.edges) {
                    final float radius2 = radiusX * radiusX;
                    float px;
                    float py;
                    do {
                        px = MathUtils.random(width) - radiusX;
                        py = MathUtils.random(height) - radiusY;
                    } while (px * px + py * py > radius2);
                    x += px;
                    y += py / scaleY;
                    break;
                }
                float spawnAngle = 0.0f;
                switch (this.spawnShapeValue.side) {
                    case top: {
                        spawnAngle = -MathUtils.random(179.0f);
                        break;
                    }
                    case bottom: {
                        spawnAngle = MathUtils.random(179.0f);
                        break;
                    }
                    default: {
                        spawnAngle = MathUtils.random(360.0f);
                        break;
                    }
                }
                final float cosDeg = MathUtils.cosDeg(spawnAngle);
                final float sinDeg = MathUtils.sinDeg(spawnAngle);
                x += cosDeg * radiusX;
                y += sinDeg * radiusX / scaleY;
                if ((updateFlags & 0x2) == 0x0) {
                    particle.angle = spawnAngle;
                    particle.angleCos = cosDeg;
                    particle.angleSin = sinDeg;
                    break;
                }
                break;
            }
            case line: {
                final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
                final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
                if (width != 0.0f) {
                    final float lineX = width * MathUtils.random();
                    x += lineX;
                    y += lineX * (height / width);
                    break;
                }
                y += height * MathUtils.random();
                break;
            }
        }
        particle.setBounds(x - spriteWidth / 2.0f, y - spriteHeight / 2.0f, spriteWidth, spriteHeight);
        int offsetTime = (int)(this.lifeOffset + this.lifeOffsetDiff * this.lifeOffsetValue.getScale(percent));
        if (offsetTime > 0) {
            if (offsetTime >= particle.currentLife) {
                offsetTime = particle.currentLife - 1;
            }
            this.updateParticle(particle, offsetTime / 1000.0f, offsetTime);
        }
    }
    
    private boolean updateParticle(final Particle particle, final float delta, final int deltaMillis) {
        final int life = particle.currentLife - deltaMillis;
        if (life <= 0) {
            return false;
        }
        particle.currentLife = life;
        final float percent = 1.0f - particle.currentLife / (float)particle.life;
        final int updateFlags = this.updateFlags;
        if ((updateFlags & 0x1) != 0x0) {
            if (this.yScaleValue.active) {
                particle.setScale(particle.xScale + particle.xScaleDiff * this.xScaleValue.getScale(percent), particle.yScale + particle.yScaleDiff * this.yScaleValue.getScale(percent));
            }
            else {
                particle.setScale(particle.xScale + particle.xScaleDiff * this.xScaleValue.getScale(percent));
            }
        }
        if ((updateFlags & 0x8) != 0x0) {
            final float velocity = (particle.velocity + particle.velocityDiff * this.velocityValue.getScale(percent)) * delta;
            float velocityX;
            float velocityY;
            if ((updateFlags & 0x2) != 0x0) {
                final float angle = particle.angle + particle.angleDiff * this.angleValue.getScale(percent);
                velocityX = velocity * MathUtils.cosDeg(angle);
                velocityY = velocity * MathUtils.sinDeg(angle);
                if ((updateFlags & 0x4) != 0x0) {
                    float rotation = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent);
                    if (this.aligned) {
                        rotation += angle;
                    }
                    particle.setRotation(rotation);
                }
            }
            else {
                velocityX = velocity * particle.angleCos;
                velocityY = velocity * particle.angleSin;
                if (this.aligned || (updateFlags & 0x4) != 0x0) {
                    float rotation2 = particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent);
                    if (this.aligned) {
                        rotation2 += particle.angle;
                    }
                    particle.setRotation(rotation2);
                }
            }
            if ((updateFlags & 0x10) != 0x0) {
                velocityX += (particle.wind + particle.windDiff * this.windValue.getScale(percent)) * delta;
            }
            if ((updateFlags & 0x20) != 0x0) {
                velocityY += (particle.gravity + particle.gravityDiff * this.gravityValue.getScale(percent)) * delta;
            }
            particle.translate(velocityX, velocityY);
        }
        else if ((updateFlags & 0x4) != 0x0) {
            particle.setRotation(particle.rotation + particle.rotationDiff * this.rotationValue.getScale(percent));
        }
        float[] color;
        if ((updateFlags & 0x40) != 0x0) {
            color = this.tintValue.getColor(percent);
        }
        else {
            color = particle.tint;
        }
        if (this.premultipliedAlpha) {
            final float alphaMultiplier = (float)(this.additive ? 0 : 1);
            final float a = particle.transparency + particle.transparencyDiff * this.transparencyValue.getScale(percent);
            particle.setColor(color[0] * a, color[1] * a, color[2] * a, a * alphaMultiplier);
        }
        else {
            particle.setColor(color[0], color[1], color[2], particle.transparency + particle.transparencyDiff * this.transparencyValue.getScale(percent));
        }
        if ((updateFlags & 0x80) != 0x0) {
            final int frame = Math.min((int)(percent * this.sprites.size), this.sprites.size - 1);
            if (particle.frame != frame) {
                final Sprite sprite = this.sprites.get(frame);
                final float prevSpriteWidth = particle.getWidth();
                final float prevSpriteHeight = particle.getHeight();
                particle.setRegion(sprite);
                particle.setSize(sprite.getWidth(), sprite.getHeight());
                particle.setOrigin(sprite.getOriginX(), sprite.getOriginY());
                particle.translate((prevSpriteWidth - sprite.getWidth()) / 2.0f, (prevSpriteHeight - sprite.getHeight()) / 2.0f);
                particle.frame = frame;
            }
        }
        return true;
    }
    
    private void generateLifeValues() {
        this.life = (int)this.lifeValue.newLowValue();
        this.lifeDiff = (int)this.lifeValue.newHighValue();
        if (!this.lifeValue.isRelative()) {
            this.lifeDiff -= this.life;
        }
    }
    
    private void generateLifeOffsetValues() {
        this.lifeOffset = (this.lifeOffsetValue.active ? ((int)this.lifeOffsetValue.newLowValue()) : 0);
        this.lifeOffsetDiff = (int)this.lifeOffsetValue.newHighValue();
        if (!this.lifeOffsetValue.isRelative()) {
            this.lifeOffsetDiff -= this.lifeOffset;
        }
    }
    
    public void setPosition(final float x, final float y) {
        if (this.attached) {
            final float xAmount = x - this.x;
            final float yAmount = y - this.y;
            final boolean[] active = this.active;
            for (int i = 0, n = active.length; i < n; ++i) {
                if (active[i]) {
                    this.particles[i].translate(xAmount, yAmount);
                }
            }
        }
        this.x = x;
        this.y = y;
    }
    
    public void setSprites(final Array<Sprite> sprites) {
        this.sprites = sprites;
        if (sprites.size == 0) {
            return;
        }
        for (int i = 0, n = this.particles.length; i < n; ++i) {
            final Particle particle = this.particles[i];
            if (particle == null) {
                break;
            }
            Sprite sprite = null;
            switch (this.spriteMode) {
                case single: {
                    sprite = sprites.first();
                    break;
                }
                case random: {
                    sprite = sprites.random();
                    break;
                }
                case animated: {
                    final float percent = 1.0f - particle.currentLife / (float)particle.life;
                    particle.frame = Math.min((int)(percent * sprites.size), sprites.size - 1);
                    sprite = sprites.get(particle.frame);
                    break;
                }
            }
            particle.setRegion(sprite);
            particle.setOrigin(sprite.getOriginX(), sprite.getOriginY());
        }
    }
    
    public void setSpriteMode(final SpriteMode spriteMode) {
        this.spriteMode = spriteMode;
    }
    
    public void allowCompletion() {
        this.allowCompletion = true;
        this.durationTimer = this.duration;
    }
    
    public Array<Sprite> getSprites() {
        return this.sprites;
    }
    
    public SpriteMode getSpriteMode() {
        return this.spriteMode;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ScaledNumericValue getLife() {
        return this.lifeValue;
    }
    
    public ScaledNumericValue getXScale() {
        return this.xScaleValue;
    }
    
    public ScaledNumericValue getYScale() {
        return this.yScaleValue;
    }
    
    public ScaledNumericValue getRotation() {
        return this.rotationValue;
    }
    
    public GradientColorValue getTint() {
        return this.tintValue;
    }
    
    public ScaledNumericValue getVelocity() {
        return this.velocityValue;
    }
    
    public ScaledNumericValue getWind() {
        return this.windValue;
    }
    
    public ScaledNumericValue getGravity() {
        return this.gravityValue;
    }
    
    public ScaledNumericValue getAngle() {
        return this.angleValue;
    }
    
    public ScaledNumericValue getEmission() {
        return this.emissionValue;
    }
    
    public ScaledNumericValue getTransparency() {
        return this.transparencyValue;
    }
    
    public RangedNumericValue getDuration() {
        return this.durationValue;
    }
    
    public RangedNumericValue getDelay() {
        return this.delayValue;
    }
    
    public ScaledNumericValue getLifeOffset() {
        return this.lifeOffsetValue;
    }
    
    public RangedNumericValue getXOffsetValue() {
        return this.xOffsetValue;
    }
    
    public RangedNumericValue getYOffsetValue() {
        return this.yOffsetValue;
    }
    
    public ScaledNumericValue getSpawnWidth() {
        return this.spawnWidthValue;
    }
    
    public ScaledNumericValue getSpawnHeight() {
        return this.spawnHeightValue;
    }
    
    public SpawnShapeValue getSpawnShape() {
        return this.spawnShapeValue;
    }
    
    public boolean isAttached() {
        return this.attached;
    }
    
    public void setAttached(final boolean attached) {
        this.attached = attached;
    }
    
    public boolean isContinuous() {
        return this.continuous;
    }
    
    public void setContinuous(final boolean continuous) {
        this.continuous = continuous;
    }
    
    public boolean isAligned() {
        return this.aligned;
    }
    
    public void setAligned(final boolean aligned) {
        this.aligned = aligned;
    }
    
    public boolean isAdditive() {
        return this.additive;
    }
    
    public void setAdditive(final boolean additive) {
        this.additive = additive;
    }
    
    public boolean cleansUpBlendFunction() {
        return this.cleansUpBlendFunction;
    }
    
    public void setCleansUpBlendFunction(final boolean cleansUpBlendFunction) {
        this.cleansUpBlendFunction = cleansUpBlendFunction;
    }
    
    public boolean isBehind() {
        return this.behind;
    }
    
    public void setBehind(final boolean behind) {
        this.behind = behind;
    }
    
    public boolean isPremultipliedAlpha() {
        return this.premultipliedAlpha;
    }
    
    public void setPremultipliedAlpha(final boolean premultipliedAlpha) {
        this.premultipliedAlpha = premultipliedAlpha;
    }
    
    public int getMinParticleCount() {
        return this.minParticleCount;
    }
    
    public void setMinParticleCount(final int minParticleCount) {
        this.minParticleCount = minParticleCount;
    }
    
    public int getMaxParticleCount() {
        return this.maxParticleCount;
    }
    
    public boolean isComplete() {
        return (!this.continuous || this.allowCompletion) && this.delayTimer >= this.delay && (this.durationTimer >= this.duration && this.activeCount == 0);
    }
    
    public float getPercentComplete() {
        if (this.delayTimer < this.delay) {
            return 0.0f;
        }
        return Math.min(1.0f, this.durationTimer / this.duration);
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public int getActiveCount() {
        return this.activeCount;
    }
    
    public Array<String> getImagePaths() {
        return this.imagePaths;
    }
    
    public void setImagePaths(final Array<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
    
    public void setFlip(final boolean flipX, final boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;
        if (this.particles == null) {
            return;
        }
        for (int i = 0, n = this.particles.length; i < n; ++i) {
            final Particle particle = this.particles[i];
            if (particle != null) {
                particle.flip(flipX, flipY);
            }
        }
    }
    
    public void flipY() {
        this.angleValue.setHigh(-this.angleValue.getHighMin(), -this.angleValue.getHighMax());
        this.angleValue.setLow(-this.angleValue.getLowMin(), -this.angleValue.getLowMax());
        this.gravityValue.setHigh(-this.gravityValue.getHighMin(), -this.gravityValue.getHighMax());
        this.gravityValue.setLow(-this.gravityValue.getLowMin(), -this.gravityValue.getLowMax());
        this.windValue.setHigh(-this.windValue.getHighMin(), -this.windValue.getHighMax());
        this.windValue.setLow(-this.windValue.getLowMin(), -this.windValue.getLowMax());
        this.rotationValue.setHigh(-this.rotationValue.getHighMin(), -this.rotationValue.getHighMax());
        this.rotationValue.setLow(-this.rotationValue.getLowMin(), -this.rotationValue.getLowMax());
        this.yOffsetValue.setLow(-this.yOffsetValue.getLowMin(), -this.yOffsetValue.getLowMax());
    }
    
    public BoundingBox getBoundingBox() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox();
        }
        final Particle[] particles = this.particles;
        final boolean[] active = this.active;
        final BoundingBox bounds = this.bounds;
        bounds.inf();
        for (int i = 0, n = active.length; i < n; ++i) {
            if (active[i]) {
                final Rectangle r = particles[i].getBoundingRectangle();
                bounds.ext(r.x, r.y, 0.0f);
                bounds.ext(r.x + r.width, r.y + r.height, 0.0f);
            }
        }
        return bounds;
    }
    
    protected RangedNumericValue[] getXSizeValues() {
        if (this.xSizeValues == null) {
            (this.xSizeValues = new RangedNumericValue[3])[0] = this.xScaleValue;
            this.xSizeValues[1] = this.spawnWidthValue;
            this.xSizeValues[2] = this.xOffsetValue;
        }
        return this.xSizeValues;
    }
    
    protected RangedNumericValue[] getYSizeValues() {
        if (this.ySizeValues == null) {
            (this.ySizeValues = new RangedNumericValue[3])[0] = this.yScaleValue;
            this.ySizeValues[1] = this.spawnHeightValue;
            this.ySizeValues[2] = this.yOffsetValue;
        }
        return this.ySizeValues;
    }
    
    protected RangedNumericValue[] getMotionValues() {
        if (this.motionValues == null) {
            (this.motionValues = new RangedNumericValue[3])[0] = this.velocityValue;
            this.motionValues[1] = this.windValue;
            this.motionValues[2] = this.gravityValue;
        }
        return this.motionValues;
    }
    
    public void scaleSize(final float scale) {
        if (scale == 1.0f) {
            return;
        }
        this.scaleSize(scale, scale);
    }
    
    public void scaleSize(final float scaleX, final float scaleY) {
        if (scaleX == 1.0f && scaleY == 1.0f) {
            return;
        }
        RangedNumericValue[] xSizeValues;
        for (int length = (xSizeValues = this.getXSizeValues()).length, i = 0; i < length; ++i) {
            final RangedNumericValue value = xSizeValues[i];
            value.scale(scaleX);
        }
        RangedNumericValue[] ySizeValues;
        for (int length2 = (ySizeValues = this.getYSizeValues()).length, j = 0; j < length2; ++j) {
            final RangedNumericValue value = ySizeValues[j];
            value.scale(scaleY);
        }
    }
    
    public void scaleMotion(final float scale) {
        if (scale == 1.0f) {
            return;
        }
        RangedNumericValue[] motionValues;
        for (int length = (motionValues = this.getMotionValues()).length, i = 0; i < length; ++i) {
            final RangedNumericValue value = motionValues[i];
            value.scale(scale);
        }
    }
    
    public void matchSize(final ParticleEmitter template) {
        this.matchXSize(template);
        this.matchYSize(template);
    }
    
    public void matchXSize(final ParticleEmitter template) {
        final RangedNumericValue[] values = this.getXSizeValues();
        final RangedNumericValue[] templateValues = template.getXSizeValues();
        for (int i = 0; i < values.length; ++i) {
            values[i].set(templateValues[i]);
        }
    }
    
    public void matchYSize(final ParticleEmitter template) {
        final RangedNumericValue[] values = this.getYSizeValues();
        final RangedNumericValue[] templateValues = template.getYSizeValues();
        for (int i = 0; i < values.length; ++i) {
            values[i].set(templateValues[i]);
        }
    }
    
    public void matchMotion(final ParticleEmitter template) {
        final RangedNumericValue[] values = this.getMotionValues();
        final RangedNumericValue[] templateValues = template.getMotionValues();
        for (int i = 0; i < values.length; ++i) {
            values[i].set(templateValues[i]);
        }
    }
    
    public void save(final Writer output) throws IOException {
        output.write(String.valueOf(this.name) + "\n");
        output.write("- Delay -\n");
        this.delayValue.save(output);
        output.write("- Duration - \n");
        this.durationValue.save(output);
        output.write("- Count - \n");
        output.write("min: " + this.minParticleCount + "\n");
        output.write("max: " + this.maxParticleCount + "\n");
        output.write("- Emission - \n");
        this.emissionValue.save(output);
        output.write("- Life - \n");
        this.lifeValue.save(output);
        output.write("- Life Offset - \n");
        this.lifeOffsetValue.save(output);
        output.write("- X Offset - \n");
        this.xOffsetValue.save(output);
        output.write("- Y Offset - \n");
        this.yOffsetValue.save(output);
        output.write("- Spawn Shape - \n");
        this.spawnShapeValue.save(output);
        output.write("- Spawn Width - \n");
        this.spawnWidthValue.save(output);
        output.write("- Spawn Height - \n");
        this.spawnHeightValue.save(output);
        output.write("- X Scale - \n");
        this.xScaleValue.save(output);
        output.write("- Y Scale - \n");
        this.yScaleValue.save(output);
        output.write("- Velocity - \n");
        this.velocityValue.save(output);
        output.write("- Angle - \n");
        this.angleValue.save(output);
        output.write("- Rotation - \n");
        this.rotationValue.save(output);
        output.write("- Wind - \n");
        this.windValue.save(output);
        output.write("- Gravity - \n");
        this.gravityValue.save(output);
        output.write("- Tint - \n");
        this.tintValue.save(output);
        output.write("- Transparency - \n");
        this.transparencyValue.save(output);
        output.write("- Options - \n");
        output.write("attached: " + this.attached + "\n");
        output.write("continuous: " + this.continuous + "\n");
        output.write("aligned: " + this.aligned + "\n");
        output.write("additive: " + this.additive + "\n");
        output.write("behind: " + this.behind + "\n");
        output.write("premultipliedAlpha: " + this.premultipliedAlpha + "\n");
        output.write("spriteMode: " + this.spriteMode.toString() + "\n");
        output.write("- Image Paths -\n");
        for (final String imagePath : this.imagePaths) {
            output.write(String.valueOf(imagePath) + "\n");
        }
        output.write("\n");
    }
    
    public void load(final BufferedReader reader) throws IOException {
        try {
            this.name = readString(reader, "name");
            reader.readLine();
            this.delayValue.load(reader);
            reader.readLine();
            this.durationValue.load(reader);
            reader.readLine();
            this.setMinParticleCount(readInt(reader, "minParticleCount"));
            this.setMaxParticleCount(readInt(reader, "maxParticleCount"));
            reader.readLine();
            this.emissionValue.load(reader);
            reader.readLine();
            this.lifeValue.load(reader);
            reader.readLine();
            this.lifeOffsetValue.load(reader);
            reader.readLine();
            this.xOffsetValue.load(reader);
            reader.readLine();
            this.yOffsetValue.load(reader);
            reader.readLine();
            this.spawnShapeValue.load(reader);
            reader.readLine();
            this.spawnWidthValue.load(reader);
            reader.readLine();
            this.spawnHeightValue.load(reader);
            String line = reader.readLine();
            if (line.trim().equals("- Scale -")) {
                this.xScaleValue.load(reader);
                this.yScaleValue.setActive(false);
            }
            else {
                this.xScaleValue.load(reader);
                reader.readLine();
                this.yScaleValue.load(reader);
            }
            reader.readLine();
            this.velocityValue.load(reader);
            reader.readLine();
            this.angleValue.load(reader);
            reader.readLine();
            this.rotationValue.load(reader);
            reader.readLine();
            this.windValue.load(reader);
            reader.readLine();
            this.gravityValue.load(reader);
            reader.readLine();
            this.tintValue.load(reader);
            reader.readLine();
            this.transparencyValue.load(reader);
            reader.readLine();
            this.attached = readBoolean(reader, "attached");
            this.continuous = readBoolean(reader, "continuous");
            this.aligned = readBoolean(reader, "aligned");
            this.additive = readBoolean(reader, "additive");
            this.behind = readBoolean(reader, "behind");
            line = reader.readLine();
            if (line.startsWith("premultipliedAlpha")) {
                this.premultipliedAlpha = readBoolean(line);
                line = reader.readLine();
            }
            if (line.startsWith("spriteMode")) {
                this.spriteMode = SpriteMode.valueOf(readString(line));
                line = reader.readLine();
            }
            final Array<String> imagePaths = new Array<String>();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                imagePaths.add(line);
            }
            this.setImagePaths(imagePaths);
        }
        catch (RuntimeException ex) {
            if (this.name == null) {
                throw ex;
            }
            throw new RuntimeException("Error parsing emitter: " + this.name, ex);
        }
    }
    
    static String readString(final String line) throws IOException {
        return line.substring(line.indexOf(":") + 1).trim();
    }
    
    static String readString(final BufferedReader reader, final String name) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            throw new IOException("Missing value: " + name);
        }
        return readString(line);
    }
    
    static boolean readBoolean(final String line) throws IOException {
        return Boolean.parseBoolean(readString(line));
    }
    
    static boolean readBoolean(final BufferedReader reader, final String name) throws IOException {
        return Boolean.parseBoolean(readString(reader, name));
    }
    
    static int readInt(final BufferedReader reader, final String name) throws IOException {
        return Integer.parseInt(readString(reader, name));
    }
    
    static float readFloat(final BufferedReader reader, final String name) throws IOException {
        return Float.parseFloat(readString(reader, name));
    }
    
    public enum SpawnEllipseSide
    {
        both("both", 0), 
        top("top", 1), 
        bottom("bottom", 2);
        
        private SpawnEllipseSide(final String name, final int ordinal) {
        }
    }
    
    public enum SpawnShape
    {
        point("point", 0), 
        line("line", 1), 
        square("square", 2), 
        ellipse("ellipse", 3);
        
        private SpawnShape(final String name, final int ordinal) {
        }
    }
    
    public enum SpriteMode
    {
        single("single", 0), 
        random("random", 1), 
        animated("animated", 2);
        
        private SpriteMode(final String name, final int ordinal) {
        }
    }
    
    public static class Particle extends Sprite
    {
        protected int life;
        protected int currentLife;
        protected float xScale;
        protected float xScaleDiff;
        protected float yScale;
        protected float yScaleDiff;
        protected float rotation;
        protected float rotationDiff;
        protected float velocity;
        protected float velocityDiff;
        protected float angle;
        protected float angleDiff;
        protected float angleCos;
        protected float angleSin;
        protected float transparency;
        protected float transparencyDiff;
        protected float wind;
        protected float windDiff;
        protected float gravity;
        protected float gravityDiff;
        protected float[] tint;
        protected int frame;
        
        public Particle(final Sprite sprite) {
            super(sprite);
        }
    }
    
    public static class ParticleValue
    {
        boolean active;
        boolean alwaysActive;
        
        public void setAlwaysActive(final boolean alwaysActive) {
            this.alwaysActive = alwaysActive;
        }
        
        public boolean isAlwaysActive() {
            return this.alwaysActive;
        }
        
        public boolean isActive() {
            return this.alwaysActive || this.active;
        }
        
        public void setActive(final boolean active) {
            this.active = active;
        }
        
        public void save(final Writer output) throws IOException {
            if (!this.alwaysActive) {
                output.write("active: " + this.active + "\n");
            }
            else {
                this.active = true;
            }
        }
        
        public void load(final BufferedReader reader) throws IOException {
            if (!this.alwaysActive) {
                this.active = ParticleEmitter.readBoolean(reader, "active");
            }
            else {
                this.active = true;
            }
        }
        
        public void load(final ParticleValue value) {
            this.active = value.active;
            this.alwaysActive = value.alwaysActive;
        }
    }
    
    public static class NumericValue extends ParticleValue
    {
        private float value;
        
        public float getValue() {
            return this.value;
        }
        
        public void setValue(final float value) {
            this.value = value;
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            if (!this.active) {
                return;
            }
            output.write("value: " + this.value + "\n");
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            if (!this.active) {
                return;
            }
            this.value = ParticleEmitter.readFloat(reader, "value");
        }
        
        public void load(final NumericValue value) {
            super.load(value);
            this.value = value.value;
        }
    }
    
    public static class RangedNumericValue extends ParticleValue
    {
        private float lowMin;
        private float lowMax;
        
        public float newLowValue() {
            return this.lowMin + (this.lowMax - this.lowMin) * MathUtils.random();
        }
        
        public void setLow(final float value) {
            this.lowMin = value;
            this.lowMax = value;
        }
        
        public void setLow(final float min, final float max) {
            this.lowMin = min;
            this.lowMax = max;
        }
        
        public float getLowMin() {
            return this.lowMin;
        }
        
        public void setLowMin(final float lowMin) {
            this.lowMin = lowMin;
        }
        
        public float getLowMax() {
            return this.lowMax;
        }
        
        public void setLowMax(final float lowMax) {
            this.lowMax = lowMax;
        }
        
        public void scale(final float scale) {
            this.lowMin *= scale;
            this.lowMax *= scale;
        }
        
        public void set(final RangedNumericValue value) {
            this.lowMin = value.lowMin;
            this.lowMax = value.lowMax;
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            if (!this.active) {
                return;
            }
            output.write("lowMin: " + this.lowMin + "\n");
            output.write("lowMax: " + this.lowMax + "\n");
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            if (!this.active) {
                return;
            }
            this.lowMin = ParticleEmitter.readFloat(reader, "lowMin");
            this.lowMax = ParticleEmitter.readFloat(reader, "lowMax");
        }
        
        public void load(final RangedNumericValue value) {
            super.load(value);
            this.lowMax = value.lowMax;
            this.lowMin = value.lowMin;
        }
    }
    
    public static class ScaledNumericValue extends RangedNumericValue
    {
        private float[] scaling;
        float[] timeline;
        private float highMin;
        private float highMax;
        private boolean relative;
        
        public ScaledNumericValue() {
            this.scaling = new float[] { 1.0f };
            this.timeline = new float[] { 0.0f };
        }
        
        public float newHighValue() {
            return this.highMin + (this.highMax - this.highMin) * MathUtils.random();
        }
        
        public void setHigh(final float value) {
            this.highMin = value;
            this.highMax = value;
        }
        
        public void setHigh(final float min, final float max) {
            this.highMin = min;
            this.highMax = max;
        }
        
        public float getHighMin() {
            return this.highMin;
        }
        
        public void setHighMin(final float highMin) {
            this.highMin = highMin;
        }
        
        public float getHighMax() {
            return this.highMax;
        }
        
        public void setHighMax(final float highMax) {
            this.highMax = highMax;
        }
        
        @Override
        public void scale(final float scale) {
            super.scale(scale);
            this.highMin *= scale;
            this.highMax *= scale;
        }
        
        @Override
        public void set(final RangedNumericValue value) {
            if (value instanceof ScaledNumericValue) {
                this.set((ScaledNumericValue)value);
            }
            else {
                super.set(value);
            }
        }
        
        public void set(final ScaledNumericValue value) {
            super.set(value);
            this.highMin = value.highMin;
            this.highMax = value.highMax;
            if (this.scaling.length != value.scaling.length) {
                this.scaling = Arrays.copyOf(value.scaling, value.scaling.length);
            }
            else {
                System.arraycopy(value.scaling, 0, this.scaling, 0, this.scaling.length);
            }
            if (this.timeline.length != value.timeline.length) {
                this.timeline = Arrays.copyOf(value.timeline, value.timeline.length);
            }
            else {
                System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
            }
            this.relative = value.relative;
        }
        
        public float[] getScaling() {
            return this.scaling;
        }
        
        public void setScaling(final float[] values) {
            this.scaling = values;
        }
        
        public float[] getTimeline() {
            return this.timeline;
        }
        
        public void setTimeline(final float[] timeline) {
            this.timeline = timeline;
        }
        
        public boolean isRelative() {
            return this.relative;
        }
        
        public void setRelative(final boolean relative) {
            this.relative = relative;
        }
        
        public float getScale(final float percent) {
            int endIndex = -1;
            final float[] timeline = this.timeline;
            final int n = timeline.length;
            for (int i = 1; i < n; ++i) {
                final float t = timeline[i];
                if (t > percent) {
                    endIndex = i;
                    break;
                }
            }
            if (endIndex == -1) {
                return this.scaling[n - 1];
            }
            final float[] scaling = this.scaling;
            final int startIndex = endIndex - 1;
            final float startValue = scaling[startIndex];
            final float startTime = timeline[startIndex];
            return startValue + (scaling[endIndex] - startValue) * ((percent - startTime) / (timeline[endIndex] - startTime));
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            if (!this.active) {
                return;
            }
            output.write("highMin: " + this.highMin + "\n");
            output.write("highMax: " + this.highMax + "\n");
            output.write("relative: " + this.relative + "\n");
            output.write("scalingCount: " + this.scaling.length + "\n");
            for (int i = 0; i < this.scaling.length; ++i) {
                output.write("scaling" + i + ": " + this.scaling[i] + "\n");
            }
            output.write("timelineCount: " + this.timeline.length + "\n");
            for (int i = 0; i < this.timeline.length; ++i) {
                output.write("timeline" + i + ": " + this.timeline[i] + "\n");
            }
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            if (!this.active) {
                return;
            }
            this.highMin = ParticleEmitter.readFloat(reader, "highMin");
            this.highMax = ParticleEmitter.readFloat(reader, "highMax");
            this.relative = ParticleEmitter.readBoolean(reader, "relative");
            this.scaling = new float[ParticleEmitter.readInt(reader, "scalingCount")];
            for (int i = 0; i < this.scaling.length; ++i) {
                this.scaling[i] = ParticleEmitter.readFloat(reader, "scaling" + i);
            }
            this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];
            for (int i = 0; i < this.timeline.length; ++i) {
                this.timeline[i] = ParticleEmitter.readFloat(reader, "timeline" + i);
            }
        }
        
        public void load(final ScaledNumericValue value) {
            super.load(value);
            this.highMax = value.highMax;
            this.highMin = value.highMin;
            this.scaling = new float[value.scaling.length];
            System.arraycopy(value.scaling, 0, this.scaling, 0, this.scaling.length);
            this.timeline = new float[value.timeline.length];
            System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
            this.relative = value.relative;
        }
    }
    
    public static class IndependentScaledNumericValue extends ScaledNumericValue
    {
        boolean independent;
        
        public boolean isIndependent() {
            return this.independent;
        }
        
        public void setIndependent(final boolean independent) {
            this.independent = independent;
        }
        
        @Override
        public void set(final RangedNumericValue value) {
            if (value instanceof IndependentScaledNumericValue) {
                this.set((IndependentScaledNumericValue)value);
            }
            else {
                super.set(value);
            }
        }
        
        @Override
        public void set(final ScaledNumericValue value) {
            if (value instanceof IndependentScaledNumericValue) {
                this.set((IndependentScaledNumericValue)value);
            }
            else {
                super.set(value);
            }
        }
        
        public void set(final IndependentScaledNumericValue value) {
            super.set(value);
            this.independent = value.independent;
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            output.write("independent: " + this.independent + "\n");
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            reader.mark(100);
            final String line = reader.readLine();
            if (line == null) {
                throw new IOException("Missing value: independent");
            }
            if (line.contains("independent")) {
                this.independent = Boolean.parseBoolean(ParticleEmitter.readString(line));
            }
            else {
                reader.reset();
            }
        }
        
        public void load(final IndependentScaledNumericValue value) {
            super.load(value);
            this.independent = value.independent;
        }
    }
    
    public static class GradientColorValue extends ParticleValue
    {
        private static float[] temp;
        private float[] colors;
        float[] timeline;
        
        static {
            GradientColorValue.temp = new float[4];
        }
        
        public GradientColorValue() {
            this.colors = new float[] { 1.0f, 1.0f, 1.0f };
            this.timeline = new float[] { 0.0f };
            this.alwaysActive = true;
        }
        
        public float[] getTimeline() {
            return this.timeline;
        }
        
        public void setTimeline(final float[] timeline) {
            this.timeline = timeline;
        }
        
        public float[] getColors() {
            return this.colors;
        }
        
        public void setColors(final float[] colors) {
            this.colors = colors;
        }
        
        public float[] getColor(final float percent) {
            int startIndex = 0;
            int endIndex = -1;
            final float[] timeline = this.timeline;
            for (int n = timeline.length, i = 1; i < n; ++i) {
                final float t = timeline[i];
                if (t > percent) {
                    endIndex = i;
                    break;
                }
                startIndex = i;
            }
            final float startTime = timeline[startIndex];
            startIndex *= 3;
            final float r1 = this.colors[startIndex];
            final float g1 = this.colors[startIndex + 1];
            final float b1 = this.colors[startIndex + 2];
            if (endIndex == -1) {
                GradientColorValue.temp[0] = r1;
                GradientColorValue.temp[1] = g1;
                GradientColorValue.temp[2] = b1;
                return GradientColorValue.temp;
            }
            final float factor = (percent - startTime) / (timeline[endIndex] - startTime);
            endIndex *= 3;
            GradientColorValue.temp[0] = r1 + (this.colors[endIndex] - r1) * factor;
            GradientColorValue.temp[1] = g1 + (this.colors[endIndex + 1] - g1) * factor;
            GradientColorValue.temp[2] = b1 + (this.colors[endIndex + 2] - b1) * factor;
            return GradientColorValue.temp;
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            if (!this.active) {
                return;
            }
            output.write("colorsCount: " + this.colors.length + "\n");
            for (int i = 0; i < this.colors.length; ++i) {
                output.write("colors" + i + ": " + this.colors[i] + "\n");
            }
            output.write("timelineCount: " + this.timeline.length + "\n");
            for (int i = 0; i < this.timeline.length; ++i) {
                output.write("timeline" + i + ": " + this.timeline[i] + "\n");
            }
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            if (!this.active) {
                return;
            }
            this.colors = new float[ParticleEmitter.readInt(reader, "colorsCount")];
            for (int i = 0; i < this.colors.length; ++i) {
                this.colors[i] = ParticleEmitter.readFloat(reader, "colors" + i);
            }
            this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];
            for (int i = 0; i < this.timeline.length; ++i) {
                this.timeline[i] = ParticleEmitter.readFloat(reader, "timeline" + i);
            }
        }
        
        public void load(final GradientColorValue value) {
            super.load(value);
            this.colors = new float[value.colors.length];
            System.arraycopy(value.colors, 0, this.colors, 0, this.colors.length);
            this.timeline = new float[value.timeline.length];
            System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
        }
    }
    
    public static class SpawnShapeValue extends ParticleValue
    {
        SpawnShape shape;
        boolean edges;
        SpawnEllipseSide side;
        
        public SpawnShapeValue() {
            this.shape = SpawnShape.point;
            this.side = SpawnEllipseSide.both;
        }
        
        public SpawnShape getShape() {
            return this.shape;
        }
        
        public void setShape(final SpawnShape shape) {
            this.shape = shape;
        }
        
        public boolean isEdges() {
            return this.edges;
        }
        
        public void setEdges(final boolean edges) {
            this.edges = edges;
        }
        
        public SpawnEllipseSide getSide() {
            return this.side;
        }
        
        public void setSide(final SpawnEllipseSide side) {
            this.side = side;
        }
        
        @Override
        public void save(final Writer output) throws IOException {
            super.save(output);
            if (!this.active) {
                return;
            }
            output.write("shape: " + this.shape + "\n");
            if (this.shape == SpawnShape.ellipse) {
                output.write("edges: " + this.edges + "\n");
                output.write("side: " + this.side + "\n");
            }
        }
        
        @Override
        public void load(final BufferedReader reader) throws IOException {
            super.load(reader);
            if (!this.active) {
                return;
            }
            this.shape = SpawnShape.valueOf(ParticleEmitter.readString(reader, "shape"));
            if (this.shape == SpawnShape.ellipse) {
                this.edges = ParticleEmitter.readBoolean(reader, "edges");
                this.side = SpawnEllipseSide.valueOf(ParticleEmitter.readString(reader, "side"));
            }
        }
        
        public void load(final SpawnShapeValue value) {
            super.load(value);
            this.shape = value.shape;
            this.edges = value.edges;
            this.side = value.side;
        }
    }
}

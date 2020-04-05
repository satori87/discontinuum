// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.input;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.InputAdapter;

public class GestureDetector extends InputAdapter
{
    final GestureListener listener;
    private float tapRectangleWidth;
    private float tapRectangleHeight;
    private long tapCountInterval;
    private float longPressSeconds;
    private long maxFlingDelay;
    private boolean inTapRectangle;
    private int tapCount;
    private long lastTapTime;
    private float lastTapX;
    private float lastTapY;
    private int lastTapButton;
    private int lastTapPointer;
    boolean longPressFired;
    private boolean pinching;
    private boolean panning;
    private final VelocityTracker tracker;
    private float tapRectangleCenterX;
    private float tapRectangleCenterY;
    private long gestureStartTime;
    Vector2 pointer1;
    private final Vector2 pointer2;
    private final Vector2 initialPointer1;
    private final Vector2 initialPointer2;
    private final Timer.Task longPressTask;
    
    public GestureDetector(final GestureListener listener) {
        this(20.0f, 0.4f, 1.1f, 0.15f, listener);
    }
    
    public GestureDetector(final float halfTapSquareSize, final float tapCountInterval, final float longPressDuration, final float maxFlingDelay, final GestureListener listener) {
        this(halfTapSquareSize, halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
    }
    
    public GestureDetector(final float halfTapRectangleWidth, final float halfTapRectangleHeight, final float tapCountInterval, final float longPressDuration, final float maxFlingDelay, final GestureListener listener) {
        this.tracker = new VelocityTracker();
        this.pointer1 = new Vector2();
        this.pointer2 = new Vector2();
        this.initialPointer1 = new Vector2();
        this.initialPointer2 = new Vector2();
        this.longPressTask = new Timer.Task() {
            @Override
            public void run() {
                if (!GestureDetector.this.longPressFired) {
                    GestureDetector.this.longPressFired = GestureDetector.this.listener.longPress(GestureDetector.this.pointer1.x, GestureDetector.this.pointer1.y);
                }
            }
        };
        this.tapRectangleWidth = halfTapRectangleWidth;
        this.tapRectangleHeight = halfTapRectangleHeight;
        this.tapCountInterval = (long)(tapCountInterval * 1.0E9f);
        this.longPressSeconds = longPressDuration;
        this.maxFlingDelay = (long)(maxFlingDelay * 1.0E9f);
        this.listener = listener;
    }
    
    @Override
    public boolean touchDown(final int x, final int y, final int pointer, final int button) {
        return this.touchDown((float)x, (float)y, pointer, button);
    }
    
    public boolean touchDown(final float x, final float y, final int pointer, final int button) {
        if (pointer > 1) {
            return false;
        }
        if (pointer == 0) {
            this.pointer1.set(x, y);
            this.gestureStartTime = Gdx.input.getCurrentEventTime();
            this.tracker.start(x, y, this.gestureStartTime);
            if (Gdx.input.isTouched(1)) {
                this.inTapRectangle = false;
                this.pinching = true;
                this.initialPointer1.set(this.pointer1);
                this.initialPointer2.set(this.pointer2);
                this.longPressTask.cancel();
            }
            else {
                this.inTapRectangle = true;
                this.pinching = false;
                this.longPressFired = false;
                this.tapRectangleCenterX = x;
                this.tapRectangleCenterY = y;
                if (!this.longPressTask.isScheduled()) {
                    Timer.schedule(this.longPressTask, this.longPressSeconds);
                }
            }
        }
        else {
            this.pointer2.set(x, y);
            this.inTapRectangle = false;
            this.pinching = true;
            this.initialPointer1.set(this.pointer1);
            this.initialPointer2.set(this.pointer2);
            this.longPressTask.cancel();
        }
        return this.listener.touchDown(x, y, pointer, button);
    }
    
    @Override
    public boolean touchDragged(final int x, final int y, final int pointer) {
        return this.touchDragged((float)x, (float)y, pointer);
    }
    
    public boolean touchDragged(final float x, final float y, final int pointer) {
        if (pointer > 1) {
            return false;
        }
        if (this.longPressFired) {
            return false;
        }
        if (pointer == 0) {
            this.pointer1.set(x, y);
        }
        else {
            this.pointer2.set(x, y);
        }
        if (this.pinching) {
            if (this.listener != null) {
                final boolean result = this.listener.pinch(this.initialPointer1, this.initialPointer2, this.pointer1, this.pointer2);
                return this.listener.zoom(this.initialPointer1.dst(this.initialPointer2), this.pointer1.dst(this.pointer2)) || result;
            }
            return false;
        }
        else {
            this.tracker.update(x, y, Gdx.input.getCurrentEventTime());
            if (this.inTapRectangle && !this.isWithinTapRectangle(x, y, this.tapRectangleCenterX, this.tapRectangleCenterY)) {
                this.longPressTask.cancel();
                this.inTapRectangle = false;
            }
            if (!this.inTapRectangle) {
                this.panning = true;
                return this.listener.pan(x, y, this.tracker.deltaX, this.tracker.deltaY);
            }
            return false;
        }
    }
    
    @Override
    public boolean touchUp(final int x, final int y, final int pointer, final int button) {
        return this.touchUp((float)x, (float)y, pointer, button);
    }
    
    public boolean touchUp(final float x, final float y, final int pointer, final int button) {
        if (pointer > 1) {
            return false;
        }
        if (this.inTapRectangle && !this.isWithinTapRectangle(x, y, this.tapRectangleCenterX, this.tapRectangleCenterY)) {
            this.inTapRectangle = false;
        }
        final boolean wasPanning = this.panning;
        this.panning = false;
        this.longPressTask.cancel();
        if (this.longPressFired) {
            return false;
        }
        if (this.inTapRectangle) {
            if (this.lastTapButton != button || this.lastTapPointer != pointer || TimeUtils.nanoTime() - this.lastTapTime > this.tapCountInterval || !this.isWithinTapRectangle(x, y, this.lastTapX, this.lastTapY)) {
                this.tapCount = 0;
            }
            ++this.tapCount;
            this.lastTapTime = TimeUtils.nanoTime();
            this.lastTapX = x;
            this.lastTapY = y;
            this.lastTapButton = button;
            this.lastTapPointer = pointer;
            this.gestureStartTime = 0L;
            return this.listener.tap(x, y, this.tapCount, button);
        }
        if (this.pinching) {
            this.pinching = false;
            this.listener.pinchStop();
            this.panning = true;
            if (pointer == 0) {
                this.tracker.start(this.pointer2.x, this.pointer2.y, Gdx.input.getCurrentEventTime());
            }
            else {
                this.tracker.start(this.pointer1.x, this.pointer1.y, Gdx.input.getCurrentEventTime());
            }
            return false;
        }
        boolean handled = false;
        if (wasPanning && !this.panning) {
            handled = this.listener.panStop(x, y, pointer, button);
        }
        this.gestureStartTime = 0L;
        final long time = Gdx.input.getCurrentEventTime();
        if (time - this.tracker.lastTime < this.maxFlingDelay) {
            this.tracker.update(x, y, time);
            handled = (this.listener.fling(this.tracker.getVelocityX(), this.tracker.getVelocityY(), button) || handled);
        }
        return handled;
    }
    
    public void cancel() {
        this.longPressTask.cancel();
        this.longPressFired = true;
    }
    
    public boolean isLongPressed() {
        return this.isLongPressed(this.longPressSeconds);
    }
    
    public boolean isLongPressed(final float duration) {
        return this.gestureStartTime != 0L && TimeUtils.nanoTime() - this.gestureStartTime > (long)(duration * 1.0E9f);
    }
    
    public boolean isPanning() {
        return this.panning;
    }
    
    public void reset() {
        this.gestureStartTime = 0L;
        this.panning = false;
        this.inTapRectangle = false;
        this.tracker.lastTime = 0L;
    }
    
    private boolean isWithinTapRectangle(final float x, final float y, final float centerX, final float centerY) {
        return Math.abs(x - centerX) < this.tapRectangleWidth && Math.abs(y - centerY) < this.tapRectangleHeight;
    }
    
    public void invalidateTapSquare() {
        this.inTapRectangle = false;
    }
    
    public void setTapSquareSize(final float halfTapSquareSize) {
        this.setTapRectangleSize(halfTapSquareSize, halfTapSquareSize);
    }
    
    public void setTapRectangleSize(final float halfTapRectangleWidth, final float halfTapRectangleHeight) {
        this.tapRectangleWidth = halfTapRectangleWidth;
        this.tapRectangleHeight = halfTapRectangleHeight;
    }
    
    public void setTapCountInterval(final float tapCountInterval) {
        this.tapCountInterval = (long)(tapCountInterval * 1.0E9f);
    }
    
    public void setLongPressSeconds(final float longPressSeconds) {
        this.longPressSeconds = longPressSeconds;
    }
    
    public void setMaxFlingDelay(final long maxFlingDelay) {
        this.maxFlingDelay = maxFlingDelay;
    }
    
    public static class GestureAdapter implements GestureListener
    {
        @Override
        public boolean touchDown(final float x, final float y, final int pointer, final int button) {
            return false;
        }
        
        @Override
        public boolean tap(final float x, final float y, final int count, final int button) {
            return false;
        }
        
        @Override
        public boolean longPress(final float x, final float y) {
            return false;
        }
        
        @Override
        public boolean fling(final float velocityX, final float velocityY, final int button) {
            return false;
        }
        
        @Override
        public boolean pan(final float x, final float y, final float deltaX, final float deltaY) {
            return false;
        }
        
        @Override
        public boolean panStop(final float x, final float y, final int pointer, final int button) {
            return false;
        }
        
        @Override
        public boolean zoom(final float initialDistance, final float distance) {
            return false;
        }
        
        @Override
        public boolean pinch(final Vector2 initialPointer1, final Vector2 initialPointer2, final Vector2 pointer1, final Vector2 pointer2) {
            return false;
        }
        
        @Override
        public void pinchStop() {
        }
    }
    
    static class VelocityTracker
    {
        int sampleSize;
        float lastX;
        float lastY;
        float deltaX;
        float deltaY;
        long lastTime;
        int numSamples;
        float[] meanX;
        float[] meanY;
        long[] meanTime;
        
        VelocityTracker() {
            this.sampleSize = 10;
            this.meanX = new float[this.sampleSize];
            this.meanY = new float[this.sampleSize];
            this.meanTime = new long[this.sampleSize];
        }
        
        public void start(final float x, final float y, final long timeStamp) {
            this.lastX = x;
            this.lastY = y;
            this.deltaX = 0.0f;
            this.deltaY = 0.0f;
            this.numSamples = 0;
            for (int i = 0; i < this.sampleSize; ++i) {
                this.meanX[i] = 0.0f;
                this.meanY[i] = 0.0f;
                this.meanTime[i] = 0L;
            }
            this.lastTime = timeStamp;
        }
        
        public void update(final float x, final float y, final long currTime) {
            this.deltaX = x - this.lastX;
            this.deltaY = y - this.lastY;
            this.lastX = x;
            this.lastY = y;
            final long deltaTime = currTime - this.lastTime;
            this.lastTime = currTime;
            final int index = this.numSamples % this.sampleSize;
            this.meanX[index] = this.deltaX;
            this.meanY[index] = this.deltaY;
            this.meanTime[index] = deltaTime;
            ++this.numSamples;
        }
        
        public float getVelocityX() {
            final float meanX = this.getAverage(this.meanX, this.numSamples);
            final float meanTime = this.getAverage(this.meanTime, this.numSamples) / 1.0E9f;
            if (meanTime == 0.0f) {
                return 0.0f;
            }
            return meanX / meanTime;
        }
        
        public float getVelocityY() {
            final float meanY = this.getAverage(this.meanY, this.numSamples);
            final float meanTime = this.getAverage(this.meanTime, this.numSamples) / 1.0E9f;
            if (meanTime == 0.0f) {
                return 0.0f;
            }
            return meanY / meanTime;
        }
        
        private float getAverage(final float[] values, int numSamples) {
            numSamples = Math.min(this.sampleSize, numSamples);
            float sum = 0.0f;
            for (int i = 0; i < numSamples; ++i) {
                sum += values[i];
            }
            return sum / numSamples;
        }
        
        private long getAverage(final long[] values, int numSamples) {
            numSamples = Math.min(this.sampleSize, numSamples);
            long sum = 0L;
            for (int i = 0; i < numSamples; ++i) {
                sum += values[i];
            }
            if (numSamples == 0) {
                return 0L;
            }
            return sum / numSamples;
        }
        
        private float getSum(final float[] values, int numSamples) {
            numSamples = Math.min(this.sampleSize, numSamples);
            float sum = 0.0f;
            for (int i = 0; i < numSamples; ++i) {
                sum += values[i];
            }
            if (numSamples == 0) {
                return 0.0f;
            }
            return sum;
        }
    }
    
    public interface GestureListener
    {
        boolean touchDown(final float p0, final float p1, final int p2, final int p3);
        
        boolean tap(final float p0, final float p1, final int p2, final int p3);
        
        boolean longPress(final float p0, final float p1);
        
        boolean fling(final float p0, final float p1, final int p2);
        
        boolean pan(final float p0, final float p1, final float p2, final float p3);
        
        boolean panStop(final float p0, final float p1, final int p2, final int p3);
        
        boolean zoom(final float p0, final float p1);
        
        boolean pinch(final Vector2 p0, final Vector2 p1, final Vector2 p2, final Vector2 p3);
        
        void pinchStop();
    }
}

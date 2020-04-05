// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.IntArray;

public class InputEventQueue implements InputProcessor
{
    private static final int SKIP = -1;
    private static final int KEY_DOWN = 0;
    private static final int KEY_UP = 1;
    private static final int KEY_TYPED = 2;
    private static final int TOUCH_DOWN = 3;
    private static final int TOUCH_UP = 4;
    private static final int TOUCH_DRAGGED = 5;
    private static final int MOUSE_MOVED = 6;
    private static final int SCROLLED = 7;
    private InputProcessor processor;
    private final IntArray queue;
    private final IntArray processingQueue;
    private long currentEventTime;
    
    public InputEventQueue() {
        this.queue = new IntArray();
        this.processingQueue = new IntArray();
    }
    
    public InputEventQueue(final InputProcessor processor) {
        this.queue = new IntArray();
        this.processingQueue = new IntArray();
        this.processor = processor;
    }
    
    public void setProcessor(final InputProcessor processor) {
        this.processor = processor;
    }
    
    public InputProcessor getProcessor() {
        return this.processor;
    }
    
    public void drain() {
        synchronized (this) {
            if (this.processor == null) {
                this.queue.clear();
                // monitorexit(this)
                return;
            }
            this.processingQueue.addAll(this.queue);
            this.queue.clear();
        }
        final int[] q = this.processingQueue.items;
        final InputProcessor localProcessor = this.processor;
        int i = 0;
        final int n = this.processingQueue.size;
        while (i < n) {
            final int type = q[i++];
            this.currentEventTime = ((long)q[i++] << 32 | ((long)q[i++] & 0xFFFFFFFFL));
            switch (type) {
                case -1: {
                    i += q[i];
                    continue;
                }
                case 0: {
                    localProcessor.keyDown(q[i++]);
                    continue;
                }
                case 1: {
                    localProcessor.keyUp(q[i++]);
                    continue;
                }
                case 2: {
                    localProcessor.keyTyped((char)q[i++]);
                    continue;
                }
                case 3: {
                    localProcessor.touchDown(q[i++], q[i++], q[i++], q[i++]);
                    continue;
                }
                case 4: {
                    localProcessor.touchUp(q[i++], q[i++], q[i++], q[i++]);
                    continue;
                }
                case 5: {
                    localProcessor.touchDragged(q[i++], q[i++], q[i++]);
                    continue;
                }
                case 6: {
                    localProcessor.mouseMoved(q[i++], q[i++]);
                    continue;
                }
                case 7: {
                    localProcessor.scrolled(q[i++]);
                    continue;
                }
                default: {
                    throw new RuntimeException();
                }
            }
        }
        this.processingQueue.clear();
    }
    
    private synchronized int next(final int nextType, int i) {
        final int[] q = this.queue.items;
        final int n = this.queue.size;
        while (i < n) {
            final int type = q[i];
            if (type == nextType) {
                return i;
            }
            i += 3;
            switch (type) {
                case -1: {
                    i += q[i];
                    continue;
                }
                case 0: {
                    ++i;
                    continue;
                }
                case 1: {
                    ++i;
                    continue;
                }
                case 2: {
                    ++i;
                    continue;
                }
                case 3: {
                    i += 4;
                    continue;
                }
                case 4: {
                    i += 4;
                    continue;
                }
                case 5: {
                    i += 3;
                    continue;
                }
                case 6: {
                    i += 2;
                    continue;
                }
                case 7: {
                    ++i;
                    continue;
                }
                default: {
                    throw new RuntimeException();
                }
            }
        }
        return -1;
    }
    
    private void queueTime() {
        final long time = TimeUtils.nanoTime();
        this.queue.add((int)(time >> 32));
        this.queue.add((int)time);
    }
    
    @Override
    public synchronized boolean keyDown(final int keycode) {
        this.queue.add(0);
        this.queueTime();
        this.queue.add(keycode);
        return false;
    }
    
    @Override
    public synchronized boolean keyUp(final int keycode) {
        this.queue.add(1);
        this.queueTime();
        this.queue.add(keycode);
        return false;
    }
    
    @Override
    public synchronized boolean keyTyped(final char character) {
        this.queue.add(2);
        this.queueTime();
        this.queue.add(character);
        return false;
    }
    
    @Override
    public synchronized boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        this.queue.add(3);
        this.queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }
    
    @Override
    public synchronized boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        this.queue.add(4);
        this.queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }
    
    @Override
    public synchronized boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        for (int i = this.next(5, 0); i >= 0; i = this.next(5, i + 6)) {
            if (this.queue.get(i + 5) == pointer) {
                this.queue.set(i, -1);
                this.queue.set(i + 3, 3);
            }
        }
        this.queue.add(5);
        this.queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        return false;
    }
    
    @Override
    public synchronized boolean mouseMoved(final int screenX, final int screenY) {
        for (int i = this.next(6, 0); i >= 0; i = this.next(6, i + 5)) {
            this.queue.set(i, -1);
            this.queue.set(i + 3, 2);
        }
        this.queue.add(6);
        this.queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        return false;
    }
    
    @Override
    public synchronized boolean scrolled(final int amount) {
        this.queue.add(7);
        this.queueTime();
        this.queue.add(amount);
        return false;
    }
    
    public long getCurrentEventTime() {
        return this.currentEventTime;
    }
}

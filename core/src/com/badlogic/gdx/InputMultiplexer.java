// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class InputMultiplexer implements InputProcessor
{
    private SnapshotArray<InputProcessor> processors;
    
    public InputMultiplexer() {
        this.processors = new SnapshotArray<InputProcessor>(4);
    }
    
    public InputMultiplexer(final InputProcessor... processors) {
        (this.processors = new SnapshotArray<InputProcessor>(4)).addAll(processors);
    }
    
    public void addProcessor(final int index, final InputProcessor processor) {
        if (processor == null) {
            throw new NullPointerException("processor cannot be null");
        }
        this.processors.insert(index, processor);
    }
    
    public void removeProcessor(final int index) {
        this.processors.removeIndex(index);
    }
    
    public void addProcessor(final InputProcessor processor) {
        if (processor == null) {
            throw new NullPointerException("processor cannot be null");
        }
        this.processors.add(processor);
    }
    
    public void removeProcessor(final InputProcessor processor) {
        this.processors.removeValue(processor, true);
    }
    
    public int size() {
        return this.processors.size;
    }
    
    public void clear() {
        this.processors.clear();
    }
    
    public void setProcessors(final InputProcessor... processors) {
        this.processors.clear();
        this.processors.addAll(processors);
    }
    
    public void setProcessors(final Array<InputProcessor> processors) {
        this.processors.clear();
        this.processors.addAll((Array<?>)processors);
    }
    
    public SnapshotArray<InputProcessor> getProcessors() {
        return this.processors;
    }
    
    @Override
    public boolean keyDown(final int keycode) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).keyDown(keycode)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).keyUp(keycode)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean keyTyped(final char character) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).keyTyped(character)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).touchDown(screenX, screenY, pointer, button)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).touchUp(screenX, screenY, pointer, button)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).touchDragged(screenX, screenY, pointer)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).mouseMoved(screenX, screenY)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
    
    @Override
    public boolean scrolled(final int amount) {
        final Object[] items = this.processors.begin();
        try {
            for (int i = 0, n = this.processors.size; i < n; ++i) {
                if (((InputProcessor)items[i]).scrolled(amount)) {
                    return true;
                }
            }
        }
        finally {
            this.processors.end();
        }
        this.processors.end();
        return false;
    }
}

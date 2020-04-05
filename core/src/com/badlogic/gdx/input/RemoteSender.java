// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import java.net.Socket;
import java.io.DataOutputStream;
import com.badlogic.gdx.InputProcessor;

public class RemoteSender implements InputProcessor
{
    private DataOutputStream out;
    private boolean connected;
    public static final int KEY_DOWN = 0;
    public static final int KEY_UP = 1;
    public static final int KEY_TYPED = 2;
    public static final int TOUCH_DOWN = 3;
    public static final int TOUCH_UP = 4;
    public static final int TOUCH_DRAGGED = 5;
    public static final int ACCEL = 6;
    public static final int COMPASS = 7;
    public static final int SIZE = 8;
    public static final int GYRO = 9;
    
    public RemoteSender(final String ip, final int port) {
        this.connected = false;
        try {
            final Socket socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(3000);
            (this.out = new DataOutputStream(socket.getOutputStream())).writeBoolean(Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen));
            this.connected = true;
            Gdx.input.setInputProcessor(this);
        }
        catch (Exception e) {
            Gdx.app.log("RemoteSender", "couldn't connect to " + ip + ":" + port);
        }
    }
    
    public void sendUpdate() {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return;
            }
        }
        try {
            this.out.writeInt(6);
            this.out.writeFloat(Gdx.input.getAccelerometerX());
            this.out.writeFloat(Gdx.input.getAccelerometerY());
            this.out.writeFloat(Gdx.input.getAccelerometerZ());
            this.out.writeInt(7);
            this.out.writeFloat(Gdx.input.getAzimuth());
            this.out.writeFloat(Gdx.input.getPitch());
            this.out.writeFloat(Gdx.input.getRoll());
            this.out.writeInt(8);
            this.out.writeFloat((float)Gdx.graphics.getWidth());
            this.out.writeFloat((float)Gdx.graphics.getHeight());
            this.out.writeInt(9);
            this.out.writeFloat(Gdx.input.getGyroscopeX());
            this.out.writeFloat(Gdx.input.getGyroscopeY());
            this.out.writeFloat(Gdx.input.getGyroscopeZ());
        }
        catch (Throwable t) {
            this.out = null;
            this.connected = false;
        }
    }
    
    @Override
    public boolean keyDown(final int keycode) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(0);
            this.out.writeInt(keycode);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(1);
            this.out.writeInt(keycode);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyTyped(final char character) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(2);
            this.out.writeChar(character);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean touchDown(final int x, final int y, final int pointer, final int button) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(3);
            this.out.writeInt(x);
            this.out.writeInt(y);
            this.out.writeInt(pointer);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean touchUp(final int x, final int y, final int pointer, final int button) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(4);
            this.out.writeInt(x);
            this.out.writeInt(y);
            this.out.writeInt(pointer);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean touchDragged(final int x, final int y, final int pointer) {
        synchronized (this) {
            if (!this.connected) {
                // monitorexit(this)
                return false;
            }
        }
        try {
            this.out.writeInt(5);
            this.out.writeInt(x);
            this.out.writeInt(y);
            this.out.writeInt(pointer);
        }
        catch (Throwable t) {
            synchronized (this) {
                this.connected = false;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseMoved(final int x, final int y) {
        return false;
    }
    
    @Override
    public boolean scrolled(final int amount) {
        return false;
    }
    
    public boolean isConnected() {
        synchronized (this) {
            return this.connected;
        }
    }
}

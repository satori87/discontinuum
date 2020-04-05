// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.input;

import java.net.Socket;
import java.io.IOException;
import com.badlogic.gdx.Gdx;
import java.io.DataInputStream;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.net.InetAddress;
import com.badlogic.gdx.InputProcessor;
import java.net.ServerSocket;
import com.badlogic.gdx.Input;

public class RemoteInput implements Runnable, Input
{
    public static int DEFAULT_PORT;
    private ServerSocket serverSocket;
    private float[] accel;
    private float[] gyrate;
    private float[] compass;
    private boolean multiTouch;
    private float remoteWidth;
    private float remoteHeight;
    private boolean connected;
    private RemoteInputListener listener;
    int keyCount;
    boolean[] keys;
    boolean keyJustPressed;
    boolean[] justPressedKeys;
    int[] deltaX;
    int[] deltaY;
    int[] touchX;
    int[] touchY;
    boolean[] isTouched;
    boolean justTouched;
    InputProcessor processor;
    private final int port;
    public final String[] ips;
    
    static {
        RemoteInput.DEFAULT_PORT = 8190;
    }
    
    public RemoteInput() {
        this(RemoteInput.DEFAULT_PORT);
    }
    
    public RemoteInput(final RemoteInputListener listener) {
        this(RemoteInput.DEFAULT_PORT, listener);
    }
    
    public RemoteInput(final int port) {
        this(port, null);
    }
    
    public RemoteInput(final int port, final RemoteInputListener listener) {
        this.accel = new float[3];
        this.gyrate = new float[3];
        this.compass = new float[3];
        this.multiTouch = false;
        this.remoteWidth = 0.0f;
        this.remoteHeight = 0.0f;
        this.connected = false;
        this.keyCount = 0;
        this.keys = new boolean[256];
        this.keyJustPressed = false;
        this.justPressedKeys = new boolean[256];
        this.deltaX = new int[20];
        this.deltaY = new int[20];
        this.touchX = new int[20];
        this.touchY = new int[20];
        this.isTouched = new boolean[20];
        this.justTouched = false;
        this.processor = null;
        this.listener = listener;
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
            final Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            final InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            this.ips = new String[allByName.length];
            for (int i = 0; i < allByName.length; ++i) {
                this.ips[i] = allByName[i].getHostAddress();
            }
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Couldn't open listening socket at port '" + port + "'", e);
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                this.connected = false;
                if (this.listener != null) {
                    this.listener.onDisconnected();
                }
                System.out.println("listening, port " + this.port);
                Socket socket = null;
                socket = this.serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(3000);
                this.connected = true;
                if (this.listener != null) {
                    this.listener.onConnected();
                }
                final DataInputStream in = new DataInputStream(socket.getInputStream());
                this.multiTouch = in.readBoolean();
                while (true) {
                    final int event = in.readInt();
                    KeyEvent keyEvent = null;
                    TouchEvent touchEvent = null;
                    switch (event) {
                        case 6: {
                            this.accel[0] = in.readFloat();
                            this.accel[1] = in.readFloat();
                            this.accel[2] = in.readFloat();
                            break;
                        }
                        case 7: {
                            this.compass[0] = in.readFloat();
                            this.compass[1] = in.readFloat();
                            this.compass[2] = in.readFloat();
                            break;
                        }
                        case 8: {
                            this.remoteWidth = in.readFloat();
                            this.remoteHeight = in.readFloat();
                            break;
                        }
                        case 9: {
                            this.gyrate[0] = in.readFloat();
                            this.gyrate[1] = in.readFloat();
                            this.gyrate[2] = in.readFloat();
                            break;
                        }
                        case 0: {
                            keyEvent = new KeyEvent();
                            keyEvent.keyCode = in.readInt();
                            keyEvent.type = 0;
                            break;
                        }
                        case 1: {
                            keyEvent = new KeyEvent();
                            keyEvent.keyCode = in.readInt();
                            keyEvent.type = 1;
                            break;
                        }
                        case 2: {
                            keyEvent = new KeyEvent();
                            keyEvent.keyChar = in.readChar();
                            keyEvent.type = 2;
                            break;
                        }
                        case 3: {
                            touchEvent = new TouchEvent();
                            touchEvent.x = (int)(in.readInt() / this.remoteWidth * Gdx.graphics.getWidth());
                            touchEvent.y = (int)(in.readInt() / this.remoteHeight * Gdx.graphics.getHeight());
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 0;
                            break;
                        }
                        case 4: {
                            touchEvent = new TouchEvent();
                            touchEvent.x = (int)(in.readInt() / this.remoteWidth * Gdx.graphics.getWidth());
                            touchEvent.y = (int)(in.readInt() / this.remoteHeight * Gdx.graphics.getHeight());
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 1;
                            break;
                        }
                        case 5: {
                            touchEvent = new TouchEvent();
                            touchEvent.x = (int)(in.readInt() / this.remoteWidth * Gdx.graphics.getWidth());
                            touchEvent.y = (int)(in.readInt() / this.remoteHeight * Gdx.graphics.getHeight());
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 2;
                            break;
                        }
                    }
                    Gdx.app.postRunnable(new EventTrigger(touchEvent, keyEvent));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    @Override
    public float getAccelerometerX() {
        return this.accel[0];
    }
    
    @Override
    public float getAccelerometerY() {
        return this.accel[1];
    }
    
    @Override
    public float getAccelerometerZ() {
        return this.accel[2];
    }
    
    @Override
    public float getGyroscopeX() {
        return this.gyrate[0];
    }
    
    @Override
    public float getGyroscopeY() {
        return this.gyrate[1];
    }
    
    @Override
    public float getGyroscopeZ() {
        return this.gyrate[2];
    }
    
    @Override
    public int getX() {
        return this.touchX[0];
    }
    
    @Override
    public int getX(final int pointer) {
        return this.touchX[pointer];
    }
    
    @Override
    public int getY() {
        return this.touchY[0];
    }
    
    @Override
    public int getY(final int pointer) {
        return this.touchY[pointer];
    }
    
    @Override
    public boolean isTouched() {
        return this.isTouched[0];
    }
    
    @Override
    public boolean justTouched() {
        return this.justTouched;
    }
    
    @Override
    public boolean isTouched(final int pointer) {
        return this.isTouched[pointer];
    }
    
    @Override
    public float getPressure() {
        return this.getPressure(0);
    }
    
    @Override
    public float getPressure(final int pointer) {
        return (float)(this.isTouched(pointer) ? 1 : 0);
    }
    
    @Override
    public boolean isButtonPressed(final int button) {
        if (button != 0) {
            return false;
        }
        for (int i = 0; i < this.isTouched.length; ++i) {
            if (this.isTouched[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isKeyPressed(final int key) {
        if (key == -1) {
            return this.keyCount > 0;
        }
        return key >= 0 && key <= 255 && this.keys[key];
    }
    
    @Override
    public boolean isKeyJustPressed(final int key) {
        if (key == -1) {
            return this.keyJustPressed;
        }
        return key >= 0 && key <= 255 && this.justPressedKeys[key];
    }
    
    @Override
    public void getTextInput(final TextInputListener listener, final String title, final String text, final String hint) {
        Gdx.app.getInput().getTextInput(listener, title, text, hint);
    }
    
    @Override
    public void setOnscreenKeyboardVisible(final boolean visible) {
    }
    
    @Override
    public void vibrate(final int milliseconds) {
    }
    
    @Override
    public void vibrate(final long[] pattern, final int repeat) {
    }
    
    @Override
    public void cancelVibrate() {
    }
    
    @Override
    public float getAzimuth() {
        return this.compass[0];
    }
    
    @Override
    public float getPitch() {
        return this.compass[1];
    }
    
    @Override
    public float getRoll() {
        return this.compass[2];
    }
    
    @Override
    public void setCatchBackKey(final boolean catchBack) {
    }
    
    @Override
    public boolean isCatchBackKey() {
        return false;
    }
    
    @Override
    public void setCatchMenuKey(final boolean catchMenu) {
    }
    
    @Override
    public boolean isCatchMenuKey() {
        return false;
    }
    
    @Override
    public void setInputProcessor(final InputProcessor processor) {
        this.processor = processor;
    }
    
    @Override
    public InputProcessor getInputProcessor() {
        return this.processor;
    }
    
    public String[] getIPs() {
        return this.ips;
    }
    
    @Override
    public boolean isPeripheralAvailable(final Peripheral peripheral) {
        return peripheral == Peripheral.Accelerometer || peripheral == Peripheral.Compass || (peripheral == Peripheral.MultitouchScreen && this.multiTouch);
    }
    
    @Override
    public int getRotation() {
        return 0;
    }
    
    @Override
    public Orientation getNativeOrientation() {
        return Orientation.Landscape;
    }
    
    @Override
    public void setCursorCatched(final boolean catched) {
    }
    
    @Override
    public boolean isCursorCatched() {
        return false;
    }
    
    @Override
    public int getDeltaX() {
        return this.deltaX[0];
    }
    
    @Override
    public int getDeltaX(final int pointer) {
        return this.deltaX[pointer];
    }
    
    @Override
    public int getDeltaY() {
        return this.deltaY[0];
    }
    
    @Override
    public int getDeltaY(final int pointer) {
        return this.deltaY[pointer];
    }
    
    @Override
    public void setCursorPosition(final int x, final int y) {
    }
    
    @Override
    public long getCurrentEventTime() {
        return 0L;
    }
    
    @Override
    public void getRotationMatrix(final float[] matrix) {
    }
    
    class KeyEvent
    {
        static final int KEY_DOWN = 0;
        static final int KEY_UP = 1;
        static final int KEY_TYPED = 2;
        long timeStamp;
        int type;
        int keyCode;
        char keyChar;
    }
    
    class TouchEvent
    {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_UP = 1;
        static final int TOUCH_DRAGGED = 2;
        long timeStamp;
        int type;
        int x;
        int y;
        int pointer;
    }
    
    class EventTrigger implements Runnable
    {
        TouchEvent touchEvent;
        KeyEvent keyEvent;
        
        public EventTrigger(final TouchEvent touchEvent, final KeyEvent keyEvent) {
            this.touchEvent = touchEvent;
            this.keyEvent = keyEvent;
        }
        
        @Override
        public void run() {
            RemoteInput.this.justTouched = false;
            if (RemoteInput.this.keyJustPressed) {
                RemoteInput.this.keyJustPressed = false;
                for (int i = 0; i < RemoteInput.this.justPressedKeys.length; ++i) {
                    RemoteInput.this.justPressedKeys[i] = false;
                }
            }
            if (RemoteInput.this.processor != null) {
                if (this.touchEvent != null) {
                    switch (this.touchEvent.type) {
                        case 0: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                            RemoteInput.this.processor.touchDown(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                            RemoteInput.this.justTouched = true;
                            break;
                        }
                        case 1: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                            RemoteInput.this.processor.touchUp(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                            break;
                        }
                        case 2: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = this.touchEvent.x - RemoteInput.this.touchX[this.touchEvent.pointer];
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = this.touchEvent.y - RemoteInput.this.touchY[this.touchEvent.pointer];
                            RemoteInput.this.processor.touchDragged(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer);
                            break;
                        }
                    }
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
                }
                if (this.keyEvent != null) {
                    switch (this.keyEvent.type) {
                        case 0: {
                            RemoteInput.this.processor.keyDown(this.keyEvent.keyCode);
                            if (!RemoteInput.this.keys[this.keyEvent.keyCode]) {
                                final RemoteInput this$0 = RemoteInput.this;
                                ++this$0.keyCount;
                                RemoteInput.this.keys[this.keyEvent.keyCode] = true;
                            }
                            RemoteInput.this.keyJustPressed = true;
                            RemoteInput.this.justPressedKeys[this.keyEvent.keyCode] = true;
                            break;
                        }
                        case 1: {
                            RemoteInput.this.processor.keyUp(this.keyEvent.keyCode);
                            if (RemoteInput.this.keys[this.keyEvent.keyCode]) {
                                final RemoteInput this$2 = RemoteInput.this;
                                --this$2.keyCount;
                                RemoteInput.this.keys[this.keyEvent.keyCode] = false;
                                break;
                            }
                            break;
                        }
                        case 2: {
                            RemoteInput.this.processor.keyTyped(this.keyEvent.keyChar);
                            break;
                        }
                    }
                }
            }
            else {
                if (this.touchEvent != null) {
                    switch (this.touchEvent.type) {
                        case 0: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                            RemoteInput.this.justTouched = true;
                            break;
                        }
                        case 1: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                            break;
                        }
                        case 2: {
                            RemoteInput.this.deltaX[this.touchEvent.pointer] = this.touchEvent.x - RemoteInput.this.touchX[this.touchEvent.pointer];
                            RemoteInput.this.deltaY[this.touchEvent.pointer] = this.touchEvent.y - RemoteInput.this.touchY[this.touchEvent.pointer];
                            break;
                        }
                    }
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
                }
                if (this.keyEvent != null) {
                    if (this.keyEvent.type == 0) {
                        if (!RemoteInput.this.keys[this.keyEvent.keyCode]) {
                            final RemoteInput this$3 = RemoteInput.this;
                            ++this$3.keyCount;
                            RemoteInput.this.keys[this.keyEvent.keyCode] = true;
                        }
                        RemoteInput.this.keyJustPressed = true;
                        RemoteInput.this.justPressedKeys[this.keyEvent.keyCode] = true;
                    }
                    if (this.keyEvent.type == 1 && RemoteInput.this.keys[this.keyEvent.keyCode]) {
                        final RemoteInput this$4 = RemoteInput.this;
                        --this$4.keyCount;
                        RemoteInput.this.keys[this.keyEvent.keyCode] = false;
                    }
                }
            }
        }
    }
    
    public interface RemoteInputListener
    {
        void onConnected();
        
        void onDisconnected();
    }
}

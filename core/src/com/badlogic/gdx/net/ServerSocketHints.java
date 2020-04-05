// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

public class ServerSocketHints
{
    public int backlog;
    public int performancePrefConnectionTime;
    public int performancePrefLatency;
    public int performancePrefBandwidth;
    public boolean reuseAddress;
    public int acceptTimeout;
    public int receiveBufferSize;
    
    public ServerSocketHints() {
        this.backlog = 16;
        this.performancePrefConnectionTime = 0;
        this.performancePrefLatency = 1;
        this.performancePrefBandwidth = 0;
        this.reuseAddress = true;
        this.acceptTimeout = 5000;
        this.receiveBufferSize = 4096;
    }
}

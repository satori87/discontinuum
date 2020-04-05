// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

public class SocketHints
{
    public int connectTimeout;
    public int performancePrefConnectionTime;
    public int performancePrefLatency;
    public int performancePrefBandwidth;
    public int trafficClass;
    public boolean keepAlive;
    public boolean tcpNoDelay;
    public int sendBufferSize;
    public int receiveBufferSize;
    public boolean linger;
    public int lingerDuration;
    public int socketTimeout;
    
    public SocketHints() {
        this.connectTimeout = 5000;
        this.performancePrefConnectionTime = 0;
        this.performancePrefLatency = 1;
        this.performancePrefBandwidth = 0;
        this.trafficClass = 20;
        this.keepAlive = true;
        this.tcpNoDelay = true;
        this.sendBufferSize = 4096;
        this.receiveBufferSize = 4096;
        this.linger = false;
        this.lingerDuration = 0;
        this.socketTimeout = 0;
    }
}

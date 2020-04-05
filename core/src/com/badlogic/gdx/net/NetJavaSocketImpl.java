// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import java.io.OutputStream;
import java.io.InputStream;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.badlogic.gdx.Net;

public class NetJavaSocketImpl implements Socket
{
    private java.net.Socket socket;
    
    public NetJavaSocketImpl(final Net.Protocol protocol, final String host, final int port, final SocketHints hints) {
        try {
            this.socket = new java.net.Socket();
            this.applyHints(hints);
            final InetSocketAddress address = new InetSocketAddress(host, port);
            if (hints != null) {
                this.socket.connect(address, hints.connectTimeout);
            }
            else {
                this.socket.connect(address);
            }
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error making a socket connection to " + host + ":" + port, e);
        }
    }
    
    public NetJavaSocketImpl(final java.net.Socket socket, final SocketHints hints) {
        this.socket = socket;
        this.applyHints(hints);
    }
    
    private void applyHints(final SocketHints hints) {
        if (hints != null) {
            try {
                this.socket.setPerformancePreferences(hints.performancePrefConnectionTime, hints.performancePrefLatency, hints.performancePrefBandwidth);
                this.socket.setTrafficClass(hints.trafficClass);
                this.socket.setTcpNoDelay(hints.tcpNoDelay);
                this.socket.setKeepAlive(hints.keepAlive);
                this.socket.setSendBufferSize(hints.sendBufferSize);
                this.socket.setReceiveBufferSize(hints.receiveBufferSize);
                this.socket.setSoLinger(hints.linger, hints.lingerDuration);
                this.socket.setSoTimeout(hints.socketTimeout);
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Error setting socket hints.", e);
            }
        }
    }
    
    @Override
    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            return this.socket.getInputStream();
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error getting input stream from socket.", e);
        }
    }
    
    @Override
    public OutputStream getOutputStream() {
        try {
            return this.socket.getOutputStream();
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error getting output stream from socket.", e);
        }
    }
    
    @Override
    public String getRemoteAddress() {
        return this.socket.getRemoteSocketAddress().toString();
    }
    
    @Override
    public void dispose() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Error closing socket.", e);
            }
        }
    }
}

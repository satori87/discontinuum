// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.badlogic.gdx.Net;

public class NetJavaServerSocketImpl implements ServerSocket
{
    private Net.Protocol protocol;
    private java.net.ServerSocket server;
    
    public NetJavaServerSocketImpl(final Net.Protocol protocol, final int port, final ServerSocketHints hints) {
        this(protocol, null, port, hints);
    }
    
    public NetJavaServerSocketImpl(final Net.Protocol protocol, final String hostname, final int port, final ServerSocketHints hints) {
        this.protocol = protocol;
        try {
            this.server = new java.net.ServerSocket();
            if (hints != null) {
                this.server.setPerformancePreferences(hints.performancePrefConnectionTime, hints.performancePrefLatency, hints.performancePrefBandwidth);
                this.server.setReuseAddress(hints.reuseAddress);
                this.server.setSoTimeout(hints.acceptTimeout);
                this.server.setReceiveBufferSize(hints.receiveBufferSize);
            }
            InetSocketAddress address;
            if (hostname != null) {
                address = new InetSocketAddress(hostname, port);
            }
            else {
                address = new InetSocketAddress(port);
            }
            if (hints != null) {
                this.server.bind(address, hints.backlog);
            }
            else {
                this.server.bind(address);
            }
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Cannot create a server socket at port " + port + ".", e);
        }
    }
    
    @Override
    public Net.Protocol getProtocol() {
        return this.protocol;
    }
    
    @Override
    public Socket accept(final SocketHints hints) {
        try {
            return new NetJavaSocketImpl(this.server.accept(), hints);
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Error accepting socket.", e);
        }
    }
    
    @Override
    public void dispose() {
        if (this.server != null) {
            try {
                this.server.close();
                this.server = null;
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Error closing server.", e);
            }
        }
    }
}

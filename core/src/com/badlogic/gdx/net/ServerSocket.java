// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Disposable;

public interface ServerSocket extends Disposable
{
    Net.Protocol getProtocol();
    
    Socket accept(final SocketHints p0);
}

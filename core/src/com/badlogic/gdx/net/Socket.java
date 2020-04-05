// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import java.io.OutputStream;
import java.io.InputStream;
import com.badlogic.gdx.utils.Disposable;

public interface Socket extends Disposable
{
    boolean isConnected();
    
    InputStream getInputStream();
    
    OutputStream getOutputStream();
    
    String getRemoteAddress();
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.InputStream;

public interface BaseJsonReader
{
    JsonValue parse(final InputStream p0);
    
    JsonValue parse(final FileHandle p0);
}

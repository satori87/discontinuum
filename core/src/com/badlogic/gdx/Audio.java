// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.AudioDevice;

public interface Audio
{
    AudioDevice newAudioDevice(final int p0, final boolean p1);
    
    AudioRecorder newAudioRecorder(final int p0, final boolean p1);
    
    Sound newSound(final FileHandle p0);
    
    Music newMusic(final FileHandle p0);
}

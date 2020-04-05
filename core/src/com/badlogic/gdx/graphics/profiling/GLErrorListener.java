// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Gdx;

public interface GLErrorListener
{
    public static final GLErrorListener LOGGING_LISTENER = new GLErrorListener() {
        @Override
        public void onError(final int error) {
            String place = null;
            try {
                final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                int i = 0;
                while (i < stack.length) {
                    if ("check".equals(stack[i].getMethodName())) {
                        if (i + 1 < stack.length) {
                            final StackTraceElement glMethod = stack[i + 1];
                            place = glMethod.getMethodName();
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            catch (Exception ex) {}
            if (place != null) {
                Gdx.app.error("GLProfiler", "Error " + GLInterceptor.resolveErrorNumber(error) + " from " + place);
            }
            else {
                Gdx.app.error("GLProfiler", "Error " + GLInterceptor.resolveErrorNumber(error) + " at: ", new Exception());
            }
        }
    };
    public static final GLErrorListener THROWING_LISTENER = new GLErrorListener() {
        @Override
        public void onError(final int error) {
            throw new GdxRuntimeException("GLProfiler: Got GL error " + GLInterceptor.resolveErrorNumber(error));
        }
    };
    
    void onError(final int p0);
}

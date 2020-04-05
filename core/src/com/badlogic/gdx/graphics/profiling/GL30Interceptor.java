// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.profiling;

import java.nio.LongBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.GL30;

public class GL30Interceptor extends GLInterceptor implements GL30
{
    protected final GL30 gl30;
    
    protected GL30Interceptor(final GLProfiler glProfiler, final GL30 gl30) {
        super(glProfiler);
        this.gl30 = gl30;
    }
    
    private void check() {
        for (int error = this.gl30.glGetError(); error != 0; error = this.gl30.glGetError()) {
            this.glProfiler.getListener().onError(error);
        }
    }
    
    @Override
    public void glActiveTexture(final int texture) {
        ++this.calls;
        this.gl30.glActiveTexture(texture);
        this.check();
    }
    
    @Override
    public void glBindTexture(final int target, final int texture) {
        ++this.textureBindings;
        ++this.calls;
        this.gl30.glBindTexture(target, texture);
        this.check();
    }
    
    @Override
    public void glBlendFunc(final int sfactor, final int dfactor) {
        ++this.calls;
        this.gl30.glBlendFunc(sfactor, dfactor);
        this.check();
    }
    
    @Override
    public void glClear(final int mask) {
        ++this.calls;
        this.gl30.glClear(mask);
        this.check();
    }
    
    @Override
    public void glClearColor(final float red, final float green, final float blue, final float alpha) {
        ++this.calls;
        this.gl30.glClearColor(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glClearDepthf(final float depth) {
        ++this.calls;
        this.gl30.glClearDepthf(depth);
        this.check();
    }
    
    @Override
    public void glClearStencil(final int s) {
        ++this.calls;
        this.gl30.glClearStencil(s);
        this.check();
    }
    
    @Override
    public void glColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
        ++this.calls;
        this.gl30.glColorMask(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glCompressedTexImage2D(final int target, final int level, final int internalformat, final int width, final int height, final int border, final int imageSize, final Buffer data) {
        ++this.calls;
        this.gl30.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        this.check();
    }
    
    @Override
    public void glCompressedTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int imageSize, final Buffer data) {
        ++this.calls;
        this.gl30.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        this.check();
    }
    
    @Override
    public void glCopyTexImage2D(final int target, final int level, final int internalformat, final int x, final int y, final int width, final int height, final int border) {
        ++this.calls;
        this.gl30.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        this.check();
    }
    
    @Override
    public void glCopyTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl30.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        this.check();
    }
    
    @Override
    public void glCullFace(final int mode) {
        ++this.calls;
        this.gl30.glCullFace(mode);
        this.check();
    }
    
    @Override
    public void glDeleteTextures(final int n, final IntBuffer textures) {
        ++this.calls;
        this.gl30.glDeleteTextures(n, textures);
        this.check();
    }
    
    @Override
    public void glDeleteTexture(final int texture) {
        ++this.calls;
        this.gl30.glDeleteTexture(texture);
        this.check();
    }
    
    @Override
    public void glDepthFunc(final int func) {
        ++this.calls;
        this.gl30.glDepthFunc(func);
        this.check();
    }
    
    @Override
    public void glDepthMask(final boolean flag) {
        ++this.calls;
        this.gl30.glDepthMask(flag);
        this.check();
    }
    
    @Override
    public void glDepthRangef(final float zNear, final float zFar) {
        ++this.calls;
        this.gl30.glDepthRangef(zNear, zFar);
        this.check();
    }
    
    @Override
    public void glDisable(final int cap) {
        ++this.calls;
        this.gl30.glDisable(cap);
        this.check();
    }
    
    @Override
    public void glDrawArrays(final int mode, final int first, final int count) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawArrays(mode, first, count);
        this.check();
    }
    
    @Override
    public void glDrawElements(final int mode, final int count, final int type, final Buffer indices) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawElements(mode, count, type, indices);
        this.check();
    }
    
    @Override
    public void glEnable(final int cap) {
        ++this.calls;
        this.gl30.glEnable(cap);
        this.check();
    }
    
    @Override
    public void glFinish() {
        ++this.calls;
        this.gl30.glFinish();
        this.check();
    }
    
    @Override
    public void glFlush() {
        ++this.calls;
        this.gl30.glFlush();
        this.check();
    }
    
    @Override
    public void glFrontFace(final int mode) {
        ++this.calls;
        this.gl30.glFrontFace(mode);
        this.check();
    }
    
    @Override
    public void glGenTextures(final int n, final IntBuffer textures) {
        ++this.calls;
        this.gl30.glGenTextures(n, textures);
        this.check();
    }
    
    @Override
    public int glGenTexture() {
        ++this.calls;
        final int result = this.gl30.glGenTexture();
        this.check();
        return result;
    }
    
    @Override
    public int glGetError() {
        ++this.calls;
        return this.gl30.glGetError();
    }
    
    @Override
    public void glGetIntegerv(final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetIntegerv(pname, params);
        this.check();
    }
    
    @Override
    public String glGetString(final int name) {
        ++this.calls;
        final String result = this.gl30.glGetString(name);
        this.check();
        return result;
    }
    
    @Override
    public void glHint(final int target, final int mode) {
        ++this.calls;
        this.gl30.glHint(target, mode);
        this.check();
    }
    
    @Override
    public void glLineWidth(final float width) {
        ++this.calls;
        this.gl30.glLineWidth(width);
        this.check();
    }
    
    @Override
    public void glPixelStorei(final int pname, final int param) {
        ++this.calls;
        this.gl30.glPixelStorei(pname, param);
        this.check();
    }
    
    @Override
    public void glPolygonOffset(final float factor, final float units) {
        ++this.calls;
        this.gl30.glPolygonOffset(factor, units);
        this.check();
    }
    
    @Override
    public void glReadPixels(final int x, final int y, final int width, final int height, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl30.glReadPixels(x, y, width, height, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glScissor(final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl30.glScissor(x, y, width, height);
        this.check();
    }
    
    @Override
    public void glStencilFunc(final int func, final int ref, final int mask) {
        ++this.calls;
        this.gl30.glStencilFunc(func, ref, mask);
        this.check();
    }
    
    @Override
    public void glStencilMask(final int mask) {
        ++this.calls;
        this.gl30.glStencilMask(mask);
        this.check();
    }
    
    @Override
    public void glStencilOp(final int fail, final int zfail, final int zpass) {
        ++this.calls;
        this.gl30.glStencilOp(fail, zfail, zpass);
        this.check();
    }
    
    @Override
    public void glTexImage2D(final int target, final int level, final int internalformat, final int width, final int height, final int border, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl30.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glTexParameterf(final int target, final int pname, final float param) {
        ++this.calls;
        this.gl30.glTexParameterf(target, pname, param);
        this.check();
    }
    
    @Override
    public void glTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl30.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glViewport(final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl30.glViewport(x, y, width, height);
        this.check();
    }
    
    @Override
    public void glAttachShader(final int program, final int shader) {
        ++this.calls;
        this.gl30.glAttachShader(program, shader);
        this.check();
    }
    
    @Override
    public void glBindAttribLocation(final int program, final int index, final String name) {
        ++this.calls;
        this.gl30.glBindAttribLocation(program, index, name);
        this.check();
    }
    
    @Override
    public void glBindBuffer(final int target, final int buffer) {
        ++this.calls;
        this.gl30.glBindBuffer(target, buffer);
        this.check();
    }
    
    @Override
    public void glBindFramebuffer(final int target, final int framebuffer) {
        ++this.calls;
        this.gl30.glBindFramebuffer(target, framebuffer);
        this.check();
    }
    
    @Override
    public void glBindRenderbuffer(final int target, final int renderbuffer) {
        ++this.calls;
        this.gl30.glBindRenderbuffer(target, renderbuffer);
        this.check();
    }
    
    @Override
    public void glBlendColor(final float red, final float green, final float blue, final float alpha) {
        ++this.calls;
        this.gl30.glBlendColor(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glBlendEquation(final int mode) {
        ++this.calls;
        this.gl30.glBlendEquation(mode);
        this.check();
    }
    
    @Override
    public void glBlendEquationSeparate(final int modeRGB, final int modeAlpha) {
        ++this.calls;
        this.gl30.glBlendEquationSeparate(modeRGB, modeAlpha);
        this.check();
    }
    
    @Override
    public void glBlendFuncSeparate(final int srcRGB, final int dstRGB, final int srcAlpha, final int dstAlpha) {
        ++this.calls;
        this.gl30.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        this.check();
    }
    
    @Override
    public void glBufferData(final int target, final int size, final Buffer data, final int usage) {
        ++this.calls;
        this.gl30.glBufferData(target, size, data, usage);
        this.check();
    }
    
    @Override
    public void glBufferSubData(final int target, final int offset, final int size, final Buffer data) {
        ++this.calls;
        this.gl30.glBufferSubData(target, offset, size, data);
        this.check();
    }
    
    @Override
    public int glCheckFramebufferStatus(final int target) {
        ++this.calls;
        final int result = this.gl30.glCheckFramebufferStatus(target);
        this.check();
        return result;
    }
    
    @Override
    public void glCompileShader(final int shader) {
        ++this.calls;
        this.gl30.glCompileShader(shader);
        this.check();
    }
    
    @Override
    public int glCreateProgram() {
        ++this.calls;
        final int result = this.gl30.glCreateProgram();
        this.check();
        return result;
    }
    
    @Override
    public int glCreateShader(final int type) {
        ++this.calls;
        final int result = this.gl30.glCreateShader(type);
        this.check();
        return result;
    }
    
    @Override
    public void glDeleteBuffer(final int buffer) {
        ++this.calls;
        this.gl30.glDeleteBuffer(buffer);
        this.check();
    }
    
    @Override
    public void glDeleteBuffers(final int n, final IntBuffer buffers) {
        ++this.calls;
        this.gl30.glDeleteBuffers(n, buffers);
        this.check();
    }
    
    @Override
    public void glDeleteFramebuffer(final int framebuffer) {
        ++this.calls;
        this.gl30.glDeleteFramebuffer(framebuffer);
        this.check();
    }
    
    @Override
    public void glDeleteFramebuffers(final int n, final IntBuffer framebuffers) {
        ++this.calls;
        this.gl30.glDeleteFramebuffers(n, framebuffers);
        this.check();
    }
    
    @Override
    public void glDeleteProgram(final int program) {
        ++this.calls;
        this.gl30.glDeleteProgram(program);
        this.check();
    }
    
    @Override
    public void glDeleteRenderbuffer(final int renderbuffer) {
        ++this.calls;
        this.gl30.glDeleteRenderbuffer(renderbuffer);
        this.check();
    }
    
    @Override
    public void glDeleteRenderbuffers(final int n, final IntBuffer renderbuffers) {
        ++this.calls;
        this.gl30.glDeleteRenderbuffers(n, renderbuffers);
        this.check();
    }
    
    @Override
    public void glDeleteShader(final int shader) {
        ++this.calls;
        this.gl30.glDeleteShader(shader);
        this.check();
    }
    
    @Override
    public void glDetachShader(final int program, final int shader) {
        ++this.calls;
        this.gl30.glDetachShader(program, shader);
        this.check();
    }
    
    @Override
    public void glDisableVertexAttribArray(final int index) {
        ++this.calls;
        this.gl30.glDisableVertexAttribArray(index);
        this.check();
    }
    
    @Override
    public void glDrawElements(final int mode, final int count, final int type, final int indices) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawElements(mode, count, type, indices);
        this.check();
    }
    
    @Override
    public void glEnableVertexAttribArray(final int index) {
        ++this.calls;
        this.gl30.glEnableVertexAttribArray(index);
        this.check();
    }
    
    @Override
    public void glFramebufferRenderbuffer(final int target, final int attachment, final int renderbuffertarget, final int renderbuffer) {
        ++this.calls;
        this.gl30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        this.check();
    }
    
    @Override
    public void glFramebufferTexture2D(final int target, final int attachment, final int textarget, final int texture, final int level) {
        ++this.calls;
        this.gl30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        this.check();
    }
    
    @Override
    public int glGenBuffer() {
        ++this.calls;
        final int result = this.gl30.glGenBuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenBuffers(final int n, final IntBuffer buffers) {
        ++this.calls;
        this.gl30.glGenBuffers(n, buffers);
        this.check();
    }
    
    @Override
    public void glGenerateMipmap(final int target) {
        ++this.calls;
        this.gl30.glGenerateMipmap(target);
        this.check();
    }
    
    @Override
    public int glGenFramebuffer() {
        ++this.calls;
        final int result = this.gl30.glGenFramebuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenFramebuffers(final int n, final IntBuffer framebuffers) {
        ++this.calls;
        this.gl30.glGenFramebuffers(n, framebuffers);
        this.check();
    }
    
    @Override
    public int glGenRenderbuffer() {
        ++this.calls;
        final int result = this.gl30.glGenRenderbuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenRenderbuffers(final int n, final IntBuffer renderbuffers) {
        ++this.calls;
        this.gl30.glGenRenderbuffers(n, renderbuffers);
        this.check();
    }
    
    @Override
    public String glGetActiveAttrib(final int program, final int index, final IntBuffer size, final Buffer type) {
        ++this.calls;
        final String result = this.gl30.glGetActiveAttrib(program, index, size, type);
        this.check();
        return result;
    }
    
    @Override
    public String glGetActiveUniform(final int program, final int index, final IntBuffer size, final Buffer type) {
        ++this.calls;
        final String result = this.gl30.glGetActiveUniform(program, index, size, type);
        this.check();
        return result;
    }
    
    @Override
    public void glGetAttachedShaders(final int program, final int maxcount, final Buffer count, final IntBuffer shaders) {
        ++this.calls;
        this.gl30.glGetAttachedShaders(program, maxcount, count, shaders);
        this.check();
    }
    
    @Override
    public int glGetAttribLocation(final int program, final String name) {
        ++this.calls;
        final int result = this.gl30.glGetAttribLocation(program, name);
        this.check();
        return result;
    }
    
    @Override
    public void glGetBooleanv(final int pname, final Buffer params) {
        ++this.calls;
        this.gl30.glGetBooleanv(pname, params);
        this.check();
    }
    
    @Override
    public void glGetBufferParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetBufferParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetFloatv(final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glGetFloatv(pname, params);
        this.check();
    }
    
    @Override
    public void glGetFramebufferAttachmentParameteriv(final int target, final int attachment, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        this.check();
    }
    
    @Override
    public void glGetProgramiv(final int program, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetProgramiv(program, pname, params);
        this.check();
    }
    
    @Override
    public String glGetProgramInfoLog(final int program) {
        ++this.calls;
        final String result = this.gl30.glGetProgramInfoLog(program);
        this.check();
        return result;
    }
    
    @Override
    public void glGetRenderbufferParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetRenderbufferParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetShaderiv(final int shader, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetShaderiv(shader, pname, params);
        this.check();
    }
    
    @Override
    public String glGetShaderInfoLog(final int shader) {
        ++this.calls;
        final String result = this.gl30.glGetShaderInfoLog(shader);
        this.check();
        return result;
    }
    
    @Override
    public void glGetShaderPrecisionFormat(final int shadertype, final int precisiontype, final IntBuffer range, final IntBuffer precision) {
        ++this.calls;
        this.gl30.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        this.check();
    }
    
    @Override
    public void glGetTexParameterfv(final int target, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glGetTexParameterfv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetTexParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetTexParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetUniformfv(final int program, final int location, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glGetUniformfv(program, location, params);
        this.check();
    }
    
    @Override
    public void glGetUniformiv(final int program, final int location, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetUniformiv(program, location, params);
        this.check();
    }
    
    @Override
    public int glGetUniformLocation(final int program, final String name) {
        ++this.calls;
        final int result = this.gl30.glGetUniformLocation(program, name);
        this.check();
        return result;
    }
    
    @Override
    public void glGetVertexAttribfv(final int index, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glGetVertexAttribfv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribiv(final int index, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetVertexAttribiv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribPointerv(final int index, final int pname, final Buffer pointer) {
        ++this.calls;
        this.gl30.glGetVertexAttribPointerv(index, pname, pointer);
        this.check();
    }
    
    @Override
    public boolean glIsBuffer(final int buffer) {
        ++this.calls;
        final boolean result = this.gl30.glIsBuffer(buffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsEnabled(final int cap) {
        ++this.calls;
        final boolean result = this.gl30.glIsEnabled(cap);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsFramebuffer(final int framebuffer) {
        ++this.calls;
        final boolean result = this.gl30.glIsFramebuffer(framebuffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsProgram(final int program) {
        ++this.calls;
        final boolean result = this.gl30.glIsProgram(program);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsRenderbuffer(final int renderbuffer) {
        ++this.calls;
        final boolean result = this.gl30.glIsRenderbuffer(renderbuffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsShader(final int shader) {
        ++this.calls;
        final boolean result = this.gl30.glIsShader(shader);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsTexture(final int texture) {
        ++this.calls;
        final boolean result = this.gl30.glIsTexture(texture);
        this.check();
        return result;
    }
    
    @Override
    public void glLinkProgram(final int program) {
        ++this.calls;
        this.gl30.glLinkProgram(program);
        this.check();
    }
    
    @Override
    public void glReleaseShaderCompiler() {
        ++this.calls;
        this.gl30.glReleaseShaderCompiler();
        this.check();
    }
    
    @Override
    public void glRenderbufferStorage(final int target, final int internalformat, final int width, final int height) {
        ++this.calls;
        this.gl30.glRenderbufferStorage(target, internalformat, width, height);
        this.check();
    }
    
    @Override
    public void glSampleCoverage(final float value, final boolean invert) {
        ++this.calls;
        this.gl30.glSampleCoverage(value, invert);
        this.check();
    }
    
    @Override
    public void glShaderBinary(final int n, final IntBuffer shaders, final int binaryformat, final Buffer binary, final int length) {
        ++this.calls;
        this.gl30.glShaderBinary(n, shaders, binaryformat, binary, length);
        this.check();
    }
    
    @Override
    public void glShaderSource(final int shader, final String string) {
        ++this.calls;
        this.gl30.glShaderSource(shader, string);
        this.check();
    }
    
    @Override
    public void glStencilFuncSeparate(final int face, final int func, final int ref, final int mask) {
        ++this.calls;
        this.gl30.glStencilFuncSeparate(face, func, ref, mask);
        this.check();
    }
    
    @Override
    public void glStencilMaskSeparate(final int face, final int mask) {
        ++this.calls;
        this.gl30.glStencilMaskSeparate(face, mask);
        this.check();
    }
    
    @Override
    public void glStencilOpSeparate(final int face, final int fail, final int zfail, final int zpass) {
        ++this.calls;
        this.gl30.glStencilOpSeparate(face, fail, zfail, zpass);
        this.check();
    }
    
    @Override
    public void glTexParameterfv(final int target, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glTexParameterfv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glTexParameteri(final int target, final int pname, final int param) {
        ++this.calls;
        this.gl30.glTexParameteri(target, pname, param);
        this.check();
    }
    
    @Override
    public void glTexParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glTexParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glUniform1f(final int location, final float x) {
        ++this.calls;
        this.gl30.glUniform1f(location, x);
        this.check();
    }
    
    @Override
    public void glUniform1fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl30.glUniform1fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform1fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform1fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform1i(final int location, final int x) {
        ++this.calls;
        this.gl30.glUniform1i(location, x);
        this.check();
    }
    
    @Override
    public void glUniform1iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl30.glUniform1iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform1iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform1iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform2f(final int location, final float x, final float y) {
        ++this.calls;
        this.gl30.glUniform2f(location, x, y);
        this.check();
    }
    
    @Override
    public void glUniform2fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl30.glUniform2fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform2fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform2fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform2i(final int location, final int x, final int y) {
        ++this.calls;
        this.gl30.glUniform2i(location, x, y);
        this.check();
    }
    
    @Override
    public void glUniform2iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl30.glUniform2iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform2iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform2iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform3f(final int location, final float x, final float y, final float z) {
        ++this.calls;
        this.gl30.glUniform3f(location, x, y, z);
        this.check();
    }
    
    @Override
    public void glUniform3fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl30.glUniform3fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform3fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform3fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform3i(final int location, final int x, final int y, final int z) {
        ++this.calls;
        this.gl30.glUniform3i(location, x, y, z);
        this.check();
    }
    
    @Override
    public void glUniform3iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl30.glUniform3iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform3iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform3iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform4f(final int location, final float x, final float y, final float z, final float w) {
        ++this.calls;
        this.gl30.glUniform4f(location, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glUniform4fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl30.glUniform4fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform4fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform4fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform4i(final int location, final int x, final int y, final int z, final int w) {
        ++this.calls;
        this.gl30.glUniform4i(location, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glUniform4iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl30.glUniform4iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform4iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl30.glUniform4iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix2fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl30.glUniformMatrix2fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix3fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl30.glUniformMatrix3fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix4fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl30.glUniformMatrix4fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUseProgram(final int program) {
        ++this.shaderSwitches;
        ++this.calls;
        this.gl30.glUseProgram(program);
        this.check();
    }
    
    @Override
    public void glValidateProgram(final int program) {
        ++this.calls;
        this.gl30.glValidateProgram(program);
        this.check();
    }
    
    @Override
    public void glVertexAttrib1f(final int indx, final float x) {
        ++this.calls;
        this.gl30.glVertexAttrib1f(indx, x);
        this.check();
    }
    
    @Override
    public void glVertexAttrib1fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl30.glVertexAttrib1fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib2f(final int indx, final float x, final float y) {
        ++this.calls;
        this.gl30.glVertexAttrib2f(indx, x, y);
        this.check();
    }
    
    @Override
    public void glVertexAttrib2fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl30.glVertexAttrib2fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib3f(final int indx, final float x, final float y, final float z) {
        ++this.calls;
        this.gl30.glVertexAttrib3f(indx, x, y, z);
        this.check();
    }
    
    @Override
    public void glVertexAttrib3fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl30.glVertexAttrib3fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib4f(final int indx, final float x, final float y, final float z, final float w) {
        ++this.calls;
        this.gl30.glVertexAttrib4f(indx, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glVertexAttrib4fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl30.glVertexAttrib4fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttribPointer(final int indx, final int size, final int type, final boolean normalized, final int stride, final Buffer ptr) {
        ++this.calls;
        this.gl30.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        this.check();
    }
    
    @Override
    public void glVertexAttribPointer(final int indx, final int size, final int type, final boolean normalized, final int stride, final int ptr) {
        ++this.calls;
        this.gl30.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        this.check();
    }
    
    @Override
    public void glReadBuffer(final int mode) {
        ++this.calls;
        this.gl30.glReadBuffer(mode);
        this.check();
    }
    
    @Override
    public void glDrawRangeElements(final int mode, final int start, final int end, final int count, final int type, final Buffer indices) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawRangeElements(mode, start, end, count, type, indices);
        this.check();
    }
    
    @Override
    public void glDrawRangeElements(final int mode, final int start, final int end, final int count, final int type, final int offset) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawRangeElements(mode, start, end, count, type, offset);
        this.check();
    }
    
    @Override
    public void glTexImage3D(final int target, final int level, final int internalformat, final int width, final int height, final int depth, final int border, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glTexImage3D(final int target, final int level, final int internalformat, final int width, final int height, final int depth, final int border, final int format, final int type, final int offset) {
        ++this.calls;
        this.gl30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
        this.check();
    }
    
    @Override
    public void glTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset, final int zoffset, final int width, final int height, final int depth, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset, final int zoffset, final int width, final int height, final int depth, final int format, final int type, final int offset) {
        ++this.calls;
        this.gl30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
        this.check();
    }
    
    @Override
    public void glCopyTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset, final int zoffset, final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl30.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
        this.check();
    }
    
    @Override
    public void glGenQueries(final int n, final int[] ids, final int offset) {
        ++this.calls;
        this.gl30.glGenQueries(n, ids, offset);
        this.check();
    }
    
    @Override
    public void glGenQueries(final int n, final IntBuffer ids) {
        ++this.calls;
        this.gl30.glGenQueries(n, ids);
        this.check();
    }
    
    @Override
    public void glDeleteQueries(final int n, final int[] ids, final int offset) {
        ++this.calls;
        this.gl30.glDeleteQueries(n, ids, offset);
        this.check();
    }
    
    @Override
    public void glDeleteQueries(final int n, final IntBuffer ids) {
        ++this.calls;
        this.gl30.glDeleteQueries(n, ids);
        this.check();
    }
    
    @Override
    public boolean glIsQuery(final int id) {
        ++this.calls;
        final boolean result = this.gl30.glIsQuery(id);
        this.check();
        return result;
    }
    
    @Override
    public void glBeginQuery(final int target, final int id) {
        ++this.calls;
        this.gl30.glBeginQuery(target, id);
        this.check();
    }
    
    @Override
    public void glEndQuery(final int target) {
        ++this.calls;
        this.gl30.glEndQuery(target);
        this.check();
    }
    
    @Override
    public void glGetQueryiv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetQueryiv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetQueryObjectuiv(final int id, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetQueryObjectuiv(id, pname, params);
        this.check();
    }
    
    @Override
    public boolean glUnmapBuffer(final int target) {
        ++this.calls;
        final boolean result = this.gl30.glUnmapBuffer(target);
        this.check();
        return result;
    }
    
    @Override
    public Buffer glGetBufferPointerv(final int target, final int pname) {
        ++this.calls;
        final Buffer result = this.gl30.glGetBufferPointerv(target, pname);
        this.check();
        return result;
    }
    
    @Override
    public void glDrawBuffers(final int n, final IntBuffer bufs) {
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawBuffers(n, bufs);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2x3fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix2x3fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3x2fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix3x2fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2x4fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix2x4fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4x2fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix4x2fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3x4fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix3x4fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4x3fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glUniformMatrix4x3fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glBlitFramebuffer(final int srcX0, final int srcY0, final int srcX1, final int srcY1, final int dstX0, final int dstY0, final int dstX1, final int dstY1, final int mask, final int filter) {
        ++this.calls;
        this.gl30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        this.check();
    }
    
    @Override
    public void glRenderbufferStorageMultisample(final int target, final int samples, final int internalformat, final int width, final int height) {
        ++this.calls;
        this.gl30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
        this.check();
    }
    
    @Override
    public void glFramebufferTextureLayer(final int target, final int attachment, final int texture, final int level, final int layer) {
        ++this.calls;
        this.gl30.glFramebufferTextureLayer(target, attachment, texture, level, layer);
        this.check();
    }
    
    @Override
    public void glFlushMappedBufferRange(final int target, final int offset, final int length) {
        ++this.calls;
        this.gl30.glFlushMappedBufferRange(target, offset, length);
        this.check();
    }
    
    @Override
    public void glBindVertexArray(final int array) {
        ++this.calls;
        this.gl30.glBindVertexArray(array);
        this.check();
    }
    
    @Override
    public void glDeleteVertexArrays(final int n, final int[] arrays, final int offset) {
        ++this.calls;
        this.gl30.glDeleteVertexArrays(n, arrays, offset);
        this.check();
    }
    
    @Override
    public void glDeleteVertexArrays(final int n, final IntBuffer arrays) {
        ++this.calls;
        this.gl30.glDeleteVertexArrays(n, arrays);
        this.check();
    }
    
    @Override
    public void glGenVertexArrays(final int n, final int[] arrays, final int offset) {
        ++this.calls;
        this.gl30.glGenVertexArrays(n, arrays, offset);
        this.check();
    }
    
    @Override
    public void glGenVertexArrays(final int n, final IntBuffer arrays) {
        ++this.calls;
        this.gl30.glGenVertexArrays(n, arrays);
        this.check();
    }
    
    @Override
    public boolean glIsVertexArray(final int array) {
        ++this.calls;
        final boolean result = this.gl30.glIsVertexArray(array);
        this.check();
        return result;
    }
    
    @Override
    public void glBeginTransformFeedback(final int primitiveMode) {
        ++this.calls;
        this.gl30.glBeginTransformFeedback(primitiveMode);
        this.check();
    }
    
    @Override
    public void glEndTransformFeedback() {
        ++this.calls;
        this.gl30.glEndTransformFeedback();
        this.check();
    }
    
    @Override
    public void glBindBufferRange(final int target, final int index, final int buffer, final int offset, final int size) {
        ++this.calls;
        this.gl30.glBindBufferRange(target, index, buffer, offset, size);
        this.check();
    }
    
    @Override
    public void glBindBufferBase(final int target, final int index, final int buffer) {
        ++this.calls;
        this.gl30.glBindBufferBase(target, index, buffer);
        this.check();
    }
    
    @Override
    public void glTransformFeedbackVaryings(final int program, final String[] varyings, final int bufferMode) {
        ++this.calls;
        this.gl30.glTransformFeedbackVaryings(program, varyings, bufferMode);
        this.check();
    }
    
    @Override
    public void glVertexAttribIPointer(final int index, final int size, final int type, final int stride, final int offset) {
        ++this.calls;
        this.gl30.glVertexAttribIPointer(index, size, type, stride, offset);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribIiv(final int index, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetVertexAttribIiv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribIuiv(final int index, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetVertexAttribIuiv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glVertexAttribI4i(final int index, final int x, final int y, final int z, final int w) {
        ++this.calls;
        this.gl30.glVertexAttribI4i(index, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glVertexAttribI4ui(final int index, final int x, final int y, final int z, final int w) {
        ++this.calls;
        this.gl30.glVertexAttribI4ui(index, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glGetUniformuiv(final int program, final int location, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetUniformuiv(program, location, params);
        this.check();
    }
    
    @Override
    public int glGetFragDataLocation(final int program, final String name) {
        ++this.calls;
        final int result = this.gl30.glGetFragDataLocation(program, name);
        this.check();
        return result;
    }
    
    @Override
    public void glUniform1uiv(final int location, final int count, final IntBuffer value) {
        ++this.calls;
        this.gl30.glUniform1uiv(location, count, value);
        this.check();
    }
    
    @Override
    public void glUniform3uiv(final int location, final int count, final IntBuffer value) {
        ++this.calls;
        this.gl30.glUniform3uiv(location, count, value);
        this.check();
    }
    
    @Override
    public void glUniform4uiv(final int location, final int count, final IntBuffer value) {
        ++this.calls;
        this.gl30.glUniform4uiv(location, count, value);
        this.check();
    }
    
    @Override
    public void glClearBufferiv(final int buffer, final int drawbuffer, final IntBuffer value) {
        ++this.calls;
        this.gl30.glClearBufferiv(buffer, drawbuffer, value);
        this.check();
    }
    
    @Override
    public void glClearBufferuiv(final int buffer, final int drawbuffer, final IntBuffer value) {
        ++this.calls;
        this.gl30.glClearBufferuiv(buffer, drawbuffer, value);
        this.check();
    }
    
    @Override
    public void glClearBufferfv(final int buffer, final int drawbuffer, final FloatBuffer value) {
        ++this.calls;
        this.gl30.glClearBufferfv(buffer, drawbuffer, value);
        this.check();
    }
    
    @Override
    public void glClearBufferfi(final int buffer, final int drawbuffer, final float depth, final int stencil) {
        ++this.calls;
        this.gl30.glClearBufferfi(buffer, drawbuffer, depth, stencil);
        this.check();
    }
    
    @Override
    public String glGetStringi(final int name, final int index) {
        ++this.calls;
        final String result = this.gl30.glGetStringi(name, index);
        this.check();
        return result;
    }
    
    @Override
    public void glCopyBufferSubData(final int readTarget, final int writeTarget, final int readOffset, final int writeOffset, final int size) {
        ++this.calls;
        this.gl30.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
        this.check();
    }
    
    @Override
    public void glGetUniformIndices(final int program, final String[] uniformNames, final IntBuffer uniformIndices) {
        ++this.calls;
        this.gl30.glGetUniformIndices(program, uniformNames, uniformIndices);
        this.check();
    }
    
    @Override
    public void glGetActiveUniformsiv(final int program, final int uniformCount, final IntBuffer uniformIndices, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
        this.check();
    }
    
    @Override
    public int glGetUniformBlockIndex(final int program, final String uniformBlockName) {
        ++this.calls;
        final int result = this.gl30.glGetUniformBlockIndex(program, uniformBlockName);
        this.check();
        return result;
    }
    
    @Override
    public void glGetActiveUniformBlockiv(final int program, final int uniformBlockIndex, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
        this.check();
    }
    
    @Override
    public void glGetActiveUniformBlockName(final int program, final int uniformBlockIndex, final Buffer length, final Buffer uniformBlockName) {
        ++this.calls;
        this.gl30.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
        this.check();
    }
    
    @Override
    public String glGetActiveUniformBlockName(final int program, final int uniformBlockIndex) {
        ++this.calls;
        final String result = this.gl30.glGetActiveUniformBlockName(program, uniformBlockIndex);
        this.check();
        return result;
    }
    
    @Override
    public void glUniformBlockBinding(final int program, final int uniformBlockIndex, final int uniformBlockBinding) {
        ++this.calls;
        this.gl30.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
        this.check();
    }
    
    @Override
    public void glDrawArraysInstanced(final int mode, final int first, final int count, final int instanceCount) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawArraysInstanced(mode, first, count, instanceCount);
        this.check();
    }
    
    @Override
    public void glDrawElementsInstanced(final int mode, final int count, final int type, final int indicesOffset, final int instanceCount) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl30.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
        this.check();
    }
    
    @Override
    public void glGetInteger64v(final int pname, final LongBuffer params) {
        ++this.calls;
        this.gl30.glGetInteger64v(pname, params);
        this.check();
    }
    
    @Override
    public void glGetBufferParameteri64v(final int target, final int pname, final LongBuffer params) {
        ++this.calls;
        this.gl30.glGetBufferParameteri64v(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGenSamplers(final int count, final int[] samplers, final int offset) {
        ++this.calls;
        this.gl30.glGenSamplers(count, samplers, offset);
        this.check();
    }
    
    @Override
    public void glGenSamplers(final int count, final IntBuffer samplers) {
        ++this.calls;
        this.gl30.glGenSamplers(count, samplers);
        this.check();
    }
    
    @Override
    public void glDeleteSamplers(final int count, final int[] samplers, final int offset) {
        ++this.calls;
        this.gl30.glDeleteSamplers(count, samplers, offset);
        this.check();
    }
    
    @Override
    public void glDeleteSamplers(final int count, final IntBuffer samplers) {
        ++this.calls;
        this.gl30.glDeleteSamplers(count, samplers);
        this.check();
    }
    
    @Override
    public boolean glIsSampler(final int sampler) {
        ++this.calls;
        final boolean result = this.gl30.glIsSampler(sampler);
        this.check();
        return result;
    }
    
    @Override
    public void glBindSampler(final int unit, final int sampler) {
        ++this.calls;
        this.gl30.glBindSampler(unit, sampler);
        this.check();
    }
    
    @Override
    public void glSamplerParameteri(final int sampler, final int pname, final int param) {
        ++this.calls;
        this.gl30.glSamplerParameteri(sampler, pname, param);
        this.check();
    }
    
    @Override
    public void glSamplerParameteriv(final int sampler, final int pname, final IntBuffer param) {
        ++this.calls;
        this.gl30.glSamplerParameteriv(sampler, pname, param);
        this.check();
    }
    
    @Override
    public void glSamplerParameterf(final int sampler, final int pname, final float param) {
        ++this.calls;
        this.gl30.glSamplerParameterf(sampler, pname, param);
        this.check();
    }
    
    @Override
    public void glSamplerParameterfv(final int sampler, final int pname, final FloatBuffer param) {
        ++this.calls;
        this.gl30.glSamplerParameterfv(sampler, pname, param);
        this.check();
    }
    
    @Override
    public void glGetSamplerParameteriv(final int sampler, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl30.glGetSamplerParameteriv(sampler, pname, params);
        this.check();
    }
    
    @Override
    public void glGetSamplerParameterfv(final int sampler, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl30.glGetSamplerParameterfv(sampler, pname, params);
        this.check();
    }
    
    @Override
    public void glVertexAttribDivisor(final int index, final int divisor) {
        ++this.calls;
        this.gl30.glVertexAttribDivisor(index, divisor);
        this.check();
    }
    
    @Override
    public void glBindTransformFeedback(final int target, final int id) {
        ++this.calls;
        this.gl30.glBindTransformFeedback(target, id);
        this.check();
    }
    
    @Override
    public void glDeleteTransformFeedbacks(final int n, final int[] ids, final int offset) {
        ++this.calls;
        this.gl30.glDeleteTransformFeedbacks(n, ids, offset);
        this.check();
    }
    
    @Override
    public void glDeleteTransformFeedbacks(final int n, final IntBuffer ids) {
        ++this.calls;
        this.gl30.glDeleteTransformFeedbacks(n, ids);
        this.check();
    }
    
    @Override
    public void glGenTransformFeedbacks(final int n, final int[] ids, final int offset) {
        ++this.calls;
        this.gl30.glGenTransformFeedbacks(n, ids, offset);
        this.check();
    }
    
    @Override
    public void glGenTransformFeedbacks(final int n, final IntBuffer ids) {
        ++this.calls;
        this.gl30.glGenTransformFeedbacks(n, ids);
        this.check();
    }
    
    @Override
    public boolean glIsTransformFeedback(final int id) {
        ++this.calls;
        final boolean result = this.gl30.glIsTransformFeedback(id);
        this.check();
        return result;
    }
    
    @Override
    public void glPauseTransformFeedback() {
        ++this.calls;
        this.gl30.glPauseTransformFeedback();
        this.check();
    }
    
    @Override
    public void glResumeTransformFeedback() {
        ++this.calls;
        this.gl30.glResumeTransformFeedback();
        this.check();
    }
    
    @Override
    public void glProgramParameteri(final int program, final int pname, final int value) {
        ++this.calls;
        this.gl30.glProgramParameteri(program, pname, value);
        this.check();
    }
    
    @Override
    public void glInvalidateFramebuffer(final int target, final int numAttachments, final IntBuffer attachments) {
        ++this.calls;
        this.gl30.glInvalidateFramebuffer(target, numAttachments, attachments);
        this.check();
    }
    
    @Override
    public void glInvalidateSubFramebuffer(final int target, final int numAttachments, final IntBuffer attachments, final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl30.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
        this.check();
    }
}

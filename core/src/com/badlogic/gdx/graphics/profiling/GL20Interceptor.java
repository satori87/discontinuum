// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.profiling;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.GL20;

public class GL20Interceptor extends GLInterceptor implements GL20
{
    protected final GL20 gl20;
    
    protected GL20Interceptor(final GLProfiler glProfiler, final GL20 gl20) {
        super(glProfiler);
        this.gl20 = gl20;
    }
    
    private void check() {
        for (int error = this.gl20.glGetError(); error != 0; error = this.gl20.glGetError()) {
            this.glProfiler.getListener().onError(error);
        }
    }
    
    @Override
    public void glActiveTexture(final int texture) {
        ++this.calls;
        this.gl20.glActiveTexture(texture);
        this.check();
    }
    
    @Override
    public void glBindTexture(final int target, final int texture) {
        ++this.textureBindings;
        ++this.calls;
        this.gl20.glBindTexture(target, texture);
        this.check();
    }
    
    @Override
    public void glBlendFunc(final int sfactor, final int dfactor) {
        ++this.calls;
        this.gl20.glBlendFunc(sfactor, dfactor);
        this.check();
    }
    
    @Override
    public void glClear(final int mask) {
        ++this.calls;
        this.gl20.glClear(mask);
        this.check();
    }
    
    @Override
    public void glClearColor(final float red, final float green, final float blue, final float alpha) {
        ++this.calls;
        this.gl20.glClearColor(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glClearDepthf(final float depth) {
        ++this.calls;
        this.gl20.glClearDepthf(depth);
        this.check();
    }
    
    @Override
    public void glClearStencil(final int s) {
        ++this.calls;
        this.gl20.glClearStencil(s);
        this.check();
    }
    
    @Override
    public void glColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
        ++this.calls;
        this.gl20.glColorMask(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glCompressedTexImage2D(final int target, final int level, final int internalformat, final int width, final int height, final int border, final int imageSize, final Buffer data) {
        ++this.calls;
        this.gl20.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        this.check();
    }
    
    @Override
    public void glCompressedTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int imageSize, final Buffer data) {
        ++this.calls;
        this.gl20.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        this.check();
    }
    
    @Override
    public void glCopyTexImage2D(final int target, final int level, final int internalformat, final int x, final int y, final int width, final int height, final int border) {
        ++this.calls;
        this.gl20.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        this.check();
    }
    
    @Override
    public void glCopyTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        this.check();
    }
    
    @Override
    public void glCullFace(final int mode) {
        ++this.calls;
        this.gl20.glCullFace(mode);
        this.check();
    }
    
    @Override
    public void glDeleteTextures(final int n, final IntBuffer textures) {
        ++this.calls;
        this.gl20.glDeleteTextures(n, textures);
        this.check();
    }
    
    @Override
    public void glDeleteTexture(final int texture) {
        ++this.calls;
        this.gl20.glDeleteTexture(texture);
        this.check();
    }
    
    @Override
    public void glDepthFunc(final int func) {
        ++this.calls;
        this.gl20.glDepthFunc(func);
        this.check();
    }
    
    @Override
    public void glDepthMask(final boolean flag) {
        ++this.calls;
        this.gl20.glDepthMask(flag);
        this.check();
    }
    
    @Override
    public void glDepthRangef(final float zNear, final float zFar) {
        ++this.calls;
        this.gl20.glDepthRangef(zNear, zFar);
        this.check();
    }
    
    @Override
    public void glDisable(final int cap) {
        ++this.calls;
        this.gl20.glDisable(cap);
        this.check();
    }
    
    @Override
    public void glDrawArrays(final int mode, final int first, final int count) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl20.glDrawArrays(mode, first, count);
        this.check();
    }
    
    @Override
    public void glDrawElements(final int mode, final int count, final int type, final Buffer indices) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl20.glDrawElements(mode, count, type, indices);
        this.check();
    }
    
    @Override
    public void glEnable(final int cap) {
        ++this.calls;
        this.gl20.glEnable(cap);
        this.check();
    }
    
    @Override
    public void glFinish() {
        ++this.calls;
        this.gl20.glFinish();
        this.check();
    }
    
    @Override
    public void glFlush() {
        ++this.calls;
        this.gl20.glFlush();
        this.check();
    }
    
    @Override
    public void glFrontFace(final int mode) {
        ++this.calls;
        this.gl20.glFrontFace(mode);
        this.check();
    }
    
    @Override
    public void glGenTextures(final int n, final IntBuffer textures) {
        ++this.calls;
        this.gl20.glGenTextures(n, textures);
        this.check();
    }
    
    @Override
    public int glGenTexture() {
        ++this.calls;
        final int result = this.gl20.glGenTexture();
        this.check();
        return result;
    }
    
    @Override
    public int glGetError() {
        ++this.calls;
        return this.gl20.glGetError();
    }
    
    @Override
    public void glGetIntegerv(final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetIntegerv(pname, params);
        this.check();
    }
    
    @Override
    public String glGetString(final int name) {
        ++this.calls;
        final String result = this.gl20.glGetString(name);
        this.check();
        return result;
    }
    
    @Override
    public void glHint(final int target, final int mode) {
        ++this.calls;
        this.gl20.glHint(target, mode);
        this.check();
    }
    
    @Override
    public void glLineWidth(final float width) {
        ++this.calls;
        this.gl20.glLineWidth(width);
        this.check();
    }
    
    @Override
    public void glPixelStorei(final int pname, final int param) {
        ++this.calls;
        this.gl20.glPixelStorei(pname, param);
        this.check();
    }
    
    @Override
    public void glPolygonOffset(final float factor, final float units) {
        ++this.calls;
        this.gl20.glPolygonOffset(factor, units);
        this.check();
    }
    
    @Override
    public void glReadPixels(final int x, final int y, final int width, final int height, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl20.glReadPixels(x, y, width, height, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glScissor(final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl20.glScissor(x, y, width, height);
        this.check();
    }
    
    @Override
    public void glStencilFunc(final int func, final int ref, final int mask) {
        ++this.calls;
        this.gl20.glStencilFunc(func, ref, mask);
        this.check();
    }
    
    @Override
    public void glStencilMask(final int mask) {
        ++this.calls;
        this.gl20.glStencilMask(mask);
        this.check();
    }
    
    @Override
    public void glStencilOp(final int fail, final int zfail, final int zpass) {
        ++this.calls;
        this.gl20.glStencilOp(fail, zfail, zpass);
        this.check();
    }
    
    @Override
    public void glTexImage2D(final int target, final int level, final int internalformat, final int width, final int height, final int border, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glTexParameterf(final int target, final int pname, final float param) {
        ++this.calls;
        this.gl20.glTexParameterf(target, pname, param);
        this.check();
    }
    
    @Override
    public void glTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int type, final Buffer pixels) {
        ++this.calls;
        this.gl20.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        this.check();
    }
    
    @Override
    public void glViewport(final int x, final int y, final int width, final int height) {
        ++this.calls;
        this.gl20.glViewport(x, y, width, height);
        this.check();
    }
    
    @Override
    public void glAttachShader(final int program, final int shader) {
        ++this.calls;
        this.gl20.glAttachShader(program, shader);
        this.check();
    }
    
    @Override
    public void glBindAttribLocation(final int program, final int index, final String name) {
        ++this.calls;
        this.gl20.glBindAttribLocation(program, index, name);
        this.check();
    }
    
    @Override
    public void glBindBuffer(final int target, final int buffer) {
        ++this.calls;
        this.gl20.glBindBuffer(target, buffer);
        this.check();
    }
    
    @Override
    public void glBindFramebuffer(final int target, final int framebuffer) {
        ++this.calls;
        this.gl20.glBindFramebuffer(target, framebuffer);
        this.check();
    }
    
    @Override
    public void glBindRenderbuffer(final int target, final int renderbuffer) {
        ++this.calls;
        this.gl20.glBindRenderbuffer(target, renderbuffer);
        this.check();
    }
    
    @Override
    public void glBlendColor(final float red, final float green, final float blue, final float alpha) {
        ++this.calls;
        this.gl20.glBlendColor(red, green, blue, alpha);
        this.check();
    }
    
    @Override
    public void glBlendEquation(final int mode) {
        ++this.calls;
        this.gl20.glBlendEquation(mode);
        this.check();
    }
    
    @Override
    public void glBlendEquationSeparate(final int modeRGB, final int modeAlpha) {
        ++this.calls;
        this.gl20.glBlendEquationSeparate(modeRGB, modeAlpha);
        this.check();
    }
    
    @Override
    public void glBlendFuncSeparate(final int srcRGB, final int dstRGB, final int srcAlpha, final int dstAlpha) {
        ++this.calls;
        this.gl20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        this.check();
    }
    
    @Override
    public void glBufferData(final int target, final int size, final Buffer data, final int usage) {
        ++this.calls;
        this.gl20.glBufferData(target, size, data, usage);
        this.check();
    }
    
    @Override
    public void glBufferSubData(final int target, final int offset, final int size, final Buffer data) {
        ++this.calls;
        this.gl20.glBufferSubData(target, offset, size, data);
        this.check();
    }
    
    @Override
    public int glCheckFramebufferStatus(final int target) {
        ++this.calls;
        final int result = this.gl20.glCheckFramebufferStatus(target);
        this.check();
        return result;
    }
    
    @Override
    public void glCompileShader(final int shader) {
        ++this.calls;
        this.gl20.glCompileShader(shader);
        this.check();
    }
    
    @Override
    public int glCreateProgram() {
        ++this.calls;
        final int result = this.gl20.glCreateProgram();
        this.check();
        return result;
    }
    
    @Override
    public int glCreateShader(final int type) {
        ++this.calls;
        final int result = this.gl20.glCreateShader(type);
        this.check();
        return result;
    }
    
    @Override
    public void glDeleteBuffer(final int buffer) {
        ++this.calls;
        this.gl20.glDeleteBuffer(buffer);
        this.check();
    }
    
    @Override
    public void glDeleteBuffers(final int n, final IntBuffer buffers) {
        ++this.calls;
        this.gl20.glDeleteBuffers(n, buffers);
        this.check();
    }
    
    @Override
    public void glDeleteFramebuffer(final int framebuffer) {
        ++this.calls;
        this.gl20.glDeleteFramebuffer(framebuffer);
        this.check();
    }
    
    @Override
    public void glDeleteFramebuffers(final int n, final IntBuffer framebuffers) {
        ++this.calls;
        this.gl20.glDeleteFramebuffers(n, framebuffers);
        this.check();
    }
    
    @Override
    public void glDeleteProgram(final int program) {
        ++this.calls;
        this.gl20.glDeleteProgram(program);
        this.check();
    }
    
    @Override
    public void glDeleteRenderbuffer(final int renderbuffer) {
        ++this.calls;
        this.gl20.glDeleteRenderbuffer(renderbuffer);
        this.check();
    }
    
    @Override
    public void glDeleteRenderbuffers(final int n, final IntBuffer renderbuffers) {
        ++this.calls;
        this.gl20.glDeleteRenderbuffers(n, renderbuffers);
        this.check();
    }
    
    @Override
    public void glDeleteShader(final int shader) {
        ++this.calls;
        this.gl20.glDeleteShader(shader);
        this.check();
    }
    
    @Override
    public void glDetachShader(final int program, final int shader) {
        ++this.calls;
        this.gl20.glDetachShader(program, shader);
        this.check();
    }
    
    @Override
    public void glDisableVertexAttribArray(final int index) {
        ++this.calls;
        this.gl20.glDisableVertexAttribArray(index);
        this.check();
    }
    
    @Override
    public void glDrawElements(final int mode, final int count, final int type, final int indices) {
        this.vertexCount.put((float)count);
        ++this.drawCalls;
        ++this.calls;
        this.gl20.glDrawElements(mode, count, type, indices);
        this.check();
    }
    
    @Override
    public void glEnableVertexAttribArray(final int index) {
        ++this.calls;
        this.gl20.glEnableVertexAttribArray(index);
        this.check();
    }
    
    @Override
    public void glFramebufferRenderbuffer(final int target, final int attachment, final int renderbuffertarget, final int renderbuffer) {
        ++this.calls;
        this.gl20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        this.check();
    }
    
    @Override
    public void glFramebufferTexture2D(final int target, final int attachment, final int textarget, final int texture, final int level) {
        ++this.calls;
        this.gl20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        this.check();
    }
    
    @Override
    public int glGenBuffer() {
        ++this.calls;
        final int result = this.gl20.glGenBuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenBuffers(final int n, final IntBuffer buffers) {
        ++this.calls;
        this.gl20.glGenBuffers(n, buffers);
        this.check();
    }
    
    @Override
    public void glGenerateMipmap(final int target) {
        ++this.calls;
        this.gl20.glGenerateMipmap(target);
        this.check();
    }
    
    @Override
    public int glGenFramebuffer() {
        ++this.calls;
        final int result = this.gl20.glGenFramebuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenFramebuffers(final int n, final IntBuffer framebuffers) {
        ++this.calls;
        this.gl20.glGenFramebuffers(n, framebuffers);
        this.check();
    }
    
    @Override
    public int glGenRenderbuffer() {
        ++this.calls;
        final int result = this.gl20.glGenRenderbuffer();
        this.check();
        return result;
    }
    
    @Override
    public void glGenRenderbuffers(final int n, final IntBuffer renderbuffers) {
        ++this.calls;
        this.gl20.glGenRenderbuffers(n, renderbuffers);
        this.check();
    }
    
    @Override
    public String glGetActiveAttrib(final int program, final int index, final IntBuffer size, final Buffer type) {
        ++this.calls;
        final String result = this.gl20.glGetActiveAttrib(program, index, size, type);
        this.check();
        return result;
    }
    
    @Override
    public String glGetActiveUniform(final int program, final int index, final IntBuffer size, final Buffer type) {
        ++this.calls;
        final String result = this.gl20.glGetActiveUniform(program, index, size, type);
        this.check();
        return result;
    }
    
    @Override
    public void glGetAttachedShaders(final int program, final int maxcount, final Buffer count, final IntBuffer shaders) {
        ++this.calls;
        this.gl20.glGetAttachedShaders(program, maxcount, count, shaders);
        this.check();
    }
    
    @Override
    public int glGetAttribLocation(final int program, final String name) {
        ++this.calls;
        final int result = this.gl20.glGetAttribLocation(program, name);
        this.check();
        return result;
    }
    
    @Override
    public void glGetBooleanv(final int pname, final Buffer params) {
        ++this.calls;
        this.gl20.glGetBooleanv(pname, params);
        this.check();
    }
    
    @Override
    public void glGetBufferParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetBufferParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetFloatv(final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl20.glGetFloatv(pname, params);
        this.check();
    }
    
    @Override
    public void glGetFramebufferAttachmentParameteriv(final int target, final int attachment, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        this.check();
    }
    
    @Override
    public void glGetProgramiv(final int program, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetProgramiv(program, pname, params);
        this.check();
    }
    
    @Override
    public String glGetProgramInfoLog(final int program) {
        ++this.calls;
        final String result = this.gl20.glGetProgramInfoLog(program);
        this.check();
        return result;
    }
    
    @Override
    public void glGetRenderbufferParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetRenderbufferParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetShaderiv(final int shader, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetShaderiv(shader, pname, params);
        this.check();
    }
    
    @Override
    public String glGetShaderInfoLog(final int shader) {
        ++this.calls;
        final String result = this.gl20.glGetShaderInfoLog(shader);
        this.check();
        return result;
    }
    
    @Override
    public void glGetShaderPrecisionFormat(final int shadertype, final int precisiontype, final IntBuffer range, final IntBuffer precision) {
        ++this.calls;
        this.gl20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        this.check();
    }
    
    @Override
    public void glGetTexParameterfv(final int target, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl20.glGetTexParameterfv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetTexParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetTexParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glGetUniformfv(final int program, final int location, final FloatBuffer params) {
        ++this.calls;
        this.gl20.glGetUniformfv(program, location, params);
        this.check();
    }
    
    @Override
    public void glGetUniformiv(final int program, final int location, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetUniformiv(program, location, params);
        this.check();
    }
    
    @Override
    public int glGetUniformLocation(final int program, final String name) {
        ++this.calls;
        final int result = this.gl20.glGetUniformLocation(program, name);
        this.check();
        return result;
    }
    
    @Override
    public void glGetVertexAttribfv(final int index, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl20.glGetVertexAttribfv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribiv(final int index, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glGetVertexAttribiv(index, pname, params);
        this.check();
    }
    
    @Override
    public void glGetVertexAttribPointerv(final int index, final int pname, final Buffer pointer) {
        ++this.calls;
        this.gl20.glGetVertexAttribPointerv(index, pname, pointer);
        this.check();
    }
    
    @Override
    public boolean glIsBuffer(final int buffer) {
        ++this.calls;
        final boolean result = this.gl20.glIsBuffer(buffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsEnabled(final int cap) {
        ++this.calls;
        final boolean result = this.gl20.glIsEnabled(cap);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsFramebuffer(final int framebuffer) {
        ++this.calls;
        final boolean result = this.gl20.glIsFramebuffer(framebuffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsProgram(final int program) {
        ++this.calls;
        final boolean result = this.gl20.glIsProgram(program);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsRenderbuffer(final int renderbuffer) {
        ++this.calls;
        final boolean result = this.gl20.glIsRenderbuffer(renderbuffer);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsShader(final int shader) {
        ++this.calls;
        final boolean result = this.gl20.glIsShader(shader);
        this.check();
        return result;
    }
    
    @Override
    public boolean glIsTexture(final int texture) {
        ++this.calls;
        final boolean result = this.gl20.glIsTexture(texture);
        this.check();
        return result;
    }
    
    @Override
    public void glLinkProgram(final int program) {
        ++this.calls;
        this.gl20.glLinkProgram(program);
        this.check();
    }
    
    @Override
    public void glReleaseShaderCompiler() {
        ++this.calls;
        this.gl20.glReleaseShaderCompiler();
        this.check();
    }
    
    @Override
    public void glRenderbufferStorage(final int target, final int internalformat, final int width, final int height) {
        ++this.calls;
        this.gl20.glRenderbufferStorage(target, internalformat, width, height);
        this.check();
    }
    
    @Override
    public void glSampleCoverage(final float value, final boolean invert) {
        ++this.calls;
        this.gl20.glSampleCoverage(value, invert);
        this.check();
    }
    
    @Override
    public void glShaderBinary(final int n, final IntBuffer shaders, final int binaryformat, final Buffer binary, final int length) {
        ++this.calls;
        this.gl20.glShaderBinary(n, shaders, binaryformat, binary, length);
        this.check();
    }
    
    @Override
    public void glShaderSource(final int shader, final String string) {
        ++this.calls;
        this.gl20.glShaderSource(shader, string);
        this.check();
    }
    
    @Override
    public void glStencilFuncSeparate(final int face, final int func, final int ref, final int mask) {
        ++this.calls;
        this.gl20.glStencilFuncSeparate(face, func, ref, mask);
        this.check();
    }
    
    @Override
    public void glStencilMaskSeparate(final int face, final int mask) {
        ++this.calls;
        this.gl20.glStencilMaskSeparate(face, mask);
        this.check();
    }
    
    @Override
    public void glStencilOpSeparate(final int face, final int fail, final int zfail, final int zpass) {
        ++this.calls;
        this.gl20.glStencilOpSeparate(face, fail, zfail, zpass);
        this.check();
    }
    
    @Override
    public void glTexParameterfv(final int target, final int pname, final FloatBuffer params) {
        ++this.calls;
        this.gl20.glTexParameterfv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glTexParameteri(final int target, final int pname, final int param) {
        ++this.calls;
        this.gl20.glTexParameteri(target, pname, param);
        this.check();
    }
    
    @Override
    public void glTexParameteriv(final int target, final int pname, final IntBuffer params) {
        ++this.calls;
        this.gl20.glTexParameteriv(target, pname, params);
        this.check();
    }
    
    @Override
    public void glUniform1f(final int location, final float x) {
        ++this.calls;
        this.gl20.glUniform1f(location, x);
        this.check();
    }
    
    @Override
    public void glUniform1fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl20.glUniform1fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform1fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform1fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform1i(final int location, final int x) {
        ++this.calls;
        this.gl20.glUniform1i(location, x);
        this.check();
    }
    
    @Override
    public void glUniform1iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl20.glUniform1iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform1iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform1iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform2f(final int location, final float x, final float y) {
        ++this.calls;
        this.gl20.glUniform2f(location, x, y);
        this.check();
    }
    
    @Override
    public void glUniform2fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl20.glUniform2fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform2fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform2fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform2i(final int location, final int x, final int y) {
        ++this.calls;
        this.gl20.glUniform2i(location, x, y);
        this.check();
    }
    
    @Override
    public void glUniform2iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl20.glUniform2iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform2iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform2iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform3f(final int location, final float x, final float y, final float z) {
        ++this.calls;
        this.gl20.glUniform3f(location, x, y, z);
        this.check();
    }
    
    @Override
    public void glUniform3fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl20.glUniform3fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform3fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform3fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform3i(final int location, final int x, final int y, final int z) {
        ++this.calls;
        this.gl20.glUniform3i(location, x, y, z);
        this.check();
    }
    
    @Override
    public void glUniform3iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl20.glUniform3iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform3iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform3iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform4f(final int location, final float x, final float y, final float z, final float w) {
        ++this.calls;
        this.gl20.glUniform4f(location, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glUniform4fv(final int location, final int count, final FloatBuffer v) {
        ++this.calls;
        this.gl20.glUniform4fv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform4fv(final int location, final int count, final float[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform4fv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniform4i(final int location, final int x, final int y, final int z, final int w) {
        ++this.calls;
        this.gl20.glUniform4i(location, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glUniform4iv(final int location, final int count, final IntBuffer v) {
        ++this.calls;
        this.gl20.glUniform4iv(location, count, v);
        this.check();
    }
    
    @Override
    public void glUniform4iv(final int location, final int count, final int[] v, final int offset) {
        ++this.calls;
        this.gl20.glUniform4iv(location, count, v, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl20.glUniformMatrix2fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix2fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl20.glUniformMatrix2fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl20.glUniformMatrix3fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix3fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl20.glUniformMatrix3fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4fv(final int location, final int count, final boolean transpose, final FloatBuffer value) {
        ++this.calls;
        this.gl20.glUniformMatrix4fv(location, count, transpose, value);
        this.check();
    }
    
    @Override
    public void glUniformMatrix4fv(final int location, final int count, final boolean transpose, final float[] value, final int offset) {
        ++this.calls;
        this.gl20.glUniformMatrix4fv(location, count, transpose, value, offset);
        this.check();
    }
    
    @Override
    public void glUseProgram(final int program) {
        ++this.shaderSwitches;
        ++this.calls;
        this.gl20.glUseProgram(program);
        this.check();
    }
    
    @Override
    public void glValidateProgram(final int program) {
        ++this.calls;
        this.gl20.glValidateProgram(program);
        this.check();
    }
    
    @Override
    public void glVertexAttrib1f(final int indx, final float x) {
        ++this.calls;
        this.gl20.glVertexAttrib1f(indx, x);
        this.check();
    }
    
    @Override
    public void glVertexAttrib1fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl20.glVertexAttrib1fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib2f(final int indx, final float x, final float y) {
        ++this.calls;
        this.gl20.glVertexAttrib2f(indx, x, y);
        this.check();
    }
    
    @Override
    public void glVertexAttrib2fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl20.glVertexAttrib2fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib3f(final int indx, final float x, final float y, final float z) {
        ++this.calls;
        this.gl20.glVertexAttrib3f(indx, x, y, z);
        this.check();
    }
    
    @Override
    public void glVertexAttrib3fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl20.glVertexAttrib3fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttrib4f(final int indx, final float x, final float y, final float z, final float w) {
        ++this.calls;
        this.gl20.glVertexAttrib4f(indx, x, y, z, w);
        this.check();
    }
    
    @Override
    public void glVertexAttrib4fv(final int indx, final FloatBuffer values) {
        ++this.calls;
        this.gl20.glVertexAttrib4fv(indx, values);
        this.check();
    }
    
    @Override
    public void glVertexAttribPointer(final int indx, final int size, final int type, final boolean normalized, final int stride, final Buffer ptr) {
        ++this.calls;
        this.gl20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        this.check();
    }
    
    @Override
    public void glVertexAttribPointer(final int indx, final int size, final int type, final boolean normalized, final int stride, final int ptr) {
        ++this.calls;
        this.gl20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        this.check();
    }
}

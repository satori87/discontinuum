// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import java.util.List;
import com.badlogic.gdx.net.HttpStatus;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;

public interface Net
{
    void sendHttpRequest(final HttpRequest p0, final HttpResponseListener p1);
    
    void cancelHttpRequest(final HttpRequest p0);
    
    ServerSocket newServerSocket(final Protocol p0, final String p1, final int p2, final ServerSocketHints p3);
    
    ServerSocket newServerSocket(final Protocol p0, final int p1, final ServerSocketHints p2);
    
    Socket newClientSocket(final Protocol p0, final String p1, final int p2, final SocketHints p3);
    
    boolean openURI(final String p0);
    
    public enum Protocol
    {
        TCP("TCP", 0);
        
        private Protocol(final String name, final int ordinal) {
        }
    }
    
    public static class HttpRequest implements Pool.Poolable
    {
        private String httpMethod;
        private String url;
        private Map<String, String> headers;
        private int timeOut;
        private String content;
        private InputStream contentStream;
        private long contentLength;
        private boolean followRedirects;
        private boolean includeCredentials;
        
        public HttpRequest() {
            this.timeOut = 0;
            this.followRedirects = true;
            this.includeCredentials = false;
            this.headers = new HashMap<String, String>();
        }
        
        public HttpRequest(final String httpMethod) {
            this();
            this.httpMethod = httpMethod;
        }
        
        public void setUrl(final String url) {
            this.url = url;
        }
        
        public void setHeader(final String name, final String value) {
            this.headers.put(name, value);
        }
        
        public void setContent(final String content) {
            this.content = content;
        }
        
        public void setContent(final InputStream contentStream, final long contentLength) {
            this.contentStream = contentStream;
            this.contentLength = contentLength;
        }
        
        public void setTimeOut(final int timeOut) {
            this.timeOut = timeOut;
        }
        
        public void setFollowRedirects(final boolean followRedirects) throws IllegalArgumentException {
            if (followRedirects || Gdx.app.getType() != Application.ApplicationType.WebGL) {
                this.followRedirects = followRedirects;
                return;
            }
            throw new IllegalArgumentException("Following redirects can't be disabled using the GWT/WebGL backend!");
        }
        
        public void setIncludeCredentials(final boolean includeCredentials) {
            this.includeCredentials = includeCredentials;
        }
        
        public void setMethod(final String httpMethod) {
            this.httpMethod = httpMethod;
        }
        
        public int getTimeOut() {
            return this.timeOut;
        }
        
        public String getMethod() {
            return this.httpMethod;
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public String getContent() {
            return this.content;
        }
        
        public InputStream getContentStream() {
            return this.contentStream;
        }
        
        public long getContentLength() {
            return this.contentLength;
        }
        
        public Map<String, String> getHeaders() {
            return this.headers;
        }
        
        public boolean getFollowRedirects() {
            return this.followRedirects;
        }
        
        public boolean getIncludeCredentials() {
            return this.includeCredentials;
        }
        
        @Override
        public void reset() {
            this.httpMethod = null;
            this.url = null;
            this.headers.clear();
            this.timeOut = 0;
            this.content = null;
            this.contentStream = null;
            this.contentLength = 0L;
            this.followRedirects = true;
        }
    }
    
    public interface HttpMethods
    {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }
    
    public interface HttpResponse
    {
        byte[] getResult();
        
        String getResultAsString();
        
        InputStream getResultAsStream();
        
        HttpStatus getStatus();
        
        String getHeader(final String p0);
        
        Map<String, List<String>> getHeaders();
    }
    
    public interface HttpResponseListener
    {
        void handleHttpResponse(final HttpResponse p0);
        
        void failed(final Throwable p0);
        
        void cancelled();
    }
}

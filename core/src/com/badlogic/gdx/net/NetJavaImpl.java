// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.OutputStreamWriter;
import com.badlogic.gdx.utils.async.AsyncTask;
import java.util.Map;
import java.net.URL;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.net.HttpURLConnection;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.async.AsyncExecutor;

public class NetJavaImpl
{
    private final AsyncExecutor asyncExecutor;
    final ObjectMap<Net.HttpRequest, HttpURLConnection> connections;
    final ObjectMap<Net.HttpRequest, Net.HttpResponseListener> listeners;
    
    public NetJavaImpl() {
        this.asyncExecutor = new AsyncExecutor(1);
        this.connections = new ObjectMap<Net.HttpRequest, HttpURLConnection>();
        this.listeners = new ObjectMap<Net.HttpRequest, Net.HttpResponseListener>();
    }
    
    public void sendHttpRequest(final Net.HttpRequest httpRequest, final Net.HttpResponseListener httpResponseListener) {
        if (httpRequest.getUrl() == null) {
            httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
            return;
        }
        try {
            final String method = httpRequest.getMethod();
            URL url;
            if (method.equalsIgnoreCase("GET")) {
                String queryString = "";
                final String value = httpRequest.getContent();
                if (value != null && !"".equals(value)) {
                    queryString = "?" + value;
                }
                url = new URL(String.valueOf(httpRequest.getUrl()) + queryString);
            }
            else {
                url = new URL(httpRequest.getUrl());
            }
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            final boolean doingOutPut = method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT");
            connection.setDoOutput(doingOutPut);
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            HttpURLConnection.setFollowRedirects(httpRequest.getFollowRedirects());
            this.putIntoConnectionsAndListeners(httpRequest, httpResponseListener, connection);
            for (final Map.Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }
            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());
            this.asyncExecutor.submit((AsyncTask<Object>)new AsyncTask<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        if (doingOutPut) {
                            final String contentAsString = httpRequest.getContent();
                            if (contentAsString != null) {
                                final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                                try {
                                    writer.write(contentAsString);
                                }
                                finally {
                                    StreamUtils.closeQuietly(writer);
                                }
                                StreamUtils.closeQuietly(writer);
                            }
                            else {
                                final InputStream contentAsStream = httpRequest.getContentStream();
                                if (contentAsStream != null) {
                                    final OutputStream os = connection.getOutputStream();
                                    try {
                                        StreamUtils.copyStream(contentAsStream, os);
                                    }
                                    finally {
                                        StreamUtils.closeQuietly(os);
                                    }
                                    StreamUtils.closeQuietly(os);
                                }
                            }
                        }
                        connection.connect();
                        final HttpClientResponse clientResponse = new HttpClientResponse(connection);
                        try {
                            final Net.HttpResponseListener listener = NetJavaImpl.this.getFromListeners(httpRequest);
                            if (listener != null) {
                                listener.handleHttpResponse(clientResponse);
                            }
                            NetJavaImpl.this.removeFromConnectionsAndListeners(httpRequest);
                        }
                        finally {
                            connection.disconnect();
                        }
                        connection.disconnect();
                    }
                    catch (Exception e) {
                        connection.disconnect();
                        try {
                            httpResponseListener.failed(e);
                        }
                        finally {
                            NetJavaImpl.this.removeFromConnectionsAndListeners(httpRequest);
                        }
                        NetJavaImpl.this.removeFromConnectionsAndListeners(httpRequest);
                    }
                    return null;
                }
            });
        }
        catch (Exception e) {
            try {
                httpResponseListener.failed(e);
            }
            finally {
                this.removeFromConnectionsAndListeners(httpRequest);
            }
            this.removeFromConnectionsAndListeners(httpRequest);
        }
    }
    
    public void cancelHttpRequest(final Net.HttpRequest httpRequest) {
        final Net.HttpResponseListener httpResponseListener = this.getFromListeners(httpRequest);
        if (httpResponseListener != null) {
            httpResponseListener.cancelled();
            this.removeFromConnectionsAndListeners(httpRequest);
        }
    }
    
    synchronized void removeFromConnectionsAndListeners(final Net.HttpRequest httpRequest) {
        this.connections.remove(httpRequest);
        this.listeners.remove(httpRequest);
    }
    
    synchronized void putIntoConnectionsAndListeners(final Net.HttpRequest httpRequest, final Net.HttpResponseListener httpResponseListener, final HttpURLConnection connection) {
        this.connections.put(httpRequest, connection);
        this.listeners.put(httpRequest, httpResponseListener);
    }
    
    synchronized Net.HttpResponseListener getFromListeners(final Net.HttpRequest httpRequest) {
        final Net.HttpResponseListener httpResponseListener = this.listeners.get(httpRequest);
        return httpResponseListener;
    }
    
    static class HttpClientResponse implements Net.HttpResponse
    {
        private final HttpURLConnection connection;
        private HttpStatus status;
        
        public HttpClientResponse(final HttpURLConnection connection) throws IOException {
            this.connection = connection;
            try {
                this.status = new HttpStatus(connection.getResponseCode());
            }
            catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }
        
        @Override
        public byte[] getResult() {
            final InputStream input = this.getInputStream();
            if (input == null) {
                return StreamUtils.EMPTY_BYTES;
            }
            try {
                return StreamUtils.copyStreamToByteArray(input, this.connection.getContentLength());
            }
            catch (IOException e) {
                return StreamUtils.EMPTY_BYTES;
            }
            finally {
                StreamUtils.closeQuietly(input);
            }
        }
        
        @Override
        public String getResultAsString() {
            final InputStream input = this.getInputStream();
            if (input == null) {
                return "";
            }
            try {
                return StreamUtils.copyStreamToString(input, this.connection.getContentLength());
            }
            catch (IOException e) {
                return "";
            }
            finally {
                StreamUtils.closeQuietly(input);
            }
        }
        
        @Override
        public InputStream getResultAsStream() {
            return this.getInputStream();
        }
        
        @Override
        public HttpStatus getStatus() {
            return this.status;
        }
        
        @Override
        public String getHeader(final String name) {
            return this.connection.getHeaderField(name);
        }
        
        @Override
        public Map<String, List<String>> getHeaders() {
            return this.connection.getHeaderFields();
        }
        
        private InputStream getInputStream() {
            try {
                return this.connection.getInputStream();
            }
            catch (IOException e) {
                return this.connection.getErrorStream();
            }
        }
    }
}

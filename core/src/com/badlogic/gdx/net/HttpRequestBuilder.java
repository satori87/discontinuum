// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import com.badlogic.gdx.utils.Base64Coder;
import java.util.Map;
import java.io.InputStream;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;

public class HttpRequestBuilder
{
    public static String baseUrl;
    public static int defaultTimeout;
    public static Json json;
    private Net.HttpRequest httpRequest;
    
    static {
        HttpRequestBuilder.baseUrl = "";
        HttpRequestBuilder.defaultTimeout = 1000;
        HttpRequestBuilder.json = new Json();
    }
    
    public HttpRequestBuilder newRequest() {
        if (this.httpRequest != null) {
            throw new IllegalStateException("A new request has already been started. Call HttpRequestBuilder.build() first.");
        }
        (this.httpRequest = Pools.obtain(Net.HttpRequest.class)).setTimeOut(HttpRequestBuilder.defaultTimeout);
        return this;
    }
    
    public HttpRequestBuilder method(final String httpMethod) {
        this.validate();
        this.httpRequest.setMethod(httpMethod);
        return this;
    }
    
    public HttpRequestBuilder url(final String url) {
        this.validate();
        this.httpRequest.setUrl(String.valueOf(HttpRequestBuilder.baseUrl) + url);
        return this;
    }
    
    public HttpRequestBuilder timeout(final int timeOut) {
        this.validate();
        this.httpRequest.setTimeOut(timeOut);
        return this;
    }
    
    public HttpRequestBuilder followRedirects(final boolean followRedirects) {
        this.validate();
        this.httpRequest.setFollowRedirects(followRedirects);
        return this;
    }
    
    public HttpRequestBuilder includeCredentials(final boolean includeCredentials) {
        this.validate();
        this.httpRequest.setIncludeCredentials(includeCredentials);
        return this;
    }
    
    public HttpRequestBuilder header(final String name, final String value) {
        this.validate();
        this.httpRequest.setHeader(name, value);
        return this;
    }
    
    public HttpRequestBuilder content(final String content) {
        this.validate();
        this.httpRequest.setContent(content);
        return this;
    }
    
    public HttpRequestBuilder content(final InputStream contentStream, final long contentLength) {
        this.validate();
        this.httpRequest.setContent(contentStream, contentLength);
        return this;
    }
    
    public HttpRequestBuilder formEncodedContent(final Map<String, String> content) {
        this.validate();
        this.httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
        final String formEncodedContent = HttpParametersUtils.convertHttpParameters(content);
        this.httpRequest.setContent(formEncodedContent);
        return this;
    }
    
    public HttpRequestBuilder jsonContent(final Object content) {
        this.validate();
        this.httpRequest.setHeader("Content-Type", "application/json");
        final String jsonContent = HttpRequestBuilder.json.toJson(content);
        this.httpRequest.setContent(jsonContent);
        return this;
    }
    
    public HttpRequestBuilder basicAuthentication(final String username, final String password) {
        this.validate();
        this.httpRequest.setHeader("Authorization", "Basic " + Base64Coder.encodeString(String.valueOf(username) + ":" + password));
        return this;
    }
    
    public Net.HttpRequest build() {
        this.validate();
        final Net.HttpRequest request = this.httpRequest;
        this.httpRequest = null;
        return request;
    }
    
    private void validate() {
        if (this.httpRequest == null) {
            throw new IllegalStateException("A new request has not been started yet. Call HttpRequestBuilder.newRequest() first.");
        }
    }
}

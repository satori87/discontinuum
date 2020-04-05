// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

public class HttpParametersUtils
{
    public static String defaultEncoding;
    public static String nameValueSeparator;
    public static String parameterSeparator;
    
    static {
        HttpParametersUtils.defaultEncoding = "UTF-8";
        HttpParametersUtils.nameValueSeparator = "=";
        HttpParametersUtils.parameterSeparator = "&";
    }
    
    public static String convertHttpParameters(final Map<String, String> parameters) {
        final Set<String> keySet = parameters.keySet();
        final StringBuilder convertedParameters = new StringBuilder();
        for (final String name : keySet) {
            convertedParameters.append(encode(name, HttpParametersUtils.defaultEncoding));
            convertedParameters.append(HttpParametersUtils.nameValueSeparator);
            convertedParameters.append(encode(parameters.get(name), HttpParametersUtils.defaultEncoding));
            convertedParameters.append(HttpParametersUtils.parameterSeparator);
        }
        if (convertedParameters.length() > 0) {
            convertedParameters.deleteCharAt(convertedParameters.length() - 1);
        }
        return convertedParameters.toString();
    }
    
    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

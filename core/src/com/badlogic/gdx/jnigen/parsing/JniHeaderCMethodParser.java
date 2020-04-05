// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen.parsing;

import java.util.ArrayList;

public class JniHeaderCMethodParser implements CMethodParser
{
    private static final String C_METHOD_MARKER = "JNIEXPORT";
    
    @Override
    public CMethodParserResult parse(final String headerFile) {
        final ArrayList<CMethod> methods = new ArrayList<CMethod>();
        int index = headerFile.indexOf("JNIEXPORT");
        if (index == -1) {
            return null;
        }
        while (index >= 0) {
            final CMethod method = this.parseCMethod(headerFile, index);
            if (method == null) {
                throw new RuntimeException("Couldn't parse method");
            }
            methods.add(method);
            index = headerFile.indexOf("JNIEXPORT", method.endIndex);
        }
        return new CMethodParserResult(methods);
    }
    
    private CMethod parseCMethod(final String headerFile, final int start) {
        final int headEnd = headerFile.indexOf(40, start);
        final String head = headerFile.substring(start, headEnd).trim();
        final String returnType = head.split(" ")[1].trim();
        final int argsStart = headEnd + 1;
        final int argsEnd = headerFile.indexOf(41, argsStart);
        final String[] args = headerFile.substring(argsStart, argsEnd).split(",");
        return new CMethod(returnType, head, args, start, argsEnd + 1);
    }
}

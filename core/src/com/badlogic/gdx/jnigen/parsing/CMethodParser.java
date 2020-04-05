// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen.parsing;

import java.util.ArrayList;

public interface CMethodParser
{
    CMethodParserResult parse(final String p0);
    
    public static class CMethodParserResult
    {
        final ArrayList<CMethod> methods;
        
        public CMethodParserResult(final ArrayList<CMethod> methods) {
            this.methods = methods;
        }
        
        public ArrayList<CMethod> getMethods() {
            return this.methods;
        }
    }
    
    public static class CMethod
    {
        final String returnType;
        final String head;
        final String[] argumentTypes;
        final int startIndex;
        final int endIndex;
        
        public CMethod(final String returnType, final String head, final String[] argumentTypes, final int startIndex, final int endIndex) {
            this.returnType = returnType;
            this.head = head;
            this.argumentTypes = argumentTypes;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            for (int i = 0; i < argumentTypes.length; ++i) {
                argumentTypes[i] = argumentTypes[i].trim();
            }
        }
        
        public String getReturnType() {
            return this.returnType;
        }
        
        public String getHead() {
            return this.head;
        }
        
        public String[] getArgumentTypes() {
            return this.argumentTypes;
        }
        
        public int getStartIndex() {
            return this.startIndex;
        }
        
        public int getEndIndex() {
            return this.endIndex;
        }
    }
}

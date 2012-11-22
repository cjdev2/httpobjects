/* 
Copyright (C) 2011, 2012 Commission Junction Inc.

This file is part of httpobjects.

httpobjects is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

httpobjects is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with httpobjects; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
 */
package org.httpobjects.multipart.header;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ContentDisposition extends MimeHeaderField {
    public final String dispositionType;
    public final Map<String, String> parameters;
    public final String filename;
    
    public ContentDisposition(String name, String value) {
        super(name, value);
        
        int firstSemicolonPos = value.indexOf(';');
        dispositionType = value.substring(0,  firstSemicolonPos).trim();
        
        Map<String, String> params = readKeyValuePairs(value, firstSemicolonPos);
        
        this.filename = params.get("filename");
        
        if(filename!=null){
            params.remove("filename");
        }
        this.parameters = Collections.unmodifiableMap(params);
    }

    private Map<String, String> readKeyValuePairs(String value, int firstSemicolonPos) {
        Map<String, String> params = new HashMap<String, String>();
        ParseTool tool = new ParseTool(value, firstSemicolonPos);
        
        while(!tool.isAtEnd()){
            System.out.println(tool.current());
            String[] nextParam = tool.readNextParam();
            if(nextParam!=null){
                params.put(nextParam[0], nextParam[1]);
            }
        }
        return params;
    }
    
    public static class ParseTool {
        private final String text;
        private int pos;
        public ParseTool(String text, int pos) {
            super();
            this.text = text;
            this.pos = pos;
        }
        
        private Character current(){
            if(pos==-1 || pos>=text.length()){
                return null;
            }else{
                return text.charAt(pos);
            }
        }
        
        private Character next(){
            if(pos==-1 || pos>text.length()) throw new RuntimeException("EOL (" + pos +" of " + text.length() + ")");
            pos++;
            return current();
        }
        
        private String scanUntilSemicolonOrEol(){
            StringBuffer text = new StringBuffer();
            Character c;
            
            while(true){
                c = current();
                next();
                if(c!=null && c!=';'){
                    text.append(c);
                }else{
                    break;
                }
            }
            
            return text.toString();
        }
        
        public boolean isAtEnd(){
            return current()==null;
        }
        
        public String[] readNextParam(){
            
            String keyValue = scanUntilSemicolonOrEol().trim();
            if(!isAtEnd()) next();
            String[] parts =  keyValue.split(Pattern.quote("="));
            if(parts.length==2){
                parts[1] = stripQuotes(parts[1]);
                return parts;
            }else return null;
            
           
        }
        private String stripQuotes(String text){
            if(text.charAt(0)=='"' && text.charAt(text.length()-1) == '"'){
                return text.substring(1, text.length()-1);
            }else{
                throw new RuntimeException("Not a quoted string: " + text);
            }
        }
    }
    
    
   
}
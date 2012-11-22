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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MimePartHeader {
    
    public static List<MimeHeaderField> parseFields(String header){
        List<MimeHeaderField> headers = new ArrayList<MimeHeaderField>();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(header));
            while(true){
                String line = reader.readLine();
                if(line==null || line.isEmpty()) break;
                final int pos = line.indexOf(':');
                String name = line.substring(0, pos).trim();
                String value = line.substring(pos+1).trim();
                if(name.equals("Content-Disposition")){
                    headers.add(new ContentDisposition(name, value));
                }else{
                    headers.add(new MimeHeaderField(name, value));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return headers;
    }
    
    public final List<MimeHeaderField> fields;
    
    
    public MimePartHeader(String headerContent) {
        this(parseFields(headerContent));
    }
    
    public MimeHeaderField fieldNamed(String name){
        for(MimeHeaderField field : fields){
            if(field.name.equals(name)){
                return field;
            }
        }
        return null;
    }
    
    public MimePartHeader(List<MimeHeaderField> fields) {
        super();
        this.fields = Collections.unmodifiableList(fields);
    }
    
    public String contentType(){
        MimeHeaderField field = fieldNamed("Content-Type");
        if(field!=null){
            return field.value;
        }else return null;
    }
    
    public ContentDisposition contentDisposition(){
        for(MimeHeaderField field: fields){
            if(field instanceof ContentDisposition){
                return (ContentDisposition) field;
            }
        }
        
        return null;
    }
    
}

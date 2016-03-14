/**
 * Copyright (C) 2011, 2012 Commission Junction Inc.
 *
 * This file is part of httpobjects.
 *
 * httpobjects is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * httpobjects is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with httpobjects; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.httpobjects.jackson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.httpobjects.DSL;
import org.httpobjects.Representation;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JacksonDSL {

    public static class RepresentationConverter {

        private final Representation representation;
        private ObjectMapper objectMapper;
        private final ByteArrayOutputStream byteStream;

        private RepresentationConverter(Representation representation) {
            this.representation = representation;
            this.objectMapper = new ObjectMapper();
            this.byteStream = new ByteArrayOutputStream();
        }

        public RepresentationConverter using(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public <T> T to(Class<T> convertTo) throws IOException {
            representation.write(byteStream);
            return objectMapper.readValue(new ByteArrayInputStream(byteStream.toByteArray()), convertTo);
        }
    }

    public static RepresentationConverter convertRepresentation(Representation representation) {
        return new RepresentationConverter(representation);
    }

    public static Representation JacksonJson(final Object object, final ObjectMapper jackson) {
    	return new Representation() {
			
			@Override
			public void write(OutputStream out) {
				try {
					jackson.writeValue(out, object);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public String contentType() {
				return DSL.CONTENT_TYPE_JSON;
			}
    	};
    }
    
    public static Representation JsonStream(final Iterable<String> stream){
    	
    	return new Representation() {
    		private final ObjectMapper mapper = new ObjectMapper();
			@Override
			public void write(OutputStream out) {
				JsonGenerator generator=null;
				try{
					JsonFactory jsonFactory = mapper.getFactory();
	                generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);
	                generator.writeStartArray();
	                for(String obj:stream){
	                    generator.writeTree(mapper.readTree(obj));
	                }
	                generator.writeEndArray();
	            }catch(Exception e){
	                throw new RuntimeException(e);
	            }finally{
	                try{
	                    generator.close();
	                }catch(Exception e){/*Give Up.*/}
	            }
			}
			
			@Override
			public String contentType() {
				return DSL.CONTENT_TYPE_JSON;
			}
    	};
    }
    
    public static Representation JacksonJson(final Object object) {
    	return JacksonJson(object, new ObjectMapper());
    }
}

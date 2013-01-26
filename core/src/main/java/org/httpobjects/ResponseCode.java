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
package org.httpobjects;

import java.util.ArrayList;
import java.util.List;

public class ResponseCode {
	private static final List<ResponseCode> standardResponseCodes = new ArrayList<ResponseCode>();
	public static final ResponseCode 
			OK = new StandardResponseCode(200, "OK"), 
			CREATED = new StandardResponseCode(201, "CREATED"), 
			NO_CONTENT = new StandardResponseCode(204, "NO_CONTENT"),
			FOUND = new StandardResponseCode(302, "FOUND"), 
			SEE_OTHER = new StandardResponseCode(303, "SEE_OTHER"), 
			TEMPORARY_REDIRECT = new StandardResponseCode(307, "TEMPORARY_REDIRECT"),
			BAD_REQUEST = new StandardResponseCode(400, "BAD_REQUEST"), 
			UNAUTHORIZED = new StandardResponseCode(401, "UNAUTHORIZED"), 
			NOT_FOUND = new StandardResponseCode(404, "NOT_FOUND"), 
			METHOD_NOT_ALLOWED = new StandardResponseCode(405, "METHOD_NOT_ALLOWED"), 
			UNSUPPORTED_MEDIA_TYPE = new StandardResponseCode(415, "UNSUPPORTED_MEDIA_TYPE"),
			INTERNAL_SERVER_ERROR = new StandardResponseCode(500, "INTERNAL_SERVER_ERROR"), 
			BAD_GATEWAY = new StandardResponseCode(502, "BAD_GATEWAY");
	
	public static ResponseCode forCode(int a){
		ResponseCode code = null;
		for(ResponseCode n : standardResponseCodes){
			if(n.value == a){
				code = n;
			}
		}
		
		if(code==null){
			synchronized(standardResponseCodes){
				code = new ResponseCode(a, "Code " + a);
				standardResponseCodes.add(code);
			}
			
		}
		
		return code;
	}
	
	private final Integer value;
	private final String name;
	
	private ResponseCode(int value, String name) {
		super();
		this.value = value;
		this.name = name;
	}

	public int value() {
		return value;
	}
	
	
	
	public boolean is300Series(){
		return value >= 300 && value < 400;
	}

	public String name(){
		return name;
	}
	
	@Override
	public boolean equals(Object other) {
		return other == this;
	}
	
	private static class StandardResponseCode extends ResponseCode {

		public StandardResponseCode(int value, String name) {
			super(value, name);
			standardResponseCodes.add(this);
		}
		
	}
}
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResponseCodeTest {
    
    @Test
    public void toStringContainsTheNameAndNumber(){
        // given:
        final ResponseCode code = ResponseCode.OK;
        
        // when:
        final String text = code.toString();
        
        // then:
        assertEquals("OK(200)", text);
    }
    
	@Test
	public void theFactoryMethodShouldNotReturnNullForNonStandardValues(){
		// given:
		int aNonStandardResponseCodeValue = 34343;
		
		// when: 
		ResponseCode result = ResponseCode.forCode(aNonStandardResponseCodeValue);
		
		// then:
		assertNotNull("The object should not be null", result);
		assertEquals(result.value(), 34343);
	}
	
	@Test
	public void responseCodesFlyweightAllowsIdentityComparisons(){
		// given: a non-standard response code value
		int value = 34343;
		
		// when: obtain value object using the factory twice 
		ResponseCode a = ResponseCode.forCode(value);
		ResponseCode b = ResponseCode.forCode(value);
		
		// then: the same object should have been returned twice
		assertTrue("We should have two handles on the same object", a == b);
	}
	

	@Test
	public void nonStandardResponseCodesAreDotEqual(){
		// given: a non-standard response code value
		int value = 34343;
		
		// when: obtain value object using the factory twice 
		ResponseCode a = ResponseCode.forCode(value);
		ResponseCode b = ResponseCode.forCode(value);
		
		// then:
		assertTrue("They should be .equal()", a.equals(b));
	}

    @Test
    public void nonStandardResponseCodeIsNot300Series() {
        int value = 34343;

        ResponseCode three = ResponseCode.forCode(value);

        assertFalse("34343 should not be 300 series", three.is300Series());
    }
}

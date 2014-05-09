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
package org.httpobjects.multipart;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.fileupload.UsableMultipartStream;
import org.httpobjects.Representation;
import org.httpobjects.multipart.header.MimePartHeader;

import java.io.*;
import java.util.Map;

public class ApacheCommonsMultipartReader extends MultipartReader {

    private static byte[] readRepresentationIntoMemory(Representation r){
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            r.write(buffer);
            buffer.close();
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] boundary;
    private final UsableMultipartStream stream;

    public ApacheCommonsMultipartReader(Representation r) {
        this(r.contentType(), new ByteArrayInputStream(readRepresentationIntoMemory(r)));
    }

    public ApacheCommonsMultipartReader(String contentTypeHeaderValue, InputStream representation) {
        try {
            System.out.println(contentTypeHeaderValue);
            this.boundary = getBoundary(contentTypeHeaderValue);
            this. stream = new UsableMultipartStream(representation, boundary);
            if(!stream.skipPreamble()) throw new RuntimeException("Couldn't find a part to store");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public MimePartHeader readNextPartHeader() {
        try {
            return new MimePartHeader(stream.readHeaders());
        } catch (FileUploadBase.FileUploadIOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (MalformedStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void readNextPart(OutputStream out) {
        try {
            stream.readBodyData(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Retrieves the boundary from the <code>Content-type</code> header.
     *
     * @param contentTypeHeaderValue The value of the content type header from which to
     *                    extract the boundary value.
     *
     * @return The boundary, as a byte array.
     */
    protected byte[] getBoundary(String contentTypeHeaderValue) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String,String> params = parser.parse(contentTypeHeaderValue, ';');
        String boundaryStr = params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary;
        try {
            boundary = boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            boundary = boundaryStr.getBytes();
        }
        return boundary;
    }


}

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
package org.httpobjects.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;


public class FsTools {


    public static FileSpec file(String name,  String content){
        return new FileSpec(name, content);
    }
    public static DirSpec dir(String name,  FsNode ... children){
        return new DirSpec(name, children);
    }

    public static abstract class FsNode {
        final String name;

        public FsNode(String name) {
            super();
            this.name = name;
        }

        abstract File create(File where);
    }

    public static class DirSpec extends FsNode {
        final List<FsNode> children;

        public DirSpec(String name, FsNode ... children) {
            super(name);
            this.children = Arrays.asList(children);
        }

        File create(File where){
            if(!where.exists() && !where.mkdir()) throw new RuntimeException("Could not create directory at " + where.getAbsolutePath());

            for(FsNode child : children){
                child.create(new File(where, child.name));
            }

            return where;
        }
    }

    public static class FileSpec extends FsNode {
        final String content;

        public FileSpec(String name, String content) {
            super(name);
            this.content = content;
        }

        @Override
        File create(File where) {
            return write(content, where);
        }
    }



    private static File write(String text, File path){
        try {
            Writer w = new FileWriter(path);
            w.write(text);
            w.close();
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File tempDir(){
        File path = tempFile();
        if(!path.delete() || !path.mkdir()) throw new RuntimeException("Could not create directory at " + path.getAbsolutePath());
        return path;
    }

    private static File tempFile(){
        try {
            return File.createTempFile("whatever", ".file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

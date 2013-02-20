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

import static javax.fn.FnIO.readUrlAsLines;

import javax.fn.Fn;
import javax.fn.Seq;

public class GenerateCode {
    public static void main(String[] args) throws Exception {
        final Seq<String> lines = readUrlAsLines("http://www.w3.org/Protocols/rfc2616/rfc2616.txt");
        
        final Seq<String> items = lines.filter(new Fn<String, Boolean>(){
            @Override
            public Boolean exec(String in) {
                return in.startsWith("10.");
            }
        });
        
        final Seq<String> withLeadingNumbersRemoved = items.map(new Fn<String, String>(){
            @Override
            public String exec(String in) {
                return in.substring(in.indexOf(" ")).trim();
            }
        });
        
        final Seq<String> minusSectionHeadings = withLeadingNumbersRemoved.filter(new Fn<String, Boolean>(){
            @Override
            public Boolean exec(String in) {
                try {
                    final String text = in.substring(0, in.indexOf(' '));
                    Integer.parseInt(text);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
        
        class NamedCode {
            final String name;
            final Integer code;
            public NamedCode(String name, Integer code) {
                super();
                this.name = name;
                this.code = code;
            }
            
        }
        
        final Seq<NamedCode> parsed = minusSectionHeadings.map(new Fn<String, NamedCode>(){
            @Override
            public NamedCode exec(String in) {
                final int idx = in.indexOf(' ');
                return new NamedCode(
                            in.substring(idx).trim(),
                            Integer.parseInt(in.substring(0, idx))
                        );
            }
        });
        
        final Seq<NamedCode> usedCodes = parsed.filter(new Fn<NamedCode, Boolean>(){
            public Boolean exec(NamedCode in) {
                return !in.name.equals("(Unused)");
            };
        });
        
        final String standardReponseCodes  = usedCodes.map(new Fn<NamedCode, String>(){
            @Override
            public String exec(NamedCode in) {
                final String name = in.name.toUpperCase().replaceAll(" ", "_").replaceAll("-", "_");
                
                return "    " + name + " = new StandardResponseCode(" + in.code + ", \"" + name + "\")";
            }
        }).mkstring(",\n").toString() + ";";
        
        System.out.println(standardReponseCodes);
        
        final String dslMethods  = usedCodes.map(new Fn<NamedCode, String>(){
            @Override
            public String exec(NamedCode in) {
                final String name = in.name.toUpperCase().replaceAll(" ", "_").replaceAll("-", "_");
                
                final int series = in.code / 100;
                
                if(series==1){
                    return "public static final Response " + name + "(){\n" +
                    		"    return new Response(ResponseCode." + name + ", null);\n" +
                    		"}";
                }else if(series==4){
                    return "public static final Response " + name + "(){\n" +
                            "    return new Response(ResponseCode." + name + ", Text(\"" + in.code + " Client Error: " + in.name + "\"));\n" +
                            "}\n" + 
                            "public static final Response " + name + "(Representation representation){\n" +
                            "    return new Response(ResponseCode." + name + ", representation);\n" +
                            "}";
                }else if(series==5){
                    return "public static final Response " + name + "(){\n" +
                            "    return new Response(ResponseCode." + name + ", Text(\"" + in.code + " Server Error: " + in.name + "\"));\n" +
                            "}\n" + 
                            "public static final Response " + name + "(Representation representation){\n" +
                            "    return new Response(ResponseCode." + name + ", representation);\n" +
                            "}\n" + 
                            "public static final Response " + name + "(Throwable t){\n" +
                            "    return new Response(ResponseCode." + name + ", Text(toString(t)));\n" +
                            "}";
                }else {
                    return "";
                }
            }
        }).mkstring("\n").toString() + ";";
        
        System.out.println(dslMethods);
    }
    

}

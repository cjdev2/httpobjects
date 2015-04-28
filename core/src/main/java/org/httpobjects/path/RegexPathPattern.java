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
package org.httpobjects.path;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPathPattern implements PathPattern {
    private final Pattern pattern;
    private final List<PathParamName> varNames;

    public RegexPathPattern(Pattern pattern, String ... varNames) {
        super();
        this.pattern = pattern;
        
        List<String> numberNames = toStrings(pattern.matcher("").groupCount());
        
        this.varNames = unmodifiableList(toNames(varNames.length==0?numberNames:Arrays.asList(varNames)));
    }
    @Override
    public String raw() {
        return pattern.pattern();
    }
    
    
    private static List<PathParamName> toNames(List<String> strings) {
        List<PathParamName> names = new ArrayList<PathParamName>(strings.size());
        for(String n : strings){
            names.add(new PathParamName(n));
        }
        return names;
    }
    
    private List<String> toStrings(int n) {
        List<String> results = new ArrayList<String>(n);
        for(int x=0;x<n;x++){
            results.add(Integer.toString(x));
        }
        return results;
    }


    @Override
    public Path match(String path) {
        final Matcher m = pattern.matcher(path);
        if(m.find()){
            PathParam[] params = new PathParam[(varNames.size())];
            for(int x=0;x<varNames.size();x++){
                params[x] = new PathParam(varNames.get(x), m.group(x+1));
            }
            return new Path(path, params);
        }else{
            return null;
        }
    }
    
    @Override
    public boolean matches(String path) {
        return pattern.matcher(path).matches();
    }
    @Override
    public List<PathParamName> varNames() {
        return varNames;
    }
}

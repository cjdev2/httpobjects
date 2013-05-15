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
package org.httpobjects.impl.fn;

import java.util.Collections;
import java.util.Iterator;

public class FunctionalJava {

    public static <T> Seq<T> emptySeq(){
        return asSeq(Collections.<T>emptyList());
    }
    public static <T> Seq<T> asSeq(final Iterable<T> iterable){
        return new AbstractSeq<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }
    
    public static <T> Seq<T> filter(final Iterable<T> strings, final Fn<T, Boolean> fn) {
        return new AbstractSeq<T>(){
            @Override
            public Iterator<T> iterator() {
                final Iterator<T> i = strings.iterator();
                
                return new ImmutableItererator<T>(){
                    boolean isStale = true;
                    T value;
                    @Override
                    public boolean hasNext() {
                        freshen();
                        return value!=null;
                    }
                    
                    private void freshen(){
                        if(isStale){
                            boolean foundOne = false;
                            
                            while(i.hasNext() && !foundOne){
                                value = i.next();
                                foundOne = fn.exec(value);   
                            }
                            
                            if(!foundOne){
                                value = null;
                            }
                            isStale = false;
                        }
                    }
                    
                    @Override
                    public T next() {
                        freshen();
                        isStale = true;
                        return value;
                    }
                    
                };
            }
        };
    }
    
    public static <T, V> Seq<V> map(final Iterable<T> strings, final Fn<T, V> fn) {
        return new AbstractSeq<V>(){
            @Override
            public Iterator<V> iterator() {
                final Iterator<T> i = strings.iterator();
                
                return new ImmutableItererator<V>(){

                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    
                    @Override
                    public V next() {
                        return fn.exec(i.next());
                    }
                    
                };
            }
        };
    }
    
    public static <T> void foreach(Iterable<T> strings, Fn<T, Void> fn) {
        Iterator<T> i = strings.iterator();
        while(i.hasNext()){
            fn.exec(i.next());
        }
    }
    
    protected static abstract class ImmutableItererator<T> implements Iterator<T> {
        @Override
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

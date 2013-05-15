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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSeq<T> implements Seq<T>{
    public <O> Seq<O> map(Fn<T, O> fn){
        return FunctionalJava.<T, O>map(this, fn);
    }
    public Seq<T> filter(Fn<T, Boolean> fn){
        return FunctionalJava.filter(this, fn);
    }
    public void foreach(Fn<T, Void> fn){
        FunctionalJava.foreach(this, fn);
    }
    @Override
    public Seq<T> plus(final Seq<? extends T> other) {
        final Seq<T> me = this;
        return new AbstractSeq<T>(){
            @Override
            public Iterator<T> iterator() {
                return new CompositeIterator<T>(me.iterator(), other.iterator());
            }
        };
    }
    
    @Override
    public List<T> toList() {
        final List<T> list = new ArrayList<T>();
        final Iterator<T> i = iterator();
        
        while(i.hasNext()){
            list.add(i.next());
        }
        
        return list;
    }
    
    @SuppressWarnings("unused")
    private class CompositeIterator<T> implements Iterator<T> {
        private final Iterator<? extends T> first;
        private final Iterator<? extends T> last;
        
        public CompositeIterator(Iterator<? extends T> first, Iterator<? extends T> last) {
            super();
            this.first = first;
            this.last = last;
        }

        private Iterator<? extends T> nextIterator(){
            if(first.hasNext()){
                return first;
            }else{
                return last;
            }
        }
        
        @Override
        public boolean hasNext() {
            return nextIterator().hasNext();
        }
        @Override
        public T next() {
            return nextIterator().next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public CharSequence mkstring(final String separator) {
        final StringBuilder text = new StringBuilder();
        
        this.foreach(new Fn<T, Void>(){
            boolean isFirst = true;
            public Void exec(T in) {
                if(isFirst){
                    isFirst=false;
                }else{
                    text.append(separator);
                }
                text.append(in.toString());
                return null;
            };
        });
        
        return text;
    }
}
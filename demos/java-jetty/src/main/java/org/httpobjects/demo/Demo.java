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
package org.httpobjects.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.httpobjects.DSL;
import org.httpobjects.HttpObject;
import org.httpobjects.Representation;
import org.httpobjects.Representation.Chunk;
import org.httpobjects.Stream.Scanner;
import org.httpobjects.Stream.Transformer;
import org.httpobjects.jetty.HttpObjectsJettyHandler;
import org.httpobjects.servlet.ServletFilter;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;

public class Demo {
	
	public static void collectDemo(){
		Representation r = DSL.Text("foo");
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		r.bytes().scan(new Scanner<Chunk>() {
			
			@Override
			public void collect(Chunk nextChunk) {
				nextChunk.writeInto(out);
			}
		});
		
		Demo demo = r.bytes().read(jackson(Demo.class)).get();
	}
	

	static <T> Transformer<Chunk, T> jackson(Class<? extends T> clazz){
		return null;
	}
	
	static class JacksonAccumulator<T> implements Transformer<Chunk, T> {
		JacksonAccumulator(Class<? extends T> clazz){
			
		}

		@Override
		public void collect(Chunk nextChunk) {
			throw new RuntimeException("NOT IMLEMENTED");
		}

		@Override
		public T finalResult() {
			throw new RuntimeException("NOT IMLEMENTED");
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		final boolean useJetty = true;
		
		HttpObject[] objects = {
				new CMSResources(new File(System.getProperty("user.dir"))),
				new PersonResource(),
				new Favicon()
		};
		
		if(useJetty){
			serveViaJettyHandler(objects);
		}else{
			serveViaServletFilter(objects);
		}
	}
	
	
	public static class Person {
		String name = "Stu";
		String[] aliases = {"Stew", "schtugh"};
		String birthdate = "2011-01-01";
		
		public String getName() {
			return name;
		}
		public String[] getAliases() {
			return aliases;
		}
		public String getBirthdate() {
			return birthdate;
		}
	}
	private static void serveViaJettyHandler(HttpObject[] objects) {
		HttpObjectsJettyHandler.launchServer(8080, objects);
	}

	private static void serveViaServletFilter(HttpObject ... objects) throws Exception {
		Server s = new Server(8080);

		ServletHandler servletHandler = new ServletHandler();
		ServletFilter filter = new ServletFilter(objects);
		servletHandler.addFilterWithMapping(new FilterHolder(filter), "/*", Handler.DEFAULT);
		servletHandler.addServletWithMapping(WelcomeServlet.class, "/*");
		s.setHandler(servletHandler);
		
		s.start();
	}
	
	@SuppressWarnings("serial")
	public static class WelcomeServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getWriter().write("Default Content");
		}
	}
}

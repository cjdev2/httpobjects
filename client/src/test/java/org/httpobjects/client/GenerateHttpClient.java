package org.httpobjects.client;

import java.io.PrintStream;

import org.httpobjects.Representation;
import org.httpobjects.Response;
import org.httpobjects.header.HeaderField;
import org.httpobjects.util.Method;

public class GenerateHttpClient {
	public static void main(String[] args) {
		final PrintStream out = System.out;
		
		out.println("package org.httpobjects.client;");
		out.println("");
		out.println("import " + HeaderField.class.getName() + ";");
		out.println("import " + Response.class.getName() + ";");
		out.println("import " + Representation.class.getName() + ";");
		out.println("");
		out.println(
				  "/**\n" + 
				  "* WARNING: This API is highly experimental.  This means it should be considered unstable at present; if you build on it, you may have to change your code a lot going forward.\n" + 
				  "*/");
		out.println("public interface HttpClient {\n"
				+ "    RemoteObject resource(String uri);\n"
				+ "\n"
				+ "    public abstract class RemoteObject {\n"
				+ "");

		
		
        for(Method m: Method.values()){
        	final String name = m.name().toLowerCase();
        	
        	out.println("        public abstract Response method(Representation r, String query, HeaderField ... fields);".replaceAll("method", name));
        }

        out.println("");
        out.println("");
        
		final String convenienceBody = ""
				+ "        /*\n"
				+ "         * Convenience methods for 'METHOD'\n"
				+ "         */\n"
				+ "\n" 
    			+ "        public final Response method(HeaderField ... fields){\n"
    			+ "            return this.method(null, \"\", fields);\n"
    			+ "        }\n"
    			+ "        public final Response method(String query, HeaderField ... fields){\n"
    			+ "            return this.method(null, query, fields);\n"
    			+ "        }\n"
    			+ "        public final Response method(Representation r, HeaderField ... fields){\n"
    			+ "            return this.method(r, \"\", fields);\n"
    			+ "        }\n"
    			;
        for(Method m: Method.values()){
        	final String name = m.name();
        	
        	out.println(convenienceBody
        					.replaceAll("method\\(", name.toLowerCase() + "(")
        					.replaceAll("METHOD", name.toUpperCase()));
        }
        
        out.println("    }");
		out.println("}");
	}
}

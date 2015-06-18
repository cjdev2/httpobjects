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

		
		
		
		final String convenienceBody = "" 
    			+ "        public Response method(HeaderField ... fields){\n"
    			+ "            return this.method(\"\", null, fields);\n"
    			+ "        }\n"
    			+ "        public Response method(String query, HeaderField ... fields){\n"
    			+ "            return this.method(query, null, fields);\n"
    			+ "        }\n"
    			+ "        public Response method(Representation r, HeaderField ... fields){\n"
    			+ "            return this.method(\"\", r, fields);\n"
    			+ "        }\n"
    			+ "        public abstract Response method(String query, Representation r, HeaderField ... fields);"
    			;
        for(Method m: Method.values()){
        	final String name = m.name().toLowerCase();
        	
        	out.println(convenienceBody.replaceAll("method", name));
        }
        out.println("    }");
		out.println("}");
	}
}

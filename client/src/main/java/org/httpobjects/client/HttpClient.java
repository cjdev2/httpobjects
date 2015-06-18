package org.httpobjects.client;

import org.httpobjects.header.HeaderField;
import org.httpobjects.Response;
import org.httpobjects.Representation;

/**
* WARNING: This API is highly experimental.  This means it should be considered unstable at present; if you build on it, you may have to change your code a lot going forward.
*/
public interface HttpClient {
    RemoteObject resource(String uri);

    public abstract class RemoteObject {

        public Response get(HeaderField ... fields){
            return this.get("", null, fields);
        }
        public Response get(String query, HeaderField ... fields){
            return this.get(query, null, fields);
        }
        public Response get(Representation r, HeaderField ... fields){
            return this.get("", r, fields);
        }
        public abstract Response get(String query, Representation r, HeaderField ... fields);
        public Response post(HeaderField ... fields){
            return this.post("", null, fields);
        }
        public Response post(String query, HeaderField ... fields){
            return this.post(query, null, fields);
        }
        public Response post(Representation r, HeaderField ... fields){
            return this.post("", r, fields);
        }
        public abstract Response post(String query, Representation r, HeaderField ... fields);
        public Response put(HeaderField ... fields){
            return this.put("", null, fields);
        }
        public Response put(String query, HeaderField ... fields){
            return this.put(query, null, fields);
        }
        public Response put(Representation r, HeaderField ... fields){
            return this.put("", r, fields);
        }
        public abstract Response put(String query, Representation r, HeaderField ... fields);
        public Response delete(HeaderField ... fields){
            return this.delete("", null, fields);
        }
        public Response delete(String query, HeaderField ... fields){
            return this.delete(query, null, fields);
        }
        public Response delete(Representation r, HeaderField ... fields){
            return this.delete("", r, fields);
        }
        public abstract Response delete(String query, Representation r, HeaderField ... fields);
        public Response patch(HeaderField ... fields){
            return this.patch("", null, fields);
        }
        public Response patch(String query, HeaderField ... fields){
            return this.patch(query, null, fields);
        }
        public Response patch(Representation r, HeaderField ... fields){
            return this.patch("", r, fields);
        }
        public abstract Response patch(String query, Representation r, HeaderField ... fields);
        public Response head(HeaderField ... fields){
            return this.head("", null, fields);
        }
        public Response head(String query, HeaderField ... fields){
            return this.head(query, null, fields);
        }
        public Response head(Representation r, HeaderField ... fields){
            return this.head("", r, fields);
        }
        public abstract Response head(String query, Representation r, HeaderField ... fields);
        public Response options(HeaderField ... fields){
            return this.options("", null, fields);
        }
        public Response options(String query, HeaderField ... fields){
            return this.options(query, null, fields);
        }
        public Response options(Representation r, HeaderField ... fields){
            return this.options("", r, fields);
        }
        public abstract Response options(String query, Representation r, HeaderField ... fields);
    }
}

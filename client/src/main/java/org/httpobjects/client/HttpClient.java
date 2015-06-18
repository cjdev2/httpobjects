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

        public abstract Response get(Representation r, String query, HeaderField ... fields);
        public abstract Response post(Representation r, String query, HeaderField ... fields);
        public abstract Response put(Representation r, String query, HeaderField ... fields);
        public abstract Response delete(Representation r, String query, HeaderField ... fields);
        public abstract Response patch(Representation r, String query, HeaderField ... fields);
        public abstract Response head(Representation r, String query, HeaderField ... fields);
        public abstract Response options(Representation r, String query, HeaderField ... fields);


        /**
         * Convenience methods for 'GET'
         */

        public Response get(HeaderField ... fields){
            return this.get(null, "", fields);
        }
        public Response get(String query, HeaderField ... fields){
            return this.get(null, query, fields);
        }
        public Response get(Representation r, HeaderField ... fields){
            return this.get(r, "", fields);
        }

        /**
         * Convenience methods for 'POST'
         */

        public Response post(HeaderField ... fields){
            return this.post(null, "", fields);
        }
        public Response post(String query, HeaderField ... fields){
            return this.post(null, query, fields);
        }
        public Response post(Representation r, HeaderField ... fields){
            return this.post(r, "", fields);
        }

        /**
         * Convenience methods for 'PUT'
         */

        public Response put(HeaderField ... fields){
            return this.put(null, "", fields);
        }
        public Response put(String query, HeaderField ... fields){
            return this.put(null, query, fields);
        }
        public Response put(Representation r, HeaderField ... fields){
            return this.put(r, "", fields);
        }

        /**
         * Convenience methods for 'DELETE'
         */

        public Response delete(HeaderField ... fields){
            return this.delete(null, "", fields);
        }
        public Response delete(String query, HeaderField ... fields){
            return this.delete(null, query, fields);
        }
        public Response delete(Representation r, HeaderField ... fields){
            return this.delete(r, "", fields);
        }

        /**
         * Convenience methods for 'PATCH'
         */

        public Response patch(HeaderField ... fields){
            return this.patch(null, "", fields);
        }
        public Response patch(String query, HeaderField ... fields){
            return this.patch(null, query, fields);
        }
        public Response patch(Representation r, HeaderField ... fields){
            return this.patch(r, "", fields);
        }

        /**
         * Convenience methods for 'HEAD'
         */

        public Response head(HeaderField ... fields){
            return this.head(null, "", fields);
        }
        public Response head(String query, HeaderField ... fields){
            return this.head(null, query, fields);
        }
        public Response head(Representation r, HeaderField ... fields){
            return this.head(r, "", fields);
        }

        /**
         * Convenience methods for 'OPTIONS'
         */

        public Response options(HeaderField ... fields){
            return this.options(null, "", fields);
        }
        public Response options(String query, HeaderField ... fields){
            return this.options(null, query, fields);
        }
        public Response options(Representation r, HeaderField ... fields){
            return this.options(r, "", fields);
        }

    }
}

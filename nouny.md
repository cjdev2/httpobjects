http objects is "nouny"
-------------

httpobjects has been said to be 'nouny', as opposed to other, more 'verby' http abstractions.

For example, a 'verby' api might look like this:

    serve(
      GET + "/some/path" -> {request=>
        OK(Text("foo"))
      }
      POST + "/some/path" -> {request=>
        OK(Text("bar"))
      }
    )

In contrast, httpobjects uses an object (i.e. a 'noun') to encapsulate the path with the methods:

    serve(
      new HttpObject("/some/path"){
        override def get(request:Request) = OK(Text("foo"))
        override def post(request:Request) = OK(Text("bar"))
      }
    )


Note that there are tradeoffs in choosing one or the other:
   
  - The 'verby' api 
     - is focused on "routing" requests to functions
     - de-couples the identity of the http resource from it's related method-handling functions
  - The 'nouny' api
     - is focused on implementing http resources
     - binds an identity to all the related method-handling functions

Yes, it's nouny.  But Why?
-----------

Q: In our new functional age, isn't that a little out-dated?  I mean, even java (8) supports first class functions!

A: The primary reason that httpobjects is 'nouny' is that the http spec/protocol itself is nouny:
   - it views the world as a collection of resources (i.e. many nouns)
   - each resource responds to the same, limited set of methods (i.e. just a few verbs)

So, given that httpobjects strives to be the best representation of the http spec, the 'nouny' approach seems best.

A secondary consideration is java6 compatibility; given that one of the design goals is to support java6, a highly 'verby' api would pose some problems.  These are not insurmountable, but they would complicate the API, and, in the end, the reasoning above (that the spec itself is 'nouny') makes this a non-issue.


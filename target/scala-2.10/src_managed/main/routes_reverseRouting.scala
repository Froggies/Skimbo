// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:0db3a9eaaf9303eee8f28ddb45447bb6ccb672b4
// @DATE:Sat Apr 06 15:51:13 CEST 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString


// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:13
package controllers.stream {

// @LINE:18
// @LINE:17
// @LINE:16
class ReverseSse {
    

// @LINE:18
def ping(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/ping")
}
                                                

// @LINE:17
def command(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "api/stream/command")
}
                                                

// @LINE:16
def connect(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/sse")
}
                                                
    
}
                          

// @LINE:13
class ReverseWebSocket {
    

// @LINE:13
def connect(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/webSocket")
}
                                                
    
}
                          
}
                  

// @LINE:21
// @LINE:6
// @LINE:5
// @LINE:4
package controllers.api {

// @LINE:21
class ReverseStats {
    

// @LINE:21
def get(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stats")
}
                                                
    
}
                          

// @LINE:6
// @LINE:5
// @LINE:4
class ReverseProviders {
    

// @LINE:5
def listServices(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/services")
}
                                                

// @LINE:4
def listAll(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/list")
}
                                                

// @LINE:6
def delete(provider:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/del/" + implicitly[PathBindable[String]].unbind("provider", provider))
}
                                                
    
}
                          
}
                  

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
package controllers.dev {

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
class ReverseUtil {
    

// @LINE:26
def staticRes(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/staticRes")
}
                                                

// @LINE:25
def testSkimboRes(service:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/testSkimboRes/" + implicitly[PathBindable[String]].unbind("service", service))
}
                                                

// @LINE:31
def invalidToken(p:String, pwd:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/invalidToken/" + implicitly[PathBindable[String]].unbind("p", p) + "/" + implicitly[PathBindable[String]].unbind("pwd", pwd))
}
                                                

// @LINE:24
def testRes(service:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/testRes/" + implicitly[PathBindable[String]].unbind("service", service))
}
                                                

// @LINE:32
def urlTest(id:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/urlTest/" + implicitly[PathBindable[String]].unbind("id", id))
}
                                                

// @LINE:30
def userDistantRes(p:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/userDistant/" + implicitly[PathBindable[String]].unbind("p", p))
}
                                                

// @LINE:27
def deleteUser(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/delDb")
}
                                                

// @LINE:28
def deleteAllUsers(pwd:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/delAllDb/" + implicitly[PathBindable[String]].unbind("pwd", pwd))
}
                                                

// @LINE:29
def userRes(p:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/userRes/" + implicitly[PathBindable[String]].unbind("p", p))
}
                                                
    
}
                          
}
                  

// @LINE:35
// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
package controllers {

// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:7
def logout(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "logout")
}
                                                

// @LINE:2
def jsRouter(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "jsRouter")
}
                                                

// @LINE:10
def authenticate(provider:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "auth/" + implicitly[PathBindable[String]].unbind("provider", provider))
}
                                                

// @LINE:1
def index(): Call = {
   Call("GET", _prefix)
}
                                                

// @LINE:9
def closePopup(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "auth/end")
}
                                                
    
}
                          

// @LINE:35
class ReverseAssets {
    

// @LINE:35
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          
}
                  


// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:13
package controllers.stream.javascript {

// @LINE:18
// @LINE:17
// @LINE:16
class ReverseSse {
    

// @LINE:18
def ping : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.ping",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/ping"})
      }
   """
)
                        

// @LINE:17
def command : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.command",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/command"})
      }
   """
)
                        

// @LINE:16
def connect : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.connect",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/sse"})
      }
   """
)
                        
    
}
              

// @LINE:13
class ReverseWebSocket {
    

// @LINE:13
def connect : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.WebSocket.connect",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/webSocket"})
      }
   """
)
                        
    
}
              
}
        

// @LINE:21
// @LINE:6
// @LINE:5
// @LINE:4
package controllers.api.javascript {

// @LINE:21
class ReverseStats {
    

// @LINE:21
def get : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Stats.get",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stats"})
      }
   """
)
                        
    
}
              

// @LINE:6
// @LINE:5
// @LINE:4
class ReverseProviders {
    

// @LINE:5
def listServices : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.listServices",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/services"})
      }
   """
)
                        

// @LINE:4
def listAll : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.listAll",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/list"})
      }
   """
)
                        

// @LINE:6
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.delete",
   """
      function(provider) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/del/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("provider", provider)})
      }
   """
)
                        
    
}
              
}
        

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
package controllers.dev.javascript {

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
class ReverseUtil {
    

// @LINE:26
def staticRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.staticRes",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/staticRes"})
      }
   """
)
                        

// @LINE:25
def testSkimboRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.testSkimboRes",
   """
      function(service) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/testSkimboRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("service", service)})
      }
   """
)
                        

// @LINE:31
def invalidToken : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.invalidToken",
   """
      function(p,pwd) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/invalidToken/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", p) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("pwd", pwd)})
      }
   """
)
                        

// @LINE:24
def testRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.testRes",
   """
      function(service) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/testRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("service", service)})
      }
   """
)
                        

// @LINE:32
def urlTest : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.urlTest",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/urlTest/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:30
def userDistantRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.userDistantRes",
   """
      function(p) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/userDistant/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", p)})
      }
   """
)
                        

// @LINE:27
def deleteUser : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.deleteUser",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/delDb"})
      }
   """
)
                        

// @LINE:28
def deleteAllUsers : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.deleteAllUsers",
   """
      function(pwd) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/delAllDb/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("pwd", pwd)})
      }
   """
)
                        

// @LINE:29
def userRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.userRes",
   """
      function(p) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/userRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", p)})
      }
   """
)
                        
    
}
              
}
        

// @LINE:35
// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
package controllers.javascript {

// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:7
def logout : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.logout",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "logout"})
      }
   """
)
                        

// @LINE:2
def jsRouter : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.jsRouter",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "jsRouter"})
      }
   """
)
                        

// @LINE:10
def authenticate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.authenticate",
   """
      function(provider) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "auth/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("provider", provider)})
      }
   """
)
                        

// @LINE:1
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        

// @LINE:9
def closePopup : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.closePopup",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "auth/end"})
      }
   """
)
                        
    
}
              

// @LINE:35
class ReverseAssets {
    

// @LINE:35
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        
    
}
              
}
        


// @LINE:18
// @LINE:17
// @LINE:16
// @LINE:13
package controllers.stream.ref {

// @LINE:18
// @LINE:17
// @LINE:16
class ReverseSse {
    

// @LINE:18
def ping(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.ping(), HandlerDef(this, "controllers.stream.Sse", "ping", Seq(), "GET", """""", _prefix + """api/stream/ping""")
)
                      

// @LINE:17
def command(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.command(), HandlerDef(this, "controllers.stream.Sse", "command", Seq(), "POST", """""", _prefix + """api/stream/command""")
)
                      

// @LINE:16
def connect(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.connect(), HandlerDef(this, "controllers.stream.Sse", "connect", Seq(), "GET", """ Streams -- Sse""", _prefix + """api/stream/sse""")
)
                      
    
}
                          

// @LINE:13
class ReverseWebSocket {
    

// @LINE:13
def connect(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.WebSocket.connect(), HandlerDef(this, "controllers.stream.WebSocket", "connect", Seq(), "GET", """ Streams -- websocket""", _prefix + """api/stream/webSocket""")
)
                      
    
}
                          
}
                  

// @LINE:21
// @LINE:6
// @LINE:5
// @LINE:4
package controllers.api.ref {

// @LINE:21
class ReverseStats {
    

// @LINE:21
def get(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Stats.get(), HandlerDef(this, "controllers.api.Stats", "get", Seq(), "GET", """ Stats""", _prefix + """api/stats""")
)
                      
    
}
                          

// @LINE:6
// @LINE:5
// @LINE:4
class ReverseProviders {
    

// @LINE:5
def listServices(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.listServices(), HandlerDef(this, "controllers.api.Providers", "listServices", Seq(), "GET", """""", _prefix + """api/providers/services""")
)
                      

// @LINE:4
def listAll(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.listAll(), HandlerDef(this, "controllers.api.Providers", "listAll", Seq(), "GET", """""", _prefix + """api/providers/list""")
)
                      

// @LINE:6
def delete(provider:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.delete(provider), HandlerDef(this, "controllers.api.Providers", "delete", Seq(classOf[String]), "GET", """""", _prefix + """api/providers/del/$provider<[^/]+>""")
)
                      
    
}
                          
}
                  

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
package controllers.dev.ref {

// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
class ReverseUtil {
    

// @LINE:26
def staticRes(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.staticRes(), HandlerDef(this, "controllers.dev.Util", "staticRes", Seq(), "GET", """""", _prefix + """api/dev/util/staticRes""")
)
                      

// @LINE:25
def testSkimboRes(service:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.testSkimboRes(service), HandlerDef(this, "controllers.dev.Util", "testSkimboRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/testSkimboRes/$service<[^/]+>""")
)
                      

// @LINE:31
def invalidToken(p:String, pwd:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.invalidToken(p, pwd), HandlerDef(this, "controllers.dev.Util", "invalidToken", Seq(classOf[String], classOf[String]), "GET", """""", _prefix + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>""")
)
                      

// @LINE:24
def testRes(service:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.testRes(service), HandlerDef(this, "controllers.dev.Util", "testRes", Seq(classOf[String]), "GET", """ Dev util""", _prefix + """api/dev/util/testRes/$service<[^/]+>""")
)
                      

// @LINE:32
def urlTest(id:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.urlTest(id), HandlerDef(this, "controllers.dev.Util", "urlTest", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/urlTest/$id<[^/]+>""")
)
                      

// @LINE:30
def userDistantRes(p:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.userDistantRes(p), HandlerDef(this, "controllers.dev.Util", "userDistantRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/userDistant/$p<[^/]+>""")
)
                      

// @LINE:27
def deleteUser(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.deleteUser(), HandlerDef(this, "controllers.dev.Util", "deleteUser", Seq(), "GET", """""", _prefix + """api/dev/util/delDb""")
)
                      

// @LINE:28
def deleteAllUsers(pwd:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.deleteAllUsers(pwd), HandlerDef(this, "controllers.dev.Util", "deleteAllUsers", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/delAllDb/$pwd<[^/]+>""")
)
                      

// @LINE:29
def userRes(p:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.userRes(p), HandlerDef(this, "controllers.dev.Util", "userRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/userRes/$p<[^/]+>""")
)
                      
    
}
                          
}
                  

// @LINE:35
// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
package controllers.ref {

// @LINE:10
// @LINE:9
// @LINE:7
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:7
def logout(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.logout(), HandlerDef(this, "controllers.Application", "logout", Seq(), "GET", """""", _prefix + """logout""")
)
                      

// @LINE:2
def jsRouter(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.jsRouter(), HandlerDef(this, "controllers.Application", "jsRouter", Seq(), "GET", """""", _prefix + """jsRouter""")
)
                      

// @LINE:10
def authenticate(provider:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.authenticate(provider), HandlerDef(this, "controllers.Application", "authenticate", Seq(classOf[String]), "GET", """""", _prefix + """auth/$provider<[^/]+>""")
)
                      

// @LINE:1
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq(), "GET", """""", _prefix + """""")
)
                      

// @LINE:9
def closePopup(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.closePopup(), HandlerDef(this, "controllers.Application", "closePopup", Seq(), "GET", """""", _prefix + """auth/end""")
)
                      
    
}
                          

// @LINE:35
class ReverseAssets {
    

// @LINE:35
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          
}
                  
      
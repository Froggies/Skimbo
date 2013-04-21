// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:4307607327691009738fbca39b56ffd3eea52d52
// @DATE:Sun Apr 21 19:41:42 CEST 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._
import java.net.URLEncoder

import play.api.mvc._


import Router.queryString


// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:15
// @LINE:14
package controllers.stream {

// @LINE:20
// @LINE:19
// @LINE:18
class ReverseSse {
    

// @LINE:20
def ping(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/ping")
}
                                                

// @LINE:19
def command(): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "api/stream/command")
}
                                                

// @LINE:18
def connect(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/sse")
}
                                                
    
}
                          

// @LINE:15
// @LINE:14
class ReverseWebSocket {
    

// @LINE:15
def connectPublicPage(publicPage:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/webSocket/" + implicitly[PathBindable[String]].unbind("publicPage", URLEncoder.encode(publicPage, "utf-8")))
}
                                                

// @LINE:14
def connect(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stream/webSocket")
}
                                                
    
}
                          
}
                  

// @LINE:23
// @LINE:7
// @LINE:6
// @LINE:5
package controllers.api {

// @LINE:23
class ReverseStats {
    

// @LINE:23
def get(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/stats")
}
                                                
    
}
                          

// @LINE:7
// @LINE:6
// @LINE:5
class ReverseProviders {
    

// @LINE:6
def listServices(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/services")
}
                                                

// @LINE:5
def listAll(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/list")
}
                                                

// @LINE:7
def delete(provider:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/providers/del/" + implicitly[PathBindable[String]].unbind("provider", URLEncoder.encode(provider, "utf-8")))
}
                                                
    
}
                          
}
                  

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
package controllers.dev {

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
class ReverseUtil {
    

// @LINE:28
def staticRes(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/staticRes")
}
                                                

// @LINE:27
def testSkimboRes(service:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/testSkimboRes/" + implicitly[PathBindable[String]].unbind("service", URLEncoder.encode(service, "utf-8")))
}
                                                

// @LINE:33
def invalidToken(p:String, pwd:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/invalidToken/" + implicitly[PathBindable[String]].unbind("p", URLEncoder.encode(p, "utf-8")) + "/" + implicitly[PathBindable[String]].unbind("pwd", URLEncoder.encode(pwd, "utf-8")))
}
                                                

// @LINE:26
def testRes(service:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/testRes/" + implicitly[PathBindable[String]].unbind("service", URLEncoder.encode(service, "utf-8")))
}
                                                

// @LINE:34
def urlTest(id:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/urlTest/" + implicitly[PathBindable[String]].unbind("id", URLEncoder.encode(id, "utf-8")))
}
                                                

// @LINE:32
def userDistantRes(p:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/userDistant/" + implicitly[PathBindable[String]].unbind("p", URLEncoder.encode(p, "utf-8")))
}
                                                

// @LINE:29
def deleteUser(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/delDb")
}
                                                

// @LINE:30
def deleteAllUsers(pwd:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/delAllDb/" + implicitly[PathBindable[String]].unbind("pwd", URLEncoder.encode(pwd, "utf-8")))
}
                                                

// @LINE:31
def userRes(p:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "api/dev/util/userRes/" + implicitly[PathBindable[String]].unbind("p", URLEncoder.encode(p, "utf-8")))
}
                                                
    
}
                          
}
                  

// @LINE:39
// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
package controllers {

// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:2
def publicPage(namePage:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "p/" + implicitly[PathBindable[String]].unbind("namePage", URLEncoder.encode(namePage, "utf-8")))
}
                                                

// @LINE:36
def downloadDistant(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "download")
}
                                                

// @LINE:8
def logout(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "logout")
}
                                                

// @LINE:3
def jsRouter(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "jsRouter")
}
                                                

// @LINE:11
def authenticate(provider:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "auth/" + implicitly[PathBindable[String]].unbind("provider", URLEncoder.encode(provider, "utf-8")))
}
                                                

// @LINE:1
def index(): Call = {
   Call("GET", _prefix)
}
                                                

// @LINE:10
def closePopup(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "auth/end")
}
                                                
    
}
                          

// @LINE:39
class ReverseAssets {
    

// @LINE:39
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          
}
                  


// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:15
// @LINE:14
package controllers.stream.javascript {

// @LINE:20
// @LINE:19
// @LINE:18
class ReverseSse {
    

// @LINE:20
def ping : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.ping",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/ping"})
      }
   """
)
                        

// @LINE:19
def command : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.command",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/command"})
      }
   """
)
                        

// @LINE:18
def connect : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.Sse.connect",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/sse"})
      }
   """
)
                        
    
}
              

// @LINE:15
// @LINE:14
class ReverseWebSocket {
    

// @LINE:15
def connectPublicPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.stream.WebSocket.connectPublicPage",
   """
      function(publicPage) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stream/webSocket/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("publicPage", encodeURIComponent(publicPage))})
      }
   """
)
                        

// @LINE:14
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
        

// @LINE:23
// @LINE:7
// @LINE:6
// @LINE:5
package controllers.api.javascript {

// @LINE:23
class ReverseStats {
    

// @LINE:23
def get : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Stats.get",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/stats"})
      }
   """
)
                        
    
}
              

// @LINE:7
// @LINE:6
// @LINE:5
class ReverseProviders {
    

// @LINE:6
def listServices : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.listServices",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/services"})
      }
   """
)
                        

// @LINE:5
def listAll : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.listAll",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/list"})
      }
   """
)
                        

// @LINE:7
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.api.Providers.delete",
   """
      function(provider) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/providers/del/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("provider", encodeURIComponent(provider))})
      }
   """
)
                        
    
}
              
}
        

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
package controllers.dev.javascript {

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
class ReverseUtil {
    

// @LINE:28
def staticRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.staticRes",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/staticRes"})
      }
   """
)
                        

// @LINE:27
def testSkimboRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.testSkimboRes",
   """
      function(service) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/testSkimboRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("service", encodeURIComponent(service))})
      }
   """
)
                        

// @LINE:33
def invalidToken : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.invalidToken",
   """
      function(p,pwd) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/invalidToken/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", encodeURIComponent(p)) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("pwd", encodeURIComponent(pwd))})
      }
   """
)
                        

// @LINE:26
def testRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.testRes",
   """
      function(service) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/testRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("service", encodeURIComponent(service))})
      }
   """
)
                        

// @LINE:34
def urlTest : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.urlTest",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/urlTest/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("id", encodeURIComponent(id))})
      }
   """
)
                        

// @LINE:32
def userDistantRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.userDistantRes",
   """
      function(p) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/userDistant/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", encodeURIComponent(p))})
      }
   """
)
                        

// @LINE:29
def deleteUser : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.deleteUser",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/delDb"})
      }
   """
)
                        

// @LINE:30
def deleteAllUsers : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.deleteAllUsers",
   """
      function(pwd) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/delAllDb/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("pwd", encodeURIComponent(pwd))})
      }
   """
)
                        

// @LINE:31
def userRes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.dev.Util.userRes",
   """
      function(p) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/dev/util/userRes/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("p", encodeURIComponent(p))})
      }
   """
)
                        
    
}
              
}
        

// @LINE:39
// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
package controllers.javascript {

// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:2
def publicPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.publicPage",
   """
      function(namePage) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "p/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("namePage", encodeURIComponent(namePage))})
      }
   """
)
                        

// @LINE:36
def downloadDistant : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.downloadDistant",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "download"})
      }
   """
)
                        

// @LINE:8
def logout : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.logout",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "logout"})
      }
   """
)
                        

// @LINE:3
def jsRouter : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.jsRouter",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "jsRouter"})
      }
   """
)
                        

// @LINE:11
def authenticate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.authenticate",
   """
      function(provider) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "auth/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("provider", encodeURIComponent(provider))})
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
                        

// @LINE:10
def closePopup : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.closePopup",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "auth/end"})
      }
   """
)
                        
    
}
              

// @LINE:39
class ReverseAssets {
    

// @LINE:39
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
        


// @LINE:20
// @LINE:19
// @LINE:18
// @LINE:15
// @LINE:14
package controllers.stream.ref {

// @LINE:20
// @LINE:19
// @LINE:18
class ReverseSse {
    

// @LINE:20
def ping(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.ping(), HandlerDef(this, "controllers.stream.Sse", "ping", Seq(), "GET", """""", _prefix + """api/stream/ping""")
)
                      

// @LINE:19
def command(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.command(), HandlerDef(this, "controllers.stream.Sse", "command", Seq(), "POST", """""", _prefix + """api/stream/command""")
)
                      

// @LINE:18
def connect(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.Sse.connect(), HandlerDef(this, "controllers.stream.Sse", "connect", Seq(), "GET", """ Streams -- Sse""", _prefix + """api/stream/sse""")
)
                      
    
}
                          

// @LINE:15
// @LINE:14
class ReverseWebSocket {
    

// @LINE:15
def connectPublicPage(publicPage:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.WebSocket.connectPublicPage(publicPage), HandlerDef(this, "controllers.stream.WebSocket", "connectPublicPage", Seq(classOf[String]), "GET", """""", _prefix + """api/stream/webSocket/$publicPage<[^/]+>""")
)
                      

// @LINE:14
def connect(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.stream.WebSocket.connect(), HandlerDef(this, "controllers.stream.WebSocket", "connect", Seq(), "GET", """ Streams -- websocket""", _prefix + """api/stream/webSocket""")
)
                      
    
}
                          
}
                  

// @LINE:23
// @LINE:7
// @LINE:6
// @LINE:5
package controllers.api.ref {

// @LINE:23
class ReverseStats {
    

// @LINE:23
def get(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Stats.get(), HandlerDef(this, "controllers.api.Stats", "get", Seq(), "GET", """ Stats""", _prefix + """api/stats""")
)
                      
    
}
                          

// @LINE:7
// @LINE:6
// @LINE:5
class ReverseProviders {
    

// @LINE:6
def listServices(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.listServices(), HandlerDef(this, "controllers.api.Providers", "listServices", Seq(), "GET", """""", _prefix + """api/providers/services""")
)
                      

// @LINE:5
def listAll(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.listAll(), HandlerDef(this, "controllers.api.Providers", "listAll", Seq(), "GET", """""", _prefix + """api/providers/list""")
)
                      

// @LINE:7
def delete(provider:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.api.Providers.delete(provider), HandlerDef(this, "controllers.api.Providers", "delete", Seq(classOf[String]), "GET", """""", _prefix + """api/providers/del/$provider<[^/]+>""")
)
                      
    
}
                          
}
                  

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
package controllers.dev.ref {

// @LINE:34
// @LINE:33
// @LINE:32
// @LINE:31
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
class ReverseUtil {
    

// @LINE:28
def staticRes(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.staticRes(), HandlerDef(this, "controllers.dev.Util", "staticRes", Seq(), "GET", """""", _prefix + """api/dev/util/staticRes""")
)
                      

// @LINE:27
def testSkimboRes(service:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.testSkimboRes(service), HandlerDef(this, "controllers.dev.Util", "testSkimboRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/testSkimboRes/$service<[^/]+>""")
)
                      

// @LINE:33
def invalidToken(p:String, pwd:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.invalidToken(p, pwd), HandlerDef(this, "controllers.dev.Util", "invalidToken", Seq(classOf[String], classOf[String]), "GET", """""", _prefix + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>""")
)
                      

// @LINE:26
def testRes(service:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.testRes(service), HandlerDef(this, "controllers.dev.Util", "testRes", Seq(classOf[String]), "GET", """ Dev util""", _prefix + """api/dev/util/testRes/$service<[^/]+>""")
)
                      

// @LINE:34
def urlTest(id:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.urlTest(id), HandlerDef(this, "controllers.dev.Util", "urlTest", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/urlTest/$id<[^/]+>""")
)
                      

// @LINE:32
def userDistantRes(p:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.userDistantRes(p), HandlerDef(this, "controllers.dev.Util", "userDistantRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/userDistant/$p<[^/]+>""")
)
                      

// @LINE:29
def deleteUser(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.deleteUser(), HandlerDef(this, "controllers.dev.Util", "deleteUser", Seq(), "GET", """""", _prefix + """api/dev/util/delDb""")
)
                      

// @LINE:30
def deleteAllUsers(pwd:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.deleteAllUsers(pwd), HandlerDef(this, "controllers.dev.Util", "deleteAllUsers", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/delAllDb/$pwd<[^/]+>""")
)
                      

// @LINE:31
def userRes(p:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.dev.Util.userRes(p), HandlerDef(this, "controllers.dev.Util", "userRes", Seq(classOf[String]), "GET", """""", _prefix + """api/dev/util/userRes/$p<[^/]+>""")
)
                      
    
}
                          
}
                  

// @LINE:39
// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
package controllers.ref {

// @LINE:36
// @LINE:11
// @LINE:10
// @LINE:8
// @LINE:3
// @LINE:2
// @LINE:1
class ReverseApplication {
    

// @LINE:2
def publicPage(namePage:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.publicPage(namePage), HandlerDef(this, "controllers.Application", "publicPage", Seq(classOf[String]), "GET", """""", _prefix + """p/$namePage<[^/]+>""")
)
                      

// @LINE:36
def downloadDistant(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.downloadDistant(), HandlerDef(this, "controllers.Application", "downloadDistant", Seq(), "GET", """""", _prefix + """download""")
)
                      

// @LINE:8
def logout(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.logout(), HandlerDef(this, "controllers.Application", "logout", Seq(), "GET", """""", _prefix + """logout""")
)
                      

// @LINE:3
def jsRouter(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.jsRouter(), HandlerDef(this, "controllers.Application", "jsRouter", Seq(), "GET", """""", _prefix + """jsRouter""")
)
                      

// @LINE:11
def authenticate(provider:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.authenticate(provider), HandlerDef(this, "controllers.Application", "authenticate", Seq(classOf[String]), "GET", """""", _prefix + """auth/$provider<[^/]+>""")
)
                      

// @LINE:1
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this, "controllers.Application", "index", Seq(), "GET", """""", _prefix + """""")
)
                      

// @LINE:10
def closePopup(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.closePopup(), HandlerDef(this, "controllers.Application", "closePopup", Seq(), "GET", """""", _prefix + """auth/end""")
)
                      
    
}
                          

// @LINE:39
class ReverseAssets {
    

// @LINE:39
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          
}
                  
      
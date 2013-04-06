// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:0db3a9eaaf9303eee8f28ddb45447bb6ccb672b4
// @DATE:Sat Apr 06 15:51:13 CEST 2013


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:1
private[this] lazy val controllers_Application_index0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:2
private[this] lazy val controllers_Application_jsRouter1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("jsRouter"))))
        

// @LINE:4
private[this] lazy val controllers_api_Providers_listAll2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/list"))))
        

// @LINE:5
private[this] lazy val controllers_api_Providers_listServices3 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/services"))))
        

// @LINE:6
private[this] lazy val controllers_api_Providers_delete4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/del/"),DynamicPart("provider", """[^/]+"""))))
        

// @LINE:7
private[this] lazy val controllers_Application_logout5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("logout"))))
        

// @LINE:9
private[this] lazy val controllers_Application_closePopup6 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("auth/end"))))
        

// @LINE:10
private[this] lazy val controllers_Application_authenticate7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("auth/"),DynamicPart("provider", """[^/]+"""))))
        

// @LINE:13
private[this] lazy val controllers_stream_WebSocket_connect8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/webSocket"))))
        

// @LINE:16
private[this] lazy val controllers_stream_Sse_connect9 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/sse"))))
        

// @LINE:17
private[this] lazy val controllers_stream_Sse_command10 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/command"))))
        

// @LINE:18
private[this] lazy val controllers_stream_Sse_ping11 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/ping"))))
        

// @LINE:21
private[this] lazy val controllers_api_Stats_get12 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stats"))))
        

// @LINE:24
private[this] lazy val controllers_dev_Util_testRes13 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/testRes/"),DynamicPart("service", """[^/]+"""))))
        

// @LINE:25
private[this] lazy val controllers_dev_Util_testSkimboRes14 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/testSkimboRes/"),DynamicPart("service", """[^/]+"""))))
        

// @LINE:26
private[this] lazy val controllers_dev_Util_staticRes15 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/staticRes"))))
        

// @LINE:27
private[this] lazy val controllers_dev_Util_deleteUser16 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/delDb"))))
        

// @LINE:28
private[this] lazy val controllers_dev_Util_deleteAllUsers17 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/delAllDb/"),DynamicPart("pwd", """[^/]+"""))))
        

// @LINE:29
private[this] lazy val controllers_dev_Util_userRes18 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/userRes/"),DynamicPart("p", """[^/]+"""))))
        

// @LINE:30
private[this] lazy val controllers_dev_Util_userDistantRes19 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/userDistant/"),DynamicPart("p", """[^/]+"""))))
        

// @LINE:31
private[this] lazy val controllers_dev_Util_invalidToken20 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/invalidToken/"),DynamicPart("p", """[^/]+"""),StaticPart("/"),DynamicPart("pwd", """[^/]+"""))))
        

// @LINE:32
private[this] lazy val controllers_dev_Util_urlTest21 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/urlTest/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:35
private[this] lazy val controllers_Assets_at22 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+"""))))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """jsRouter""","""controllers.Application.jsRouter"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/list""","""controllers.api.Providers.listAll"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/services""","""controllers.api.Providers.listServices"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/del/$provider<[^/]+>""","""controllers.api.Providers.delete(provider:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """logout""","""controllers.Application.logout"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """auth/end""","""controllers.Application.closePopup"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """auth/$provider<[^/]+>""","""controllers.Application.authenticate(provider:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/webSocket""","""controllers.stream.WebSocket.connect"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/sse""","""controllers.stream.Sse.connect"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/command""","""controllers.stream.Sse.command"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/ping""","""controllers.stream.Sse.ping"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stats""","""controllers.api.Stats.get"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/testRes/$service<[^/]+>""","""controllers.dev.Util.testRes(service:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/testSkimboRes/$service<[^/]+>""","""controllers.dev.Util.testSkimboRes(service:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/staticRes""","""controllers.dev.Util.staticRes"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/delDb""","""controllers.dev.Util.deleteUser"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/delAllDb/$pwd<[^/]+>""","""controllers.dev.Util.deleteAllUsers(pwd:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/userRes/$p<[^/]+>""","""controllers.dev.Util.userRes(p:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/userDistant/$p<[^/]+>""","""controllers.dev.Util.userDistantRes(p:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>""","""controllers.dev.Util.invalidToken(p:String, pwd:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/urlTest/$id<[^/]+>""","""controllers.dev.Util.urlTest(id:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:1
case controllers_Application_index0(params) => {
   call { 
        invokeHandler(controllers.Application.index, HandlerDef(this, "controllers.Application", "index", Nil,"GET", """""", Routes.prefix + """"""))
   }
}
        

// @LINE:2
case controllers_Application_jsRouter1(params) => {
   call { 
        invokeHandler(controllers.Application.jsRouter, HandlerDef(this, "controllers.Application", "jsRouter", Nil,"GET", """""", Routes.prefix + """jsRouter"""))
   }
}
        

// @LINE:4
case controllers_api_Providers_listAll2(params) => {
   call { 
        invokeHandler(controllers.api.Providers.listAll, HandlerDef(this, "controllers.api.Providers", "listAll", Nil,"GET", """""", Routes.prefix + """api/providers/list"""))
   }
}
        

// @LINE:5
case controllers_api_Providers_listServices3(params) => {
   call { 
        invokeHandler(controllers.api.Providers.listServices, HandlerDef(this, "controllers.api.Providers", "listServices", Nil,"GET", """""", Routes.prefix + """api/providers/services"""))
   }
}
        

// @LINE:6
case controllers_api_Providers_delete4(params) => {
   call(params.fromPath[String]("provider", None)) { (provider) =>
        invokeHandler(controllers.api.Providers.delete(provider), HandlerDef(this, "controllers.api.Providers", "delete", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/providers/del/$provider<[^/]+>"""))
   }
}
        

// @LINE:7
case controllers_Application_logout5(params) => {
   call { 
        invokeHandler(controllers.Application.logout, HandlerDef(this, "controllers.Application", "logout", Nil,"GET", """""", Routes.prefix + """logout"""))
   }
}
        

// @LINE:9
case controllers_Application_closePopup6(params) => {
   call { 
        invokeHandler(controllers.Application.closePopup, HandlerDef(this, "controllers.Application", "closePopup", Nil,"GET", """""", Routes.prefix + """auth/end"""))
   }
}
        

// @LINE:10
case controllers_Application_authenticate7(params) => {
   call(params.fromPath[String]("provider", None)) { (provider) =>
        invokeHandler(controllers.Application.authenticate(provider), HandlerDef(this, "controllers.Application", "authenticate", Seq(classOf[String]),"GET", """""", Routes.prefix + """auth/$provider<[^/]+>"""))
   }
}
        

// @LINE:13
case controllers_stream_WebSocket_connect8(params) => {
   call { 
        invokeHandler(controllers.stream.WebSocket.connect, HandlerDef(this, "controllers.stream.WebSocket", "connect", Nil,"GET", """ Streams -- websocket""", Routes.prefix + """api/stream/webSocket"""))
   }
}
        

// @LINE:16
case controllers_stream_Sse_connect9(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.connect, HandlerDef(this, "controllers.stream.Sse", "connect", Nil,"GET", """ Streams -- Sse""", Routes.prefix + """api/stream/sse"""))
   }
}
        

// @LINE:17
case controllers_stream_Sse_command10(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.command, HandlerDef(this, "controllers.stream.Sse", "command", Nil,"POST", """""", Routes.prefix + """api/stream/command"""))
   }
}
        

// @LINE:18
case controllers_stream_Sse_ping11(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.ping, HandlerDef(this, "controllers.stream.Sse", "ping", Nil,"GET", """""", Routes.prefix + """api/stream/ping"""))
   }
}
        

// @LINE:21
case controllers_api_Stats_get12(params) => {
   call { 
        invokeHandler(controllers.api.Stats.get, HandlerDef(this, "controllers.api.Stats", "get", Nil,"GET", """ Stats""", Routes.prefix + """api/stats"""))
   }
}
        

// @LINE:24
case controllers_dev_Util_testRes13(params) => {
   call(params.fromPath[String]("service", None)) { (service) =>
        invokeHandler(controllers.dev.Util.testRes(service), HandlerDef(this, "controllers.dev.Util", "testRes", Seq(classOf[String]),"GET", """ Dev util""", Routes.prefix + """api/dev/util/testRes/$service<[^/]+>"""))
   }
}
        

// @LINE:25
case controllers_dev_Util_testSkimboRes14(params) => {
   call(params.fromPath[String]("service", None)) { (service) =>
        invokeHandler(controllers.dev.Util.testSkimboRes(service), HandlerDef(this, "controllers.dev.Util", "testSkimboRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/testSkimboRes/$service<[^/]+>"""))
   }
}
        

// @LINE:26
case controllers_dev_Util_staticRes15(params) => {
   call { 
        invokeHandler(controllers.dev.Util.staticRes, HandlerDef(this, "controllers.dev.Util", "staticRes", Nil,"GET", """""", Routes.prefix + """api/dev/util/staticRes"""))
   }
}
        

// @LINE:27
case controllers_dev_Util_deleteUser16(params) => {
   call { 
        invokeHandler(controllers.dev.Util.deleteUser, HandlerDef(this, "controllers.dev.Util", "deleteUser", Nil,"GET", """""", Routes.prefix + """api/dev/util/delDb"""))
   }
}
        

// @LINE:28
case controllers_dev_Util_deleteAllUsers17(params) => {
   call(params.fromPath[String]("pwd", None)) { (pwd) =>
        invokeHandler(controllers.dev.Util.deleteAllUsers(pwd), HandlerDef(this, "controllers.dev.Util", "deleteAllUsers", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/delAllDb/$pwd<[^/]+>"""))
   }
}
        

// @LINE:29
case controllers_dev_Util_userRes18(params) => {
   call(params.fromPath[String]("p", None)) { (p) =>
        invokeHandler(controllers.dev.Util.userRes(p), HandlerDef(this, "controllers.dev.Util", "userRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/userRes/$p<[^/]+>"""))
   }
}
        

// @LINE:30
case controllers_dev_Util_userDistantRes19(params) => {
   call(params.fromPath[String]("p", None)) { (p) =>
        invokeHandler(controllers.dev.Util.userDistantRes(p), HandlerDef(this, "controllers.dev.Util", "userDistantRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/userDistant/$p<[^/]+>"""))
   }
}
        

// @LINE:31
case controllers_dev_Util_invalidToken20(params) => {
   call(params.fromPath[String]("p", None), params.fromPath[String]("pwd", None)) { (p, pwd) =>
        invokeHandler(controllers.dev.Util.invalidToken(p, pwd), HandlerDef(this, "controllers.dev.Util", "invalidToken", Seq(classOf[String], classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>"""))
   }
}
        

// @LINE:32
case controllers_dev_Util_urlTest21(params) => {
   call(params.fromPath[String]("id", None)) { (id) =>
        invokeHandler(controllers.dev.Util.urlTest(id), HandlerDef(this, "controllers.dev.Util", "urlTest", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/urlTest/$id<[^/]+>"""))
   }
}
        

// @LINE:35
case controllers_Assets_at22(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        
}
    
}
        
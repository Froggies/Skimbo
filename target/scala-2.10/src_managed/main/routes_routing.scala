// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:5994e1e51c0c55a11058a1e75ef1642bcd355445
// @DATE:Mon May 06 19:03:07 CEST 2013


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
private[this] lazy val controllers_Application_publicPage1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("p/"),DynamicPart("namePage", """[^/]+""",true))))
        

// @LINE:3
private[this] lazy val controllers_Application_jsRouter2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("jsRouter"))))
        

// @LINE:5
private[this] lazy val controllers_api_Providers_listAll3 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/list"))))
        

// @LINE:6
private[this] lazy val controllers_api_Providers_listServices4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/services"))))
        

// @LINE:7
private[this] lazy val controllers_api_Providers_delete5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/providers/del/"),DynamicPart("provider", """[^/]+""",true))))
        

// @LINE:8
private[this] lazy val controllers_Application_logout6 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("logout"))))
        

// @LINE:10
private[this] lazy val controllers_Application_closePopup7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("auth/end"))))
        

// @LINE:11
private[this] lazy val controllers_Application_authenticate8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("auth/"),DynamicPart("provider", """[^/]+""",true))))
        

// @LINE:14
private[this] lazy val controllers_stream_WebSocket_connect9 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/webSocket"))))
        

// @LINE:15
private[this] lazy val controllers_stream_WebSocket_connectPublicPage10 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/webSocket/"),DynamicPart("publicPage", """[^/]+""",true))))
        

// @LINE:18
private[this] lazy val controllers_stream_Sse_connect11 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/sse"))))
        

// @LINE:19
private[this] lazy val controllers_stream_Sse_command12 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/command"))))
        

// @LINE:20
private[this] lazy val controllers_stream_Sse_ping13 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/ping"))))
        

// @LINE:23
private[this] lazy val controllers_stream_LongPolling_connect14 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stream/longpolling"))))
        

// @LINE:26
private[this] lazy val controllers_api_Stats_get15 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/stats"))))
        

// @LINE:29
private[this] lazy val controllers_dev_Util_testRes16 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/testRes/"),DynamicPart("service", """[^/]+""",true))))
        

// @LINE:30
private[this] lazy val controllers_dev_Util_testSkimboRes17 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/testSkimboRes/"),DynamicPart("service", """[^/]+""",true))))
        

// @LINE:31
private[this] lazy val controllers_dev_Util_staticRes18 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/staticRes"))))
        

// @LINE:32
private[this] lazy val controllers_dev_Util_deleteUser19 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/delDb"))))
        

// @LINE:33
private[this] lazy val controllers_dev_Util_deleteAllUsers20 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/delAllDb/"),DynamicPart("pwd", """[^/]+""",true))))
        

// @LINE:34
private[this] lazy val controllers_dev_Util_userRes21 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/userRes/"),DynamicPart("p", """[^/]+""",true))))
        

// @LINE:35
private[this] lazy val controllers_dev_Util_userDistantRes22 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/userDistant/"),DynamicPart("p", """[^/]+""",true))))
        

// @LINE:36
private[this] lazy val controllers_dev_Util_invalidToken23 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/invalidToken/"),DynamicPart("p", """[^/]+""",true),StaticPart("/"),DynamicPart("pwd", """[^/]+""",true))))
        

// @LINE:37
private[this] lazy val controllers_dev_Util_urlTest24 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("api/dev/util/urlTest/"),DynamicPart("id", """[^/]+""",true))))
        

// @LINE:39
private[this] lazy val controllers_Application_downloadDistant25 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("download"))))
        

// @LINE:42
private[this] lazy val controllers_Assets_at26 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """p/$namePage<[^/]+>""","""controllers.Application.publicPage(namePage:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """jsRouter""","""controllers.Application.jsRouter"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/list""","""controllers.api.Providers.listAll"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/services""","""controllers.api.Providers.listServices"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/providers/del/$provider<[^/]+>""","""controllers.api.Providers.delete(provider:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """logout""","""controllers.Application.logout"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """auth/end""","""controllers.Application.closePopup"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """auth/$provider<[^/]+>""","""controllers.Application.authenticate(provider:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/webSocket""","""controllers.stream.WebSocket.connect"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/webSocket/$publicPage<[^/]+>""","""controllers.stream.WebSocket.connectPublicPage(publicPage:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/sse""","""controllers.stream.Sse.connect"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/command""","""controllers.stream.Sse.command"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/ping""","""controllers.stream.Sse.ping"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stream/longpolling""","""controllers.stream.LongPolling.connect"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/stats""","""controllers.api.Stats.get"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/testRes/$service<[^/]+>""","""controllers.dev.Util.testRes(service:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/testSkimboRes/$service<[^/]+>""","""controllers.dev.Util.testSkimboRes(service:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/staticRes""","""controllers.dev.Util.staticRes"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/delDb""","""controllers.dev.Util.deleteUser"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/delAllDb/$pwd<[^/]+>""","""controllers.dev.Util.deleteAllUsers(pwd:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/userRes/$p<[^/]+>""","""controllers.dev.Util.userRes(p:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/userDistant/$p<[^/]+>""","""controllers.dev.Util.userDistantRes(p:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>""","""controllers.dev.Util.invalidToken(p:String, pwd:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """api/dev/util/urlTest/$id<[^/]+>""","""controllers.dev.Util.urlTest(id:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """download""","""controllers.Application.downloadDistant"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
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
case controllers_Application_publicPage1(params) => {
   call(params.fromPath[String]("namePage", None)) { (namePage) =>
        invokeHandler(controllers.Application.publicPage(namePage), HandlerDef(this, "controllers.Application", "publicPage", Seq(classOf[String]),"GET", """""", Routes.prefix + """p/$namePage<[^/]+>"""))
   }
}
        

// @LINE:3
case controllers_Application_jsRouter2(params) => {
   call { 
        invokeHandler(controllers.Application.jsRouter, HandlerDef(this, "controllers.Application", "jsRouter", Nil,"GET", """""", Routes.prefix + """jsRouter"""))
   }
}
        

// @LINE:5
case controllers_api_Providers_listAll3(params) => {
   call { 
        invokeHandler(controllers.api.Providers.listAll, HandlerDef(this, "controllers.api.Providers", "listAll", Nil,"GET", """""", Routes.prefix + """api/providers/list"""))
   }
}
        

// @LINE:6
case controllers_api_Providers_listServices4(params) => {
   call { 
        invokeHandler(controllers.api.Providers.listServices, HandlerDef(this, "controllers.api.Providers", "listServices", Nil,"GET", """""", Routes.prefix + """api/providers/services"""))
   }
}
        

// @LINE:7
case controllers_api_Providers_delete5(params) => {
   call(params.fromPath[String]("provider", None)) { (provider) =>
        invokeHandler(controllers.api.Providers.delete(provider), HandlerDef(this, "controllers.api.Providers", "delete", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/providers/del/$provider<[^/]+>"""))
   }
}
        

// @LINE:8
case controllers_Application_logout6(params) => {
   call { 
        invokeHandler(controllers.Application.logout, HandlerDef(this, "controllers.Application", "logout", Nil,"GET", """""", Routes.prefix + """logout"""))
   }
}
        

// @LINE:10
case controllers_Application_closePopup7(params) => {
   call { 
        invokeHandler(controllers.Application.closePopup, HandlerDef(this, "controllers.Application", "closePopup", Nil,"GET", """""", Routes.prefix + """auth/end"""))
   }
}
        

// @LINE:11
case controllers_Application_authenticate8(params) => {
   call(params.fromPath[String]("provider", None)) { (provider) =>
        invokeHandler(controllers.Application.authenticate(provider), HandlerDef(this, "controllers.Application", "authenticate", Seq(classOf[String]),"GET", """""", Routes.prefix + """auth/$provider<[^/]+>"""))
   }
}
        

// @LINE:14
case controllers_stream_WebSocket_connect9(params) => {
   call { 
        invokeHandler(controllers.stream.WebSocket.connect, HandlerDef(this, "controllers.stream.WebSocket", "connect", Nil,"GET", """ Streams -- websocket""", Routes.prefix + """api/stream/webSocket"""))
   }
}
        

// @LINE:15
case controllers_stream_WebSocket_connectPublicPage10(params) => {
   call(params.fromPath[String]("publicPage", None)) { (publicPage) =>
        invokeHandler(controllers.stream.WebSocket.connectPublicPage(publicPage), HandlerDef(this, "controllers.stream.WebSocket", "connectPublicPage", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/stream/webSocket/$publicPage<[^/]+>"""))
   }
}
        

// @LINE:18
case controllers_stream_Sse_connect11(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.connect, HandlerDef(this, "controllers.stream.Sse", "connect", Nil,"GET", """ Streams -- Sse""", Routes.prefix + """api/stream/sse"""))
   }
}
        

// @LINE:19
case controllers_stream_Sse_command12(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.command, HandlerDef(this, "controllers.stream.Sse", "command", Nil,"POST", """""", Routes.prefix + """api/stream/command"""))
   }
}
        

// @LINE:20
case controllers_stream_Sse_ping13(params) => {
   call { 
        invokeHandler(controllers.stream.Sse.ping, HandlerDef(this, "controllers.stream.Sse", "ping", Nil,"GET", """""", Routes.prefix + """api/stream/ping"""))
   }
}
        

// @LINE:23
case controllers_stream_LongPolling_connect14(params) => {
   call { 
        invokeHandler(controllers.stream.LongPolling.connect, HandlerDef(this, "controllers.stream.LongPolling", "connect", Nil,"GET", """ Streams -- LongPolling""", Routes.prefix + """api/stream/longpolling"""))
   }
}
        

// @LINE:26
case controllers_api_Stats_get15(params) => {
   call { 
        invokeHandler(controllers.api.Stats.get, HandlerDef(this, "controllers.api.Stats", "get", Nil,"GET", """ Stats""", Routes.prefix + """api/stats"""))
   }
}
        

// @LINE:29
case controllers_dev_Util_testRes16(params) => {
   call(params.fromPath[String]("service", None)) { (service) =>
        invokeHandler(controllers.dev.Util.testRes(service), HandlerDef(this, "controllers.dev.Util", "testRes", Seq(classOf[String]),"GET", """ Dev util""", Routes.prefix + """api/dev/util/testRes/$service<[^/]+>"""))
   }
}
        

// @LINE:30
case controllers_dev_Util_testSkimboRes17(params) => {
   call(params.fromPath[String]("service", None)) { (service) =>
        invokeHandler(controllers.dev.Util.testSkimboRes(service), HandlerDef(this, "controllers.dev.Util", "testSkimboRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/testSkimboRes/$service<[^/]+>"""))
   }
}
        

// @LINE:31
case controllers_dev_Util_staticRes18(params) => {
   call { 
        invokeHandler(controllers.dev.Util.staticRes, HandlerDef(this, "controllers.dev.Util", "staticRes", Nil,"GET", """""", Routes.prefix + """api/dev/util/staticRes"""))
   }
}
        

// @LINE:32
case controllers_dev_Util_deleteUser19(params) => {
   call { 
        invokeHandler(controllers.dev.Util.deleteUser, HandlerDef(this, "controllers.dev.Util", "deleteUser", Nil,"GET", """""", Routes.prefix + """api/dev/util/delDb"""))
   }
}
        

// @LINE:33
case controllers_dev_Util_deleteAllUsers20(params) => {
   call(params.fromPath[String]("pwd", None)) { (pwd) =>
        invokeHandler(controllers.dev.Util.deleteAllUsers(pwd), HandlerDef(this, "controllers.dev.Util", "deleteAllUsers", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/delAllDb/$pwd<[^/]+>"""))
   }
}
        

// @LINE:34
case controllers_dev_Util_userRes21(params) => {
   call(params.fromPath[String]("p", None)) { (p) =>
        invokeHandler(controllers.dev.Util.userRes(p), HandlerDef(this, "controllers.dev.Util", "userRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/userRes/$p<[^/]+>"""))
   }
}
        

// @LINE:35
case controllers_dev_Util_userDistantRes22(params) => {
   call(params.fromPath[String]("p", None)) { (p) =>
        invokeHandler(controllers.dev.Util.userDistantRes(p), HandlerDef(this, "controllers.dev.Util", "userDistantRes", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/userDistant/$p<[^/]+>"""))
   }
}
        

// @LINE:36
case controllers_dev_Util_invalidToken23(params) => {
   call(params.fromPath[String]("p", None), params.fromPath[String]("pwd", None)) { (p, pwd) =>
        invokeHandler(controllers.dev.Util.invalidToken(p, pwd), HandlerDef(this, "controllers.dev.Util", "invalidToken", Seq(classOf[String], classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/invalidToken/$p<[^/]+>/$pwd<[^/]+>"""))
   }
}
        

// @LINE:37
case controllers_dev_Util_urlTest24(params) => {
   call(params.fromPath[String]("id", None)) { (id) =>
        invokeHandler(controllers.dev.Util.urlTest(id), HandlerDef(this, "controllers.dev.Util", "urlTest", Seq(classOf[String]),"GET", """""", Routes.prefix + """api/dev/util/urlTest/$id<[^/]+>"""))
   }
}
        

// @LINE:39
case controllers_Application_downloadDistant25(params) => {
   call { 
        invokeHandler(controllers.Application.downloadDistant, HandlerDef(this, "controllers.Application", "downloadDistant", Nil,"GET", """""", Routes.prefix + """download"""))
   }
}
        

// @LINE:42
case controllers_Assets_at26(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        
}
    
}
        
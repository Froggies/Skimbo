// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:0db3a9eaaf9303eee8f28ddb45447bb6ccb672b4
// @DATE:Sat Apr 06 15:51:13 CEST 2013

package controllers.stream;

public class routes {
public static final controllers.stream.ReverseSse Sse = new controllers.stream.ReverseSse();
public static final controllers.stream.ReverseWebSocket WebSocket = new controllers.stream.ReverseWebSocket();
public static class javascript {
public static final controllers.stream.javascript.ReverseSse Sse = new controllers.stream.javascript.ReverseSse();
public static final controllers.stream.javascript.ReverseWebSocket WebSocket = new controllers.stream.javascript.ReverseWebSocket();    
}   
public static class ref {
public static final controllers.stream.ref.ReverseSse Sse = new controllers.stream.ref.ReverseSse();
public static final controllers.stream.ref.ReverseWebSocket WebSocket = new controllers.stream.ref.ReverseWebSocket();    
} 
}
              
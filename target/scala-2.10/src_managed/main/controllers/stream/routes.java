// @SOURCE:/home/manland/projets/skimbo/Skimbo/conf/routes
// @HASH:5994e1e51c0c55a11058a1e75ef1642bcd355445
// @DATE:Mon May 06 19:32:17 CEST 2013

package controllers.stream;

public class routes {
public static final controllers.stream.ReverseSse Sse = new controllers.stream.ReverseSse();
public static final controllers.stream.ReverseLongPolling LongPolling = new controllers.stream.ReverseLongPolling();
public static final controllers.stream.ReverseWebSocket WebSocket = new controllers.stream.ReverseWebSocket();
public static class javascript {
public static final controllers.stream.javascript.ReverseSse Sse = new controllers.stream.javascript.ReverseSse();
public static final controllers.stream.javascript.ReverseLongPolling LongPolling = new controllers.stream.javascript.ReverseLongPolling();
public static final controllers.stream.javascript.ReverseWebSocket WebSocket = new controllers.stream.javascript.ReverseWebSocket();    
}   
public static class ref {
public static final controllers.stream.ref.ReverseSse Sse = new controllers.stream.ref.ReverseSse();
public static final controllers.stream.ref.ReverseLongPolling LongPolling = new controllers.stream.ref.ReverseLongPolling();
public static final controllers.stream.ref.ReverseWebSocket WebSocket = new controllers.stream.ref.ReverseWebSocket();    
} 
}
              
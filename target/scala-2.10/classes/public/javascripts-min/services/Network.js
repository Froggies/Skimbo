define(["app"],function(e){return e.factory("Network",["$http","$timeout","ServerCommunication","$location",function(e,t,n,r){function p(){a==undefined&&(a=new WebSocket(s),a.onclose=function(){a=undefined,l++,t(function(){h==1&&l<c?p():(l=0,d())},500),console.log("WS closed : nbError = ",l)},a.onerror=function(){a=undefined,l++,t(function(){h==1&&l<c?p():(l=0,d())},800),console.log("WS error : nbError = ",l)},a.onmessage=function(e){h=!0,l=0;var t;try{t=JSON.parse(e.data)}catch(n){t=e.data}v(t)},t(function(){h==0&&f==undefined&&(console.log("no data after 5sc !"),a=undefined,l=0,d())},5e3))}function d(){console.log("sseMode actif !"),f==undefined&&(f=new EventSource(o),f.addEventListener("message",function(e){l=0;var t;try{t=JSON.parse(e.data)}catch(n){t=e.data}v(t)},!1),f.addEventListener("error",function(e){f.close(),f=undefined,l++,t(function(){l<c?d():n.cmd({cmd:"disconnect"})},800),console.log("sse error : nb = ",l)},!1))}function v(e){e.cmd=="ping"?m({cmd:"pong"}):n.cmd(e)}function m(t){a!==undefined?a.send(JSON.stringify(t)):e.post("/api/stream/command",JSON.stringify(t))}var i=r.path().split("/"),s=jsRoutes.controllers.stream.WebSocket.connect().webSocketURL();i!==undefined&&i.length==3&&(s=s+"/"+i[2]),console.log("WHOST URL = "+s);var o=jsRoutes.controllers.stream.Sse.connect().url;console.log("SSEHOST URL = "+o);var u=jsRoutes.controllers.stream.Sse.ping().url;console.log("SSEPING URL = "+u);var a=undefined,f=undefined,l=0,c=5,h=!1;return window.MozWebSocket&&(window.WebSocket=window.MozWebSocket),window.WebSocket?p():d(),{send:function(e){m(e)}}}]),e})
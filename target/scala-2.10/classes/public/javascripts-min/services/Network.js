define(["app"],function(e){return e.factory("Network",["$http","$timeout","ServerCommunication",function(e,t,n){function c(){o==undefined&&(o=new WebSocket(r),o.onclose=function(){o=undefined,a++,t(function(){l==1&&a<f?c():(a=0,h())},500),console.log("WS closed : nbError = ",a)},o.onerror=function(){o=undefined,a++,t(function(){l==1&&a<f?c():(a=0,h())},800),console.log("WS error : nbError = ",a)},o.onmessage=function(e){l=!0,a=0;var t;try{t=JSON.parse(e.data)}catch(n){t=e.data}p(t)},t(function(){l==0&&u==undefined&&(console.log("no data after 5sc !"),o=undefined,a=0,h())},5e3))}function h(){console.log("sseMode actif !"),u==undefined&&(u=new EventSource(i),u.addEventListener("message",function(e){a=0;var t;try{t=JSON.parse(e.data)}catch(n){t=e.data}p(t)},!1),u.addEventListener("error",function(e){u.close(),u=undefined,a++,t(function(){a<f?h():n.cmd({cmd:"disconnect"})},800),console.log("sse error : nb = ",a)},!1))}function p(e){e.cmd=="ping"?d({cmd:"pong"}):n.cmd(e)}function d(t){o!==undefined?o.send(JSON.stringify(t)):e.post("/api/stream/command",JSON.stringify(t))}var r=jsRoutes.controllers.stream.WebSocket.connect().webSocketURL(),i=jsRoutes.controllers.stream.Sse.connect().absoluteURL(),s=jsRoutes.controllers.stream.Sse.ping().absoluteURL(),o=undefined,u=undefined,a=0,f=5,l=!1;return window.MozWebSocket&&(window.WebSocket=window.MozWebSocket),window.WebSocket?c():h(),{send:function(e){d(e)}}}]),e})
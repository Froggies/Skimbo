'use strict';

define(["app"], function(app) {

app.factory("ServerCommunication", [
  "$http", "$rootScope", "UnifiedRequestUtils", "ImagesUtils", "StringUtils", "ArrayUtils",
  function($http, $rootScope, $unifiedRequestUtils, $imagesUtils, $stringUtils, $arrayUtils) {

  function broadcast(cmd, data) {
    $rootScope.$broadcast(cmd, data);
  };

  return {

    cmd: function(data) {
      if(data.cmd == "allUnifiedRequests") {
        var serviceProposes = new Array();
        for (var i = 0; i < data.body.length; i++) {
          var elementBodySocialNetwork = data.body[i];
          var services = elementBodySocialNetwork.services;
          for (var j = 0; j < services.length; j++) {
            var service = {
              socialNetwork:elementBodySocialNetwork.endpoint,
              socialNetworkToken:elementBodySocialNetwork.hasToken,
              typeService:services[j].service.split(".")[1],
              typeServiceChar:"",
              explainService:"",
              args:{},
              service:services[j],
              hasParser:services[j].hasParser,
              hasHelper:services[j].hasHelper
            };
            for (var k = 0; k < services[j].args.length; k++) {
              service.args[services[j].args[k]] = "";
            };
            serviceProposes.push(service);
          }
        };
        broadcast('availableServices', serviceProposes);
        broadcast('allUnifiedRequests', data.body);
      } else if(data.cmd == "msg") {
        data.body.msg.authorAvatar = $imagesUtils.checkExistingImage(data.body.msg.authorAvatar);
        data.body.msg.original = data.body.msg.message;
        //no urlify for rss because almost are in html
        if(data.body.msg.from !== "rss" && 
          data.body.msg.from !== "scoopit") {
          data.body.msg.message = $stringUtils.truncateString(data.body.msg.message);
          data.body.msg.message = $stringUtils.urlify(data.body.msg);
        } else {
          data.body.msg.message = data.body.msg.message;
        }
        broadcast('msg', data.body);
      } else if(data.cmd == "allColumns") {
        var columns = [];
        var cols = data.body;
        for (var i = 0; i < cols.length; i++) {
          var originalColumn = cols[i];
          var clientColumn = {};
          clientColumn.title = originalColumn.title;
          clientColumn.unifiedRequests = [];
          clientColumn.index = originalColumn.index;
          clientColumn.width = originalColumn.width;
          clientColumn.height = originalColumn.height;
          for (var j = 0; j < originalColumn.unifiedRequests.length; j++) {
            var unifiedRequest = originalColumn.unifiedRequests[j];
            var clientUnifiedRequest = $unifiedRequestUtils.serverToClientUnifiedRequest(unifiedRequest);
            clientUnifiedRequest.fromServer = true;
            clientColumn.unifiedRequests.push(clientUnifiedRequest);
          };
          columns.splice($arrayUtils.sortedArrayLocationOf(originalColumn, columns, 'index')+1, 0, clientColumn);
        }
        broadcast('allColumns', columns);
      } else if(data.cmd == "delColumn") {
        broadcast('delColumn', data.body);
      } else if(data.cmd == "addColumn") {
        var originalColumn = data.body;
        var clientColumn = {};
        clientColumn.title = originalColumn.title;
        clientColumn.unifiedRequests = [];
        clientColumn.index = originalColumn.index;
        clientColumn.width = originalColumn.width;
        clientColumn.height = originalColumn.height;
        for (var j = 0; j < originalColumn.unifiedRequests.length; j++) {
          var unifiedRequest = originalColumn.unifiedRequests[j];
          var clientUnifiedRequest = $unifiedRequestUtils.serverToClientUnifiedRequest(unifiedRequest);
          clientUnifiedRequest.fromServer = true;
          clientColumn.unifiedRequests.push(clientUnifiedRequest);
        };
        broadcast('addColumn', clientColumn);
      } else if(data.cmd == "modColumn") {
        var originalColumn = data.body.column;
        var clientColumn = {};
        clientColumn.title = originalColumn.title;
        clientColumn.unifiedRequests = [];
        clientColumn.index = originalColumn.index;
        clientColumn.width = originalColumn.width;
        clientColumn.height = originalColumn.height;
        for (var j = 0; j < originalColumn.unifiedRequests.length; j++) {
          var unifiedRequest = originalColumn.unifiedRequests[j];
          var clientUnifiedRequest = $unifiedRequestUtils.serverToClientUnifiedRequest(unifiedRequest);
          clientUnifiedRequest.fromServer = true;
          clientColumn.unifiedRequests.push(clientUnifiedRequest);
        };
        data.body.column = clientColumn;
        broadcast('modColumn', data.body);
      } else if(data.cmd == "userInfos") {
        data.body.avatar = $imagesUtils.checkExistingImage(data.body.avatar);
        broadcast('userInfos', data.body);
      } else if (data.cmd == "tokenInvalid") {
        broadcast('tokenInvalid', data.body);
      } else if(data.cmd == "newToken") {
        broadcast('newToken', data.body);
      } else if(data.cmd == "allProviders") {
        broadcast('allProviders', data.body);
      } else if(data.cmd == "error") {
        broadcast('error', data.body);
      } else if (data.cmd == "disconnect") {
        broadcast('disconnect', data.body);
      } else if (data.cmd == "allPosters") {
        broadcast('allPosters', data.body);
      } else if (data.cmd == "paramHelperSearch") {
        broadcast('paramHelperSearch', data.body);
      } else if (data.cmd == "paramPostHelperSearch") {
        broadcast('paramPostHelperSearch', data.body);
      } else if (data.cmd == "deleteProvider") {
        broadcast('deleteProvider', data.body);
      } else if (data.cmd == "logout") {
        var body = {};
        body.title = "You have been disconnected from";
        body.footer = "Click here to be connected again.";
        body.isError = false;
        body.forceIdentification = true;
        broadcast('disconnect', body);
      } else {
        console.log("Command not yet implemented: ", data);
      }
    }
  }

}]);

return app;
});

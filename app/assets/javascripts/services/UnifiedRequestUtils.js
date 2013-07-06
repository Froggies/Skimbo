'use strict';

define(["app"], function(app) {

app.factory("UnifiedRequestUtils", function() {

  return {

    serverToClientUnifiedRequest: function(unifiedRequest) {
      var clientUnifiedRequest = {};
      clientUnifiedRequest.service = unifiedRequest.service;
      clientUnifiedRequest.providerName = unifiedRequest.service.split(".")[0];
      clientUnifiedRequest.serviceName = unifiedRequest.service.split(".")[1];
      clientUnifiedRequest.args = [];
      for (var key in unifiedRequest.args) {
        var value = unifiedRequest.args[key];
        clientUnifiedRequest.args.push({"key":key,"value":value});
      };
      clientUnifiedRequest.hasArguments = clientUnifiedRequest.args.length > 0;
      return clientUnifiedRequest;
    },

    serverToUnifiedRequest: function(unifiedRequest) {
      var clientUnifiedRequest = {};
      clientUnifiedRequest.service = unifiedRequest.service;
      clientUnifiedRequest.providerName = unifiedRequest.service.split(".")[0];
      clientUnifiedRequest.serviceName = unifiedRequest.service.split(".")[1];
      clientUnifiedRequest.hasParser = unifiedRequest.hasParser;
      clientUnifiedRequest.hasHelper = unifiedRequest.hasHelper;
      clientUnifiedRequest.args = []
      for (var index in unifiedRequest.args) {
        var key = unifiedRequest.args[index];
        clientUnifiedRequest.args.push({"key":key,"value":""});
      };
      clientUnifiedRequest.hasArguments = clientUnifiedRequest.args.length > 0;
      return clientUnifiedRequest;
    },

    clientToServerUnifiedRequest: function(unifiedRequest) {
      var serverUnifiedRequest = {};
      serverUnifiedRequest.service = unifiedRequest.providerName+"."+unifiedRequest.serviceName;
      serverUnifiedRequest.args = {}
      for (var index in unifiedRequest.args) {
        var arg = unifiedRequest.args[index];
        serverUnifiedRequest.args[arg.key] = arg.value;
      };
      return serverUnifiedRequest;
    }

  };

});

return app;
});
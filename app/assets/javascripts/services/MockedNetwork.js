'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", "$location", '$window',
  function($http, $timeout, $serverCommunication, $location, $window) {

  function addUserInfos() {
    var userInfos = {
      "cmd": "userInfos", 
      "body": {
        "username": "login",
        "name": "real name",
        "avatar": "",
        "description": "descriptione",
        "id": "1111",
        "socialType": "twitter"
      }
    }
    $serverCommunication.cmd(userInfos);
  }

  function addColumns() {
    var columns = {
      cmd: "allColumns",
      body: [
        {
          "title":"column1", 
          "unifiedRequests": [
            {
              "service":"twitter.wall",
              "args": {}
            }
          ],
          "index":0,
          "width":3,
          "height":3
        }
      ]
    };
    $serverCommunication.cmd(columns);
  }

  var id = 1;

  function addMsg() {
    var msg = {
      "cmd":"msg", 
      "body": {
        "column": "column1",
        "msg": {
          "authorName": "Froggies",
          "authorScreenName": "Skimbo",
          "message": "message n°"+id,
          "createdAt": new Date(),
          "comments": "",
          "shared": "5",
          "directLink": "",
          "sinceId": id+"",
          "authorAvatar": "",
          "from": "twitter",
          //TODO add into doc
          "idProvider": id
        }
      }
    };
    $serverCommunication.cmd(msg);
    id++;
  }

  $timeout(function() {
    addUserInfos();
    addColumns();
  }, 500);

  function msgDelaye() {
    $timeout(function() {
      addMsg();
      msgDelaye();
    }, 1500);
  };
  msgDelaye();

  function _send(jsonMsg) {
    
  }
  
  return {

    send: function(jsonMsg) {
      _send(jsonMsg);
    }


  };

}]);

return app;
});
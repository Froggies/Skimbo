'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", 'ArrayUtils', 'DataCache',
  function($http, $timeout, $serverCommunication, $arrayUtils, $dataCache) {

  //---------------------------------------------------\\
  //-------------------- VARIABLES --------------------\\
  //---------------------------------------------------\\

  var providers = [];
  var allUnifiedRequests = {'cmd': "allUnifiedRequests", 'body': []};
  var idMsg = 1;
  var twitterMsg = true;
  var columns = [];

  var providersServices = {
    "twitter": [
      {name: "wall", args: [], hasHelper: false}, 
      {name: "hashtag", args: ['hashtag'], hasHelper: false}, 
      {name: "user", args: ['username'], hasHelper: true}, 
      {name: "directMessage", args: [], hasHelper: false}, 
      {name: "messageToMe", args: [], hasHelper: false}
    ], 
    "facebook": [
      {name: "wall", args: [], hasHelper: false},
      {name: "notification", args: [], hasHelper: false},
      {name: "user", args: ['username'], hasHelper: true},
      {name: "group", args: ['groupId'], hasHelper: true},
      {name: "page", args: ['pageId'], hasHelper: true},
      {name: "message", args: [], hasHelper: false}
    ],
    "viadeo": [
      {name: "smartNews", args: [], hasHelper: false},
      {name: "newsfeed", args: [], hasHelper: false},
      {name: "homeNewsfeed", args: [], hasHelper: false},
      {name: "inbox", args: [], hasHelper: false}
    ],
    "linkedin": [
      {name: "wall", args: [], hasHelper: false}
    ],
    "googleplus": [
      {name: "wall", args: [], hasHelper: false},
      {name: "user", args: ['username'], hasHelper: true}
    ],
    "github": [
      {name: "userEvents", args: ['username'], hasHelper: true}
    ],
    "bitbucket": [
      {name: "eventsRepo", args: ['id'], hasHelper: true},
      {name: "commits", args: ['id'], hasHelper: true}
    ],
    "trello": [
      {name: "notifications", args: [], hasHelper: false}
    ],
    "scoopit": [
      {name: "wall", args: [], hasHelper: false},
      {name: "topic", args: ['id'], hasHelper: true}
    ],
    "betaseries": [
      {name: "planning", args: [], hasHelper: false},
      {name: "timeline", args: [], hasHelper: false}
    ],
    "rss": [
      {name: "rss", args: ['url'], hasHelper: false}
    ],
    "reddit": [
      {name: "hot", args: [], hasHelper: false},
      {name: "top", args: [], hasHelper: false},
      {name: "newer", args: [], hasHelper: false},
      {name: "subreddit", args: [], hasHelper: false}
    ]
  };

  //---------------------------------------------------------\\
  //-------------------- FUNCTIONS UTILS --------------------\\
  //---------------------------------------------------------\\

  function fillProvidersAndAllUnifiedRequests() {
    for(var provider in providersServices) {
      providers.push(provider);
      var p = {endpoint: provider, services: [], "hasToken": true};
      for(var i=0; i < providersServices[provider].length; i++) {
        var service = providersServices[provider][i];
        p.services.push({
          "service": provider+'.'+service.name,
          "args": service.args,
          "hasParser": true,
          "hasHelper": service.hasHelper
        });
      }
      allUnifiedRequests.body.push(p);
    }
  };

  function addUserInfos(provider) {
    var userInfos = {
      "cmd": "userInfos", 
      "body": {
        "username": provider,
        "name": "real name",
        "avatar": "",
        "description": "description",
        "id": "1111",
        "socialType": provider
      }
    }
    $serverCommunication.cmd(userInfos);
  }

  function addMsg(columnTitle, service) {
    var msg = {
      "cmd":"msg", 
      "body": {
        "column": columnTitle,
        "unifiedRequest": {
          "service": service,
          "args": [{name: "n", value:{call:"blabla"}}],
          "uidProviderUser": "12345"+service
        },
        "msg": {
          "authorName": "Froggies",
          "authorScreenName": "Skimbo",
          "message": "message n°"+idMsg,
          "createdAt": new Date(),
          "comments": [],//TODO transform string into array in doc
          "stared": "5",//TODO check shared into doc
          "directLink": "",
          "sinceId": idMsg+"",
          "authorAvatar": "",
          "from": service.split('.')[0],
          //TODO add into doc
          "idProvider": idMsg,
          "service": service,
          "hasDetails": false,
          "canStar": false,
          "iStared": true,
          "canComment": false,
          "picturesMin": [],
          "picturesMax": []
        }
      }
    };
    $serverCommunication.cmd(msg);
    idMsg++;
  }

  function msgDelaye() {
    $timeout(function() {
      if(columns.length > 0 && twitterMsg) {
        var c = $arrayUtils.random(columns);
        var s = $arrayUtils.random(c.unifiedRequests);
        addMsg(c.title, s.service);
      }
      msgDelaye();
    }, 3000);
  };

  //-----------------------------------------------------\\
  //-------------------- FILL VALUES --------------------\\
  //-----------------------------------------------------\\

  fillProvidersAndAllUnifiedRequests();
  msgDelaye();

  $timeout(function() {
    for (var i = 0; i < providers.length; i++) {
      addUserInfos(providers[i]);
    };
  }, 500);

  $timeout(function() {
    $serverCommunication.cmd({cmd: 'allColumns', body: []});
  }, 500);

  //-----------------------------------------------------\\
  //-------------------- APPLI CALLS --------------------\\
  //-----------------------------------------------------\\

  $dataCache.on('allColumns', function(c) {
    columns = c;
  });

  function _send(jsonMsg) {
    console.log(jsonMsg);
    if(jsonMsg) {
      if(jsonMsg.cmd == "pauseProvider") {
        twitterMsg = false;
      } else if(jsonMsg.cmd == "allUnifiedRequests") {
        $serverCommunication.cmd(allUnifiedRequests);
      } else if(jsonMsg.cmd == "addColumn") {
        $serverCommunication.cmd(jsonMsg);
      } else if(jsonMsg.cmd == "delColumn") {
        $serverCommunication.cmd(jsonMsg);
      } else if(jsonMsg.cmd == "modColumn") {
        $serverCommunication.cmd(jsonMsg);
      }  else if(jsonMsg.cmd == "deleteProvider") {
        $serverCommunication.cmd(jsonMsg);
      }
    }
  }
  
  return {

    send: function(jsonMsg) {
      _send(jsonMsg);
    }


  };

}]);

return app;
});
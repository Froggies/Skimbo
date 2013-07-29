'use strict';

define(["app"], function(app) {

app.factory("Network", ["$http", "$timeout", "ServerCommunication", "$location", '$window', 
  'ArrayUtils',
  function($http, $timeout, $serverCommunication, $location, $window, $arrayUtils) {

  var providers = ["twitter", "facebook", "googleplus", "github", "bitbucket", "viadeo", "linkedin",
    "betaseries", "rss", "scoopit", "trello", "reddit"];

  var services = {
    "twitter": ["wall", "hashtag", "user", "directMessage", "messageToMe"], 
    "facebook": ["wall", "notification", "user", "group", "page", "message"],
    "viadeo": ["smartNews", "newsfeed", "homeNewsfeed", "inbox"],
    "linkedIn": ["wall"],
    "googleplus": ["wall", "user"],
    "github": ["userEvents"],
    "bitbucket": ["eventsRepo", "commits"]
    // Endpoint(Trello, Seq(
    //   Configuration.Trello.notifications)),
    // Endpoint(Scoopit, Seq(
    //   Configuration.Scoopit.wall,
    //   Configuration.Scoopit.topic)),
    // Endpoint(BetaSeries, Seq(
    //   Configuration.BetaSeries.planning,
    //   Configuration.BetaSeries.timeline)),
    // Endpoint(RssProvider, Seq(
    //   Configuration.Rss.rss)),
    // Endpoint(Reddit, Seq(
    //   Configuration.Reddit.hot,
    //   Configuration.Reddit.top,
    //   Configuration.Reddit.newer,
    //   Configuration.Reddit.subreddit)
  };

  function addUserInfos(provider) {
    var userInfos = {
      "cmd": "userInfos", 
      "body": {
        "username": provider,
        "name": "real name",
        "avatar": "",
        "description": "descriptione",
        "id": "1111",
        "socialType": provider
      }
    }
    $serverCommunication.cmd(userInfos);
  }

  function addColumns(titles, provider, service) {
    var b = [];
    for (var i = 0; i < titles.length; i++) {
      b.push({
        "title":titles[i], 
        "unifiedRequests": [
          {
            "service":provider + "." + service,
            "args": {}
          }
        ],
        "index":0,
        "width":2,
        "height":1
      });
    };
    var columns = {
      cmd: "allColumns",
      body: b
    };
    $serverCommunication.cmd(columns);
  }

  var id = 1;

  function addMsg(columnTitle, provider) {
    var msg = {
      "cmd":"msg", 
      "body": {
        "column": columnTitle,
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
          "from": provider,
          //TODO add into doc
          "idProvider": id
        }
      }
    };
    $serverCommunication.cmd(msg);
    id++;
  }

  var twitterMsg = true;
  var columns = ["column12", "column11", "column10", "column9", "column8", "column7", 
    "column6", "column5", "column4", "column3", "column2", "column1"];

  $timeout(function() {
    for (var i = 0; i < providers.length; i++) {
      addUserInfos(providers[i]);
    };
    addColumns(columns, $arrayUtils.random(providers), "wall");
  }, 500);

  function msgDelaye() {
    $timeout(function() {
      if(twitterMsg) {
        addMsg($arrayUtils.random(columns), $arrayUtils.random(providers));
        msgDelaye();
      }
    }, 500);
  };
  msgDelaye();

  function _send(jsonMsg) {
    if(jsonMsg) {
      if(jsonMsg.cmd == "pauseProvider") {
        twitterMsg = false;
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
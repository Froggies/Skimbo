package github

object GithubFixture {

  val miniTimeLineForkEvent = 
    """
[{"actor":{
    "url":"https://api.github.com/users/marsupinole",
    "login":"marsupinole",
    "avatar_url":"https://secure.gravatar.com/avatar/ac620629c1d400ba8d10d182280eaf0e?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
    "gravatar_id":"ac620629c1d400ba8d10d182280eaf0e",
    "id":920264},
  "type":"ForkEvent",
  "repo":{
    "url":"https://api.github.com/repos/me/meteorirc",
    "name":"me/meteorirc",
    "id":4141969},
  "created_at":"2012-11-15T05:51:55Z",
  "public":true,
  "payload":{
    "forkee":{
      "description":"",
      "fork":true,
      "open_issues_count":0,
      "ssh_url":"git@github.com:marsupinole/meteorirc.git",
      "updated_at":"2012-11-15T05:51:55Z",
      "size":152,
      "full_name":"marsupinole/meteorirc",
      "watchers_count":0,
      "language":"JavaScript",
      "has_downloads":true,
      "homepage":null,
      "private":false,
      "watchers":0,
      "clone_url":"https://github.com/marsupinole/meteorirc.git",
      "created_at":"2012-11-15T05:51:55Z",
      "forks_count":0,
      "public":true,
      "svn_url":"https://github.com/marsupinole/meteorirc",
      "open_issues":0,
      "has_issues":false,
      "mirror_url":null,
      "url":"https://api.github.com/repos/marsupinole/meteorirc",
      "has_wiki":true,
      "html_url":"https://github.com/marsupinole/meteorirc",
      "forks":0,
      "id":6700146,
      "owner":{
        "login":"marsupinole",
        "gravatar_id":"ac620629c1d400ba8d10d182280eaf0e",
        "avatar_url":"https://secure.gravatar.com/avatar/ac620629c1d400ba8d10d182280eaf0e?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
        "url":"https://api.github.com/users/marsupinole",
        "id":920264},
      "git_url":"git://github.com/marsupinole/meteorirc.git",
      "name":"meteorirc",
      "pushed_at":"2012-04-26T23:49:20Z"}},
  "id":"1626921353"}]
    """
    
  val miniTimeLinePushEvent = 
    """
[{"actor":{
    "url":"https://api.github.com/users/Blaisorblade",
    "login":"Blaisorblade",
    "avatar_url":"https://secure.gravatar.com/avatar/a3a676c96a88feb813010e67af012ca0?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
    "gravatar_id":"a3a676c96a88feb813010e67af012ca0",
    "id":289960},
  "type":"PushEvent",
  "repo":{
    "url":"https://api.github.com/repos/ps-mr/LinqOnSteroids",
    "name":"ps-mr/LinqOnSteroids",
    "id":3493271},
  "created_at":"2012-10-06T18:12:54Z",
  "public":true,
  "org":{
    "url":"https://api.github.com/orgs/ps-mr",
    "login":"ps-mr",
    "avatar_url":"https://secure.gravatar.com/avatar/29338c5287111f160d72b932f929ff36?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
    "gravatar_id":"29338c5287111f160d72b932f929ff36",
    "id":1248609},
  "payload":{
    "ref":"refs/heads/cse",
    "size":1,
    "head":"2bca81a4f614471050283525f300cbf7c5eea486",
    "push_id":109475256,
    "before":"5e716acd7cd685d4a3fb659f87b82f453a2859fc",
    "commits":[{
      "author":{
        "email":"p.giarrusso@gmail.com",
        "name":"Paolo G. Giarrusso"},
      "sha":"2bca81a4f614471050283525f300cbf7c5eea486",
      "message":"Fix lifting of Vector.range in tests (does not affect evaluation)\n\nI had forgot the exact API of globalFmap, so that Vector.range(a, b)\nwas translated to Vector(a, b) and tests still passed! Fix this.",
      "distinct":true,
      "url":"https://api.github.com/repos/ps-mr/LinqOnSteroids/commits/2bca81a4f614471050283525f300cbf7c5eea486"}]},
  "id":"1607692892"}]
    """
  
  val miniTimeLineDownloadEvent = 
    """
[{"type":"DownloadEvent",
  "repo":{
    "url":"https://api.github.com/repos/ai/visibility.js",
    "id":1919264,
    "name":"ai/visibility.js"},
  "actor":{
    "url":"https://api.github.com/users/ai",
    "avatar_url":"https://secure.gravatar.com/avatar/cd32d17c95d3bfb352504c36462b98bd?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
    "gravatar_id":"cd32d17c95d3bfb352504c36462b98bd",
    "id":19343,
    "login":"ai"},
  "created_at":"2012-11-08T13:31:37Z",
  "public":true,
  "payload":{
    "download":{
      "size":3072,
      "html_url":"https://github.com/downloads/ai/visibility.js/visibility-0.6.1.min.js",
      "description":"",
      "download_count":0,
      "url":"https://api.github.com/repos/ai/visibility.js/downloads/356813",
      "id":356813,
      "content_type":"application/javascript",
      "created_at":"2012-11-08T13:31:37Z",
      "name":"visibility-0.6.1.min.js"}},
"id":"1623492622"}]
    """
    
  val miniTimeLineIssuesEvent = 
    """
  [
    {"type":"IssuesEvent",
    "public":true,
    "repo":{
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "id":6017536,"name":"Froggies/Skimbo"},
    "org":{
      "url":"https://api.github.com/orgs/Froggies",
      "id":2130294,
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "login":"Froggies"},
    "created_at":"2012-11-23T22:55:04Z",
    "payload":{
      "issue":{
        "labels":[{
          "name":"bug",
          "url":"https://api.github.com/repos/Froggies/Skimbo/labels/bug",
          "color":"fc2929"},
          {"name":"enhancement",
          "url":"https://api.github.com/repos/Froggies/Skimbo/labels/enhancement",
          "color":"84b6eb"}],
        "closed_at":"2012-11-23T22:55:03Z",
        "url":"https://api.github.com/repos/Froggies/Skimbo/issues/11",
        "user":{
          "url":"https://api.github.com/users/manland",
          "organizations_url":"https://api.github.com/users/manland/orgs",
          "events_url":"https://api.github.com/users/manland/events{/privacy}",
          "repos_url":"https://api.github.com/users/manland/repos","id":1492516,
          "followers_url":"https://api.github.com/users/manland/followers",
          "subscriptions_url":"https://api.github.com/users/manland/subscriptions",
          "following_url":"https://api.github.com/users/manland/following",
          "gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3",
          "received_events_url":"https://api.github.com/users/manland/received_events",
          "avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
          "gists_url":"https://api.github.com/users/manland/gists{/gist_id}",
          "starred_url":"https://api.github.com/users/manland/starred{/owner}{/repo}",
          "login":"manland"},
        "events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/events",
        "state":"closed",
        "id":8535508,
        "number":11,
        "assignee":null,
        "comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/comments",
        "milestone":null,
        "created_at":"2012-11-21T08:56:52Z",
        "updated_at":"2012-11-23T22:55:03Z",
        "body":"L'id de la colonne est son nom (oué on a choisit comme ça), du coup il faut checker avant d'envoyer au serveur que son nom est unique sinon ça modifiera (supprimera) les X colonnes qui ont le même nom.",
        "title":"Check du nom de la colonne qui doit être unique !",
        "comments":2,
        "labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/labels{/name}",
        "pull_request":{
          "patch_url":null,
          "diff_url":null,
          "html_url":null},
        "html_url":"https://github.com/Froggies/Skimbo/issues/11"},
      "action":"closed"},
    "id":"1631241492",
    "actor":{
      "url":"https://api.github.com/users/audreyn",
      "id":2131697,
      "gravatar_id":"353070ba39c118452072e68e3594f213",
      "avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "login":"audreyn"}}
  ]
    """
    
  val timelineIssueCommentEvent = 
    """
  [{"type":"IssueCommentEvent",
    "public":true,
    "repo":{
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "id":6017536,"name":"Froggies/Skimbo"},
    "org":{
      "url":"https://api.github.com/orgs/Froggies",
      "id":2130294,
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "login":"Froggies"},
    "created_at":"2012-11-23T22:55:04Z",
    "payload":{
      "comment":{
        "url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/10671300",
        "user":{
          "url":"https://api.github.com/users/audreyn",
          "events_url":"https://api.github.com/users/audreyn/events{/privacy}",
          "organizations_url":"https://api.github.com/users/audreyn/orgs",
          "id":2131697,
          "repos_url":"https://api.github.com/users/audreyn/repos",
          "followers_url":"https://api.github.com/users/audreyn/followers",
          "received_events_url":"https://api.github.com/users/audreyn/received_events",
          "subscriptions_url":"https://api.github.com/users/audreyn/subscriptions",
          "following_url":"https://api.github.com/users/audreyn/following",
          "gravatar_id":"353070ba39c118452072e68e3594f213",
          "avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
          "gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}",
          "login":"audreyn",
          "starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},
        "id":10671300,
        "updated_at":"2012-11-23T22:55:03Z",
        "body":"Voic la règle que j'ai mis en place : par défaut à la création, le nom de colonne est vide, il doit être rempli pour valider la création, et doit être unique.\r\nPush de cette modif effectué",
        "created_at":"2012-11-23T22:55:03Z"},
      "issue":{
        "labels":[{"name":"bug","url":"https://api.github.com/repos/Froggies/Skimbo/labels/bug","color":"fc2929"},{"name":"enhancement","url":"https://api.github.com/repos/Froggies/Skimbo/labels/enhancement","color":"84b6eb"}],
        "closed_at":"2012-11-23T22:55:03Z",
        "url":"https://api.github.com/repos/Froggies/Skimbo/issues/11",
        "events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/events",
        "user":{
          "url":"https://api.github.com/users/manland",
          "events_url":"https://api.github.com/users/manland/events{/privacy}",
          "organizations_url":"https://api.github.com/users/manland/orgs",
          "id":1492516,
          "repos_url":"https://api.github.com/users/manland/repos",
          "followers_url":"https://api.github.com/users/manland/followers",
          "received_events_url":"https://api.github.com/users/manland/received_events",
          "subscriptions_url":"https://api.github.com/users/manland/subscriptions",
          "following_url":"https://api.github.com/users/manland/following",
          "gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3",
          "avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
          "gists_url":"https://api.github.com/users/manland/gists{/gist_id}",
          "login":"manland",
          "starred_url":"https://api.github.com/users/manland/starred{/owner}{/repo}"},
        "id":8535508,
        "state":"closed",
        "assignee":null,
        "number":11,
        "comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/comments",
        "milestone":null,
        "updated_at":"2012-11-23T22:55:03Z",
        "body":"L'id de la colonne est son nom (oué on a choisit comme ça), du coup il faut checker avant d'envoyer au serveur que son nom est unique sinon ça modifiera (supprimera) les X colonnes qui ont le même nom.",
        "created_at":"2012-11-21T08:56:52Z",
        "title":"Check du nom de la colonne qui doit être unique !",
        "comments":2,
        "labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/labels{/name}",
        "pull_request":{"patch_url":null,"diff_url":null,"html_url":null},
        "html_url":"https://github.com/Froggies/Skimbo/issues/11"},
      "action":"created"},
    "id":"1631241491",
    "actor":{"url":"https://api.github.com/users/audreyn","id":2131697,"gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"audreyn"}}
    ]
    """
    
  val timelineDeleteEvent =
    """
  [
  {"type":"DeleteEvent",
    "public":true,
    "repo":{
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "id":6017536,
      "name":"Froggies/Skimbo"},
    "org":{
      "url":"https://api.github.com/orgs/Froggies",
      "id":2130294,
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "login":"Froggies"},
    "created_at":"2012-11-23T00:28:02Z",
    "payload":{
      "ref_type":"branch",
      "ref":"MIGRATION-2.1-RC1-BUGFINDING"},
    "id":"1630848261",
    "actor":{
      "url":"https://api.github.com/users/studiodev",
      "id":872095,
      "gravatar_id":"c2296a4512d6f4310768691075a6a673",
      "avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "login":"studiodev"}}
  ]
    """
    
  val timelineCreateEvent =
    """
    [
  {"type":"CreateEvent",
    "public":true,
    "repo":{
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "id":6017536,
      "name":"Froggies/Skimbo"},
    "org":{
      "url":"https://api.github.com/orgs/Froggies",
      "id":2130294,
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "login":"Froggies"},
    "created_at":"2012-11-22T22:48:32Z",
    "payload":{
      "ref_type":"branch",
      "ref":"MIGRATION-2.1-RC1-BUGFINDING",
      "master_branch":"master",
      "description":"Typesafe contest app"},
    "id":"1630830274",
    "actor":{
      "url":"https://api.github.com/users/studiodev",
      "id":872095,
      "gravatar_id":"c2296a4512d6f4310768691075a6a673",
      "avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "login":"studiodev"}}
    ]
    """
    
  val miniGollumnEvent = 
    """
  {
    "type":"GollumEvent",
    "org":{
      "id":2130294,
      "url":"https://api.github.com/orgs/Froggies",
      "login":"Froggies",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7"},
    "actor":{
      "id":1492516,
      "url":"https://api.github.com/users/manland",
      "login":"manland",
      "avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3"},
    "repo":{
      "id":6017536,
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "name":"Froggies/Skimbo"},
    "public":true,
    "payload":{
      "pages":[{
        "action":"created",
        "sha":"af6f3d5d5ca5a128b40b9e297a7dd55e7284da3c",
        "html_url":"https://github.com/Froggies/Skimbo/wiki/Features",
        "summary":null,
        "page_name":"Features",
        "title":"Features"}]},
    "created_at":"2012-12-09T12:34:08Z",
    "id":"1639167048"}
    """
    
  val miniWatchEvent = 
    """
  {"type":"WatchEvent",
    "org":{
      "id":2130294,
      "url":"https://api.github.com/orgs/Froggies",
      "login":"Froggies",
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7"},
    "actor":{
      "id":2130753,
      "url":"https://api.github.com/users/Adedib",
      "login":"Adedib",
      "avatar_url":"https://secure.gravatar.com/avatar/7be780eff9db7c2496db48645642c02a?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "gravatar_id":"7be780eff9db7c2496db48645642c02a"},
    "repo":{
      "id":6017536,
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "name":"Froggies/Skimbo"},
    "public":true,
    "payload":{"action":"started"},
    "created_at":"2012-12-09T17:33:36Z",
    "id":"1639219457"}
    """
    
  val commitCommentEvent = 
    """
  {"type":"CommitCommentEvent",
    "org":{
      "avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png",
      "url":"https://api.github.com/orgs/Froggies",
      "gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7",
      "id":2130294,
      "login":"Froggies"},
    "created_at":"2012-12-16T16:14:28Z",
    "public":true,
    "actor":{
      "avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
      "url":"https://api.github.com/users/manland",
      "gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3",
      "id":1492516,
      "login":"manland"},
    "repo":{
      "url":"https://api.github.com/repos/Froggies/Skimbo",
      "id":6017536,
      "name":"Froggies/Skimbo"},
    "payload":{
      "comment":{
        "url":"https://api.github.com/repos/Froggies/Skimbo/comments/2315443",
        "commit_id":"f6be865463b4395c76dc498c62f44d1b060c335d",
        "html_url":"https://github.com/Froggies/Skimbo/commit/f6be865463b4395c76dc498c62f44d1b060c335d#commitcomment-2315443",
        "user":{
          "followers_url":"https://api.github.com/users/manland/followers",
          "received_events_url":"https://api.github.com/users/manland/received_events",
          "url":"https://api.github.com/users/manland",
          "gists_url":"https://api.github.com/users/manland/gists{/gist_id}",
          "following_url":"https://api.github.com/users/manland/following",
          "gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3",
          "type":"User",
          "starred_url":"https://api.github.com/users/manland/starred{/owner}{/repo}",
          "avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
          "events_url":"https://api.github.com/users/manland/events{/privacy}",
          "repos_url":"https://api.github.com/users/manland/repos",
          "subscriptions_url":"https://api.github.com/users/manland/subscriptions",
          "id":1492516,
          "login":"manland",
          "organizations_url":"https://api.github.com/users/manland/orgs"},
        "path":"public/app/scripts/controllers/columns.js",
        "updated_at":"2012-12-16T16:14:28Z",
        "body":"Encore un test!",
        "created_at":"2012-12-16T16:14:28Z",
        "id":2315443,
        "line":63,
        "position":6}},
    "id":"1642941013"}
    """
    
  val timeline = 
    """
[
    {"type":"PushEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-23T22:53:16Z","payload":{"commits":[{"url":"https://api.github.com/repos/Froggies/Skimbo/commits/d70e156b0dd90b05cff9922f9fd051e77a5afb7a","message":"add validation on column title, its no longer possible to add a column with a blank name or with an existing name","sha":"d70e156b0dd90b05cff9922f9fd051e77a5afb7a","author":{"name":"Audrey","email":"novak.audrey@gmail.com"},"distinct":true},{"url":"https://api.github.com/repos/Froggies/Skimbo/commits/d14fcca44e2c66e7ad4bc9135575c201fd7f16ca","message":"Merge branch 'master' of https://github.com/Froggies/Skimbo\n\nConflicts:\n\tpublic/app/scripts/controllers/columns.js","sha":"d14fcca44e2c66e7ad4bc9135575c201fd7f16ca","author":{"name":"Audrey","email":"novak.audrey@gmail.com"},"distinct":true}],"size":2,"head":"d14fcca44e2c66e7ad4bc9135575c201fd7f16ca","before":"8a184ba7d2cebdc2b38e803675210b7ff18c8a7a","ref":"refs/heads/master","distinct_size":2,"push_id":122473563},"id":"1631241109","actor":{"url":"https://api.github.com/users/audreyn","id":2131697,"gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"audreyn"}},
    {"type":"IssueCommentEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-23T22:49:30Z","payload":{"comment":{"url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/10671234","user":{"url":"https://api.github.com/users/audreyn","events_url":"https://api.github.com/users/audreyn/events{/privacy}","organizations_url":"https://api.github.com/users/audreyn/orgs","id":2131697,"repos_url":"https://api.github.com/users/audreyn/repos","followers_url":"https://api.github.com/users/audreyn/followers","received_events_url":"https://api.github.com/users/audreyn/received_events","subscriptions_url":"https://api.github.com/users/audreyn/subscriptions","following_url":"https://api.github.com/users/audreyn/following","gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}","login":"audreyn","starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},"id":10671234,"updated_at":"2012-11-23T22:49:30Z","body":"Ca marche chez moi depuis plusieurs jours déjà. Je vais pusher les modifs que j'ai fait (plus possible d'ajouter une colonne avec le même nom, et validation si le champs du titre est vide).\r\nReteste après et dis-moi si tu as toujours ce problème ;)","created_at":"2012-11-23T22:49:30Z"},"issue":{"labels":[],"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/issues/15","events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/events","user":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"id":8618291,"number":15,"state":"open","assignee":null,"comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/comments","milestone":null,"updated_at":"2012-11-23T22:49:30Z","body":"C'est plus naturel, et ça fait gagner un clic !","created_at":"2012-11-23T22:29:04Z","title":"A la création d'une colone, l'afficher en mode configuration","comments":1,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/labels{/name}","pull_request":{"patch_url":null,"diff_url":null,"html_url":null},"html_url":"https://github.com/Froggies/Skimbo/issues/15"},"action":"created"},"id":"1631240271","actor":{"url":"https://api.github.com/users/audreyn","id":2131697,"gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"audreyn"}},
    {"type":"IssuesEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-23T22:29:04Z","payload":{"issue":{"labels":[],"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/issues/15","events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/events","user":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","received_events_url":"https://api.github.com/users/studiodev/received_events","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"id":8618291,"assignee":null,"number":15,"state":"open","comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/comments","milestone":null,"updated_at":"2012-11-23T22:29:04Z","body":"C'est plus naturel, et ça fait gagner un clic !","created_at":"2012-11-23T22:29:04Z","title":"A la création d'une colone, l'afficher en mode configuration","comments":0,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/15/labels{/name}","html_url":"https://github.com/Froggies/Skimbo/issues/15","pull_request":{"patch_url":null,"diff_url":null,"html_url":null}},"action":"opened"},"id":"1631235868","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"IssueCommentEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-23T20:09:08Z","payload":{"comment":{"url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/10668703","user":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","received_events_url":"https://api.github.com/users/studiodev/received_events","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}","login":"studiodev"},"id":10668703,"created_at":"2012-11-23T20:09:08Z","updated_at":"2012-11-23T20:09:08Z","body":"En priorité, nommer les nouvelles colones\r\nSkimbo1, Skimbo2, Skimbo3 (ou title1, title2, title3)\r\n\r\nCa limitera les problèmes.\r\nAprès empécher de mettre un nom de colonne déjà existant"},"issue":{"labels":[{"name":"bug","url":"https://api.github.com/repos/Froggies/Skimbo/labels/bug","color":"fc2929"},{"name":"enhancement","url":"https://api.github.com/repos/Froggies/Skimbo/labels/enhancement","color":"84b6eb"}],"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/issues/11","events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/events","user":{"url":"https://api.github.com/users/manland","events_url":"https://api.github.com/users/manland/events{/privacy}","organizations_url":"https://api.github.com/users/manland/orgs","id":1492516,"repos_url":"https://api.github.com/users/manland/repos","followers_url":"https://api.github.com/users/manland/followers","subscriptions_url":"https://api.github.com/users/manland/subscriptions","following_url":"https://api.github.com/users/manland/following","gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3","received_events_url":"https://api.github.com/users/manland/received_events","avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/manland/gists{/gist_id}","starred_url":"https://api.github.com/users/manland/starred{/owner}{/repo}","login":"manland"},"state":"open","id":8535508,"number":11,"assignee":null,"comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/comments","milestone":null,"created_at":"2012-11-21T08:56:52Z","updated_at":"2012-11-23T20:09:08Z","body":"L'id de la colonne est son nom (oué on a choisit comme ça), du coup il faut checker avant d'envoyer au serveur que son nom est unique sinon ça modifiera (supprimera) les X colonnes qui ont le même nom.","title":"Check du nom de la colonne qui doit être unique !","comments":1,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/11/labels{/name}","pull_request":{"patch_url":null,"diff_url":null,"html_url":null},"html_url":"https://github.com/Froggies/Skimbo/issues/11"},"action":"created"},"id":"1631200187","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"IssuesEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-23T20:08:09Z","payload":{"issue":{"labels":[],"closed_at":"2012-11-23T20:08:09Z","url":"https://api.github.com/repos/Froggies/Skimbo/issues/2","user":{"url":"https://api.github.com/users/manland","organizations_url":"https://api.github.com/users/manland/orgs","events_url":"https://api.github.com/users/manland/events{/privacy}","repos_url":"https://api.github.com/users/manland/repos","id":1492516,"followers_url":"https://api.github.com/users/manland/followers","subscriptions_url":"https://api.github.com/users/manland/subscriptions","following_url":"https://api.github.com/users/manland/following","gravatar_id":"c6b552a4cd47f7cf1701ea5b650cd2e3","received_events_url":"https://api.github.com/users/manland/received_events","avatar_url":"https://secure.gravatar.com/avatar/c6b552a4cd47f7cf1701ea5b650cd2e3?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/manland/gists{/gist_id}","starred_url":"https://api.github.com/users/manland/starred{/owner}{/repo}","login":"manland"},"events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/2/events","state":"closed","id":8118553,"number":2,"assignee":null,"comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/2/comments","milestone":null,"created_at":"2012-11-05T20:49:58Z","updated_at":"2012-11-23T20:08:09Z","body":"Si on fetch un provider sans token il ne se passe rien (je viens de passer 1 heure à debugger un truc qui fonctionne... -_-')","title":"No token does nothing","comments":2,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/2/labels{/name}","pull_request":{"patch_url":null,"diff_url":null,"html_url":null},"html_url":"https://github.com/Froggies/Skimbo/issues/2"},"action":"closed"},"id":"1631199937","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"DeleteEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T22:48:58Z","payload":{"ref_type":"branch","ref":"new-master"},"id":"1630830361","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"CreateEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T22:29:21Z","payload":{"ref_type":"branch","ref":"new-master","master_branch":"master","description":"Typesafe contest app"},"id":"1630826048","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"PullRequestEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T19:50:38Z","payload":{"number":14,"action":"closed","pull_request":{"closed_at":"2012-11-22T19:50:37Z","url":"https://api.github.com/repos/Froggies/Skimbo/pulls/14","review_comments_url":"https://github.com/Froggies/Skimbo/pull/14/comments","user":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"commits":2,"id":3100374,"state":"closed","assignee":null,"merged_by":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"number":14,"patch_url":"https://github.com/Froggies/Skimbo/pull/14.patch","comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/14/comments","commits_url":"https://github.com/Froggies/Skimbo/pull/14/commits","base":{"repo":{"name":"Skimbo","fork":false,"merges_url":"https://api.github.com/repos/Froggies/Skimbo/merges","url":"https://api.github.com/repos/Froggies/Skimbo","contents_url":"https://api.github.com/repos/Froggies/Skimbo/contents/{+path}","private":false,"notifications_url":"https://api.github.com/repos/Froggies/Skimbo/notifications{?since,all,participating}","pulls_url":"https://api.github.com/repos/Froggies/Skimbo/pulls{/number}","svn_url":"https://github.com/Froggies/Skimbo","full_name":"Froggies/Skimbo","events_url":"https://api.github.com/repos/Froggies/Skimbo/events","milestones_url":"https://api.github.com/repos/Froggies/Skimbo/milestones{/number}","keys_url":"https://api.github.com/repos/Froggies/Skimbo/keys{/key_id}","trees_url":"https://api.github.com/repos/Froggies/Skimbo/git/trees{/sha}","watchers_count":2,"id":6017536,"assignees_url":"https://api.github.com/repos/Froggies/Skimbo/assignees{/user}","issue_comment_url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/{number}","homepage":null,"git_commits_url":"https://api.github.com/repos/Froggies/Skimbo/git/commits{/sha}","comments_url":"https://api.github.com/repos/Froggies/Skimbo/comments{/number}","commits_url":"https://api.github.com/repos/Froggies/Skimbo/commits{/sha}","forks_url":"https://api.github.com/repos/Froggies/Skimbo/forks","pushed_at":"2012-11-22T19:50:37Z","git_url":"git://github.com/Froggies/Skimbo.git","languages_url":"https://api.github.com/repos/Froggies/Skimbo/languages","size":512,"forks_count":1,"updated_at":"2012-11-22T19:50:37Z","contributors_url":"https://api.github.com/repos/Froggies/Skimbo/contributors","collaborators_url":"https://api.github.com/repos/Froggies/Skimbo/collaborators{/collaborator}","subscribers_url":"https://api.github.com/repos/Froggies/Skimbo/subscribers","language":"Scala","downloads_url":"https://api.github.com/repos/Froggies/Skimbo/downloads","created_at":"2012-09-30T10:25:39Z","git_tags_url":"https://api.github.com/repos/Froggies/Skimbo/git/tags{/sha}","teams_url":"https://api.github.com/repos/Froggies/Skimbo/teams","has_wiki":true,"branches_url":"https://api.github.com/repos/Froggies/Skimbo/branches{/branch}","issues_url":"https://api.github.com/repos/Froggies/Skimbo/issues{/number}","hooks_url":"https://api.github.com/repos/Froggies/Skimbo/hooks","stargazers_url":"https://api.github.com/repos/Froggies/Skimbo/stargazers","has_issues":true,"ssh_url":"git@github.com:Froggies/Skimbo.git","archive_url":"https://api.github.com/repos/Froggies/Skimbo/{archive_format}{/ref}","clone_url":"https://github.com/Froggies/Skimbo.git","watchers":2,"subscription_url":"https://api.github.com/repos/Froggies/Skimbo/subscription","tags_url":"https://api.github.com/repos/Froggies/Skimbo/tags{/tag}","blobs_url":"https://api.github.com/repos/Froggies/Skimbo/git/blobs{/sha}","statuses_url":"https://api.github.com/repos/Froggies/Skimbo/statuses/{sha}","labels_url":"https://api.github.com/repos/Froggies/Skimbo/labels{/name}","issue_events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/events{/number}","git_refs_url":"https://api.github.com/repos/Froggies/Skimbo/git/refs{/sha}","has_downloads":true,"mirror_url":null,"compare_url":"https://api.github.com/repos/Froggies/Skimbo/compare/{base}...{head}","owner":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"open_issues_count":11,"open_issues":11,"html_url":"https://github.com/Froggies/Skimbo","description":"Typesafe contest app","forks":1},"user":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"label":"Froggies:master","sha":"c457c08211e453bcaf684beddae21eae796df9c2","ref":"master"},"milestone":null,"merge_commit_sha":"","mergeable_state":"unknown","updated_at":"2012-11-22T19:50:37Z","body":"","head":{"repo":{"name":"Skimbo","fork":false,"merges_url":"https://api.github.com/repos/Froggies/Skimbo/merges","url":"https://api.github.com/repos/Froggies/Skimbo","contents_url":"https://api.github.com/repos/Froggies/Skimbo/contents/{+path}","private":false,"notifications_url":"https://api.github.com/repos/Froggies/Skimbo/notifications{?since,all,participating}","pulls_url":"https://api.github.com/repos/Froggies/Skimbo/pulls{/number}","svn_url":"https://github.com/Froggies/Skimbo","full_name":"Froggies/Skimbo","events_url":"https://api.github.com/repos/Froggies/Skimbo/events","milestones_url":"https://api.github.com/repos/Froggies/Skimbo/milestones{/number}","keys_url":"https://api.github.com/repos/Froggies/Skimbo/keys{/key_id}","trees_url":"https://api.github.com/repos/Froggies/Skimbo/git/trees{/sha}","watchers_count":2,"id":6017536,"assignees_url":"https://api.github.com/repos/Froggies/Skimbo/assignees{/user}","issue_comment_url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/{number}","homepage":null,"git_commits_url":"https://api.github.com/repos/Froggies/Skimbo/git/commits{/sha}","comments_url":"https://api.github.com/repos/Froggies/Skimbo/comments{/number}","commits_url":"https://api.github.com/repos/Froggies/Skimbo/commits{/sha}","forks_url":"https://api.github.com/repos/Froggies/Skimbo/forks","pushed_at":"2012-11-22T19:50:37Z","git_url":"git://github.com/Froggies/Skimbo.git","languages_url":"https://api.github.com/repos/Froggies/Skimbo/languages","size":512,"forks_count":1,"updated_at":"2012-11-22T19:50:37Z","contributors_url":"https://api.github.com/repos/Froggies/Skimbo/contributors","collaborators_url":"https://api.github.com/repos/Froggies/Skimbo/collaborators{/collaborator}","subscribers_url":"https://api.github.com/repos/Froggies/Skimbo/subscribers","language":"Scala","downloads_url":"https://api.github.com/repos/Froggies/Skimbo/downloads","created_at":"2012-09-30T10:25:39Z","git_tags_url":"https://api.github.com/repos/Froggies/Skimbo/git/tags{/sha}","teams_url":"https://api.github.com/repos/Froggies/Skimbo/teams","has_wiki":true,"branches_url":"https://api.github.com/repos/Froggies/Skimbo/branches{/branch}","issues_url":"https://api.github.com/repos/Froggies/Skimbo/issues{/number}","hooks_url":"https://api.github.com/repos/Froggies/Skimbo/hooks","stargazers_url":"https://api.github.com/repos/Froggies/Skimbo/stargazers","has_issues":true,"ssh_url":"git@github.com:Froggies/Skimbo.git","archive_url":"https://api.github.com/repos/Froggies/Skimbo/{archive_format}{/ref}","clone_url":"https://github.com/Froggies/Skimbo.git","watchers":2,"subscription_url":"https://api.github.com/repos/Froggies/Skimbo/subscription","tags_url":"https://api.github.com/repos/Froggies/Skimbo/tags{/tag}","blobs_url":"https://api.github.com/repos/Froggies/Skimbo/git/blobs{/sha}","statuses_url":"https://api.github.com/repos/Froggies/Skimbo/statuses/{sha}","labels_url":"https://api.github.com/repos/Froggies/Skimbo/labels{/name}","issue_events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/events{/number}","git_refs_url":"https://api.github.com/repos/Froggies/Skimbo/git/refs{/sha}","has_downloads":true,"mirror_url":null,"compare_url":"https://api.github.com/repos/Froggies/Skimbo/compare/{base}...{head}","owner":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"open_issues_count":11,"open_issues":11,"html_url":"https://github.com/Froggies/Skimbo","description":"Typesafe contest app","forks":1},"user":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"label":"Froggies:MIGRATION-2.1-RC1","sha":"c3c96c255426975806c88e2da9f4614c9a826112","ref":"MIGRATION-2.1-RC1"},"created_at":"2012-11-22T19:24:55Z","changed_files":38,"title":"Migration 2.1 rc1","merged_at":"2012-11-22T19:50:37Z","review_comment_url":"/repos/Froggies/Skimbo/pulls/comments/{number}","comments":0,"deletions":323,"additions":213,"diff_url":"https://github.com/Froggies/Skimbo/pull/14.diff","_links":{"issue":{"href":"https://api.github.com/repos/Froggies/Skimbo/issues/14"},"html":{"href":"https://github.com/Froggies/Skimbo/pull/14"},"comments":{"href":"https://api.github.com/repos/Froggies/Skimbo/issues/14/comments"},"self":{"href":"https://api.github.com/repos/Froggies/Skimbo/pulls/14"},"review_comments":{"href":"https://api.github.com/repos/Froggies/Skimbo/pulls/14/comments"}},"review_comments":0,"issue_url":"https://github.com/Froggies/Skimbo/issues/14","mergeable":null,"merged":true,"html_url":"https://github.com/Froggies/Skimbo/pull/14"}},"id":"1630785374","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"PullRequestEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T19:27:37Z","payload":{"number":14,"action":"reopened","pull_request":{"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/pulls/14","review_comments_url":"https://github.com/Froggies/Skimbo/pull/14/comments","commits":2,"user":{"url":"https://api.github.com/users/studiodev","events_url":"https://api.github.com/users/studiodev/events{/privacy}","organizations_url":"https://api.github.com/users/studiodev/orgs","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"id":3100374,"merged_by":null,"assignee":null,"number":14,"state":"open","patch_url":"https://github.com/Froggies/Skimbo/pull/14.patch","comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/14/comments","base":{"repo":{"name":"Skimbo","fork":false,"merges_url":"https://api.github.com/repos/Froggies/Skimbo/merges","url":"https://api.github.com/repos/Froggies/Skimbo","contents_url":"https://api.github.com/repos/Froggies/Skimbo/contents/{+path}","private":false,"notifications_url":"https://api.github.com/repos/Froggies/Skimbo/notifications{?since,all,participating}","pulls_url":"https://api.github.com/repos/Froggies/Skimbo/pulls{/number}","full_name":"Froggies/Skimbo","events_url":"https://api.github.com/repos/Froggies/Skimbo/events","milestones_url":"https://api.github.com/repos/Froggies/Skimbo/milestones{/number}","keys_url":"https://api.github.com/repos/Froggies/Skimbo/keys{/key_id}","svn_url":"https://github.com/Froggies/Skimbo","trees_url":"https://api.github.com/repos/Froggies/Skimbo/git/trees{/sha}","watchers_count":2,"id":6017536,"assignees_url":"https://api.github.com/repos/Froggies/Skimbo/assignees{/user}","issue_comment_url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/{number}","homepage":null,"git_commits_url":"https://api.github.com/repos/Froggies/Skimbo/git/commits{/sha}","comments_url":"https://api.github.com/repos/Froggies/Skimbo/comments{/number}","commits_url":"https://api.github.com/repos/Froggies/Skimbo/commits{/sha}","forks_url":"https://api.github.com/repos/Froggies/Skimbo/forks","pushed_at":"2012-11-22T19:24:09Z","git_url":"git://github.com/Froggies/Skimbo.git","languages_url":"https://api.github.com/repos/Froggies/Skimbo/languages","size":512,"forks_count":1,"updated_at":"2012-11-22T19:24:09Z","contributors_url":"https://api.github.com/repos/Froggies/Skimbo/contributors","collaborators_url":"https://api.github.com/repos/Froggies/Skimbo/collaborators{/collaborator}","subscribers_url":"https://api.github.com/repos/Froggies/Skimbo/subscribers","language":"Scala","downloads_url":"https://api.github.com/repos/Froggies/Skimbo/downloads","created_at":"2012-09-30T10:25:39Z","git_tags_url":"https://api.github.com/repos/Froggies/Skimbo/git/tags{/sha}","teams_url":"https://api.github.com/repos/Froggies/Skimbo/teams","has_wiki":true,"branches_url":"https://api.github.com/repos/Froggies/Skimbo/branches{/branch}","issues_url":"https://api.github.com/repos/Froggies/Skimbo/issues{/number}","hooks_url":"https://api.github.com/repos/Froggies/Skimbo/hooks","stargazers_url":"https://api.github.com/repos/Froggies/Skimbo/stargazers","has_issues":true,"ssh_url":"git@github.com:Froggies/Skimbo.git","archive_url":"https://api.github.com/repos/Froggies/Skimbo/{archive_format}{/ref}","clone_url":"https://github.com/Froggies/Skimbo.git","watchers":2,"subscription_url":"https://api.github.com/repos/Froggies/Skimbo/subscription","tags_url":"https://api.github.com/repos/Froggies/Skimbo/tags{/tag}","blobs_url":"https://api.github.com/repos/Froggies/Skimbo/git/blobs{/sha}","statuses_url":"https://api.github.com/repos/Froggies/Skimbo/statuses/{sha}","labels_url":"https://api.github.com/repos/Froggies/Skimbo/labels{/name}","issue_events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/events{/number}","git_refs_url":"https://api.github.com/repos/Froggies/Skimbo/git/refs{/sha}","mirror_url":null,"has_downloads":true,"compare_url":"https://api.github.com/repos/Froggies/Skimbo/compare/{base}...{head}","owner":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"html_url":"https://github.com/Froggies/Skimbo","open_issues_count":12,"open_issues":12,"description":"Typesafe contest app","forks":1},"user":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"label":"Froggies:master","sha":"c457c08211e453bcaf684beddae21eae796df9c2","ref":"master"},"milestone":null,"commits_url":"https://github.com/Froggies/Skimbo/pull/14/commits","merge_commit_sha":"","mergeable_state":"unknown","updated_at":"2012-11-22T19:27:36Z","body":"","head":{"repo":{"name":"Skimbo","fork":false,"merges_url":"https://api.github.com/repos/Froggies/Skimbo/merges","url":"https://api.github.com/repos/Froggies/Skimbo","contents_url":"https://api.github.com/repos/Froggies/Skimbo/contents/{+path}","private":false,"notifications_url":"https://api.github.com/repos/Froggies/Skimbo/notifications{?since,all,participating}","pulls_url":"https://api.github.com/repos/Froggies/Skimbo/pulls{/number}","full_name":"Froggies/Skimbo","events_url":"https://api.github.com/repos/Froggies/Skimbo/events","milestones_url":"https://api.github.com/repos/Froggies/Skimbo/milestones{/number}","keys_url":"https://api.github.com/repos/Froggies/Skimbo/keys{/key_id}","svn_url":"https://github.com/Froggies/Skimbo","trees_url":"https://api.github.com/repos/Froggies/Skimbo/git/trees{/sha}","watchers_count":2,"id":6017536,"assignees_url":"https://api.github.com/repos/Froggies/Skimbo/assignees{/user}","issue_comment_url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/{number}","homepage":null,"git_commits_url":"https://api.github.com/repos/Froggies/Skimbo/git/commits{/sha}","comments_url":"https://api.github.com/repos/Froggies/Skimbo/comments{/number}","commits_url":"https://api.github.com/repos/Froggies/Skimbo/commits{/sha}","forks_url":"https://api.github.com/repos/Froggies/Skimbo/forks","pushed_at":"2012-11-22T19:24:09Z","git_url":"git://github.com/Froggies/Skimbo.git","languages_url":"https://api.github.com/repos/Froggies/Skimbo/languages","size":512,"forks_count":1,"updated_at":"2012-11-22T19:24:09Z","contributors_url":"https://api.github.com/repos/Froggies/Skimbo/contributors","collaborators_url":"https://api.github.com/repos/Froggies/Skimbo/collaborators{/collaborator}","subscribers_url":"https://api.github.com/repos/Froggies/Skimbo/subscribers","language":"Scala","downloads_url":"https://api.github.com/repos/Froggies/Skimbo/downloads","created_at":"2012-09-30T10:25:39Z","git_tags_url":"https://api.github.com/repos/Froggies/Skimbo/git/tags{/sha}","teams_url":"https://api.github.com/repos/Froggies/Skimbo/teams","has_wiki":true,"branches_url":"https://api.github.com/repos/Froggies/Skimbo/branches{/branch}","issues_url":"https://api.github.com/repos/Froggies/Skimbo/issues{/number}","hooks_url":"https://api.github.com/repos/Froggies/Skimbo/hooks","stargazers_url":"https://api.github.com/repos/Froggies/Skimbo/stargazers","has_issues":true,"ssh_url":"git@github.com:Froggies/Skimbo.git","archive_url":"https://api.github.com/repos/Froggies/Skimbo/{archive_format}{/ref}","clone_url":"https://github.com/Froggies/Skimbo.git","watchers":2,"subscription_url":"https://api.github.com/repos/Froggies/Skimbo/subscription","tags_url":"https://api.github.com/repos/Froggies/Skimbo/tags{/tag}","blobs_url":"https://api.github.com/repos/Froggies/Skimbo/git/blobs{/sha}","statuses_url":"https://api.github.com/repos/Froggies/Skimbo/statuses/{sha}","labels_url":"https://api.github.com/repos/Froggies/Skimbo/labels{/name}","issue_events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/events{/number}","git_refs_url":"https://api.github.com/repos/Froggies/Skimbo/git/refs{/sha}","mirror_url":null,"has_downloads":true,"compare_url":"https://api.github.com/repos/Froggies/Skimbo/compare/{base}...{head}","owner":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"html_url":"https://github.com/Froggies/Skimbo","open_issues_count":12,"open_issues":12,"description":"Typesafe contest app","forks":1},"user":{"url":"https://api.github.com/users/Froggies","events_url":"https://api.github.com/users/Froggies/events{/privacy}","organizations_url":"https://api.github.com/users/Froggies/orgs","id":2130294,"repos_url":"https://api.github.com/users/Froggies/repos","followers_url":"https://api.github.com/users/Froggies/followers","received_events_url":"https://api.github.com/users/Froggies/received_events","subscriptions_url":"https://api.github.com/users/Froggies/subscriptions","following_url":"https://api.github.com/users/Froggies/following","gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","gists_url":"https://api.github.com/users/Froggies/gists{/gist_id}","login":"Froggies","starred_url":"https://api.github.com/users/Froggies/starred{/owner}{/repo}"},"label":"Froggies:MIGRATION-2.1-RC1","sha":"c3c96c255426975806c88e2da9f4614c9a826112","ref":"MIGRATION-2.1-RC1"},"created_at":"2012-11-22T19:24:55Z","merged_at":null,"changed_files":38,"title":"Migration 2.1 rc1","review_comment_url":"/repos/Froggies/Skimbo/pulls/comments/{number}","comments":0,"deletions":323,"additions":213,"diff_url":"https://github.com/Froggies/Skimbo/pull/14.diff","_links":{"issue":{"href":"https://api.github.com/repos/Froggies/Skimbo/issues/14"},"html":{"href":"https://github.com/Froggies/Skimbo/pull/14"},"comments":{"href":"https://api.github.com/repos/Froggies/Skimbo/issues/14/comments"},"self":{"href":"https://api.github.com/repos/Froggies/Skimbo/pulls/14"},"review_comments":{"href":"https://api.github.com/repos/Froggies/Skimbo/pulls/14/comments"}},"review_comments":0,"mergeable":null,"issue_url":"https://github.com/Froggies/Skimbo/issues/14","html_url":"https://github.com/Froggies/Skimbo/pull/14","merged":false}},"id":"1630779048","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"PushEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T19:24:10Z","payload":{"commits":[{"url":"https://api.github.com/repos/Froggies/Skimbo/commits/c3c96c255426975806c88e2da9f4614c9a826112","message":"Rewrite Json things","sha":"c3c96c255426975806c88e2da9f4614c9a826112","author":{"name":"Julien Lafont","email":"julien.lafont@studio-dev.fr"},"distinct":true}],"size":1,"head":"c3c96c255426975806c88e2da9f4614c9a826112","before":"713955461ff591ee96a56600dcb2bc31b7ee2103","distinct_size":1,"ref":"refs/heads/MIGRATION-2.1-RC1","push_id":122222196},"id":"1630778108","actor":{"url":"https://api.github.com/users/studiodev","id":872095,"gravatar_id":"c2296a4512d6f4310768691075a6a673","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"studiodev"}},
    {"type":"IssueCommentEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T18:19:03Z","payload":{"comment":{"url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/10641982","user":{"url":"https://api.github.com/users/audreyn","organizations_url":"https://api.github.com/users/audreyn/orgs","events_url":"https://api.github.com/users/audreyn/events{/privacy}","id":2131697,"repos_url":"https://api.github.com/users/audreyn/repos","followers_url":"https://api.github.com/users/audreyn/followers","gravatar_id":"353070ba39c118452072e68e3594f213","received_events_url":"https://api.github.com/users/audreyn/received_events","subscriptions_url":"https://api.github.com/users/audreyn/subscriptions","following_url":"https://api.github.com/users/audreyn/following","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}","login":"audreyn","starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},"id":10641982,"updated_at":"2012-11-22T18:19:03Z","body":"Tu t'en es chargé ou je le fait ? (pour romain)","created_at":"2012-11-22T18:19:03Z"},"issue":{"labels":[{"name":"bug","url":"https://api.github.com/repos/Froggies/Skimbo/labels/bug","color":"fc2929"}],"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/issues/6","events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/events","user":{"url":"https://api.github.com/users/studiodev","organizations_url":"https://api.github.com/users/studiodev/orgs","events_url":"https://api.github.com/users/studiodev/events{/privacy}","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","gravatar_id":"c2296a4512d6f4310768691075a6a673","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"id":8523887,"number":6,"state":"open","assignee":{"url":"https://api.github.com/users/audreyn","organizations_url":"https://api.github.com/users/audreyn/orgs","events_url":"https://api.github.com/users/audreyn/events{/privacy}","id":2131697,"repos_url":"https://api.github.com/users/audreyn/repos","followers_url":"https://api.github.com/users/audreyn/followers","gravatar_id":"353070ba39c118452072e68e3594f213","received_events_url":"https://api.github.com/users/audreyn/received_events","subscriptions_url":"https://api.github.com/users/audreyn/subscriptions","following_url":"https://api.github.com/users/audreyn/following","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}","login":"audreyn","starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},"comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/comments","milestone":null,"updated_at":"2012-11-22T18:19:03Z","body":"Perte du focus due à AngularJS lors de la saisie d'une config","created_at":"2012-11-20T22:50:34Z","title":"Perte du focus lors de la saisie du hastag/user/etc","comments":3,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/labels{/name}","html_url":"https://github.com/Froggies/Skimbo/issues/6","pull_request":{"patch_url":null,"diff_url":null,"html_url":null}},"action":"created"},"id":"1630759655","actor":{"url":"https://api.github.com/users/audreyn","id":2131697,"gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"audreyn"}},
    {"type":"IssueCommentEvent","public":true,"repo":{"url":"https://api.github.com/repos/Froggies/Skimbo","id":6017536,"name":"Froggies/Skimbo"},"org":{"url":"https://api.github.com/orgs/Froggies","id":2130294,"gravatar_id":"6562ef61dbcfb7174e9adb63f38345e7","avatar_url":"https://secure.gravatar.com/avatar/6562ef61dbcfb7174e9adb63f38345e7?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-org-420.png","login":"Froggies"},"created_at":"2012-11-22T12:45:47Z","payload":{"comment":{"url":"https://api.github.com/repos/Froggies/Skimbo/issues/comments/10633354","user":{"url":"https://api.github.com/users/audreyn","organizations_url":"https://api.github.com/users/audreyn/orgs","events_url":"https://api.github.com/users/audreyn/events{/privacy}","id":2131697,"repos_url":"https://api.github.com/users/audreyn/repos","followers_url":"https://api.github.com/users/audreyn/followers","gravatar_id":"353070ba39c118452072e68e3594f213","received_events_url":"https://api.github.com/users/audreyn/received_events","subscriptions_url":"https://api.github.com/users/audreyn/subscriptions","following_url":"https://api.github.com/users/audreyn/following","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}","login":"audreyn","starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},"id":10633354,"updated_at":"2012-11-22T12:45:46Z","body":"Merci pour le lien, je regarde ça ce soir !","created_at":"2012-11-22T12:45:46Z"},"issue":{"labels":[{"name":"bug","url":"https://api.github.com/repos/Froggies/Skimbo/labels/bug","color":"fc2929"}],"closed_at":null,"url":"https://api.github.com/repos/Froggies/Skimbo/issues/6","events_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/events","user":{"url":"https://api.github.com/users/studiodev","organizations_url":"https://api.github.com/users/studiodev/orgs","events_url":"https://api.github.com/users/studiodev/events{/privacy}","id":872095,"repos_url":"https://api.github.com/users/studiodev/repos","followers_url":"https://api.github.com/users/studiodev/followers","gravatar_id":"c2296a4512d6f4310768691075a6a673","received_events_url":"https://api.github.com/users/studiodev/received_events","subscriptions_url":"https://api.github.com/users/studiodev/subscriptions","following_url":"https://api.github.com/users/studiodev/following","avatar_url":"https://secure.gravatar.com/avatar/c2296a4512d6f4310768691075a6a673?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/studiodev/gists{/gist_id}","login":"studiodev","starred_url":"https://api.github.com/users/studiodev/starred{/owner}{/repo}"},"id":8523887,"number":6,"state":"open","assignee":{"url":"https://api.github.com/users/audreyn","organizations_url":"https://api.github.com/users/audreyn/orgs","events_url":"https://api.github.com/users/audreyn/events{/privacy}","id":2131697,"repos_url":"https://api.github.com/users/audreyn/repos","followers_url":"https://api.github.com/users/audreyn/followers","gravatar_id":"353070ba39c118452072e68e3594f213","received_events_url":"https://api.github.com/users/audreyn/received_events","subscriptions_url":"https://api.github.com/users/audreyn/subscriptions","following_url":"https://api.github.com/users/audreyn/following","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","gists_url":"https://api.github.com/users/audreyn/gists{/gist_id}","login":"audreyn","starred_url":"https://api.github.com/users/audreyn/starred{/owner}{/repo}"},"comments_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/comments","milestone":null,"updated_at":"2012-11-22T12:45:46Z","body":"Perte du focus due à AngularJS lors de la saisie d'une config","created_at":"2012-11-20T22:50:34Z","title":"Perte du focus lors de la saisie du hastag/user/etc","comments":2,"labels_url":"https://api.github.com/repos/Froggies/Skimbo/issues/6/labels{/name}","html_url":"https://github.com/Froggies/Skimbo/issues/6","pull_request":{"patch_url":null,"diff_url":null,"html_url":null}},"action":"created"},"id":"1630609321","actor":{"url":"https://api.github.com/users/audreyn","id":2131697,"gravatar_id":"353070ba39c118452072e68e3594f213","avatar_url":"https://secure.gravatar.com/avatar/353070ba39c118452072e68e3594f213?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png","login":"audreyn"}}
]
    """
  
}
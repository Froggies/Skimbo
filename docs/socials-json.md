## Intégration API Réseaus sociaux

 > [Dump des flux JSON d'activités](https://gist.github.com/3810519)

## Twitter

 * *home_timeline* : `https://api.twitter.com/1.1/statuses/home_timeline.json` ([DOC](https://dev.twitter.com/docs/api/1.1))

## Facebook

 * *home* : `https://graph.facebook.com/me/home` ([DOC](https://developers.facebook.com/docs/reference/api/user/#home))

Autre flux possibles (intéressant à intégrer pour avoir plus d'infos, dans le flux principal ou à part) : 
 * [friendrequests](https://developers.facebook.com/docs/reference/api/user/#friendrequests)
 * [notifications](https://developers.facebook.com/docs/reference/api/user/#notifications)

## Google+

 * *activities* : `https://www.googleapis.com/plus/v1/people/me/activities/public` ([DOC](https://developers.google.com/+/api/latest/activities/list))

Seules les activités publiques sont affichées

##  Viadeo

* *smart_news* : `https://api.viadeo.com/me/smart_news.json` ([DOC](http://dev.viadeo.com/graph-api-resource/?resource=%2Fuser%2FID%2Fsmart_news))

Autres flux possibles :
 * [home_newsfeed](http://dev.viadeo.com/documentation/graph-api-resource/?resource=/user/ID/home_newsfeed/)
 * [inbox](http://dev.viadeo.com/documentation/graph-api-resource/?resource=/user/ID/inbox/)
 * [newsfeed](http://dev.viadeo.com/documentation/graph-api-resource/?resource=/user/ID/newsfeed/) 

## LinkedIn

* *network updates* : `http://api.linkedin.com/v1/people/~/network/updates` ([DOC](https://developer.linkedin.com/documents/get-network-updates-and-statistics-api))

Autres flux possibles :
 * [invitations](https://developer.linkedin.com/documents/invitation-api)

## Github

 * *user detail* : `https://api.github.com/user` ([DOC](http://developer.github.com/v3/users/#get-a-single-user))
 * *events received* : `https://api.github.com/users/:user/received_events` ([DOC](http://developer.github.com/v3/events/#list-events-that-a-user-has-received))

## StackExchange

 * *inbox* : `https://api.stackexchange.com/2.1/inbox` ([DOC](https://api.stackexchange.com/docs/inbox))
 * *notifications* : `https://api.stackexchange.com/2.1/notifications` ([DOC](https://api.stackexchange.com/docs/notifications))

## Trello

 * *notifications* : `https://api.trello.com/1/members/me/notifications` ([DOC](https://trello.com/docs/api/notification/index.html))
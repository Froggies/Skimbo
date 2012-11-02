# Skimbo

> The main problem that our application solves is that most user are member of several social networks so to follow every changes on each social networks, users have to open one tab for each ones, which is not very convenient. Thanks to our application, the user can follow all its social networks, in a unique window, thanks to a new kind of display.

> We are going to use Play2 and Scala, the main things our server will do is : open authentification, usage of web services in restFull mode. To show the power of scala and play2, we will send social activities in real time to user through websocket and iteratees.

App created during the [Typesafe contest app](http://blog.typesafe.com/typesafe-developer-contest) with [Play2](https://github.com/playframework/play20/)

# Team
 * [@udr3y](https://twitter.com/udr3y)
 * [@RmManeschi](https://twitter.com/RmManeschi)
 * [@studiodev](https://twitter.com/studiodev)
 * [@vp3n](https://twitter.com/vp3n)
 * Laurent Dufour (This guy is not on twitter !!)

# Test app
  * launch server
  * go on 127.0.0.1:9000
  * if login page -> choose provider
  * and then column page with provider(s) call
  * click on logout -> must stop stream !

# Technical
	* on logon : UserInfosActor start for retreive users infos (in bd if exist, else call provider(s))
	* on endpoint start (sse for now in Application.scala/testActor2()) start ProviderActor
	* on logout shutdown all ProviderActor for user 
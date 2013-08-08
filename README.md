Skimbo [![Build Status](https://travis-ci.org/Froggies/Skimbo.png?branch=master)](https://travis-ci.org/Froggies/Skimbo)
==========

This app was created during the Typesafe contest app with Play2. Test it on Demo_

For the Typesafe contest, we made a web application *Skimbo* : a social networks mashup. 
The main problem that our application solves is that most user are members of several social networks so to follow every changes on each social network, users have to open one tab for each one, which is not very convenient. Thanks to our application, the user can follow all its social networks, in a unique window, thanks to a new kind of display.

This application fully uses the [Typesafe stack](http://typesafe.com/platform) : Scala, Play 2.x, Akka, with bonus Reactivemongo for the MongoDB link, WebSocket but if the client doesn't support WebSocket, Server Send Event is used. The client is made with AngularJs.


How to install ?
---------------

**Prerequisite :**

- Play 2.1-RC1
- MongoDb > 2.0
- A browser which suppport WebSocket and/or Sse

**Installation :**

- Get the project
- Add your secret keys for all the social networks in a file called : "**conf/my-social.conf**" with the following structure : https://gist.github.com/4172803
- You can add Linkedin for example, which does not check the domain (https://gist.github.com/3c2b283adf15be148fdf), or create your own apps (oauth1/2 token)
- Start mongoDB server (mongoDb conf can be modified in application.conf)
- cd Skimbo
- play run

That's all ;)

Froggies, our Team :
-----

Follow us on twitter :

* [Skimbo](https://twitter.com/skimbo34)
* [udr3y](https://twitter.com/udr3y)
* [RmManeschi](https://twitter.com/RmManeschi)
* [studiodev](https://twitter.com/studiodev)
* [vp3n]( https://twitter.com/vp3n)
* [LaurentDufour](https://twitter.com/_LaurentDufour)

Licence ![License](http://www.gnu.org/graphics/agplv3-155x51.png)
-------

Copyright 2012-2013 Froggies Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.






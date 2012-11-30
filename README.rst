Skimbo
=====

This app was created during the Typesafe_ contest app with Play2_.
-----

For the Typesafe contest, we made a web application **Skimbo** : a social networks mashup. 
The main problem that our application solves is that most user are members of several social networks so to follow every changes on each social network, users have to open one tab for each one, which is not very convenient. Thanks to our application, the user can follow all its social networks, in a unique window, thanks to a new kind of display.

This application fully uses the **Typesafe stack** : Scala, Play 2.1-RC1, Akka, with bonus Reactivemongo for the MongoDB link, WebSocket but if the client doesn't support WebSocket, Server Send Event is used. The client is made with AngularJs.

**So why those technologies ?**
-------- 

Find out new things is our favorite hobby ! The Typesafe contest was a real challenge, because it's based on a technologic stack in which we were debutants. Almost all developpers in our team had never written a piece of scala code before the contest. Those notions : actor/asynchronus/real time/reactive model were really new for us. 

Scala language was a revelation in the use of our so dear, but so dusty JVM.
It's really fantastic to find a language which with only a few lines of code allows to write what we imagine in our small developpers' head ! Given that the contest lasted 2 months, this was a great asset in the rapid implementation of several of our key concepts.

In the same way, Play allowed us to create webservices in less than 3 lines of code ! And all of this without being bothered by rebuild nuisances. Our application is communicating with a lot of distant webservices, Play was very helpfull to implements all the communication protocols. Easy to use and mainly really fast to approach.

The first issue that we found was the large number of web services to ask for. Besides the fact that we had to code all specificities of each of them, we had to make our best to not slow down our application, to make it hold the charge. We had the same concern for the large number of connected users. Indeed, social networks mashup means a lot of potentials users.

To deal with those concerns, we decided to use Akka with its actors system. In the same way than Play, this library is very easy to use (even if our biggest regret is that we didn't have enough time to dive into its deepest configuration). As Akka is thread safe, we decided to plug it to our pooling system to distant services. This allow to compartmentalize each client in its thread, without disturbing other users.

As the pooling was well managed by the server, it was useless to  ask to the client to do it. So we decided that the communication between client and server will be made via an http ServersSendEvent channel. Thus, when the server finds out new messages, it just has to send them to the client. The client needs also to communicate with the server, so we had to add the implementation of WebSocket to our application.

Later, we found out the Akka's events. So we can send an order from outside the stream to the actors. After a lot of refactoring, we had two operationals technologies : SSE and WebSocket.

Once those steps passed, we decided to store the user's configuration into a MogoDb database, thanks to the reactivemongo library. Once more, we choose MongoDb because we didn't know anything about this wonderful database, so it was for pleasure and joy to learn and discover.

It was the same for AngularJS, client side, it was also a real revelation, very powerful even with a lot of data.

How to install ?
-------- 

**Prerequisite :**

- Play 2.1-RC1
- MongoDb > 2.0
- A browser which suppport WebSocket and/or Sse

> Get the project
> Add your secret keys for all the social networks in a file called : "**my-social.conf**" with the following structure : https://gist.github.com/4172803

- Start mongoDB server (mongoDb conf can be modified in application.conf)
- cd Skimbo
- play run

That's all ;)

Why do you have to make us win ?
-----
Because we don't care to win ^^ We already won a lot of competences, and that's enough for us. But if you want us to keep running Skimbo on a server, we will need money to pay it, or may be a place on your server :D

Anyway, we will keep adding functionnality to Skimbo. The next feature will be to code the "Skimber!" action. This action will allow you to post a message into each social networks that you are connected on. And of course, a lot of new social networks are coming. And why not, paid options as for example have several count of the same social network, or see statistics to use them for marketing.

If you want to help us to realize this dream, don't hesitate, contact us ! Don't worry, our english is bad, but our passion for computer and technology is very big !

Froggies, our Team :
-----

Follow us on twitter :

- udr3y_
- RmManeschi_
- studiodev_
- vp3n_
- Laurent Dufour (This guy is not on twitter !!)
- Skimbo_

Licence
----

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


.. _Typesafe: http://blog.typesafe.com/typesafe-developer-contest
.. _Play2: https://github.com/playframework/play20/
.. _udr3y:  https://twitter.com/udr3y
.. _RmManeschi: https://twitter.com/RmManeschi
.. _studiodev: https://twitter.com/studiodev
.. _vp3n: https://twitter.com/vp3n
.. _skimbo: https://twitter.com/skimbo34
---
layout: en
title: Créer un nouveau provider de A à Z
type: article
needTranslation: en
---
#Créer un nouveau provider de A à Z

Un article, sous forme de tutorial pour vous présenter comment ajouter un réseau social sur Skimbo !

###Obtenir l'accès

Pour commencer il vous faut, bien évidemment, un provider qui a une api ouverte. Pour cet exemple, je prendrais reddit. Il suffit, en règle général, de tapper "nom du réseau api" sur google pour trouver tout ce dont on a besoin.

Dans notre cas tout ce trouve sur <a href="http://www.reddit.com/dev/api" target="_blank">l'api de reddit</a>. Ensuite, il faut créer un compte sur le réseau pour avoir un token d'accès, toujours pour reddit il faut aller sur son <a href="https://github.com/reddit/reddit/wiki/OAuth2" target="_blank">github</a>. Souvent le réseau social demande le nom de l'application, une courte description et une url de callback. Cette url permettra de rediriger l'utilisateur, lorsqu'il aura accepter de nous donner accès à ses données. Pour skimbo, il faut pointer vers l'url du serveur et /auth/nomDuProvider. Pour le serveur de production et reddit ça donne donc : http://skimbo-froggies.rhcloud.com/auth/reddit. Nous ne redirigeons pas vers http://skimbo.fr/auth/reddit car skimbo.fr redirige en clair vers rh-cloud, notre hébergeur actuel. Pour la version locale (en mode développement nous indiquerons donc http://127.0.0.1:9000/auth/reddit). Enfin nous pouvons récupérer notre clé de connexion et un clientId qu'il nous suffit de copier/coller dans le fichier SKIMBO_REP/conf/my-social.conf sous cette forme :

{% highlight scala %}
nomReseau {
  clientId=""
  secret=""
}
{% endhighlight %}

Ensuite, il nous faut trouver les url d'authorisation pour l'application et de récupération de token. Dans notre cas, elles se trouvent sur le <a href="https://github.com/reddit/reddit/wiki/OAuth2#other-important-information" target="_blank">github</a>. Une fois de plus, nous allons les copier/coller dans le fichier SKIMBO_REP/conf/default-social.conf (attention ce n'est pas le même fichier, le précédent est privé alors que celui-ci est publique) de la même manière que précédemment :

{% highlight scala %}
reddit {
  clientId=""
  secret=""
  authorize="https://ssl.reddit.com/api/v1/authorize"
  accessToken="https://ssl.reddit.com/api/v1/access_token"
  urlUserInfos="https://oauth.reddit.com/api/v1/me"
}
{% endhighlight %}

Nous pouvons remarquer, que nous réécrivons clientId et secret. En fait, le fichier my-social.conf viendra écrasé les propriétés de default-social.conf. De plus, nous en profitons pour rajouter l'url qui permettra à Skimbo de savoir quel utilisateur est connecté.

Maintenant que la configuration est prête nous pouvons commencer à développer :D

###Ajout du provider 

Reddit ouvre ces données grâce à OAuth2. Nous allons donc créer une classe Reddit qui étend la classe OAuth2. Afin de faciliter la lecture des classes nous préfèrerons la créer dans services.auth.providers.

Les paramètres à donner pour que les utilisateurs puissent se connecter à Skimbo par ce provider sont : name (le nom qui correspond dans la conf et dans l'url) et un namespace (un nom plus court pour les cookies). Dans ce cas là je met :

{% highlight scala %}
override val name = "reddit"
override val namespace = "rd"
{% endhighlight %}

De plus, il faut rajouter votre nouveau provider, à la liste de ceux affichés dans l'interface. Cela ce fait dans la classe ProviderDispatcher. Merci de ne pas oublié d'ajouter le logo dans public/img/brand/nom.png. 

Enfin, il suffit de dire à Skimbo comment il peut récupérer le token d'accès fournit par reddit :

{% highlight scala %}
override def processToken(response: play.api.libs.ws.Response) = { 
  Token((response.json \ "access_token").asOpt[String], 
      (response.json \ "expires_in").asOpt[Int],
      (response.json \ "refresh_token").asOpt[String])
}
{% endhighlight %}

Relancer Skimbo, votre nouveau provider est disponible pour la connexion.

Pour réellement finir ce nouveau provider, il nous faut encore parser le résultat de l'appel à urlUserInfos (oui oui celui entré dès le début). Pour cela il suffit d'overrider :

{% highlight scala %}
override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser]
{% endhighlight %}

Petit détail final, penser à ajouter la couleur primaire du provider dans SKIMBO_REP/app/assets/stelsheets/main.less.

Si d'aventure, vous êtes perdu ou que vous ne comprenez pas quelque chose, n'hésitez pas <a href="https://github.com/Froggies/Skimbo/issues" target="_blank">à nous le dire</a>. De même, pour avoir d'autres exemples, nous avons déjà implémenté pas mal de providers basés sur oauth ou oauth2 sur notre <a href="https://github.com/Froggies/Skimbo/tree/master/app/services/auth/providers" target="_blank">github</a>. Enfin, sachez que chaque provider fait ça petite sauce interne, donc ne soyez pas surpris que votre code ne marche pas du premier coup. Moi même pour rédiger cet article, j'ai passé 2h à débugger ce p...n de header perso imposé par reddit :D Pour preuve le commit final de l'ajout de reddit <a href="https://github.com/Froggies/Skimbo/commit/5ed536f685300a03e4f2fda25f31c28a46eaaeed" target="_blank">https://github.com/Froggies/Skimbo/commit/5ed536f685300a03e4f2fda25f31c28a46eaaeed</a>
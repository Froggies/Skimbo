
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import views.html._
/**/
object main extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Html,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String)(css: Html)(content: Html):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.43*/("""

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
  <head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width"/>
    <title>"""),_display_(Seq[Any](/*13.13*/title)),format.raw/*13.18*/("""</title>
    <link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*14.55*/routes/*14.61*/.Assets.at("img/favicon.png"))),format.raw/*14.90*/("""">
    """),_display_(Seq[Any](/*15.6*/css)),format.raw/*15.9*/("""
    <script type="text/javascript" src=""""),_display_(Seq[Any](/*16.42*/routes/*16.48*/.Application.jsRouter())),format.raw/*16.71*/(""""></script>
    <!-- Twitter card meta -->
    <meta name="twitter:card" content="summary">
    <meta name="twitter:url" content="http://skimbo.studio-dev.fr">
    <meta name="twitter:title" content="Skimbo">
    <meta name="twitter:description" content="Skimbo will change the way you follow streams from social networks by displaying them in a unique window, thus you will always be aware of new informations ! Just try it ! 😊">
    <meta name="twitter:image" content="https://trello-attachments.s3.amazonaws.com/505a376f783db194689c3972/509035d5c0af2b130c0020db/2a06737f55267206f4a40dd62c2ac9b9/128x128_transparent.png">
    <meta name="twitter:creator:id" content="854525299">
  </head>
  <body>
    """),_display_(Seq[Any](/*26.6*/content)),format.raw/*26.13*/("""
      
  	<script type="text/javascript">
  	  var _gaq = _gaq || [];
  	  _gaq.push(['_setAccount', 'UA-36716356-1']);
  	  _gaq.push(['_trackPageview']);
  	  (function() """),format.raw/*32.18*/("""{"""),format.raw/*32.19*/("""
  	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
  	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
  	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  	  """),format.raw/*36.6*/("""}"""),format.raw/*36.7*/(""")();
  	</script>
	    
  </body>
</html>
"""))}
    }
    
    def render(title:String,css:Html,content:Html): play.api.templates.Html = apply(title)(css)(content)
    
    def f:((String) => (Html) => (Html) => play.api.templates.Html) = (title) => (css) => (content) => apply(title)(css)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Apr 09 20:07:10 CEST 2013
                    SOURCE: /home/manland/projets/skimbo/Skimbo/app/views/main.scala.html
                    HASH: 781e3aa3092d56a9ddd3b609338b7076ba9dc410
                    MATRIX: 514->1|632->42|1176->550|1203->555|1302->618|1317->624|1368->653|1411->661|1435->664|1513->706|1528->712|1573->735|2314->1441|2343->1448|2545->1622|2574->1623|2917->1939|2945->1940
                    LINES: 19->1|22->1|34->13|34->13|35->14|35->14|35->14|36->15|36->15|37->16|37->16|37->16|47->26|47->26|53->32|53->32|57->36|57->36
                    -- GENERATED --
                */
            
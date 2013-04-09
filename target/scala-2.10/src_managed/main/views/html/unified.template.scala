
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
object unified extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {

Seq[Any](_display_(Seq[Any](/*1.2*/main("Skimbo")/*1.16*/ {_display_(Seq[Any](format.raw/*1.18*/("""
  <link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*2.48*/routes/*2.54*/.Assets.at("stylesheets/logged.min.css"))),format.raw/*2.94*/(""""/>
""")))}/*3.2*/ {_display_(Seq[Any](format.raw/*3.4*/("""

	<div id="publicApp" ng-controller="MainController">

    <header class="banner" ng-include="'"""),_display_(Seq[Any](/*7.42*/routes/*7.48*/.Assets.at("app/views/header.html"))),format.raw/*7.83*/("""'"></header>

    <!-- Add your site or application content here -->
    <div class="container" ng-view>
      <noscript>
        <p>Your navigator must run javascript files !</p>
        <p>Vous devez autoriser l'exécution de javascript !</p>
      </noscript>
    </div>

  <script src=""""),_display_(Seq[Any](/*17.17*/routes/*17.23*/.Assets.at("app/scripts/vendor/angular.min.js"))),format.raw/*17.70*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*18.17*/routes/*18.23*/.Assets.at("app/scripts/vendor/angular-sanitize.min.js"))),format.raw/*18.79*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*19.17*/routes/*19.23*/.Assets.at("app/scripts/vendor/moment.min.js"))),format.raw/*19.69*/(""""></script>
	<script src=""""),_display_(Seq[Any](/*20.16*/routes/*20.22*/.Assets.at("app/scripts/vendor/whoareyou.js"))),format.raw/*20.67*/(""""></script>
		
  """),_display_(Seq[Any](/*22.4*/helper/*22.10*/.requireJs(core = routes.Assets.at("javascripts/require.js").url, module = routes.Assets.at("javascripts/main").url))),format.raw/*22.126*/("""

  <!-- UserVoice -->
  <script type="text/javascript">
    var uvOptions = """),format.raw/*26.21*/("""{"""),format.raw/*26.22*/("""}"""),format.raw/*26.23*/(""";
    (function() """),format.raw/*27.17*/("""{"""),format.raw/*27.18*/("""
      var uv = document.createElement('script'); uv.type = 'text/javascript'; uv.async = true;
      uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/SKUqq2hdqI52HMB8QRZjw.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(uv, s);
    """),format.raw/*31.5*/("""}"""),format.raw/*31.6*/(""")();
  </script>
  <!-- end UserVoice -->
  </div>
""")))})))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Apr 09 22:25:41 CEST 2013
                    SOURCE: /home/manland/projets/skimbo/Skimbo/app/views/unified.scala.html
                    HASH: 12d00d807b611132b3c8e60a613936edb0b80023
                    MATRIX: 580->1|602->15|641->17|724->65|738->71|799->111|821->116|859->118|991->215|1005->221|1061->256|1387->546|1402->552|1471->599|1535->627|1550->633|1628->689|1692->717|1707->723|1775->769|1838->796|1853->802|1920->847|1973->865|1988->871|2127->987|2232->1064|2261->1065|2290->1066|2336->1084|2365->1085|2716->1409|2744->1410
                    LINES: 22->1|22->1|22->1|23->2|23->2|23->2|24->3|24->3|28->7|28->7|28->7|38->17|38->17|38->17|39->18|39->18|39->18|40->19|40->19|40->19|41->20|41->20|41->20|43->22|43->22|43->22|47->26|47->26|47->26|48->27|48->27|52->31|52->31
                    -- GENERATED --
                */
            

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

  <script type="text/javascript" src=""""),_display_(Seq[Any](/*17.40*/routes/*17.46*/.Application.jsRouter())),format.raw/*17.69*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*18.17*/routes/*18.23*/.Assets.at("app/scripts/vendor/angular.min.js"))),format.raw/*18.70*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*19.17*/routes/*19.23*/.Assets.at("app/scripts/vendor/angular-sanitize.min.js"))),format.raw/*19.79*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*20.17*/routes/*20.23*/.Assets.at("app/scripts/vendor/moment.min.js"))),format.raw/*20.69*/(""""></script>
  <script src=""""),_display_(Seq[Any](/*21.17*/routes/*21.23*/.Assets.at("app/scripts/vendor/whoareyou.js"))),format.raw/*21.68*/(""""></script>
		
  """),_display_(Seq[Any](/*23.4*/helper/*23.10*/.requireJs(core = routes.Assets.at("javascripts/require.js").url, module = routes.Assets.at("javascripts/main").url))),format.raw/*23.126*/("""

  <!-- UserVoice -->
  <script type="text/javascript">
    var uvOptions = """),format.raw/*27.21*/("""{"""),format.raw/*27.22*/("""}"""),format.raw/*27.23*/(""";
    (function() """),format.raw/*28.17*/("""{"""),format.raw/*28.18*/("""
      var uv = document.createElement('script'); uv.type = 'text/javascript'; uv.async = true;
      uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/SKUqq2hdqI52HMB8QRZjw.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(uv, s);
    """),format.raw/*32.5*/("""}"""),format.raw/*32.6*/(""")();
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
                    DATE: Mon Apr 15 21:19:03 CEST 2013
                    SOURCE: /home/manland/projets/skimbo/Skimbo/app/views/unified.scala.html
                    HASH: 7d267672a2114015b294d5ac0aecbce7cd4a8546
                    MATRIX: 580->1|602->15|641->17|724->65|738->71|799->111|821->116|859->118|991->215|1005->221|1061->256|1410->569|1425->575|1470->598|1534->626|1549->632|1618->679|1682->707|1697->713|1775->769|1839->797|1854->803|1922->849|1986->877|2001->883|2068->928|2121->946|2136->952|2275->1068|2380->1145|2409->1146|2438->1147|2484->1165|2513->1166|2864->1490|2892->1491
                    LINES: 22->1|22->1|22->1|23->2|23->2|23->2|24->3|24->3|28->7|28->7|28->7|38->17|38->17|38->17|39->18|39->18|39->18|40->19|40->19|40->19|41->20|41->20|41->20|42->21|42->21|42->21|44->23|44->23|44->23|48->27|48->27|48->27|49->28|49->28|53->32|53->32
                    -- GENERATED --
                */
            
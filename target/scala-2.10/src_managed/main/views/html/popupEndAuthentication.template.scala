
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
object popupEndAuthentication extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.1*/("""<html>
<head>
	<title>Skimbo</title>
	<script type="text/javascript">
		window.opener.callMeToRefresh();
		window.close();
	</script>
</head>
<body>
</body>
</html>"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Apr 09 20:07:10 CEST 2013
                    SOURCE: /home/manland/projets/skimbo/Skimbo/app/views/popupEndAuthentication.scala.html
                    HASH: 6c28c4f4e95ff1a899aa67cfe7b5ff9247f550b7
                    MATRIX: 586->0
                    LINES: 22->1
                    -- GENERATED --
                */
            
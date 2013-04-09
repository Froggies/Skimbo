
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
object index extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Seq[Service],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(providers: Seq[Service]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.27*/("""

"""),_display_(Seq[Any](/*3.2*/main("Skimbo")/*3.16*/ {_display_(Seq[Any](format.raw/*3.18*/("""
	<link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*4.47*/routes/*4.53*/.Assets.at("stylesheets/login.min.css"))),format.raw/*4.92*/("""">
""")))}/*5.2*/ {_display_(Seq[Any](format.raw/*5.4*/("""
	<div id="container">
		<header>
			<div class="logo">
				<img src=""""),_display_(Seq[Any](/*9.16*/routes/*9.22*/.Assets.at("img/skimbo_dessous_256.png"))),format.raw/*9.62*/("""" width="256" height="256"/>
			</div>
		</header>
	    <section>
        <div id="connect-with">
          <h1>Skimbo, a revolutionary way to follow all your social networks in real time.</h1>
          <h2>Start now by connecting your favorite network.</h2>
        </div>
        <ul id="social-networks">
        	"""),_display_(Seq[Any](/*18.11*/for(provider <- providers) yield /*18.37*/ {_display_(Seq[Any](format.raw/*18.39*/("""
						<li>
							<a href="/auth/"""),_display_(Seq[Any](/*20.24*/provider/*20.32*/.name)),format.raw/*20.37*/("""" title="Connect with """),_display_(Seq[Any](/*20.60*/provider/*20.68*/.name)),format.raw/*20.73*/("""">
								<img src=""""),_display_(Seq[Any](/*21.20*/routes/*21.26*/.Assets.at("img/brand/"+provider.name+".png"))),format.raw/*21.71*/("""" alt=""""),_display_(Seq[Any](/*21.79*/provider/*21.87*/.name)),format.raw/*21.92*/("""" width="50" height="50">
							</a>
						</li>
					""")))})),format.raw/*24.7*/("""
				</ul>
		</section>

		<footer class="footer-graddient">
				<p>⓵ <br/>
					First, connect to Skimbo with your <strong>favorite network</strong>. Simply choose one, you can add more again later.</p>
				<div class="separateur">
					<div class="divSeparateurGauche-blanc"></div>
					<div class="divSeparateurDroit-blanc"></div>
				</div>
				<p>⓶ <br />
					Create your first column. It's an easy way to group your feeds by <strong>interest</strong>. 
				</p>
				<div class="separateur">
					<div class="divSeparateurGauche-blanc"></div>
					<div class="divSeparateurDroit-blanc"></div>
				</div>
				<p>⓷ <br />
					You can add <strong>as many streams</strong> as you want in these columns. No limit !
				</p>
		</footer>
	</div>

""")))})))}
    }
    
    def render(providers:Seq[Service]): play.api.templates.Html = apply(providers)
    
    def f:((Seq[Service]) => play.api.templates.Html) = (providers) => apply(providers)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Apr 09 22:25:41 CEST 2013
                    SOURCE: /home/manland/projets/skimbo/Skimbo/app/views/index.scala.html
                    HASH: d478ec2027c918aa79e7d1dd707df86f3108bb34
                    MATRIX: 511->1|613->26|650->29|672->43|711->45|793->92|807->98|867->137|888->141|926->143|1032->214|1046->220|1107->260|1462->579|1504->605|1544->607|1615->642|1632->650|1659->655|1718->678|1735->686|1762->691|1820->713|1835->719|1902->764|1946->772|1963->780|1990->785|2077->841
                    LINES: 19->1|22->1|24->3|24->3|24->3|25->4|25->4|25->4|26->5|26->5|30->9|30->9|30->9|39->18|39->18|39->18|41->20|41->20|41->20|41->20|41->20|41->20|42->21|42->21|42->21|42->21|42->21|42->21|45->24
                    -- GENERATED --
                */
            
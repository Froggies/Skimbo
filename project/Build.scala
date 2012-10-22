import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "skimbo"
    val appVersion      = "0.1-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

	val appDependencies = Seq(
    	"se.radley" %% "play-plugins-salat" % "1.1"
	)
	val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
		routesImport += "se.radley.plugin.salat.Binders._",
		templatesImport += "org.bson.types.ObjectId"
	)

}

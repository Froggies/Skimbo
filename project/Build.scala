import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "skimbo"
    val appVersion      = "0.1-SNAPSHOT"

	val appDependencies = Seq(
	    "reactivemongo" %% "reactivemongo" % "0.1-SNAPSHOT",// changing(),
      "play.modules.reactivemongo" %% "play2-reactivemongo" % "0.1-SNAPSHOT"// changing()
	)
	
	val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
	    resolvers += "sgodbillon" at "https://bitbucket.org/sgodbillon/repository/raw/master/snapshots/"
	)

}

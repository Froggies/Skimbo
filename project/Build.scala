import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "skimbo"
    val appVersion      = "0.1-SNAPSHOT"

	val appDependencies = Seq(
	    "org.reactivemongo" %% "play2-reactivemongo" % "0.9"
	)
	
	val main = play.Project(appName, appVersion, appDependencies).settings(
	    scalacOptions ++= Seq("-language:postfixOps", "-deprecation","-unchecked","-feature"),
	    requireJs += "main.js"
	)

}

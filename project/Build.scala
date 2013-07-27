import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "skimbo"
  val appVersion      = "0.1-SNAPSHOT"
      
	val appDependencies = Seq(
	    "org.reactivemongo" %% "play2-reactivemongo" % "0.9",
	    "org.apache.commons" % "commons-email" % "1.3.1"
	)
	
	val buildSettings = Defaults.defaultSettings ++ Seq (
	    scalacOptions ++= Seq("-language:postfixOps", "-deprecation","-unchecked","-feature"),
      requireJs += "main.js",
      requireJs += "mockedMain.js"
  )
	
	val main = play.Project(appName, appVersion, appDependencies, settings = buildSettings)

}

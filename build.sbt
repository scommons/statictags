
import sbtcrossproject.CrossProject

val commonSettings = Seq(
  organization := "org.scommons.shogowada",
  name := "statictags",

  crossScalaVersions := Seq("2.12.1", "2.13.1"),
  scalaVersion := "2.12.1",
  scalacOptions ++= Seq(
    "-deprecation", "-unchecked", "-feature", "-Xcheckinit", "-target:jvm-1.8", "-Xfatal-warnings"
  ),

  sonatypeProfileName := "org.scommons",
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    if (isSnapshot.value)
      Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
    else
      Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  },
  pomExtra := {
    <url>https://github.com/scommons/statictags</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:scommons/statictags.git</url>
      <connection>scm:git@github.com:scommons/statictags.git</connection>
    </scm>
    <developers>
      <developer>
        <id>shogowada</id>
        <name>Shogo Wada</name>
        <url>https://github.com/shogowada</url>
      </developer>
      <developer>
        <id>viktorp</id>
        <name>Viktor Podzigun</name>
        <url>https://github.com/viktor-podzigun</url>
      </developer>
    </developers>
  },
  pomIncludeRepository := {
    _ => false
  }
)

lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
      crossScalaVersions := Nil, //must be set to Nil on the aggregating project
      skip in publish := true,
      publish := ((): Unit),
      publishLocal := ((): Unit),
      publishM2 := ((): Unit)
    )
    .aggregate(jvm, js)

lazy val statictags = CrossProject("statictags", file("statictags"))(JSPlatform, JVMPlatform)
    .crossType(sbtcrossproject.CrossType.Pure)
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % "3.2.2" % "test"
      )
    )

lazy val jvm = statictags.jvm
lazy val js = statictags.js

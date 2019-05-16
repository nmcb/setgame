 import Dependencies._

lazy val commonSettings = Seq(
  name         := "setgame",
  version      := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.8"
)

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ypartial-unification",
  "-Ywarn-unused-import"
)

lazy val `setgame` = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies += scalaTest,
    addCompilerPlugin(kindProjector)
  )

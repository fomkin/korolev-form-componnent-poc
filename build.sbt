organization := "com.example"

name := "FormComponnentPoc"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.4"

val korolevVersion = "0.6.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.+",
  "com.github.fomkin" %% "korolev-server-blaze" % korolevVersion,
  "com.propensive" %% "magnolia" % "0.6.1"
)

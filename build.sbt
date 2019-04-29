lazy val catsVersion = "1.0.1"
lazy val monocleVersion = "1.5.0-cats"
lazy val monixVersion = "3.0.0-RC1"
lazy val circeVersion = "0.9.1"
lazy val akkaHttpVersion = "10.0.10"
lazy val akkaVersion = "2.5.3"
lazy val macwireVersion = "2.3.0"
lazy val scalikeJdbcVersion = "3.0.1"


lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "shiftkun",
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.12.3",
      scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-Xlint:_,-missing-interpolator",
        "-Ypartial-unification",
        "-language:higherKinds",
        "-language:postfixOps"
      )
    )),

    name := "shiftkun",
    fork in Test := true,
    fork in IntegrationTest := true,
    Defaults.itSettings,
    resolvers += "Bintary JCenter" at "http://jcenter.bintray.com",
    libraryDependencies ++= Seq (
      // cats
      "org.typelevel" %% "cats-core" % catsVersion,

      // circe
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // monocle
      "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
      "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,

      // monix
      "io.monix" %% "monix" % monixVersion,

      // akka-http
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.17.0",
      "ch.megard" %% "akka-http-cors" % "0.2.1",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test,

      // macwire
      "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided",
      "com.softwaremill.macwire" %% "macrosakka" % macwireVersion % "provided",
      "com.softwaremill.macwire" %% "util" % macwireVersion,
      "com.softwaremill.macwire" %% "proxy" % macwireVersion,

      // scalikejdbc
      "mysql" % "mysql-connector-java" % "5.1.38",
      "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVersion,

      // logging
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.3",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.1",


      // swagger
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.14.1",

      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",

      "io.spray" %%  "spray-json" % "1.3.5",

      // json web token
      "com.pauldijou" %% "jwt-core" % "0.18.0",
    ),

    mainClass in run := Some("shiftkun.ApiServerMain"),

  )

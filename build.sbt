// todomvc-scalajs-react-slinky

// ===========================================================================
lazy val root = (project in file("."))
  .settings(
    name         := "todomvc-scalajs-react-slinky",
    version      := "0.0.1",
    scalaVersion := "2.13.4",

    scalaJSUseMainModuleInitializer := true,                 // to use main()
    skip in packageJSDependencies   := false,                // TODO: not working? js-deps file is empty
    scalacOptions                   += "-Ymacro-annotations" // to use slinky's @react annotation
  )

// ===========================================================================
// also see project/plugins.sbt

// ---------------------------------------------------------------------------
enablePlugins(ScalaJSPlugin)        // for: %%%
enablePlugins(JSDependenciesPlugin) // for: jsDependencies += ...

// ===========================================================================
lazy val scalaJsDomVersion = "1.1.0"
lazy val reactVersion      = "16.12.0"
lazy val slinkyVersion     = "0.6.7"  // react framework for scala.js
lazy val enumeratumVersion = "1.6.1"  // note: better enums are coming to scala 3.0

// ---------------------------------------------------------------------------
libraryDependencies ++=
  Seq(
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
    "me.shadaj"    %%% "slinky-core" % slinkyVersion,
    "me.shadaj"    %%% "slinky-web"  % slinkyVersion,
    "com.beachape" %%% "enumeratum"  % enumeratumVersion)

// ---------------------------------------------------------------------------
jsDependencies ++= Seq(
  "org.webjars.npm" % "react"     % reactVersion % Test / "umd/react.development.js"                    minified "umd/react.production.min.js"                                                             commonJSName "React",
  "org.webjars.npm" % "react-dom" % reactVersion % Test / "umd/react-dom.development.js"                minified "umd/react-dom.production.min.js"                dependsOn "umd/react.development.js"     commonJSName "ReactDOM",
  "org.webjars.npm" % "react-dom" % reactVersion % Test / "umd/react-dom-test-utils.development.js"     minified "umd/react-dom-test-utils.production.min.js"     dependsOn "umd/react-dom.development.js" commonJSName "ReactTestUtils",
  "org.webjars.npm" % "react-dom" % reactVersion % Test / "umd/react-dom-server.browser.development.js" minified "umd/react-dom-server.browser.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactDOMServer")

// ===========================================================================

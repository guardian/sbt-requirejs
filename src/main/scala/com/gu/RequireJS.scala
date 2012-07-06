package com.gu

import java.io.File
import sbt._
import org.mozilla.javascript.tools.jsc.Main

object RequireJS extends Plugin {

  val requireJsConfig = SettingKey[File]("require-js-config", "The configuration file for requireJS")
  val requireJsAppDir = SettingKey[File]("require-js-app-dir", "The location of the javascript you want to optimize")
  val requireJsDir = SettingKey[File]("require-js-dir", "The location you want the javascript optimized to")
  val requireJsBaseUrl = SettingKey[String]("require-js-base-url", "The base url of requireJs modules")


  val requireJsSettings = Seq[Project.Setting[_]](
    requireJsConfig <<= createDefaultConfig()
  )

  private def createDefaultConfig() = (requireJsAppDir, requireJsDir, requireJsBaseUrl) { (appDir, dir, baseUrl) =>

    val configFile = IO.createTemporaryDirectory / "build.js"

    //for definition of config file see...
    // http://requirejs.org/docs/optimization.html#wholeproject

    //    appDir    the raw source
    //    dir       where I want files copied to - resource generated
    //    baseUrl (relative to appdir) where your modules are located


    val config =
      """({
            appDir: "%s",
            baseUrl: "%s",
            dir: "%s",
            out: "main-built.js",
            modules: [
                {
                    name: "main"
                }
            ]
        })"""  format (appDir, baseUrl, dir)


    IO.write(configFile, config)
    configFile
  }

  def requireJsCompiler = (requireJsAppDir, requireJsDir, requireJsBaseUrl, requireJsConfig) map { (appDir, dir, baseUrl, config) =>

    println(appDir + "  " + dir + "  " + baseUrl)

    //Main.main(Array("/home/gklopper/tools/r.js", "-o", config.getAbsolutePath))

    Main.main(Array("/home/gklopper/tools/r.js", config.getAbsolutePath))
    //Main.main(Array("/home/gklopper/tools/r.js", "-o baseUrl=" + baseUrl + " appDir=" + appDir +" dir=" + dir))

    Seq.empty[File]
  }

}

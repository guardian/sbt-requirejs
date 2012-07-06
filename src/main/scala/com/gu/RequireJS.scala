package com.gu

import java.io.File
import sbt._
import org.mozilla.javascript.tools.shell.Main
import io.Source

object RequireJS extends Plugin {

  val requireJsAppDir = SettingKey[File]("require-js-app-dir", "The location of the javascript you want to optimize")
  val requireJsDir = SettingKey[File]("require-js-dir", "The location you want the javascript optimized to")
  val requireJsBaseUrl = SettingKey[String]("require-js-base-url", "The base url of requireJs modules")

  val requireJsRJSFile = SettingKey[File]("require-js-rjs-file", "The r.js file that is used to compile requirejs files")

  val requireJsModules = SettingKey[Seq[String]]("require-js-modules", "The requireJs entry modules (usually main - for main.js)")


  val requireJsSettings = Seq[Project.Setting[_]](
    requireJsRJSFile := writeRJSFile
  )

  private def writeRJSFile = {
    val rjsFile = IO.createTemporaryDirectory / "r.js"
    val rjs = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("r.js")).mkString
    IO.write(rjsFile, rjs)
    rjsFile
  }

  def requireJsCompiler = (requireJsRJSFile, requireJsAppDir, requireJsDir, requireJsBaseUrl, requireJsModules) map { (rjsFile, appDir, dir, baseUrl, modules) =>

    val moduleArgs = modules.map(m => """{name:"%s"}""".format(m)).mkString(",")

    val config =
      """
        ({
         baseUrl: "%s",
         appDir: "%s",
         dir: "%s",
         modules:[
          %s
         ]
        })
      """ format (baseUrl, appDir, dir, moduleArgs)


    val configFile = IO.createTemporaryDirectory / "config.js"
    IO.write(configFile, config)

    //Main.main(Array(rjsFile.getAbsolutePath, "-o", "baseUrl=" + baseUrl, "appDir=" + appDir, "dir=" + dir, "modules=name:'main',name:'after'"))
    Main.main(Array(rjsFile.getAbsolutePath, "-o", configFile.getAbsolutePath))
    Seq.empty[File]
  }
}

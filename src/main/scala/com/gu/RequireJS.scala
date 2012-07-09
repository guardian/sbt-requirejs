package com.gu

import java.io.File
import sbt._
import sbt.Keys._

object RequireJS extends Plugin {

  val requireJsAppDir = SettingKey[File]("require-js-app-dir", "The location of the javascript you want to optimize")
  val requireJsDir = SettingKey[File]("require-js-dir", "The location you want the javascript optimized to")
  val requireJsBaseUrl = SettingKey[String]("require-js-base-url", "The base url of requireJs modules")

  //val requireJsRJSFile = SettingKey[File]("require-js-rjs-file", "The r.js file that is used to compile requirejs files")

  val requireJsModules = SettingKey[Seq[String]]("require-js-modules", "The requireJs entry modules (usually main - for main.js)")


  val requireJsSettings = Seq[Project.Setting[_]]()

  def requireJsCompiler = (requireJsAppDir, requireJsDir, requireJsBaseUrl, requireJsModules, streams) map {
    (appDir, dir, baseUrl, modules, s) =>
      implicit val log = s.log

      val config = RequireJsConfig(baseUrl, appDir.getAbsolutePath, dir.getAbsolutePath, modules.map(Module(_)))
      RequireJsOptimizer.optimize(config)
  }
}




package com.gu

import java.io.File
import sbt._
import sbt.Keys._

object RequireJS extends Plugin {

  val requireJsAppDir = SettingKey[File]("require-js-app-dir", "The location of the javascript you want to optimize")
  val requireJsDir = SettingKey[File]("require-js-dir", "The location you want the javascript optimized to")
  val requireJsBaseUrl = SettingKey[String]("require-js-base-url", "The base url of requireJs modules")
  val requireJsOptimize = SettingKey[Boolean]("require-js-optimize", "Let requireJs know whether to optimize files or not")

  //val requireJsRJSFile = SettingKey[File]("require-js-rjs-file", "The r.js file that is used to compile requirejs files")

  val requireJsModules = SettingKey[Seq[String]]("require-js-modules", "The requireJs entry modules (usually main - for main.js)")
  val requireJsPaths = SettingKey[Map[String, String]]("require-js-paths", "The requireJS paths mapping (Eg, 'bonzo' -> 'vendor/bonzo-v1.0.1'")

  def requireJsCompiler = (requireJsOptimize, requireJsAppDir, requireJsDir, requireJsBaseUrl, requireJsPaths, requireJsModules, streams) map {
    (optimize, appDir, dir, baseUrl, paths, modules, s) =>
      implicit val log = s.log

      val optimizeOpt = if (optimize) None else Some("none")

      val config = RequireJsConfig(baseUrl, appDir.getAbsolutePath,
        dir.getAbsolutePath, paths, modules.map(Module(_)), optimizeOpt)

      RequireJsOptimizer.optimize(config)
  }
}




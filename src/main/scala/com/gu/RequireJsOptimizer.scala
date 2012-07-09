package com.gu

import sbt._
import io.Source
import org.mozilla.javascript.tools.shell.{Global, ShellContextFactory, Main}
import com.codahale.jerkson.Json._

case class Module(name: String)

case class RequireJsConfig(baseUrl: String, appDir: String, dir: String, modules: Seq[Module],
                           // to turn off optimization use optimize=Some("none")    - yeah, I know
                           optimize: Option[String])


object RequireJsOptimizer {

  private val rjsFile = {
    val dir = IO.createTemporaryDirectory
    dir.deleteOnExit()
    val rjsFile = dir / "r.js"
    val rjs = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("r.js")).mkString
    IO.write(rjsFile, rjs)
    rjsFile
  }

  def optimize(config: RequireJsConfig)(implicit log: Logger) = {
    time{
      val configFile = IO.createTemporaryDirectory / "config.js"
      val configString = generate(config)

      log.info("Running requirejs optimization with config: " + configString)

      IO.write(configFile, configString)

      //if we do not do this then the script is not executed again
      //I found this out by trial and error
      Main.shellContextFactory = new ShellContextFactory()
      Main.global = new Global()

      val result = Main.exec(Array(rjsFile.getAbsolutePath, "-o", configFile.getAbsolutePath))

      //TODO find a case where this happens and figure out what to do about it
      if (result != 0) {
        System.exit(result)
      }

      val allJsFiles = (file(config.appDir) ** "*.js").get
      val destination = file(config.dir)
      val optimizedFiles = (allJsFiles x rebase(file(config.appDir), destination)) map (_._2)
      optimizedFiles
    }
  }

  private def time[A](block: => A)(implicit log: Logger): A = {
    val start = System.currentTimeMillis
    val result = block
    log.info("RequireJs optimization took " + (System.currentTimeMillis() - start) + " ms")
    result
  }
}
package com.gu

import sbt._
import io.Source
import org.mozilla.javascript.tools.shell.{Global, ShellContextFactory, Main}
import com.codahale.jerkson.Json._
import java.util

case class Module(name: String)

case class RequireJsConfig(baseUrl: String,
                           appDir: String,
                           dir: String,
                           paths: Map[String, String],
                           modules: Seq[Module],
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
      clearFileList()

      val result = Main.exec(Array(rjsFile.getAbsolutePath, "-o", configFile.getAbsolutePath))

      //have not actually been able to get this to return anything other than 0
      //even when I know there are errors.
      if (result != 0) {
        System.exit(result)
      }

      val allJsFiles = (file(config.appDir) ** "*.js").get
      val destination = file(config.dir)
      val optimizedFiles = (allJsFiles x rebase(file(config.appDir), destination)) map (_._2)
      optimizedFiles
    }
  }


  private def clearFileList() {
    //due to the way we are calling this, it adds a null to the list each time.
    //this is the only way I found to clear the list
    val fileList = classOf[Main].getDeclaredField("fileList")
    fileList.setAccessible(true)
    fileList.set(null, new util.ArrayList[String]())
  }

  private def time[A](block: => A)(implicit log: Logger): A = {
    val start = System.currentTimeMillis
    val result = block
    log.info("RequireJs optimization took " + (System.currentTimeMillis() - start) + " ms")
    result
  }
}

package com.gu

import java.io.File
import sbt._


object `package` {

  implicit def file2copy(f: File) = new {

    def rebase(fromDirectory: File, toDirectory: File) =
      f.relativeTo(fromDirectory).map {
        _.getPath
      }.map {
        toDirectory / _
      }

  }
}
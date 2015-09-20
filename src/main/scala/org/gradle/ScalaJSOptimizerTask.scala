package org.gradle

import org.scalajs.core.tools.javascript.OutputMode
import org.scalajs.core.tools.sem.Semantics
import org.scalajs.core.tools.optimizer._

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ScalaJSOptimizerTask extends DefaultTask {

  @TaskAction
  def OptJS(): Unit = {
    println("Hello")
    def optimizer = new ScalaJSOptimizer(
      Semantics.Defaults,
      OutputMode.ECMAScript51Isolated,
      ParIncOptimizer.factory
    )
    println("Done.")

    //  val s = streams.value
    //    val output = (artifactPath in fullOptJS).value
    //    val taskCache =
    //            WritableFileVirtualTextFile(s.cacheDirectory / "fullopt-js")
    //
    //    IO.createDirectory(output.getParentFile)
    //
    //    val relSourceMapBase =
    //    if ((relativeSourceMaps in fullOptJS).value)
    //        Some(output.getParentFile.toURI())
    //    else None
    //
    //    val opts = (scalaJSOptimizerOptions in fullOptJS).value
    //
    //
    //    val outCP = new ScalaJSClosureOptimizer().optimizeCP(
    //            new scalaJSOptimizer(),
    //            (scalaJSPreLinkClasspath in fullOptJS).value,
    //            Config(AtomicWritableFileVirtualJSFile(output))
    //                    .withCache(Some(taskCache))
    //                    .withWantSourceMap((emitSourceMaps in fullOptJS).value)
    //                    .withRelativizeSourceMapBase(relSourceMapBase)
    //                    .withBypassLinkingErrors(opts.bypassLinkingErrors)
    //                    .withCheckIR(opts.checkScalaJSIR)
    //                    .withDisableOptimizer(opts.disableOptimizer)
    //                    .withBatchMode(opts.batchMode)
    //                    .withCustomOutputWrapper(scalaJSOutputWrapper.value)
    //                    .withPrettyPrint(opts.prettyPrintFullOptJS),
    //            s.log)
    //
    //    Attributed.blank(output).put(scalaJSCompleteClasspath, outCP)
  }

}
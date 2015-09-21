package org.gradle

import org.scalajs.core.tools.classpath.builder.PartialClasspathBuilder
import org.scalajs.core.tools.io._
import org.scalajs.core.tools.javascript.OutputMode
import org.scalajs.core.tools.optimizer.ScalaJSOptimizer.Config
import org.scalajs.core.tools.sem.Semantics
import org.scalajs.core.tools.optimizer._
import org.scalajs.core.tools.logging.ScalaConsoleLogger

import org.gradle.api.DefaultTask
import org.gradle.api.tasks._

import collection.JavaConversions._

import java.io._

class ScalaJSOptimizerTask extends DefaultTask {

  @OutputDirectory
  var outputDir = new File(getProject.getBuildDir, "fastopt")

  @TaskAction
  def OptJS(): Unit = {

    //TODO: At this point we should check for scalajs library on compile classpath?

    def optimizer = new ScalaJSOptimizer(
      Semantics.Defaults,
      OutputMode.ECMAScript51Isolated,
      ParIncOptimizer.factory
    )

    val cacheDir = new File(getProject.getBuildDir, "scalajs-cache")
    val taskCache = WritableFileVirtualTextFile(new File(cacheDir, "fastopt-js"))

    outputDir.mkdir()
    cacheDir.mkdir()

    val output = new File(outputDir, getProject.getName + "-fastopt.js")

    // Replaced above line with these
    /** Whether to only warn if the linker has errors */
    val bypassLinkingErrors: Boolean = false
    /** Whether to run the optimizer in batch (i.e. non-incremental) mode */
    val batchMode: Boolean = false
    /** Whether to run the Scala.js optimizer */
    val disableOptimizer: Boolean = false
    /** Perform expensive checks of the sanity of the Scala.js IR */
    val checkScalaJSIR: Boolean = true

    val sourceSets = getProject.property("sourceSets").asInstanceOf[SourceSetContainer]

    val cp = sourceSets.getByName("main").getRuntimeClasspath.getFiles
    val pcp = PartialClasspathBuilder.build(collection.immutable.Seq[File](cp.toSeq: _*))
    //    val ccp = pcp.resolve(jsDependencyFilter.value, jsManifestFilter.value)
    val ccp = pcp.resolve(identity, identity)

    val outCP = optimizer.optimizeCP(
      ccp,
      Config(AtomicWritableFileVirtualJSFile(output))
        .withCache(Some(taskCache))
        .withWantSourceMap(true)
        .withRelativizeSourceMapBase(None)
        //              .withRelativizeSourceMapBase(relSourceMapBase)
        .withBypassLinkingErrors(bypassLinkingErrors)
        .withCheckIR(checkScalaJSIR)
        .withDisableOptimizer(disableOptimizer)
        .withBatchMode(batchMode)
        .withCustomOutputWrapper(("", "")),
      new ScalaConsoleLogger()
    )

    // Old command from the sbt plugin; could make gradle aware of output files somehow?
    // Attributed.blank(output).put(scalaJSCompleteClasspath, outCP)
  }

}
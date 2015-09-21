package org.gradle

import _root_.groovy.lang.Closure
import org.codehaus.groovy.runtime.MethodClosure
import org.gradle.api.tasks.scala.ScalaCompile
import org.scalajs.core.tools.classpath.builder.PartialClasspathBuilder
import org.scalajs.core.tools.io._
import org.scalajs.core.tools.javascript.OutputMode
import org.scalajs.core.tools.optimizer.ScalaJSOptimizer.Config
import org.scalajs.core.tools.sem.Semantics
import org.scalajs.core.tools.optimizer._
import org.scalajs.core.tools.logging.ScalaConsoleLogger

import org.gradle.api.{Action, DefaultTask}
import org.gradle.api.tasks._

import collection.JavaConversions._

import java.io._

class ScalaJSOptimizerTask extends DefaultTask {

  val sourceSets = getProject.property("sourceSets").asInstanceOf[SourceSetContainer]

  val scalaPlugin = getProject.getPlugins.getPlugin("scala").asInstanceOf[org.gradle.api.plugins.scala.ScalaPlugin]
  val scalaCompileTask = getProject.getTasks.getByName(sourceSets.getByName("main").getCompileTaskName("scala")).asInstanceOf[ScalaCompile]

  getProject.getTasks.withType(classOf[ScalaCompile]).foreach(task => {
      task.doFirst(new UpdateScalaCompileOptions(this).closure)
    }
  )

  @OutputDirectory
  var outputDir = new File(getProject.getBuildDir, "fastopt")

  @TaskAction
  def OptJS(): Unit = {
    println("Hello")
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


    println(System.getProperty("java.home"))

//    val relSourceMapBase =
//    if ((relativeSourceMaps in fullOptJS).value)
//        Some(output.getParentFile.toURI())
//    else None

//    val opts = (scalaJSOptimizerOptions in fullOptJS).value

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

    //Attributed.blank(output).put(scalaJSCompleteClasspath, outCP)
    println("Done.")
  }

}

class UpdateScalaCompileOptions(sjsTask: ScalaJSOptimizerTask) {

  private object ConfigClosure {

    def configureScalaCompile(task: ScalaCompile): Unit = {
      val sjsCompiler = sjsTask.getProject.getBuildscript.getConfigurations.getByName("classpath").getFiles.filter(file =>
        file.getName.contains("scalajs-compiler")
      ).toList(0)
      task.getScalaCompileOptions.setAdditionalParameters(Array(s"-Xplugin:$sjsCompiler").toList)
    }

  }

  val closure = new MethodClosure(ConfigClosure, "configureScalaCompile")
}
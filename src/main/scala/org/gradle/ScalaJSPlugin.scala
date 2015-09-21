package org.gradle

/**
 * Created by davidhewitt on 21/09/2015.
 */

import org.codehaus.groovy.runtime.MethodClosure
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.scala.ScalaCompile
import org.gradle.api.{Project, Plugin}

import collection.JavaConversions._

class ScalaJSPlugin extends Plugin[Project] {

  private var project: Project = null

  override def apply(project: Project): Unit = {
    this.project = project
    project.getPlugins.apply(classOf[ScalaPlugin])

    addFastOptTask()
    addCompilerArguments()
  }

  private def addFastOptTask(): Unit = {
    val scalaJSTask = project.getTasks.create("fastOptJS", classOf[ScalaJSOptimizerTask])
    val sourceSets = project.property("sourceSets").asInstanceOf[SourceSetContainer]
    val scalaCompileTask = project.getTasks.getByName(sourceSets.getByName("main").getCompileTaskName("scala")).asInstanceOf[ScalaCompile]
    scalaJSTask.dependsOn(scalaCompileTask)
    project.getTasks.getByName(sourceSets.getByName("main").getClassesTaskName).dependsOn(scalaJSTask)
  }

  private def addCompilerArguments(): Unit = {
    project.getTasks.withType(classOf[ScalaCompile]).foreach(task => {
        task.doFirst(new AddScalaJSCompilerPluginOption(project).closure)
      }
    )
  }

}

class AddScalaJSCompilerPluginOption(project: Project) {

  private object ConfigClosure {

    def configureScalaCompile(task: ScalaCompile): Unit = {
      val sjsCompiler = project.getBuildscript.getConfigurations.getByName("classpath").getFiles.filter(file =>
        file.getName.contains("scalajs-compiler")
      ).toList(0)
      task.getScalaCompileOptions.setAdditionalParameters(Array(s"-Xplugin:$sjsCompiler").toList)
    }

  }

  val closure = new MethodClosure(ConfigClosure, "configureScalaCompile")
}

scalajs-gradle-plugin
=====================

This is a gradle plugin for the ScalaJS compiler.

At the moment no configuration options are exposed.

##Usage

Clone this project and build using gradle to generate the plugin .jar

To use the plugin in another project add the following to your build.gradle

    buildscript {
    
        repositories {
            mavenCentral()
        }
    
        dependencies {
            classpath 'org.scala-js:scalajs-compiler_2.11.7:0.6.5'
            classpath "org.scala-js:scalajs-tools_2.11:0.6.5"
            classpath 'org.scala-lang:scala-library:2.11.7'
            classpath files("path/to/this/repo/build/libs/scalajs-gradle-plugin-1.0-SNAPSHOT.jar")
        }
    }
    
    apply plugin: 'scalajs'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        compile 'org.scala-lang:scala-library:2.11.7'
        compile 'org.scala-js:scalajs-library_2.11:0.6.5'
        compile 'org.scala-js:scalajs-dom_sjs0.6_2.11:0.8.1'
    }

## Todo

1. Add smart error messages when scalajs is missing from classpath
2. Publish to Maven so that buildscript dependencies are added automatically
3. Add new features and configuration
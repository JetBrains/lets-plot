/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

pluginManagement {
    plugins {
        val kotlinVersion = extra["kotlin_version"] as String
        val ideaExtVersion = extra["idea_ext_version"] as String

//        id "org.jetbrains.kotlin.multiplatform" version "$kotlin_version"
//        id "org.jetbrains.kotlin.js" version "$kotlin_version"
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("js").version(kotlinVersion)

//        id "org.jetbrains.gradle.plugin.idea-ext" version "$idea_ext_version"
        id("org.jetbrains.gradle.plugin.idea-ext").version(ideaExtVersion)
//        id "com.github.johnrengelman.shadow" version "5.1.0"
        id("com.github.johnrengelman.shadow").version("5.1.0")
//        id "io.codearte.nexus-staging" version "0.30.0"
        id("io.codearte.nexus-staging").version("0.30.0")
    }
}

rootProject.name = "lets-plot"

include("commons")
include("datamodel")
include("canvas")
include("gis")
include("livemap")
include("plot-livemap")
include("plot-base")
include("plot-builder")
include("plot-stem")
include("platf-w3c")
include("platf-awt")
include("platf-batik")
include("platf-jfx-swing")
include("platf-native")
include("plot-image-export")
include("python-extension")
include("python-package-build")
include("js-package")
include("jvm-package:jvm-publish-common")
include("jvm-package:jvm-publish-jfx")
include("jvm-package:jvm-publish-batik")
include("jvm-package:jvm-publish-gis")

include("demo-and-test-shared")
include("vis-demo-common")
include("vis-demo-common-jfx")
include("vis-demo-common-batik")
include("demo-svg")
include("plot-demo-common")
include("plot-demo")
include("demo-livemap")
include("plot-export-demo")

project(":demo-and-test-shared").projectDir = File("./demo/demo-and-test-shared")
project(":vis-demo-common").projectDir = File("./demo/vis-demo-common")
project(":vis-demo-common-jfx").projectDir = File("./demo/vis-demo-common-jfx")
project(":vis-demo-common-batik").projectDir = File("./demo/vis-demo-common-batik")
project(":demo-svg").projectDir = File("./demo/svg")
project(":plot-demo-common").projectDir = File("./demo/plot-demo-common")
project(":plot-demo").projectDir = File("./demo/plot-demo")
project(":demo-livemap").projectDir = File("./demo/livemap")
project(":plot-export-demo").projectDir = File("./demo/plot-export-demo")

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

pluginManagement {
    plugins {
        val kotlinVersion = extra["kotlin_version"] as String

        kotlin("multiplatform").version(kotlinVersion)
        kotlin("js").version(kotlinVersion)

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
project(":demo-and-test-shared").projectDir = File("./demo/demo-and-test-shared")

include("demo-common-jvm-utils")
project(":demo-common-jvm-utils").projectDir = File("./demo/common-jvm-utils")

include("demo-common-svg")
project(":demo-common-svg").projectDir = File("./demo/common-svg")

include("demo-common-plot")
project(":demo-common-plot").projectDir = File("./demo/common-plot")

include("demo-svg-jvm")
project(":demo-svg-jvm").projectDir = File("./demo/svg-jvm")
include("demo-svg-browser")
project(":demo-svg-browser").projectDir = File("./demo/svg-browser")

include("demo-plot-jvm")
project(":demo-plot-jvm").projectDir = File("./demo/plot-jvm")
include("demo-plot-browser")
project(":demo-plot-browser").projectDir = File("./demo/plot-browser")

include("demo-export")
project(":demo-export").projectDir = File("./demo/export")


include("demo-common-livemap")
project(":demo-common-livemap").projectDir = File("./demo/common-livemap")

include("demo-livemap-jvm")
project(":demo-livemap-jvm").projectDir = File("./demo/livemap-jvm")

include("demo-livemap-browser")
project(":demo-livemap-browser").projectDir = File("./demo/livemap-browser")

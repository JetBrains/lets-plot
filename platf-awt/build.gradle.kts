/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

val jsvgVersion = project.extra["weisj.jsvg.version"] as String
val assertjVersion = extra["assertj.version"] as String

dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":plot-livemap"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-base"))
    compileOnly(project(":plot-builder"))
    compileOnly(project(":plot-stem"))
    compileOnly(project(":plot-raster"))

    compileOnly("com.github.weisj:jsvg:${jsvgVersion}")

    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":demo-common-svg"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":canvas"))
    testImplementation(project(":plot-raster"))
    testImplementation(project(":plot-raster"))
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
}
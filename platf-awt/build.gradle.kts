/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":plot-livemap"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-base"))
    compileOnly(project(":plot-builder"))
    compileOnly(project(":plot-stem"))
    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":demo-common-svg"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":canvas"))
    testImplementation(project(":plot-raster"))
}
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

val batikVersion = project.extra["batik_version"] as String

dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":plot-stem"))
    compileOnly(project(":platf-awt"))
    compileOnly("org.apache.xmlgraphics:batik-codec:$batikVersion")
    testImplementation(project(":commons"))
    testImplementation(project(":datamodel"))
    testImplementation(project(":plot-base"))
    testImplementation(project(":plot-builder"))
    testImplementation(project(":plot-stem"))
    testImplementation(project(":demo-and-test-shared"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.apache.xmlgraphics:batik-codec:$batikVersion")
    testImplementation(project(":platf-awt"))
}
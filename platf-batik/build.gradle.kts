/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

val batikVersion = project.extra["batik.version"] as String
val commonsIOVersion = project.extra["commons-io.version"] as String

dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":plot-builder"))
    compileOnly(project(":plot-stem"))
    compileOnly(project(":platf-awt"))
    compileOnly("org.apache.xmlgraphics:batik-codec:$batikVersion")

    // commons-io: enforce a newer version than Batik's transitive dependency.
    // A fix for https://github.com/JetBrains/lets-plot/issues/1421 (Drop commons-io dependency)
//    compileOnly("commons-io:commons-io:${commonsIOVersion}")
    constraints {
        api("commons-io:commons-io:$commonsIOVersion")
    }

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
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val batikVersion = project.extra["batik_version"] as String

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-stem"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-and-test-shared"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly(project(":platf-awt"))
                compileOnly("org.apache.xmlgraphics:batik-codec:$batikVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.apache.xmlgraphics:batik-codec:$batikVersion")
                implementation(project(":platf-awt"))
            }
        }
    }
}
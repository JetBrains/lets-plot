/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val batikVersion = extra["batik_version"] as String

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-config-portable"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
            }
        }

        jvmMain {
            dependencies {
                implementation(project(":platf-awt"))

                api("org.apache.xmlgraphics:batik-codec:$batikVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}
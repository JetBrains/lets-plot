/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val jfxVersion = extra["jfx_version"] as String
val jfxPlatform = extra["jfx_platform_resolved"] as String

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":vis-canvas"))
                implementation(project(":base-portable"))
                implementation(project(":plot-config-portable"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-common"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
                compileOnly("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
                compileOnly("org.openjfx:javafx-swing:$jfxVersion:$jfxPlatform")

                implementation(project(":platf-awt"))
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
/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val ktorVersion = extra["ktor_version"] as String
val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
val hamcrestVersion = extra["hamcrest_version"] as String
val mockitoVersion = extra["mockito_version"] as String
val assertjVersion = extra["assertj_version"] as String

kotlin {
    jvm()
    js() {
        browser {}
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("io.ktor:ktor-client-websockets:$ktorVersion")

                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":canvas"))
                compileOnly(project(":gis"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":gis"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.ktor:ktor-client-js:$ktorVersion")
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val hamcrestVersion = project.extra["hamcrest.version"] as String
val mockitoVersion = project.extra["mockito.version"] as String
val assertjVersion = project.extra["assertj.version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":canvas"))
                compileOnly(project(":datamodel"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":demo-and-test-shared")) {
                    // w: duplicate library name: org.jetbrains.lets-plot:plot-base
                    exclude(group = "org.jetbrains.lets-plot", module = "plot-base")
                }
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}


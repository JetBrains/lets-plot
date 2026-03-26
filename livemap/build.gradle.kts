@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val ktorVersion = project.extra["ktor.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val mockitoVersion = project.extra["mockito.version"] as String
val assertjVersion = project.extra["assertj.version"] as String
val kotlinxHtmlVersion = project.extra["kotlinx.html.version"] as String
val kotlinxBrowserVersion = project.extra["kotlinx.browser.version"] as String

kotlin {
    jvm()
    js() {
        browser {}
    }
    wasmJs() {
        browser {}
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":canvas"))
                compileOnly(project(":gis"))

                compileOnly("io.ktor:ktor-client-core:$ktorVersion")
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":gis"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-cio:$ktorVersion")
            }
        }


/*      Fix for build errors:
         - 'Could not find "io.github.oshai:kotlin-logging"...'
         - 'Could not find "io.ktor:ktor-client-js"...'
        (Kotlin 1.9.xx versions): */
        named("jsTest") {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }

        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:$kotlinxBrowserVersion")
            }
        }
    }
}

/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val os: org.gradle.internal.os.OperatingSystem = org.gradle.internal.os.OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

val ktorVersion = project.extra["ktor.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val hamcrestVersion = project.extra["hamcrest.version"] as String
val mockitoVersion = project.extra["mockito.version"] as String
val assertjVersion = project.extra["assertj.version"] as String

kotlin {
    jvm()
    js {
        browser {}
    }
    wasmJs {
        browser()
    }

    when {
        os.isMacOsX && arch == "arm64" -> macosArm64()
        os.isMacOsX && arch == "x86_64" -> macosX64()
        os.isLinux && arch == "arm64" -> linuxArm64()
        os.isLinux && arch == "x86_64" -> linuxX64()
        os.isWindows -> mingwX64()
        else -> throw Exception("Unsupported platform! Check project settings.")
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("io.ktor:ktor-client-core:$ktorVersion")
                compileOnly(project(":commons"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }

        nativeMain {
            dependencies {
                if (os.isMacOsX) {
                    implementation("io.ktor:ktor-client-darwin:${ktorVersion}")
                } else {
                    // Uses CIO for Linux and Windows
                    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                }
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":commons"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }

        // Fix for 'Could not find "io.github.oshai:kotlin-logging"...' and
        // 'Could not find "io.ktor:ktor-client-js"...'build errors (Kotlin 1.9.xx versions):
        named("jsTest") {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}


val mockkVersion = project.extra["mockk.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String
val kotlinxAtomicfuVersion = project.extra["kotlinx.atomicfu.version"] as String
val kotlinxIoVersion = project.extra["kotlinx.io.version"] as String
val hamcrestVersion = project.extra["hamcrest.version"] as String
val mockitoVersion = project.extra["mockito.version"] as String
val assertjVersion = project.extra["assertj.version"] as String

val os: org.gradle.internal.os.OperatingSystem = org.gradle.internal.os.OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

kotlin {
    jvm()
    js {
        browser()
    }
    wasmJs {
        browser()
    }

    val target = when {
        os.isMacOsX && arch == "arm64" -> macosArm64()
        os.isMacOsX && arch == "x86_64" -> macosX64()
        os.isLinux && arch == "arm64" -> linuxArm64()
        os.isLinux && arch == "x86_64" -> linuxX64()
        os.isWindows -> mingwX64()
        else -> throw Exception("Unsupported platform! Check project settings.")
    }

    target.compilations.getByName("main") {
        val stb_image by cinterops.creating {
            includeDirs(project.file("src/nativeInterop/cinterop"))
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        nativeMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:atomicfu:$kotlinxAtomicfuVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        nativeMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:$kotlinxIoVersion")
            }
        }

        jsMain {
            dependencies {
                compileOnly("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")

                // deflate for PNG
                implementation(npm("pako", "2.1.0"))

                // Add timezone support for js (in kotlinx-datetime)
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }

        wasmJsMain {
            dependencies {
                compileOnly("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")

                // deflate for PNG
                implementation("org.jetbrains.kotlin-wrappers:kotlin-pako:2026.3.8-2.1.0")

                // Add timezone support for js (in kotlinx-datetime)
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }

        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))

                // Fix for 'Could not find "io.github.oshai:kotlin-logging"...' build error (Kotlin 1.9.xx versions):
                implementation("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}


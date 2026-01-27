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
val hamcrestVersion = project.extra["hamcrest.version"] as String
val mockitoVersion = project.extra["mockito.version"] as String
val assertjVersion = project.extra["assertj.version"] as String

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                compileOnly("org.jetbrains.kotlinx:atomicfu:$kotlinxAtomicfuVersion")
            }
        }

        nativeMain {
            dependencies {
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
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
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

        named("jsMain") {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")

                // deflate for PNG
                implementation(npm("pako", "2.1.0"))

                // Add timezone support for js (in kotlinx-datetime)
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }

        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))

                // Fix for 'Could not find "io.github.microutils:kotlin-logging"...' build error (Kotlin 1.9.xx versions):
                implementation("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}


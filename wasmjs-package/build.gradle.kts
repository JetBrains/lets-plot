/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinVersion = project.extra["kotlin.version"] as String
val ktorVersion = project.extra["ktor.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String
val kotlinxBrowserVersion = project.extra["kotlinx.browser.version"] as String

kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "lets-plot.js"
                outputModuleName = "LetsPlot"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:$kotlinxBrowserVersion")

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":platf-w3c"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

                implementation("io.ktor:ktor-client-websockets:${ktorVersion}")
                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.github.oshai:kotlin-logging:${kotlinLoggingVersion}")

            }
        }
        wasmJsTest {
            dependencies {
                implementation(project(":visual-testing"))
            }
        }
    }
}


val buildDir = project.layout.buildDirectory.get().asFile.path as String // 'project.buildDir' has been deprecated.
val prodDistDir = "${buildDir}/dist/wasmJs/productionExecutable/"
val jsArtifactName = "lets-plot.js"
val wasmJsTestExpectedImagesDir = project.file("src/wasmJsTest/resources/expected-images")

val updateWasmJsTestImageIndexes = tasks.register("updateWasmJsTestImageIndexes") {
    inputs.dir(wasmJsTestExpectedImagesDir)
    outputs.dir(wasmJsTestExpectedImagesDir)

    doLast {
        if (!wasmJsTestExpectedImagesDir.exists()) {
            return@doLast
        }

        wasmJsTestExpectedImagesDir
            .walkTopDown()
            .filter { it.isDirectory }
            .forEach { dir ->
                val pngFileNames = dir.listFiles()
                    ?.asSequence()
                    ?.filter { it.isFile && it.extension == "png" }
                    ?.map { it.name }
                    ?.sorted()
                    ?.toList()
                    ?: emptyList()

                val indexFile = dir.resolve("index.txt")
                when {
                    pngFileNames.isNotEmpty() -> {
                        indexFile.writeText(pngFileNames.joinToString(separator = "\n", postfix = "\n"))
                    }

                    indexFile.exists() -> {
                        indexFile.delete()
                    }
                }
            }
    }
}

tasks.register<Copy>("copyForPublish") {
    dependsOn(tasks.named("wasmJsBrowserProductionWebpack"))
    from("${prodDistDir}${jsArtifactName}")
    rename(jsArtifactName, "lets-plot.min.js")
    into("${rootDir}/wasmjs-package/distr/")
}

tasks.named("wasmJsTestProcessResources") {
    dependsOn(updateWasmJsTestImageIndexes)
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val kotlinVersion = project.extra["kotlin_version"] as String
val ktorVersion = project.extra["ktor_version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

kotlin {
    js {
        browser {
            webpackTask {
                mainOutputFileName = "lets-plot.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${kotlinVersion}")

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

                implementation("io.ktor:ktor-client-websockets-js:${ktorVersion}")
                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.github.microutils:kotlin-logging-js:${kotlinLoggingVersion}")
            }
        }
    }
}


val jsPackageBuildDir = project.layout.buildDirectory.get().asFile.path as String // 'project.buildDir' has been deprecated.
val prodJsExecDistDir = "${jsPackageBuildDir}/dist/js/productionExecutable/"
val jsArtifactName = "lets-plot.js"

tasks.register<Copy>("copyForPublish") {
    dependsOn(tasks.named("jsBrowserProductionWebpack"))
    from("${prodJsExecDistDir}${jsArtifactName}")
    rename(jsArtifactName, "lets-plot.min.js")
    into("${rootDir}/js-package/distr/")
}

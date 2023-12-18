/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
val kotlinVersion = extra["kotlin_version"] as String
val ktorVersion = extra["ktor_version"] as String

kotlin {
    js {
        browser()
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

                implementation("io.ktor:ktor-client-websockets-js:${ktorVersion}")
                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.github.microutils:kotlin-logging-js:${kotlinLoggingVersion}")
            }
        }
    }
}


val jsPackageBuildDir = project.layout.buildDirectory.get().asFile.path as String // 'project.buildDir' has been deprecated.
val prodJsExecDistDir = "${jsPackageBuildDir}/dist/js/productionExecutable/"
val devJsExecDistDir = "${jsPackageBuildDir}/dist/js/developmentExecutable/"
val jsArtifactName = "js-package.js"

tasks.named("jsBrowserProductionWebpack") {
    doLast {
        copy {
            from("${prodJsExecDistDir}${jsArtifactName}")
            rename(jsArtifactName, "lets-plot.min.js")
            into(prodJsExecDistDir)
        }
    }
}

tasks.named("jsBrowserDevelopmentWebpack"){
    doLast {
        copy {
            from("${devJsExecDistDir}${jsArtifactName}")
            rename(jsArtifactName, "lets-plot.js")
            into(devJsExecDistDir)
        }
    }
}

tasks.register<Copy>("copyForPublish") {
    dependsOn(tasks.named("jsBrowserProductionWebpack"))
    from("${prodJsExecDistDir}${jsArtifactName}")
    rename(jsArtifactName, "lets-plot.min.js")
    into("${rootDir}/js-package/distr/")
}

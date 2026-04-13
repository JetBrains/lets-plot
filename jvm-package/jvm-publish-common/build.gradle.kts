/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm()
    js().browser()
    wasmJs().browser()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":commons"))
                api(project(":canvas"))
                api(project(":datamodel"))
                api(project(":plot-base"))
                api(project(":plot-builder"))
                api(project(":plot-stem"))
                api(project(":plot-raster"))

                api("org.jetbrains.kotlinx:kotlinx-datetime:${project.extra["kotlinx.datetime.version"]}")
                api("io.github.oshai:kotlin-logging:${project.extra["kotlinLogging.version"]}")
            }
        }
    }
}

val artifactBaseName = "lets-plot-common"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]
val jarJavaDocs by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from("$rootDir/README.md")
}
val jarJavaDocsFile = jarJavaDocs.flatMap { it.archiveFile }

tasks.withType<Jar>().configureEach {
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        groupId = artifactGroupId
        version = artifactVersion

        if (artifactId == project.name) {
            artifactId = artifactBaseName
        } else if (artifactId.startsWith(project.name)) {
            artifactId = artifactId.replace(project.name, artifactBaseName)
        }

        artifact(jarJavaDocsFile) {
            builtBy(jarJavaDocs)
            classifier = "javadoc"
            extension = "jar"
        }

        pom {
            name.set("Lets-Plot common modules")
            description.set("Lets-Plot common modules.")
            url.set("https://github.com/JetBrains/lets-plot")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://raw.githubusercontent.com/JetBrains/lets-plot/master/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("jetbrains")
                    name.set("JetBrains")
                    email.set("lets-plot@jetbrains.com")
                }
            }

            scm {
                url.set("https://github.com/JetBrains/lets-plot")
            }
        }
    }

    repositories {
        mavenLocal {
            url = uri("$mavenLocalPath")
        }
        maven {
            // For SNAPSHOT publication use separate URL and credentials:
            if (version.toString().endsWith("-SNAPSHOT")) {
                url = uri(rootProject.project.extra["mavenSnapshotPublishUrl"].toString())

                credentials {
                    username = rootProject.project.extra["sonatypeUsername"].toString()
                    password = rootProject.project.extra["sonatypePassword"].toString()
                }
            } else {
                url = uri(rootProject.project.extra["mavenReleasePublishUrl"].toString())
            }
        }
    }
}

signing {
    if (!project.version.toString().contains("SNAPSHOT")) {
        sign(publishing.publications)
    }
}

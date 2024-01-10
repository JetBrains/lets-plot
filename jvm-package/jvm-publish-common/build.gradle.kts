/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm()
}

val artifactBaseName = "lets-plot-common"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
val mavenLocalPath = rootProject.extra["localMavenRepository"]

val jvmJarCommon by tasks.registering (Jar::class) {
    archiveFileName.set("$artifactBaseName-${artifactVersion}.jar")

    // Add LICENSE file to the META-INF folder inside published JAR files.
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }
}

val pomDependencies = listOf(
    // Lets-Plot core artifacts.
    listOf(project.group, "commons-jvm", project.version),
    listOf(project.group, "datamodel-jvm", project.version),
    listOf(project.group, "plot-base-jvm", project.version),
    listOf(project.group, "plot-builder-jvm", project.version),
    listOf(project.group, "plot-stem-jvm", project.version),
    listOf(project.group, "deprecated-in-v4-jvm", project.version),
    // Kotlin logging.
    listOf("io.github.microutils", "kotlin-logging", kotlinLoggingVersion)
)

publishing {
    publications {
        register("letsPlotJvmCommon", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(jvmJarCommon.get())

            pom {
                name = "Lets-Plot common modules"
                description = "Lets-Plot JVM package without the actual rendering."
                url = "https://github.com/JetBrains/lets-plot"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://raw.githubusercontent.com/JetBrains/lets-plot/master/LICENSE"
                    }
                }

                developers {
                    developer {
                        id = "jetbrains"
                        name = "JetBrains"
                        email = "lets-plot@jetbrains.com"
                    }
                }

                scm {
                    url = "https://github.com/JetBrains/lets-plot"
                }

                // Dependencies
                withXml {
                    val deps = asNode().appendNode("dependencies")
                    pomDependencies.forEach() {
                        val dep = deps.appendNode("dependency")
                        dep.appendNode("groupId", it[0])
                        dep.appendNode("artifactId", it[1])
                        dep.appendNode("version", it[2])
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal {
            url = uri("$mavenLocalPath")
        }
    }
}

signing {
    if (!project.version.toString().contains("SNAPSHOT")) {
        sign(publishing.publications["letsPlotJvmCommon"])
    }
}

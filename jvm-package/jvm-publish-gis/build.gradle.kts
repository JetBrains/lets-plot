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

val artifactBaseName = "lets-plot-gis"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val ktorVersion = extra["ktor_version"] as String
val mavenLocalPath = rootProject.extra["localMavenRepository"]

val jvmJarGis by tasks.named<Jar>("jvmJar") {
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
    listOf(project.group, "canvas-jvm", project.version),
    listOf(project.group, "gis-jvm", project.version),
    listOf(project.group, "livemap-jvm", project.version),
    listOf(project.group, "plot-livemap-jvm", project.version),
    // Ktor.
    listOf("io.ktor", "ktor-client-websockets-jvm", ktorVersion),
    listOf("io.ktor", "ktor-client-cio", ktorVersion)
)

publishing {
    publications {
        register("letsPlotGIS", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(jvmJarGis)

            pom {
                name = "Lets-Plot GIS"
                description = "Interactive map and Geocoding related modules."
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
        sign(publishing.publications["letsPlotGIS"])
    }
}

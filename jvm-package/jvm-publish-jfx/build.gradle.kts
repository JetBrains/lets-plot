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

val artifactBaseName = "lets-plot-jfx"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]

val jvmJarJfx by tasks.named<Jar>("jvmJar") {
    archiveFileName.set("$artifactBaseName-${artifactVersion}.jar")

    // Add LICENSE file to the META-INF folder inside published JAR files.
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }
}

val pomDependencies = listOf(
    // Lets-Plot JVM common
    listOf(project.group, "lets-plot-common", project.version),
    // Lets-Plot core artifacts.
    listOf(project.group, "platf-awt-jvm", project.version),
    listOf(project.group, "platf-jfx-swing-jvm", project.version)
)

publishing {
    publications {
        register("letsPlotJvmJfx", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(jvmJarJfx)

            pom {
                name = "Lets-Plot for JavaFX"
                description = "Lets-Plot JVM package with Swing/JavaFX rendering"
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
        sign(publishing.publications["letsPlotJvmJfx"])
    }
}

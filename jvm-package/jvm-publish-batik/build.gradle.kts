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

val artifactBaseName = "lets-plot-batik"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val batikVersion = project.extra["batik_version"] as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]

val jvmJarBatik by tasks.named<Jar>("jvmJar") {
    archiveFileName.set("$artifactBaseName-${artifactVersion}.jar")

    // Add LICENSE file to the META-INF folder inside published JAR files.
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }
}

val pomDependencies = listOf(
    // Lets-Plot JVM common.
    listOf(project.group, "lets-plot-common", project.version),
    // Lets-Plot core artifacts.
    listOf(project.group, "platf-awt", project.version),
    listOf(project.group, "platf-batik", project.version),
    // Batik.
    listOf("org.apache.xmlgraphics", "batik-codec", batikVersion)
)

publishing {
    publications {
        register("letsPlotJvmBatik", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(jvmJarBatik)

            pom {
                name = "Lets-Plot for Swing/Batik"
                description = "Lets-Plot JVM package with Swing/Batik rendering"
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
                    pomDependencies.forEach {
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
        sign(publishing.publications["letsPlotJvmBatik"])
    }
}

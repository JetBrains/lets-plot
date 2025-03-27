/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
    id("com.gradleup.shadow") version "8.3.6" // Updated to use GradleUp shadow plugin
}

val artifactBaseName = "idea-lets-plot-plugin"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]

val packagePrefix = "ideaLPP"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))

                implementation(project(":platf-awt"))
                implementation(project(":platf-batik"))

                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
            }
        }
    }
}

// Disable all tasks with metadata in their name -
// we don't need 'metadata jar': this is no longer 'muptiplatform' lib.
tasks.configureEach {
    if (name.contains("metadata", ignoreCase = true)) {
        enabled = false
    }
}

// Configure the JVM jar task to use the shadow jar output
val jvmJar by tasks.named<Jar>("jvmJar") {
    enabled = false
}

val javaDocsJar by tasks.creating(Jar::class) {
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("javadoc")
    from("$rootDir/README.md")
    group = "lets plot"
}

// Create fat JAR with shadowed (relocated) classes
val shadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    from(kotlin.jvm().compilations.getByName("main").output)
    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))

    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("")

    group = "lets plot"

    // Exclude Kotlin module files
    exclude("META-INF/*.kotlin_module")

    // Exclude packages
    exclude("org/slf4j/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/annotations/**")
    exclude("kotlinx/coroutines/**")
    exclude("kotlin/**")
    exclude("javax/xml/**")
    exclude("_COROUTINE/**")
    exclude("DebugProbesKt.bin")

    // Relocate all classes
    relocate("org.jetbrains.letsPlot", "$packagePrefix.org.jetbrains.letsPlot")

    // Add LICENSE file to the META-INF folder inside published JAR files
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }

    // Merge service files
    mergeServiceFiles()
}


// Create a sources JAR task with shadowed sources
//val sourcesJar = tasks.register<Jar>("sourcesJar") {
val sourcesJar = tasks.named<org.gradle.jvm.tasks.Jar>("jvmSourcesJar") {
    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("sources")
    // Clear the default archiveAppendix that adds "jvm"
    archiveAppendix.set("")

    group = "lets plot"

    // Set up basic task configuration here
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    doFirst {
        // Create a temporary directory for processed files
        val tempDir = layout.buildDirectory.dir("tmp/shadowSourcesJar").get().asFile
        tempDir.deleteRecursively()
        tempDir.mkdirs()

        // Process all project dependencies
        project.configurations.getByName("jvmCompileClasspath").allDependencies.withType<ProjectDependency>()
            .forEach { dep ->
                val projectDep = dep.dependencyProject
                logger.lifecycle("Processing sources from dependency: ${projectDep.name}")

                // Handle Kotlin Multiplatform projects
                val kotlinMultiplatformExt =
                    projectDep.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()
                if (kotlinMultiplatformExt != null) {
                    // Process source sets
                    listOf("jvmMain", "commonMain").forEach { sourceSetName ->
                        kotlinMultiplatformExt.sourceSets.findByName(sourceSetName)?.kotlin?.srcDirs?.forEach { srcDir ->
                            processSourceDirectory(srcDir, tempDir, packagePrefix)
                        }
                    }
                } else {
                    // Handle JVM-only projects
                    val sourceSets = projectDep.extensions.findByType<SourceSetContainer>()
                    sourceSets?.findByName("main")?.allSource?.srcDirs?.forEach { srcDir ->
                        processSourceDirectory(srcDir, tempDir, packagePrefix)
                    }
                }
            }

        // Add only the processed files with the target prefix path
        from(tempDir)
        include("$packagePrefix/**")
    }

    doLast {
        logger.lifecycle("Sources JAR created with relocated packages")
    }
}

// Helper function to process a source directory
fun Project.processSourceDirectory(
    srcDir: File,
    tempDir: File,
    packagePrefix: String
) {
    if (srcDir.exists()) {
        logger.lifecycle("Processing sources from: ${srcDir.absolutePath}")

        // Copy and process the files
        copy {
            from(srcDir)
            into(tempDir)
            include("**/*.kt", "**/*.java")

            // Process only files in org.jetbrains.letsPlot package
            eachFile {
                if (path.contains("org/jetbrains/letsPlot")) {
                    // Create relocated path with prefix
                    val newPath = path.replace(
                        "org/jetbrains/letsPlot",
                        "$packagePrefix/org/jetbrains/letsPlot"
                    )

                    // Update the path
                    path = newPath

                    // Filter file content to update package and import statements
                    filter { line ->
                        when {
                            line.startsWith("package org.jetbrains.letsPlot") -> {
                                line.replace(
                                    "package org.jetbrains.letsPlot",
                                    "package $packagePrefix.org.jetbrains.letsPlot"
                                )
                            }

                            line.contains("import org.jetbrains.letsPlot") -> {
                                line.replace(
                                    "import org.jetbrains.letsPlot",
                                    "import $packagePrefix.org.jetbrains.letsPlot"
                                )
                            }

                            else -> line
                        }
                    }
                } else {
                    // Exclude files that aren't in the target package
                    exclude()
                }
            }
        }
    }
}

// Configure which tasks the build depends on
// to be able to run: `./gradlew jvm-package:jvm-publish-idea-lets-plot-plugin:build`
tasks.named("build") {
    dependsOn(shadowJar, sourcesJar, javaDocsJar)
}


publishing {
    publications {
        register("ideaLetsPlotPlugin", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(shadowJar)
            artifact(sourcesJar)
            artifact(javaDocsJar)

            pom {
                name = "Shadowed Lets-Plot for IDEA Plugin"
                description = "Shadowed Lets-Plot library for Lets-Plot in Sci-View IDEA Plugin."
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

                // No dependencies in POM
                withXml {
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
        sign(publishing.publications["ideaLetsPlotPlugin"])
    }
}
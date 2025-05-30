plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("demoRunner")
    js {
        browser()
        binaries.executable()
    }

    val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String
    val ktorVersion = project.extra["ktor_version"] as String
    val kotlinxCoroutinesVersion = project.extra["kotlinx_coroutines_version"] as String
    val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-common-plot"))
                implementation(project(":demo-and-test-shared"))
            }
        }
        named("demoRunnerMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":demo-common-jvm-utils"))

                implementation(project(":canvas"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":gis"))

                implementation("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("org.slf4j:slf4j-simple:${project.extra["slf4j_version"]}")  // Enable logging to console
                implementation(project(":platf-awt"))

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(project(":platf-w3c"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }
    }
}

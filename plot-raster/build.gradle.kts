/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    //id("com.android.library")
    `maven-publish`
    signing
}

//val letsPlotVersion = extra["letsPlot.version"] as String
//val kotlinLoggingVersion = extra["kotlinLogging.version"] as String
//val assertjVersion = extra["assertj.version"] as String

kotlin {
    //androidTarget {
    //    publishLibraryVariants("release")
    //}

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":canvas"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-base"))
                compileOnly(project(":plot-stem"))
                compileOnly(project(":plot-builder"))
            }
        }

//        named("jvmMain") {
//            dependencies {
////                compileOnly("org.jetbrains.skiko:skiko-awt:$skikoVersion")
//                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
//            }
//        }

//        named("jvmTest") {
//            dependencies {
//                implementation(kotlin("test"))
//                implementation("org.assertj:assertj-core:$assertjVersion")
//                implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:platf-awt:$letsPlotVersion")
//                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
//                implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
//            }
//        }
//
//        nativeMain() {
//            dependencies {
//                implementation("org.jetbrains.lets-plot:canvas:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:commons:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
//                implementation("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
//                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
//            }
//        }

        //named("androidMain") {
        //    dependencies {
        //        compileOnly("org.jetbrains.skiko:skiko-android:$skikoVersion")
        //        compileOnly("org.jetbrains.lets-plot:commons:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:datamodel:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-base:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-builder:$letsPlotVersion")
        //        compileOnly("org.jetbrains.lets-plot:plot-stem:$letsPlotVersion")
        //    }
        //}
    }
}

//android {
//    namespace = "org.jetbrains.letsPlot.skia.android"
//
//    compileSdk = (findProperty("android.compileSdk") as String).toInt()
//
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//
//    defaultConfig {
//        minSdk = (findProperty("android.minSdk") as String).toInt()
//    }
//
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false // true - error: when compiling demo cant resolve classes
////            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//
//    kotlin {
//        jvmToolchain(11)
//    }
//}


///////////////////////////////////////////////
//  Publishing
///////////////////////////////////////////////

//afterEvaluate {
//    publishing {
//        publications.forEach { pub ->
//            with(pub as MavenPublication) {
//                artifact(tasks.jarJavaDocs)
//
//                pom {
//                    name.set("Lets-Plot Rasterizer")
//                    description.set("Rasterizer for Lets-Plot multiplatform plotting library.")
//                    url.set("https://github.com/JetBrains/lets-plot-skia")
//                    licenses {
//                        license {
//                            name.set("MIT")
//                            url.set("https://raw.githubusercontent.com/JetBrains/lets-plot-skia/master/LICENSE")
//                        }
//                    }
//                    developers {
//                        developer {
//                            id.set("jetbrains")
//                            name.set("JetBrains")
//                            email.set("lets-plot@jetbrains.com")
//                        }
//                    }
//                    scm {
//                        url.set("https://github.com/JetBrains/lets-plot-skia")
//                    }
//                }
//            }
//        }
//
//        repositories {
//            mavenLocal {
//                url = uri("$rootDir/.maven-publish-dev-repo")
//            }
//        }
//    }
//}

//signing {
//    if (!(project.version as String).contains("SNAPSHOT")) {
//        sign(publishing.publications)
//    }
//}
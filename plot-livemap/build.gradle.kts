/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js() {
        browser {}
        // browser {}
        // /\
        // ||
        // ERROR in ./kotlin/lets-plot-livemap.js
        // Module not found: Error: Can't resolve 'vis-canvas' in '/Users/ikupriyanov/Projects/lets-plot/build/js/packages/lets-plot-livemap/kotlin'
        // @ ./kotlin/lets-plot-livemap.js 3:4-120
        // @ multi ./kotlin/lets-plot-livemap.js source-map-support/browser-source-map-support.js
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":demo-and-test-shared"))
            }
        }
    }
}

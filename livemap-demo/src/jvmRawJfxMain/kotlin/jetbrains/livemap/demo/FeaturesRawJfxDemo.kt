/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.api.geocodingHint
import jetbrains.livemap.api.location

class FeaturesRawJfxDemo : Application() {

    override fun start(theStage: Stage) {
        val dim = Vector(800, 600)
        val javafxCanvasControl = JavafxCanvasControl(dim, 1.0)
        FeaturesDemoModel(dim.toDoubleVector()).show(javafxCanvasControl) {
            zoom = 7
            location {
                geocodingHint {
                    parent = MapRegion.withName("Russia")
                    level = FeatureLevel.CITY
                }
                name = "Moscow"
            }
        }

        theStage.title = "Javafx Livemap Demo"
        theStage.scene = Scene(
            Group().apply {
                children.add(javafxCanvasControl.javafxRoot)
            },
            dim.x.toDouble(),
            dim.y.toDouble()
        )
        theStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(FeaturesRawJfxDemo::class.java, *args)
        }
    }
}

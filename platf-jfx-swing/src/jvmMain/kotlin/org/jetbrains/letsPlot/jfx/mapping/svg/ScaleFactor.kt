/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.scene.transform.Transform
import javafx.scene.transform.Translate

/**
 * Scale canvas and un-scale all shapes
 * to make image sharp on retina and other hires monitors
 */
internal object ScaleFactor {

    //    1 -> standard resolution (no scaling)
    //    2 -> retina
    //    ...
    val value = 1.0 //Toolkit.getDefaultToolkit().screenResolution / 96.0
}

fun unScale(v: Double) = v * ScaleFactor.value

fun unScaleTransforms(transforms: List<Transform>): List<Transform> {
    return transforms.map {
        if (it is Translate) {
            unScaleTranslate(it)
        } else {
            it
        }
    }
}

fun unScaleTranslate(translateTransform: Translate): Translate {
    return Translate(
        translateTransform.x * ScaleFactor.value,
        translateTransform.y * ScaleFactor.value
    )
}

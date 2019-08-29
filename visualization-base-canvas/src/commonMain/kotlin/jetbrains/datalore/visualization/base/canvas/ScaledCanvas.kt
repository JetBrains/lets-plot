package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.geometry.Vector

abstract class ScaledCanvas protected constructor(context2d: Context2d, override val size: Vector, pixelRatio: Double) : Canvas {
    final override val context2d: Context2d =
        if (pixelRatio == 1.0) context2d else ScaledContext2d(context2d, pixelRatio)
}

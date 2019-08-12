package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.rendering.primitives.Label.LabelPosition.*

class Label(override var origin: DoubleVector, private var text: Text) : RenderBox {
    private var frame: Frame? = null
    override var dimension = DoubleVector.ZERO
    private val rectangle: Rectangle = Rectangle()
    var padding: Double = 0.0
    var background: Color = Color.TRANSPARENT
    private var position = RIGHT

    override fun render(ctx: Context2d) {
        if (text.isDirty) {
            dimension = text.measureText(ctx) + DoubleVector(2 * padding, 2 * padding)

            rectangle.apply {
                rect = DoubleRectangle(DoubleVector.ZERO, dimension)
                color = background
            }

            origin += when (position) {
                LEFT -> DoubleVector(-dimension.x, 0.0)
                CENTER -> DoubleVector(-dimension.x / 2, 0.0)
                RIGHT -> DoubleVector.ZERO
            }

            text.origin = DoubleVector(padding, padding)
            frame = Frame.create(origin, rectangle, text)
        }

        frame?.render(ctx)
    }

    enum class LabelPosition {
        RIGHT,
        CENTER,
        LEFT
    }
}

private operator fun DoubleVector.minus(doubleVector: DoubleVector): DoubleVector {
    return subtract(doubleVector)
}

private operator fun DoubleVector.plus(doubleVector: DoubleVector): DoubleVector {
    return add(doubleVector)
}

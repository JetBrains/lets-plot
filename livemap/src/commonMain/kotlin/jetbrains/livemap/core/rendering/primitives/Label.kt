package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d

class Label(private var origin: DoubleVector, private var text: Text) : RenderBox {
    private var frame: Frame? = null
    private var dimension = DoubleVector.ZERO
    private val rectangle: Rectangle = Rectangle()
    private var padding: Double = 0.0
    private var background: Color? = null
    private var position = LabelPosition.RIGHT

    override fun origin(): DoubleVector {
        return origin
    }

    override fun dimension(): DoubleVector {
        return dimension
    }

    override fun render(ctx: Context2d) {
        if (text.isDirty) {
            dimension = text.measureText(ctx).add(DoubleVector(2 * padding, 2 * padding))

            rectangle.apply {
                rect = DoubleRectangle(DoubleVector.ZERO, dimension)
                color = background
            }

            when (position) {
                LabelPosition.LEFT -> origin = origin.subtract(DoubleVector(dimension.x, 0.0))
                LabelPosition.CENTER -> origin = origin.subtract(DoubleVector(dimension.x / 2, 0.0))
                LabelPosition.RIGHT -> {}
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
package jetbrains.livemap.core.rendering.controls

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.rendering.primitives.Frame
import jetbrains.livemap.core.rendering.primitives.Rectangle
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.core.rendering.primitives.Text

class Button(position: DoubleVector, buttonSize: DoubleVector, buttonText: String) : RenderBox {

    private var frame: RenderBox

    init {
        val buttonRect = DoubleRectangle(DoubleVector.ZERO, buttonSize)
        val textPosition = buttonRect.center

        val rectangle = Rectangle().apply {
            rect = buttonRect
            color = Color.LIGHT_GRAY
        }

        val txt = Text().apply {
            text = listOf(buttonText)
            origin = textPosition
        }

        frame = Frame.create(position, rectangle, txt)
    }

    override fun origin(): DoubleVector {
        return frame.origin()
    }

    override fun dimension(): DoubleVector {
        return frame.dimension()
    }

    override fun render(ctx: Context2d) {
        frame.render(ctx)
    }
}
package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.canvas.Context2d
import kotlin.math.max


class Text : RenderBox {
    var origin = DoubleVector.ZERO
    var dimension = DoubleVector.ZERO
    var text: Collection<String> = emptyList()
        set(value) {
            field = value
            isDirty = true
        }
    var color: Color = Color.WHITE
    var isDirty = true
        private set
    var fontHeight: Double = 0.0
    var fontFamily = "sherif"

    override fun origin(): DoubleVector {
        return origin
    }

    override fun dimension(): DoubleVector {
        return dimension
    }

    override fun render(ctx: Context2d) {
        ctx.setFont(fontHeight.toString() + "px " + fontFamily)
        ctx.setTextBaseline(Context2d.TextBaseline.BOTTOM)

        if (isDirty) {
            dimension = calculateDimension(ctx)
            isDirty = false
        }

        ctx.setFillColor(color.toHexColor())

        var y = fontHeight
        for (s in text) {
            ctx.fillText(s, 0.0, y)
            y += fontHeight
        }
    }

    fun measureText(ctx: Context2d): DoubleVector {
        if (isDirty) {
            ctx.save()
            ctx.setFont(fontHeight.toString() + "px " + fontFamily)
            ctx.setTextBaseline(Context2d.TextBaseline.BOTTOM)

            dimension = calculateDimension(ctx)
            isDirty = false
            ctx.restore()
        }

        return dimension
    }

    private fun calculateDimension(ctx: Context2d): DoubleVector {
        var maxWidth = 0.0
        for (s in text) {
            maxWidth = max(maxWidth, ctx.measureText(s))
        }

        return DoubleVector(maxWidth, text.size * fontHeight)
    }
}
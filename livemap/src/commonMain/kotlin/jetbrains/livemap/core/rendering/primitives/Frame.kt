package jetbrains.livemap.core.rendering.primitives

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Context2d
import kotlin.math.max

class Frame constructor(private val position: DoubleVector, private val renderBoxes: List<RenderBox>) :
    RenderBox {

    override val origin get() = position
    override val dimension get() = calculateDimension()

    override fun render(ctx: Context2d) {
        renderBoxes.forEach { primitive ->
            ctx.save()
            val origin = primitive.origin
            ctx.translate(origin.x, origin.y)
            primitive.render(ctx)
            ctx.restore()
        }
    }

    private fun calculateDimension(): DoubleVector {
        var right = getRight(renderBoxes[0])
        var bottom = getBottom(renderBoxes[0])

        for (renderBox in renderBoxes) {
            right = max(right, getRight(renderBox))
            bottom = max(bottom, getBottom(renderBox))
        }

        return DoubleVector(right, bottom)
    }

    private fun getRight(renderObject: RenderBox): Double {
        return renderObject.origin.x + renderBoxes[0].dimension.x
    }

    private fun getBottom(renderObject: RenderBox): Double {
        return renderObject.origin.y + renderBoxes[0].dimension.y
    }

    companion object {
        fun create(position: DoubleVector, vararg renderBoxes: RenderBox): Frame {
            return Frame(position, renderBoxes.toList())
        }
    }
}
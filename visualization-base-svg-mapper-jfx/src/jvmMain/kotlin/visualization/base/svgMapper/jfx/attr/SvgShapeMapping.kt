package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import jetbrains.datalore.visualization.base.svg.SvgColor
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgShape

internal abstract class SvgShapeMapping<TargetT : Shape>(target: TargetT) : SvgAttrMapping<TargetT>(target) {
    init {
//        target.smoothProperty().set(false)
//        target.strokeType = StrokeType.CENTERED
    }

    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> setColor(value as SvgColor, fillGet(target), fillSet(target))
            SvgShape.FILL_OPACITY.name -> setOpacity(asDouble(value), fillGet(target), fillSet(target))
            SvgShape.STROKE.name -> setColor(value as SvgColor, strokeGet(target), strokeSet(target))
            SvgShape.STROKE_OPACITY.name -> setOpacity(asDouble(value), strokeGet(target), strokeSet(target))
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = asDouble(value)
            else -> super.setAttribute(name, value)
        }
    }

    companion object {
        private val fillGet = { shape: Shape ->
            // This will reset fill color to black if color is defined via style
            { shape.fill as? Color ?: Color.BLACK }
        }
        private val fillSet = { shape: Shape -> { c: Color -> shape.fill = c } }
        private val strokeGet = { shape: Shape ->
            // This will reset stroke color to black if color is defined via style
            { shape.stroke as? Color ?: Color.BLACK }
        }
        private val strokeSet = { shape: Shape -> { c: Color -> shape.stroke = c } }


        private fun setColor(value: SvgColor, get: () -> Color, set: (Color) -> Unit) {
            val svgColorString = value.toString()
            val newColor =
                    if (svgColorString == SvgColors.NONE.toString()) {
                        Color(0.0, 0.0, 0.0, 0.0)
                    } else {
                        val new = Paint.valueOf(svgColorString) as Color
                        val curr = get()
                        Color.color(new.red, new.green, new.blue, curr.opacity)
                    }
            set(newColor)
        }

        private fun setOpacity(value: Double, get: () -> Color, set: (Color) -> Unit) {
            val c = get()
            set(Color.color(c.red, c.green, c.blue, value))
        }
    }
}
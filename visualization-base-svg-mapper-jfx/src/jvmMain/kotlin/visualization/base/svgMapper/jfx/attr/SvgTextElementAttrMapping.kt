package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.geometry.VPos
import javafx.scene.text.Text
import jetbrains.datalore.visualization.base.svg.SvgConstants
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_TEXT_DY_CENTER
import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_TEXT_DY_TOP
import jetbrains.datalore.visualization.base.svg.SvgTextContent
import jetbrains.datalore.visualization.base.svg.SvgTextElement

internal class SvgTextElementAttrMapping(target: Text) : SvgShapeMapping<Text>(target) {
    private var svgTextAnchor: String? = null

    init {
        target.boundsInLocalProperty().addListener(object : ChangeListener<Bounds> {
            override fun changed(observable: ObservableValue<out Bounds>?, oldValue: Bounds?, newValue: Bounds?) {
                updateTranslateX(svgTextAnchor, target)
            }
        })
    }

    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgTextElement.X.name -> target.x = value as Double
            SvgTextElement.Y.name -> target.y = value as Double
            SvgTextContent.TEXT_ANCHOR.name -> {
                svgTextAnchor = value as String?
                updateTranslateX(svgTextAnchor, target)
            }
            SvgTextContent.TEXT_DY.name -> {
                when (value) {
                    SVG_TEXT_DY_TOP -> target.textOrigin = VPos.TOP
                    SVG_TEXT_DY_CENTER -> target.textOrigin = VPos.CENTER
                    else -> target.textOrigin = VPos.BASELINE
                }
            }

            SvgTextContent.FILL.name,
            SvgTextContent.FILL_OPACITY.name,
            SvgTextContent.STROKE.name,
            SvgTextContent.STROKE_OPACITY.name,
            SvgTextContent.STROKE_WIDTH.name -> super.setAttribute(name, value)

            else -> super.setAttribute(name, value)
        }
    }

    companion object {
        private fun updateTranslateX(svgTextAnchor: String?, target: Text) {
            val width = target.boundsInLocal.width
            SvgConstants.SVG_TEXT_ANCHOR_END
            when (svgTextAnchor) {
                SvgConstants.SVG_TEXT_ANCHOR_END -> {
                    target.translateX = -width
                }
                SvgConstants.SVG_TEXT_ANCHOR_MIDDLE -> {
                    target.translateX = -width / 2
                }
                else -> {
                    target.translateX = 0.0
                }
            }
        }
    }
}
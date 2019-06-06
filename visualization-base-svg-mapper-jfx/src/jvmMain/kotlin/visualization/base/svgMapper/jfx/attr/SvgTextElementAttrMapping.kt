package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.text.Text
import jetbrains.datalore.visualization.base.svg.SvgTextContent
import jetbrains.datalore.visualization.base.svg.SvgTextElement

internal class SvgTextElementAttrMapping(target: Text) : SvgShapeMapping<Text>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgTextElement.X.name -> target.x = value as Double
            SvgTextElement.Y.name -> target.y = value as Double
            SvgTextContent.TEXT_ANCHOR.name -> TODO(SvgTextContent.TEXT_ANCHOR.name)
            SvgTextContent.TEXT_DY.name -> TODO(SvgTextContent.TEXT_DY.name)

            SvgTextContent.FILL.name,
            SvgTextContent.FILL_OPACITY.name,
            SvgTextContent.STROKE.name,
            SvgTextContent.STROKE_OPACITY.name,
            SvgTextContent.STROKE_WIDTH.name -> super.setAttribute(name, value)
            else -> super.setAttribute(name, value)
        }
    }
}
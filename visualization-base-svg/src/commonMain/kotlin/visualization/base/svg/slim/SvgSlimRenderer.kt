package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.visualization.base.svg.slim.AttributeUtil.oneIfNull
import jetbrains.datalore.visualization.base.svg.slim.AttributeUtil.stringOrNull
import jetbrains.datalore.visualization.base.svg.slim.AttributeUtil.zeroIfNull
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.CIRCLE
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.LINE
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.PATH
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.RECT

internal object SvgSlimRenderer {
    fun draw(element: SlimBase, context: CanvasContext) {
        when (element.elementName) {
            CIRCLE -> context.drawCircle(
                    zeroIfNull(element, SlimBase.cx),
                    zeroIfNull(element, SlimBase.cy),
                    zeroIfNull(element, SlimBase.r),
                    null,
                    stringOrNull(element, SlimBase.transform),
                    stringOrNull(element, SlimBase.fill),
                    oneIfNull(element, SlimBase.fillOpacity),
                    stringOrNull(element, SlimBase.stroke),
                    oneIfNull(element, SlimBase.strokeOpacity),
                    zeroIfNull(element, SlimBase.strokeWidth)
            )
            LINE -> context.drawLine(
                    zeroIfNull(element, SlimBase.x1),
                    zeroIfNull(element, SlimBase.y1),
                    zeroIfNull(element, SlimBase.x2),
                    zeroIfNull(element, SlimBase.y2), null,
                    stringOrNull(element, SlimBase.transform),
                    stringOrNull(element, SlimBase.stroke),
                    oneIfNull(element, SlimBase.strokeOpacity),
                    zeroIfNull(element, SlimBase.strokeWidth)
            )
            RECT -> context.drawRect(
                    zeroIfNull(element, SlimBase.x),
                    zeroIfNull(element, SlimBase.y),
                    zeroIfNull(element, SlimBase.width),
                    zeroIfNull(element, SlimBase.height), null,
                    stringOrNull(element, SlimBase.transform),
                    stringOrNull(element, SlimBase.fill),
                    oneIfNull(element, SlimBase.fillOpacity),
                    stringOrNull(element, SlimBase.stroke),
                    oneIfNull(element, SlimBase.strokeOpacity),
                    zeroIfNull(element, SlimBase.strokeWidth)
            )
            PATH -> context.drawPath(
                    stringOrNull(element, SlimBase.pathData), null,
                    stringOrNull(element, SlimBase.transform),
                    stringOrNull(element, SlimBase.fill),
                    oneIfNull(element, SlimBase.fillOpacity),
                    stringOrNull(element, SlimBase.stroke),
                    oneIfNull(element, SlimBase.strokeOpacity),
                    zeroIfNull(element, SlimBase.strokeWidth)
            )
            else -> throw IllegalArgumentException("Unknown element with name: " + element.elementName)
        }
    }
}

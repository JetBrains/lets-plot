package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.vis.svg.*

class LegendComponent(legendSpec: LegendComponentSpec) : LegendBox(legendSpec) {

    override val spec: LegendComponentSpec
        get() = super.spec as LegendComponentSpec

    override fun appendGuideContent(contentRoot: SvgNode): DoubleVector {
        val l = spec.layout

        val keyLabelBoxes = l.keyLabelBoxes.iterator()
        val labelBoxes = l.labelBoxes.iterator()
        for (br in spec.breaks) {
            val keyLabelBox = keyLabelBoxes.next()
            val labelBox = labelBoxes.next()
            val breakElement = createBreakElement(br, l.keySize, keyLabelBox, labelBox)
            contentRoot.children().add(breakElement)
        }
        return l.size
    }

    private fun createBreakElement(
        br: LegendBreak,
        keySize: DoubleVector,
        keyLabelBox: DoubleRectangle,
        labelBox: DoubleRectangle
    ): SvgElement {
        val breakComponent = GroupComponent()

        // key element
        breakComponent.add(createKeyElement(br, keySize))

        // add label at position as was layout
        val label = TextLabel(br.label)
        label.setHorizontalAnchor(TextLabel.HorizontalAnchor.LEFT)
        label.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
        label.moveTo(labelBox.origin.add(DoubleVector(0.0, labelBox.height / 2)))
        breakComponent.add(label)

        breakComponent.moveTo(keyLabelBox.origin)
        return breakComponent.rootGroup
    }

    private fun createKeyElement(legendBreak: LegendBreak, size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        val innerSize = DoubleVector(size.x - 2, size.y - 2)

        val backgroundFill = spec.theme.backgroundFill()

        // common background
        val backgroundRect = SvgRectElement(1.0, 1.0, innerSize.x, innerSize.y)
        backgroundRect.strokeWidth().set(1.0)
        backgroundRect.strokeColor().set(backgroundFill)
        backgroundRect.fillColor().set(backgroundFill)

        g.children().add(backgroundRect)

        // key
        val keyElement = legendBreak.createKeyElement(innerSize)
        val keyElementTransform = buildTransform(DoubleVector(1.0, 1.0), 0.0)
        keyElement.transform().set(keyElementTransform)

        g.children().add(keyElement)

        // white frame
        val frame = SvgRectElement(0.0, 0.0, size.x, size.y)
        frame.strokeWidth().set(1.0)
        frame.strokeColor().set(backgroundFill)
        frame.fill().set(SvgColors.NONE)

        g.children().add(frame)
        return g
    }
}

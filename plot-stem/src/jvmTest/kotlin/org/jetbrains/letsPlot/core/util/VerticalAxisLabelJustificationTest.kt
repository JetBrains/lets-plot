package org.jetbrains.letsPlot.core.util

import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import kotlin.test.Test

/**
 * Regression tests for vertical-axis tick-label justification (see VerticalRotatedLabelsLayout).
 *
 * The point of the layout is that, for `hjust` exactly 0 / 0.5 / 1 on a non-rotated (angle 0) vertical axis,
 * the labels are aligned via the exact SVG `text-anchor` (so all labels share a single screen-x and the
 * alignment does not depend on the imprecise text-width estimate), mirroring the horizontal axis.
 */
class VerticalAxisLabelJustificationTest {

    private data class AxisText(val x: Double, val anchor: String, val text: String)

    private fun axisTexts(position: String, hjust: Double): List<AxisText> {
        val spec = """
            {
                'kind': 'plot',
                'theme': {
                   'name': 'classic',
                   'axis_title': { 'blank': true },
                   'axis_text_y': { 'angle': 0, 'hjust': $hjust, 'blank': false }
                },
                'ggsize': {'width': 360, 'height': 300},
                'mapping': { 'x': 'x', 'y': 'y' },
                'layers': [ { 'geom': 'point' } ],
                'scales': [ {'aesthetic': 'y', 'position': '$position'} ]
            }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec)).apply {
            this["data"] = mapOf(
                "x" to listOf(1, 2, 3, 4, 5),
                "y" to listOf("label 1", "label number 2", "long label", "4", "label 5"),
            )
        }
        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(plotSpec, null) { _ -> }

        // For a non-rotated vertical axis the only x-translation comes from the nested <g> transforms
        // (the per-tick group translates in y only), so the absolute screen-x of a label is the sum of
        // ancestor translate-x plus its own x attribute. text-anchor is omitted when it is the default "start".
        val result = mutableListOf<AxisText>()
        fun walk(node: XmlNode, parentTranslateX: Double) {
            if (node !is XmlNode.Element) return
            val translateX = parentTranslateX + (node.attributes["transform"]
                ?.let { Regex("translate\\(\\s*(-?[\\d.]+(?:[eE][-+]?\\d+)?)").find(it)?.groupValues?.get(1)?.toDoubleOrNull() } ?: 0.0)
            if (node.name == "text" && (node.attributes["class"] ?: "").contains("axis-text-y")) {
                val x = translateX + (node.attributes["x"]?.toDoubleOrNull() ?: 0.0)
                val anchor = node.attributes["text-anchor"] ?: "start"
                val text = node.children.filterIsInstance<XmlNode.Element>()
                    .flatMap { it.children }.filterIsInstance<XmlNode.Text>().joinToString("") { it.content }
                result.add(AxisText(x, anchor, text))
            }
            node.children.forEach { walk(it, translateX) }
        }
        walk(Xml.parse(svg).root, 0.0)
        return result
    }

    @Test
    fun `left axis hjust=0 left-aligns all labels via 'start' anchor`() {
        val texts = axisTexts("left", 0.0)
        assertThat(texts).hasSize(5)
        assertThat(texts.map { it.anchor }.distinct()).containsExactly("start")
        // All labels share a single screen-x (exact alignment, independent of estimated width).
        assertThat(texts.map { it.x }.distinct()).hasSize(1)
    }

    @Test
    fun `left axis hjust=1 right-aligns all labels via 'end' anchor`() {
        val texts = axisTexts("left", 1.0)
        assertThat(texts).hasSize(5)
        assertThat(texts.map { it.anchor }.distinct()).containsExactly("end")
        assertThat(texts.map { it.x }.distinct()).hasSize(1)
    }

    @Test
    fun `left axis hjust=0_5 centers all labels via 'middle' anchor`() {
        val texts = axisTexts("left", 0.5)
        assertThat(texts).hasSize(5)
        assertThat(texts.map { it.anchor }.distinct()).containsExactly("middle")
        assertThat(texts.map { it.x }.distinct()).hasSize(1)
    }

    @Test
    fun `right axis matches horizontal convention - hjust=0 is 'start', hjust=1 is 'end'`() {
        assertThat(axisTexts("right", 0.0).map { it.anchor }.distinct()).containsExactly("start")
        assertThat(axisTexts("right", 1.0).map { it.anchor }.distinct()).containsExactly("end")
    }

    // Number of y-axis tick labels that survive overlap-based break filtering.
    private fun keptRotatedYLabels(angle: Int, vjust: Double?, height: Int, labelCount: Int): Int {
        val vjustSpec = vjust?.let { "'vjust': $it," } ?: ""
        val spec = """
            {
                'kind': 'plot',
                'theme': {
                   'name': 'classic',
                   'axis_title': { 'blank': true },
                   'axis_text_y': { 'angle': $angle, $vjustSpec 'blank': false }
                },
                'ggsize': {'width': 360, 'height': $height},
                'mapping': { 'x': 'x', 'y': 'y' },
                'layers': [ { 'geom': 'point' } ]
            }
        """.trimIndent()
        val plotSpec = HashMap(parsePlotSpec(spec)).apply {
            this["data"] = mapOf(
                "x" to (1..labelCount).toList(),
                "y" to (1..labelCount).map { "category number $it" },
            )
        }
        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(plotSpec, null) { _ -> }
        var count = 0
        fun walk(node: XmlNode) {
            if (node !is XmlNode.Element) return
            if (node.name == "text" && (node.attributes["class"] ?: "").contains("axis-text-y")) count++
            node.children.forEach(::walk)
        }
        walk(Xml.parse(svg).root)
        return count
    }

    /**
     * Regression for overlap-based break filtering of rotated (±90°) vertical-axis labels
     * (see VerticalRotatedLabelsLayout: the layout reports each label's TRUE rendered box, so a
     * dense rotated axis drops overlapping labels instead of keeping visually-overlapping ones
     * or over-dropping). `vjust` slides labels along the axis but must not break filtering.
     */
    @Test
    fun `rotated 90deg y-axis filters overlapping labels for any vjust`() {
        val total = 20
        for (vjust in listOf(null, 0.0, 0.5, 1.0)) {
            val kept = keptRotatedYLabels(angle = 90, vjust = vjust, height = 300, labelCount = total)
            assertThat(kept)
                .describedAs("kept labels for vjust=%s", vjust)
                .isGreaterThan(0)
                .isLessThan(total)
        }
    }
}

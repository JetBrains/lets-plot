package org.jetbrains.letsPlot.core.util

import demoAndTestShared.parsePlotSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.encoding.UnsupportedRGBEncoder
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString
import kotlin.test.Test

class PlotSvgExportTest {
    val plotSpec = parsePlotSpec("""
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ]
            |      }
            |    }
            |  ]
            |}
        """.trimMargin())

    @Test
    fun `buildSvg(ggsize(222, 111))`() {
        val plotSpecWithGgSize = plotSpec.toMutableMap().apply {
            this["ggsize"] = mapOf(
                "width" to 222.0,
                "height" to 111.0
            )
        }

        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpecWithGgSize,
            plotSize = null,
            svgToString = SvgToString(rgbEncoder = UnsupportedRGBEncoder)
        ) { _ -> }

        val root = Xml.parse(svg) as XmlNode.Element
        assertThat(root.attributes["width"]).isEqualTo("222.0")
        assertThat(root.attributes["height"]).isEqualTo("111.0")
        assertThat(root.attributes).doesNotContainKey("viewBox")
    }

    @Test
    fun `buildSvg(ggsize(222, 111), w=400, h=200)`() {
        val plotSpecWithGgSize = plotSpec.toMutableMap().apply {
            this["ggsize"] = mapOf(
                "width" to 222.0,
                "height" to 111.0
            )
        }

        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpecWithGgSize,
            plotSize = DoubleVector(400, 200),
            svgToString = SvgToString(rgbEncoder = UnsupportedRGBEncoder)
        ) { _ -> }

        val root = Xml.parse(svg) as XmlNode.Element
        assertThat(root.attributes["width"]).isEqualTo("400.0")
        assertThat(root.attributes["height"]).isEqualTo("200.0")
        assertThat(root.attributes).doesNotContainKey("viewBox")
    }

    @Test
    fun `buildSvg()`() {
        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = null,
            svgToString = SvgToString(rgbEncoder = UnsupportedRGBEncoder)
        ) { _ -> }

        val root = Xml.parse(svg) as XmlNode.Element
        assertThat(root.attributes["width"]).isEqualTo("600.0")
        assertThat(root.attributes["height"]).isEqualTo("400.0")
        assertThat(root.attributes).doesNotContainKey("viewBox")
    }

    @Test
    fun `buildSvg(w=500, h=300)`() {
        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = DoubleVector(500, 300),
            svgToString = SvgToString(rgbEncoder = UnsupportedRGBEncoder)
        ) { _ -> }

        val root = Xml.parse(svg) as XmlNode.Element
        assertThat(root.attributes["width"]).isEqualTo("500.0")
        assertThat(root.attributes["height"]).isEqualTo("300.0")
        assertThat(root.attributes).doesNotContainKey("viewBox")
    }

    @Test
    fun `buildSvg(w=8, h=6, unit='in')`() {
        val svg = MonolithicCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = DoubleVector(8, 6),
            sizeUnit = SizeUnit.IN,
            svgToString = SvgToString(rgbEncoder = UnsupportedRGBEncoder)
        ) { _ -> }

        val root = Xml.parse(svg) as XmlNode.Element
        assertThat(root.attributes["width"]).isEqualTo("8.0in")
        assertThat(root.attributes["height"]).isEqualTo("6.0in")
        assertThat(root.attributes["viewBox"]).isEqualTo("0 0 768.0 576.0")
    }
}
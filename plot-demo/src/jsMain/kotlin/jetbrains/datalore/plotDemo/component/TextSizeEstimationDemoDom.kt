/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import kotlinx.browser.document
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.data.PlotTitles
import jetbrains.datalore.plotDemo.model.component.TextSizeEstimationDemo
import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import org.w3c.dom.get
import org.w3c.dom.svg.SVGTextContentElement
import kotlin.math.pow

/**
 * Called from generated HTML
 * Run with TextSizeEstimationDemoBrowser.kt
 */
fun textSizeEstimationDemo() {
    val rootNodeId = "root"
    val fontName = "Arial"
    val fontSize = 14
    val isBold = false
    val isItalic = false
    val lineSizes = PlotTitles.TITLES.map { DoubleVector(0.0, 0.0) }
    val svgRoot = TextSizeEstimationDemo.createSvgElement(
        DoubleVector(800.0, 600.0),
        0.75,
        PlotTitles.TITLES,
        "clustering",
        fontName,
        fontSize,
        isBold,
        isItalic,
        lineSizes,
        1.0,
        1.0,
        1.0,
        1.0,
        0.0
    ) ?: return
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), rootNodeId)

    val rootElement = document.getElementById(rootNodeId)
        ?: throw IllegalStateException("Root node '$rootNodeId' wasn't found")
    val svgElement = rootElement.getElementsByTagName("svg")[0]
        ?: throw IllegalStateException("SVG element wasn't found")
    val textElements = svgElement.getElementsByTagName("text")
    val textElementsCount = textElements.length
    val deltas = mutableListOf<Double>()
    for (i in 0 until textElementsCount / 2) {
        val textElement = textElements[2 * i] as SVGTextContentElement
        val actualWidth = textElement.getComputedTextLength()
        val estimationString = textElements[2 * i + 1] as SVGTextContentElement
        val estimatedWidth = (estimationString.textContent ?: "").replace("actual=0, estimated=", "").split(",")[0].toDoubleOrNull() ?: 0.0
        val delta = estimatedWidth - actualWidth
        deltas.add(delta)

        val div = document.createElement("div")
        div.innerHTML = "width(\"${textElement.textContent}\"): actual=$actualWidth, estimated=$estimatedWidth, &#8710;=$delta"
        rootElement.appendChild(div)
    }

    val meanDelta = deltas.sum() / deltas.size
    val stdDelta = (deltas.sumOf { (it - meanDelta).pow(2) } / deltas.size).pow(0.5)
    val div = document.createElement("div")
    div.innerHTML = "Mean &#8710; = $meanDelta, Std &#8710; = $stdDelta"
    rootElement.appendChild(div)
}
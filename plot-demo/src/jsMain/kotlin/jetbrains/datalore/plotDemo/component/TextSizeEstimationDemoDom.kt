/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import kotlinx.browser.document
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.component.TextSizeEstimationDemo
import jetbrains.datalore.vis.browser.DomMapperDemoUtil
import org.w3c.dom.get
import org.w3c.dom.svg.SVGTextContentElement

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
    val textLines = listOf(
        "Mean Mobility in North America",
        "Correlation between Mobility and CRI",
        "Combined Weekly Change of Mobility and CRI in North America",
        "Mean Temperature Along Period Under Review",
        "Mean Temperature by Month",
        "Relation Between Mean Temperature and Wind Speed",
        "One Day Lag Scatter Plot",
        "Annual Path of Mean Temperature and Humidity",
        "Autocorrelation Functions",
        "Installations by Category",
        "Connection Between Installations and Rating",
        "Popular Migration Directions for Nobel Laureates",
        "Prize Proportion between Top Countries and Others",
        "Distribution of Nobel Laureates in the World",
        "Gender Ratio",
        "Nobel Prizes by Categories",
        "Death Age Distribution of Nobel Laureates",
        "Common Distribution of Death Age and Getting the Prize Age",
        "Laureates Who Won Nobel Prize More Than Once",
    )
    val lineSizes = textLines.map { DoubleVector(0.0, 0.0) }
    val svgRoot = TextSizeEstimationDemo.createSvgElement(
        DoubleVector(800.0, 600.0),
        0.75,
        textLines,
        fontName,
        fontSize,
        isBold,
        isItalic,
        lineSizes,
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
    for (i in 0 until textElementsCount / 2) {
        val textElement = textElements[2 * i] as SVGTextContentElement
        val actualWidth = textElement.getComputedTextLength()
        val estimationString = textElements[2 * i + 1] as SVGTextContentElement
        val estimatedWidth = (estimationString.textContent ?: "").replace("actual=0, estimated=", "").split(",")[0].toDoubleOrNull() ?: 0.0
        val delta = estimatedWidth - actualWidth

        val div = document.createElement("div")
        div.innerHTML = "width(\"${textElement.textContent}\"): actual=$actualWidth, estimated=$estimatedWidth, &#8710;=$delta"
        rootElement.appendChild(div)
    }
}
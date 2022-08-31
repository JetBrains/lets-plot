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
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import org.w3c.dom.svg.SVGTextContentElement

/**
 * Called from generated HTML
 * Run with TextSizeEstimationDemoBrowser.kt
 */
fun textSizeEstimationDemo() {
    val defaultMultiplicativeCoefficient = 1.0
    val rootNodeId = "root"
    val rootElement = document.getElementById(rootNodeId)
        ?: throw IllegalStateException("Root node '$rootNodeId' wasn't found")

    val inputPanel = document.createElement("div")
    val multiplicativeCoefficientInput = document.createElement("input") as HTMLInputElement
    multiplicativeCoefficientInput.setAttribute("type", "text")
    multiplicativeCoefficientInput.value = defaultMultiplicativeCoefficient.toString()
    inputPanel.appendChild(multiplicativeCoefficientInput)
    rootElement.appendChild(inputPanel)

    val outputPanel = document.createElement("div")
    outputPanel.id = "root-output"
    rootElement.appendChild(outputPanel)
    rebuild(outputPanel, defaultMultiplicativeCoefficient)

    multiplicativeCoefficientInput.addEventListener("input", { event ->
        rebuild(outputPanel, (event.target as HTMLInputElement).value.toDouble())
    })
}

fun rebuild(rootElement: Element, multiplicativeCoefficient: Double) {
    fillSvg(rootElement, multiplicativeCoefficient, PlotTitles.TITLES.map { DoubleVector(0.0, 0.0) })

    val svgElement = rootElement.getElementsByTagName("svg")[0]
        ?: throw IllegalStateException("SVG element wasn't found")
    val textElements = svgElement.getElementsByTagName("text")
    val textElementsCount = textElements.length
    val lineSizes = mutableListOf<DoubleVector>()
    val deltas = mutableListOf<Double>()
    val qs = mutableListOf<Double>()
    for (i in 0 until textElementsCount / 2) {
        val textElement = textElements[2 * i] as SVGTextContentElement
        val actualWidth = textElement.getComputedTextLength()
        lineSizes.add(DoubleVector(actualWidth.toDouble(), 0.0))
        val estimationString = textElements[2 * i + 1] as SVGTextContentElement
        val estimatedWidth = (estimationString.textContent ?: "").replace("actual=0, estimated=", "").split(",")[0].toDoubleOrNull() ?: 0.0
        val delta = estimatedWidth - actualWidth
        val q = estimatedWidth / actualWidth
        deltas.add(delta)
        qs.add(q)

        val div = document.createElement("div")
        div.innerHTML = "width(\"${textElement.textContent}\"): actual=$actualWidth, estimated=$estimatedWidth, &#8710;=$delta, q=$q"
        rootElement.appendChild(div)
    }
    fillSvg(rootElement, multiplicativeCoefficient, lineSizes)
}

fun fillSvg(rootElement: Element, multiplicativeCoefficient: Double, lineSizes: List<DoubleVector>) {
    rootElement.innerHTML = ""

    val fontName = "Arial"
    val fontSize = 14
    val isBold = false
    val isItalic = false

    val svgRoot = TextSizeEstimationDemo.createSvgElement(
        DoubleVector(800.0, 600.0),
        0.742,
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
        multiplicativeCoefficient,
        0.0
    ) ?: return
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), rootElement.id)
}
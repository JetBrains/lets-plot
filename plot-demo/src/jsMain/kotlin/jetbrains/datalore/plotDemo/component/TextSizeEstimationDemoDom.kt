/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import kotlinx.browser.document
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.data.PlotTexts
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
    val defaultFontSize = 14
    val defaultIsBold = false
    val defaultIsItalic = false
    val defaultMultiplicativeCoefficient = 1.0
    val defaultAdditiveCoefficient = 0.0
    val rootNodeId = "root"
    val rootElement = document.getElementById(rootNodeId)
        ?: throw IllegalStateException("Root node '$rootNodeId' wasn't found")

    val inputPanel = document.createElement("div")
    val fontSizeInput = initInputElement(inputPanel, "text", "Font size: ", defaultFontSize.toString())
    val isBoldCheckbox = initInputElement(inputPanel, "checkbox", "Is bold: ", defaultIsBold)
    val isItalicCheckbox = initInputElement(inputPanel, "checkbox", "Is italic: ", defaultIsItalic)
    val multiplicativeCoefficientInput = initInputElement(inputPanel, "text", "Mult. coeff: ", defaultMultiplicativeCoefficient.toString())
    val additiveCoefficientInput = initInputElement(inputPanel, "text", "Add. coeff: ", defaultAdditiveCoefficient.toString())
    rootElement.appendChild(inputPanel)

    val outputPanel = document.createElement("div")
    outputPanel.id = "root-output"
    rootElement.appendChild(outputPanel)

    rebuild(outputPanel, defaultFontSize, defaultIsBold, defaultIsItalic, defaultMultiplicativeCoefficient, defaultAdditiveCoefficient)

    fontSizeInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            (event.target as HTMLInputElement).value.toInt(),
            isBoldCheckbox.checked,
            isItalicCheckbox.checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    isBoldCheckbox.addEventListener("click", { event ->
        rebuild(
            outputPanel,
            fontSizeInput.value.toInt(),
            (event.target as HTMLInputElement).checked,
            isItalicCheckbox.checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    isItalicCheckbox.addEventListener("click", { event ->
        rebuild(
            outputPanel,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked, (event.target as HTMLInputElement).checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    multiplicativeCoefficientInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked, isItalicCheckbox.checked,
            (event.target as HTMLInputElement).value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    additiveCoefficientInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked, isItalicCheckbox.checked,
            multiplicativeCoefficientInput.value.toDouble(),
            (event.target as HTMLInputElement).value.toDouble()
        )
    })
}

fun initInputElement(inputPanel: Element, type: String, label: String, defaultValue: Any): HTMLInputElement {
    val inputContainer = document.createElement("div")
    val inputLabel = document.createElement("span")
    inputLabel.textContent = label
    inputContainer.appendChild(inputLabel)
    val inputElement = document.createElement("input") as HTMLInputElement
    inputElement.setAttribute("type", type)
    when (type) {
        "text" -> inputElement.value = defaultValue as String
        "checkbox" -> inputElement.checked = defaultValue as Boolean
        else -> throw Exception("Input type $type is not supported")
    }
    inputContainer.appendChild(inputElement)
    inputPanel.appendChild(inputContainer)
    return inputElement
}

fun rebuild(
    rootElement: Element,
    fontSize: Int,
    isBold: Boolean,
    isItalic: Boolean,
    multiplicativeCoefficient: Double,
    additiveCoefficient: Double
) {
    fillSvg(rootElement, PlotTexts.TEST.map { DoubleVector(0.0, 0.0) }, fontSize, isBold, isItalic, multiplicativeCoefficient, additiveCoefficient)

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
    fillSvg(rootElement, lineSizes, fontSize, isBold, isItalic, multiplicativeCoefficient, additiveCoefficient)
}

fun fillSvg(
    rootElement: Element,
    lineSizes: List<DoubleVector>,
    fontSize: Int,
    isBold: Boolean,
    isItalic: Boolean,
    multiplicativeCoefficient: Double,
    additiveCoefficient: Double,
) {
    rootElement.innerHTML = ""
    val fontName = "Arial"
    val svgRoot = TextSizeEstimationDemo.createSvgElement(
        DoubleVector(800.0, 600.0),
        0.742,
        PlotTexts.TEST,
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
        additiveCoefficient
    ) ?: return
    DomMapperDemoUtil.mapToDom(listOf(svgRoot), rootElement.id)
}
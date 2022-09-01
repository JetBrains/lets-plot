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
import org.w3c.dom.*
import org.w3c.dom.svg.SVGTextContentElement

/**
 * Called from generated HTML
 * Run with TextSizeEstimationDemoBrowser.kt
 */
fun textSizeEstimationDemo() {
    val availableFontFamilies = listOf("Lucida Grande", "Arial", "Calibri", "Garamond", "Geneva",
                                       "Georgia", "Helvetica", "Rockwell", "Times New Roman", "Verdana")
        .filter { checkFont(it) }

    val defaultModel = "clustering2"
    val defaultFontFamily = availableFontFamilies.first()
    val defaultFontSize = 14
    val defaultIsBold = false
    val defaultIsItalic = false
    val defaultMultiplicativeCoefficient = 1.0
    val defaultAdditiveCoefficient = 0.0
    val rootNodeId = "root"
    val rootElement = document.getElementById(rootNodeId)
        ?: throw IllegalStateException("Root node '$rootNodeId' wasn't found")

    val inputPanel = document.createElement("div")
    val modelSelect = initSelectElement(inputPanel, mapOf(
        "original" to "Original",
        "clustering1" to "Clustering #1",
        "clustering2" to "Clustering #2"
    ), "Model: ", defaultModel)
    val fontSelect = initSelectElement(inputPanel, availableFontFamilies.associateWith { it }, "Font family: ", defaultFontFamily)
    val fontSizeInput = initInputElement(inputPanel, "text", "Font size: ", defaultFontSize.toString())
    val isBoldCheckbox = initInputElement(inputPanel, "checkbox", "Is bold: ", defaultIsBold)
    val isItalicCheckbox = initInputElement(inputPanel, "checkbox", "Is italic: ", defaultIsItalic)
    val multiplicativeCoefficientInput = initInputElement(inputPanel, "text", "Mult. coeff: ", defaultMultiplicativeCoefficient.toString())
    val additiveCoefficientInput = initInputElement(inputPanel, "text", "Add. coeff: ", defaultAdditiveCoefficient.toString())
    rootElement.appendChild(inputPanel)

    val outputPanel = document.createElement("div")
    outputPanel.id = "root-output"
    rootElement.appendChild(outputPanel)

    rebuild(outputPanel, defaultModel, defaultFontFamily, defaultFontSize, defaultIsBold, defaultIsItalic, defaultMultiplicativeCoefficient, defaultAdditiveCoefficient)

    modelSelect.addEventListener("change", { event ->
        rebuild(
            outputPanel,
            (event.target as HTMLSelectElement).value,
            fontSelect.value,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked,
            isItalicCheckbox.checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    fontSelect.addEventListener("change", { event ->
        rebuild(
            outputPanel,
            modelSelect.value,
            (event.target as HTMLSelectElement).value,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked,
            isItalicCheckbox.checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    fontSizeInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            modelSelect.value,
            fontSelect.value,
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
            modelSelect.value,
            fontSelect.value,
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
            modelSelect.value,
            fontSelect.value,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked, (event.target as HTMLInputElement).checked,
            multiplicativeCoefficientInput.value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    multiplicativeCoefficientInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            modelSelect.value,
            fontSelect.value,
            fontSizeInput.value.toInt(),
            isBoldCheckbox.checked, isItalicCheckbox.checked,
            (event.target as HTMLInputElement).value.toDouble(),
            additiveCoefficientInput.value.toDouble()
        )
    })
    additiveCoefficientInput.addEventListener("input", { event ->
        rebuild(
            outputPanel,
            modelSelect.value,
            fontSelect.value,
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

fun initSelectElement(inputPanel: Element, options: Map<String, String>, label: String, defaultValue: String): HTMLSelectElement {
    val selectContainer = document.createElement("div")
    val selectLabel = document.createElement("span")
    selectLabel.textContent = label
    selectContainer.appendChild(selectLabel)
    val selectElement = document.createElement("select") as HTMLSelectElement
    for ((value, text) in options) {
        val optionElement = document.createElement("option") as HTMLOptionElement
        optionElement.value = value
        optionElement.textContent = text
        optionElement.selected = value == defaultValue
        selectElement.appendChild(optionElement)
    }
    selectContainer.appendChild(selectElement)
    inputPanel.appendChild(selectContainer)
    return selectElement
}

fun rebuild(
    rootElement: Element,
    model: String,
    fontName: String,
    fontSize: Int,
    isBold: Boolean,
    isItalic: Boolean,
    multiplicativeCoefficient: Double,
    additiveCoefficient: Double
) {
    fillSvg(rootElement, PlotTexts.TEST.map { DoubleVector(0.0, 0.0) }, model, fontName, fontSize, isBold, isItalic, multiplicativeCoefficient, additiveCoefficient)

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
    fillSvg(rootElement, lineSizes, model, fontName, fontSize, isBold, isItalic, multiplicativeCoefficient, additiveCoefficient)
}

fun fillSvg(
    rootElement: Element,
    lineSizes: List<DoubleVector>,
    model: String,
    fontName: String,
    fontSize: Int,
    isBold: Boolean,
    isItalic: Boolean,
    multiplicativeCoefficient: Double,
    additiveCoefficient: Double,
) {
    rootElement.innerHTML = ""
    val svgRoot = TextSizeEstimationDemo.createSvgElement(
        DoubleVector(800.0, 600.0),
        0.742,
        PlotTexts.TEST,
        model,
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

// Source: https://gist.github.com/fijiwebdesign/3b0bf8e88ceef7518844
fun checkFont(fontName: String): Boolean {
    // A font will be compared against all the three default fonts.
    // And if it doesn't match all 3 then that font is not available.
    val baseFonts = listOf("monospace", "sans-serif", "serif")

    // We use m or w because these two characters take up the maximum width.
    // And we use a LLi so that the same matching fonts can get separated.
    val testString = "mmmmmmmmmmlli"

    // We test using 72px font size, we may use any size. I guess larger the better.
    val testSize = "72px"

    val h = document.getElementsByTagName("body")[0]!!

    // Create a SPAN in the document to get the width of the text we use to test.
    val s = document.createElement("span") as HTMLSpanElement
    s.style.fontSize = testSize
    s.innerHTML = testString
    val defaultWidth = mutableMapOf<String, Int>()
    val defaultHeight = mutableMapOf<String, Int>()
    for (baseFont in baseFonts) {
        // Get the default width for the three base fonts.
        s.style.fontFamily = baseFont
        h.appendChild(s)
        defaultWidth[baseFont] = s.offsetWidth // Width for the default font.
        defaultHeight[baseFont] = s.offsetHeight // Height for the defualt font.
        h.removeChild(s)
    }

    fun check(font: String): Boolean {
        var detected = false
        for (baseFont in baseFonts) {
            s.style.fontFamily = "\"$font\",$baseFont" // Name of the font along with the base font for fallback.
            h.appendChild(s)
            val matched = (s.offsetWidth != defaultWidth[baseFont] || s.offsetHeight != defaultHeight[baseFont])
            h.removeChild(s)
            detected = detected || matched
        }
        return detected
    }

    return check(fontName)
}
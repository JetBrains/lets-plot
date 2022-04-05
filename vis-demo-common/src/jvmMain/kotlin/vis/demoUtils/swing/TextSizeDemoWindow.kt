/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.CharCategory
import jetbrains.datalore.plot.builder.presentation.getOptionsForFont
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.font.FontRenderContext
import javax.swing.*

data class TextSettings(
    val lines: List<String>,
    val fontName: String,
    val fontSize: Int,
    val isBold: Boolean,
    val isItalic: Boolean,
    val fontWidthRatio: Double,
    val categoryRatio: Double?,
    val boldRatio: Double,
    val lineBounds: List<DoubleVector>
)

object TextSizeDemoCharCategories {

    private val extendedCharLists = mapOf(
        "All printable" to (32..126).map(Int::toChar),
        "Letters" to ('a'..'z') + ('A'..'Z'),
        "Digits" to ('0'..'9'),
        "Symbols" to (32..126).map(Int::toChar) -
                (('0'..'9') + ('a'..'z') + ('A'..'Z')),
        "Extended chars" to (128..255).map(Int::toChar)
    )

    fun getCategoryNamesForDemo(): List<String> {
        return CharCategory.values().map(CharCategory::name) + extendedCharLists.keys
    }

    fun getCharsForCategory(catName: String?, font: String): List<Char> {
        val category = CharCategory.values().find { it.name == catName }
        return when {
            category != null -> CharCategory.getCharListByCategory(category, font)
            extendedCharLists.containsKey(catName) -> extendedCharLists[catName]!!.toList()
            else -> emptyList()
        }
    }

    fun getDefaultRatioForCategory(catName: String?): Double? {
        return CharCategory.values().find { it.name == catName }?.value
    }

    fun getFontRatio(font: String) = getOptionsForFont(font).fontRatio
}


class TextSizeDemoWindow(
    title: String,
    windowSize: Dimension,
    private val svgComponentFactory: (Dimension, TextSettings) -> JComponent?,
    categoryNames: List<String>,
    private val categoryToChars: (String, String) -> List<Char>,
    private val fontToDefaultRatio: (String) -> Double,
    private val categoryToDefaultRatio: (String?) -> Double?
) : JFrame(title) {

    private val myTextArea = JTextArea(80, 1)
    private val myScrollPane = JScrollPane(
        myTextArea,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    )

    private val myCharCategories = JComboBox(categoryNames.toTypedArray())
    private val myCategoryRatio = JSpinner(
        SpinnerNumberModel(getDefaultRatioForSelectedCategory(), 0.1, 2.0, 0.01)
    )
    private val myCharCount = JSpinner()
    private val myFontList = JComboBox(
        arrayOf(
            "Lucida Grande", "Helvetica", "Verdana", "Geneva", "Times New Roman", "Georgia", "Courier"
        )
    )
    private val myFontWidthRatio = JSpinner(
        SpinnerNumberModel(fontToDefaultRatio(getSelectedFontName()), 0.1, 2.0, 0.01)
    )

    private val myFontSize = JSpinner()
    private val myIsBold = JCheckBox("bold")
    private val myBoldRatio = JSpinner(
        SpinnerNumberModel(1.2, 0.01, 2.0, 0.01)
    )
    private val myIsItalic = JCheckBox("italic")

    private val myInputPanel = JPanel()
    private val myOutputPanel = JPanel()
    private val mySplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myInputPanel, myOutputPanel)

    private fun getStringBounds(
        text: String,
        fontName: String,
        fontSize: Int,
        isBold: Boolean,
        isItalic: Boolean
    ): DoubleVector {
        val fontStyle = when {
            isBold && isItalic -> Font.BOLD + Font.ITALIC
            isBold -> Font.BOLD
            isItalic -> Font.ITALIC
            else -> Font.PLAIN
        }
        val font = Font(fontName, fontStyle, fontSize)

        // val metrics: FontMetrics = object : FontMetrics(font) {}
        // val bounds =  metrics.getStringBounds(text, null)

        val frc = FontRenderContext(font.transform, true, true)
        val bounds = font.getStringBounds(text, frc)
        return DoubleVector(bounds.width, bounds.height)
    }

    private fun rebuild() {
        val lines = myTextArea.text.split("\n").filter(String::isNotEmpty)
        val fontName = getSelectedFontName()
        val fontSize = myFontSize.value.toString().toInt()
        val lineBounds = lines.map {
            getStringBounds(it, fontName, fontSize, myIsBold.isSelected, myIsItalic.isSelected)
        }
        val plotComponent = svgComponentFactory(
            Dimension(size.width - myTextArea.width - 50, size.height - 50),
            TextSettings(
                lines = lines,
                fontName = fontName,
                fontSize = fontSize,
                isBold = myIsBold.isSelected,
                isItalic = myIsItalic.isSelected,
                fontWidthRatio = myFontWidthRatio.value.toString().toDouble(),
                categoryRatio = if (myCategoryRatio.value.toString() == "-1") {
                    null
                } else {
                    myCategoryRatio.value.toString().toDouble()
                },
                boldRatio = myBoldRatio.value.toString().toDouble(),
                lineBounds = lineBounds
            )
        )
        mySplitPane.rightComponent = plotComponent
    }

    init {
        myCharCount.addChangeListener { textAreaUpdate() }
        myFontSize.addChangeListener { rebuild() }
        myCharCategories.addActionListener { textAreaUpdate() }
        myCategoryRatio.addChangeListener { rebuild() }
        myFontWidthRatio.addChangeListener { rebuild() }
        myFontList.addActionListener { fontChanged() }
        myIsBold.addChangeListener { rebuild() }
        myIsItalic.addChangeListener { rebuild() }
        myTextArea.addCaretListener { rebuild() }
        myBoldRatio.addChangeListener { rebuild() }

        contentPane.add(mySplitPane, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        size = windowSize
        setLocationRelativeTo(null)

        mySplitPane.dividerSize = 0

        // input panel

        myInputPanel.layout = BoxLayout(myInputPanel, BoxLayout.Y_AXIS)

        val catGridPanel = JPanel()
        catGridPanel.layout = GridLayout(0, 2)
        catGridPanel.add(myCharCategories)
        catGridPanel.add(myCategoryRatio)
        myInputPanel.add(catGridPanel)

        myTextArea.lineWrap = true
        myInputPanel.add(myScrollPane)

        val grid = JPanel()
        grid.layout = GridLayout(0, 2)

        grid.add(JLabel("Char count to generate:"))
        myCharCount.value = 15
        grid.add(myCharCount)

        grid.add(JLabel("Font:"))
        myFontList.isEditable = true;
        grid.add(myFontList)

        grid.add(JLabel("Font ratio:"))
        grid.add(myFontWidthRatio)

        grid.add(JLabel("Font size:"))
        myFontSize.value = 19
        grid.add(myFontSize)

        grid.add(myIsBold)
        grid.add(myBoldRatio)

        grid.add(myIsItalic)

        myInputPanel.add(grid)
    }

    private fun getSelectedFontName(): String {
        return myFontList.selectedItem?.toString().let {
            if (it.isNullOrEmpty()) {
                myFontList.selectedIndex = 0
                myFontList.getItemAt(0).toString()
            } else {
                it
            }
        }
    }
    private fun getDefaultRatioForSelectedCategory(): Double? {
        return categoryToDefaultRatio(myCharCategories.selectedItem?.toString())
    }

    private fun textAreaUpdate() {
        myTextArea.text = ""

        val categoryRatio = getDefaultRatioForSelectedCategory()
        if (categoryRatio == null) {
            myCategoryRatio.isEnabled = false
            myCategoryRatio.value = -1
        } else  {
            myCategoryRatio.isEnabled = true
            myCategoryRatio.value = categoryRatio
        }

        val n = myCharCount.value.toString().toInt()
        val lines = categoryToChars(
            myCharCategories.selectedItem?.toString() ?: "",
            myFontList.selectedItem?.toString() ?: ""
        ).map { ch ->
            List(n) { ch }.joinToString("") + "\n"
        }
        myTextArea.append(lines.joinToString(""))
    }

    private fun fontChanged() {
        myFontWidthRatio.value = fontToDefaultRatio(getSelectedFontName())
        textAreaUpdate()
    }

    fun run() {
        isVisible = true
    }
}
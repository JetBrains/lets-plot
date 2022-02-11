/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

data class TextSettings(
   val lines: List<String>,
   val fontName: String,
   val fontSize: Int,
   val isBold: Boolean,
   val isItalic: Boolean,
   val isMonospaced: Boolean,
   val fontWidthRatio: Double
)

class TextSizeDemoWindow(
    title: String,
    windowSize: Dimension,
    private val svgComponentFactory: (Dimension, TextSettings) -> JComponent?,
    categoryNames: List<String>,
    private val categoryToChars: (String) -> List<Char>
) : JFrame(title) {

    private val myTextArea = JTextArea()

    private val myCharCategories = JComboBox(categoryNames.toTypedArray())

    private val myCharCount = JSpinner()
    private val myFontWidthRatio = JSpinner(SpinnerNumberModel(0.67, 0.1, 2.0, 0.01))
    private val myFontList = JComboBox(
        arrayOf(
            "Lucida Grande", "Helvetica", "Arial", "Verdana", "Geneva",
            "Times", "Times New Roman", "Georgia",
            "Courier", "Courier New"
        )
    )

    private val myFontSize = JSpinner()
    private val myIsBold = JCheckBox("bold")
    private val myIsItalic = JCheckBox("italic")

    private val myInputPanel = JPanel()
    private val myOutputPanel = JPanel()
    private val mySplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myInputPanel, myOutputPanel)

    private fun rebuild() {
        val font = myFontList.selectedItem?.toString().let {
            if (it.isNullOrEmpty()) {
                myFontList.selectedIndex = 0
                myFontList.getItemAt(0).toString()
            } else {
                it
            }
        }
        val plotComponent = svgComponentFactory(
            Dimension(size.width / 2 , size.height),
            TextSettings(
                lines = myTextArea.text.split("\n").filter(String::isNotEmpty),
                fontName = "\"$font\"",
                fontSize = myFontSize.value.toString().toInt(),
                isBold = myIsBold.isSelected,
                isItalic = myIsItalic.isSelected,
                isMonospaced = font in listOf("Courier New", "Courier", "monospace"), // todo
                fontWidthRatio = myFontWidthRatio.value.toString().toDouble()
            )
        )
        mySplitPane.rightComponent = plotComponent
    }

    init {
        myCharCount.addChangeListener { categoryChanged() }
        myFontSize.addChangeListener { rebuild() }
        myCharCategories.addActionListener { categoryChanged() }
        myFontWidthRatio.addChangeListener { rebuild() }
        myFontList.addActionListener { rebuild() }
        myIsBold.addChangeListener { rebuild() }
        myIsItalic.addChangeListener { rebuild() }
        myTextArea.addCaretListener { rebuild() }

        contentPane.add(mySplitPane, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        size = windowSize
        setLocationRelativeTo(null)

        mySplitPane.dividerLocation = size.width / 3
        mySplitPane.dividerSize = 0

        // input panel

        myInputPanel.layout = BoxLayout(myInputPanel, BoxLayout.Y_AXIS)

        myInputPanel.add(myCharCategories)

        myTextArea.lineWrap = true
        myInputPanel.add(myTextArea)

        val grid = JPanel()
        val gridLayout = GridLayout(5, 2)
        grid.layout = gridLayout

        grid.add(JLabel("Char count to generate:"))
        myCharCount.value = 15
        grid.add(myCharCount)

        grid.add(JLabel("Ratio:"))
        grid.add(myFontWidthRatio)

        grid.add(JLabel("Font:"))
        myFontList.isEditable = true;
        grid.add(myFontList)

        grid.add(JLabel("Font size:"))
        myFontSize.value = 19
        grid.add(myFontSize)

        grid.add(myIsBold)
        grid.add(myIsItalic)

        myInputPanel.add(grid)
    }

    private fun categoryChanged() {
        myTextArea.text = ""
        val n = myCharCount.value.toString().toInt()
        categoryToChars(
            myCharCategories.selectedItem?.toString() ?: ""
        ).forEach { ch -> myTextArea.append(List(n) { ch }.joinToString("") + "\n") }
        rebuild()
    }

    fun run() {
        isVisible = true
        rebuild()
    }
}
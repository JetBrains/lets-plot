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
    val fontWidthRatio: Double
)

class TextSizeDemoWindow(
    title: String,
    windowSize: Dimension,
    private val svgComponentFactory: (Dimension, TextSettings) -> JComponent?,
    categoryNames: List<String>,
    private val categoryToChars: (String, String) -> List<Char>,
    private val defaultFontRatio: (String) -> Double,
) : JFrame(title) {

    private val myTextArea = JTextArea(60, 1)
    private val myScrollPane = JScrollPane(
        myTextArea,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    )

    private val myCharCategories = JComboBox(categoryNames.toTypedArray())

    private val myCharCount = JSpinner()
    private val myFontList = JComboBox(
        arrayOf(
            "Lucida Grande", "Helvetica", "Verdana", "Geneva", "Times New Roman", "Georgia", "Courier"
        )
    )
    private val myFontWidthRatio = JSpinner(
        SpinnerNumberModel(defaultFontRatio(getSelectedFontName()), 0.1, 2.0, 0.01)
    )

    private val myFontSize = JSpinner()
    private val myIsBold = JCheckBox("bold")
    private val myIsItalic = JCheckBox("italic")

    private val myInputPanel = JPanel()
    private val myOutputPanel = JPanel()
    private val mySplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myInputPanel, myOutputPanel)

    private fun rebuild() {
        val plotComponent = svgComponentFactory(
            Dimension(size.width - myTextArea.width - 50, size.height - 50),
            TextSettings(
                lines = myTextArea.text.split("\n").filter(String::isNotEmpty),
                fontName = getSelectedFontName(),
                fontSize = myFontSize.value.toString().toInt(),
                isBold = myIsBold.isSelected,
                isItalic = myIsItalic.isSelected,
                fontWidthRatio = myFontWidthRatio.value.toString().toDouble()
            )
        )
        mySplitPane.rightComponent = plotComponent
    }

    init {
        myCharCount.addChangeListener { textAreaUpdate() }
        myFontSize.addChangeListener { rebuild() }
        myCharCategories.addActionListener { textAreaUpdate() }
        myFontWidthRatio.addChangeListener { rebuild() }
        myFontList.addActionListener { fontChanged() }
        myIsBold.addChangeListener { rebuild() }
        myIsItalic.addChangeListener { rebuild() }
        myTextArea.addCaretListener { rebuild() }

        contentPane.add(mySplitPane, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        size = windowSize
        setLocationRelativeTo(null)

        mySplitPane.dividerSize = 0

        // input panel

        myInputPanel.layout = BoxLayout(myInputPanel, BoxLayout.Y_AXIS)
        myInputPanel.add(myCharCategories)

        myTextArea.lineWrap = true
        myInputPanel.add(myScrollPane)

        val grid = JPanel()
        val gridLayout = GridLayout(5, 2)
        grid.layout = gridLayout

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

    private fun textAreaUpdate() {
        myTextArea.text = ""
        val n = myCharCount.value.toString().toInt()
        categoryToChars(
            myCharCategories.selectedItem?.toString() ?: "",
            myFontList.selectedItem?.toString() ?: ""
        ).forEach { ch ->
            myTextArea.append(List(n) { ch }.joinToString("") + "\n")
        }
        rebuild()
    }

    private fun fontChanged() {
        myFontWidthRatio.value = defaultFontRatio(getSelectedFontName())
        textAreaUpdate()
    }

    fun run() {
        isVisible = true
    }
}
/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils.swing

import jetbrains.datalore.base.geometry.DoubleVector
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.GridLayout
import java.awt.font.FontRenderContext
import javax.swing.*

data class TextSettings(
    val textLines: List<String>,
    val model: String,
    val fontName: String,
    val fontSize: Int,
    val isBold: Boolean,
    val isItalic: Boolean,
    val actualTextDimensions: List<DoubleVector>,
    val sizeRatio: Double,
    val boldRatio: Double,
    val italicRatio: Double,
    val multiplicativeCoefficient: Double,
    val additiveCoefficient: Double
)

class TextSizeDemoWindow(
    title: String,
    windowSize: Dimension,
    defaultText: String,
    private val svgComponentFactory: (Dimension, TextSettings) -> JComponent?,
) : JFrame(title) {

    private val myTextArea = JTextArea(80, 1)
    private val myScrollPane = JScrollPane(
        myTextArea,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    )

    private val myModelComboBox = JComboBox(arrayOf("original", "clustering1", "clustering2"))

    private val myFontComboBox = JComboBox(
       // arrayOf("Lucida Grande", "Helvetica", "Verdana", "Geneva", "Times New Roman", "Georgia", "Courier")
        GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
    )

    private val myFontSize = JSpinner()
    private val mySizeRatio = JSpinner(
        SpinnerNumberModel(1.0, 0.01, 1.5, 0.01)
    )

    private val myIsBold = JCheckBox("bold")
    private val myBoldRatio = JSpinner(
        SpinnerNumberModel(1.075, 0.1, 2.0, 0.01)
    )

    private val myIsItalic = JCheckBox("italic")
    private val myItalicRatio = JSpinner(
        SpinnerNumberModel(1.0, 0.1, 2.0, 0.01)
    )

    private val myMultiplicativeCoefficient = JSpinner(
        SpinnerNumberModel(1.0, 0.01, 2.0, 0.01)
    )

    private val myAdditiveCoefficient = JSpinner(
        SpinnerNumberModel(0.0, -2.0, 2.0, 0.01)
    )

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
        val textLines = myTextArea.text.split("\n").filter(String::isNotEmpty)
        val fontName = getSelectedFontName()
        val fontSize = myFontSize.value.toString().toInt()
        val lineSizes = textLines.map {
            getStringBounds(it, fontName, fontSize, myIsBold.isSelected, myIsItalic.isSelected)
        }
        val plotComponent = svgComponentFactory(
            Dimension(size.width - myTextArea.width - 50, size.height - 50),
            TextSettings(
                textLines = textLines,
                model = myModelComboBox.selectedItem?.toString() ?: "",
                fontName = fontName,
                fontSize = fontSize,
                isBold = myIsBold.isSelected,
                isItalic = myIsItalic.isSelected,
                actualTextDimensions = lineSizes,
                sizeRatio = mySizeRatio.value.toString().toDouble(),
                boldRatio = myBoldRatio.value.toString().toDouble(),
                italicRatio = myItalicRatio.value.toString().toDouble(),
                multiplicativeCoefficient = myMultiplicativeCoefficient.value.toString().toDouble(),
                additiveCoefficient = myAdditiveCoefficient.value.toString().toDouble(),
            )
        )
        mySplitPane.rightComponent = plotComponent
    }

    init {
        myTextArea.text = defaultText
        myFontSize.addChangeListener { rebuild() }
        myModelComboBox.addActionListener { rebuild() }
        myFontComboBox.addActionListener { rebuild() }
        myIsBold.addChangeListener {
            myBoldRatio.isEnabled = myIsBold.isSelected
            rebuild()
        }
        myIsItalic.addChangeListener {
            myItalicRatio.isEnabled = myIsItalic.isSelected
            rebuild()
        }
        myTextArea.addCaretListener { rebuild() }
        mySizeRatio.addChangeListener { rebuild() }
        myBoldRatio.addChangeListener { rebuild() }
        myItalicRatio.addChangeListener { rebuild() }
        myMultiplicativeCoefficient.addChangeListener { rebuild() }
        myAdditiveCoefficient.addChangeListener { rebuild() }

        contentPane.add(mySplitPane, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
        size = windowSize
        setLocationRelativeTo(null)

        mySplitPane.dividerSize = 0

        // input panel

        myInputPanel.layout = BoxLayout(myInputPanel, BoxLayout.Y_AXIS)

        myTextArea.lineWrap = true
        myInputPanel.add(myScrollPane)

        val grid = JPanel()
        grid.layout = GridLayout(0, 2)
        grid.preferredSize = Dimension(50,50)
        grid.minimumSize = Dimension(400, 300)

        grid.add(JLabel("Model:"))
        myModelComboBox.isEditable = false
        myModelComboBox.selectedItem = "clustering2"
        grid.add(myModelComboBox)

        grid.add(JLabel("Font:"))
        myFontComboBox.isEditable = false
        myFontComboBox.selectedItem = "Arial"
        grid.add(myFontComboBox)

        grid.add(JLabel("Font size:"))
        myFontSize.value = 14
        grid.add(myFontSize)

        grid.add(JLabel("Font size ratio:"))
        grid.add(mySizeRatio)

        grid.add(JLabel("Font multiplicative coefficient:"))
        grid.add(myMultiplicativeCoefficient)

        grid.add(JLabel("Font additive coefficient:"))
        grid.add(myAdditiveCoefficient)

        grid.add(myIsBold)
        myBoldRatio.isEnabled = myIsBold.isSelected
        grid.add(myBoldRatio)

        grid.add(myIsItalic)
        myItalicRatio.isEnabled = myIsItalic.isSelected
        grid.add(myItalicRatio)

        myInputPanel.add(grid)
    }

    private fun getSelectedFontName(): String {
        return myFontComboBox.selectedItem?.toString().let {
            if (it.isNullOrEmpty()) {
                myFontComboBox.selectedIndex = 0
                myFontComboBox.getItemAt(0).toString()
            } else {
                it
            }
        }
    }

    fun run() {
        isVisible = true
    }
}
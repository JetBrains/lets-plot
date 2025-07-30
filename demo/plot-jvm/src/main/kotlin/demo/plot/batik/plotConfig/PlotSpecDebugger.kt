import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.VegaDataUtil
import org.jetbrains.letsPlot.core.spec.write
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.*
import javax.swing.undo.UndoManager

private class PasteIcon(private val size: Int = 20) : Icon {
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // A dark, solid color for high contrast
        val boardColor = Color(80, 80, 80)

        // Draw the clipboard backplate
        g2d.color = boardColor
        g2d.fillRoundRect(x + 2, y, size - 4, size, 6, 6)

        // Draw the clip on top
        g2d.fillRect(x + 6, y - 1, size - 12, 5)

        // Draw the "paper" with a very light color
        g2d.color = Color(245, 245, 245)
        g2d.fillRect(x + 4, y + 4, size - 8, size - 7)

        // Draw text lines
        g2d.stroke = BasicStroke(1.5f)
        g2d.color = boardColor
        g2d.drawLine(x + 5, y + 8, x + 14, y + 8)
        g2d.drawLine(x + 5, y + 11, x + 14, y + 11)
        g2d.drawLine(x + 5, y + 14, x + 14, y + 14)

        g2d.dispose()
    }

    override fun getIconWidth(): Int = size
    override fun getIconHeight(): Int = size
}

private fun loadSpecFromFile(): String? {
    val specFile = getSpecFile()
    return try {
        if (specFile.isFile && specFile.canRead()) {
            val content = specFile.readText()
            if (content.isNotBlank()) content else null
        } else {
            null
        }
    } catch (e: IOException) {
        println("Warning: Could not read previous plot spec from ${specFile.absolutePath}")
        e.printStackTrace()
        null
    }
}

private fun getSpecFile(): File {
    val homeDir = System.getProperty("user.home")
    val appDir = File(homeDir, ".lets-plot-debugger")
    return File(appDir, "last_spec.json")
}

fun main() {
    val specString = System.getenv("PLOT_SPEC")
        ?: loadSpecFromFile()
        ?: """ 
        {
            'kind': 'plot',
            'data': { 'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner'] },
            'mapping': { 'x': 'time', 'color': 'time', 'fill': 'time' },
            'layers': [ { 'geom': 'bar', 'alpha': '0.5' } ]
        }
        """.trimIndent()

    SwingUtilities.invokeLater {

        val plotSpecDebugger = PlotSpecDebugger()
        try {
            val spec = parsePlotSpec(specString)
            val specToSet = if (spec.containsKey("kind")) {
                JsonSupport.formatJson(spec, pretty = true)
            } else {
                specString
            }

            plotSpecDebugger.setSpec(specToSet)
            plotSpecDebugger.evaluate()
        } catch (e: Exception) {
            plotSpecDebugger.setSpec(specString)
            e.printStackTrace()
        }

        plotSpecDebugger.isVisible = true
    }
}


class PlotSpecDebugger : JFrame("PlotSpec Debugger") {
    private val plotSpecTextArea = JTextArea().apply {
        wrapStyleWord = true
        lineWrap = true
        autoscrolls = true
    }
    private val specEditorPane = JScrollPane(plotSpecTextArea)

    private val evaluateButton = JButton("Evaluate (Shift+Enter)").apply {
        addActionListener { evaluate() }
    }

    private val pasteAndEvaluateButton = JButton().apply {
        icon = PasteIcon()
        toolTipText = "Paste & Run"
        addActionListener {
            if (pasteFromClipboard()) {
                evaluate()
            }
        }
    }

    private val plotPanel = JPanel(BorderLayout()).apply {
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }

    private val undoManager = UndoManager()
    private lateinit var undoAction: AbstractAction
    private lateinit var redoAction: AbstractAction

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(1400, 600)

        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                saveSpecToFile()
            }
        })

        setupUndoRedo()
        setupKeystrokes()

        val buttonPanel = JPanel(BorderLayout(5, 0)).apply {
            add(evaluateButton, BorderLayout.CENTER)
            add(pasteAndEvaluateButton, BorderLayout.EAST)
        }

        val evalHeight = evaluateButton.preferredSize.height
        pasteAndEvaluateButton.preferredSize = Dimension(evalHeight, evalHeight)

        val controlPanel = JPanel(BorderLayout(0, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(specEditorPane, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.SOUTH)
        }
        val splitPane = JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, controlPanel, plotPanel
        ).apply {
            dividerLocation = 420
        }
        contentPane.layout = BorderLayout()
        contentPane.add(splitPane, BorderLayout.CENTER)

        pack()
        setLocationRelativeTo(null)
    }

    private fun saveSpecToFile() {
        val specFile = getSpecFile()
        try {
            specFile.parentFile.mkdirs()
            specFile.writeText(plotSpecTextArea.text)
            println("Plot spec saved to ${specFile.absolutePath}")
        } catch (e: IOException) {
            println("Error: Could not save plot spec to ${specFile.absolutePath}")
            e.printStackTrace()
        }
    }

    private fun setupUndoRedo() {
        plotSpecTextArea.document.addUndoableEditListener { event ->
            undoManager.addEdit(event.edit)
            updateUndoRedoState()
        }
        undoAction = object : AbstractAction("Undo") {
            override fun actionPerformed(e: ActionEvent?) {
                if (undoManager.canUndo()) {
                    undoManager.undo()
                    updateUndoRedoState()
                }
            }
        }.apply { isEnabled = false }

        redoAction = object : AbstractAction("Redo") {
            override fun actionPerformed(e: ActionEvent?) {
                if (undoManager.canRedo()) {
                    undoManager.redo()
                    updateUndoRedoState()
                }
            }
        }.apply { isEnabled = false }
    }

    private fun updateUndoRedoState() {
        undoAction.isEnabled = undoManager.canUndo()
        redoAction.isEnabled = undoManager.canRedo()
    }

    private fun setupKeystrokes() {
        val inputMap = plotSpecTextArea.getInputMap(JComponent.WHEN_FOCUSED)
        val actionMap = plotSpecTextArea.actionMap

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), "evaluateAction")
        actionMap.put("evaluateAction", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                evaluate()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoAction")
        actionMap.put("undoAction", undoAction)

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redoAction")
        actionMap.put("redoAction", redoAction)
    }

    private fun pasteFromClipboard(): Boolean {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                val clipboardText = clipboard.getData(DataFlavor.stringFlavor) as String
                plotSpecTextArea.text = clipboardText
                plotSpecTextArea.caretPosition = 0
                return true
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No text found on the clipboard.",
                    "Paste Info",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Could not paste text from clipboard.\nError: ${e.message}",
                "Paste Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
        return false
    }

    fun setSpec(spec: String) {
        plotSpecTextArea.text = spec
        plotSpecTextArea.caretPosition = 0
        undoManager.discardAllEdits()
        updateUndoRedoState()
    }

    fun evaluate() {
        saveSpecToFile()

        val spec = parsePlotSpec(plotSpecTextArea.text).let(::fetchVegaLiteData)
        plotPanel.removeAll()
        val processedSpec = MonolithicCommon.processRawSpecs(spec)
        val newPlotComponent = DefaultPlotPanelBatik(
            processedSpec = processedSpec,
            preferredSizeFromPlot = false,
            repaintDelay = 300,
            preserveAspectRatio = false,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }
        plotPanel.add(newPlotComponent, BorderLayout.CENTER)
        plotPanel.revalidate()
        plotPanel.repaint()
    }

    private fun fetchVegaLiteData(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        val url = plotSpec.getString("data", "url")
        if (url != null) {
            try {
                val urlObj = URL("https://vega.github.io/editor/$url")
                val connection = urlObj.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val content = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                plotSpec.remove("data")
                plotSpec.write("data", "values") { VegaDataUtil.parseVegaDataset(content, url) }
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to fetch data from URL: $url\nError: ${e.message}",
                    "Data Fetch Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
        return plotSpec
    }
}
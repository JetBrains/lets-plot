package demo.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.canvas.CanvasPane
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.VegaDataUtil
import org.jetbrains.letsPlot.core.spec.write
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.undo.UndoManager

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

    // New components for frontend selection and pixel density
    private val frontendComboBox = JComboBox(arrayOf("batik", "canvas")).apply {
        addItemListener {
            pixelDensityLabel.isVisible = selectedItem == "canvas"
            pixelDensitySpinner.isVisible = selectedItem == "canvas"
            evaluate() // Re-render on frontend change
        }
    }
    private val pixelDensityLabel = JLabel("Pixel Density:").apply { isVisible = false }
    private val pixelDensitySpinner = JSpinner(SpinnerNumberModel(2.0, 0.5, 5.0, 0.1)).apply {
        isVisible = false
        preferredSize = Dimension(80, this.preferredSize.height) // Set a fixed width
        addChangeListener {
            // Use the debouncer timer
            densityDebouncer.restart()
        }
    }

    // Debouncer timer for pixel density
    private val densityDebouncer = Timer(300) {
        evaluate()
    }.apply {
        isRepeats = false // Make it a single-shot timer
    }

    // Frontend panel
    private val frontendPanel = JPanel().apply {
        layout = FlowLayout(FlowLayout.LEFT)
        add(JLabel("Frontend:"))
        add(frontendComboBox)
        add(pixelDensityLabel)
        add(pixelDensitySpinner)
    }

    // Favorites components
    private val favorites = mutableMapOf<String, String>()
    private val favoritesComboBox = JComboBox<String>()
    private val saveFavoriteButton = JButton("Save")
    private val loadFavoriteButton = JButton("Load")
    private val removeFavoriteButton = JButton("Remove")

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
        setupFavorites()

        val buttonPanel = JPanel(BorderLayout(5, 0)).apply {
            add(evaluateButton, BorderLayout.CENTER)
            add(pasteAndEvaluateButton, BorderLayout.EAST)
        }

        val evalHeight = evaluateButton.preferredSize.height
        pasteAndEvaluateButton.preferredSize = Dimension(evalHeight, evalHeight)

        // Panel for the buttons
        val favoriteButtonsPanel = JPanel(GridLayout(1, 3, 5, 0)).apply { // 1 row, 3 cols, 5px horizontal gap
            add(loadFavoriteButton)
            add(saveFavoriteButton)
            add(removeFavoriteButton)
        }

        // Main panel with a vertical layout
        val favoritesPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Favorites")

            // Align components to the left
            favoritesComboBox.alignmentX = Component.LEFT_ALIGNMENT
            favoriteButtonsPanel.alignmentX = Component.LEFT_ALIGNMENT

            // Set max size to prevent vertical stretching of combobox
            favoritesComboBox.maximumSize = Dimension(Integer.MAX_VALUE, favoritesComboBox.preferredSize.height)
            favoriteButtonsPanel.maximumSize = Dimension(Integer.MAX_VALUE, favoriteButtonsPanel.preferredSize.height)

            add(favoritesComboBox)
            add(Box.createRigidArea(Dimension(0, 5))) // 5px vertical space
            add(favoriteButtonsPanel)
        }

        val controlPanel = JPanel(BorderLayout(0, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(favoritesPanel, BorderLayout.NORTH)
            add(specEditorPane, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.SOUTH)
        }

        // Main layout changes:
        contentPane.layout = BorderLayout()

        val mainPanel = JPanel(BorderLayout())  // Panel to hold plot and frontend panel

        mainPanel.add(plotPanel, BorderLayout.CENTER) // Plot occupies center
        mainPanel.add(frontendPanel, BorderLayout.SOUTH) // Frontend panel under the plot

        val splitPane = JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, controlPanel, mainPanel
        ).apply {
            dividerLocation = 420
        }

        contentPane.add(splitPane, BorderLayout.CENTER) // splitPane occupies the contentPane

        pack()
        setLocationRelativeTo(null)

        updateFavoritesComboBox()
        updateSaveButtonState()
    }

    private fun getFavoritesFile(): File {
        val homeDir = System.getProperty("user.home")
        val appDir = File(homeDir, ".lets-plot-debugger")
        return File(appDir, "favorites.json")
    }


    private fun saveFavoritesToFile() {
        val favoritesFile = getFavoritesFile()
        try {
            favoritesFile.parentFile.mkdirs()
            val json = JsonSupport.formatJson(favorites)
            favoritesFile.writeText(json)
        } catch (e: IOException) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error saving favorites: ${e.message}",
                "Favorites Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun loadFavoritesFromFile() {
        val favoritesFile = getFavoritesFile()
        if (favoritesFile.exists()) {
            try {
                val jsonText = favoritesFile.readText()
                if (jsonText.isNotBlank()) {
                    val parsed = JsonSupport.parseJson(jsonText) as Map<*, *>
                    favorites.clear()
                    parsed.forEach { (key, value) ->
                        if (key is String && value is String) {
                            favorites[key] = value
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(
                    this,
                    "Error loading favorites: ${e.message}",
                    "Favorites Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun updateFavoritesComboBox() {
        val selected = favoritesComboBox.selectedItem
        favoritesComboBox.removeAllItems()
        favorites.keys.sorted().forEach { favoritesComboBox.addItem(it) }
        favoritesComboBox.selectedItem = selected

        val hasFavorites = favoritesComboBox.itemCount > 0
        loadFavoriteButton.isEnabled = hasFavorites
        removeFavoriteButton.isEnabled = hasFavorites
    }

    private fun updateSaveButtonState() {
        val selectedName = favoritesComboBox.selectedItem as? String
        if (selectedName == null) {
            saveFavoriteButton.isEnabled = true // Can always save as a new favorite
            return
        }

        val favoriteContent = favorites[selectedName]
        val currentContent = plotSpecTextArea.text

        // Disable if the selected favorite's content is the same as the current text
        saveFavoriteButton.isEnabled = (favoriteContent != currentContent)
    }

    private fun setupFavorites() {
        loadFavoritesFromFile()

        saveFavoriteButton.addActionListener {
            val name = JOptionPane.showInputDialog(this, "Enter a name for the favorite:", "Save Favorite", JOptionPane.PLAIN_MESSAGE)
            if (!name.isNullOrBlank()) {
                if (favorites.containsKey(name) && favorites[name] != plotSpecTextArea.text) {
                    val overwrite = JOptionPane.showConfirmDialog(
                        this,
                        "Favorite '$name' already exists. Overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION
                    )
                    if (overwrite != JOptionPane.YES_OPTION) {
                        return@addActionListener
                    }
                }
                favorites[name] = plotSpecTextArea.text
                saveFavoritesToFile()
                updateFavoritesComboBox()
                favoritesComboBox.selectedItem = name
            }
        }

        loadFavoriteButton.addActionListener {
            val selectedName = favoritesComboBox.selectedItem as? String
            if (selectedName != null) {
                val spec = favorites[selectedName]
                if (spec != null) {
                    setSpec(spec)
                    evaluate()
                }
            }
        }

        removeFavoriteButton.addActionListener {
            val selectedName = favoritesComboBox.selectedItem as? String
            if (selectedName != null) {
                val confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove '$selectedName'?",
                    "Remove Favorite",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirm == JOptionPane.YES_OPTION) {
                    favorites.remove(selectedName)
                    saveFavoritesToFile()
                    updateFavoritesComboBox()
                }
            }
        }

        favoritesComboBox.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                updateSaveButtonState()
            }
        }

        plotSpecTextArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateSaveButtonState()
            }
            override fun removeUpdate(e: DocumentEvent?) {
                updateSaveButtonState()
            }
            override fun changedUpdate(e: DocumentEvent?) {
                updateSaveButtonState()
            }
        })
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
        // Stop the debouncer timer if it's running
        densityDebouncer.stop()

        saveSpecToFile()

        val spec = parsePlotSpec(plotSpecTextArea.text).let(::fetchVegaLiteData)
        plotPanel.removeAll()

        try {
            val newPlotComponent = when (frontendComboBox.selectedItem) {
                "batik" -> {
                    val processedSpec = MonolithicCommon.processRawSpecs(spec)
                    DefaultPlotPanelBatik(
                        processedSpec = processedSpec,
                        preferredSizeFromPlot = false,
                        repaintDelay = 300,
                        preserveAspectRatio = false,
                    ) { messages ->
                        for (message in messages) {
                            println("[Demo Plot Viewer] $message")
                        }
                    }
                }

                "canvas" -> {
                    val plotFig = MonolithicCanvas.buildPlotFigureFromRawSpec(
                        rawSpec = spec,
                        sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false),
                        computationMessagesHandler = { messages ->
                            for (message in messages) {
                                println("[PlotSpecDebugger] $message")
                            }
                        }
                    )
                    CanvasPane(plotFig, pixelDensity = (pixelDensitySpinner.value as Double))
                }
                else -> throw IllegalArgumentException("Unknown frontend: ${frontendComboBox.selectedItem}")
            }

            plotPanel.add(newPlotComponent, BorderLayout.CENTER)
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error building plot: ${e.message}",
                "Plot Error",
                JOptionPane.ERROR_MESSAGE
            )
            // The crucial part:  Reset the selection back to what it was.
            // This prevents the combo box from getting into a bad state.
            SwingUtilities.invokeLater {
                frontendComboBox.selectedItem = frontendComboBox.selectedItem // Reset selection
            }

        } finally {
            plotPanel.revalidate()
            plotPanel.repaint()
        }
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

}
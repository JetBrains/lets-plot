package demo.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.canvas.CanvasPane
import org.jetbrains.letsPlot.awt.plot.component.DefaultPlotPanelCanvas
import org.jetbrains.letsPlot.awt.sandbox.SandboxToolbarAwt
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicButtonUI
import javax.swing.undo.UndoManager

// --- START OF PYTHON INTEGRATION ---
object PythonRunner {
    private const val PREFS_FILE = "python_config.json"

    data class PythonConfig(val pythonPath: String)


    fun getPythonPath(): String? {
        val file = getConfigFile()
        if (file.exists()) {
            try {
                val json = file.readText()
                val map = JsonSupport.parseJson(json) as Map<*, *>
                return map["pythonPath"] as? String
            } catch (e: Exception) {
                // Ignore
            }
        }
        return null
    }

    fun setPythonPath(path: String) {
        val file = getConfigFile()
        file.parentFile.mkdirs()
        val json = JsonSupport.formatJson(mapOf("pythonPath" to path))
        file.writeText(json)
    }

    private fun getConfigFile(): File {
        val homeDir = System.getProperty("user.home")
        val appDir = File(homeDir, ".lets-plot-debugger")
        return File(appDir, PREFS_FILE)
    }

    /**
     * returns Pair(JsonString?, ErrorMessage?)
     */
    fun runPythonScript(userCode: String, pythonPath: String): Pair<String?, String?> {
        // Wrapper script that executes user code and extracts the plot
        // remove LetsPlot.setup_html() from user code
        // For terminal use, we force offline mode with no JS to avoid any network calls.
        val userCode = userCode.replace(Regex("""LetsPlot\.setup_html\([^)]*\)\s*"""), "")

        val wrapperScript = """
            |import sys
            |import json
            |import os
            |
            |try:
            |    from lets_plot import *
            |    from lets_plot._type_utils import standardize_dict
            |    LetsPlot.setup_html(offline=True, no_js=True)
            |except ImportError:
            |    print("Error: 'lets-plot' library is not installed in this Python environment.", file=sys.stderr)
            |    sys.exit(1)
            |
            |def run_user_code():
            |    # User code will be injected here via execution
            |    user_code_path = sys.argv[1]
            |    
            |    with open(user_code_path, 'r', encoding='utf-8') as f:
            |        code = f.read()
            |
            |    local_scope = {}
            |    
            |    try:
            |        lp_init = "from lets_plot import *; LetsPlot.setup_html(offline=True, no_js=True)\n"
            |        exec(lp_init + code, {}, local_scope)
            |    except Exception as e:
            |        import traceback
            |        traceback.print_exc(file=sys.stderr)
            |        sys.exit(1)
            |
            |    # Strategy: 
            |    # 1. Look for variable named 'p' (convention)
            |    # 2. Look for any variable that has 'as_dict' method (lets-plot object)
            |    
            |    plot_obj = local_scope.get('p')
            |    
            |    if plot_obj is None:
            |        # Fallback: find the last defined object that looks like a plot
            |        candidates = [v for k, v in local_scope.items() if hasattr(v, 'as_dict')]
            |        if candidates:
            |            plot_obj = candidates[-1]
            |
            |    if plot_obj and hasattr(plot_obj, 'as_dict'):
            |        plot_dict = standardize_dict(plot_obj.as_dict())
            |        plot_json = json.dumps(plot_dict, indent=2)
            |        print(plot_json)
            |    else:
            |        print("Error: No plot object found. Please assign your plot to a variable named 'p'.", file=sys.stderr)
            |        sys.exit(1)
            |
            |if __name__ == "__main__":
            |    run_user_code()
            |""".trimMargin()

        try {
            val wrapperFile = File.createTempFile("lets_plot_wrapper", ".py")
            wrapperFile.writeText(wrapperScript)

            val userCodeFile = File.createTempFile("user_code", ".py")
            userCodeFile.writeText(userCode)

            val pb = ProcessBuilder(pythonPath, wrapperFile.absolutePath, userCodeFile.absolutePath)
            pb.environment()["PYTHONIOENCODING"] = "utf-8"

            val process = pb.start()

            // Read stdout (JSON)
            val output = process.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            // Read stderr (Errors)
            val errors = process.errorStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

            val exited = process.waitFor(10, TimeUnit.SECONDS)

            wrapperFile.delete()
            userCodeFile.delete()

            if (!exited) {
                process.destroy()
                return Pair(null, "Timeout: Python script took too long to execute.")
            }

            if (process.exitValue() != 0) {
                return Pair(null, errors.ifBlank { "Unknown Python error (Exit code ${process.exitValue()})" })
            }

            return Pair(output, if (errors.isNotBlank()) "Python stderr:\n$errors" else null)

        } catch (e: Exception) {
            return Pair(null, "Execution failed: ${e.message}")
        }
    }
}
// --- END OF PYTHON INTEGRATION ---

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
            // Attempt to treat initial load as JSON.
            // If main() was triggered with Python code in environment, this logic handles it gracefully by failing JSON parse
            // and letting the UI load the raw string.
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
            // If it's not JSON, it might be python. Don't print stacktrace on startup, just let UI handle it.
            //e.printStackTrace()
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
        autoscrolls = true
        // Set a monospaced font for code
        font = Font("Monospaced", Font.PLAIN, 12)
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

    private val pythonConfigButton = JButton("Python Path").apply {
        addActionListener {
            val current = PythonRunner.getPythonPath() ?: "python"
            val input = JOptionPane.showInputDialog(this@PlotSpecDebugger,
                "Enter path to Python executable (must have 'lets-plot' installed):",
                current
            )
            if (input != null && input.isNotBlank()) {
                PythonRunner.setPythonPath(input.trim())
            }
        }
    }

    private val sharedToolbar = SandboxToolbarAwt()
    private val plotPanel = JPanel(BorderLayout()).apply {
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        // Extra cleanup removing child components.
        addContainerListener(object : ContainerAdapter() {
            override fun componentRemoved(e: ContainerEvent) {
                if (e.child is Disposable) {   // the actual plot panel is disposible.
                    (e.child as Disposable).dispose()
                }
            }
        })
    }

    private val undoManager = UndoManager()
    private lateinit var undoAction: AbstractAction
    private lateinit var redoAction: AbstractAction

    // New components for frontend selection and pixel density
    private val frontendComboBox = JComboBox(arrayOf("batik", "DefaultPlotPanelCanvas", "CanvasPane")).apply {
        selectedItem = "DefaultPlotPanelCanvas"
        addActionListener {
            pixelDensityLabel.isVisible = selectedItem == "CanvasPane"
            pixelDensitySpinner.isVisible = selectedItem == "CanvasPane"
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

    private val exportPngButton = JButton("Export PNG").apply {
        addActionListener {
            if (plotPanel.width > 0 && plotPanel.height > 0) {
                try {
                    // Create an image of the plotPanel content
                    val image = BufferedImage(plotPanel.width, plotPanel.height, BufferedImage.TYPE_INT_RGB)
                    val g2 = image.createGraphics()
                    plotPanel.paint(g2)
                    g2.dispose()

                    // Show Save Dialog
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Save Screenshot"
                        selectedFile = File("plot_screenshot.png")
                    }

                    if (fileChooser.showSaveDialog(this@PlotSpecDebugger) == JFileChooser.APPROVE_OPTION) {
                        ImageIO.write(image, "png", fileChooser.selectedFile)
                    }
                    Desktop.getDesktop().open(fileChooser.selectedFile)
                } catch (ex: Exception) {
                    JOptionPane.showMessageDialog(
                        this@PlotSpecDebugger,
                        "Failed to save screenshot: ${ex.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                    ex.printStackTrace()
                }
            }
        }
    }

    private val exportSvgButton = JButton("Export SVG").apply {
        addActionListener {
            if (plotPanel.width > 0 && plotPanel.height > 0) {
                try {
                    val canvaPanel = plotPanel.components.first() as DefaultPlotPanelCanvas
                    val canvasPane = canvaPanel.components.first() as CanvasPane
                    val svgCanvasFigure = canvasPane.figure as SvgCanvasFigure
                    val svgString = SvgToString.render(svgCanvasFigure.svgSvgElement)

                    // Show Save Dialog
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Save SVG"
                        selectedFile = File("plot_screenshot.svg")
                    }

                    if (fileChooser.showSaveDialog(this@PlotSpecDebugger) == JFileChooser.APPROVE_OPTION) {
                        Files.write(fileChooser.selectedFile.toPath(), svgString.toByteArray(StandardCharsets.UTF_8))
                    }
                    Desktop.getDesktop().open(fileChooser.selectedFile)
                } catch (ex: Exception) {
                    JOptionPane.showMessageDialog(
                        this@PlotSpecDebugger,
                        "Failed to save screenshot: ${ex.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                    ex.printStackTrace()
                }
            }
        }
    }


    // Frontend panel
    private val frontendPanel = JPanel().apply {
        layout = FlowLayout(FlowLayout.LEFT)
        add(JLabel("Frontend:"))
        add(frontendComboBox)
        add(pixelDensityLabel)
        add(pixelDensitySpinner)
        add(Box.createHorizontalStrut(10))
        add(exportPngButton)
        add(exportSvgButton)
        add(Box.createHorizontalStrut(10))
        add(pythonConfigButton) // Add config button here
    }

    // Favorites components
    private val favorites = mutableMapOf<String, String>()
    private val favoritesComboBox = JComboBox<String>()
    private val saveFavoriteButton = JButton("Save")
    private val loadFavoriteButton = JButton("Load")
    private val removeFavoriteButton = JButton("Remove")

    // Processed spec components
    private val processedSpecTextArea = JTextArea().apply {
        isEditable = false
        font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
        lineWrap = false
    }
    private val processedSpecScrollPane = JScrollPane(processedSpecTextArea)
    private val toggleSpecPanelButton = JToggleButton().apply {
        margin = Insets(5, 2, 5, 2)
    }
    private var mainSplitPane: JSplitPane
    private var lastProcessedSpecDividerLocation: Int = -1
    private var defaultDividerSize: Int = 0

    // Message components
    private val messagesTextArea = JTextArea().apply {
        isEditable = false
        font = Font("Monospaced", Font.PLAIN, 12)
        foreground = Color.RED
        wrapStyleWord = true
        lineWrap = true
        autoscrolls = true
    }
    private val messagesScrollPane = JScrollPane(messagesTextArea)
    private val plotAndMessagesSplitPane: JSplitPane


    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(1400, 800)

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

        val favoriteButtonsPanel = JPanel(GridLayout(1, 3, 5, 0)).apply {
            add(loadFavoriteButton)
            add(saveFavoriteButton)
            add(removeFavoriteButton)
        }

        val favoritesPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Favorites")
            favoritesComboBox.alignmentX = LEFT_ALIGNMENT
            favoriteButtonsPanel.alignmentX = LEFT_ALIGNMENT
            favoritesComboBox.maximumSize = Dimension(Integer.MAX_VALUE, favoritesComboBox.preferredSize.height)
            favoriteButtonsPanel.maximumSize = Dimension(Integer.MAX_VALUE, favoriteButtonsPanel.preferredSize.height)
            add(favoritesComboBox)
            add(Box.createRigidArea(Dimension(0, 5)))
            add(favoriteButtonsPanel)
        }

        val controlPanel = JPanel(BorderLayout(0, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(favoritesPanel, BorderLayout.NORTH)
            add(specEditorPane, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.SOUTH)
        }

        plotAndMessagesSplitPane = JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            plotPanel,
            messagesScrollPane
        ).apply {
            resizeWeight = 1.0 // Give all extra space to the plot panel
        }
        messagesScrollPane.isVisible = false // Hide initially

        val plotAreaPanel = JPanel(BorderLayout())
        plotAreaPanel.add(sharedToolbar, BorderLayout.NORTH)
        plotAreaPanel.add(plotAndMessagesSplitPane, BorderLayout.CENTER)
        plotAreaPanel.add(frontendPanel, BorderLayout.SOUTH)

        controlPanel.minimumSize = Dimension(0, 0)
        plotAreaPanel.minimumSize = Dimension(0, 0)
        processedSpecScrollPane.minimumSize = Dimension(0, 0)

        val leftSplitPane = JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, controlPanel, plotAreaPanel
        ).apply {
            dividerLocation = 500
        }

        mainSplitPane = JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            leftSplitPane,
            processedSpecScrollPane
        ).apply {
            isContinuousLayout = true
            resizeWeight = 1.0
        }

        // FIX: Hide splitter and panel by default.
        processedSpecScrollPane.isVisible = false
        defaultDividerSize = mainSplitPane.dividerSize
        mainSplitPane.dividerSize = 0

        setupProcessedSpecToggle()

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(mainSplitPane, BorderLayout.CENTER)
        mainPanel.add(toggleSpecPanelButton, BorderLayout.EAST)

        contentPane.add(mainPanel)

        pack()
        setLocationRelativeTo(null)

        updateFavoritesComboBox()
        updateSaveButtonState()
    }

    private fun setupProcessedSpecToggle() {
        toggleSpecPanelButton.setUI(VerticalButtonUI())
        updateToggleButtonText()

        toggleSpecPanelButton.addActionListener {
            if (toggleSpecPanelButton.isSelected) {
                processedSpecScrollPane.isVisible = true
                mainSplitPane.dividerSize = defaultDividerSize
                SwingUtilities.invokeLater {
                    val targetLocation = if (lastProcessedSpecDividerLocation != -1) {
                        lastProcessedSpecDividerLocation
                    } else {
                        (mainSplitPane.width * 0.75).toInt()
                    }
                    mainSplitPane.setDividerLocation(targetLocation)
                }
            } else {
                lastProcessedSpecDividerLocation = mainSplitPane.dividerLocation
                processedSpecScrollPane.isVisible = false
                mainSplitPane.dividerSize = 0
            }
            updateToggleButtonText()
        }
    }


    private fun updateToggleButtonText() {
        val text = if (toggleSpecPanelButton.isSelected) "Hide processed spec" else "Show processed spec"
        toggleSpecPanelButton.text = text.toCharArray().joinToString("\n")
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
            JOptionPane.showMessageDialog(this, "Error saving favorites: ${e.message}", "Favorites Error", JOptionPane.ERROR_MESSAGE)
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
                        if (key is String && value is String) favorites[key] = value
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
            saveFavoriteButton.isEnabled = true
            return
        }
        val favoriteContent = favorites[selectedName]
        val currentContent = plotSpecTextArea.text
        saveFavoriteButton.isEnabled = (favoriteContent != currentContent)
    }

    private fun setupFavorites() {
        loadFavoritesFromFile()

        saveFavoriteButton.addActionListener {
            val name = JOptionPane.showInputDialog(
                this,
                "Enter a name for the favorite:",
                "Save Favorite",
                JOptionPane.PLAIN_MESSAGE
            )
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
        } catch (e: IOException) {
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        densityDebouncer.stop()
        saveSpecToFile()

        messagesTextArea.text = ""
        messagesScrollPane.isVisible = false
        plotPanel.removeAll()
        plotPanel.revalidate()
        plotPanel.repaint() // Clear immediately so user sees something is happening

        val rawText = plotSpecTextArea.text.trim()
        val isJson = rawText.startsWith("{")

        // Helper to update messages UI
        val messageHandler: (List<String>) -> Unit = { messages ->
            if (messages.isNotEmpty()) {
                SwingUtilities.invokeLater {
                    if (!messagesScrollPane.isVisible) {
                        messagesScrollPane.isVisible = true
                        plotAndMessagesSplitPane.setDividerLocation(0.8)
                    }
                    val currentText = messagesTextArea.text
                    val newText = if (currentText.isBlank()) messages.joinToString("\n") else currentText + "\n" + messages.joinToString("\n")
                    messagesTextArea.text = newText
                    messagesTextArea.caretPosition = messagesTextArea.document.length
                }
            }
        }

        // Logic to obtain the spec map
        fun processSpec(specMap: Map<String, Any>) {
            try {
                val processedSpec: Map<String, Any>
                val newPlotComponent: JComponent

                when (frontendComboBox.selectedItem) {
                    "batik" -> {
                        processedSpec = MonolithicCommon.processRawSpecs(specMap as MutableMap<String, Any>)
                        newPlotComponent = DefaultPlotPanelBatik(
                            processedSpec = processedSpec,
                            preferredSizeFromPlot = false,
                            repaintDelay = 300,
                            preserveAspectRatio = false,
                            computationMessagesHandler = messageHandler
                        )
                    }
                    "CanvasPane" -> {
                        processedSpec = MonolithicCommon.processRawSpecs(specMap as MutableMap<String, Any>)
                        val plotFig = PlotCanvasFigure()
                        plotFig.onHrefClick { Desktop.getDesktop().browse(java.net.URI.create(it)) }
                        plotFig.update(
                            processedSpec,
                            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false),
                            computationMessagesHandler = messageHandler
                        )
                        newPlotComponent = CanvasPane(plotFig, pixelDensity = (pixelDensitySpinner.value as Double))
                    }
                    "DefaultPlotPanelCanvas" -> {
                        processedSpec = MonolithicCommon.processRawSpecs(specMap as MutableMap<String, Any>)
                        newPlotComponent = DefaultPlotPanelCanvas(
                            processedSpec = processedSpec,
                            preferredSizeFromPlot = false,
                            repaintDelay = 0,
                            preserveAspectRatio = false,
                            computationMessagesHandler = messageHandler
                        )
                    }
                    else -> throw IllegalArgumentException("Unknown frontend")
                }

                processedSpecTextArea.text = JsonSupport.formatJson(processedSpec, pretty = true)
                processedSpecTextArea.caretPosition = 0

                if (newPlotComponent is DefaultPlotPanelBatik) sharedToolbar.attach(newPlotComponent.figureModel)
                else if (newPlotComponent is DefaultPlotPanelCanvas) sharedToolbar.attach(newPlotComponent.figureModel)

                plotPanel.add(newPlotComponent, BorderLayout.CENTER)
                plotPanel.revalidate()
                plotPanel.repaint()
            } catch (e: Exception) {
                e.printStackTrace()
                val errorText = "Error rendering plot:\n${e.message}\n${e.stackTraceToString()}"
                messageHandler(listOf(errorText))
                JOptionPane.showMessageDialog(this, "Error building plot: ${e.message}", "Plot Error", JOptionPane.ERROR_MESSAGE)
            }
        }

        if (isJson) {
            try {
                val spec = parsePlotSpec(rawText)
                processSpec(spec)
            } catch (e: Exception) {
                messageHandler(listOf("JSON Parse Error: ${e.message}"))
                JOptionPane.showMessageDialog(this, "Invalid JSON: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
            }
        } else {
            // Assume Python
            val pythonPath = PythonRunner.getPythonPath() ?: "python"

            // Show a "Running..." cursor
            cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
            evaluateButton.isEnabled = false

            // Run in background to avoid freezing UI
            val worker = object : SwingWorker<Pair<String?, String?>, Void>() {
                override fun doInBackground(): Pair<String?, String?> {
                    return PythonRunner.runPythonScript(rawText, pythonPath)
                }

                override fun done() {
                    cursor = Cursor.getDefaultCursor()
                    evaluateButton.isEnabled = true
                    try {
                        val result = get()
                        val jsonOutput = result.first
                        val errorOutput = result.second

                        if (errorOutput != null) {
                            messageHandler(listOf(errorOutput))
                        }

                        if (jsonOutput != null) {
                            try {
                                val spec = parsePlotSpec(jsonOutput)
                                processSpec(spec)
                            } catch (e: Exception) {
                                messageHandler(listOf(
                                    "Error parsing JSON from Python output: ${e.message}",
                                    "Output was:\n$jsonOutput"
                                ))
                            }
                        } else if (errorOutput == null) {
                            messageHandler(listOf("Python script finished but produced no output."))
                        }
                    } catch (e: Exception) {
                        messageHandler(listOf("Execution Error: ${e.message}"))
                        e.printStackTrace()
                    }
                }
            }
            worker.execute()
        }
    }

    private class PasteIcon(private val size: Int = 20) : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            val g2d = g.create() as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val boardColor = Color(80, 80, 80)
            g2d.color = boardColor
            g2d.fillRoundRect(x + 2, y, size - 4, size, 6, 6)
            g2d.fillRect(x + 6, y - 1, size - 12, 5)
            g2d.color = Color(245, 245, 245)
            g2d.fillRect(x + 4, y + 4, size - 8, size - 7)
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

    private class VerticalButtonUI : BasicButtonUI() {
        override fun paint(g: Graphics, c: JComponent) {
            val button = c as AbstractButton
            // Call the superclass's paint method to paint the button's background, border, etc.
            super.paint(g, c)

            val text = button.text
            if (text == null || text.isEmpty()) {
                return
            }

            val g2d = g.create() as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.color = button.foreground
            g2d.font = button.font

            val fm = g2d.fontMetrics
            val lines = text.split("\n")
            val lineHeight = fm.height
            val totalHeight = lines.size * lineHeight

            // Calculate the starting y-coordinate to center the text vertically
            var y = (c.height - totalHeight) / 2 + fm.ascent

            for (line in lines) {
                // Calculate the x-coordinate to center the text horizontally
                val stringWidth = fm.stringWidth(line)
                val x = (c.width - stringWidth) / 2
                g2d.drawString(line, x, y)
                y += lineHeight
            }
            g2d.dispose()
        }

        override fun getPreferredSize(c: JComponent): Dimension {
            val text = (c as AbstractButton).text ?: return Dimension(20, 100)
            val fm = c.getFontMetrics(c.font)
            val lines = text.split("\n")
            var maxWidth = 0
            for (line in lines) {
                maxWidth = kotlin.math.max(maxWidth, fm.stringWidth(line))
            }
            val totalHeight = fm.height * lines.size
            val insets = c.insets
            return Dimension(
                maxWidth + insets.left + insets.right,
                totalHeight + insets.top + insets.bottom
            )
        }
    }
}
package demo.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.canvas.CanvasPane2
import org.jetbrains.letsPlot.awt.plot.component.DefaultPlotPanelCanvas
import org.jetbrains.letsPlot.awt.sandbox.SandboxToolbarAwt
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure2
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicButtonUI
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
            dividerLocation = 420
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
                // Show panel
                processedSpecScrollPane.isVisible = true
                mainSplitPane.dividerSize = defaultDividerSize // Restore splitter

                // FIX: Use invokeLater to set the divider location after the layout is updated.
                SwingUtilities.invokeLater {
                    val targetLocation = if (lastProcessedSpecDividerLocation != -1) {
                        lastProcessedSpecDividerLocation
                    } else {
                        // First time opening: occupy 25% of the width
                        (mainSplitPane.width * 0.75).toInt()
                    }
                    mainSplitPane.setDividerLocation(targetLocation)
                }
            } else {
                // Hide panel
                lastProcessedSpecDividerLocation = mainSplitPane.dividerLocation // Store current size
                processedSpecScrollPane.isVisible = false
                mainSplitPane.dividerSize = 0 // Hide splitter
            }
            updateToggleButtonText()
        }
    }


    private fun updateToggleButtonText() {
        val text = if (toggleSpecPanelButton.isSelected) {
            "Hide processed spec"
        } else {
            "Show processed spec"
        }
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
        densityDebouncer.stop()
        saveSpecToFile()

        // Clear and hide messages pane at the start of evaluation
        messagesTextArea.text = ""
        messagesScrollPane.isVisible = false

        val spec = parsePlotSpec(plotSpecTextArea.text)
        plotPanel.removeAll()

        // Message handler to display messages in the UI
        val messageHandler: (List<String>) -> Unit = { messages ->
            if (messages.isNotEmpty()) {
                SwingUtilities.invokeLater {
                    if (!messagesScrollPane.isVisible) {
                        messagesScrollPane.isVisible = true
                        plotAndMessagesSplitPane.setDividerLocation(0.8)
                    }
                    messagesTextArea.text = messages.joinToString("\n")
                    messagesTextArea.caretPosition = messagesTextArea.document.length
                }
            }
        }

        try {
            val processedSpec: Map<String, Any>
            val newPlotComponent: JComponent

            when (frontendComboBox.selectedItem) {
                "batik" -> {
                    processedSpec = MonolithicCommon.processRawSpecs(spec)
                    newPlotComponent = DefaultPlotPanelBatik(
                        processedSpec = processedSpec,
                        preferredSizeFromPlot = false,
                        repaintDelay = 300,
                        preserveAspectRatio = false,
                        computationMessagesHandler = messageHandler
                    )
                }

                "CanvasPane" -> {
                    processedSpec = MonolithicCommon.processRawSpecs(spec)
                    val plotFig = PlotCanvasFigure2()
                    plotFig.onHrefClick {
                        Desktop.getDesktop().browse(java.net.URI.create(it))
                    }
                    plotFig.update(
                        processedSpec,
                        sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false),
                        computationMessagesHandler = messageHandler
                    )
                    newPlotComponent = CanvasPane2(plotFig, pixelDensity = (pixelDensitySpinner.value as Double))
                }

                "DefaultPlotPanelCanvas" -> {
                    processedSpec = MonolithicCommon.processRawSpecs(spec)
                    newPlotComponent = DefaultPlotPanelCanvas(
                        processedSpec = processedSpec,
                        preferredSizeFromPlot = false,
                        repaintDelay = 0,
                        preserveAspectRatio = false,
                        computationMessagesHandler = messageHandler
                    )
                }

                else -> throw IllegalArgumentException("Unknown frontend: ${frontendComboBox.selectedItem}")
            }

            processedSpecTextArea.text = JsonSupport.formatJson(processedSpec, pretty = true)
            processedSpecTextArea.caretPosition = 0

            if (newPlotComponent is DefaultPlotPanelBatik) {
                sharedToolbar.attach(newPlotComponent.figureModel)
            } else if (newPlotComponent is DefaultPlotPanelCanvas) {
                sharedToolbar.attach(newPlotComponent.figureModel)
            }

            plotPanel.add(newPlotComponent, BorderLayout.CENTER)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorText = "Error processing spec:\n\n${e.message}\n\n${e.stackTraceToString()}"
            processedSpecTextArea.text = errorText
            messageHandler(listOf(errorText)) // Show error in the new message panel
            JOptionPane.showMessageDialog(
                this,
                "Error building plot: ${e.message}",
                "Plot Error",
                JOptionPane.ERROR_MESSAGE
            )
        } finally {
            plotPanel.revalidate()
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
    }}
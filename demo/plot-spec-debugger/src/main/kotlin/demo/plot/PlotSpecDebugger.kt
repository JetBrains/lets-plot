/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.canvas.CanvasComponent
import org.jetbrains.letsPlot.awt.plot.swing.SwingPlotPanel
import org.jetbrains.letsPlot.awt.sandbox.SandboxToolbarAwt
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.util.SvgToString
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.raster.view.SvgCanvasDrawable
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.undo.UndoManager

fun main() {
    val specString = System.getenv("PLOT_SPEC")
        ?: SpecStore.load()
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
        } catch (_: Exception) {
            plotSpecDebugger.setSpec(specString)
            // If it's not JSON, it might be python. Don't print stacktrace on startup, just let UI handle it.
        }
        plotSpecDebugger.isVisible = true
    }
}

class PlotSpecDebugger : JFrame("PlotSpec Debugger") {
    private enum class EditorSpecSourceKind {
        DIRECT,
        FAVORITE,
        TEMP
    }

    private data class EditorSpecSource(
        val kind: EditorSpecSourceKind,
        val id: String? = null
    )

    private val plotSpecTextArea = object : JTextArea() {
        override fun paste() {
            val before = text
            isPasteInProgress = true
            try {
                super.paste()
            } finally {
                isPasteInProgress = false
            }
            if (text != before) {
                registerPastedSpec(text, resetUndoHistory = false)
            }
        }
    }.apply {
        wrapStyleWord = true
        autoscrolls = true
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
            val current = PythonConfigStore.getPythonPath() ?: "python"
            val input = JOptionPane.showInputDialog(this@PlotSpecDebugger,
                "Enter path to Python executable (must have 'lets-plot' installed):",
                current
            )
            if (input != null && input.isNotBlank()) {
                PythonConfigStore.setPythonPath(input.trim())
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
        preferredSize = Dimension(80, this.preferredSize.height)
        addChangeListener {
            densityDebouncer.restart()
        }
    }

    private val densityDebouncer = Timer(300) {
        evaluate()
    }.apply {
        isRepeats = false
    }

    private val exportPngButton = JButton("Export PNG").apply {
        addActionListener {
            if (plotPanel.width > 0 && plotPanel.height > 0) {
                try {
                    val image = BufferedImage(plotPanel.width, plotPanel.height, BufferedImage.TYPE_INT_RGB)
                    val g2 = image.createGraphics()
                    plotPanel.paint(g2)
                    g2.dispose()

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
                    val canvaPanel = plotPanel.components.first() as SwingPlotPanel
                    val canvasComponent = canvaPanel.components.first() as CanvasComponent
                    val svgCanvasDrawable = canvasComponent.content as SvgCanvasDrawable
                    val svgString = SvgToString.render(svgCanvasDrawable.svgSvgElement)

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
        add(pythonConfigButton)
    }

    private val favoritesStore = FavoritesStore()
    private val tempStore = TempStore()
    private val favoritesListModel = DefaultListModel<String>()
    private val favoritesList = JList(favoritesListModel).apply {
        cellRenderer = FavoriteCellRenderer { name -> favoritesStore[name] }
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        visibleRowCount = 4
        // Uniform row height — rows without a cached thumbnail stay aligned with rows that have one.
        fixedCellHeight = PreviewCache.THUMB_HEIGHT + 8
    }
    private val tempListModel = DefaultListModel<String>()
    private val tempList = JList(tempListModel).apply {
        cellRenderer = TempCellRenderer { id -> tempStore[id] }
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        visibleRowCount = 4
        fixedCellHeight = PreviewCache.THUMB_HEIGHT + 8
    }
    private val previewPopup by lazy { PreviewHoverPopup(this) }
    private var favoritesHoverRowIndex = -1
    private var tempHoverRowIndex = -1
    private val saveFavoriteButton = JButton("Save")
    private val loadFavoriteButton = JButton("Load")
    private val removeFavoriteButton = JButton("Remove")
    private val loadTempButton = JButton("Load")
    private val removeTempButton = JButton("Remove")
    private var pendingPreviewTimer: Timer? = null

    private val processedSpecTextArea = JTextArea().apply {
        isEditable = false
        font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
        lineWrap = false
    }
    private val processedSpecScrollPane = JScrollPane(processedSpecTextArea)
    private val toggleFavoritesPanelButton = JToggleButton().apply {
        isSelected = true
        text = "_"
        font = font.deriveFont(Font.BOLD)
        isFocusPainted = false
        margin = Insets(0, 6, 6, 6)
        preferredSize = Dimension(28, 22)
    }
    private val toggleSpecPanelButton = JToggleButton().apply {
        margin = Insets(5, 2, 5, 2)
    }
    private val storageTabbedPane: JTabbedPane
    private val topBottomSplitPane: JSplitPane
    private var mainSplitPane: JSplitPane
    private var lastStorageDividerLocation: Int = 320
    private var defaultTopBottomDividerSize: Int = 0
    private var lastProcessedSpecDividerLocation: Int = -1
    private var defaultDividerSize: Int = 0
    private var editorSpecSource = EditorSpecSource(EditorSpecSourceKind.DIRECT)
    private var isProgrammaticSpecChange = false
    private var isPasteInProgress = false

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
                SpecStore.save(plotSpecTextArea.text)
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

        val tempButtonsPanel = JPanel(GridLayout(1, 2, 5, 0)).apply {
            add(loadTempButton)
            add(removeTempButton)
        }

        val favoritesPanel = JPanel(BorderLayout(0, 5)).apply {
            add(JScrollPane(favoritesList), BorderLayout.CENTER)
            add(favoriteButtonsPanel, BorderLayout.SOUTH)
        }

        val tempPanel = JPanel(BorderLayout(0, 5)).apply {
            add(JScrollPane(tempList), BorderLayout.CENTER)
            add(tempButtonsPanel, BorderLayout.SOUTH)
        }

        storageTabbedPane = JTabbedPane().apply {
            addTab("Favorites", favoritesPanel)
            addTab("Temp", tempPanel)
            setTabComponentAt(0, createFavoritesTabHeader())
        }

        topBottomSplitPane = JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            storageTabbedPane,
            specEditorPane
        ).apply {
            resizeWeight = 0.0  // keep editor growing when the window grows
            dividerLocation = 320
            isContinuousLayout = true
        }
        defaultTopBottomDividerSize = topBottomSplitPane.dividerSize

        val controlPanel = JPanel(BorderLayout(0, 10)).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(topBottomSplitPane, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.SOUTH)
        }

        plotAndMessagesSplitPane = JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            plotPanel,
            messagesScrollPane
        ).apply {
            resizeWeight = 1.0
        }
        messagesScrollPane.isVisible = false

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

        // Hide splitter and panel by default.
        processedSpecScrollPane.isVisible = false
        defaultDividerSize = mainSplitPane.dividerSize
        mainSplitPane.dividerSize = 0

        setupFavoritesToggle()
        setupProcessedSpecToggle()

        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(mainSplitPane, BorderLayout.CENTER)
        mainPanel.add(toggleSpecPanelButton, BorderLayout.EAST)

        contentPane.add(mainPanel)

        pack()
        setLocationRelativeTo(null)

        updateFavoritesList()
        updateTempList()
        updateSaveButtonState()
    }

    private fun setupFavoritesToggle() {
        updateFavoritesToggleButtonText()

        toggleFavoritesPanelButton.addActionListener {
            if (toggleFavoritesPanelButton.isSelected) {
                topBottomSplitPane.dividerSize = defaultTopBottomDividerSize
                SwingUtilities.invokeLater {
                    topBottomSplitPane.setDividerLocation(lastStorageDividerLocation)
                }
            } else {
                lastStorageDividerLocation = topBottomSplitPane.dividerLocation
                topBottomSplitPane.dividerSize = 0
                topBottomSplitPane.setDividerLocation(collapsedStorageHeight())
            }
            topBottomSplitPane.revalidate()
            topBottomSplitPane.repaint()
            updateFavoritesToggleButtonText()
        }
    }

    private fun createFavoritesTabHeader(): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)).apply {
            isOpaque = false
            add(JLabel("Favorites"))
            add(toggleFavoritesPanelButton)
        }
    }

    private fun collapsedStorageHeight(): Int {
        val tabHeight = if (storageTabbedPane.tabCount > 0) {
            storageTabbedPane.getBoundsAt(0)?.height ?: 0
        } else {
            0
        }
        return maxOf(34, tabHeight + 8, toggleFavoritesPanelButton.preferredSize.height + 12)
    }

    private fun updateFavoritesToggleButtonText() {
        toggleFavoritesPanelButton.toolTipText = if (toggleFavoritesPanelButton.isSelected) {
            "Minimize favorites"
        } else {
            "Show favorites"
        }
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

    private fun saveFavorites() {
        try {
            favoritesStore.save()
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

    private fun updateFavoritesList() {
        val selected = when (editorSpecSource.kind) {
            EditorSpecSourceKind.FAVORITE -> editorSpecSource.id
            else -> null
        }
        favoritesListModel.clear()
        favoritesStore.sortedNames().forEach { favoritesListModel.addElement(it) }
        if (selected != null && favoritesListModel.contains(selected)) {
            favoritesList.setSelectedValue(selected, true)
        } else {
            favoritesList.clearSelection()
        }
        updateFavoriteButtonsState()
    }

    private fun updateTempList() {
        val selected = when (editorSpecSource.kind) {
            EditorSpecSourceKind.TEMP -> editorSpecSource.id
            else -> null
        }
        tempListModel.clear()
        tempStore.ids().forEach { tempListModel.addElement(it) }
        if (selected != null && tempListModel.contains(selected)) {
            tempList.setSelectedValue(selected, true)
        } else {
            tempList.clearSelection()
        }
        updateTempButtonsState()
    }

    private fun updateFavoriteButtonsState() {
        val hasSelection = favoritesList.selectedValue != null
        loadFavoriteButton.isEnabled = hasSelection
        removeFavoriteButton.isEnabled = hasSelection
    }

    private fun updateTempButtonsState() {
        val hasSelection = tempList.selectedValue != null
        loadTempButton.isEnabled = hasSelection
        removeTempButton.isEnabled = hasSelection
    }

    private fun updateSaveButtonState() {
        val currentContent = plotSpecTextArea.text
        val favoriteName = if (editorSpecSource.kind == EditorSpecSourceKind.FAVORITE) editorSpecSource.id else null
        if (favoriteName == null) {
            saveFavoriteButton.isEnabled = currentContent.isNotBlank()
            return
        }
        saveFavoriteButton.isEnabled = favoritesStore[favoriteName] != currentContent
    }

    private fun setEditorSpecSource(source: EditorSpecSource, selectStorageTab: Boolean = false) {
        editorSpecSource = source
        when (source.kind) {
            EditorSpecSourceKind.FAVORITE -> {
                val favoriteName = source.id
                if (favoriteName != null && favoritesListModel.contains(favoriteName)) {
                    favoritesList.setSelectedValue(favoriteName, true)
                } else {
                    favoritesList.clearSelection()
                }
                tempList.clearSelection()
                if (selectStorageTab) {
                    storageTabbedPane.selectedIndex = 0
                }
            }
            EditorSpecSourceKind.TEMP -> {
                val tempId = source.id
                favoritesList.clearSelection()
                if (tempId != null && tempListModel.contains(tempId)) {
                    tempList.setSelectedValue(tempId, true)
                } else {
                    tempList.clearSelection()
                }
                if (selectStorageTab) {
                    storageTabbedPane.selectedIndex = 1
                }
            }
            EditorSpecSourceKind.DIRECT -> {
                favoritesList.clearSelection()
                tempList.clearSelection()
            }
        }
        updateFavoriteButtonsState()
        updateTempButtonsState()
        updateSaveButtonState()
    }

    private fun saveTempStore() {
        try {
            tempStore.save()
        } catch (e: IOException) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error saving temp specs: ${e.message}",
                "Temp Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun registerPastedSpec(spec: String, resetUndoHistory: Boolean) {
        val tempEntry = tempStore.add(spec)
        saveTempStore()
        setEditorSpecSource(EditorSpecSource(EditorSpecSourceKind.TEMP, tempEntry.id), selectStorageTab = true)
        updateTempList()
        if (resetUndoHistory) {
            isProgrammaticSpecChange = true
            try {
                plotSpecTextArea.text = spec
                plotSpecTextArea.caretPosition = 0
            } finally {
                isProgrammaticSpecChange = false
            }
            undoManager.discardAllEdits()
            updateUndoRedoState()
        }
    }

    private fun updateCurrentTempSpec() {
        val tempId = (if (editorSpecSource.kind == EditorSpecSourceKind.TEMP) editorSpecSource.id else null) ?: return
        tempStore.update(tempId, plotSpecTextArea.text) ?: return
        saveTempStore()
        tempList.repaint()
    }

    private fun handleEditorTextChanged() {
        if (isProgrammaticSpecChange || isPasteInProgress) return
        if (editorSpecSource.kind == EditorSpecSourceKind.TEMP) {
            updateCurrentTempSpec()
        }
        updateSaveButtonState()
    }

    private fun schedulePreviewCapture(specKey: String) {
        pendingPreviewTimer?.stop()
        pendingPreviewTimer = Timer(500) {
            capturePreview(specKey)
        }.apply {
            isRepeats = false
            start()
        }
    }

    private fun capturePreview(specKey: String) {
        val w = plotPanel.width
        val h = plotPanel.height
        if (w <= 0 || h <= 0) return
        try {
            val image = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
            val g = image.createGraphics()
            try {
                plotPanel.paint(g)
            } finally {
                g.dispose()
            }
            PreviewCache.save(specKey, image)
            favoritesList.repaint()
            tempList.repaint()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSelectedFavorite() {
        val selectedName = favoritesList.selectedValue ?: return
        val spec = favoritesStore[selectedName] ?: return
        setSpec(spec, EditorSpecSource(EditorSpecSourceKind.FAVORITE, selectedName), selectStorageTab = true)
        evaluate()
    }

    private fun loadSelectedTemp() {
        val selectedId = tempList.selectedValue ?: return
        val spec = tempStore[selectedId]?.spec ?: return
        setSpec(spec, EditorSpecSource(EditorSpecSourceKind.TEMP, selectedId), selectStorageTab = true)
        evaluate()
    }

    private fun setupFavorites() {
        favoritesStore.load()
        tempStore.load()

        saveFavoriteButton.addActionListener {
            val initialName = when (editorSpecSource.kind) {
                EditorSpecSourceKind.FAVORITE -> editorSpecSource.id
                else -> null
            }
            val name = JOptionPane.showInputDialog(
                this,
                "Enter a name for the favorite:",
                "Save Favorite",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initialName ?: favoritesList.selectedValue ?: ""
            ) as? String
            if (!name.isNullOrBlank()) {
                if (name in favoritesStore && favoritesStore[name] != plotSpecTextArea.text) {
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
                favoritesStore[name] = plotSpecTextArea.text
                saveFavorites()
                setEditorSpecSource(EditorSpecSource(EditorSpecSourceKind.FAVORITE, name), selectStorageTab = true)
                updateFavoritesList()
            }
        }

        loadFavoriteButton.addActionListener {
            loadSelectedFavorite()
        }

        removeFavoriteButton.addActionListener {
            val selectedName = favoritesList.selectedValue
            if (selectedName != null) {
                val confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove '$selectedName'?",
                    "Remove Favorite",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirm == JOptionPane.YES_OPTION) {
                    favoritesStore.remove(selectedName)
                    saveFavorites()
                    if (editorSpecSource.kind == EditorSpecSourceKind.FAVORITE && editorSpecSource.id == selectedName) {
                        setEditorSpecSource(EditorSpecSource(EditorSpecSourceKind.DIRECT))
                    }
                    updateFavoritesList()
                }
            }
        }

        loadTempButton.addActionListener {
            loadSelectedTemp()
        }

        removeTempButton.addActionListener {
            val selectedId = tempList.selectedValue ?: return@addActionListener
            tempStore.remove(selectedId)
            saveTempStore()
            if (editorSpecSource.kind == EditorSpecSourceKind.TEMP && editorSpecSource.id == selectedId) {
                setEditorSpecSource(EditorSpecSource(EditorSpecSourceKind.DIRECT))
            }
            updateTempList()
        }

        favoritesList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                updateFavoriteButtonsState()
                updateSaveButtonState()
            }
        }

        tempList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                updateTempButtonsState()
            }
        }

        favoritesList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val idx = favoritesList.locationToIndex(e.point)
                    if (idx >= 0 && favoritesList.getCellBounds(idx, idx)?.contains(e.point) == true) {
                        favoritesList.selectedIndex = idx
                        loadSelectedFavorite()
                    }
                }
            }

            override fun mouseExited(e: MouseEvent) {
                favoritesHoverRowIndex = -1
                previewPopup.hide()
            }
        })

        tempList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val idx = tempList.locationToIndex(e.point)
                    if (idx >= 0 && tempList.getCellBounds(idx, idx)?.contains(e.point) == true) {
                        tempList.selectedIndex = idx
                        loadSelectedTemp()
                    }
                }
            }

            override fun mouseExited(e: MouseEvent) {
                tempHoverRowIndex = -1
                previewPopup.hide()
            }
        })

        favoritesList.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val idx = favoritesList.locationToIndex(e.point)
                val bounds = if (idx >= 0) favoritesList.getCellBounds(idx, idx) else null
                // Only trigger when the cursor is over the thumbnail column at the left of a cell.
                // +4 absorbs the JLabel's left border insets so the trigger feels natural.
                val overThumbnail = idx >= 0
                        && bounds != null
                        && bounds.contains(e.point)
                        && (e.point.x - bounds.x) <= PreviewCache.THUMB_WIDTH + 4

                if (!overThumbnail) {
                    if (favoritesHoverRowIndex != -1) {
                        favoritesHoverRowIndex = -1
                        previewPopup.hide()
                    }
                    return
                }
                if (idx == favoritesHoverRowIndex) return
                favoritesHoverRowIndex = idx

                val name = favoritesListModel.getElementAt(idx)
                val full = favoritesStore[name]?.let { PreviewCache.loadFull(it) }
                if (full != null) {
                    previewPopup.show(full, favoritesList, bounds)
                } else {
                    previewPopup.hide()
                }
            }
        })

        tempList.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val idx = tempList.locationToIndex(e.point)
                val bounds = if (idx >= 0) tempList.getCellBounds(idx, idx) else null
                val overThumbnail = idx >= 0
                        && bounds != null
                        && bounds.contains(e.point)
                        && (e.point.x - bounds.x) <= PreviewCache.THUMB_WIDTH + 4

                if (!overThumbnail) {
                    if (tempHoverRowIndex != -1) {
                        tempHoverRowIndex = -1
                        previewPopup.hide()
                    }
                    return
                }
                if (idx == tempHoverRowIndex) return
                tempHoverRowIndex = idx

                val id = tempListModel.getElementAt(idx)
                val full = tempStore[id]?.spec?.let { PreviewCache.loadFull(it) }
                if (full != null) {
                    previewPopup.show(full, tempList, bounds)
                } else {
                    previewPopup.hide()
                }
            }
        })

        plotSpecTextArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                handleEditorTextChanged()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                handleEditorTextChanged()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                handleEditorTextChanged()
            }
        })
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
                isProgrammaticSpecChange = true
                try {
                    plotSpecTextArea.text = clipboardText
                    plotSpecTextArea.caretPosition = 0
                } finally {
                    isProgrammaticSpecChange = false
                }
                registerPastedSpec(clipboardText, resetUndoHistory = true)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun setSpec(spec: String) {
        setSpec(spec, EditorSpecSource(EditorSpecSourceKind.DIRECT))
    }

    private fun setSpec(
        spec: String,
        source: EditorSpecSource,
        selectStorageTab: Boolean = false
    ) {
        isProgrammaticSpecChange = true
        try {
            plotSpecTextArea.text = spec
            plotSpecTextArea.caretPosition = 0
        } finally {
            isProgrammaticSpecChange = false
        }
        setEditorSpecSource(source, selectStorageTab = selectStorageTab)
        undoManager.discardAllEdits()
        updateUndoRedoState()
    }

    fun evaluate() {
        densityDebouncer.stop()
        SpecStore.save(plotSpecTextArea.text)

        messagesTextArea.text = ""
        messagesScrollPane.isVisible = false
        plotPanel.removeAll()
        plotPanel.revalidate()
        plotPanel.repaint() // Clear immediately so user sees something is happening

        val specKey = plotSpecTextArea.text
        val rawText = specKey.trim()
        val isJson = rawText.startsWith("{")

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
                        val plotCanvasDrawable = PlotCanvasDrawable()
                        plotCanvasDrawable.onHrefClick { Desktop.getDesktop().browse(java.net.URI.create(it)) }
                        plotCanvasDrawable.update(
                            processedSpec,
                            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio = false),
                            computationMessagesHandler = messageHandler
                        )
                        newPlotComponent = CanvasComponent(plotCanvasDrawable)
                    }
                    "DefaultPlotPanelCanvas" -> {
                        processedSpec = MonolithicCommon.processRawSpecs(specMap as MutableMap<String, Any>)
                        newPlotComponent = SwingPlotPanel(
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
                else if (newPlotComponent is SwingPlotPanel) sharedToolbar.attach(newPlotComponent.figureModel)

                plotPanel.add(newPlotComponent, BorderLayout.CENTER)
                plotPanel.revalidate()
                plotPanel.repaint()

                schedulePreviewCapture(specKey)
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
            val pythonPath = PythonConfigStore.getPythonPath() ?: "python"

            cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
            evaluateButton.isEnabled = false

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
}

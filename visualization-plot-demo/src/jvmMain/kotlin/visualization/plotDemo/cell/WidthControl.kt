package jetbrains.datalore.visualization.plotDemo.cell

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class WidthControl(initialWidth: Double) : JPanel() {

    private val value: Property<Double> = ValueProperty(initialWidth)

    val width: ReadableProperty<Double>
        get() = value

    init {
        val fastBackButton = JButton("<<")
        val backButton = JButton("<")
        val fwdButton = JButton(">")
        val fastFwdButton = JButton(">>")
        val textField = JTextField(initialWidth.toString(), 5)

        fastBackButton.addActionListener {
            value.set(Math.max(0.0, value.get() - FAST_STEP))
        }
        backButton.addActionListener {
            value.set(Math.max(0.0, value.get() - STEP))
        }
        fwdButton.addActionListener {
            value.set(value.get() + STEP)
        }
        fastFwdButton.addActionListener {
            value.set(value.get() + FAST_STEP)
        }

        textField.document.addDocumentListener(object : DocumentListener {
            private var lastText = textField.text

            override fun changedUpdate(e: DocumentEvent) {
                SwingUtilities.invokeLater {
                    if (textField.text != lastText) {
                        lastText = textField.text
                        value.set(lastText.toDouble())
                    }
                }
            }

            override fun insertUpdate(e: DocumentEvent) {
                changedUpdate(e)
            }

            override fun removeUpdate(e: DocumentEvent) {
                changedUpdate(e)
            }
        })


        add(fastBackButton)
        add(backButton)
        add(textField)
        add(fwdButton)
        add(fastFwdButton)

        value.addHandler(object : EventHandler<PropertyChangeEvent<out Double>> {
            override fun onEvent(event: PropertyChangeEvent<out Double>) {
                textField.text = event.newValue.toString()
            }
        })
    }


    companion object {
        private const val FAST_STEP = 100
        private const val STEP = 10
    }
}
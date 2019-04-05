package demo

import java.awt.FlowLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun main() {
    println("Hello!")
    SwingUtilities.invokeLater {
        val frame = JFrame("Platform demo AWT")
        frame.layout = FlowLayout()
        frame.contentPane.add(JLabel(PlatformObject.name))
        frame.contentPane.add(JLabel(PlatformClass().getName()))
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(300, 200)
        frame.isVisible = true
        println(frame.layout)
    }
}
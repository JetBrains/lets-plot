/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel

class PlotToolbar : JPanel() {
    
    init {
        layout = FlowLayout(FlowLayout.CENTER)
        preferredSize = java.awt.Dimension(preferredSize.width, 33)
        minimumSize = java.awt.Dimension(minimumSize.width, 33)
        maximumSize = java.awt.Dimension(maximumSize.width, 33)
        
        val zoomButton = JButton("Zoom").apply {
            addActionListener { 
                println("Zoom button clicked")
            }
        }
        
        val resetButton = JButton("Reset").apply {
            addActionListener {
                println("Reset button clicked") 
            }
        }
        
        add(zoomButton)
        add(resetButton)
    }
}
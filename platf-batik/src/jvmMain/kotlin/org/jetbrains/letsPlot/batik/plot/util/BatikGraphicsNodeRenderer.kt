/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.util

import org.apache.batik.bridge.TextNode
import org.apache.batik.gvt.CompositeGraphicsNode
import org.apache.batik.gvt.GraphicsNode
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import javax.swing.JLabel

interface BatikGraphicsNodeRenderer {
    val priority: Int
    fun paint(node: GraphicsNode, g: Graphics2D, size: Dimension)

    object Default : BatikGraphicsNodeRenderer {
        override val priority: Int
            get() = 0

        override fun paint(node: GraphicsNode, g: Graphics2D, size: Dimension) {
            fun getPosition(graphicsNode: GraphicsNode): Pair<Double, Double> {
                val bounds: Rectangle2D = graphicsNode.bounds ?: return Pair(0.0, 0.0)
                val transform: AffineTransform? = graphicsNode.transform
                var position: Point2D = Point2D.Double(bounds.x, bounds.y)
                if (transform != null) {
                    position = transform.transform(position, null)
                }
                return Pair(position.x, position.y)
            }

            fun extractText(input: String): List<String> {
                val pattern = """\\\(.*?\\\)(?!\\)""".toRegex()
                return pattern.findAll(input)
                    .map { it.value.removePrefix("\\(").removeSuffix("\\)") }
                    .toList()
            }

            fun processSubnodes(parentNode: CompositeGraphicsNode) {
                for (child in parentNode.children) {
                    when (child) {
                        is TextNode -> {
                            val text = child.text
                            if ("\\(" in text) {
                                val formula = extractText(child.text).firstOrNull()
                                val (x, y) = getPosition(parentNode)
                                val newChild = TextNode()
                                newChild.location = child.location
                                parentNode.remove(child)
                                parentNode.add(newChild)
                                val icon = TeXFormula(formula).createTeXIcon(TeXFormula.SERIF, 20f)
                                icon.paintIcon(JLabel(), g, x.toInt(), y.toInt() + 15)
                            }
                        }
                        is CompositeGraphicsNode -> {
                            processSubnodes(child)
                        }
                        else -> continue
                    }
                }
            }

            node.paint(g)
            if (node is CompositeGraphicsNode) {
                processSubnodes(node)
            }
        }
    }

    companion object {
        fun getInstance(): BatikGraphicsNodeRenderer {
            val instances = ServiceLoaderHelper.loadInstances<BatikGraphicsNodeRenderer>()
            return instances.maxByOrNull { it.priority } ?: Default
        }
    }
}

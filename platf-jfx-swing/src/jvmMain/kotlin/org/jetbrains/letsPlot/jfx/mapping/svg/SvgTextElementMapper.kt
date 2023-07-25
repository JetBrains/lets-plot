/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.SimpleCollectionProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_STYLE_ATTRIBUTE
import org.jetbrains.letsPlot.jfx.mapping.svg.attr.SvgTextElementAttrMapping
import org.jetbrains.letsPlot.datamodel.svg.dom.*

internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: Text,
    peer: SvgJfxPeer
) : SvgElementMapper<SvgTextElement, Text>(source, target, peer) {

    private val myTextAttrSupport = TextAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
//        println("text -> $name = $value")
//        println("text -> ${target.font}")
//        val def = Font.getDefault()
//        println("text -> ${def.family}")
//        val font = Font.font(def.family, FontPosture.ITALIC, def.size)
//        val font = Font.font("Times New Roman", FontPosture.ITALIC, def.size)
//        target.font = font
//        println("text -> ${target.font}")
//        Font.getFamilies().forEach { println(it) }
//        Font.getFontNames().forEach { println(it) }
//        throw RuntimeException("The End")


        myTextAttrSupport.setAttribute(name, value)
    }

    override fun applyStyle() {
        setFontProperties(target, peer.styleSheet)
    }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        // Sync TextNodes, TextSpans
        val sourceTextProperty = sourceTextProperty(source.children())
//        sourceTextProperty.addHandler(object : EventHandler<PropertyChangeEvent<out String>> {
//            override fun onEvent(event: PropertyChangeEvent<out String>) {
//                println("new text: ${event.newValue}")
//            }
//        })
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextProperty,
                targetTextProperty(target)
            )
        )
    }

    private fun setFontProperties(target: Text, styleSheet: StyleSheet?) {
        if (styleSheet == null) {
            return
        }
        val className = source.fullClass()
        if (className.isNotEmpty()) {
            val style = styleSheet.getTextStyle(className)
            target.font = style.createFont()
            myTextAttrSupport.setAttribute(SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }

    companion object {
        private fun sourceTextProperty(nodes: ObservableCollection<SvgNode>): ReadableProperty<String> {
            return object : SimpleCollectionProperty<SvgNode, String>(nodes, joinToString(nodes)) {
                override val propExpr = "joinToString($collection)"
                override fun doGet() = joinToString(collection)
            }
        }

        private fun joinToString(nodes: ObservableCollection<SvgNode>): String {
            return nodes.asSequence()
                .flatMap{((it as? SvgTSpanElement)?.children() ?: listOf(it as SvgTextNode)).asSequence()}
                .joinToString ("\n") { (it as SvgTextNode).textContent().get() }
        }

        private fun targetTextProperty(target: Text): WritableProperty<String?> {
            return object : WritableProperty<String?> {
                override fun set(value: String?) {
                    target.text = value ?: "n/a"
                }
            }
        }

        private fun TextStyle.createFont(): Font {
            val posture = if (face.italic) FontPosture.ITALIC else null
            val weight = if (face.bold) FontWeight.BOLD else null
            // todo Need to choose an available font:
            //   'TextStyle.family' string may contain a comma-separated list of families
            val familyList = family.toString().split(",").map { it.trim(' ', '\"') }
            return familyList
                .map { Font.font(it, weight, posture, size) }
                .firstOrNull {
                    // todo choose a font with a supported style ('bold' or/and 'italic')
                    (if (face.italic) it.style.contains("italic", ignoreCase = true) else true) &&
                            (if (face.bold) it.style.contains("bold", ignoreCase = true) else true)
                }
                ?: Font.font(
                    familyList.firstOrNull(),
                    weight,
                    posture,
                    size
                )
        }
    }

    private class TextAttributesSupport(val target: Text) {
        private var mySvgTextAnchor: String? = null

        init {
            @Suppress("ObjectLiteralToLambda")
            target.boundsInLocalProperty().addListener(object : ChangeListener<Bounds> {
                override fun changed(observable: ObservableValue<out Bounds>?, oldValue: Bounds?, newValue: Bounds?) {
                    SvgTextElementAttrMapping.revalidatePositionAttributes(mySvgTextAnchor, target)
                }
            })
        }

        fun setAttribute(name: String, value: Any?) {
            if (name == SvgTextContent.TEXT_ANCHOR.name) {
                mySvgTextAnchor = value as String?
            }
            SvgTextElementAttrMapping.setAttribute(target, name, value)
        }
    }
}
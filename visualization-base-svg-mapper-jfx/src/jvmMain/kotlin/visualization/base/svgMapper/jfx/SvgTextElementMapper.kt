package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.text.Text
import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.SimpleCollectionProperty
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svgMapper.jfx.attr.SvgTextElementAttrMapping

internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: Text,
    peer: SvgAwtPeer
) : SvgElementMapper<SvgTextElement, Text>(source, target, peer) {

    private val myTextAttrSupport = TextAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
        myTextAttrSupport.setAttribute(name, value)
    }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        // Sync TextNodes, TextSpans
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextProperty(source.children()),
                targetTextProperty(target)
            )
        )
    }

    companion object {
        private fun sourceTextProperty(nodes: ObservableCollection<SvgNode>): ReadableProperty<String> {
            return object : SimpleCollectionProperty<SvgNode, String>(nodes, joinToString(nodes)) {

                override val propExpr: String
                    get() = "joinToString($collection)"

                override fun doGet(): String {
                    return joinToString(collection)
                }
            }
        }

        private fun joinToString(nodes: ObservableCollection<SvgNode>): String {
            return nodes
                .map {
                    (it as? SvgTSpanElement)?.children() ?: listOf(it as SvgTextNode)
                }
                .flatMap {
                    @Suppress("UNCHECKED_CAST")
                    it as List<SvgTextNode>
                }
                .map {
                    it.textContent().get()
                }
                .joinToString("/n")
        }

        private fun targetTextProperty(target: Text): WritableProperty<String?> {
            return object : WritableProperty<String?> {
                override fun set(value: String?) {
                    target.text = value ?: "n/a"
                }
            }
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
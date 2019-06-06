package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.text.Text
import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.SimpleCollectionProperty
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgTSpanElement
import jetbrains.datalore.visualization.base.svg.SvgTextElement
import jetbrains.datalore.visualization.base.svg.SvgTextNode

internal class SvgTextElementMapper(
        source: SvgTextElement,
        target: Text,
        peer: SvgAwtPeer) : SvgElementMapper<SvgTextElement, Text>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)
//        val targetList = Utils.elementChildren(target)

        // Sync TextNodes, TextSpans
        conf.add(Synchronizers.forPropsOneWay(
                sourceTextProperty(source.children()),
                targetTextProperty(target)
        ))
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
}
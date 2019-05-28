package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgNodeBufferUtil
import org.w3c.dom.Element
import org.w3c.dom.Node

class SvgNodeSubtreeBufferGeneratingSynchronizer(private val mySource: SvgNode, private val myTarget: Node): Synchronizer {

    companion object {
        private fun setInnerHtml(e: Element, html: String) {
            e.innerHTML = html
        }
    }

    override fun attach(ctx: SynchronizerContext) {
        val innerHtml = StringBuilder()
        for (sourceNode in mySource.children()) {
            innerHtml.append(SvgNodeBufferUtil.generateSvgNodeBuffer(sourceNode))
        }
        setInnerHtml(myTarget as Element, innerHtml.toString())
    }

    override fun detach() {
        if (myTarget.hasChildNodes()) {
            var child = myTarget.firstChild
            while (child != null) {
                val nextSibling = child.nextSibling
                myTarget.removeChild(child)
                child = nextSibling
            }
        }
    }
}
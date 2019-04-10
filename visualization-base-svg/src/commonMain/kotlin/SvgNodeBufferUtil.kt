package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.visualization.base.svg.slim.WithTextGen

object SvgNodeBufferUtil {

    fun generateSvgNodeBuffer(source: SvgNode): StringBuilder {
        if (source is WithTextGen) {
            val sb = StringBuilder()
            (source as WithTextGen).appendTo(sb)
            return sb
        } else if (source is SvgElement) {
            return generateSvgElementBuffer(source as SvgElement)
        } else if (source is SvgTextNode) {
            return StringBuilder((source as SvgTextNode).textContent().get())
        }

        throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
    }

    private fun generateSvgElementBuffer(source: SvgElement): StringBuilder {
        // head
        val elementName = source.elementName
        val sb = StringBuilder()
        sb.append('<').append(elementName)
        for (key in source.attributeKeys) {
            sb.append(' ').append(key).append("=\"").append(source.getAttribute<Property<*>>(key.name).get()).append('\"')
        }
        sb.append('>')

        // content
        for (child in source.children()) {
            sb.append(generateSvgNodeBuffer(child))
        }

        // foot
        sb.append("</").append(elementName).append('>')
        return sb
    }
}

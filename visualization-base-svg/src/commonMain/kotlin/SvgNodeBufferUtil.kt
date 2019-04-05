package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.visualization.base.svg.slim.WithTextGen

object SvgNodeBufferUtil {

    fun generateSvgNodeBuffer(source: SvgNode): StringBuffer {
        if (source is WithTextGen) {
            val sb = StringBuffer()
            (source as WithTextGen).appendTo(sb)
            return sb
        } else if (source is SvgElement) {
            return generateSvgElementBuffer(source as SvgElement)
        } else if (source is SvgTextNode) {
            return StringBuffer((source as SvgTextNode).textContent().get())
        }

        throw IllegalStateException("Can't generate dom for svg node " + source.getClass().getSimpleName())
    }

    private fun generateSvgElementBuffer(source: SvgElement): StringBuffer {
        // head
        val elementName = source.getElementName()
        val sb = StringBuffer()
        sb.append('<').append(elementName)
        for (key in source.getAttributeKeys()) {
            sb.append(' ').append(key).append("=\"").append(source.getAttribute(key.getName()).get()).append('\"')
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

package jetbrains.datalore.visualization.base.svg

class SvgStyleElement(val resource: SvgCssResource) : SvgElement() {

    override val elementName = "style"

    init {
        setContent(resource.css())
    }

    fun setContent(content: String) {
        val children = children()
        while (!children.isEmpty()) {
            children.removeAt(0)
        }
        val textNode = SvgTextNode(content)
        children.add(textNode)
        setAttribute("type", "text/css")
    }
}
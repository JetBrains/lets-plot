package jetbrains.datalore.visualization.base.svg

class SvgStyleElement : SvgElement {

    override val elementName = "style"

    constructor()

    constructor(content: String) : this() {

        setContent(content)
    }

    constructor(resource: SvgCssResource) : this(resource.css())

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
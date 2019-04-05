package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.collections.list.ObservableList

class SvgStyleElement : SvgElement {

    val elementName: String
        get() = "style"

    constructor() {}

    constructor(content: String) : this() {

        setContent(content)
    }

    constructor(resource: SvgCssResource) : this(resource.css()) {}

    fun setContent(content: String) {
        val children = children()
        while (!children.isEmpty()) {
            children.remove(0)
        }
        val textNode = SvgTextNode(content)
        children.add(textNode)
        setAttribute("type", "text/css")
    }
}
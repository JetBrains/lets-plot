package jetbrains.datalore.base.domCore.dom

object DomApi {

    val body: DomHTMLElement?
        get() = DomHTMLElement.create(document.body)

    val document: DomDocument
        get() = DomWindow.getDocument()

    val active: DomElement?
        get() = DomElement.create(document.activeElement)

    fun createDiv(): DomElement {
        return createElement("div")
    }

    fun createSpan(): DomElement {
        return createElement("span")
    }

    fun createList(): DomElement {
        return createElement("ul")
    }

    fun createLi(): DomElement {
        return createElement("li")
    }

    fun createAnchor(): DomElement {
        return createElement("a")
    }

    fun createButton(): DomElement {
        return createElement("button")
    }

    fun createTR(): DomElement {
        return createElement("tr")
    }

    fun createTH(): DomElement {
        return createElement("th")
    }

    fun createTD(): DomElement {
        return createElement("td")
    }

    fun createHR(): DomElement {
        return createElement("hr")
    }

    fun createItalic(): DomElement {
        return createElement("i")
    }

    fun createForm(): DomElement {
        return createElement("form")
    }

    fun createCanvas(): DomHTMLCanvasElement {
        return createElement("canvas").cast()
    }

    fun createInput(type: String): InputDomElement {
        val input: InputDomElement = createElement("input").cast()
        input.setAttribute("type", type)
        return input
    }

    fun createElement(tag: String): DomElement {
        return DomElement.create(DomWindow.getDocument().createElement(tag))!!
    }

    fun createInputText(): InputDomElement {
        return createInput("text")
    }

    fun createCheckbox(): InputDomElement {
        return createInput("checkbox")
    }

    fun createRadio(): InputDomElement {
        return createInput("radio").cast()
    }

    fun createTextArea(): TextAreaDomElement {
        return createElement("textarea").cast()
    }

    fun createSelect(): DomElement {
        return createElement("select")
    }

    fun createOption(): OptionDomElement {
        return createElement("option").cast()
    }

    fun createImage(): ImageDomElement {
        return createElement("img").cast()
    }

    fun createBR(): DomElement {
        return createElement("br")
    }

    fun createTextNode(data: String): DomCharacterData {
        return document.createTextNode(data)
    }

    fun assign(url: String) {
        DomWindow.getWindow().location.assign(url)
    }
}

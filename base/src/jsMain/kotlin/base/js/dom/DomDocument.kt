package jetbrains.datalore.base.js.dom

import org.w3c.dom.Document

typealias DomDocument = Document

val DomDocument.clientWidth: Int
    get() = body!!.clientWidth

val DomDocument.clientHeight: Int
    get() = body!!.clientHeight

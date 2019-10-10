package jetbrains.datalore.vis.svgMapper.dom.domExtensions

import org.w3c.dom.css.CSSStyleDeclaration

fun CSSStyleDeclaration.clearProperty(name: String): CSSStyleDeclaration {
    removeProperty(name)
    return this
}

fun CSSStyleDeclaration.clearDisplay(): CSSStyleDeclaration = clearProperty("display")
package jetbrains.datalore.base.js.css

import jetbrains.datalore.base.js.css.enumerables.*
import org.w3c.dom.css.CSSStyleDeclaration

typealias StyleMap = CSSStyleDeclaration

val StyleMap.cssPosition: CssPosition?
    get() = CssPosition.parse(getProperty("position"))

val StyleMap.cssOverflow: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow"))

val StyleMap.cssOverflowX: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow-x"))

val StyleMap.cssOverflowY: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow-y"))

fun StyleMap.setWidth(width: Int, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("width", width, unit)
}

fun StyleMap.clearWidth(): StyleMap {
    return clearProperty("width")
}

fun StyleMap.setMinWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("min-width", width, unit)
}

fun StyleMap.setMaxWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("max-width", width, unit)
}

fun StyleMap.setHeight(height: Int, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("height", height, unit)
}

fun StyleMap.clearHeight(): StyleMap {
    return clearProperty("height")
}

fun StyleMap.setMinHeight(height: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("min-height", height, unit)
}

fun StyleMap.setMaxHeight(height: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("max-height", height, unit)
}

fun StyleMap.clearMaxHeight(): StyleMap {
    return clearProperty("max-height")
}

fun StyleMap.setMargin(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    setMarginLeft(margin, unit)
    setMarginRight(margin, unit)
    setMarginTop(margin, unit)
    setMarginBottom(margin, unit)
    return this
}

fun StyleMap.setMarginLeft(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("margin-left", margin, unit)
}

fun StyleMap.setMarginRight(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("margin-right", margin, unit)
}

fun StyleMap.setMarginTop(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("margin-top", margin, unit)
}

fun StyleMap.clearMarginTop(): StyleMap {
    return clearProperty("margin-top")
}

fun StyleMap.setMarginBottom(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("margin-bottom", margin, unit)
}

fun StyleMap.clearMarginBottom(): StyleMap {
    return clearProperty("margin-bottom")
}

fun StyleMap.setPadding(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    setPaddingLeft(padding, unit)
    setPaddingRight(padding, unit)
    setPaddingTop(padding, unit)
    setPaddingBottom(padding, unit)
    return this
}

fun StyleMap.setPaddingLeft(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("padding-left", padding, unit)
}

fun StyleMap.setPaddingRight(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("padding-right", padding, unit)
}

fun StyleMap.setPaddingTop(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("padding-top", padding, unit)
}

fun StyleMap.setPaddingBottom(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("padding-bottom", padding, unit)
}

fun StyleMap.setBorder(border: String): StyleMap {
    return doSetProperty("border", border)
}

fun StyleMap.setBorderStyle(value: CssBorderStyle): StyleMap {
    return doSetProperty("border-style", value)
}

fun StyleMap.clearBorder(): StyleMap {
    return clearProperty("border")
}

fun StyleMap.clearBorderWidth(): StyleMap {
    return clearProperty("border-width")
}

fun StyleMap.setBorderLeft(border: String): StyleMap {
    return doSetProperty("border-left", border)
}

fun StyleMap.clearBorderLeft(): StyleMap {
    return clearProperty("border-left")
}

fun StyleMap.setBorderRight(border: String): StyleMap {
    return doSetProperty("border-right", border)
}

fun StyleMap.clearBorderRight(): StyleMap {
    return clearProperty("border-right")
}

fun StyleMap.setBorderTop(border: String): StyleMap {
    return doSetProperty("border-top", border)
}

fun StyleMap.clearBorderTop(): StyleMap {
    return clearProperty("border-top")
}

fun StyleMap.setBorderBottom(border: String): StyleMap {
    return doSetProperty("border-bottom", border)
}

fun StyleMap.clearBorderBottom(): StyleMap {
    return clearProperty("border-bottom")
}

fun StyleMap.setBorderColor(color: String): StyleMap {
    return doSetProperty("border-color", color)
}

fun StyleMap.setBorderLeftColor(color: String): StyleMap {
    return doSetProperty("border-left-color", color)
}

fun StyleMap.setBorderRightColor(color: String): StyleMap {
    return doSetProperty("border-right-color", color)
}

fun StyleMap.setBorderTopColor(color: String): StyleMap {
    return doSetProperty("border-top-color", color)
}

fun StyleMap.setBorderBottomColor(color: String): StyleMap {
    return doSetProperty("border-bottom-color", color)
}

fun StyleMap.clearBorderLeftColor(): StyleMap {
    return clearProperty("border-left-color")
}

fun StyleMap.clearBorderRightColor(): StyleMap {
    return clearProperty("border-right-color")
}

fun StyleMap.clearBorderTopColor(): StyleMap {
    return clearProperty("border-top-color")
}

fun StyleMap.clearBorderBottomColor(): StyleMap {
    return clearProperty("border-bottom-color")
}

fun StyleMap.setTop(top: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("top", top, unit)
}

fun StyleMap.clearTop(): StyleMap {
    return clearProperty("top")
}

fun StyleMap.setBottom(bottom: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("bottom", bottom, unit)
}

fun StyleMap.clearBottom(): StyleMap {
    return clearProperty("bottom")
}

fun StyleMap.setRight(right: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("right", right, unit)
}

fun StyleMap.clearRight(): StyleMap {
    return clearProperty("right")
}

fun StyleMap.setLeft(left: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("left", left, unit)
}

fun StyleMap.clearLeft(): StyleMap {
    return clearProperty("left")
}

fun StyleMap.setOpacity(opacity: Double): StyleMap {
    return doSetProperty("opacity", opacity.toString())
}

fun StyleMap.clearOpacity(): StyleMap {
    return clearProperty("opacity")
}

fun StyleMap.setZIndex(zIndex: Int): StyleMap {
    return doSetProperty("z-index", zIndex.toString())
}

fun StyleMap.setDisplay(display: CssDisplay): StyleMap {
    return doSetProperty("display", display)
}

fun StyleMap.clearDisplay(): StyleMap {
    return clearProperty("display")
}

fun StyleMap.setPosition(position: CssPosition): StyleMap {
    return doSetProperty("position", position)
}

fun StyleMap.setVisibility(visibility: CssVisibility): StyleMap {
    return doSetProperty("visibility", visibility)
}

fun StyleMap.setBackgroundColor(color: String): StyleMap {
    return doSetProperty("background-color", color)
}

fun StyleMap.clearBackgroundColor(): StyleMap {
    return clearProperty("background-color")
}

fun StyleMap.setBackgroundImage(image: String): StyleMap {
    return doSetProperty("background-image", image)
}

fun StyleMap.setBackgroundSize(size: String): StyleMap {
    return doSetProperty("background-size", size)
}

fun StyleMap.setColor(color: String): StyleMap {
    return doSetProperty("color", color)
}

fun StyleMap.setFont(value: String): StyleMap {
    return doSetProperty("font", value)
}

fun StyleMap.setFontFamily(value: String): StyleMap {
    return doSetProperty("font-family", value)
}

fun StyleMap.setFontStyle(value: CssFontStyle): StyleMap {
    return doSetProperty("font-style", value)
}

fun StyleMap.setFontWeight(value: CssFontWeight): StyleMap {
    return doSetProperty("font-weight", value)
}

fun StyleMap.setFontSize(value: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("font-size", value, unit)
}

fun StyleMap.setTextDecoration(value: String): StyleMap {
    return doSetProperty("text-decoration", value)
}

fun StyleMap.clearTextDecoration(): StyleMap {
    return clearProperty("text-decoration")
}

fun StyleMap.setTextDecorationColor(value: String): StyleMap {
    return doSetProperty("text-decoration-color", value)
}

fun StyleMap.setUserSelect(value: String): StyleMap {
    return doSetProperty("user-select", value)
}

fun StyleMap.setTransform(transform: String): StyleMap {
    return doSetProperty("transform", transform)
}

fun StyleMap.clearTransform(): StyleMap {
    return clearProperty("transform")
}

fun StyleMap.setTransition(transition: String): StyleMap {
    return doSetProperty("transition", transition)
}

fun StyleMap.clearTransition(): StyleMap {
    return clearProperty("transition")
}

fun StyleMap.setCursor(cursor: CssCursor): StyleMap {
    return doSetProperty("cursor", cursor)
}

fun StyleMap.setPointerEvents(pointerEvents: CssPointerEvents): StyleMap {
    return doSetProperty("pointer-events", pointerEvents)
}

fun StyleMap.clearPointerEvents(): StyleMap {
    return clearProperty("pointer-events")
}

fun StyleMap.setGridTemplateColumns(template: String): StyleMap {
    return doSetProperty("grid-template-columns", template)
}

fun StyleMap.setGridRow(gridRow: String): StyleMap {
    return doSetProperty("grid-row", gridRow)
}

fun StyleMap.setGridColumn(gridColumn: String): StyleMap {
    return doSetProperty("grid-column", gridColumn)
}

fun StyleMap.setAlignItems(alignItems: CssAlignItem): StyleMap {
    return doSetProperty("align-items", alignItems)
}

fun StyleMap.clearAlignItems(): StyleMap {
    return clearProperty("align-items")
}

fun StyleMap.setSelfAlignment(alignItems: CssAlignItem): StyleMap {
    return doSetProperty("self-alignment", alignItems)
}

fun StyleMap.clearSelfAlignment(): StyleMap {
    return clearProperty("self-alignment")
}

fun StyleMap.setOutline(outline: String): StyleMap {
    return doSetProperty("outline", outline)
}

fun StyleMap.clearOutline(): StyleMap {
    return clearProperty("outline")
}

fun StyleMap.setOutlineColor(color: String): StyleMap {
    return doSetProperty("outline-color", color)
}

fun StyleMap.clearOutlineColor(): StyleMap {
    return clearProperty("outline-color")
}

fun StyleMap.setOutlineWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("outline-width", width, unit)
}

fun StyleMap.setOutlineStyle(value: CssOutlineStyle): StyleMap {
    return doSetProperty("outline-style", value)
}

fun StyleMap.clearOutlineStyle(): StyleMap {
    return clearProperty("outline-style")
}

fun StyleMap.setFill(fill: String): StyleMap {
    return doSetProperty("fill", fill)
}

fun StyleMap.setStroke(stroke: String): StyleMap {
    return doSetProperty("stroke", stroke)
}

fun StyleMap.setStrokeWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
    return doSetProperty("stroke-width", width, unit)
}

fun StyleMap.setWhiteSpace(whiteSpace: CssWhiteSpace): StyleMap {
    return doSetProperty("white-space", whiteSpace)
}

fun StyleMap.setVerticalAlign(align: CssVerticalAlign): StyleMap {
    return doSetProperty("vertical-align", align)
}

fun StyleMap.setBoxShadow(value: String): StyleMap {
    return doSetProperty("box-shadow", value)
}

fun StyleMap.clearBoxShadow(): StyleMap {
    return clearProperty("box-shadow")
}

fun StyleMap.setOverflow(overflow: CssOverflow): StyleMap {
    return doSetProperty("overflow", overflow)
}

fun StyleMap.setOverflowX(overflow: CssOverflow): StyleMap {
    return doSetProperty("overflow-x", overflow)
}

fun StyleMap.setOverflowY(overflow: CssOverflow): StyleMap {
    return doSetProperty("overflow-y", overflow)
}

fun StyleMap.setClear(clear: CssClear): StyleMap {
    return doSetProperty("clear", clear)
}

fun StyleMap.setFloat(cssFloat: CssFloat): StyleMap {
    return doSetProperty("float", cssFloat)
}

fun StyleMap.setCssVariable(name: String, value: String): StyleMap {
    return doSetProperty("--$name", value)
}

private fun StyleMap.doSetProperty(name: String, value: String): StyleMap {
    setProperty(name, value)
    return this
}

private fun <ValueT> StyleMap.doSetProperty(name: String, value: ValueT, unit: CssUnit): StyleMap {
    return doSetProperty(name, value.toString() + unit.stringRepresentation)
}

private fun StyleMap.doSetProperty(name: String, value: CssUnitQualifier): StyleMap {
    doSetProperty(name, value.stringQualifier)
    return this
}

fun StyleMap.getProperty(name: String): String {
    return getPropertyValue(name)
}

private fun StyleMap.clearProperty(name: String): StyleMap {
    removeProperty(name)
    return this
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.css

import jetbrains.datalore.base.js.css.enumerables.*
import org.w3c.dom.css.CSSStyleDeclaration

val CSSStyleDeclaration.cssPosition: CssPosition?
    get() = CssPosition.parse(getProperty("position"))

val CSSStyleDeclaration.cssOverflow: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow"))

val CSSStyleDeclaration.cssOverflowX: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow-x"))

val CSSStyleDeclaration.cssOverflowY: CssOverflow?
    get() = CssOverflow.parse(getProperty("overflow-y"))

fun CSSStyleDeclaration.setWidth(
    width: Int,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("width", width, unit)
}

fun CSSStyleDeclaration.clearWidth(): CSSStyleDeclaration {
    return clearProperty("width")
}

fun CSSStyleDeclaration.setMinWidth(
    width: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("min-width", width, unit)
}

fun CSSStyleDeclaration.setMaxWidth(
    width: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("max-width", width, unit)
}

fun CSSStyleDeclaration.setHeight(
    height: Int,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("height", height, unit)
}

fun CSSStyleDeclaration.clearHeight(): CSSStyleDeclaration {
    return clearProperty("height")
}

fun CSSStyleDeclaration.setMinHeight(
    height: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("min-height", height, unit)
}

fun CSSStyleDeclaration.setMaxHeight(
    height: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("max-height", height, unit)
}

fun CSSStyleDeclaration.clearMaxHeight(): CSSStyleDeclaration {
    return clearProperty("max-height")
}

fun CSSStyleDeclaration.setMargin(
    margin: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    setMarginLeft(margin, unit)
    setMarginRight(margin, unit)
    setMarginTop(margin, unit)
    setMarginBottom(margin, unit)
    return this
}

fun CSSStyleDeclaration.setMarginLeft(
    margin: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("margin-left", margin, unit)
}

fun CSSStyleDeclaration.setMarginRight(
    margin: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("margin-right", margin, unit)
}

fun CSSStyleDeclaration.setMarginTop(
    margin: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("margin-top", margin, unit)
}

fun CSSStyleDeclaration.clearMarginTop(): CSSStyleDeclaration {
    return clearProperty("margin-top")
}

fun CSSStyleDeclaration.setMarginBottom(
    margin: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("margin-bottom", margin, unit)
}

fun CSSStyleDeclaration.clearMarginBottom(): CSSStyleDeclaration {
    return clearProperty("margin-bottom")
}

fun CSSStyleDeclaration.setPadding(
    padding: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    setPaddingLeft(padding, unit)
    setPaddingRight(padding, unit)
    setPaddingTop(padding, unit)
    setPaddingBottom(padding, unit)
    return this
}

fun CSSStyleDeclaration.setPaddingLeft(
    padding: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("padding-left", padding, unit)
}

fun CSSStyleDeclaration.setPaddingRight(
    padding: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("padding-right", padding, unit)
}

fun CSSStyleDeclaration.setPaddingTop(
    padding: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("padding-top", padding, unit)
}

fun CSSStyleDeclaration.setPaddingBottom(
    padding: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("padding-bottom", padding, unit)
}

fun CSSStyleDeclaration.setBorder(border: String): CSSStyleDeclaration {
    return doSetProperty("border", border)
}

fun CSSStyleDeclaration.setBorderStyle(value: CssBorderStyle): CSSStyleDeclaration {
    return doSetProperty("border-style", value)
}

fun CSSStyleDeclaration.clearBorder(): CSSStyleDeclaration {
    return clearProperty("border")
}

fun CSSStyleDeclaration.clearBorderWidth(): CSSStyleDeclaration {
    return clearProperty("border-width")
}

fun CSSStyleDeclaration.setBorderLeft(border: String): CSSStyleDeclaration {
    return doSetProperty("border-left", border)
}

fun CSSStyleDeclaration.clearBorderLeft(): CSSStyleDeclaration {
    return clearProperty("border-left")
}

fun CSSStyleDeclaration.setBorderRight(border: String): CSSStyleDeclaration {
    return doSetProperty("border-right", border)
}

fun CSSStyleDeclaration.clearBorderRight(): CSSStyleDeclaration {
    return clearProperty("border-right")
}

fun CSSStyleDeclaration.setBorderTop(border: String): CSSStyleDeclaration {
    return doSetProperty("border-top", border)
}

fun CSSStyleDeclaration.clearBorderTop(): CSSStyleDeclaration {
    return clearProperty("border-top")
}

fun CSSStyleDeclaration.setBorderBottom(border: String): CSSStyleDeclaration {
    return doSetProperty("border-bottom", border)
}

fun CSSStyleDeclaration.clearBorderBottom(): CSSStyleDeclaration {
    return clearProperty("border-bottom")
}

fun CSSStyleDeclaration.setBorderColor(color: String): CSSStyleDeclaration {
    return doSetProperty("border-color", color)
}

fun CSSStyleDeclaration.setBorderLeftColor(color: String): CSSStyleDeclaration {
    return doSetProperty("border-left-color", color)
}

fun CSSStyleDeclaration.setBorderRightColor(color: String): CSSStyleDeclaration {
    return doSetProperty("border-right-color", color)
}

fun CSSStyleDeclaration.setBorderTopColor(color: String): CSSStyleDeclaration {
    return doSetProperty("border-top-color", color)
}

fun CSSStyleDeclaration.setBorderBottomColor(color: String): CSSStyleDeclaration {
    return doSetProperty("border-bottom-color", color)
}

fun CSSStyleDeclaration.clearBorderLeftColor(): CSSStyleDeclaration {
    return clearProperty("border-left-color")
}

fun CSSStyleDeclaration.clearBorderRightColor(): CSSStyleDeclaration {
    return clearProperty("border-right-color")
}

fun CSSStyleDeclaration.clearBorderTopColor(): CSSStyleDeclaration {
    return clearProperty("border-top-color")
}

fun CSSStyleDeclaration.clearBorderBottomColor(): CSSStyleDeclaration {
    return clearProperty("border-bottom-color")
}

fun CSSStyleDeclaration.setTop(
    top: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("top", top, unit)
}

fun CSSStyleDeclaration.clearTop(): CSSStyleDeclaration {
    return clearProperty("top")
}

fun CSSStyleDeclaration.setBottom(
    bottom: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("bottom", bottom, unit)
}

fun CSSStyleDeclaration.clearBottom(): CSSStyleDeclaration {
    return clearProperty("bottom")
}

fun CSSStyleDeclaration.setRight(
    right: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("right", right, unit)
}

fun CSSStyleDeclaration.clearRight(): CSSStyleDeclaration {
    return clearProperty("right")
}

fun CSSStyleDeclaration.setLeft(
    left: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("left", left, unit)
}

fun CSSStyleDeclaration.clearLeft(): CSSStyleDeclaration {
    return clearProperty("left")
}

fun CSSStyleDeclaration.setOpacity(opacity: Double): CSSStyleDeclaration {
    return doSetProperty("opacity", opacity.toString())
}

fun CSSStyleDeclaration.clearOpacity(): CSSStyleDeclaration {
    return clearProperty("opacity")
}

fun CSSStyleDeclaration.setZIndex(zIndex: Int): CSSStyleDeclaration {
    return doSetProperty("z-index", zIndex.toString())
}

fun CSSStyleDeclaration.setDisplay(display: CssDisplay): CSSStyleDeclaration {
    return doSetProperty("display", display)
}

fun CSSStyleDeclaration.clearDisplay(): CSSStyleDeclaration {
    return clearProperty("display")
}

fun CSSStyleDeclaration.setPosition(position: CssPosition): CSSStyleDeclaration {
    return doSetProperty("position", position)
}

fun CSSStyleDeclaration.setVisibility(visibility: CssVisibility): CSSStyleDeclaration {
    return doSetProperty("visibility", visibility)
}

fun CSSStyleDeclaration.setBackgroundColor(color: String): CSSStyleDeclaration {
    return doSetProperty("background-color", color)
}

fun CSSStyleDeclaration.clearBackgroundColor(): CSSStyleDeclaration {
    return clearProperty("background-color")
}

fun CSSStyleDeclaration.setBackgroundImage(image: String): CSSStyleDeclaration {
    return doSetProperty("background-image", image)
}

fun CSSStyleDeclaration.setBackgroundSize(size: String): CSSStyleDeclaration {
    return doSetProperty("background-size", size)
}

fun CSSStyleDeclaration.setColor(color: String): CSSStyleDeclaration {
    return doSetProperty("color", color)
}

fun CSSStyleDeclaration.setFont(value: String): CSSStyleDeclaration {
    return doSetProperty("font", value)
}

fun CSSStyleDeclaration.setFontFamily(value: String): CSSStyleDeclaration {
    return doSetProperty("font-family", value)
}

fun CSSStyleDeclaration.setFontStyle(value: CssFontStyle): CSSStyleDeclaration {
    return doSetProperty("font-style", value)
}

fun CSSStyleDeclaration.setFontWeight(value: CssFontWeight): CSSStyleDeclaration {
    return doSetProperty("font-weight", value)
}

fun CSSStyleDeclaration.setFontSize(
    value: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("font-size", value, unit)
}

fun CSSStyleDeclaration.setTextDecoration(value: String): CSSStyleDeclaration {
    return doSetProperty("text-decoration", value)
}

fun CSSStyleDeclaration.clearTextDecoration(): CSSStyleDeclaration {
    return clearProperty("text-decoration")
}

fun CSSStyleDeclaration.setTextDecorationColor(value: String): CSSStyleDeclaration {
    return doSetProperty("text-decoration-color", value)
}

fun CSSStyleDeclaration.setUserSelect(value: String): CSSStyleDeclaration {
    return doSetProperty("user-select", value)
}

fun CSSStyleDeclaration.setTransform(transform: String): CSSStyleDeclaration {
    return doSetProperty("transform", transform)
}

fun CSSStyleDeclaration.clearTransform(): CSSStyleDeclaration {
    return clearProperty("transform")
}

fun CSSStyleDeclaration.setTransition(transition: String): CSSStyleDeclaration {
    return doSetProperty("transition", transition)
}

fun CSSStyleDeclaration.clearTransition(): CSSStyleDeclaration {
    return clearProperty("transition")
}

fun CSSStyleDeclaration.setCursor(cursor: CssCursor): CSSStyleDeclaration {
    return doSetProperty("cursor", cursor)
}

fun CSSStyleDeclaration.setPointerEvents(pointerEvents: CssPointerEvents): CSSStyleDeclaration {
    return doSetProperty("pointer-events", pointerEvents)
}

fun CSSStyleDeclaration.clearPointerEvents(): CSSStyleDeclaration {
    return clearProperty("pointer-events")
}

fun CSSStyleDeclaration.setGridTemplateColumns(template: String): CSSStyleDeclaration {
    return doSetProperty("grid-template-columns", template)
}

fun CSSStyleDeclaration.setGridRow(gridRow: String): CSSStyleDeclaration {
    return doSetProperty("grid-row", gridRow)
}

fun CSSStyleDeclaration.setGridColumn(gridColumn: String): CSSStyleDeclaration {
    return doSetProperty("grid-column", gridColumn)
}

fun CSSStyleDeclaration.setAlignItems(alignItems: CssAlignItem): CSSStyleDeclaration {
    return doSetProperty("align-items", alignItems)
}

fun CSSStyleDeclaration.clearAlignItems(): CSSStyleDeclaration {
    return clearProperty("align-items")
}

fun CSSStyleDeclaration.setSelfAlignment(alignItems: CssAlignItem): CSSStyleDeclaration {
    return doSetProperty("self-alignment", alignItems)
}

fun CSSStyleDeclaration.clearSelfAlignment(): CSSStyleDeclaration {
    return clearProperty("self-alignment")
}

fun CSSStyleDeclaration.setOutline(outline: String): CSSStyleDeclaration {
    return doSetProperty("outline", outline)
}

fun CSSStyleDeclaration.clearOutline(): CSSStyleDeclaration {
    return clearProperty("outline")
}

fun CSSStyleDeclaration.setOutlineColor(color: String): CSSStyleDeclaration {
    return doSetProperty("outline-color", color)
}

fun CSSStyleDeclaration.clearOutlineColor(): CSSStyleDeclaration {
    return clearProperty("outline-color")
}

fun CSSStyleDeclaration.setOutlineWidth(
    width: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("outline-width", width, unit)
}

fun CSSStyleDeclaration.setOutlineStyle(value: CssOutlineStyle): CSSStyleDeclaration {
    return doSetProperty("outline-style", value)
}

fun CSSStyleDeclaration.clearOutlineStyle(): CSSStyleDeclaration {
    return clearProperty("outline-style")
}

fun CSSStyleDeclaration.setFill(fill: String): CSSStyleDeclaration {
    return doSetProperty("fill", fill)
}

fun CSSStyleDeclaration.setStroke(stroke: String): CSSStyleDeclaration {
    return doSetProperty("stroke", stroke)
}

fun CSSStyleDeclaration.setStrokeWidth(
    width: Double,
    unit: CssUnit = CssUnit.PX
): CSSStyleDeclaration {
    return doSetProperty("stroke-width", width, unit)
}

fun CSSStyleDeclaration.setWhiteSpace(whiteSpace: CssWhiteSpace): CSSStyleDeclaration {
    return doSetProperty("white-space", whiteSpace)
}

fun CSSStyleDeclaration.setVerticalAlign(align: CssVerticalAlign): CSSStyleDeclaration {
    return doSetProperty("vertical-align", align)
}

fun CSSStyleDeclaration.setBoxShadow(value: String): CSSStyleDeclaration {
    return doSetProperty("box-shadow", value)
}

fun CSSStyleDeclaration.clearBoxShadow(): CSSStyleDeclaration {
    return clearProperty("box-shadow")
}

fun CSSStyleDeclaration.setOverflow(overflow: CssOverflow): CSSStyleDeclaration {
    return doSetProperty("overflow", overflow)
}

fun CSSStyleDeclaration.setOverflowX(overflow: CssOverflow): CSSStyleDeclaration {
    return doSetProperty("overflow-x", overflow)
}

fun CSSStyleDeclaration.setOverflowY(overflow: CssOverflow): CSSStyleDeclaration {
    return doSetProperty("overflow-y", overflow)
}

fun CSSStyleDeclaration.setClear(clear: CssClear): CSSStyleDeclaration {
    return doSetProperty("clear", clear)
}

fun CSSStyleDeclaration.setFloat(cssFloat: CssFloat): CSSStyleDeclaration {
    return doSetProperty("float", cssFloat)
}

fun CSSStyleDeclaration.setCssVariable(
    name: String,
    value: String
): CSSStyleDeclaration {
    return doSetProperty("--$name", value)
}

private fun CSSStyleDeclaration.doSetProperty(
    name: String,
    value: String
): CSSStyleDeclaration {
    setProperty(name, value)
    return this
}

private fun <ValueT> CSSStyleDeclaration.doSetProperty(
    name: String,
    value: ValueT,
    unit: CssUnit
): CSSStyleDeclaration {
    return doSetProperty(name, value.toString() + unit.stringRepresentation)
}

private fun CSSStyleDeclaration.doSetProperty(
    name: String,
    value: CssUnitQualifier
): CSSStyleDeclaration {
    doSetProperty(name, value.stringQualifier)
    return this
}

fun CSSStyleDeclaration.getProperty(name: String): String {
    return getPropertyValue(name)
}

private fun CSSStyleDeclaration.clearProperty(name: String): CSSStyleDeclaration {
    removeProperty(name)
    return this
}

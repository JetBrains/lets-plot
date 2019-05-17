package jetbrains.datalore.base.domCore.css

import jetbrains.datalore.base.domCore.css.enumerables.*
import org.w3c.dom.css.CSSStyleDeclaration

class StyleMap(private val style: CSSStyleDeclaration) {

    val borderLeft: String
        get() = getProperty("border-left")

    val top: String
        get() = getProperty("top")

    val left: String
        get() = getProperty("left")

    val position: CssPosition
        get() = CssPosition.parse(getProperty("position"))!!

    val overflow: CssOverflow
        get() = CssOverflow.parse(getProperty("overflow"))!!

    val overflowX: CssOverflow
        get() = CssOverflow.parse(getProperty("overflow-x"))!!

    val overflowY: CssOverflow
        get() = CssOverflow.parse(getProperty("overflow-y"))!!

    var backgroundColor: String
        get() = style.backgroundColor
        set(value) {
            style.backgroundColor = value
        }

    var color: String
        get() = style.color
        set(value) {
            style.color = value
        }

    fun setWidth(width: Int, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("width", width, unit)
    }

    fun clearWidth(): StyleMap {
        return clearProperty("width")
    }

    fun setMinWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("min-width", width, unit)
    }

    fun setMaxWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("max-width", width, unit)
    }

    fun setHeight(height: Int, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("height", height, unit)
    }

    fun clearHeight(): StyleMap {
        return clearProperty("height")
    }

    fun setMinHeight(height: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("min-height", height, unit)
    }

    fun setMaxHeight(height: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("max-height", height, unit)
    }

    fun clearMaxHeight(): StyleMap {
        return clearProperty("max-height")
    }

    fun setMargin(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        setMarginLeft(margin, unit)
        setMarginRight(margin, unit)
        setMarginTop(margin, unit)
        setMarginBottom(margin, unit)
        return this
    }

    fun setMarginLeft(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("margin-left", margin, unit)
    }

    fun setMarginRight(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("margin-right", margin, unit)
    }

    fun setMarginTop(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("margin-top", margin, unit)
    }

    fun clearMarginTop(): StyleMap {
        return clearProperty("margin-top")
    }

    fun setMarginBottom(margin: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("margin-bottom", margin, unit)
    }

    fun clearMarginBottom(): StyleMap {
        return clearProperty("margin-bottom")
    }

    fun setPadding(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        setPaddingLeft(padding, unit)
        setPaddingRight(padding, unit)
        setPaddingTop(padding, unit)
        setPaddingBottom(padding, unit)
        return this
    }

    fun setPaddingLeft(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("padding-left", padding, unit)
    }

    fun setPaddingRight(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("padding-right", padding, unit)
    }

    fun setPaddingTop(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("padding-top", padding, unit)
    }

    fun setPaddingBottom(padding: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("padding-bottom", padding, unit)
    }

    fun setBorder(border: String): StyleMap {
        return setProperty("border", border)
    }

    fun setBorderStyle(value: CssBorderStyle): StyleMap {
        return setProperty("border-style", value)
    }

    fun clearBorder(): StyleMap {
        return clearProperty("border")
    }

    fun clearBorderWidth(): StyleMap {
        return clearProperty("border-width")
    }

    fun setBorderLeft(border: String): StyleMap {
        return setProperty("border-left", border)
    }

    fun clearBorderLeft(): StyleMap {
        return clearProperty("border-left")
    }

    fun setBorderRight(border: String): StyleMap {
        return setProperty("border-right", border)
    }

    fun clearBorderRight(): StyleMap {
        return clearProperty("border-right")
    }

    fun setBorderTop(border: String): StyleMap {
        return setProperty("border-top", border)
    }

    fun clearBorderTop(): StyleMap {
        return clearProperty("border-top")
    }

    fun setBorderBottom(border: String): StyleMap {
        return setProperty("border-bottom", border)
    }

    fun clearBorderBottom(): StyleMap {
        return clearProperty("border-bottom")
    }

    fun setBorderColor(color: String): StyleMap {
        return setProperty("border-color", color)
    }

    fun setBorderLeftColor(color: String): StyleMap {
        return setProperty("border-left-color", color)
    }

    fun setBorderRightColor(color: String): StyleMap {
        return setProperty("border-right-color", color)
    }

    fun setBorderTopColor(color: String): StyleMap {
        return setProperty("border-top-color", color)
    }

    fun setBorderBottomColor(color: String): StyleMap {
        return setProperty("border-bottom-color", color)
    }

    fun clearBorderLeftColor(): StyleMap {
        return clearProperty("border-left-color")
    }

    fun clearBorderRightColor(): StyleMap {
        return clearProperty("border-right-color")
    }

    fun clearBorderTopColor(): StyleMap {
        return clearProperty("border-top-color")
    }

    fun clearBorderBottomColor(): StyleMap {
        return clearProperty("border-bottom-color")
    }

    fun setTop(top: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("top", top, unit)
    }

    fun clearTop(): StyleMap {
        return clearProperty("top")
    }

    fun setBottom(bottom: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("bottom", bottom, unit)
    }

    fun clearBottom(): StyleMap {
        return clearProperty("bottom")
    }

    fun setRight(right: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("right", right, unit)
    }

    fun clearRight(): StyleMap {
        return clearProperty("right")
    }

    fun setLeft(left: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("left", left, unit)
    }

    fun clearLeft(): StyleMap {
        return clearProperty("left")
    }

    fun setOpacity(opacity: Double): StyleMap {
        return setProperty("opacity", opacity.toString())
    }

    fun clearOpacity(): StyleMap {
        return clearProperty("opacity")
    }

    fun setZIndex(zIndex: Int): StyleMap {
        return setProperty("z-index", zIndex.toString())
    }

    fun setDisplay(display: CssDisplay): StyleMap {
        return setProperty("display", display)
    }

    fun clearDisplay(): StyleMap {
        return clearProperty("display")
    }

    fun setPosition(position: CssPosition): StyleMap {
        return setProperty("position", position)
    }

    fun setVisibility(visibility: CssVisibility): StyleMap {
        return setProperty("visibility", visibility)
    }

    fun setBackgroundColor(color: String): StyleMap {
        return setProperty("background-color", color)
    }

    fun clearBackgroundColor(): StyleMap {
        return clearProperty("background-color")
    }

    fun setBackgroundImage(image: String): StyleMap {
        return setProperty("background-image", image)
    }

    fun setBackgroundSize(size: String): StyleMap {
        return setProperty("background-size", size)
    }

    fun setColor(color: String): StyleMap {
        return setProperty("color", color)
    }

    fun setFont(value: String): StyleMap {
        return setProperty("font", value)
    }

    fun setFontFamily(value: String): StyleMap {
        return setProperty("font-family", value)
    }

    fun setFontStyle(value: CssFontStyle): StyleMap {
        return setProperty("font-style", value)
    }

    fun setFontWeight(value: CssFontWeight): StyleMap {
        return setProperty("font-weight", value)
    }

    fun setFontSize(value: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("font-size", value, unit)
    }

    fun setTextDecoration(value: String): StyleMap {
        return setProperty("text-decoration", value)
    }

    fun clearTextDecoration(): StyleMap {
        return clearProperty("text-decoration")
    }

    fun setTextDecorationColor(value: String): StyleMap {
        return setProperty("text-decoration-color", value)
    }

    fun setUserSelect(value: String): StyleMap {
        return setProperty("user-select", value)
    }

    fun setTransform(transform: String): StyleMap {
        return setProperty("transform", transform)
    }

    fun clearTransform(): StyleMap {
        return clearProperty("transform")
    }

    fun setTransition(transition: String): StyleMap {
        return setProperty("transition", transition)
    }

    fun clearTransition(): StyleMap {
        return clearProperty("transition")
    }

    fun setCursor(cursor: CssCursor): StyleMap {
        return setProperty("cursor", cursor)
    }

    fun setPointerEvents(pointerEvents: CssPointerEvents): StyleMap {
        return setProperty("pointer-events", pointerEvents)
    }

    fun clearPointerEvents(): StyleMap {
        return clearProperty("pointer-events")
    }

    fun setGridTemplateColumns(template: String): StyleMap {
        return setProperty("grid-template-columns", template)
    }

    fun setGridRow(gridRow: String): StyleMap {
        return setProperty("grid-row", gridRow)
    }

    fun setGridColumn(gridColumn: String): StyleMap {
        return setProperty("grid-column", gridColumn)
    }

    fun setAlignItems(alignItems: CssAlignItem): StyleMap {
        return setProperty("align-items", alignItems)
    }

    fun clearAlignItems(): StyleMap {
        return clearProperty("align-items")
    }

    fun setSelfAlignment(alignItems: CssAlignItem): StyleMap {
        return setProperty("self-alignment", alignItems)
    }

    fun clearSelfAlignment(): StyleMap {
        return clearProperty("self-alignment")
    }

    fun setOutline(outline: String): StyleMap {
        return setProperty("outline", outline)
    }

    fun clearOutline(): StyleMap {
        return clearProperty("outline")
    }

    fun setOutlineColor(color: String): StyleMap {
        return setProperty("outline-color", color)
    }

    fun clearOutlineColor(): StyleMap {
        return clearProperty("outline-color")
    }

    fun setOutlineWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("outline-width", width, unit)
    }

    fun setOutlineStyle(value: CssOutlineStyle): StyleMap {
        return setProperty("outline-style", value)
    }

    fun clearOutlineStyle(): StyleMap {
        return clearProperty("outline-style")
    }

    fun setFill(fill: String): StyleMap {
        return setProperty("fill", fill)
    }

    fun setStroke(stroke: String): StyleMap {
        return setProperty("stroke", stroke)
    }

    fun setStrokeWidth(width: Double, unit: CssUnit = CssUnit.PX): StyleMap {
        return setProperty("stroke-width", width, unit)
    }

    fun setWhiteSpace(whiteSpace: CssWhiteSpace): StyleMap {
        return setProperty("white-space", whiteSpace)
    }

    fun setVerticalAlign(align: CssVerticalAlign): StyleMap {
        return setProperty("vertical-align", align)
    }

    fun setBoxShadow(value: String): StyleMap {
        return setProperty("box-shadow", value)
    }

    fun clearBoxShadow(): StyleMap {
        return clearProperty("box-shadow")
    }

    fun setOverflow(overflow: CssOverflow): StyleMap {
        return setProperty("overflow", overflow)
    }

    fun setOverflowX(overflow: CssOverflow): StyleMap {
        return setProperty("overflow-x", overflow)
    }

    fun setOverflowY(overflow: CssOverflow): StyleMap {
        return setProperty("overflow-y", overflow)
    }

    fun setClear(clear: CssClear): StyleMap {
        return setProperty("clear", clear)
    }

    fun setFloat(cssFloat: CssFloat): StyleMap {
        return setProperty("float", cssFloat)
    }

    fun setCssVariable(name: String, value: String): StyleMap {
        return setProperty("--$name", value)
    }

    private fun setProperty(name: String, value: String): StyleMap {
        doSetProperty(name, value)
        return this
    }

    private fun <ValueT> setProperty(name: String, value: ValueT, unit: CssUnit): StyleMap {
        return setProperty(name, value.toString() + unit.stringRepresentation)
    }

    private fun setProperty(name: String, value: CssUnitQualifier): StyleMap {
        setProperty(name, value.stringQualifier)
        return this
    }

    fun doSetProperty(name: String, value: String) {
        style.setProperty(name, value)
    }

    fun getProperty(name: String): String {
        return style.getPropertyValue(name)
    }

    private fun clearProperty(name: String): StyleMap {
        removeProperty(name)
        return this
    }

    fun removeProperty(name: String) {
        style.removeProperty(name)
    }
}

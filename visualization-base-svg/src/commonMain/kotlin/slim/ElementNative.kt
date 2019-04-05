package jetbrains.datalore.visualization.base.svg.slim

import com.google.gwt.core.client.JavaScriptObject

internal class ElementNative protected constructor(elementName: String) : SlimBase(elementName), WithTextGen {

    private val myAttributes = createAttributeStore(ATTR_COUNT)
    private external fun createAttributeStore(size: Int) /*-{
    return new Array(size);
  }-*/: JavaScriptObject

    private external fun putAttribute(attributeStore: JavaScriptObject, index: Int, value: Any) /*-{
    return attributeStore[index] = value;
  }-*/

    private external fun hasAttribute(attributeStore: JavaScriptObject, index: Int) /*-{
    return attributeStore[index] !== undefined
  }-*/: Boolean

    private external fun getAttribute(attributeStore: JavaScriptObject, index: Int) /*-{
    return attributeStore[index];
  }-*/: Any

    protected fun setAttribute(index: Int, v: Any) {
        putAttribute(myAttributes, index, v)
    }

    protected fun hasAttribute(index: Int): Boolean {
        return hasAttribute(myAttributes, index)
    }

    protected fun getAttribute(index: Int): Any {
        return getAttribute(myAttributes, index)
    }

    fun appendTo(sb: StringBuffer) {
        sb.append('<').append(getElementName())
        for (i in 0 until ATTR_COUNT) {
            if (hasAttribute(i)) {
                sb.append(' ')
                        .append(ATTR_KEYS[i])
                        .append("=\"")
                        .append(getAttribute(i))
                        .append('\"')
            }
        }

        if (!hasInnerTextContent()) {
            sb.append("/>")
        } else {
            sb.append(">")
            appendInnerTextContentTo(sb)
            sb.append("</").append(getElementName()).append(">")
        }
    }

    protected fun hasInnerTextContent(): Boolean {
        return false
    }

    protected fun appendInnerTextContentTo(sb: StringBuffer) {}

    fun appendTo(g: SvgSlimGroup) {
        (g as GroupNative).addChild(this)
    }
}

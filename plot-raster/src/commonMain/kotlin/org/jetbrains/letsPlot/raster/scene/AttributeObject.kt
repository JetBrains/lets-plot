package org.jetbrains.letsPlot.raster.scene

internal abstract class AttributeObject {
    protected abstract val attributeRegistry: AttributeRegistry
    internal val attributes = AttributeBag(this, attributeRegistry, ::onAttributeChangedInternal)

    protected abstract fun onAttributeChangedInternal(attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?)
    abstract val parent: AttributeObject?

    protected fun <T> variableAttr(value: T) = attributeRegistry.variableAttr(value)
    protected fun <T> derivedAttr(valueProvider: () -> T) = attributeRegistry.derivedAttr(valueProvider)
    protected fun <T> resolvedAttr(compute: (T?) -> T) = attributeRegistry.resolvedAttr(compute)
}
package org.jetbrains.letsPlot.raster.scene

internal class AttributeBag(
    private val owner: AttributeObject,
    private val attributeRegistry: AttributeRegistry,
    private val onAttributeChanged: (spec: AttributeSpec, oldValue: Any?, newValue: Any?) -> Unit
) {
    private val attrInstances = LinkedHashMap<AttributeSpec, Attribute>()
    private val attrVersions = LinkedHashMap<AttributeSpec, Int>()

    internal fun <T> createAttribute(attrSpec: VariableAttributeSpec<T>, value: T): VariableAttribute<T> {
        val attribute = VariableAttribute(attrSpec, value, ::handleAttributeChange)
        attrInstances[attrSpec] = attribute
        return attribute
    }

    internal fun <T> createAttribute(attrSpec: DerivedAttributeSpec<T>, valueProvider: () -> T): DerivedAttribute<T> {
        val attribute = DerivedAttribute(attrSpec, valueProvider, this::handleAttributeChange)
        attrInstances[attrSpec] = attribute
        return attribute
    }

    internal fun <T> createAttribute(attrSpec: ResolvedAttributeSpec<T>, compute: (T?) -> T): ResolvedAttribute<T> {
        val attribute = ResolvedAttribute(
            attributeSpec = attrSpec,
            compute = { rawParentValue: Any? ->
                @Suppress("UNCHECKED_CAST")
                (compute(rawParentValue as T?))
            }
        )
        attrInstances[attrSpec] = attribute

        attrSpec.dependencies.forEach { dep ->
            if (dep !in attrVersions) {
                attrVersions[dep] = 0
            }
        }

        return attribute
    }

    internal fun invalidateDerivedAttribute(attrSpec: AttributeSpec) {
        val attribute = get<DerivedAttribute<*>>(attrSpec)
        attribute.invalidate()
    }

    internal fun <TValue> inheritValue(owner: AttributeObject, attrSpec: AttributeSpec, value: TValue) {
        val attr = get<VariableAttribute<TValue>>(attrSpec)
        attr.inheritValue(owner, value)
    }

    internal inline fun <reified T : Attribute> get(attrSpec: AttributeSpec): T {
        val attr = attrInstances[attrSpec]
            ?: error("Property not defined for '${attrSpec.property.name}'")

        return attr as? T
            ?: error("Property '${attrSpec.property.name}' type mismatch. Expected: ${T::class}, actual: ${attr::class}")
    }

    internal fun pullVersion(attrSpec: ResolvedAttributeSpec<*>): Int {
        var versionSum = 0
        for (dep in attrSpec.dependencies) {
            versionSum += attrVersions[dep] ?: error("Property '${dep.property.name}' version not found in Element.")
        }
        return versionSum
    }

    private fun handleAttributeChange(owner: AttributeObject, attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?) {
        if (attrSpec in attrVersions) {
            attrVersions[attrSpec] = attrVersions[attrSpec]!! + 1
        }

        val dependentAttrSpecs = attributeRegistry.getDependents(owner, attrSpec)
        dependentAttrSpecs.forEach(::invalidateDerivedAttribute)

        onAttributeChanged(attrSpec, oldValue, newValue)
    }
}
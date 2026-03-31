package org.jetbrains.letsPlot.raster.scene

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class AttributeRegistry {
    private var cached = false
    private val attributeSpecs: MutableSet<AttributeSpec> = mutableSetOf()
    private val attributeSpecByProperty: MutableMap<KProperty<*>, AttributeSpec> = HashMap()

    // Transitive dependencies for a given attribute
    private val attributeDependencies: MutableMap<AttributeSpec, Set<AttributeSpec>> = mutableMapOf()

    // Attributes that have dependencies on a given (object class, attribute) pair
    private val dependentAttributes: MutableMap<Pair<KClass<*>, AttributeSpec>, Set<AttributeSpec>> = mutableMapOf()

    private val attributeMetaData: MutableMap<AttributeSpec, Map<String, Any>> = mutableMapOf()

    inner class ClassMeta(val kClass: KClass<*>) {
        internal fun <T> registerVariableAttr(
            kProp: KProperty<T>,
            metaData: Map<String, Any> = emptyMap()
        ): VariableAttributeSpec<T> {
            val attrSpec = VariableAttributeSpec<T>(kClass, kProp)
            registerAttributeSpec(attrSpec, metaData)
            return attrSpec
        }

        internal fun <T> registerDerivedAttr(
            kProp: KProperty<T>,
            dependencies: Set<AttributeSpec>,
            metaData: Map<String, Any> = emptyMap()
        ): DerivedAttributeSpec<T> {
            val attrSpec = DerivedAttributeSpec<T>(kClass, kProp, dependencies)
            registerAttributeSpec(attrSpec, metaData)
            return attrSpec
        }

        internal fun <T> registerResolvedAttr(
            kProp: KProperty<T>,
            dependencies: List<AttributeSpec>,
            metaData: Map<String, Any> = emptyMap()
        ): ResolvedAttributeSpec<T> {
            val attrSpec = ResolvedAttributeSpec<T>(kClass, kProp, dependencies)
            registerAttributeSpec(attrSpec, metaData)
            return attrSpec
        }

        private fun registerAttributeSpec(attributeSpec: AttributeSpec, metaData: Map<String, Any>) {
            attributeSpecs.add(attributeSpec)
            attributeSpecByProperty[attributeSpec.property] = attributeSpec

            if (metaData.isNotEmpty()) {
                attributeMetaData[attributeSpec] = metaData
            }
            cached = false
        }
    }

    fun addClass(kClass: KClass<*>): ClassMeta {
        val classMeta = ClassMeta(kClass)
        return classMeta
    }

    fun <T> variableAttr(value: T): PropertyDelegateProvider<AttributeObject, ReadWriteProperty<AttributeObject, T>> {
        return PropertyDelegateProvider { thisRef, property ->
            val attrSpec = find<VariableAttributeSpec<T>>(property)
            thisRef.attributes.createAttribute(attrSpec, value)
        }
    }

    fun <T> derivedAttr(valueProvider: () -> T): PropertyDelegateProvider<AttributeObject, ReadOnlyProperty<AttributeObject, T>> {
        return PropertyDelegateProvider { thisRef, property ->
            val attrSpec = find<DerivedAttributeSpec<T>>(property)
            thisRef.attributes.createAttribute(attrSpec, valueProvider)
        }
    }

    fun <T> resolvedAttr(compute: (T?) -> T): PropertyDelegateProvider<AttributeObject, ReadOnlyProperty<AttributeObject, T>> {
        return PropertyDelegateProvider { thisRef, property ->
            val parentAttr = find<ResolvedAttributeSpec<T>>(property)
            thisRef.attributes.createAttribute(parentAttr, compute)
        }
    }

    inline fun <reified T : AttributeSpec> find(property: KProperty<*>): T {
        return attributeSpecByProperty[property] as? T
            ?: error("Property '${property.name}' is not registered in ElementMeta.")
    }

    inline fun <reified T> getMetaData(attrSpec: AttributeSpec, key: String): T? {
        return attributeMetaData[attrSpec]?.get(key) as? T
    }

    private fun collectTransitiveDependencies(triggerAttrSpec: AttributeSpec): Set<AttributeSpec> {
        if (triggerAttrSpec !is DerivedAttributeSpec<*>) {
            return emptySet()
        }

        val transitiveDependencies = triggerAttrSpec.dependencies.flatMap(::collectTransitiveDependencies)
        return triggerAttrSpec.dependencies + transitiveDependencies
    }

    fun getDependents(obj: AttributeObject, attrSpec: AttributeSpec): Set<AttributeSpec> {
        if (!cached) {
            dependentAttributes.clear()
            attributeDependencies.clear()
            attributeDependencies += attributeSpecs
                    .associateWith(::collectTransitiveDependencies)
                    .filterValues { it.isNotEmpty() }

            cached = true
        }

        val attrInstance = Pair(obj::class, attrSpec)
        if (attrInstance in dependentAttributes) {
            return dependentAttributes[attrInstance]!!
        }

        val nodeHierarchyDependencySpecs = attributeDependencies.filterKeys { it.ownerClass.isInstance(obj) }
        val dependents = nodeHierarchyDependencySpecs.filter { it.value.contains(attrSpec) }
            .map { it.key }
            .toSet()


        dependentAttributes[attrInstance] = dependents

        return dependents
    }

}
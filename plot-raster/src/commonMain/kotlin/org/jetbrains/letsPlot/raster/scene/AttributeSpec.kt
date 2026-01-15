package org.jetbrains.letsPlot.raster.scene

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal interface AttributeSpec {
    val ownerClass: KClass<*>
    val property: KProperty<*>
}

internal class VariableAttributeSpec<T>(
    override val ownerClass: KClass<*>,
    override val property: KProperty<*>
) : AttributeSpec {
    override fun toString(): String = "${ownerClass.simpleName}.${property.name}"
}

internal class DerivedAttributeSpec<T>(
    override val ownerClass: KClass<*>,
    override val property: KProperty<*>,
    val dependencies: Set<AttributeSpec>
) : AttributeSpec {
    override fun toString(): String = "${ownerClass.simpleName}.${property.name}"
}

internal class ResolvedAttributeSpec<T>(
    override val ownerClass: KClass<*>,
    override val property: KProperty<*>,
    val dependencies: List<AttributeSpec>
) : AttributeSpec {
    override fun toString(): String = "${ownerClass.simpleName}.${property.name}"
}
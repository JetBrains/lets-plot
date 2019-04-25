package jetbrains.datalore.base.composite

import jetbrains.datalore.base.observable.property.Property

interface HasVisibility {
    fun visible(): Property<Boolean>
}
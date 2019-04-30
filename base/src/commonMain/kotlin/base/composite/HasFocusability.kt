package jetbrains.datalore.base.composite

import jetbrains.datalore.base.observable.property.Property

interface HasFocusability {
    fun focusable(): Property<Boolean>
}

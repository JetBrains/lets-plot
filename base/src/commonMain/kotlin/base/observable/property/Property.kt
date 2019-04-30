package jetbrains.datalore.base.observable.property

/**
 * Read/Write property
 */
interface Property<ValueT> : ReadableProperty<ValueT>, WritableProperty<ValueT>
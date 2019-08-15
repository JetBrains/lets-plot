package jetbrains.datalore.base.observable.property

abstract class BaseReadableProperty<ValueT> : ReadableProperty<ValueT> {
    override val propExpr: String = this::class.toString() ?: "<Name not available, inherits BaseReadableProperty>"

    override fun toString(): String {
        return propExpr
    }
}
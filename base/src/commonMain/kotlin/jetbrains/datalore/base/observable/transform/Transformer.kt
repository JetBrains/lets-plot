package jetbrains.datalore.base.observable.transform

/**
 * Factory for a dynamic transformation from a mutable object of type SourceT to a mutable object of TargetT.
 *
 * Such transformations involve listening to the state of object, so we should dispose of them. That's why
 * normal function will not work.
 *
 * You can chain such factories with the andThen method.
 */
interface Transformer<SourceT, TargetT> {
    fun transform(from: SourceT): Transformation<SourceT, TargetT>
    fun transform(from: SourceT, to: TargetT): Transformation<SourceT, TargetT>

    fun <ParameterTargetT> andThen(transformer: Transformer<TargetT, ParameterTargetT>): Transformer<SourceT, ParameterTargetT>
}
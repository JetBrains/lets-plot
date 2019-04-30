package jetbrains.datalore.base.observable.transform

/**
 * A dynamic transformation from a mutable object of type SourceT to a mutable object of TargetT.
 */
abstract class Transformation<SourceT, TargetT> : TerminalTransformation<TargetT>() {

    abstract val source: SourceT
}
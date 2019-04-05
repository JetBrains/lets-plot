package jetbrains.datalore.base.observable.collections

class DataloreIndexOutOfBoundsException : RuntimeException {

    constructor(index: Int) : super(index.toString()) {}

    constructor(message: String) : super(message) {}
}

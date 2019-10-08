package jetbrains.datalore.plot.builder.assemble

abstract class GuideOptions {

    var isReverse: Boolean = false

    companion object {
        val NONE: GuideOptions = object : GuideOptions() {}
    }
}

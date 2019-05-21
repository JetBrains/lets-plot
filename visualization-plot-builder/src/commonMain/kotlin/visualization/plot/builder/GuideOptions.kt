package jetbrains.datalore.visualization.plot.builder

abstract class GuideOptions {

    var isReverse: Boolean = false

    companion object {
        val NONE: GuideOptions = object : GuideOptions() {

        }
    }
}

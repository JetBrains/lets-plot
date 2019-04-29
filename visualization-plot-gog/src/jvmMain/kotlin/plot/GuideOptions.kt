package jetbrains.datalore.visualization.plot.gog.plot

abstract class GuideOptions {

    var isReverse: Boolean = false

    companion object {
        val NONE: GuideOptions = object : GuideOptions() {

        }
    }
}

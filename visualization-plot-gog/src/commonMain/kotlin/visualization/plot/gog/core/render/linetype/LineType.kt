package jetbrains.datalore.visualization.plot.gog.core.render.linetype

interface LineType {
    val isSolid: Boolean

    val isBlank: Boolean

    val dashArray: List<Double>
}

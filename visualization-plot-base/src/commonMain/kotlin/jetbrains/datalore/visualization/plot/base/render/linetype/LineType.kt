package jetbrains.datalore.visualization.plot.base.render.linetype

interface LineType {
    val isSolid: Boolean

    val isBlank: Boolean

    val dashArray: List<Double>
}

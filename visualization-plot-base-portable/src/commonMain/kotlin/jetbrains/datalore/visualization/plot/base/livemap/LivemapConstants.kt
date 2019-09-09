package jetbrains.datalore.visualization.plot.base.livemap

interface LivemapConstants {
    enum class DisplayMode {
        POLYGON,
        POINT,
        PIE,
        HEATMAP,
        BAR
    }

    enum class Theme {
        COLOR,
        LIGHT,
        DARK
    }

    enum class Projection {
        EPSG3857,
        EPSG4326,
        AZIMUTHAL,
        CONIC
    }
}
package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.MappedDataAccess

class LiveMapLayerData(
    val geom: Geom,
    val geomKind: GeomKind,
    val aesthetics: Aesthetics,
    val dataAccess: MappedDataAccess
)

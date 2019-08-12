package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.projectionGeometry.QuadKey

data class FragmentKey(val regionId: String, val quadKey: QuadKey) {
    fun zoom() = quadKey.zoom()
}
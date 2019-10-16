package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.api.LiveMapBuilder

class EmptyLiveMapDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapBuilder {
        return basicLiveMap {  }
    }
}
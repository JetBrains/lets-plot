package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.LiveMapSpec

class EmptyLiveMapDemoModel(dimension: DoubleVector): DemoModelBase(dimension) {
    override fun createLiveMapSpec(): LiveMapSpec {
        return basicLiveMap {  }
    }
}
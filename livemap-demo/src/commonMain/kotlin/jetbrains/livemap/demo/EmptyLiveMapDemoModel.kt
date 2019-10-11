package jetbrains.livemap.demo

import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.livemap.LiveMapSpec

class EmptyLiveMapDemoModel(canvasControl: CanvasControl): DemoModelBase(canvasControl) {
    override fun createLiveMapSpec(): LiveMapSpec {
        return basicLiveMap {  }
    }
}
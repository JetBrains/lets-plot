package jetbrains.livemap.demo

import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.livemap.LiveMapSpec

class EmptyLivemMapDemoModel(canvasControl: CanvasControl): DemoModelBase(canvasControl) {
    override fun createLiveMapSpec(): LiveMapSpec {
        return basicLiveMap {  }
    }
}
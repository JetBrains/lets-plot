package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class CaptionOptions : Options() {
    var text: String? by map(Option.Plot.CAPTION_TEXT)
}

fun caption(block: CaptionOptions.() -> Unit) = CaptionOptions().apply(block)

var PlotOptions.captionText: String?
    get() = this.caption?.text
    set(value) {
        if (value != null) {
            if (this.caption == null) {
                this.caption = CaptionOptions()
            }
            this.caption?.text = value
        } else {
            this.caption = null
        }
    }

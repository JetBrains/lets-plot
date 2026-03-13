/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.events

import org.w3c.dom.DragEvent
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import org.w3c.xhr.ProgressEvent

class DomEventType<EventT : Event> private constructor(val name: String) {
    companion object {

        val BLUR = DomEventType<Event>("blur")
        val CHANGE = DomEventType<Event>("change")
        val INPUT = DomEventType<Event>("input")
        val PASTE = DomEventType<Event>("paste")
        val RESIZE = DomEventType<Event>("resize")
        val CLICK = DomEventType<MouseEvent>("click")
        val CONTEXT_MENU = DomEventType<MouseEvent>("contextmenu")
        val DOUBLE_CLICK = DomEventType<MouseEvent>("dblclick")
        val DRAG = DomEventType<DragEvent>("drag")
        val DRAG_END = DomEventType<DragEvent>("dragend")
        val DRAG_ENTER = DomEventType<DragEvent>("dragenter")
        val DRAG_LEAVE = DomEventType<DragEvent>("dragleave")
        val DRAG_OVER = DomEventType<DragEvent>("dragover")
        val DRAG_START = DomEventType<DragEvent>("dragstart")
        val DROP = DomEventType<DragEvent>("drop")
        val FOCUS = DomEventType<Event>("focus")
        val FOCUS_IN = DomEventType<Event>("focusin")
        val FOCUS_OUT = DomEventType<Event>("focusout")
        val KEY_DOWN = DomEventType<KeyboardEvent>("keydown")
        val KEY_PRESS = DomEventType<KeyboardEvent>("keypress")
        val KEY_UP = DomEventType<KeyboardEvent>("keyup")
        val LOAD = DomEventType<Event>("load")
        val MOUSE_ENTER = DomEventType<MouseEvent>("mouseenter")
        val MOUSE_LEAVE = DomEventType<MouseEvent>("mouseleave")
        val MOUSE_DOWN = DomEventType<MouseEvent>("mousedown")
        val MOUSE_MOVE = DomEventType<MouseEvent>("mousemove")
        val MOUSE_OUT = DomEventType<MouseEvent>("mouseout")
        val MOUSE_OVER = DomEventType<MouseEvent>("mouseover")
        val MOUSE_UP = DomEventType<MouseEvent>("mouseup")
        val MOUSE_WHEEL = DomEventType<WheelEvent>("wheel")
        val SCROLL = DomEventType<Event>("scroll")
        val TOUCH_CANCEL = DomEventType<Event>("touchcancel")
        val TOUCH_END = DomEventType<Event>("touchend")
        val TOUCH_MOVE = DomEventType<Event>("touchmove")
        val TOUCH_START = DomEventType<Event>("touchstart")
        val COMPOSITION_START = DomEventType<Event>("compositionstart")
        val COMPOSITION_END = DomEventType<Event>("compositionend")
        val COMPOSITION_UPDATE = DomEventType<Event>("compositionupdate")
        val MESSAGE = DomEventType<MessageEvent>("message")

        val XHR_PROGRESS = DomEventType<ProgressEvent>("progress")
        val XHR_LOAD = DomEventType<ProgressEvent>("load")
        val XHR_LOAD_START = DomEventType<ProgressEvent>("loadstart")
        val XHR_LOAD_END = DomEventType<ProgressEvent>("loadend")
        val XHR_ABORT = DomEventType<Event>("abort")
        val XHR_ERROR = DomEventType<Event>("error")
    }
}

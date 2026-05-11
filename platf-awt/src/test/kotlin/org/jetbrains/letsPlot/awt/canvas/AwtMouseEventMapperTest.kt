/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import java.awt.event.InputEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import kotlin.test.Test
import kotlin.test.assertEquals

class AwtMouseEventMapperTest {
    @Test
    fun `should ignore mouse exited while button is pressed`() {
        val component = JPanel().apply { setSize(300, 300) }
        val events = registerEvents(
            AwtMouseEventMapper(component),
            MouseEventSpec.MOUSE_PRESSED,
            MouseEventSpec.MOUSE_DRAGGED,
            MouseEventSpec.MOUSE_RELEASED
        )

        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_PRESSED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_EXITED, x = 205, y = 50))
        component.dispatchEvent(
            mouseEvent(
                component,
                MouseEvent.MOUSE_DRAGGED,
                x = 215,
                y = 65,
                modifiersEx = InputEvent.BUTTON1_DOWN_MASK
            )
        )
        component.dispatchEvent(
            mouseEvent(
                component,
                MouseEvent.MOUSE_RELEASED,
                x = 215,
                y = 65,
                button = MouseEvent.BUTTON1
            )
        )

        assertEquals(
            listOf(
                MouseEventSpec.MOUSE_PRESSED,
                MouseEventSpec.MOUSE_DRAGGED,
                MouseEventSpec.MOUSE_RELEASED
            ),
            events
        )
    }

    @Test
    fun `should dispatch drag and release outside mapper bounds after press`() {
        val component = JPanel().apply { setSize(300, 300) }
        val events = registerEvents(
            AwtMouseEventMapper(component, Rectangle(0, 0, 200, 200)),
            MouseEventSpec.MOUSE_PRESSED,
            MouseEventSpec.MOUSE_DRAGGED,
            MouseEventSpec.MOUSE_RELEASED
        )

        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_PRESSED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(
            mouseEvent(
                component,
                MouseEvent.MOUSE_DRAGGED,
                x = 240,
                y = 70,
                modifiersEx = InputEvent.BUTTON1_DOWN_MASK
            )
        )
        component.dispatchEvent(
            mouseEvent(
                component,
                MouseEvent.MOUSE_RELEASED,
                x = 240,
                y = 70,
                button = MouseEvent.BUTTON1
            )
        )

        assertEquals(
            listOf(
                MouseEventSpec.MOUSE_PRESSED,
                MouseEventSpec.MOUSE_DRAGGED,
                MouseEventSpec.MOUSE_RELEASED
            ),
            events
        )
    }

    @Test
    fun `should not require click after release`() {
        val component = JPanel().apply { setSize(300, 300) }
        val events = registerEvents(
            AwtMouseEventMapper(component),
            MouseEventSpec.MOUSE_PRESSED,
            MouseEventSpec.MOUSE_RELEASED,
            MouseEventSpec.MOUSE_MOVED
        )

        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_PRESSED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_RELEASED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_EXITED, x = 205, y = 50))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_MOVED, x = 80, y = 80))

        assertEquals(
            listOf(
                MouseEventSpec.MOUSE_PRESSED,
                MouseEventSpec.MOUSE_RELEASED,
                MouseEventSpec.MOUSE_MOVED
            ),
            events
        )
    }

    @Test
    fun `should dispatch click after release`() {
        val component = JPanel().apply { setSize(300, 300) }
        val events = registerEvents(
            AwtMouseEventMapper(component),
            MouseEventSpec.MOUSE_PRESSED,
            MouseEventSpec.MOUSE_RELEASED,
            MouseEventSpec.MOUSE_CLICKED
        )

        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_PRESSED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_RELEASED, x = 50, y = 50, button = MouseEvent.BUTTON1))
        component.dispatchEvent(mouseEvent(component, MouseEvent.MOUSE_CLICKED, x = 50, y = 50, button = MouseEvent.BUTTON1))

        assertEquals(
            listOf(
                MouseEventSpec.MOUSE_PRESSED,
                MouseEventSpec.MOUSE_RELEASED,
                MouseEventSpec.MOUSE_CLICKED
            ),
            events
        )
    }

    @Test
    fun `should ignore wheel events outside mapper bounds`() {
        val component = JPanel().apply { setSize(300, 300) }
        val events = registerEvents(
            AwtMouseEventMapper(component, Rectangle(0, 0, 200, 200)),
            MouseEventSpec.MOUSE_WHEEL_ROTATED
        )

        component.dispatchEvent(mouseWheelEvent(component, x = 240, y = 70))
        component.dispatchEvent(mouseWheelEvent(component, x = 50, y = 70))

        assertEquals(listOf(MouseEventSpec.MOUSE_WHEEL_ROTATED), events)
    }

    private fun registerEvents(
        mapper: AwtMouseEventMapper,
        vararg specs: MouseEventSpec
    ): List<MouseEventSpec> {
        val events = mutableListOf<MouseEventSpec>()
        specs.forEach { spec ->
            mapper.addEventHandler(spec, object : EventHandler<org.jetbrains.letsPlot.commons.event.MouseEvent> {
                override fun onEvent(event: org.jetbrains.letsPlot.commons.event.MouseEvent) {
                    events += spec
                }
            })
        }
        return events
    }

    private fun mouseEvent(
        source: JPanel,
        id: Int,
        x: Int,
        y: Int,
        button: Int = MouseEvent.NOBUTTON,
        modifiersEx: Int = 0,
        clickCount: Int = 1
    ): MouseEvent {
        return MouseEvent(
            source,
            id,
            System.currentTimeMillis(),
            modifiersEx,
            x,
            y,
            clickCount,
            false,
            button
        )
    }

    private fun mouseWheelEvent(
        source: JPanel,
        x: Int,
        y: Int
    ): MouseWheelEvent {
        return MouseWheelEvent(
            source,
            MouseEvent.MOUSE_WHEEL,
            System.currentTimeMillis(),
            0,
            x,
            y,
            0,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            1,
            1
        )
    }
}

/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.SwingConstants

internal class TempCellRenderer(
    private val entryFor: (String) -> TempStore.Entry?
) : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
        label.preferredSize = null
        val id = value as? String

        if (index < 0 || id == null) {
            label.icon = null
            label.iconTextGap = 0
            return label
        }

        val entry = entryFor(id)
        label.text = entry?.title ?: id
        label.icon = entry?.spec?.let { PreviewCache.loadIcon(it) }
        label.horizontalTextPosition = SwingConstants.RIGHT
        label.verticalTextPosition = SwingConstants.CENTER
        label.iconTextGap = 8
        return label
    }
}

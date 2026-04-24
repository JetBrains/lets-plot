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

internal class FavoriteCellRenderer(
    private val specFor: (String) -> String?
) : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
        // DefaultListCellRenderer reuses one JLabel — clear any explicit size the previous call set.
        label.preferredSize = null
        val name = value as? String

        // index == -1 is the collapsed combo button — keep it compact, text-only.
        if (index < 0 || name == null) {
            label.icon = null
            label.iconTextGap = 0
            return label
        }

        label.icon = specFor(name)?.let { PreviewCache.loadIcon(it) }
        label.horizontalTextPosition = SwingConstants.RIGHT
        label.verticalTextPosition = SwingConstants.CENTER
        label.iconTextGap = 8
        return label
    }
}

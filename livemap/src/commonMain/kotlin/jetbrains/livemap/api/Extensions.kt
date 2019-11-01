/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api



fun Pies.pie(block: ChartSource.() -> Unit) {
    factory.add(ChartSource().apply(block))
}

fun Texts.text(block: TextBuilder.() -> Unit) {
    items.add(
        TextBuilder()
            .apply(block)
            .build()
    )
}

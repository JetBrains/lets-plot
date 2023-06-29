/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.datamodel.mapping.framework.transform.Transformers

internal open class ItemMapper(item: Item) : Mapper<Item, Item>(item, Item()) {
    private lateinit var mySimpleRole: SimpleRoleSynchronizer<Item, Item>

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        conf.add(
            Synchronizers.forObservableRole(
                this, source.observableChildren, target.observableChildren, createMapperFactory()))
        conf.add(
            Synchronizers.forObservableRole(
                this, source.transformedChildren, Transformers.identityList(),
                target.transformedChildren, createMapperFactory()))


        conf.add(
            Synchronizers.forSingleRole(
                this, source.singleChild, target.singleChild, createMapperFactory()))

        mySimpleRole = Synchronizers.forSimpleRole(
                this, source.children, target.children, createMapperFactory())
        conf.add(mySimpleRole)

        conf.add(Synchronizers.forPropsTwoWay(source.name, target.name))
    }

    fun refreshSimpleRole() {
        mySimpleRole.refresh()
    }

    protected open fun createMapperFactory(): MapperFactory<Item, Item> {
        return object : MapperFactory<Item, Item> {
            override fun createMapper(source: Item): Mapper<out Item, out Item> {
                return ItemMapper(source)
            }
        }
    }
}
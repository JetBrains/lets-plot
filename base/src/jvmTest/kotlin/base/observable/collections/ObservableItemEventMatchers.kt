/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.CollectionItemEvent.EventType.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

object ObservableItemEventMatchers {
    fun <T> event(oldItem: Matcher<in T>, newItem: Matcher<in T>,
                  index: Matcher<Int>, type: Matcher<CollectionItemEvent.EventType>): Matcher<CollectionItemEvent<out T>> {
        return object : TypeSafeDiagnosingMatcher<CollectionItemEvent<out T>>() {
            override fun matchesSafely(event: CollectionItemEvent<out T>, description: Description): Boolean {
                if (!type.matches(event.type)) {
                    description.appendText("type was ").appendValue(event.type)
                    return false
                }
                if (!oldItem.matches(event.oldItem)) {
                    description.appendText("old item was ").appendValue(event.oldItem)
                    return false
                }
                if (!newItem.matches(event.newItem)) {
                    description.appendText("new item was ").appendValue(event.newItem)
                    return false
                }
                if (!index.matches(event.index)) {
                    description.appendText("index was ").appendValue(event.index)
                    return false
                }
                return true
            }

            override fun describeTo(description: Description) {
                description.appendText("an ").appendDescriptionOf(type).appendText(" event with ")
                        .appendText("old item ").appendDescriptionOf(oldItem).appendText(", ")
                        .appendText("new item ").appendDescriptionOf(newItem).appendText(", ")
                        .appendText("index ").appendDescriptionOf(index)
            }
        }
    }

    fun <T> addEvent(item: Matcher<in T>, index: Matcher<Int>): Matcher<CollectionItemEvent<out T>> {
        return event(nullValue(), item, index, equalTo(ADD))
    }

    fun <T> setEvent(
            oldItem: Matcher<in T>, newItem: Matcher<in T>, index: Matcher<Int>): Matcher<CollectionItemEvent<out T>> {
        return event(oldItem, newItem, index, equalTo(SET))
    }

    fun <T> removeEvent(item: Matcher<in T>, index: Matcher<Int>): Matcher<CollectionItemEvent<out T>> {
        return event(item, nullValue(), index, equalTo(REMOVE))
    }
}

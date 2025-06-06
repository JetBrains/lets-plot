/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.ui

import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider

class ResourceManager(private val canvasProvider: CanvasProvider) {
    private val snapshotMap: MutableMap<String, Canvas.Snapshot> = HashMap()

    init {
        add(KEY_PLUS, BUTTON_PLUS)
        add(KEY_PLUS_DISABLED, BUTTON_PLUS_DISABLED)
        add(KEY_MINUS, BUTTON_MINUS)
        add(KEY_MINUS_DISABLED, BUTTON_MINUS_DISABLED)
        add(KEY_GET_CENTER, BUTTON_GET_CENTER)
        add(KEY_MAKE_GEOMETRY, BUTTON_MAKE_GEOMETRY)
        add(KEY_MAKE_GEOMETRY_ACTIVE, BUTTON_MAKE_GEOMETRY_ACTIVE)
        add(KEY_DROP_OPEN, BUTTON_DROP_OPEN)
        add(KEY_DROP_CLOSE, BUTTON_DROP_CLOSE)
        add(KEY_RESET_POSITION, BUTTON_RESET_POSITION)

    }

    private fun add(key: String, dataUrl: String): ResourceManager {
        canvasProvider.decodeDataImageUrl(dataUrl).onResult(
            { snapshot -> snapshotMap[key] = snapshot },
            { message -> error(message) })
        return this
    }

    operator fun get(key: String): Canvas.Snapshot {
        return snapshotMap[key]!!
    }

    fun isReady(): Boolean = listOf(
        KEY_PLUS,
        KEY_PLUS_DISABLED,
        KEY_MINUS,
        KEY_MINUS_DISABLED,
        KEY_GET_CENTER,
        KEY_MAKE_GEOMETRY,
        KEY_MAKE_GEOMETRY_ACTIVE,
        KEY_DROP_OPEN,
        KEY_DROP_CLOSE,
        KEY_RESET_POSITION,
    ).all { snapshotMap.containsKey(it) }

    companion object {
        const val KEY_PLUS = "img_plus"
        const val KEY_PLUS_DISABLED = "img_plus_disable"
        const val KEY_MINUS = "img_minus"
        const val KEY_MINUS_DISABLED = "img_minus_disable"
        const val KEY_GET_CENTER = "img_get_center"
        const val KEY_MAKE_GEOMETRY = "img_create_geometry"
        const val KEY_MAKE_GEOMETRY_ACTIVE = "img_create_geometry_active"
        const val KEY_DROP_OPEN = "img_drop_open"
        const val KEY_DROP_CLOSE = "img_drop_close"
        const val KEY_RESET_POSITION = "img_reset_position"

        private const val BUTTON_PLUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABWElEQVR4nO2a242EMAxFU0JKSGeznbCdQCdbAiXkh1cFPD4gG0d4ZM0I7U4UkIN8JGvE1+RgYynSVWrHWqvbtv3uuu7H/7pMqm6a5qFe6fveeBHL4ICx5fthzVMIZYZhcMuyuG3bXA5M0xTOTKS08i37QplcRCjrulKpQsEcwgPY5so8z0EIvn+Fc5gz0CX0uIUQIELcESHuiBB3RIg7IsQdEYqlLEuntXbGGFdV1Wn/c5kQiPg7ZCgQO4vLhFAG6yxEKBYRiiS5EG6zV4G/KtX2Sy5Et9mnlWL7JReK6Q7tEjshGLmYLsGLYDlyR2S7FI4QoUhEKBa6/VJssyMuvT6ASKptdoRc8LgjQtwRIe6IEHdEiDsixJ03oRxjMZSnEMbJIF6SK+M4olANHSrg4Q7hpZCdg0gWjZflEmICEZgqGi+7bwAQ2bNzNYMD/qv8eeH7L0Lwb+cXJAQVFqa8ZecAAAAASUVORK5CYII=")

        private const val BUTTON_MINUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABKElEQVR4nO2ay62DMBBFpwSX4M6STpJOoBNKoARv+FXAZwGOB+HIyua9sMk1ukcaIXZzuMPuihw450zbts++76vw9JlM3TTNTT4JEjaMA1jw1HRdF/JwNhXaZYZh8Muy+G3bfA5M07TvnEgZCZHdo0wuIinrur6lQjAP0TvUF7XNlXmeo1Al8Q5zRlOKHpcQUiiEDoXQoRA6FEKHQuhQCB0KoUMhdCj0F0VReGOMF5Gvxlrry7LEE9LFvpWJox8CTuhMOmlKcEJ6cmdS0g8BeXK/hkLoUAgdCqFDIXQohA6F0KEQOhRC5/pCOdZiUt5CsU6m9ZJcGccxCtUq9NCXK5SX9u6cVrLSelkuJSYV0atK62XXLQBGju5c/esF/zth30p/mb34d/ACQe2a//LLvF8AAAAASUVORK5CYII=")

        private const val BUTTON_MINUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABJUlEQVR4nO2aMYqEQBBF6whzhLmW0e5NZs6gYGAkxiqoN5l8MFFkT9Hrl+lBlmUCDfwtv+EFJlKPrs6e2ev0fX8Zx/GeJMkjz3MXAmmaPodh+LK/Z5qma5ZlP0cPuBXMPl/I9S3kZYqicHVdu7ZtXdd19FRVtcy8krrYfGXfXiYUkTVN07yl5k27GfYQH7A9eritYKvggPdvfg+PHmrvLXmPUwgBCbEjIXYkxI6E2JEQOxJiR0LsSIgdCX0iiiJnZrvAP2iE9sp4JKSVCwgJsSMhdiTEjoTYkRA7EmJHQuxIiJ3zC4WYxfwr5HMy5CVHD7WVsiwXGWQ+SMtu+DhDvLS0c0iy1nlZKBETRLBV67zsvAGgP2jnfG4WAnEcP/BklvDvdX4BUOGPnW8A9BMAAAAASUVORK5CYII=")

        private const val BUTTON_PLUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABQUlEQVR4nO2aMY6DMBBFfYQcIdei2r1JcgUoKKgQNSABN0m/ogGhPYXDt2JkJWkC2XjMfktPwhXzNGNLlr5St9X3/WEcx3OSJJc8z3UIpGn6MwzDl7pf0zQdsyz79V3gWlD73JDjImRliqLQdV3rtm1113XiqarK1OxIHdTcsm8rE4qIS9M0i9R8ZE4Kc4gNbH0XtxZMFRxw/pWdQ99Fbe2S9diFEKCQdCgkHQpJh0LSoZB0PiYURZGeXygGfAcvZGUsFKIQhSgUnpB7Na9l65X+VqGtMu/oHoX+1ci90r2/+g+FKEQhCvkR2t0D71NQSDoUkg6FpEMh6VBIOg9CIcZingrZOBniJb6LWktZlkYGMR+FwA82ewgvmewcIlluvCyUEBNEMFVuvGzJy+0uAGgXsnM2bhYCcRxfcGRM8O+2rqYVHjNnkD6PAAAAAElFTkSuQmCC")

        private const val BUTTON_GET_CENTER =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAADSElEQVR4nO2aPWvyUBTHU11EOhRXEWy3jk7Oirg42H4Cn6FjV0Fw0Elwc3QR/AClg5vTg4OrDoVCJ5dqHdwEXwY9T/7BKydX7ZMXjSb4h0Nj7jkn95fc3Jf0KspGg8Hgbjgclkej0V/1L7nE+t/f3zlF1s/PT1QFGVxABa2a+jwG0S2QgBmPx7RcLmm9XpMbNJ/PtTozqDtFfWR/BIxbQLhWqxWHKiloh/gBWrdqsVhoQHj/FdEO3Sw8JcHhCSDoCnTpcgyoXC6TOjJohuNTyTEgASPsVLoCWZUngLrdLhWLRUokEjtAOIcy+BxTJwF6f3+ndDq9A3HI4IuYY+ioQIh/fX01DCIbYo9Rh6MA9Xo9enp62qnk7e0tRSIRSiaT9PLyohmOcQ5lsj9yINdZgRAnwwSDQcpms9RqtfbmxTmUwQe+MpSdutgGkptZNBqler1uKB984IsYuflZkW0gvMwyDO68WSFGhrLSUdgG4r1ZKBSit7c3Xfl0OqVqtUrxeJwCgYBmOMY5lHEhFjl47+coEMYQfkdzuZwuR6fToVQqdbBXQxl8eGWQg/uYHadsAWFgFBcOh8O6i+Pu/wbDofiTQg7kEuW4hmNAfAaAivF4NCmj4w98eYX4jcA1TgbElwCy5fN5nS/eE6NA8OVCrkO+/1t6mAKSk/v9/u1xo9HQ+eLlNwoEXy7k2ncNYScD4uZKoN+aXKVS0fnaaXLI5UiTk8U7hefnZ+1rq5DVTgE5kMuRTkEW77YfHh7o6+trW2a120YO5BLljnbbfGC9ubmhWq2me0pmB1bEIgdyCR9HB1aIT33u7+/p4+NDV25m6oNY5BD5HJ/6QPLkNJPJ0GQyMZ0HMYjluc4yOYX48sHn82kVG41GuuZ3SPCBL2IQK/KcbfkgksgLvMfHR2o2m9qd3weGcyiDD3x57NkXeNC+JThe7lgsRoVCgdrtNn1+fmqGY5xDGe8ABMzZl+A8mWc+knB55jOWLM98aNwnMxNMO7oCWZXngDz3Dy+ndAW6dO0AuXFbDNcWSGwnw/YSt2o2mwmgPp5QCT+8sHlJ2zuHLVl8e5lbNjEBBK2Kby9ThDy3AVBos3eufwEVNGRqffH+l7SNfxv9AyaodeN8K1S9AAAAAElFTkSuQmCC")


        private const val BUTTON_MAKE_GEOMETRY =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABW0lEQVR4nO2aTYqEQAxGPUrfUG+gN/AoHsGFB3GnS+1Vb9Sa+oQ0NdIOo/WXavIgGwXxEf2IkizTjOP4GIah0/XSpRKspu/7R2bIPBnclG09dynY4cA8z2rbNpUay7KoaZpIqsvIECdSZV1XEnq9hVKHPESIKyLEHRHijghxR4S4I0KuadtW1XW9lx77ra8XVagsS6U/xX4VjtkQTQjdOMpQoWt3iSaER+xMCOfuIkKuyPP8VMgmHKIIfQoDqqqqrK4dXOgoUxTFO7ZtwoAIKnSUse3GJ4IJhZABQYRCyQDvQiFlgHMhczY7RrNvGeBUyGcc/xdnQn/NZojmUDgT8jXKXEWEzkAY+JjNruI0FPDixwoDwnlsoxsuZ7OrRP+n4BoR4o4IcUeEuCNC3BEh7ny10L4jl+ImFoHFK1MIi3/7ilaKG1lohLFe1nzfAqCx1dgwuKk7hVemI5kf7d5LEFHxxYkAAAAASUVORK5CYII="

        private const val BUTTON_MAKE_GEOMETRY_ACTIVE =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAB90lEQVR4nO2awU7CQBCG+yi8OYlw4EYUr1oMCZwVPECi3AoaDNaYkEhx7Q9ZwcKWbXe609ZO8icNTXfnyyyz0+04Tmh1d1ZrdL1e885bXbieKJpC39t1d1pzDmB8bqeMFTJsocKLNn5oDd5FZ/gtrkeiULp6CELflzJSPUcS4ga3c2nVGW62DM2ut/oF4nbKVJKjAsqr/jfQ7USIxacQ6434Y+MX9TO4p7Kkz838nQ8kQBgoCmIbCPYVxENpA80/aB0zeQ6rxBgozmwDIUrkQFn/udPOXwGVBghr+lC2gXTnZ99YB1MhHmc7ndtjdMQKNHk9XkoTw+izASEaKhs8FxAIS0xlI68C4geavqmBbsYWgCjT9qlkIE01NnnajhoVzNNin7b7MclAd36rQFGYJJHOHZAJTO6ATGFyBUQBwwp0WJtFU7NJdiQH0kmbadKxrqxX23G1GVKzCUwSkQFlVcpUQFRASAYqM6nN2ICg8YmkYPt1nbzaRjR0ajN2oNIdkkTNNpDu/BVQaYDWwfGgOpksi8N6EiB8wsgLED58GQMhHauiZPuDV9xGnWgfwkBznwcIIFgl56oO9rNtalVAeVd5gWSPXBE7saQu74M9EFqycIEWLdzgdi6pEIhWfymB2qVpAGzIBkDZ1Qg6bqfSCD1yDdfrSZgflF5tUND3tCgAAAAASUVORK5CYII="

        private const val BUTTON_DROP_OPEN =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABYElEQVR4nO2ay42FMAxF0wAVQFVQCFSFaIAKoA8kauCzQXh0oxeGxXuaGXAyDvKRvGAT+UAIRrrGGEOoJEmoqipqmoaGYYii2ralPM/JObzKUJZl1Pf9vzd4tbquozRNv4WczDiOtK4r7ftOMTDPs+3ZSWGXmaIoDplYRM5s23ZIlWVJBvsQF7CNlWVZrAPef+P2YczgKTmPRwgBFZKOCklHhaSjQtJRIemokHRU6B3nf3kO7qzHLnRX6u5aXoSuSnGsw/YO3W2G66awHgpXm+KSAeyn3F+b45QBXo7t3zbJLQO8fYd+ataHDPD6Yf3UtC8Z4H1SeNe8LxkQZPQJJQOCzXIhZEDQ4dS3DNBpWzoqJB0Vko4KSUeFpKNC0nm+UIyxmDOHkIuTIV4SK9M0WQfEfAwCP7h4QnjJZucQyTrHy2IJMUEEu+ocL3tuANAVsnMubhZD1XVtM3I2+Pdy+AImbuo3Xk/sNAAAAABJRU5ErkJggg=="

        private const val BUTTON_DROP_CLOSE =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAABUElEQVR4nO3ay42DQAwGYDdABVAVFAJVIRqgAugDiRp4XBBeeZRhR/tKNtiJB/mXfOCC/InhAPoBAJAmSRKsqgqbpsFhGKKYtm0xz3P0htsAZlmGfd+/fcFnp+s6TNP0E+Qx4zjiuq647zvGkHme3c4eRacMiqI4MLFAwmzbdqDKskSgc0gXpI01y7I4A73/4M9hzKGn5B2XAFEMpD0G0h4DaY+BtMdA2mMg7THQmYTf/FJ5GejLDwwx1EtAP2GkUOKgvzASKFHQb8tLosRA95aWQomAHl1WAsUO+u+S3ChW0LPLcaLYQGeX4kKxgLiW4biPCOhM1IE48naQphhIewykPQbSHgNpj4G0x0Da8w0UYy0mzAHydTKql8SaaZqcgWo+QIUfurhCecl156iSFdbLYikxEYROVVgvu24B0A9153zdLIap69p15Fzx72b4AGkE6jeGtTtDAAAAAElFTkSuQmCC"

        private const val BUTTON_RESET_POSITION =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAADSElEQVR4nO2aPWvyUBTHU11EOhRXEWy3jk7Oirg42H4Cn6FjV0Fw0Elwc3QR/AClg5vTg4OrDoVCJ5dqHdwEXwY9T/7BKydX7ZMXjSb4h0Nj7jkn95fc3Jf0KspGg8Hgbjgclkej0V/1L7nE+t/f3zlF1s/PT1QFGVxABa2a+jwG0S2QgBmPx7RcLmm9XpMbNJ/PtTozqDtFfWR/BIxbQLhWqxWHKiloh/gBWrdqsVhoQHj/FdEO3Sw8JcHhCSDoCnTpcgyoXC6TOjJohuNTyTEgASPsVLoCWZUngLrdLhWLRUokEjtAOIcy+BxTJwF6f3+ndDq9A3HI4IuYY+ioQIh/fX01DCIbYo9Rh6MA9Xo9enp62qnk7e0tRSIRSiaT9PLyohmOcQ5lsj9yINdZgRAnwwSDQcpms9RqtfbmxTmUwQe+MpSdutgGkptZNBqler1uKB984IsYuflZkW0gvMwyDO68WSFGhrLSUdgG4r1ZKBSit7c3Xfl0OqVqtUrxeJwCgYBmOMY5lHEhFjl47+coEMYQfkdzuZwuR6fToVQqdbBXQxl8eGWQg/uYHadsAWFgFBcOh8O6i+Pu/wbDofiTQg7kEuW4hmNAfAaAivF4NCmj4w98eYX4jcA1TgbElwCy5fN5nS/eE6NA8OVCrkO+/1t6mAKSk/v9/u1xo9HQ+eLlNwoEXy7k2ncNYScD4uZKoN+aXKVS0fnaaXLI5UiTk8U7hefnZ+1rq5DVTgE5kMuRTkEW77YfHh7o6+trW2a120YO5BLljnbbfGC9ubmhWq2me0pmB1bEIgdyCR9HB1aIT33u7+/p4+NDV25m6oNY5BD5HJ/6QPLkNJPJ0GQyMZ0HMYjluc4yOYX48sHn82kVG41GuuZ3SPCBL2IQK/KcbfkgksgLvMfHR2o2m9qd3weGcyiDD3x57NkXeNC+JThe7lgsRoVCgdrtNn1+fmqGY5xDGe8ABMzZl+A8mWc+knB55jOWLM98aNwnMxNMO7oCWZXngDz3Dy+ndAW6dO0AuXFbDNcWSGwnw/YSt2o2mwmgPp5QCT+8sHlJ2zuHLVl8e5lbNjEBBK2Kby9ThDy3AVBos3eufwEVNGRqffH+l7SNfxv9AyaodeN8K1S9AAAAAElFTkSuQmCC"

    }
}

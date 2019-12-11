# Coordinates

The coordinate system sets the `x` and `y` aesthetics combination for the element positioning in the plot. 

- `coord_fixed()` | Cartesian coordinates with fixed "aspect ratio"
- `coord_cartesian()` | Default Cartesian coordinates
- `coord_map()` | Map projections

### `coord_cartesian()`

Zooming?

`coord_cartesian()`

##### Arguments
* `xlim` (?)
* `ylim` (?)

##### Examples

### `coord_fixed()`

Overrides the X to Y ratio. The default ratio is 1. Ratios higher than one make units on the `y` axis longer than units on the `x` axis, and vice versa

`coord_fixed(ratio=NONE)`

##### Arguments
* `ratio`

##### Examples

```python
p = ggplot() + ggsize(600, 300)
p += geom_image(image_data=img)
p + coord_fixed(ratio=2)
```

### `coord_map()`

Enables projecting of a portion of the earth onto a flat 2D-plane using the default `merkator` projection.

`coord_map(projection=NONE)`

##### Arguments
* `projection`

##### Examples




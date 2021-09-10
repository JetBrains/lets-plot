# Lets-Plot for Python [![official JetBrains project](http://jb.gg/badges/official-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

>
> **Note:** The content of this page has been moved to
> our new documentation website: https://lets-plot.org/
>
 
### What is new in 2.1.0

- Upgraded dependencies:

    - Kotlin: 1.5.21
    - Apache Batik: 1.14 [[#398](https://github.com/JetBrains/lets-plot/issues/398)]

- Ordering categories:

  New parameters added to the `as_discrete` function:

    - `order_by` (string) - the name of the variable by which the ordering will be performed;
    - `order` (int) - the ordering direction: 1 for ascending direction and -1 for descending (default).

  See: [as_discrete](https://github.com/JetBrains/lets-plot/blob/master/docs/as_discrete.md).

- Interactive maps:

    - Pre-configured raster tilesets in new `lets_plot.tilesets` module.
    - Builtin blank maptiles.

  See: [Configuring basemap tiles](https://github.com/JetBrains/lets-plot/blob/master/docs/basemap_tiles.md).

### Change Log

See [CHANGELOG.md](https://github.com/JetBrains/lets-plot/blob/master/CHANGELOG.md) for other changes and fixes.

### License

Code and documentation released under the [MIT license](https://github.com/JetBrains/lets-plot/blob/master/LICENSE).
Copyright © 2019-2021, JetBrains s.r.o.

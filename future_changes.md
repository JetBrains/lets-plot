## [4.3.3] - 2024-mm-dd

### Added
- Support for "angle" aesthetic in `geom_point()` [[#736](https://github.com/JetBrains/lets-plot/issues/736)].
  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24d/geom_point_angle.ipynb).

### Changed
- [BREAKING] Dropped support for Python 3.7 as it is in the ["end-of-life"](https://devguide.python.org/versions/) of its release cycle.

### Fixed
- Livemap: improve "tiles" documentation [[#1093](https://github.com/JetBrains/lets-plot/issues/1093)].
- Undesired vertical scroller when displaying `gggrid` in Jupyter notebook.
- GeoJson structure breaks if the ring start label occurs several times [[#1086](https://github.com/JetBrains/lets-plot/issues/1086)].
- `theme`: left margin doesn't work for the `plot_title` parameter [[#1101](https://github.com/JetBrains/lets-plot/issues/1101)].
- Improve border line type experience [[LPK-220](https://github.com/JetBrains/lets-plot-kotlin/issues/220)].
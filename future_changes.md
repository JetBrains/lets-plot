## [4.6.2] - 2025-mm-dd

### Added

### Changed

- [**BREAKING**] `kotlinx-coroutines` is now a `compileOnly` dependency and must be explicitly added to **JVM** projects.
- The 'base-midnight', 'base-antique' and 'base-flatblue' tilesets have been restored.

### Fixed

- geom_livemap: Map zoom freezes after multiple rapid clicks on +/- buttons [[#1315](https://github.com/JetBrains/lets-plot/issues/1315)].
- Import error ("libc++abi: Terminating...") when installing Lets-Plot in a fresh `conda` environment on macOS (Darwin).  

## [4.6.2] - 2025-mm-dd

### Added

### Changed

- [**BREAKING**] `kotlinx-coroutines` is now a `compileOnly` dependency and must be explicitly added to **JVM** projects.
- The 'base-midnight', 'base-antique' and 'base-flatblue' tilesets have been restored.

### Fixed

- geom_livemap: Map zoom freezes after multiple rapid clicks on +/- buttons [[#1315](https://github.com/JetBrains/lets-plot/issues/1315)].
- macOS: incorrect system libraries linkage that may lead to Lets-Plot import failure:<br>
  ```libc++abi: Terminating due to typed operator new being invoked before its static initializer in libcxx has been executed.```

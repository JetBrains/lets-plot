## [4.6.2] - 2025-mm-dd

### Fixed

- macOS: incorrect system libraries linkage that may lead to Lets-Plot import failure:<br>
  ```libc++abi: Terminating due to typed operator new being invoked before its static initializer in libcxx has been executed.```


- `geom_livemap()`: map zoom freezes after multiple rapid clicks on +/- buttons [[#1315](https://github.com/JetBrains/lets-plot/issues/1315)].
- Misleading warnings when using CARTO 'base-midnight', 'base-antique' and 'base-flatblue' tilesets in livemap.

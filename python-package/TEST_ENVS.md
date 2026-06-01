# Test Envs

These environment files are for checking Python tests against different optional dependency sets.

- `test_env_empty.yml`:
  Empty test env. Includes only `python` and `pytest`. 
- `test_env_basic.yml`:
  Adds `numpy` and `pandas`.
- `test_env_extended.yml`:
  Adds `numpy`, `pandas`, `geopandas`, and `pypng`.
- `module_env.yml`:
  Full development/test env for the Python module.

Environment files and names:

- `test_env_empty.yml` -> `lets-plot-python-test-empty`
- `test_env_basic.yml` -> `lets-plot-python-test-basic`
- `test_env_extended.yml` -> `lets-plot-python-test-extended`
- `module_env.yml` -> `lets-plot-python-module`


## Setup

For the first setup:

```shell
conda env create -f test_env_empty.yml
```

For an existing environment:

```shell
conda env update -f test_env_empty.yml --prune
```

Reinstall the local Lets-Plot wheel:

```shell
conda run -n lets-plot-python-test-empty pip install --no-index --find-links=dist/ lets-plot --no-deps --force-reinstall
```

## Run Tests

After setup, run tests manually with `conda run`:

```shell
conda run -n lets-plot-python-test-empty python -m pytest test
```

To run one test file:

```shell
conda run -n lets-plot-python-test-empty python -m pytest test/plot/test_ggplot.py
```


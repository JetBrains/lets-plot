# Test Envs

These environment files are for checking Python tests against different optional dependency sets.

- `test_env_empty.yml`:
  Minimal test env. Includes only `python` and `pytest`.
- `test_env_basic.yml`:
  Adds `numpy` and `pandas`.
- `test_env_extended.yml`:
  Adds `numpy`, `pandas`, `geopandas`, and `pypng`.
- `module_env.yml`:
  Full development/test env for the Python module.

Use them to verify that tests:

- skip cleanly when optional dependencies are missing
- still run when only a subset of optional dependencies is available
- continue to pass in the full environment

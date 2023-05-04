## Additional notes on releasing of release candidate (RC) version

All release procedures that described in `RELEASE.md` document apply to all releases - RC and production.

Activities described in this document are mandatory for RC releases only.

### Smoke testing

##### 1. Edit `binder/environment.yml` file.

Upgrade `Lets-Plot` version to just released RC version:

```yaml
  - pip:
      - lets-plot==1.2.0rc2
``` 

##### 2. Create a new Conda environment.

`conda env remove -n my_test_env`

`conda env create -n my_test_env -f <path>/binder/environment.yml`

`conda activate my_test_env`

`pip install jupyter`

##### 3. Open and evaluate the example notebooks.
                                                   
Note: all demo notebooks has been moved to the "lets-plot-docs" Github repository: 

https://github.com/JetBrains/lets-plot-docs/tree/master/source/examples

### Prepare demos for release

Push all changes to the repository and add a tag:

```
git add --all && git commit -m "Updated demo notebooks, add v1.2.3demos1 repo tag" && git push

git tag v1.2.3demos1 && git push --tags
```

In [docs/README.md](docs/README.md) update the Binder link with new "demo" tag (above).

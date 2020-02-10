## Additional notes on releasing of release candidate (RC) version

All release procedures that described in `RELEASE.md` document apply to all releases - RC and production.

Activities described in this document are mandatory for RC releases only.

### Smoke testing

##### 1. Edit binder/environment.yml file.

Upgrade `Lets-Plot` version to just released RC version:

```yaml
  - pip:
      - lets-plot==1.2.0rc2
``` 

##### 2. Create a new Conda environment.

`conda env create -n my_test_env -f <path>/binder/environment.yml`
`conda activate my_test_env`
`conda install jupyter`

##### 3. Open and evaluate the example notebooks.

##### 4. Push all changes to the repository.


### Prepare docs and demos for the production release

##### 1. NBViewer

TBD.

##### 2. Binder

TBD.
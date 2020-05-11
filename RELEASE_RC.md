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

`conda install jupyter`

##### 3. Open and evaluate the example notebooks.

##### 4. Push all changes to the repository.


### Prepare docs and demos for the production release

##### 1. NBViewer

No farther changes needed.

##### 2. Binder

In all notebooks which uses new features or fixes included in the coming production build update the *demos-tag* part in
the Binder URLs.  

For example: the next *Lets-Plot* version is 1.2.3. The *demos-tag* is going to be **v1.2.3demos1*** add all Binder URLs
like:

`https://mybinder.org/v2/gh/JetBrains/lets-plot/v1.1.0demos1?....`

need to be updated as: 

`https://mybinder.org/v2/gh/JetBrains/lets-plot/v1.2.3demos1?....`

Push the updated demo notebooks and git tag:

`git add --all && git commit -m "Updated demo notebooks binder url to match repo tag v1.2.3demos1" && git push`

`git tag v1.2.3demos1 && git push --tags`
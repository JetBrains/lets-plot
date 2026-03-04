#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import decimal
import importlib
import json
import math
from datetime import datetime, date, time, timezone
from typing import Any, Union, Type, Dict


class LazyModule:
    """
    A wrapper for optional libraries that delays importing until a specific
    class or function is actually needed.
    """

    def __init__(self, name: str):
        self._name = name
        self._module = None
        self._tried = False

    @property
    def _inst(self):
        """Internal helper to load the module. Triggers an actual import."""
        if not self._tried:
            try:
                self._module = importlib.import_module(self._name)
            except (ImportError, AttributeError):
                self._module = None

            self._tried = True
        return self._module

    def likely_defines(self, v: Any) -> bool:
        """
        Check whether object `v` appears to originate from this lazy module or its subpackages,
        without triggering an import of the underlying module.

        This is a non-invasive, heuristic check that inspects the object's type information
        (MRO/module strings) to decide if the object is likely provided by the optional
        dependency wrapped by this LazyModule instance.

        Args:
            v: Any Python object to test. Passing None will always return False.

        Returns:
            bool: True if `v`'s type (or any of its base types) is defined in the module
                  identified by this LazyModule (self._name) or one of its subpackages.
                  False otherwise.

        Behavior summary:
        - Never triggers an actual import of the wrapped module. This function is safe to
          call during import-time or early startup where importing heavy optional dependencies
          would be undesirable.
        - If we previously attempted to import the module and that attempt failed
          (self._tried is True and self._module is None), we treat this LazyModule as
          "owns nothing" and return False immediately.
        - Otherwise we delegate to _check_mro_strings(v), which inspects type(v).mro()
          and compares parent.__module__ strings against self._name.

        Examples:
            # If the LazyModule wraps 'numpy' and v is an ndarray instance coming from numpy,
            # this will return True (but it will not import numpy).
            numpy = LazyModule('numpy')
            lazy_is_module_obj(some_ndarray)  # -> True (heuristic from MRO/module strings)

            # If module import was attempted and failed before, function returns False quickly.
            lazy_mod = LazyModule('heavy_optional')
            lazy_mod._tried = True
            lazy_mod._module = None
            lazy_mod.lazy_is_module_obj(obj)  # -> False

        Notes and caveats:
        - Because the check is based on strings in __module__, it is a heuristic and may
          produce false positives if third-party code reuses module names, or false negatives
          for some dynamic types. It is intended as a best-effort guard to avoid importing
          optional dependencies unnecessarily.
        - The function intentionally avoids any attribute access on `self` that could
          trigger __getattr__ and import the module; it only reads lightweight flags and
          uses type introspection on the object.
        - This method does not catch exceptions from _check_mro_strings; that helper should
          itself be safe (it catches AttributeError/TypeError).
        """
        if v is None:
            return False

        # If we already tried to load the lib and it failed, it owns nothing.
        if self._tried and self._module is None:
            return False

        return self._check_mro_strings(v)

    def lazy_is_instance(self, v: Any, module_classinfo: Union[str, Type]) -> bool:
        """
        Check whether object `v` is an instance of the class described by
        `module_classinfo`, but avoid importing the target module unless necessary.

        This method supports two modes:
        - Fast path: if the wrapped module has already been imported (self._module
          is not None), resolve the requested class against that module and use
          normal isinstance() checking.
        - Lazy path: if the wrapped module is not yet imported, we first inspect
          the object's MRO/module strings (via `lazy_is_module_obj`) to see if the
          object appears to originate from this library (or its subpackages). Only
          if that heuristic matches we attempt to resolve the class against `self`
          which intentionally triggers the import (via __getattr__/_inst) and then
          perform isinstance().

        Args:
            v: The object to test.
            module_classinfo: Either a Type object (e.g. numpy.ndarray class) or a
                dotted string like 'ndarray' or 'numpy.ndarray' describing the
                class to check against. If a dotted string is used, nested
                attributes are resolved (see _resolve_class).

        Returns:
            True if `v` is an instance of the resolved class, False otherwise.

        Notes and behavior details:
        - Passing an actual Type avoids any string resolution and is the simplest
          case.
        - The method is careful to avoid importing heavy optional libraries (e.g.
          numpy, pandas) unless the object clearly originates from that library.
          This keeps startup time and side-effects minimal.
        - If the lazy path decides to resolve against `self`, that resolution will
          trigger the actual import of the optional module and thus may raise
          ImportError if the module is not installed. Such exceptions are not
          caught here and will propagate to the caller.
        - _resolve_class supports nested attribute strings (e.g. 'subpkg.Class').
        - isinstance semantics are used, so subclass relationships are honored.
        - If the module was attempted to be imported before and failed, the fast
          path will treat it as not present and lazy_is_module_obj will return False
          (so this method returns False).

        Example:
            # Fast path (module already loaded)
            numpy = LazyModule('numpy')
            # assume numpy._module is a real module object here
            lazy_is_instance(array_obj, 'ndarray')  -> uses numpy.ndarray

            # Lazy path (module not loaded yet)
            lazy_is_instance(array_obj, 'numpy.ndarray')  -> only imports numpy if
            array_obj's module string indicates it came from 'numpy' or a subpackage.

        Raises:
            AttributeError: if the dotted class string cannot be resolved after the
                module is imported.
            ImportError: if the lazy path triggers an import and the module is missing.
        """
        # Fast path: If module is already in memory
        if self._module is not None:
            target_cls = self._resolve_class(self._module, module_classinfo)
            return isinstance(v, target_cls)

        # Lazy path: Only trigger import if the object's module string matches
        if self.likely_defines(v):
            # We use 'self' here to trigger the __getattr__ import logic
            target_cls = self._resolve_class(self, module_classinfo)
            return isinstance(v, target_cls)

        return False

    @staticmethod
    def _resolve_class(root: Any, class_info: Union[str, Type]) -> Type:
        """Helper to resolve strings (including nested 'a.b.C') into class objects."""
        if not isinstance(class_info, str):
            return class_info

        # Handle nested attributes like 'numpy.ndarray'
        curr = root
        for part in class_info.split("."):
            curr = getattr(curr, part)
        return curr

    def _check_mro_strings(self, v: Any) -> bool:
        """Internal helper: Scans MRO strings. Does NOT trigger imports."""
        try:
            mro = type(v).mro()
        except (AttributeError, TypeError):
            return False

        for parent in mro:
            mod = getattr(parent, "__module__", "")
            if mod and (mod == self._name or mod.startswith(f"{self._name}.")):
                return True
        return False

    def __getattr__(self, item):
        """Triggered by direct access (e.g., pd.DataFrame). Triggers an actual import."""
        if self._inst is None:
            raise ImportError(f"Module '{self._name}' is required but not installed.")
        return getattr(self._inst, item)

    def __bool__(self):
        """Triggered by 'if pd:'. Triggers an actual import to verify availability."""
        return self._inst is not None


pandas = LazyModule('pandas')
geopandas = LazyModule('geopandas')
numpy = LazyModule('numpy')
jax = LazyModule('jax')
polars = LazyModule('polars')
shapely = LazyModule('shapely')


# Parameter 'value' can also be pandas.DataFrame
def standardize_dict(value: Dict) -> Dict:
    result = {}
    for k, v in value.items():
        result[_standardize_value(k)] = _standardize_value(v)

    return result


def is_ndarray(data) -> bool:
    if numpy.lazy_is_instance(data, 'ndarray'):
        return True

    if jax.lazy_is_instance(data, 'numpy.ndarray'):
        return True

    return False


def _standardize_value(v):
    # Notes:
    # - Check libs first, because they may have custom types derived from built-in types that require special handling,
    #   e.g. pandas.NaT is a datetime subclass, but missing .timestamp() method and will fail in a regular conversion.
    # - Handle dicts-like containers before list-like
    # - Handle containers before is_nan() because nan checks may raise an error,
    #   e.g. ValueError: The truth value of an array with more than one element is ambiguous. Use a.any() or a.all())
    # - Check for NaN/inf before other types - datetime64 and other may raise an error when converted to float,
    #   e.g. ValueError: NaTType does not support timestamp

    try:
        if pandas.likely_defines(v):
            if isinstance(v, pandas.DataFrame):  # don't use is_dict_like - Series is dict-like, but should be list
                return standardize_dict(v)
            if pandas.api.types.is_list_like(v):
                return [_standardize_value(element) for element in v]
            if pandas.api.types.is_scalar(v) and pandas.isna(v):
                return None

        if numpy.likely_defines(v):
            if isinstance(v, numpy.ndarray):
                # Process each array element individually.
                # Don't use '.tolist()' because this will implicitly
                # convert 'datetime64' values to unpredictable 'datetime' objects.
                return [_standardize_value(x) for x in v]
            if numpy.isnan(v):
                return None
            if isinstance(v, numpy.floating):
                return float(v) if math.isfinite(v) else None
            if isinstance(v, numpy.integer):
                return float(v)
            if isinstance(v, numpy.bool_):
                return bool(v)
            if isinstance(v, numpy.str_):
                return str(v)
            if isinstance(v, numpy.datetime64):
                # numpy.datetime64: to milliseconds since epoch (Unix time)
                return float(v.astype('datetime64[ms]').astype(numpy.int64))

        if polars.likely_defines(v):
            if isinstance(v, polars.DataFrame):
                return standardize_dict(v.to_dict(as_series=False))
            if isinstance(v, polars.Series):
                return _standardize_value(v.to_list())

        if jax.likely_defines(v):
            if isinstance(v, jax.numpy.ndarray):
                return _standardize_value(v.tolist())
            if isinstance(v, jax.numpy.floating):
                return float(v) if math.isfinite(v) else None
            if isinstance(v, jax.numpy.integer):
                return float(v)

        if geopandas.likely_defines(v):
            if isinstance(v, geopandas.GeoDataFrame):
                return standardize_dict(v)
            if isinstance(v, geopandas.GeoSeries):
                return [_standardize_value(element) for element in v]

        if shapely.likely_defines(v):
            if isinstance(v, shapely.geometry.base.BaseGeometry):
                return json.dumps(shapely.geometry.mapping(v))

        # python containers
        if isinstance(v, dict):
            return standardize_dict(v)
        if isinstance(v, list):
            return [_standardize_value(elem) for elem in v]
        if isinstance(v, tuple):
            return [_standardize_value(elem) for elem in v]
        if isinstance(v, set):
            return [_standardize_value(elem) for elem in v]

        # python types
        if v is None:
            return None
        if isinstance(v, bool):
            return bool(v)
        if isinstance(v, int):
            return float(v)
        if isinstance(v, float):
            return float(v) if math.isfinite(v) else None  # nan/inf to None
        if isinstance(v, decimal.Decimal):
            return float(v) if v.is_finite() else None
        if isinstance(v, str):
            return str(v)
        if isinstance(v, datetime):
            # Datetime: to milliseconds since epoch (time zone aware)
            return v.timestamp() * 1000
        if isinstance(v, date):
            # Local date: to milliseconds since epoch (midnight UTC)
            return datetime.combine(v, time.min, tzinfo=timezone.utc).timestamp() * 1000
        if isinstance(v, time):
            # Local time: to milliseconds since midnight
            return float(v.hour * 3600_000 + v.minute * 60_000 + v.second * 1000 + v.microsecond // 1000)

        return repr(v)
    except Exception as e:
        raise Exception('Failed to standardize type {0} ({1})'.format(type(v), str(v)[:100])) from e

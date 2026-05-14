#  Copyright (c) 2026. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

#
#
import decimal
import importlib
import json
import math
import sys
from datetime import datetime, date, time, timezone, timedelta
from typing import Any, Union, Type, Dict


class LazyModule:
    """
    Lazy wrapper for optional libraries.

    Attribute access and truthiness checks import the wrapped module. The
    type-check helpers do not import: they either inspect already-loaded
    modules or use type metadata from the value itself.
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

    @property
    def is_loaded(self) -> bool:
        """
        Return True if the module is already loaded, without importing it.

        If the module is found in sys.modules, cache it locally for later
        attribute and type checks.
        """
        if self._module is not None:
            return True

        module = sys.modules.get(self._name)
        if module is not None:
            self._module = module
            self._tried = True
            return True
        return False

    def likely_defines(self, v: Any) -> bool:
        """
        Return True if `v` appears to be owned by this module, without importing it.

        This is a heuristic based on the value type's MRO module names. It is used
        before library-specific conversions to prevent a loaded library from
        applying broad helper APIs to values owned by another library.
        """
        if v is None:
            return False

        if self._tried and self._module is None:
            return False

        return self._check_mro_strings(v)

    def lazy_is_instance(self, v: Any, module_classinfo: Union[str, Type]) -> bool:
        """
        Return True if `v` is an instance of `module_classinfo`, without importing.

        If the wrapped module is not already loaded, or if a string class path does
        not exist in the loaded module version, return False.

        Args:
            v: The object to test.
            module_classinfo: Either a Type object (e.g. numpy.ndarray class) or a
                dotted string like 'ndarray' or 'numpy.ndarray' describing the
                class to check against. If a dotted string is used, nested
                attributes are resolved (see _resolve_class).

        Returns:
            True if `v` is an instance of the resolved class, False otherwise.

        Example:
            # Does not import numpy just to answer False for non-numpy values.
            # If array_obj is a NumPy array, numpy is already present in sys.modules.
            LazyModule('numpy').lazy_is_instance(array_obj, 'ndarray')
        """
        module = self._module if self.is_loaded else None
        if module is None:
            return False

        try:
            target_cls = self._resolve_class(module, module_classinfo)
        except AttributeError:
            return False
        return isinstance(v, target_cls)

    @staticmethod
    def _resolve_class(module: Any, class_info: Union[str, Type]) -> Type:
        """Resolve strings such as 'DataFrame' or 'api.extensions.ExtensionArray'."""
        if not isinstance(class_info, str):
            return class_info

        # Handle nested attributes like 'numpy.ndarray'
        clazz = module
        for part in class_info.split("."):
            clazz = getattr(clazz, part)
        return clazz

    def _check_mro_strings(self, v: Any) -> bool:
        """Check value ownership by scanning type MRO module names."""
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
        """Resolve module attributes, importing the wrapped module if needed."""
        if self._inst is None:
            raise ImportError(f"Module '{self._name}' is required but not installed.")
        return getattr(self._inst, item)

    def __bool__(self):
        """Import the wrapped module to verify that it is available."""
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

    if jax.is_loaded and isinstance(data, jax.numpy.ndarray):
        return True

    if jax.lazy_is_instance(data, 'Array'):
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
        if numpy.likely_defines(v):
            if isinstance(v, numpy.ndarray):
                if v.ndim == 0:
                    # 0-dim array: process the single value. Don't use item() - it may break datetime64
                    return _standardize_value(v[()])

                # Optimization
                kind = v.dtype.kind
                if kind == 'f':  # Floats
                    # Cast to object array so we can insert Python `None`
                    v_obj = v.astype(object)
                    v_obj[~numpy.isfinite(v)] = None
                    return v_obj.tolist()

                elif kind in 'iu':  # Integers
                    return v.astype(float).tolist()

                elif kind == 'b' or kind in 'SU':  # Booleans and Strings
                    return v.tolist()

                elif kind == 'M':  # Datetime64
                    # Cast ms to int64, then to float64, then object (for None insertion)
                    v_float = v.astype('datetime64[ms]').astype(numpy.int64).astype(numpy.float64)
                    v_obj = v_float.astype(object)
                    v_obj[numpy.isnat(v)] = None
                    return v_obj.tolist()

                elif kind == 'm':  # Timedelta64
                    v_float = v.astype('timedelta64[ms]').astype(numpy.int64).astype(numpy.float64)
                    v_obj = v_float.astype(object)
                    v_obj[numpy.isnat(v)] = None
                    return v_obj.tolist()

                # Fallback only for object arrays (which can contain mixed/unpredictable types)
                return [_standardize_value(x) for x in v]
            if isinstance(v, numpy.timedelta64):
                if numpy.isnat(v):
                    return None
                # numpy.timedelta64: to milliseconds
                return float(v.astype('timedelta64[ms]').astype(numpy.int64))
            if isinstance(v, numpy.datetime64):
                if numpy.isnat(v):
                    return None
                # numpy.datetime64: to milliseconds since epoch (Unix time)
                return float(v.astype('datetime64[ms]').astype(numpy.int64))
            if isinstance(v, numpy.floating):
                return float(v) if math.isfinite(v) else None
            if isinstance(v, numpy.bool_):
                return bool(v)
            if isinstance(v, numpy.str_):
                return str(v)
            if isinstance(v, numpy.integer):
                return float(v)

        if pandas.likely_defines(v):
            if isinstance(v, pandas.DataFrame):  # don't use is_dict_like - Series is dict-like, but should be list
                return standardize_dict(v)
            if pandas.api.types.is_list_like(v):
                return _standardize_value(v.to_numpy())
            if pandas.isna(v):
                return None
            if isinstance(v, pandas.Timestamp):
                # pandas.Timestamp: to milliseconds since epoch
                return v.timestamp() * 1000
            if isinstance(v, pandas.Timedelta):
                # pandas.Timedelta: to milliseconds
                return float(v.total_seconds() * 1000)

        if polars.likely_defines(v):
            if isinstance(v, polars.DataFrame):
                return standardize_dict(v.to_dict())
            if isinstance(v, polars.Series):
                return _standardize_value(v.to_numpy())

        # Modern JAX arrays may be jax.Array without reporting a jax.* implementation module.
        # Do not use jax.likely_defines() - can be provided by jaxlib and won't pass the check.
        # Use string lookup because the dependency floor (>=0.3.25) still allows JAX versions without jax.Array.
        if jax.lazy_is_instance(v, 'Array'):
            return _standardize_value(numpy.array(v))

        if jax.likely_defines(v):
            if isinstance(v, jax.numpy.ndarray):
                return _standardize_value(numpy.array(v))
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
        if isinstance(v, timedelta):
            # Standard Python timedelta: to milliseconds
            return v / timedelta(milliseconds=1)

        return repr(v)
    except Exception as e:
        raise Exception('Failed to standardize type {0} ({1})'.format(type(v), str(v)[:100])) from e

from enum import Enum
from typing import Union, Callable, Any, Dict, Optional, Type, List, Generic, TypeVar


class FluentDict:

    def __init__(self, dict: Optional[Dict] = None):
        if dict is not None:
            self._dict = dict
        else:
            self._dict = {}

    def contains(self, key: Union[str, Enum]) -> bool:
        str_key: str = self._key_to_str(key)
        return str_key in self._dict and self.get(str_key) is not None

    def put(self, key: Union[str, Enum], value: Any) -> 'FluentDict':
        if value is None:
            pass
        elif isinstance(value, (int, float, str, dict)):
            pass
        elif isinstance(value, FluentDict):
            value = value.to_dict()
        elif isinstance(value, Enum):
            value = value.value
        elif isinstance(value, (list, tuple)):
            if len(value) > 0:
                if isinstance(value[0], Enum):
                    value = list(map(lambda enum: enum.value, value))
        else:
            raise ValueError('Not supported type: ' + str(value.__class__))

        self._dict[self._key_to_str(key)] = value
        return self

    def visit(self, key: Union[str, Enum], consumer: Callable[[Any], Any]) -> 'FluentDict':
        consumer(self._dict[self._key_to_str(key)])
        return self

    def visit_str(self, key: Union[str, Enum], consumer: Callable[[str], Any]) -> 'FluentDict':
        consumer(str(self.get(key)))
        return self

    def visit_str_existing(self, key: Union[str, Enum], consumer: Callable[[str], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            consumer(self.get(key))
        return self

    def visit_int(self, key: Union[str, Enum], consumer: Callable[[int], Any]) -> 'FluentDict':
        consumer(int(self.get(key)))
        return self

    def get_float(self, key: Union[str, Enum]) -> float:
        return float(self.get(key))

    def visit_float(self, key: Union[str, Enum], consumer: Callable[[float], Any]) -> 'FluentDict':
        consumer(self.get_float(key))
        return self

    def visit_float_optional(self, key: Union[str, Enum], consumer: Callable[[float], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            self.visit_float(key, consumer)
        return self

    def visit_int_optional(self, key: Union[str, Enum], consumer: Callable[[int], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            self.visit_int(key, consumer)
        return self

    def visit_dict(self, key: Union[str, Enum], consumer: Callable[['FluentDict'], Any]) -> 'FluentDict':
        consumer(self.get_object(key))
        return self

    def visit_object_optional(self, key: Union[str, Enum], consumer: Callable[['FluentDict'], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            self.visit_dict(key, consumer)
        return self

    def visit_list_optional(self, key: Union[str, Enum], consumer: Callable[['FluentList'], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            self.visit_list(key, consumer)
        return self

    def get_enum(self, key: Union[str, Enum], enum_type: Type[Enum]):
        return self._to_enum(self.get(key), enum_type)

    def visit_enum(self, key: Union[str, Enum], enum_type: Type[Enum], consumer: Callable[[Enum], Any]) -> 'FluentDict':
        consumer(self.get_enum(key, enum_type))
        return self

    def visit_enums(self, key: Union[str, Enum], enum_type: Type[Enum], consumer: Callable[[List[Enum]], Any]) -> 'FluentDict':
        consumer(
            self.get_fluent_list(key)
                .map(lambda enum_str: self._to_enum(enum_str, enum_type))
                .list()
        )
        return self

    def visit_enum_existing(self, key: Union[str, Enum], enum_type: Type[Enum], consumer: Callable[[Enum], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            consumer(self.get_enum(key, enum_type))

        return self

    def visit_objects(self, key: Union[str, Enum], consumer: Callable[['FluentDict'], Any]) -> 'FluentDict':
        for d in self.get(key):
            consumer(FluentDict(d))
        return self

    def visit_object(self, key: Union[str, Enum], consumer: Callable[['FluentDict'], Any]) -> 'FluentDict':
        consumer(FluentDict(self.get(key)))
        return self

    def visit_list(self, key: Union[str, Enum], consumer: Callable[['FluentList'], Any]) -> 'FluentDict':
        consumer(self.get_fluent_list(key))
        return self

    def get(self, key: Union[str, Enum]) -> Any:
        return self._dict[self._key_to_str(key)]

    def get_bool(self, key: Union[str, Enum]) -> bool:
        return bool(self.get(key))

    def visit_bool(self, key: Union[str, Enum], consumer: Callable[[bool], Any]) -> 'FluentDict':
        consumer(self.get_bool(key))
        return self

    def get_fluent_list(self, key: Union[str, Enum]) -> 'FluentList':
        return FluentList(self.get(key))

    def get_list(self, key: Union[str, Enum]) -> list:
        value = self.get(key)
        assert isinstance(value, list)
        return value

    def get_object(self, key: Union[str, Enum]) -> 'FluentDict':
        return FluentDict(self.get(key))

    def get_objects(self, key: Union[str, Enum]) -> 'FluentList[FluentDict]':
        return FluentList(self._dict[self._key_to_str(key)]).map(lambda v: FluentDict(v))

    def to_dict(self) -> Dict:
        return self._dict

    def _is_not_none(self, key: Union[str, Enum]):
        return self._key_to_str(key) in self._dict and self.get(key) is not None

    @staticmethod
    def _key_to_str(key: Union[str, Enum]) -> str:
        if isinstance(key, str):
            return key
        elif isinstance(key, Enum):
            return key.value
        else:
            raise ValueError('Unknown key type: ' + str(key.__class__))

    @staticmethod
    def _to_enum(enum_str: str, enum_type: Type[Enum]):
        for e in enum_type:
            if e.value == enum_str:
                return e
        raise ValueError('Unknown emum value: ' + enum_str)

    def visit_str_list(self, key: Union[str, Enum], consumer: Callable[[List[str]], Any]) -> 'FluentDict':
        consumer(self.get(key))
        return self

    def visit_str_list_optional(self, key: Union[str, Enum], consumer: Callable[[List[str]], Any]) -> 'FluentDict':
        if self._is_not_none(key):
            consumer(self.get(key))

        return self


T = TypeVar('T')
TOut = TypeVar('TOut')


class FluentList(Generic[T]):

    def __init__(self, list: List[T] = None):
        if list is None:
            self._list: List[T] = []
        else:
            self._list: List[T] = list

    def add(self, obj: T) -> 'FluentList[T]':
        self._list.append(obj)
        return self

    def list(self) -> List[T]:
        return self._list

    TOut = TypeVar('TOut')

    def map(self, func: Callable[[T], TOut]) -> 'FluentList[TOut]':
        out = []
        for v in self._list:
            out.append(func(v))

        return FluentList(out)

def assert_type(obj, *types):
    assert isinstance(obj, types), 'Invalid type: \nActual: ' + str(type(obj)) + ' (' + str(obj) + ')\nExpected: ' + str(types)


def assert_optional_type(obj, *types):
    if obj is None:
        return
    assert_type(obj, *types)


def assert_list_type(obj, *types):
    assert_type(obj, list, tuple)
    for v in obj:
        assert_type(v, *types)


def assert_optional_list_type(obj, *types):
    if obj is None:
        return
    assert_list_type(obj, *types)


def assert_optional_tuple_type(obj, *types):
    if obj is None:
        return
    assert_tuple_type(obj, *types)


def assert_tuple_type(obj, *types):
    assert_type(obj, tuple)
    assert len(obj) == len(types)

    for v, t in zip(obj, types):
        assert_type(v, t)

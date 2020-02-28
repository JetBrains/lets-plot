#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


class VariableMeta:
    def __init__(self, name, kind):
        if name is None:
            raise ValueError("name can't be none")

        if kind is None:
            raise ValueError("kind can't be none")

        self.name = name
        self.kind = kind


def factor(var_name):
    if isinstance(var_name, str):
        return VariableMeta(var_name, 'discrete')

    # aes(x=factor([1, 2, 3])) - pass as is
    return var_name

from typing import Dict, Tuple


# `plot_spec` can also be `pandas.DataFrame`
def _to_response_data_object(plot_spec: Dict) -> Tuple[str, Dict]:
    return 'datalore-ggplot', plot_spec

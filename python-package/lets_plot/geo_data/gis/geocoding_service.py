import json
import urllib.parse
import urllib.request
from urllib.error import URLError

from .json_request import RequestFormatter
from .json_response import ResponseParser
from .request import Request
from .response import Response, ResponseBuilder, Status
from ..._global_settings import has_global_value, get_global_val
from ...settings_utils import GEOCODING_PROVIDER_URL


class GeocodingService:
    def do_request(self, request: Request) -> Response:
        if not has_global_value(GEOCODING_PROVIDER_URL):
            raise ValueError('Geocoding server url is not defined')

        url = '{}/{}'.format(get_global_val(GEOCODING_PROVIDER_URL), 'map_data/geocoding')
        try:
            r_str = self._get_entity(url, RequestFormatter().format(request).to_dict())
            return ResponseParser().parse(json.loads(r_str))
        except URLError:
            return ResponseBuilder() \
                .set_status(Status.error) \
                .set_message('Service is down for maintenance') \
                .build()


    @staticmethod
    def _get_entity(url: str, body: dict) -> str:
        headers = {'Content-Type': 'application/json'}
        request = urllib.request.Request(url, data=bytearray(json.dumps(body), 'utf-8'), headers=headers, method='POST')
        response = urllib.request.urlopen(request)
        return response.read().decode('utf-8')

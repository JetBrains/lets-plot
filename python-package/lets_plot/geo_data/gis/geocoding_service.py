import json
import urllib.parse
import urllib.request
from urllib.error import HTTPError

from .json_request import RequestFormatter
from .json_response import ResponseParser
from .request import Request
from .response import Response, ResponseBuilder, Status
from ..._global_settings import has_global_value, get_global_str
from ...settings_utils import GEOCODING_PROVIDER_URL


class GeocodingService:
    def do_request(self, request: Request) -> Response:
        if not has_global_value(GEOCODING_PROVIDER_URL):
            raise ValueError('Geocoding server url is not defined')

        try:
            request_json = RequestFormatter().format(request).to_dict()
            request_str = json.dumps(request_json)

            request = urllib.request.Request(
                url=get_global_str(GEOCODING_PROVIDER_URL) + '/map_data/geocoding',
                headers={'Content-Type': 'application/json'},
                method='POST',
                data=bytearray(request_str, 'utf-8')
            )
            response = urllib.request.urlopen(request)
            response_str = response.read().decode('utf-8')
            response_json = json.loads(response_str)
            return ResponseParser().parse(response_json)

        except HTTPError as e:
            raise ValueError('Geocoding server connection failure: {} {} ({})'.format(e.code, e.msg, e.filename)) from None

        except Exception as e:
            return ResponseBuilder() \
                .set_status(Status.error) \
                .set_message('Geocoding service exception: {}'.format(str(e))) \
                .build()

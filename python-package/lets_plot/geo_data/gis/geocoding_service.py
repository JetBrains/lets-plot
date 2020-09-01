import json
import urllib.parse
import urllib.request
from urllib.error import HTTPError

from .json_request import RequestFormatter
from .json_response import ResponseParser
from .request import Request, GeocodingRequest
from .response import Response, SuccessResponse, ErrorResponse, AmbiguousResponse, ResponseBuilder, Status
from ..._global_settings import has_global_value, get_global_str
from ...settings_utils import GEOCODING_PROVIDER_URL


class GeocodingService:
    def do_request(self, request: Request, chunk_size=None, progress_callback=None) -> Response:
        # level autodetection can work only with whole request
        if chunk_size is not None and isinstance(request, GeocodingRequest) and request.level is not None:
            return self._execute_chunked(request, chunk_size, progress_callback)
        else:
            return self._execute(request)

    def _execute_chunked(self, request: GeocodingRequest, chunk_size, progress_callback) -> Response:
        success_chunks = []
        ambiguous_chunks = []

        def chunked(items):
            for i in range(0, len(items), chunk_size):
                yield items[i:i + chunk_size]

        total_count = len(request.region_queries)
        i = 0
        if progress_callback:
            progress_callback(i, total_count)

        for q in chunked(request.region_queries):
            chunked_request = GeocodingRequest(
                requested_payload=request.requested_payload,
                resolution=request.resolution,
                region_queries=q,
                level=request.level,
                namesake_example_limit=request.namesake_example_limit,
                allow_ambiguous=request.allow_ambiguous
            )

            response = self._execute(chunked_request)
            if progress_callback:
                i = i + len(q)
                progress_callback(i, total_count)

            if isinstance(response, ErrorResponse):
                return response
            elif isinstance(response, SuccessResponse):
                success_chunks.append(response)
            elif isinstance(response, AmbiguousResponse):
                ambiguous_chunks.append(response)
            else:
                raise ValueError('Unknown response type: ' + type(response).__name__)

        # combine ambiguous features from all chunks
        if ambiguous_chunks:
            ambiguous_features = []
            for response in ambiguous_chunks:
                ambiguous_features.extend(response.features)

            return AmbiguousResponse(ambiguous_chunks[0].message, request.level, ambiguous_features)

        # no errors or ambiguous responses - combine success features from all chunks
        success_features = []
        for response in success_chunks:
            success_features.extend(response.features)

        return SuccessResponse(success_chunks[0].message, request.level, success_features)

    def _execute(self, request: Request) -> Response:
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
            raise ValueError(
                'Geocoding server connection failure: {} {} ({})'.format(e.code, e.msg, e.filename)) from None

        except Exception as e:
            return ResponseBuilder() \
                .set_status(Status.error) \
                .set_message('Geocoding service exception: {}'.format(str(e))) \
                .build()

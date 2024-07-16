from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model import Model
from openapi_server.models.execution_full import ExecutionFull
from openapi_server import util

from openapi_server.models.execution_full import ExecutionFull  # noqa: E501

class OffersResponse(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, result=None, offers=None, messages=None):  # noqa: E501
        """OffersResponse - a model defined in OpenAPI

        :param result: The result of this OffersResponse.  # noqa: E501
        :type result: str
        :param offers: The offers of this OffersResponse.  # noqa: E501
        :type offers: List[ExecutionFull]
        :param messages: The messages of this OffersResponse.  # noqa: E501
        :type messages: List[str]
        """
        self.openapi_types = {
            'result': str,
            'offers': List[ExecutionFull],
            'messages': List[str]
        }

        self.attribute_map = {
            'result': 'result',
            'offers': 'offers',
            'messages': 'messages'
        }

        self._result = result
        self._offers = offers
        self._messages = messages

    @classmethod
    def from_dict(cls, dikt) -> 'OffersResponse':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The OffersResponse of this OffersResponse.  # noqa: E501
        :rtype: OffersResponse
        """
        return util.deserialize_model(dikt, cls)

    @property
    def result(self) -> str:
        """Gets the result of this OffersResponse.

        A flag to indicate whether the request can be handled by this service. If service is able to handle the request, then the `result` will be `YES` and the `offers` block should contain one or more offers. If service is not able to handle the request, the `result` will be `NO` and the `messages` block may contain one or more reasons explaining why.   # noqa: E501

        :return: The result of this OffersResponse.
        :rtype: str
        """
        return self._result

    @result.setter
    def result(self, result: str):
        """Sets the result of this OffersResponse.

        A flag to indicate whether the request can be handled by this service. If service is able to handle the request, then the `result` will be `YES` and the `offers` block should contain one or more offers. If service is not able to handle the request, the `result` will be `NO` and the `messages` block may contain one or more reasons explaining why.   # noqa: E501

        :param result: The result of this OffersResponse.
        :type result: str
        """
        allowed_values = ["YES", "NO"]  # noqa: E501
        if result not in allowed_values:
            raise ValueError(
                "Invalid value for `result` ({0}), must be one of {1}"
                .format(result, allowed_values)
            )

        self._result = result

    @property
    def offers(self) -> List[ExecutionFull]:
        """Gets the offers of this OffersResponse.

        If the response is `YES`, this list should contain one or more offers describing how the request can be handled.   # noqa: E501

        :return: The offers of this OffersResponse.
        :rtype: List[ExecutionFull]
        """
        return self._offers

    @offers.setter
    def offers(self, offers: List[ExecutionFull]):
        """Sets the offers of this OffersResponse.

        If the response is `YES`, this list should contain one or more offers describing how the request can be handled.   # noqa: E501

        :param offers: The offers of this OffersResponse.
        :type offers: List[ExecutionFull]
        """

        self._offers = offers

    @property
    def messages(self) -> List[str]:
        """Gets the messages of this OffersResponse.

        If the response is `NO`, this list may contain one or more messages explaining why the request can't be handled.   # noqa: E501

        :return: The messages of this OffersResponse.
        :rtype: List[str]
        """
        return self._messages

    @messages.setter
    def messages(self, messages: List[str]):
        """Sets the messages of this OffersResponse.

        If the response is `NO`, this list may contain one or more messages explaining why the request can't be handled.   # noqa: E501

        :param messages: The messages of this OffersResponse.
        :type messages: List[str]
        """

        self._messages = messages